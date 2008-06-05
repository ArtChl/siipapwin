package com.luxsoft.siipap.em.replica.qro;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxp.dao.AnalisisDao;
import com.luxsoft.siipap.cxp.domain.Analisis;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.cxp.domain.AnalisisDet;
import com.luxsoft.siipap.dao.ProveedorDao;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * Proceso para la migracion de datos a Queretaro
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class MigracionQueretaro extends HibernateDaoSupport{
	
	private ApplicationContext qroCtx;
	private ApplicationContext ctx;
	private Proveedor papel;
	
	public MigracionQueretaro(){
		init();
	}
	
	private void init(){															 
		ctx=new ClassPathXmlApplicationContext(new String[]{"com/luxsoft/siipap/em/replica/qro/produccion-ctx.xml"});
		System.out.println("Contexto de produccion OK");
		SessionFactory f1=(SessionFactory)ctx.getBean("sessionFactory");
		prod.setSessionFactory(f1);
		
		qroCtx=new ClassPathXmlApplicationContext(new String[]{"com/luxsoft/siipap/em/replica/qro/qro-ctx.xml"});
		System.out.println("Contexto de Qro OK");
		SessionFactory f2=(SessionFactory)qroCtx.getBean("sessionFactory");
		qro.setSessionFactory(f2);
		ProveedorDao pdao=(ProveedorDao)qroCtx.getBean("proveedorDao");
		papel=pdao.buscarPorClave("P001");
	}
	
	public void procesarVentas(){
		prod.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int count=0;
				ScrollableResults scroll=session.createQuery("from Venta v where v.year=2006 and v.clave=?")
				.setString(0,"P010389")
				.scroll();
				while(scroll.next()){
					Venta v=(Venta)scroll.get()[0];
					//System.out.println("Factura: "+v.getNumeroFiscal()+" Num "+v.getNumero()+" Serie:"+v.getSerie());
					Analisis a=crearAnalisis(session,v);
					String msg=MessageFormat.format("Analisis generado factura: {0}\t TotalF: {1} Total A: {2}"
							,new Object[]{a.getFactura(),a.getTotalf(),a.getTotal()} );
					System.out.println(msg);
					salvarAnalisis(a);
					while(count++%20==0){
						session.flush();
						session.clear();
					}
				}
				return null;
			}
			
		});
	}
	
	protected Analisis crearAnalisis(final Session session,final Venta v) {
		Analisis a=new Analisis();
		a.setClave(v.getClave());
		a.setFactura("I"+v.getNumero());
		a.setFecha(v.getFecha());		
		a.setImpreso(false);		
		a.setMoneda(v.getTotal().currency());
		a.setNombre(v.getNombre());
		a.setProveedor(papel);
		a.setTc(BigDecimal.ONE);
		a.setImportef(v.getSubTotal());
		a.setImpuestof(v.getImpuesto());
		a.setTotalf(v.getTotal());
		
		List<AnalisisDeEntrada> entradas=localizarEntradas(v);
		
		
		NotasDeCreditoDet desc1=buscarNotaDeCreditoDesc1(session,v);
		if(desc1==null){
			String mm=MessageFormat.format("No se encontro el descuento para la venta \tid: {0}\tNum Fisc: {1}\tNumero: {2}"
					, new Object[]{v.getId(),v.getNumeroFiscal(),v.getNumero()});
			logger.error(mm);
		}
		
		if(entradas.size()==0 ||(entradas.size()!=v.getPartidas().size())){
			String msg="\tEl numero de AnalisisDeEntrada(COMS2) para la venta {0} " +
					"no es el esperado\t, la venta tiene {1} partidas  \ten coms2  hay {2} registros" +
					"\t Numero fiscal: {3}\t Numero int: {4}";
			msg=MessageFormat.format(msg, new Object[]{v.getId(),v.getPartidas().size(),entradas.size(),v.getNumeroFiscal(),v.getNumero()});
			logger.error(msg);
		}else{
			for(VentaDet det:v.getPartidas()){			
				for(AnalisisDeEntrada e:entradas  ){
					if(e.getClave().equals(det.getClave())){					
						AnalisisDet ad=new AnalisisDet();
						ad.setARTCLAVE(e.getClave());
						ad.setARTNOMBR(e.getDescripcion());
						BigDecimal cantidad=e.getIngresada();
						/*
						BigDecimal devoluciones=buscarDevolucion(session, det);
						if(devoluciones.doubleValue()>0){
							cantidad=cantidad.subtract(devoluciones);
							String mm=MessageFormat.format("VentaDet: {0} con devoluciones \tcantidad: {1} \t devoluciones: {2} \ttotal_analisis: {3}"
									, new Object[]{det.getId(),det.getCantidad(),devoluciones,cantidad});
							System.out.println(mm);
						}
						if(cantidad.equals(BigDecimal.ZERO))
							continue;
							*/
						ad.setCantidad(cantidad);
						//double costo=det.getPrecioLista();
						
						if(desc1!=null){						
							//double dd=((100+desc1.getDescuento())/100);
							//costo*=dd;
							ad.setCosto(CantidadMonetaria.pesos(det.getPrecioLista()));							
							ad.setDesc1(new BigDecimal(-desc1.getDescuento()));
							//ad.setNeto(CantidadMonetaria.pesos(costo));
							//ad.setNetoMN(ad.getNeto());							

							ad.setCom(e.getCOM());
							ad.setEntrada(e);
							ad.setFent(e.getFENT());
							//ad.setImporte();
							
							ad.setSucursal(1);
							ad.setTc(BigDecimal.ONE);
							ad.setUNIDAD(e.getUnidad());
							
							ad.calcularImportes();
							ad.setNetoMN(ad.getNeto());
							a.agregarPartida(ad);
						}else
							continue;
						
					}
					//System.out.println("VentaDet sin COMS2: "+det.getId());
				}
			}
			
		}
		
		if(a.getImporte()==null){
			a.setImporte(CantidadMonetaria.pesos(0));
		}		
		a.setImpuesto(MonedasUtils.calcularImpuesto(a.getImporte()));
		a.setTotal(MonedasUtils.calcularTotal(a.getImporte()));
		a.calcularImportesEnMN();
		return a;
		
	}
	
	
	protected List<AnalisisDeEntrada> localizarEntradas(final Venta v) {
		
		return qro.getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from AnalisisDeEntrada e where e.PROVCLAVE=:prov and e.FACREM=:fac")// and e.ingresada=:ing")
				.setString("prov", papel.getClave())
				.setLong("fac", v.getNumeroFiscal())
				//.setBigDecimal("ing", new BigDecimal(-v.getCantidad()))
				.list();
			}
			
			
		});		
	}
	
	protected NotasDeCreditoDet buscarNotaDeCreditoDesc1(Session s,Venta v){
		try {
			Object o=s.createQuery("from NotasDeCreditoDet d where d.factura=:venta and d.serie in (\'U\',\'T\')")
			.setEntity("venta", v)
			.uniqueResult();
			return (NotasDeCreditoDet)o;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private BigDecimal buscarDevolucion(Session s,VentaDet v){
		try {
			List<DevolucionDet> devos=s.createQuery("from DevolucionDet d where d.ventaDet=:venta and d.nota is not null")
			.setEntity("venta", v)
			.list();
			double val=0;
			for(DevolucionDet devo:devos){
				val=val+devo.getCantidad();
			}
			return new BigDecimal(val);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	private void salvarAnalisis(final Analisis a){
		AnalisisDao dao=(AnalisisDao)qroCtx.getBean("analisisDao");
		dao.salvar(a);
	}
	
	protected DataSource getDataSource(){
		return (DataSource)ctx.getBean("dataSource");
	}
	protected DataSource getQroDataSource(){
		return (DataSource)qroCtx.getBean("dataSource");
	}
	
	
	
	
	public static void main(String[] args) {
		MigracionQueretaro m=new MigracionQueretaro();
		m.procesarVentas();
		
	}
	
	private HibernateDaoSupport prod=new HibernateDaoSupport(){
		
	};
	private HibernateDaoSupport qro=new HibernateDaoSupport(){
		
	};
}
