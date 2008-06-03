package com.luxsoft.siipap.em.replica.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.em.replica.domain.ReplicaLog;



public class ReplicaLogDaoImpl extends HibernateDaoSupport implements ReplicaLogDao{

	public void borrar(ReplicaLog log) {
		getHibernateTemplate().delete(log);
		
	}

	public void salvar(ReplicaLog log) {
		getHibernateTemplate().saveOrUpdate(log);
		
	}

	public ReplicaLog buscar(Long id) {
		return (ReplicaLog)getHibernateTemplate().get(ReplicaLog.class, id);
	}
	
	

	public ReplicaLog buscar(final String entity, final String tabla, final int year,final int mes) {
		return (ReplicaLog)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from ReplicaLog r where r.entity=:entity and r.tabla=:tabla and r.year=:year and r.month=:mes";
				return (ReplicaLog)session.createQuery(hql)
				.setString("entity",entity)
				.setString("tabla",tabla)
				.setParameter("year",year)
				.setParameter("mes",mes)
				.setMaxResults(1)
				.uniqueResult();
			}			
		});
		
	}

	@SuppressWarnings("unchecked")
	public List<ReplicaLog> buscar(final String entity) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from ReplicaLog r where r.entity=:entity and r.tabla=:tabla and r.year=:year and r.month=:mes";
				return (ReplicaLog)session.createQuery(hql)
				.setString("entity",entity)				
				.setMaxResults(1)
				.list();
			}			
		});
	}

	
	
	
	

}
