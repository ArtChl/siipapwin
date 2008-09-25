package com.luxsoft.siipap.em.replica.notas;

import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.dao.DevolucionDao;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Parche para corregir vinculaciones de notas y notasdet
 * 
 * @author Ruben Cancino
 *
 */
public class VinculacionDeNotas extends HibernateDaoSupport{
	
	
	protected Logger logger=Logger.getLogger(getClass());
	
	public VinculacionDeNotas() {
		super();
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void vincularClientes(){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotaDeCredito n " +
						" where   and n.cliente  is null";
				ScrollableResults rs=session.createQuery(hql)				
				.scroll();
				int count=0;
				while(rs.next()){
					final NotaDeCredito n=(NotaDeCredito)rs.get()[0];
					Cliente c=getClienteDao().buscarPorClave(n.getClave());
					if(c!=null)
						n.setCliente(c);
					System.out.println("Actualizando cliente de nota: "+n);
					if(count++%20==0){
						session.flush();
						session.clear();
					}					
				}
				return null;
			}
			
		});
	}



	/**
	 * Vincula las notas de creditodet con sus ventas
	 *
	 */
	public void vincularVentas(){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotasDeCreditoDet n where   n.factura is null";
				ScrollableResults rs=session.createQuery(hql)				
				.scroll();
				int count=0;
				while(rs.next()){
					final NotasDeCreditoDet det=(NotasDeCreditoDet)rs.get()[0];
					Venta v=getVentasDao().buscarVenta(det.getSucDocumento(),det.getSerieDocumento(), det.getTipoDocumento(), det.getNumDocumento());
					if(v==null){
						v=getVentasDao().buscarVenta(det.getSucDocumento(), det.getTipoDocumento(), det.getNumDocumento());
					}
					det.setFactura(v);
					System.out.println("Actualizando venta de notadet: "+det);
					if(count++%20==0){
						session.flush();
						session.clear();
					}					
				}
				return null;
			}
			
		});
	}
	
	public void vincularDevoluciones(){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotaDeCredito n where n.tipo in(?,?,?) and devolucion is null ";
				ScrollableResults rs=session.createQuery(hql)
				.setString(0, "H")
				.setString(1, "I")
				.scroll();
				int count=0;				
				while(rs.next()){
					
					final NotaDeCredito nota=(NotaDeCredito)rs.get()[0];
					Devolucion d=getDevolucionDao().buscar(nota.getNumeroDevo(), nota.getSucursalDevo());
					if(d!=null){
						nota.setDevolucion(d);
						for(NotasDeCreditoDet det:nota.getPartidas()){
							Venta v=d.getVenta();
							if(nota.getCliente()==null)
								nota.setCliente(v.getCliente());
							det.setFactura(v);
							det.setSucDocumento(v.getSucursal());
							det.setTipoDocumento(v.getTipo());
							det.setSerieDocumento(v.getSerie());
							det.setNumDocumento(v.getNumero());
							det.setFechaDocumento(v.getFecha());
							det.setImporte(nota.getImporte().multiply(1.15));
						}
					}
					if(count++%20==0){
						session.flush();
						session.clear();
					}
					
				}
				return null;
			}
			
		});
	}
	
	public void vincularPagosVentas(){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Pago p where p.tipoDocto in(?,?,?,?) and venta is null ";
				ScrollableResults rs=session.createQuery(hql)
				.setString(0, "A")
				.setString(1, "B")
				.setString(2, "C")
				.setString(3, "D")
				.scroll();
				int count=0;
				while(rs.next()){
					final Pago p=(Pago)rs.get()[0];
					Venta v=getVentasDao().buscarVenta(p.getSucursal(), p.getTipoDocto(), p.getNumero());
					if(v!=null)
						p.setVenta(v);
					else{
						String msg=MessageFormat.format("No localizo venta para pago en sucursal: {0} numero: {1} y tipo:{2}", p.getSucursal(),p.getNumero(),p.getTipoDocto());
						System.out.println(msg);
					}						
					if(count++%20==0){
						session.flush();
						session.clear();
					}
					
				}
				return null;
			}
			
		});
	}
	
	public VinculacionDeNotas vincularClientesPagos(){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Pago p " +
						" where   p.cliente  is null";
				ScrollableResults rs=session.createQuery(hql)				
				.scroll();
				int count=0;
				while(rs.next()){
					final Pago p=(Pago)rs.get()[0];
					Cliente c=getClienteDao().buscarPorClave(p.getClave());
					if(c!=null){
						p.setCliente(c);
						System.out.println("Actualizando cliente de pago: "+p);
					}else
						System.out.println("Cliente no localizado: "+p.getClave());
					if(count++%20==0){
						session.flush();
						session.clear();
					}					
				}
				return null;
			}
			
		});
		return this;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return ServiceLocator.getJdbcTemplate();
	}

	public ClienteDao getClienteDao() {
		return (ClienteDao)ServiceLocator.getDaoContext().getBean("clienteDao");
	}

	public VentasDao getVentasDao() {
		return (VentasDao)ServiceLocator.getDaoContext().getBean("ventasDao");
	}
	
	public DevolucionDao getDevolucionDao(){
		return (DevolucionDao)ServiceLocator.getDaoContext().getBean("devolucionDao");
	}

	public static void main(String[] args) {
		VinculacionDeNotas manager=new VinculacionDeNotas();
		//manager.vincularClientes();
		//manager.vincularVentas();
		//manager.vincularDevoluciones();
		
		
		manager.vincularClientesPagos();
	}
	

}
