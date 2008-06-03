package com.luxsoft.siipap.ventas.dao;



import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

public class DevolucionDaoImpl extends HibernateDaoSupport implements  DevolucionDao{

	

	public void salvar(final Devolucion dev) {
		getHibernateTemplate().saveOrUpdate(dev);		
	}

	public void eliminar(Devolucion dev) {
		getHibernateTemplate().delete(dev);
	}

	public Devolucion buscarPorId(final Long id) {
		return (Devolucion)getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Devolucion d=(Devolucion)session.get(Devolucion.class, id);
				if(d==null) return null;				
				for(DevolucionDet det:d.getPartidas()){
					Hibernate.initialize(det.getVentaDet());
					Hibernate.initialize(det.getArticulo());
					Hibernate.initialize(det.getVentaDet().getVenta());
				}
				return d;
						
			}
			
		});
	}
	
	public void salvarDevoluciones(final Collection<Devolucion> devs){
		logger.debug("Devoluciones a salvar: "+devs.size());
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int buff=0;
				
				for(Devolucion d:devs){					
					session.saveOrUpdate(d);
					if(++buff%20==0){
						System.out.println("Salvando buffer de 20");
						session.flush();
						session.clear();
					}
					
				}				
				return null;
			}
			
		});
	}
	

	public Devolucion buscar(final long numero, final int sucursal) {
		final String hql="select distinct d from Devolucion d left join fetch d.venta v where d.venta.sucursal=:sucursal and d.numero=:numero";
		return (Devolucion)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("sucursal", sucursal)
				.setParameter("numero",numero)				
				.uniqueResult();
				
			}
			
		});
	}

	@SuppressWarnings("unchecked")
	public List<DevolucionDet> buscarDevolucionesSinAplicar(final String cliente) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from DevolucionDet d " +
						"left join fetch d.devolucion dd " +
						"left join fetch d.articulo a " +
						"left join fetch d.ventaDet v " +						
						"left join fetch v.venta vv " +
						"left join fetch vv.cliente c " +
						"where d.year>2006" +
						" and d.nota is  null " +
						//" and d.ventaDet.venta.cliente.clave=:clave")
						" and d.devolucion.cliente=:clave")
						.setString("clave",cliente)
						.list();
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<DevolucionDet> buscarDevoluciones(final Venta v){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(
						"from DevolucionDet d " +
						"left join fetch d.devolucion dd " +
						"left join fetch d.ventaDet fac " +
						"join fetch d.nota nota " +
						" where d.ventaDet.venta=:venta" )
						.setEntity("venta",v)
						.list();
			}
			
		});
	}

}
