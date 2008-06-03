package com.luxsoft.siipap.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Marca;

public class MarcaDaoImpl extends HibernateDaoSupport implements MarcaDao{

	

	public Marca buscar(Long id) {
		return (Marca)getHibernateTemplate().get(Marca.class, id);
	}

	public void eliminar(Marca m) {
		getHibernateTemplate().delete(m);
		
	}

	public void salvar(Marca n) {
		getHibernateTemplate().saveOrUpdate(n);
		
	}
	
	public void actualizar(Marca m) {
		salvar(m);		
	}

	public Marca buscarPorNombre(String nombre) {
		List l=getHibernateTemplate().find("from Marca m where m.nombre=?", nombre);
		if(l.isEmpty())
			return null;
		return (Marca)l.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Marca> buscarTodas() {
		return getHibernateTemplate().find("from Marca");
	}

}
