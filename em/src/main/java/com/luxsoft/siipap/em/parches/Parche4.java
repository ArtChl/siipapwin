package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.VentaACredito;

/**
 * Parche para actualizar los descuentos pactados en {@link VentaACredito}
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche4 extends HibernateDaoSupport{
	
	public Parche4(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void execute(){
		String sql="select * from VENTASP1";		 
		final List<Map<String, Object>> rows=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(new Date()).queryForList(sql);		
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				int count=0;
				for(Map<String, Object> row:rows){
					Long id=((Number)row.get("ID")).longValue();
					double desc=((Number)row.get("DESCTO")).doubleValue();
					VentaACredito v=(VentaACredito)session.get(VentaACredito.class, id);
					v.setDescuento(desc);
					System.out.println("Venta actualizada: "+id);
					
				count++;
				}
				System.out.println(count);
				
				
				return null;
			}
			
		});		
	}
	
	public static void main(String[] args) {
		new Parche4().execute();
	}

}
