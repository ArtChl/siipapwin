/*
 * Created on 21-abr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Familia;

/**
 * Implementacion FamiliaDao para el mantenimiento
 * basico CURD de la clase Familia
 * 
 * @author Ruben Cancino 
 */
@SuppressWarnings("unchecked")
public class FamiliaDaoImpl extends HibernateDaoSupport implements FamiliaDao
{
	
	public Familia crearFamilia(Familia familia) {
		familia.setCreado(new Date());
		familia.setModificado(new Date());
		getHibernateTemplate().saveOrUpdate(familia);
		return familia;
	}

	public Familia actualizarFamilia(Familia familia) {
		familia.setModificado(new Date());
		getHibernateTemplate().update(familia);
		return familia;
	}
	
	public Familia buscarFamilia(Long id) {
		Familia f=null;
		Object o=getHibernateTemplate().get(Familia.class,id);
		if(o!=null)f=(Familia)o;
		return f;
	}
	
	public List buscarTodasFamilias() {
		final String hql="from Familia";
		return getHibernateTemplate().find(hql);		
	}
	
	public void eliminarFamilia(Familia familia) {
		getHibernateTemplate().delete(familia);
	}
	
	
	public Familia buscarFamilia(final String clave) {
		final String hql="from Familia f where f.clave=:clave";
		Object o=getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
					.setString("clave",clave)
					.uniqueResult();
			}
			
		});
		if(o!=null)
			return (Familia)o;
		return null;
	}
	
	
	public int contarFamilias() {
		return ((Integer)getHibernateTemplate()
				.find("select count(*) from Familia")
				.get(0)).intValue();
		
	}
	
	public void crearFamilias(final List familias) {
		int salvados=0;
		Iterator iter=familias.iterator();
		while(iter.hasNext()){
			Familia f=(Familia)iter.next();
			crearFamilia(f);
			salvados++;
			logger.debug("Se salvo la familia: "+f.getClave());
		}
		//return salvados;

	}
	

	public List<Familia> buscarRangoDeFamilias(final Familia f1,final Familia f2){
		final String hql="from Familia f ";
		List<Familia> familias=getHibernateTemplate().find(hql);
		
		Collection<Familia> list=CollectionUtils.select(familias,new Predicate(){

			public boolean evaluate(Object object) {
				Familia f=(Familia) object;
				if(f.compareTo(f1)>=0)
					if(f.compareTo(f2)<=0)
						return true;
				return false;
			}
				
			
		});
		return new ArrayList(list);
	}
	
	
}
