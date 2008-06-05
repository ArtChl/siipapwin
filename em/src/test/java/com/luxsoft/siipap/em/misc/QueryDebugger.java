package com.luxsoft.siipap.em.misc;

import org.hibernate.SessionFactory;

import com.luxsoft.siipap.cxp.dao.CXPDaoImpl;
import com.luxsoft.siipap.em.replica.service.ServiceManager;

public abstract class QueryDebugger {
	
	public static void main(String[] args) {
		/**SessionFactory fac=(SessionFactory)ServiceManager.instance().getContext().getBean("sessionFactory");
		CXPDaoImpl dao=new CXPDaoImpl();
		dao.setSessionFactory(fac);
		dao.buscarFacturasPendietnes();
		**/
		double desc1=52;
		double desc2=2;
		
		double d1=(100-desc1);
		desc1=(100-(100-desc1))+((100-desc1)*desc2/100);
		System.out.println(desc1);
	}

}
