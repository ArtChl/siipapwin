package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class ProvisionDaoImpl extends HibernateDaoSupport implements ProvisionDao{

	public void actualizar(Provision p) {		
		salvar(p);
	}
	
	public Provision buscar(final Venta v) {
		List<Provision> l=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session
				.createQuery("from Provision p where p.venta=?")
				.setEntity(0, v)
				.setMaxResults(1)
				.list();
				
			}
			
		});
		return l.isEmpty()?null:l.get(0);
	}

	public Provision buscarPorId(Long id) {
		return (Provision)getHibernateTemplate().get(Provision.class, id);
	}

	public void eliminar(Provision p) {
		getHibernateTemplate().delete(p);
		
	}

	public void salvar(final Provision p) {
		//p.calcularProvision();
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(p, LockMode.NONE);
				p.calcularProvision();
				session.saveOrUpdate(p);
				return null;
			}
			
		});
		
	}

	public List<Provision> buscarProvisiones() {
		return getHibernateTemplate().find("from Provision p left join fetch p.venta where  p.aplicado=?",new Object[]{Boolean.FALSE});
	}
	
	

}
