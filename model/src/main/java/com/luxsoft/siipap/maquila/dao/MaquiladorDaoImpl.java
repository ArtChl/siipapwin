package com.luxsoft.siipap.maquila.dao;


import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.dao.GenericHibernateDao;
import com.luxsoft.siipap.maquila.domain.Almacen;
import com.luxsoft.siipap.maquila.domain.Maquilador;

@SuppressWarnings("unchecked")
public class MaquiladorDaoImpl extends GenericHibernateDao<Maquilador,Long>	
							implements MaquiladorDao{

	public MaquiladorDaoImpl() {
		super(Maquilador.class);
	}
	
	
	public Maquilador buscarPorClave(final String clave){
		Maquilador b=null;
		List l=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session s) throws HibernateException, SQLException {
				Criteria c=s.createCriteria(Maquilador.class)
				.add(Expression.eq("clave",clave));
				return c.setMaxResults(1).list();
			}
			
		});
		if(l.size()==1)
			b=(Maquilador)l.get(0);
		return b;
	}
	
	public List<Almacen> buscarAlmacenes(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Almacen.class)
						.setFetchMode("maquilador",FetchMode.JOIN)
						.list();
			}			
		});
	}
	
	public Almacen buscarAlmacen(final String nombre){
		return (Almacen)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Almacen.class)
					.add(Restrictions.eq("nombre",nombre))
					.setMaxResults(1)
					.uniqueResult();
			}			
		});
	}
	
	public List<Maquilador> buscarMaquiladores(){
		String hql="from Maquilador";
		return getHibernateTemplate().find(hql);
	}
	
	
}
