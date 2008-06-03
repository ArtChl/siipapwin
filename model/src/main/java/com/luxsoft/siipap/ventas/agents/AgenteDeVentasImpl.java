package com.luxsoft.siipap.ventas.agents;

import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

public class AgenteDeVentasImpl extends HibernateDaoSupport implements AgenteDeVentas{
	
	private VentasDao ventasDao;
	
	public void revisarVentas() {
		revisarVentas(new Date());
		
	}

	
	public void revisarVentas(final Date dia) {
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults sc=session.createQuery("from Venta v where v.fecha=?")
				.setParameter(0, dia, Hibernate.DATE)
				.scroll();
				int buff=0;
				while(sc.next()){
					Venta v=(Venta)sc.get()[0];
					if(logger.isDebugEnabled()){
						logger.debug("Procesando venta: "+v);
					}
					if(++buff%20==0){
						session.flush();
						session.clear();
					}
				}
				return null;
			}
			
		});
	}

	public VentasDao getVentasDao() {
		return ventasDao;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}

	
	
	
	

}
