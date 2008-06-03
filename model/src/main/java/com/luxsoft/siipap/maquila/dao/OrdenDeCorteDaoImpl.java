package com.luxsoft.siipap.maquila.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.luxsoft.siipap.maquila.domain.Almacen;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.OrdenDeCorte;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;

@SuppressWarnings("unchecked")
public class OrdenDeCorteDaoImpl extends HibernateDaoSupport implements OrdenDeCorteDao{

	public void create(final OrdenDeCorte oc){
		for(SalidaACorte sa:oc.getSalidas()){
			if(sa.getKilos().doubleValue()>0)
				sa.setKilos(sa.getKilos().multiply(BigDecimal.valueOf(-1)));
			if(sa.getMetros2().doubleValue()>0)
				sa.setMetros2(sa.getMetros2().multiply(BigDecimal.valueOf(-1)));
		}
		//Assert.isNull(oc.getId(),"Orden de corte ya existe: "+oc.getId());
		//getHibernateTemplate().save(oc);
		getHibernateTemplate().saveOrUpdate(oc);
		actualizarSaldoDeEntradas(oc.getSalidas());
	}

	
	public OrdenDeCorte get(Long id){
		String hql="from OrdenDeCorte oc left join fetch oc.salidas left join oc.almacen a";
		List<OrdenDeCorte> list=getHibernateTemplate().find(hql);
		return list.isEmpty()?null:list.get(0);
	}
	
	public void update(final OrdenDeCorte oc){
		Assert.notNull(oc.getId(),"Orden de corte no es nueva");
	
	}
	
	/**
	 * Elimina una orden de corte de acuerdo a las reglas de negocios
	 * 
	 * @param oc
	 */
	public void delete(final OrdenDeCorte oc){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(oc,LockMode.NONE);
				Set<SalidaACorte> cortes=oc.getSalidas();
				oc.getSalidas().clear();
				for(SalidaACorte sc:cortes){
					EntradaDeMaterial em=sc.getEntrada();
					em.recalcularSaldos();
				}
				session.delete(oc);
				return null;
			}			
		});
	}
	
	public List<OrdenDeCorte> buscarOrdenes() {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from OrdenDeCorte o " +
						" left join fetch o.almacen a" +
						" left join fetch a.maquilador m"+
						" left join fetch o.salidas";
				List<OrdenDeCorte> cortes=session.createQuery(hql).list();
				Set set=new HashSet(cortes);
				return new ArrayList(set);
			}			
		});
	}
	
	public List<EntradaDeMaterial> buscarEntradasDisponibles(final Almacen a){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(EntradaDeMaterial.class)
					.createAlias("recepcion","r")
					.setFetchMode("r.almacen",FetchMode.JOIN)
					.setFetchMode("r.almacen.maquilador",FetchMode.JOIN)
					.add(Restrictions.eq("r.almacen",a))
					.add(Restrictions.gt("disponibleKilos",BigDecimal.ZERO))
					.list();
			}
			
		});
	}
	
	/**
	 * Busca todas las entrads de material disponibles
	 * @return
	 */
	public List<EntradaDeMaterial> buscarEntradasDisponibles(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final String hql="from EntradaDeMaterial e" +
						"  left join fetch e.articulo b" +
						"  left join fetch e.recepcion r" +
						"  left join fetch e.recepcion.almacen a" +
						"  left join fetch e.recepcion.almacen.maquilador " +
						"where e.disponibleKilos>0";
				return session.createQuery(hql).list();
				/*
				return session.createCriteria(EntradaDeMaterial.class)
					.createAlias("recepcion","r")
					.createAlias("r.almacen","ra")
					.setFetchMode("r.almacen",FetchMode.JOIN)
					.setFetchMode("a.maquilador",FetchMode.JOIN)
					.list();*/
			}
			
		});
	}
	
	/**
	 * Inicializa todas las asociacienes importantes de la
	 * orden de corte
	 * 
	 * @param oc
	 */
	public void inicializarOrdenDeCorte(final OrdenDeCorte oc){
		if(oc.getId()==null) return;
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(oc,LockMode.NONE);
				for(SalidaACorte sc:oc.getSalidas()){
					//Hibernate.initialize(sc.getArticulo());
					session.lock(sc,LockMode.NONE);
					Hibernate.initialize(sc.getEntrada());
					
				}
				return null;
			}			
		});
	}
	
	
	/**
	 * Actualiza el saldo de las entradas anteriormente asociadas
	 * a estas Salidas 
	 * 
	 * @param beans
	 */
	public void actualizarSaldoDeEntradas(final Collection<SalidaACorte> salidas){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				for(SalidaACorte sc:salidas){
					session.lock(sc,LockMode.NONE);					
					EntradaDeMaterial em=sc.getEntrada();
					em.recalcularSaldos();
				}
				return null;
			}			
		});
	}
	
	/**
	 * Busca las SalidasACorte
	 * 
	 * @param disponibles
	 * @return
	 */
	public List<SalidaACorte> buscarSalidas(final boolean disponibles){
		
		String hql="from SalidaACorte sc " +
				"left join fetch sc.articulo " +
				"left join fetch sc.destino " +
				"left join fetch sc.orden so" +
				"left join fetch sc.orden.almacen soa" +
				"left join fetch sc.entrada e" +
				" where sc not in(select ej.origen from EntradaDeHojas ej)";
		
		String hql2="from SalidaACorte sc " +
			"left join fetch sc.articulo " +
			"left join fetch sc.destino " +
			"left join fetch sc.orden so" +
			"left join fetch sc.orden.almacen soa" +
			"left join fetch sc.entrada e";
			
		if(!disponibles)
			return getHibernateTemplate().find(hql2);
		return getHibernateTemplate().find(hql);
		
		/*
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c=session.createCriteria(SalidaACorte.class)
				.setFetchMode("articulo",FetchMode.JOIN)
				.setFetchMode("destino",FetchMode.JOIN)
				.setFetchMode("orden",FetchMode.JOIN)
				.setFetchMode("entrada",FetchMode.JOIN);
				return c.list();
			}			
		});
		*/
	}
	
	public void inicializarSalidaACorte(final SalidaACorte sc){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(sc,LockMode.NONE);
				Hibernate.initialize(sc.getEntrada());
				return null;
			}
			
		});
	}
	
	
	
}
