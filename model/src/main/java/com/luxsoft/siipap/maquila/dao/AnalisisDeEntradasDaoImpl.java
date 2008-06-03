package com.luxsoft.siipap.maquila.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.maquila.domain.AnalisisDeEntradas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.utils.SiipapDateUtils;

@SuppressWarnings("unchecked")
public class AnalisisDeEntradasDaoImpl extends HibernateDaoSupport implements AnalisisDeEntradasDao{

	public void salvar(AnalisisDeEntradas analisis) {
		getHibernateTemplate().saveOrUpdate(analisis);
	}

	public void eliminar(AnalisisDeEntradas analisis) {
		analisis.removerEntradas();
		getHibernateTemplate().delete(analisis);
	}

	public AnalisisDeEntradas getFromId(Long id) {
		return (AnalisisDeEntradas)getHibernateTemplate().get(AnalisisDeEntradas.class,id);
	}

	public void actualizar(AnalisisDeEntradas analisis) {
		salvar(analisis);		
	}

	
	public AnalisisDeEntradas buscarPorFactura(String factura, Proveedor p) {
		String hql="from AnalisisDeEntradas a where a.factura=:? and a.proveedor.clave=:?";
		List<AnalisisDeEntradas> entradas=getHibernateTemplate().find(hql,new Object[]{factura,p.getClave()});
		if(entradas.size()==1)
			return entradas.get(0);
		return null;
	}

	public List<AnalisisDeEntradas> buscarTodos() {
		String hql="from AnalisisDeEntradas ";
		return getHibernateTemplate().find(hql);
	}

	public List<EntradaDeMaterial> buscarEntradasPorFactura(String factura, Proveedor p) {
		String hql="from EntradaDeMaterial e where e.analisis is not null and e.analisis.factura=? and e.analisis.proveedor.clave=?" ;
		List<EntradaDeMaterial> entradas=getHibernateTemplate().find(hql,new Object[]{factura,p.getClave()});
		return entradas;
	}
	
	public List<EntradaDeMaterial> buscarEntradas(){
		String hql="from EntradaDeMaterial e " +
				"left join fetch e.analisis a " +
				"left join fetch e.articulo art" +
				"left join fetch e.recepcion r "+			
				"left join fetch r.almacen ra " +
				"left join fetch ra.maquilador ";
		List<EntradaDeMaterial> entradas=getHibernateTemplate().find(hql);
		return entradas;
	}
	
	public List<EntradaDeMaterial> buscarEntradasPendientes(){
		final String hql="from EntradaDeMaterial e " +		
		"left join fetch e.articulo art" +
		"left join fetch e.recepcion r "+			
		"left join fetch r.almacen ra " +
		"left join fetch ra.maquilador " +
		"where e.analisis is null and e.fecha>?";
		List<EntradaDeMaterial> entradas=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter(0,SiipapDateUtils.getMXDate("01/01/06"))
				.list();
				
			}
			
		});
		return entradas;
	}
	
	/**
	 * Inicializa un analisis particularmente inicializa su coleccion
	 * de EntradaDeMaterial
	 * @param a
	 */
	public void inicializarAnalisis(final AnalisisDeEntradas a){
		
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				session.lock(a,LockMode.NONE);
				Hibernate.initialize(a.getEntradas());
				for(EntradaDeMaterial e:a.getEntradas()){
					Hibernate.initialize(e.getArticulo());
					Hibernate.initialize(e.getRecepcion());
					Hibernate.initialize(e.getRecepcion().getAlmacen());
					Hibernate.initialize(e.getRecepcion().getAlmacen().getMaquilador());
				}
				return null;
				
			}
			
		});
	}

}
