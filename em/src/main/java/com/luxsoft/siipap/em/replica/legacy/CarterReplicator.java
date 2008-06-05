package com.luxsoft.siipap.em.replica.legacy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.clipper.domain.Carter;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.managers.EMServiceLocator;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.services.ServiceLocator;
/**
 * 
 * @author RUBEN
 *@deprecated En favor de BasicReplicator
 */
@SuppressWarnings("unchecked")
public class CarterReplicator extends HibernateDaoSupport{
	
	private SiipapJdbcTemplateFactory factory;
	
	public int bulkImport(Periodo periodo){
		return 0;
	}
	
	
	public int bulkImportarMes(){
		logger.debug("Importando registros Carter para: ");
				
		int importados=0;
		String sql="select * from CARTER";//+ReplicationUtils.resolveTable("CARTER", mes.getFechaFinal());
		System.out.println(sql);
		List<Map> registros=getFactory().getJdbcTemplate().queryForList(sql);
		List<Carter> beans=ReplicationUtils.transformarRegistros(registros, Carter.class);
		for(Carter c:beans){
			c.setMES(6);
			c.setYEAR(2007);
		}
		logger.debug("Registros a importar de Carter: "+registros.size());
		logger.debug("Beans creados: "+beans.size());
		importados=bulkSave(beans);
		logger.debug("Beans Importados: "+beans.size());
		return importados;
	}
	
	public int bulkSave(final Collection beans){		
		return (Integer)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int buff=0;
				int rows=0;
				for(Object d:beans){					
					session.saveOrUpdate(d);
					rows++;
					if(++buff%20==0){						
						session.flush();
						session.clear();
					}					
				}
				return rows;
			}
			
		});
	}
	
	
	
	
	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}
	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}


	public static void main(String[] args) {
		CarterReplicator r=new CarterReplicator();
		r.setFactory(EMServiceLocator.instance().getSiipapTemplateFactory());
		r.setSessionFactory(ServiceLocator.getSessionFactory());
		r.bulkImportarMes();
	}

}
