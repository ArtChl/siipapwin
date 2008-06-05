package com.luxsoft.siipap.em.replica.qro;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.services.ServiceLocator;

public class Parche1 extends HibernateDaoSupport{
	
	public void verificarArticulos(){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createCriteria(Articulo.class).scroll();
				while(rs.next()){
					Articulo a=(Articulo)rs.get()[0];
					System.out.println("Verificando articulo: "+a);
					//a.getLinea().toString();
					//a.getMarca().toString();
					//a.getClase().toString();
					//a.getUnidad().toString();
				}
				return null;
			}
			
		});
	}
	
	public static void main(String[] args) {
		Parche1 p=new Parche1();
		p.setSessionFactory(ServiceLocator.getSessionFactory());
		p.verificarArticulos();
		
	}

}
