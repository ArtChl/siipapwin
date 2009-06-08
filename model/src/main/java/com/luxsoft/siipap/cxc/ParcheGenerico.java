package com.luxsoft.siipap.cxc;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;

public class ParcheGenerico implements HibernateCallback{
	
	public void execute(){
		ServiceLocator.getDaoSupport().getHibernateTemplate().execute(this);
	}

	public Object doInHibernate(Session session) throws HibernateException,SQLException {
		String hql="from Venta v where v.year>2007 and v.serie=\'E\' and v.saldo>0";
		ScrollableResults rs=session.createQuery(hql).scroll();
		int buff=0;
		while(rs.next()){
			Venta v=(Venta)rs.get()[0];
			System.out.println("Por arreglar: "+v.getId()+" Plazo actual: "+v.getCredito().getPlazo()+" Row: "+rs.getRowNumber());
			int plazo=v.getCliente().getCredito().getPlazo();
			v.getCredito().setPlazo(plazo);
			System.out.println("Nuevo plazo: "+v.getCliente().getCredito().getPlazo());
			if(buff++%20==0){
				session.flush();
				session.clear();
			}
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		new ParcheGenerico().execute();
	}

}
