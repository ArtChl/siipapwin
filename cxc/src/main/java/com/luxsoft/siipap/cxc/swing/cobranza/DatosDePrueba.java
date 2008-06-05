package com.luxsoft.siipap.cxc.swing.cobranza;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.cxc.model.SolicitudDePago;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.dao.DevolucionDao;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public class DatosDePrueba {
	
	
	public static EventList<Venta> ventasDePrueba(){
		try {
			String clave="U050008";
			String nombre="UNION DE CREDITO";
			Cliente c=new Cliente();
			c.setClave(clave);
			c.setNombre(nombre);
			c.setTipo_vencimiento("V");
			c.setCredito(new ClienteCredito(c));
			c.getCredito().updateProperties();
			c.setCorreoelectronico1("rcancino@luxsoftnet.com");
			EventList<Venta> ventas=new BasicEventList<Venta>();
			DateFormat df=new SimpleDateFormat("dd/MM/yy");
			Double[] montos={12345.25,45365.50,25368.00};
			Date[] fechas={df.parse("31/03/07"),df.parse("31/03/07"),df.parse("31/03/07")};
			Long[] numeros={13423l,13424l,13428l};
			
			for(int i=0;i<montos.length;i++){
				Venta v=new Venta();
				v.setId(new Long(i+1));
				v.setCliente(c);
				v.setDiaRevision(6);
				v.setCobrador(8);
				v.setDiaPago(6);
				v.setClave(clave);
				v.setSucursal(1);
				v.setNombre(nombre);
				v.setImporteBruto(CantidadMonetaria.pesos(montos[i]));
				v.setSubTotal(CantidadMonetaria.pesos(montos[i]));
				v.setTotal(v.getSubTotal().multiply(1.15));
				v.setFecha(fechas[i]);
				v.setNumero(numeros[i]);
				v.setNumeroFiscal(v.getNumero().intValue());
				v.setSaldo(v.getTotal().amount());
				v.setTipo("E");
				v.setOrigen("CRE");
				v.setVencimiento(calcularVencimiento(v.getFecha()));
				v.actualizarDatosDeCredito();
				v.getCredito().setDescuento(50);
				
				
				
				Provision p=new Provision(v){
					public double getDescuento(){
						return 45.00;
					}
				};
				p.setDescuento1(55.00);
				p.calcularProvision();
				
				ventas.add(v);				
				
			}
			return ventas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;		}
		
	}
	
	
	
	public static Date calcularVencimiento(Date d){
		Calendar c=Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 30);
		return c.getTime();
	}
	
	public static EventList<SolicitudDePago> solicitudes(){
		EventList<SolicitudDePago> s=new BasicEventList<SolicitudDePago>();
		for(Venta v:ventasDePrueba()){
			SolicitudDePago sp=new SolicitudDePago();
			sp.setVenta(v);
			sp.setDesc1(51.00);
			sp.setPagoAsNumber(v.getSaldo());
			s.add(sp);
		}
		return s;
	}
	
	public static EventList<Pago> createPagosPrueba(Venta v){
		EventList<Pago> pagos=new BasicEventList<Pago>();
		Pago p=new Pago();
		p.setId(2433l);
		p.setClave(v.getClave());
		pagos.add(p);
		return pagos;
	} 
	
	public static Cliente createClienteDePrueba(){
		return new Cliente("U050008","Union de credito");
	}
	
	public static EventList<NotaDeCredito> crearNotasDeCargo(final Cliente c){		
		final List<NotaDeCredito> cargos=new ArrayList<NotaDeCredito>();
		for(int i=0;i<10;i++){
			NotaDeCredito n=new NotaDeCredito();
			n.setCliente(c);
			n.setClave(c.getClave());
			n.setNumero(i+100);
			n.setImporte(CantidadMonetaria.pesos(4000));
			n.setTipo("M");
			n.setSerie("M");
			n.setTotal(4000*1.15);
			n.setSaldoDelCargo(n.getTotal());
			n.setComentario("Cargo test"+i);
			n.setId(Long.valueOf(i));
			cargos.add(n);
		}
		return GlazedLists.eventList(cargos);
	}
	
	public static NotaDeCredito crearNotaDeCreditoDescuento1(Venta v){
		NotaDeCredito nc=new NotaDeCredito();
		nc.setTipo("U");
		nc.setSerie("U");
		nc.setDescuento(45);
		nc.setImporte(v.getSubTotal().multiply(.45));
		nc.setNumero(2443);
		return nc;
	}
	
	public static Provision crearProvision(Venta v){
		Provision p=new Provision();
		p.setVenta(v);
		p.setImporte(v.getImporteBruto().multiply(.43).amount());
		p.actualizarCargo();
		
		return p;
	}
	
	public static Venta buscarVenta(Long id){
		VentasDao dao=(VentasDao)ServiceLocator.getDaoContext().getBean("ventasDao");
		return dao.buscarPorId(id);
	}
	
	public static Cliente buscarCliente(String clave){
		ClienteDao dao=(ClienteDao)ServiceLocator.getDaoContext().getBean("clienteDao");
		return dao.buscarPorClave(clave);
	}
	
	
	
	/**
	 * Regresa 10 ventas con saldo para el cliente seleccionado
	 * 
	 * @param clave
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Venta> buscarVentasConSaldoEnDB(final String clave){		
		HibernateTemplate tm=new HibernateTemplate(ServiceLocator.getSessionFactory());
		return tm.executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Venta v left join fetch v.credito " +
						" left join fetch v.cliente" +
						" where v.year=2007 and v.saldo>0 and v.clave=:clave";
				return session.createQuery(hql)
				.setMaxResults(10)
				.setString("clave", clave)
				.list();
				
			}
			
		});
		
	}
	
	/**
	 * Regresa 10 ventas con saldo <100
	 * 
	 * @param clave
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Venta> buscarVentaParaPagoAutomatico(){		
		HibernateTemplate tm=new HibernateTemplate(ServiceLocator.getSessionFactory());
		return tm.executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Venta v left join fetch v.credito " +
						" left join fetch v.cliente" +
						" where v.year=2007 and v.saldo>0 and v.saldo<100" +
						" and v.tipo=:tipo order by v.clave";
				List<Venta> ventas=session.createQuery(hql)
				.setMaxResults(100)
				.setString("tipo", "E")
				.list();
				
				return ventas;
			}
			
		});
		
	}
	
	public static Devolucion createDevolucionDePrueba(){
		return createDevolucionDePrueba(200);
	}
	
	public static Devolucion createDevolucionDePrueba(double precio){
		final Venta v=ventasDePrueba().get(0);
		v.setPrecioCorte(CantidadMonetaria.pesos(5));
		Devolucion d=new Devolucion();
		d.setCliente(v.getClave());
		d.setId(5L);
		d.setNumero(10L);
		d.setVenta(v);
		d.setFecha(DateUtils.obtenerFecha("20/08/2007"));
		d.setComentario("Devolucion de prueba");
		for(int i=0;i<15;i++){
			DevolucionDet det=new DevolucionDet();
			det.setArticulo(new Articulo());
			det.getArticulo().setClave("PB36"+i);
			det.getArticulo().setDescripcion1("PAPALE PB36  "+i);
			det.setCantidad(i+2);
			d.addPartida(det);
			VentaDet vd=new VentaDet();
			vd.setArticulo(det.getArticulo());
			vd.setPrecioFacturado(precio);			
			det.setVentaDet(vd);
			det.setImporte(det.getCantidad()*vd.getPrecioFacturado());
			
		}
		d.actualizarImporte();
		return d;
	}
	
	public static EventList<NotaDeCredito> createCargosDePrueba(){
		final EventList<NotaDeCredito> cargos=new BasicEventList<NotaDeCredito>();
		for(int i=0;i<5;i++){
			NotaDeCredito n=new NotaDeCredito();
			n.setCliente(createClienteDePrueba());
			n.setClave(n.getCliente().getClave());
			n.setSaldoDelCargo(5000);
			n.setTipo("M");
			n.setSerie("M");
			n.setOrigen("CRE");
			n.setId((long)i);
			cargos.add(n);
		}
		return cargos;
	}
	
	public static NotaDeCredito buscarNotasConDevoluciones(){
		NotaDeCredito nc= (NotaDeCredito)ServiceLocator.getDaoSupport().getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)throws HibernateException, SQLException {
				return session.createQuery("from NotaDeCredito nc " +
						" left join fetch nc.partidas p" +
						" left join fetch nc.cliente c" +
						" left join fetch nc.devolucion d" +
						" where nc.clave=:cliente " +
						"  and nc.devolucion is not null " +
						"  and nc.cliente is not null" +
						"  and nc.year=2007")
						.setString("cliente", "U050008")
						.setMaxResults(1)
						.uniqueResult();
			}			
		});
		System.out.println("Nota: "+nc.getId()+" Cliente: "+nc.getCliente());
		return nc;
	}
	 
	public static Devolucion buscarDevolucion(Long id){
		DevolucionDao dao=(DevolucionDao)ServiceLocator.getDaoContext().getBean("devolucionDao");
		return dao.buscarPorId(id);
	}
	

}
