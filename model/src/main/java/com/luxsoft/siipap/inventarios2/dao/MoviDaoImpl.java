package com.luxsoft.siipap.inventarios2.dao;

import java.util.Date;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.inventarios2.domain.MoviDoc;

/**
 * Implementacion de MoviDao
 * 
 * @author Ruben Cancino
 *
 */
public class MoviDaoImpl extends HibernateDaoSupport implements MoviDao{

	
	public MoviDoc buscarById(Long id) {
		return (MoviDoc)getHibernateTemplate().get(MoviDoc.class, id);
	}

	public void eliminar(MoviDoc mov) {
		getHibernateTemplate().delete(mov);
	}

	public void salvar(MoviDoc mov) {
		mov.setModificado(new Date());
		getHibernateTemplate().saveOrUpdate(mov);
		
	}

}
