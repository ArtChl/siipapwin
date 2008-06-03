package com.luxsoft.siipap.maquila.dao;


import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.dao.GenericHibernateDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.maquila.domain.Bobina;

@SuppressWarnings("unchecked")
public class BobinaDaoImpl extends GenericHibernateDao<Bobina,Long>	
							implements BobinaDao{

	public BobinaDaoImpl() {
		super(Bobina.class);
	}
	
	public Bobina buscarPorClave(String clave){
		return buscarPorClave(clave,true);
	}	
	public Bobina buscarPorClave(String clave,boolean fetchUnidades){
		return buscarPorClave(clave,true,true);
	}
	
	public Bobina buscarPorClave(final String clave,final boolean fetchUnidades,final boolean fetchFamilia){
		Bobina b=null;
		List l=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session s) throws HibernateException, SQLException {
				Criteria c=s.createCriteria(Bobina.class)
				.add(Expression.eq("clave",clave));
				if(fetchUnidades)
					c.setFetchMode("unidades",FetchMode.JOIN);
				if(fetchFamilia)
					c.setFetchMode("familia",FetchMode.JOIN);
				return c.setMaxResults(1).list();
			}
			
		});
		if(l.size()==1)
			b=(Bobina)l.get(0);
		return b;
	}
	
	public List<Articulo> buscarSulfatada(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				/*
				return session.createCriteria(Articulo.class)
				.add(Restrictions.or(
						Restrictions.like("clave","SBS%")
						,Restrictions.or(Restrictions.like("clave","TYV%"),Restrictions.like("clave","TVK%"))
						)
					)
				//.add(Restrictions.like("clave","SBS%")).add(Restrictions.or)
				.addOrder(Order.asc("clave"))
				.list();
				*/
				return session.createCriteria(Articulo.class)
					.add(Restrictions.or(
						Restrictions.like("clave","SBS%")
						,Restrictions.or(Restrictions.like("clave","TYV%"),Restrictions.like("clave","TVK%"))
						)
					).add(Restrictions.like("clave", "PBB%"))
				//.add(Restrictions.like("clave","SBS%")).add(Restrictions.or)
				.addOrder(Order.asc("clave"))
				.list();
			}
			
		});
	}
	
	
	
	
}
