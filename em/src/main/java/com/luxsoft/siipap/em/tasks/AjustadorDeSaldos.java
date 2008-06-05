package com.luxsoft.siipap.em.tasks;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class AjustadorDeSaldos extends HibernateDaoSupport{
	
	public AjustadorDeSaldos(){
		super();
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	protected void importData(final String tabla,final String columna){

		String ss="select @COL from "+tabla;
		ss=ss.replaceAll("@COL", columna);
		System.out.println(ss);
		final List<Map<String, Object>> rrs=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(new Date()).queryForList(ss);		
		String sql2="insert into @TABLA values(?)";
		sql2=sql2.replaceAll("@TABLA", tabla);
		for(Map<String,Object> row:rrs){
			Object obj=row.get(columna);
			Long idd=((Number)obj).longValue();
			int val=ServiceManager.instance().getDefaultJdbcTemplate().update(sql2, new Object[]{idd});
			System.out.println("Inserto "+val);
		}
		
	}
	
	private Set<Long> getVentasIds(int year){
		System.out.println("Localizando ventas por ajustar");
		final String sql="select VENTA_ID from V_VENTAS where year=? and saldo>0 and venta_id not in(select venta_id from SALDFC05)";		
		final List<Map<String, Object>> rows=ServiceLocator.getJdbcTemplate().queryForList(sql,new Object[]{year});
		final Set<Long> ids=new HashSet<Long>();
		for(Map<String,Object> row:rows){
			Object obj=row.get("VENTA_ID");
			long id=((Number)obj).longValue();
			ids.add(new Long(id));
		}
		return ids;
	}
	
	public void execute(final int year){		
		ajustarVentas(year);
		ajustarCargos(year);
		ajustarNotas(year);
	}
	
	public void ajustarVentas(final int year){
		final Set<Long> ids=getVentasIds(year);
		System.out.println("Por ajustar: "+ids.size());
		
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int count=0;
				for(Long id:ids){
					Venta v=(Venta)session
						.createQuery("from Venta v where v.id=:id")
						.setLong("id", id)
						.uniqueResult();
					CantidadMonetaria saldo=v.getSaldoEnMonedaSinImportaroSigno();
					v.setPago(saldo);		
					Pago p=new Pago();
					p.setVenta(v);
					p.setFecha(v.getFecha());
					p.setYear(v.getYear());
					p.setMes(v.getMes());
					p.setNumero(v.getNumero());
					p.setTipoDocto(v.getTipo());		
					p.setCliente(v.getCliente());
					p.setClave(v.getClave());
					p.setOrigen("INI");
					p.setFormaDePago("D");
					p.setDescFormaDePago(FormaDePago.D.getDesc());
					p.setDescFormaDePago("AJUSTE INI");
					p.setReferencia("INI_01");
					p.setComentario("Ajuste en carga inicial");
					p.setImporte(saldo.abs());
					v.agregarPago(p);
					System.out.println("Procesando: "+count+" de: "+ids.size()+ " Pago :"+p.getImporte());
					session.save(p);					
					if(count++%20==0){
						session.flush();
						session.clear();						
					}
				}
				System.out.println("Proc: "+count);
				return null;
			}
			
		});
	}
	
	private Set<Long> getCargosIds(int year){
		final String sql="select NOTA_ID from V_CARGOS where year=? and saldo>0 and nota_id not in(select nota_id from SALDNR05)";		
		final List<Map<String, Object>> rows=ServiceManager.instance().getDefaultJdbcTemplate().queryForList(sql,new Object[]{year});
		final Set<Long> ids=new HashSet<Long>();
		for(Map<String,Object> row:rows){
			Object obj=row.get("NOTA_ID");
			long id=((Number)obj).longValue();
			ids.add(new Long(id));
		}
		return ids;
	}
	
	public void ajustarCargos(final int year){
		final Set<Long> ids=getCargosIds(year);
		System.out.println("Cargos por ajustar: "+ids);		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int count=0;
				for(Long id:ids){
					NotaDeCredito n=(NotaDeCredito)session.load(NotaDeCredito.class, id);
					
					CantidadMonetaria saldo=n.getSaldoDelCargoEnMoneda();
							
					Pago p=new Pago();
					p.setNota(n);
					p.setFecha(n.getFecha());
					p.setYear(n.getYear());
					p.setMes(n.getMes());
					p.setNumero(n.getNumero());
					p.setTipoDocto(n.getTipo());		
					p.setCliente(n.getCliente());
					p.setClave(n.getClave());
					p.setOrigen("INI");
					p.setFormaDePago("D");
					p.setDescFormaDePago(FormaDePago.D.getDesc());
					p.setDescFormaDePago("AJUSTE INI");
					p.setReferencia("INI_01");
					p.setComentario("Ajuste en carga inicial");
					p.setImporte(saldo.abs());
					
					String msg=MessageFormat.format("Ajustando Nota {0} saldo {1} pago {2}",n.getId(),n.getSaldoDelCargoEnMoneda(),p.getImporte());
					System.out.println(msg);
					session.save(p);
					if(count++%20==0){
						session.flush();
						session.clear();						
					}
				}
				System.out.println("Proc: "+count);
				return null;
			}
			
		});
	}
	
	
	private Set<Long> getNotasIds(int year){
		final String sql="select NOTA_ID from V_NOTAS where year=? and saldo>0 and nota_id not in(select nota_id from SALDNC05)";		
		final List<Map<String, Object>> rows=ServiceManager.instance().getDefaultJdbcTemplate().queryForList(sql,new Object[]{year});
		final Set<Long> ids=new HashSet<Long>();
		for(Map<String,Object> row:rows){
			Object obj=row.get("NOTA_ID");
			long id=((Number)obj).longValue();
			ids.add(new Long(id));
		}
		return ids;
	}
	
	
	public void ajustarNotas(int year){
		final Set<Long> ids=getNotasIds(year);
		System.out.println("Cargos por ajustar: "+ids.size());		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int count=0;
				for(Long id:ids){
					NotaDeCredito n=(NotaDeCredito)session
						.createQuery("from NotaDeCredito n where n.id=:id")						
						.setLong("id", id)
						.uniqueResult();
					CantidadMonetaria saldo=n.getSaldoAsMoneda();
					
					NotaDeCredito c=new NotaDeCredito();
					c.setClave(n.getClave());
					c.setCliente(n.getCliente());
					c.setComentario("Nota de cargo INI");
					c.setFecha(n.getFecha());
					c.setImporte(saldo.divide(1.15));
					c.setTipo("M");
					c.setSerie("M");
					c.setOrigen("INI");
					c.setYear(n.getYear());
					c.setMes(n.getMes());
					

					NotasDeCreditoDet cdet=new NotasDeCreditoDet();
					c.agregarPartida(cdet);
					cdet.setYear(c.getYear());
					cdet.setMes(c.getMes());
					cdet.setComentario("Nota de cargo INI");
					cdet.setImporte(saldo);
					cdet.setFechaDocumento(n.getFecha());
					cdet.setNumDocumento(n.getNumero());
					cdet.setOrigen("INI");
					//det.setRenglon(1);
					cdet.setSerie(c.getSerie());
					cdet.setSerieDocumento(n.getSerie());
					cdet.setSucDocumento(1);
					cdet.setTipo(c.getTipo());
					cdet.setTipoDocumento(n.getTipo());
					
					
					
					
					Pago p=new Pago();
					p.setNota(c);
					p.setNotaDelPago(n);
					p.setNumero(c.getNumero());
					p.setTipoDocto(c.getTipo());		
					p.setCliente(c.getCliente());
					p.setClave(c.getClave());
					p.setOrigen("INI");
					p.setFormaDePago("D");
					p.setDescFormaDePago(FormaDePago.D.getDesc());
					p.setDescFormaDePago("AJUSTE INI");
					p.setReferencia("INI_01");
					p.setComentario("Ajuste en carga inicial");
					p.setImporte(saldo.abs());
					p.setYear(c.getYear());
					p.setMes(c.getMes());
					String msg=MessageFormat.format("Procesando nota {0} saldo {1} cargo {2}  cargo.pago {3}"
							, n.getId(),n.getSaldo(),c.getImporte(),p.getImporte());
					System.out.println(msg);
					
					session.save(c);
					session.save(p);
					if(count++%20==0){
						session.flush();
						session.clear();						
					}
				}
				System.out.println("Proc: "+count);
				return null;
			}
			
		});
	}
	
	
	public static void main(String[] args) {
		//new AjustadorDeSaldos().importData("SALDFC05","VENTA_ID");
		new AjustadorDeSaldos().execute(2005);
		
	}

}
