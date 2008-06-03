package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.services.ServiceLocator;

@SuppressWarnings("unchecked")
public class ClienteDaoImpl extends HibernateDaoSupport implements ClienteDao{

	public Cliente buscar(Long id) {		
		return (Cliente)getHibernateTemplate().get(Cliente.class, id);
	}

	
	public Cliente buscarPorClave(String clave) {
		String hql="from Cliente c " +
				" left join fetch c.credito cc" +
				"  where c.clave=?" ;
				
		List<Cliente> clientes=getHibernateTemplate().find(hql,clave);
		return clientes.isEmpty()?null:clientes.get(0);
		
	}

	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void salvar(Cliente c) {
		if(c.getCredito()!=null)
			c.getCredito().updateProperties();
		getHibernateTemplate().saveOrUpdate(c);
	}


	public List<Cliente> buscarClientesPorClave(final String clave) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Cliente.class)
				.add(Restrictions.like("clave", clave, MatchMode.START).ignoreCase())
				.setMaxResults(500)
				.list();
				
			}
			
		});
	}


	public List<Cliente> buscarPorNombre(final String nombre) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Cliente.class)
				.add(Restrictions.like("nombre", nombre, MatchMode.START).ignoreCase())
				.setMaxResults(500)
				.list();
				
			}
			
		});
	}


	public List<ClienteCredito> buscarClientesDeCredito() {
		return getHibernateTemplate().find("from ClienteCredito c left join fetch c.cliente order by c.clave");
	}
	
	public void refrescar(final ClienteCredito c){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.update(c);
				Hibernate.initialize(c.getFechasPago());
				Hibernate.initialize(c.getFechasRevision());
				return null;
			}			
		});
	}


	@Transactional(propagation=Propagation.REQUIRED)
	public void generarClienteCredito(String clave) {
		Cliente c=buscarPorClave(clave);
		c.generarCredito();
		getHibernateTemplate().saveOrUpdate(c);
		
	}

/*
	public List<Cliente> buscarClientesCredito() {
		return getHibernateTemplate().find("from Cliente c left join fetch c.credito where c.credito is not null");
	}
	
	*/
	
	//public List<ClienteCredito> busacarCliente
	
	public static void main(String[] args) {
		ServiceLocator.getClienteDao().generarClienteCredito("S010461");
	}

}
