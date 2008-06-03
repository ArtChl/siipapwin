package com.luxsoft.siipap.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GenericHibernateDao<T,ID extends Serializable> extends HibernateDaoSupport
								implements GenericDao<T,ID>{
	
		
	private Class<T> persistentClass;
	
	public GenericHibernateDao(Class<T> persistentClass){
		this.persistentClass=persistentClass;
	}
	
	public T findById(Serializable id) {
		return findById(id,false);
	}

	@SuppressWarnings("unchecked")
	public T findById(Serializable id, boolean lock) {
		T entity;
		if(lock)
			entity=(T)getHibernateTemplate().get(getPersistentClass(),id,LockMode.UPGRADE);
		else
			entity=(T)getHibernateTemplate().get(getPersistentClass(),id);
		return entity;
	}
	
	public List<T> findAll() {
		return findByCriteria();
	}

	public List<T> findByExample(Object exampleInstance) {
		return findByCriteria(Example.create(exampleInstance));
	}

	@SuppressWarnings("unchecked")
	public T create(Object entity) {
		getHibernateTemplate().saveOrUpdate(entity);
		return (T) entity;
	}
	
	public void delete(Object entity) {
		getHibernateTemplate().delete(entity);
		
	}
	
	@SuppressWarnings("unchecked")
	public T update(Object entity) {
		return create(entity);
	}

	/*
	 * Use this inside subclasses as a convenience method.
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(final Criterion... criterion){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			@SuppressWarnings("unused")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit=session.createCriteria(getPersistentClass());
				for(Criterion c: criterion){
					crit.add(c);
				}
				return crit.list();
			}
			
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public List<T> findByQuery(String hql) {
		return getHibernateTemplate().find(hql);
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public void initProxy(final Object proxy) {		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException {
				session.lock(proxy,LockMode.NONE);
				Hibernate.initialize(proxy);
				return null;
			}
		});
		
	}
	

	public void bulkUpdate(final List beans) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException {
				int saved=0;
				for(Object m:beans){					
					session.saveOrUpdate(m);					
					saved++;
					if(saved%20==0){						
						session.flush();
						session.clear();
						
					}
				}
				return new Integer(saved);
			}			
		});
		
	}
	
	

}
