package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.DescuentoEspecial;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class DescuentoEspecialDaoImpl extends HibernateDaoSupport implements DescuentoEspecialDao{

	public void actualisar(DescuentoEspecial d) {
		salvar(d);
	}

	public DescuentoEspecial buscar(final Venta v) {
		return (DescuentoEspecial)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {				
				DescuentoEspecial d=(DescuentoEspecial) session.createQuery("from DescuentoEspecial d "+
						" where d.venta=?")
				.setEntity(0, v)
				.setMaxResults(1)
				.uniqueResult();
				if(d!=null){
					d.getVenta();
					d.getVenta().getNumero();
					d.getCliente();
					d.getCliente().getCliente();
				}
				
				return d;
			}			
		});
	}

	public DescuentoEspecial buscarPorId(Long id) {
		return (DescuentoEspecial)getHibernateTemplate().get(DescuentoEspecial.class, id);
	}

	public void eliminar(DescuentoEspecial d) {
		getHibernateTemplate().delete(d);		
	}

	public void salvar(final DescuentoEspecial d) {
		/*
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.update(d.getVenta());
				session.save(d);
				d.getVenta().getCredito().setDescuento(d.getDescuento());
				return null;
			}
			
		});
		*/
		getHibernateTemplate().saveOrUpdate(d);
		
	}

	
	public List<DescuentoEspecial> buscar() {
		return getHibernateTemplate().find("from DescuentoEspecial d" +
				" left join fetch d.venta" +
				" left join fetch d.cliente");
	}

}
