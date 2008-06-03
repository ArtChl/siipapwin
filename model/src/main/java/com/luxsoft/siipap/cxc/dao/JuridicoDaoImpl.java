package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Juridico;
import com.luxsoft.siipap.ventas.domain.Venta;

public class JuridicoDaoImpl extends HibernateDaoSupport implements JuridicoDao{

	public Juridico buscar(Long id) {
		return (Juridico)getHibernateTemplate().get(Juridico.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Juridico> buscar() {
		return getHibernateTemplate().find("from Juridico");
	}

	public void eliminar(Juridico j) {
		getHibernateTemplate().delete(j);
		
	}

	public void salvar(Juridico j) {
		getHibernateTemplate().saveOrUpdate(j);
		
	}
	
	public Juridico buscar(final Venta v){
		return (Juridico)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				return sess.createQuery("from Juridico j where j.venta=:venta")
				.setEntity("venta", v)
				.uniqueResult();				
			}			
		});
	}

}
