package com.luxsoft.siipap.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T,ID extends Serializable> {
	
	T findById(ID id);
	
	T findById(ID id,boolean lock);
	
	List<T> findAll();
	
	List<T> findByExample(T exampleInstance);
	
	List<T> findByQuery(String hql);
	
	T create(T entity);
	
	void delete(T entity);
	
	T update(T entity);
	
	void initProxy(Object proxy);
	
	public void bulkUpdate(final List beans);

}
