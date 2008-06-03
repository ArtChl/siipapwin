package com.luxsoft.siipap.maquila.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.luxsoft.siipap.maquila.domain.Almacen;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.RecepcionDeMaterial;
import com.luxsoft.siipap.services.ServiceLocator;

@SuppressWarnings("unchecked")
public class RecepcionDeMaterialDaoImpl extends HibernateDaoSupport implements RecepcionDeMaterialDao{

	
	
	public void create(RecepcionDeMaterial rec){
		for(EntradaDeMaterial e:rec.getEntradas()){
			e.setEntradaDeMaquilador(rec.getEntradaDeMaquilador());
		}
		getHibernateTemplate().saveOrUpdate(rec);
	}
	
	public RecepcionDeMaterial get(Long id){
		return (RecepcionDeMaterial)getHibernateTemplate().get(RecepcionDeMaterial.class,id);
	}
	
	public void update(RecepcionDeMaterial rec){
		create(rec);
	}
	
	public void delete(RecepcionDeMaterial rec){
		getHibernateTemplate().delete(rec);
	}
	
	
	public List<RecepcionDeMaterial> buscarRecepciones(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(RecepcionDeMaterial.class)
					.setFetchMode("almacen",FetchMode.JOIN)
					.setFetchMode("almacen.maquilador",FetchMode.JOIN)
					.list();
			}
			
		});
	}
	
	public void initEntradas(final RecepcionDeMaterial r){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(r,LockMode.NONE);
				Hibernate.initialize(r.getEntradas());
				return null;
			}
			
		});
	}
	
	public void eliminarRecepcion(final RecepcionDeMaterial r){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(r,LockMode.NONE);
				Assert.isTrue(!r.tieneCortes(),"No se puede Borrar una Recepcion que tiene por lo menos una entrada\n" +
						" con cortes procesados");
				session.delete(r);
				return null;
			}			
		});
	}
	
	public List<EntradaDeMaterial> buscarEntradasDisponibles(){
		String hql="from EntradaDeMaterial e where e.disponibleKilos>0";
		return getHibernateTemplate().find(hql);
	}
	
	public List<EntradaDeMaterial> buscarEntradasDisponibles(final String articulo){
		String hql="from EntradaDeMaterial e where e.disponibleKilos>0 and e.articulo.clave=?";		
		return getHibernateTemplate().find(hql,articulo);
	}
	
	public List<EntradaDeMaterial> buscarEntradasDisponibles(final Almacen almacen){
		String hql="from EntradaDeMaterial e where e.disponibleKilos>0 and e.recepcion.almacen=?";		
		return getHibernateTemplate().find(hql,almacen);
	}
	
	public List<EntradaDeMaterial> buscarEntradasPorTraslados(boolean disponible){
		String hql="from EntradaDeMaterial e " +
				"left join fetch e.articulo " +
				"left join fetch e.recepcion r " +
				"left join fetch r.almacen a " +
				"left join fetch a.maquilador  " +
				"left join fetch e.trasladoOrigen to " +
				"left join fetch to.entrada toe " +
				"left join fetch toe.recepcion toer " +
				"left join fetch toer.almacen toera " +
				"where e.trasladoOrigen is not null";
		if(disponible)
			hql=hql+" and e.disponibleKilos>0";
		return getHibernateTemplate().find(hql);
	}
	
	public EntradaDeMaterial buscarEntrada(final Long id){
		return (EntradaDeMaterial)getHibernateTemplate().get(EntradaDeMaterial.class,id);
	}

	public List<EntradaDeMaterial> buscarEntradas() {
		final String hql="from EntradaDeMaterial e " +
				"left join fetch e.articulo b " +				
				"left join fetch e.trasladoOrigen to " +
				"order by e.id desc";		
		return getHibernateTemplate().find(hql);
	}
	
	public static void main(String[] args) {
		RecepcionDeMaterialDao dao=(RecepcionDeMaterialDao)ServiceLocator.getDaoContext().getBean("recepcionDeMaterialDao");
		List<EntradaDeMaterial> entradas=dao.buscarEntradas();
		for(EntradaDeMaterial e:entradas){
			System.out.println("Entrada: "+e.getId()+" Bobina: "+e.getArticulo().getClave());
		}
	}

}

