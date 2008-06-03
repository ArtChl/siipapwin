package com.luxsoft.siipap.services;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.Pago;

public class ParcheA1 extends HibernateDaoSupport{
	
	public void exec(){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults sc=session.createQuery("from Pago p where p.pagoM is not null").scroll();
				while(sc.next()){
					Pago p=(Pago)sc.get()[0];
					FormaDePago f=p.getPagoM().getFormaDePago();					
					p.setDescFormaDePago(f.getDesc());
					System.out.println("Asignando forma de pago: "+f.getDesc()+" Anterior era: "+p.getFormaDePago());
				}
				return null;
			}
			
		});
	}
	
	
	public static void main(String[] args) {
		ParcheA1 p=new ParcheA1();
		p.setSessionFactory(ServiceLocator.getSessionFactory());
		p.exec();
	}

}
