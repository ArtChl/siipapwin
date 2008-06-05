package com.luxsoft.siipap.em.replica.legacy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.replica.ReplicationUtils;

@SuppressWarnings("unchecked")
public class BasicReplicator extends HibernateDaoSupport{
	
	private SiipapJdbcTemplateFactory factory;
	private String tabla;
	private Class clazz;
	
	public BasicReplicator() {		
	}
	
	public int bulkImport(Periodo periodo){
		int importados=0;
		for(Periodo mes:Periodo.periodosMensuales(periodo)){
			importados+=bulkImportarMes(mes);
			
		}
		return importados;
	}
	
	
	
	public int bulkImportarMes(Periodo mes){
		logger.debug("Importando registros "+getTabla()+" para: "+mes);
		
		
		int importados=0;
		String sql="select * from "+ReplicationUtils.resolveTable(getTabla(), mes.getFechaFinal());
		//System.out.println(sql);
		List<Map> registros=getFactory().getJdbcTemplate(mes).queryForList(sql);
		List beans=ReplicationUtils.transformarRegistros(registros, getClazz());
		for(Object c:beans){
			BeanWrapperImpl wrapper=new BeanWrapperImpl(c);
			wrapper.setPropertyValue("MES",Periodo.obtenerMes(mes.getFechaFinal())+1);
			wrapper.setPropertyValue("YEAR",Periodo.obtenerYear(mes.getFechaFinal()));
		}
		logger.debug("Registros a importar : "+registros.size());
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

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

	public String getTabla() {
		return tabla;
	}

	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	
	

}
