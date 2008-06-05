package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


import com.luxsoft.siipap.cxc.domain.Vendedor;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Parche para importar los vendedores
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class VendedoresParche extends HibernateDaoSupport{
	
	public VendedoresParche(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
		
	public void execute(){
		String sql="select * from VENDEDOR";		 
		final List<Map<String, Object>> rows=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(new Date()).queryForList(sql);		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				for(Map row:rows){
					//{VENCLAVE=44.0, VENNOMBR=VICTOR GUTIERREZ, VENCOMIS=1.0, VENFECCOMI=null, VENDIREC=null, VENCP=null, VENTELEFON=null, VENDELEG=null, VENUSUAR=null, VENFECBACA=null, VENFECALTA=2003-11-28, VENCOMCO=1.0, VENFECOCO=null, VENEQUIORG=null, VENHORAREG=null, VENACCES=null, VENVERSION=null}
					int clave=((Number)row.get("VENCLAVE")).intValue();
					String nom=row.get("VENNOMBR").toString();
					
					final Vendedor c=new Vendedor();
					c.setClave(clave);
					c.setNombre(nom);
					c.setCreado(new Date());
					c.setModificado(new Date());
					session.save(c);
					
					System.out.println(row);
				}
				return null;
			}
						
		});		
	}
	
	
	
	public static void main(String[] args) {
		new VendedoresParche().execute();
		
	}

}
