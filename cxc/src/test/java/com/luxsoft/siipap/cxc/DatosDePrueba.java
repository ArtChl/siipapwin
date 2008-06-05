package com.luxsoft.siipap.cxc;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.cxc.model.SolicitudDePago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

public class DatosDePrueba {
	
	
	public static EventList<Venta> ventasDePrueba(){
		try {
			String clave="U050008";
			String nombre="UNION DE CREDITO";
			Cliente c=new Cliente();
			c.setClave(clave);
			c.setNombre(nombre);
			c.setTipo_vencimiento("V");
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
				
				Provision p=new Provision(v);
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
						" where v.year>2005 and v.saldo>0 and v.clave=:clave";
				return session.createQuery(hql)
				.setMaxResults(10)
				.setString("clave", clave)
				.list();
				
			}
			
		});
		
	}

}
