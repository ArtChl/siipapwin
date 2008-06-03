package com.luxsoft.siipap.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Linea;

public class LineaDaoImpl extends HibernateDaoSupport implements LineaDao{

	

	public Linea buscar(Long id) {
		return (Linea)getHibernateTemplate().get(Linea.class, id);
	}

	public void eliminar(Linea l) {
		getHibernateTemplate().delete(l);		
	}

	public void salvar(Linea l) {
		getHibernateTemplate().saveOrUpdate(l);		
	}
	
	public void actualizar(Linea l) {
		salvar(l);
	}

	public Linea buscarPorNombre(String nombre) {
		List l=getHibernateTemplate().find("from Linea l where l.nombre=?", nombre);
		if(l.isEmpty())
			return null;
		return (Linea)l.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Linea> buscarTodas() {
		return getHibernateTemplate().find("from Linea");
	}

}
