package com.luxsoft.siipap.cxp.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;


@SuppressWarnings("unchecked")
public class AnalisisDeEntradaDaoImpl extends HibernateDaoSupport implements AnalisisDeEntradaDao{
	
		

	public void salvar(final AnalisisDeEntrada com) {
		//com.get
		getHibernateTemplate().saveOrUpdate(com);
	}
	
	public AnalisisDeEntrada buscarAnalisisDeEntrada(final Long id){
		return (AnalisisDeEntrada)getHibernateTemplate().get(AnalisisDeEntrada.class,id);
	}
	
	public List<AnalisisDeEntrada> buscarEntradasPorCompra(final Periodo p){
		String hql="from AnalisisDeEntrada a join fetch a.com c where a.com.ALMFECHA between :f1 and :f2";
		return getHibernateTemplate().findByNamedParam(hql,new String[]{"f1","f2"},new Object[]{p.getFechaInicial(),p.getFechaFinal()});
	}
	
	public List<AnalisisDeEntrada> buscarEntradasPorCompra(final Periodo p,final String clave){
		String hql="from AnalisisDeEntrada a where a.clave=:clave and a.FENT between :f1 and :f2";
		return getHibernateTemplate().findByNamedParam(hql,new String[]{"clave","f1","f2"},new Object[]{clave,p.getFechaInicial(),p.getFechaFinal()});
	}
	
	public List<AnalisisDeEntrada> buscarEntradasPorProveedor(final String clave,final Periodo p){
		String hql="from AnalisisDeEntrada a join fetch a.com c where a.PROVCLAVE=:proveedor and c.ALMFECHA between :f1 and :f2";
		return getHibernateTemplate().findByNamedParam(hql
				,new String[]{"proveedor","f1","f2"}
				,new Object[]{clave,p.getFechaInicial(),p.getFechaFinal()});
	}
	
	public List<AnalisisDeEntrada> buscarDisponibles(final String proveedor){
		String hql="from AnalisisDeEntrada a join fetch a.com c where a.PROVCLAVE=:proveedor and a.porAnalizar>0 order by a.FENT desc";
		//String hql="from AnalisisDeEntrada a join fetch a.com c where a.PROVCLAVE=:proveedor and (a.ingresada-a.analizadoHojas)>0 order by a.FENT desc";
		return getHibernateTemplate().findByNamedParam(hql,"proveedor",proveedor);
	}
	
	public List<AnalisisDeEntrada> buscarDisponibles(final String proveedor,final String articulo){
		String hql="from AnalisisDeEntrada a join fetch a.com c where a.PROVCLAVE=:proveedor and a.clave=:clave and a.porAnalizar>0 order by a.FENT desc";
		//String hql="from AnalisisDeEntrada a join fetch a.com c where a.PROVCLAVE=:proveedor and a.clave=:clave and (a.ingresada-a.analizadoHojas)>0 order by a.FENT desc";
		String[] names={"proveedor","clave"};
		Object[] vals={proveedor,articulo};
		return getHibernateTemplate().findByNamedParam(hql,names,vals);
	}

	/**
	 * Utilizado casi exclusivamente para la migracion de la version anterirod de siiapawin
	 * 
	 */
	public AnalisisDeEntrada localizarEntrada(final Integer sucursal,final Long numero,final String articulo,final Integer renglon){
		List<AnalisisDeEntrada> entradas=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final String hql="select ae from AnalisisDeEntrada ae left join ae.com e"  +						
						" where e.ALMARTIC=:articulo" +
						"   and e.ALMSUCUR=:almsucur" +
						"   and e.ALMNUMER=:numero" +
						"   and e.ALMTIPO=:tipo " +
						"   and e.ALMRENGL=:rengl";
				return session.createQuery(hql)
					.setString("articulo",articulo)
					.setInteger("almsucur",sucursal)
					.setLong("numero",numero)
					.setInteger("rengl",renglon)
					.setString("tipo","COM")
					.list();					
			}			
		});
		if(entradas.isEmpty()) return null;
		return entradas.get(0);
	}
	
	
	public void salvarAnalisis(final List<AnalisisDeEntrada> analisis){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int buf=0;
				for(AnalisisDeEntrada ae:analisis){
					session.save(ae);
					if(++buf%20==0){
						session.clear();
						session.flush();
					}
				}
				return null;
			}			
		});
	}

	public AnalisisDeEntrada buscarAnalisisPorEntrada(final Entrada e) {
		return (AnalisisDeEntrada)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(AnalisisDeEntrada.class)
				.add(Restrictions.eq("com",e))
				.setMaxResults(1)
				.uniqueResult();
				
			}
			
		});
	}
	
	public List<AnalisisDeEntrada> buscarEntradasPorAnalizar(final String articulo,final Date hasta){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from AnalisisDeEntrada a where a.clave=:clave and a.FENT<=:fecha ";
				return session.createQuery(hql)
				.setString("clave", articulo)
				.setParameter("fecha", hasta)
				.list();
			}
			
		});
		
	}
	
	public static void main(String[] args) {
		List l=ServiceLocator.getAnalisisDeEntradaDao().buscarEntradasPorAnalizar("SBS19012",DateUtils.obtenerFecha("15/01/2007"));
		System.out.println(l.size());
	}

}
