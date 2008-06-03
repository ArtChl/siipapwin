package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.domain.Articulo;

@SuppressWarnings("unchecked")
public class DescuentoPorArticuloDaoImpl extends HibernateDaoSupport implements DescuentoPorArticuloDao{

	public void actualisar(DescuentoPorArticulo d) {
		salvar(d);
	}

	public DescuentoPorArticulo buscar(final Cliente c,final  Articulo articulo) {
		return (DescuentoPorArticulo)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from DescuentoPorArticulo d where d.cliente=? and d.articulo=? and d.activo=? ")
				.setEntity(0, c)
				.setEntity(1, articulo)
				.setBoolean(2, true)
				.setMaxResults(1)				
				.uniqueResult();				
			}
			
		});
	}

	public DescuentoPorArticulo buscarPorId(Long id) {
		return (DescuentoPorArticulo)getHibernateTemplate().get(DescuentoPorArticulo.class, id);
	}

	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void eliminar(DescuentoPorArticulo d) {
		//getHibernateTemplate().find("from ProvisionDet p where p.descArticulo.id=?", d.getId());
		getHibernateTemplate().delete(d);
		
	}

	public void salvar(DescuentoPorArticulo d) {
		getHibernateTemplate().saveOrUpdate(d);
		
	}
	
	
	public List<DescuentoPorArticulo> buscar(final String c){
		return getHibernateTemplate().find("from DescuentoPorArticulo d " +
				" left join fetch d.articulo a " +
				" left join fetch d.articulo.linea l " +
				" where d.cliente.clave=?",new Object[]{c});
		
	}

}
