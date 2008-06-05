package com.luxsoft.siipap.em.replica.service;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.services.ServiceLocator;

public class ServiceTest extends HibernateDaoSupport{
	
	
	public JdbcTemplate getJdbcTemplate(){
		return ServiceLocator.getJdbcTemplate();
	}
	
	public void execute(){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createCriteria(Cliente.class)
				.scroll();
				while(rs.next()){
					Cliente c=(Cliente)rs.get()[0];
					System.out.println("Cliente: "+c.getClave());
				}
				return null;
			}
			
		});
	}
	
	
	public static void main(String[] args) {
		ServiceTest test=new ServiceTest();
		test.setSessionFactory(ServiceLocator.getSessionFactory());
		test.execute();
		
	}

}
