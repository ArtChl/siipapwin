package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;

@SuppressWarnings("unchecked")
public class DescuentoPorClienteDaoImpl extends HibernateDaoSupport implements DescuentoPorClienteDao{

	public void actualisar(DescuentoPorCliente d) {
		salvar(d);
		
	}

	public DescuentoPorCliente buscarPorId(Long id) {
		return (DescuentoPorCliente)getHibernateTemplate().get(DescuentoPorCliente.class, id);
	}

	public void eliminar(DescuentoPorCliente d) {
		getHibernateTemplate().delete(d);
		
	}

	public void salvar(DescuentoPorCliente d) {
		getHibernateTemplate().saveOrUpdate(d);
		
	}

	public void eliminarDescuentosOrigenSiipap() {
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete DescuentoPorCliente d where d.precioNeto=?")
				.setBoolean(0, true)
				.executeUpdate();
				return null;
			}
			
		});
		
	}
	
	public DescuentoPorCliente buscar(final Cliente c){
		return (DescuentoPorCliente)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from DescuentoPorCliente d left join fetch d.cliente c where c=:cliente and d.activo=:activo";
				return session.createQuery(hql)
				.setEntity("cliente", c)
				.setBoolean("activo", true)
				.setMaxResults(1)
				.uniqueResult();				
			}
			
		});
	}

	
	public List<DescuentoPorCliente> buscarDescuentos() {
		return getHibernateTemplate().find("from DescuentoPorCliente d left join fetch d.cliente");
	}
	

}
