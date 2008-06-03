package com.luxsoft.siipap.compras.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.luxsoft.siipap.compras.domain.Compra;
import com.luxsoft.siipap.compras.domain.CompraUnitaria;
import com.luxsoft.siipap.domain.Articulo;

@SuppressWarnings("unchecked")
public class CompraDaoImpl extends HibernateDaoSupport implements CompraDao{

	public void salvar(final  Compra c) {
		getHibernateTemplate().saveOrUpdate(c);
		
	}

	public void eliminar(Compra c) {
		getHibernateTemplate().delete(c);
		
	}

	public void actualizar(Compra c) {
		salvar(c);
		
	}

	public Compra getObject(Long id) {
		try {
			return (Compra)getHibernateTemplate().get(Compra.class,id);
		} catch (Exception e) {
			return null;
		}
		
	}

	
	public List<Compra> buscarPorFecha(final Date fecha) {
		String hql="from Compra c where c.fecha=?";
		return getHibernateTemplate().find(hql,fecha);
	}
	
	public Compra buscarCompra(final Integer sucursal,final Integer numero){
		final String hql="from Compra c " +
				" left join fetch c.partidas " +
				" left join fetch c.proveedor " +
				" where c.sucursal=? and c.numero=?";
		Object[] vals={sucursal,numero};
		List<Compra> l=getHibernateTemplate().find(hql,vals);
		
		Set<Compra> s=new HashSet<Compra>(l);
		Assert.isTrue(s.size()<=1,"No puede haber compras duplicadas elementos: "+s.size());
		return s.isEmpty()?null:s.iterator().next();
	}
	
	public Set<CompraUnitaria> buscarPendientes(final Integer sucursal,final Articulo articulo){
		List l= getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final String hql="from Comuni c " +
						" left join fetch c.articulo" +
						" left join fetch c.compra" +
						" where c.compra.sucursal=:suc" +
						"  and c.articulo=:art" +
						"  and c.pendiente>0" +
						"  and c.estatus is null";
				return session.createQuery(hql)
					.setInteger("suc",sucursal)
					.setEntity("art",articulo)
					.list();
				
			}
			
		});
		return new HashSet<CompraUnitaria>(l);
	}

}
