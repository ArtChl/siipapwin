package com.luxsoft.siipap.inventarios.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.cxp.domain.AnalisisDet;
import com.luxsoft.siipap.dao.GenericHibernateDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.CostoPromedio;

@SuppressWarnings("unchecked")
public class CostoPromedioDaoImpl extends GenericHibernateDao<CostoPromedio,Long>
	implements CostoPromedioDao{

	public CostoPromedioDaoImpl() {
		super(CostoPromedio.class);
	}
	
	/**
	 * Busca el costo promedio para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoPromedio buscarCostoPromedio(final String articulo,final Periodo periodo) {
		String pp=CostoPromedio.format(periodo);
		return buscarCostoPromedio(articulo,pp);
	}

	/**
	 * Busca el costo promedio para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoPromedio buscarCostoPromedio(final String articulo,final String periodo) {
		Object costo=getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				return sess.createCriteria(CostoPromedio.class)
					.add(Restrictions.eq("periodo",periodo))
					.createAlias("articulo","a")
					.add(Restrictions.eq("a.clave",articulo))
					.setMaxResults(1)
					.uniqueResult();
			}
		});
		return (CostoPromedio)costo;
	}
	
	
	public List<CostoPromedio> buscarCostosPromedio(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CostoPromedio.class)
				.createAlias("articulo","a")
				.createAlias("a.unidad","au")
				.createAlias("a.familia","af")
				.setFetchMode("articulo",FetchMode.JOIN)
				.setFetchMode("au",FetchMode.JOIN)
				.setFetchMode("af",FetchMode.JOIN)
				//.setMaxResults(1000)
				.list();
				
			}
			
		});
	}
	
	public CostoPromedio buscarCostoMasProximo(final Articulo articulo,final Date antesDe){
		return (CostoPromedio)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="select cp from CostoPromedio cp left join fetch cp.articulo where cp.articulo=:articulo and cp.costo.amount>:costo ";
				List<CostoPromedio> costos=session.createQuery(hql)
				.setParameter("articulo",articulo,Hibernate.entity(Articulo.class))
				.setParameter("costo",BigDecimal.ZERO)
				.list();
				
				if(costos.isEmpty())
					return null;
				Comparator c=new Comparator<CostoPromedio>(){
					
					

					public int compare(CostoPromedio o1, CostoPromedio o2) {
						/*
						String p1=o1.getPeriodo();
						String p2=o2.getPeriodo();
						try {
							Date d1=df.parse(p1);
							Date d2=df.parse(p2);
							val=d1.compareTo(d2); 
						} catch (Exception e) {
						}
						*/
						if(o2.periodo().getFechaInicial().after(antesDe) || o2.periodo().getFechaInicial().equals(antesDe))
							return -1;
						int val=o1.periodo().getFechaFinal().compareTo(o2.periodo().getFechaFinal());
						//System.out.println("P1: "+o1.periodo().toString()+ "P2: "+o2.periodo().toString()+" Res :"+val);
						
						return val*(-1);
					
						//return (-1)*o1.periodo().compareTo(o2.periodo());
					}
					
				};
				//System.out.println("Inicio de periodo: "+antesDe);
				Collections.sort(costos,c);
				for(CostoPromedio ccc:costos){
					//System.out.println(ccc);
				}
				
				
				return costos.get(0);
			}
			
		});
	}
	
	/**
	 * Busca el costo promedio mas reciente
	 * 
	 * @param articulo
	 * @return
	 */
	public CostoPromedio buscarCostoMasReciente(final Articulo articulo){
		List<CostoPromedio> costos=getHibernateTemplate().find("from CostoPromedio cp left join fetch cp.articulo a" +
				" where cp.articulo.clave=? order by cp.year desc,cp.mes desc", articulo.getClave());
		if(costos.size()>0){			
			return costos.get(0);
		}else
			return null;
			
	}
	
	/**
	 * Busca en las tablas de CXP el costo mas reciente para el articulo
	 * 
	 * @param articulo
	 * @return
	 */
	public CantidadMonetaria buscarUltimoCosto(final Articulo articulo){
		return (CantidadMonetaria)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				AnalisisDet det=(AnalisisDet)session.createQuery("from AnalisisDet d where d.ARTCLAVE=:clave order by d.analisis.fecha desc")
				.setParameter("clave", articulo.getClave())
				.setMaxResults(1)
				.uniqueResult();
				if(det!=null)
					return det.getNetoMN();
				return CantidadMonetaria.pesos(0);
			}
			
		});
	}


}
