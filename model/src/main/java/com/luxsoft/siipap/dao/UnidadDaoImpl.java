package com.luxsoft.siipap.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Unidad;

/**
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class UnidadDaoImpl extends GenericHibernateDao<Unidad,Long>
							implements UnidadDao{

	public UnidadDaoImpl() {
		super(Unidad.class);
	}

	
	public Unidad findByClave(String clave) {
		final String hql="from Unidad u where u.clave=:clave";
		List<Unidad> l=getHibernateTemplate().findByNamedParam(hql,"clave",clave);
		if(l.size()==1)
			return l.get(0);
		return null;
	}
	
	

}
