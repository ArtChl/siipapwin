package com.luxsoft.siipap.inventarios2.dao;

import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.inventarios2.domain.Concepto;

/**
 * Implementacion de ConceptoDao
 * 
 * @author Ruben Cancino
 *
 */
public class ConceptoDaoImpl extends HibernateDaoSupport implements ConceptoDao{

	public Concepto buscarById(Long id) {
		return (Concepto)getHibernateTemplate().get(Concepto.class, id);
	}

	public void delete(Concepto c) {
		getHibernateTemplate().delete(c);
		
	}

	public void salvar(Concepto c) {
		c.setModificado(new Date());
		getHibernateTemplate().saveOrUpdate(c);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Concepto> buscarManuales(){
		return getHibernateTemplate().find("from Concepto c where c.automatico=?",Boolean.FALSE);
	}

}
