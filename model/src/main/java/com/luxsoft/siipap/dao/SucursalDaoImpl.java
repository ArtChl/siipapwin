/*
 * Created on 21-abr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


import com.luxsoft.siipap.domain.Sucursal;

/**
 * Implentacion de SucursalDao
 * 
 * @author Ruben Cancino
 * 
 */
public class SucursalDaoImpl extends HibernateDaoSupport implements SucursalDao{
	
	

	public Sucursal actualizarSucursal(Sucursal sucursal) {
		getHibernateTemplate().update(sucursal);
		return sucursal;
	}
	public Sucursal buscarSucursal(final int clave) {
		final String hql="from Sucursal s where s.clave=:clave";
		Object o=getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
					.setInteger("clave",clave)
					.uniqueResult();
			}			
		});
		if(o!=null)return (Sucursal)o;
		return null;
	}
	
	public Sucursal crearSucursal(Sucursal sucursal) {
		getHibernateTemplate().save(sucursal);
		return sucursal;
	}
	
	public void eliminaSucursal(Sucursal sucursal) {
		getHibernateTemplate().delete(sucursal);

	}
	
	
	public int contarSucursales() {
		return ((Integer)getHibernateTemplate()
		.find("select count(*) from Sucursal")
		.get(0)).intValue();
	}
	
	public List buscarSucursales() {
		return getHibernateTemplate().find("from Sucursal");
	}
}
