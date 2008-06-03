package com.luxsoft.siipap.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Clase;

public class ClaseDaoImpl extends HibernateDaoSupport implements ClaseDao{

	public void actualizar(Clase c) {
		salvar(c);
	}

	public Clase buscar(Long id) {
		return (Clase)getHibernateTemplate().get(Clase.class, id);
	}

	public void eliminar(Clase c) {
		getHibernateTemplate().delete(c);
	}

	public void salvar(Clase c) {
		getHibernateTemplate().saveOrUpdate(c);		
	}

	public Clase buscarPorNombre(String nombre) {
		List l=getHibernateTemplate().find("from Clase c where c.nombre=?", nombre);
		if(l.isEmpty())
			return null;		
		return (Clase)l.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Clase> buscarTodas() {
		return getHibernateTemplate().find("from Clase");
	}

}
