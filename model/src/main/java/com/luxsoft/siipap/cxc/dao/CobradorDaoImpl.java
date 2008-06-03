package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Cobrador;

public class CobradorDaoImpl extends HibernateDaoSupport implements CobradorDao{

	public void actualizar(Cobrador c) {
		getHibernateTemplate().update(c);
	}

	@SuppressWarnings("unchecked")
	public List<Cobrador> buscar() {
		return getHibernateTemplate().loadAll(Cobrador.class);
	}

	public Cobrador buscar(Long id) {		
		return (Cobrador)getHibernateTemplate().get(Cobrador.class, id);
	}

	public void eliminar(Cobrador c) {
		getHibernateTemplate().delete(c);
	}

	public void salvar(Cobrador c) {
		getHibernateTemplate().merge(c);
		
	}

}
