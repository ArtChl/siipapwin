package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Cobrador;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Parche para importar los cobradores
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class CobradoresParche extends HibernateDaoSupport{
	
	public CobradoresParche(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
		
	public void execute(){
		String sql="select COBCLAVE,COBNOMBR,COBFECBACA,COBFECALTA from COBRADOR";		 
		final List<Map<String, Object>> rows=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(new Date()).queryForList(sql);		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				for(Map row:rows){
					int clave=((Number)row.get("COBCLAVE")).intValue();
					String nom=row.get("COBNOMBR").toString();
					Date alta=(Date)row.get("COBFECALTA");
					Date baja=(Date)row.get("COBFECBACA");
					final Cobrador c=new Cobrador();
					c.setClave(clave);
					c.setNombre(nom);
					c.setAlta(alta);
					c.setBaja(baja);
					session.save(c);
				}
				return null;
			}
						
		});		
	}
	
	
	
	public static void main(String[] args) {
		new CobradoresParche().execute();
		
	}

}
