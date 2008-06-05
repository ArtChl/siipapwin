package com.luxsoft.siipap.em.replica;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.replica.dao.ReplicaLogDao;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;

@SuppressWarnings("unchecked")
public abstract class AbstractReplicatorSupport implements Replicador{

	protected SiipapJdbcTemplateFactory factory;
	private JdbcTemplate targetJdbcTemplate;	
	private ReplicaLogDao replicaLogDao;
	protected Logger logger = Logger.getLogger(getClass());
	private BeanWrapperImpl wrapper=new BeanWrapperImpl();
	private HibernateDaoSupport dao;
	
	private Map<String,String> propertyColumnMap;
	private Class beanClass;
	
	private boolean fechado=true;
		
	

	public void persistir(Object bean) {
		salvar(bean);		
	}


	protected String obtenerSQL(final Periodo p,final String tablaPrefix){
		ReplicationUtils.validarMismoMes(p);
		String tabla=ReplicationUtils.resolveTable(tablaPrefix, p.getFechaFinal());
		String sql="select * from "+tabla;
		return sql;
	}
	

	protected ReplicaLog registrar(String entidad,String tabla,String tipo,Periodo mes,int beans,int registros){
		final int year=getYear(mes);
		final int month=getMes(mes);
		final Date dia=mes.getFechaFinal();
		ReplicaLog log=getReplicaLogDao().buscar(entidad, tabla, year,month);
		if(log!=null)
			getReplicaLogDao().borrar(log);
		
		log=new ReplicaLog();
		log.setEntity(entidad);
		log.setTabla(tabla);
		log.setPeriodo(mes.toString());
		log.setBeans(beans);
		log.setYear(year);
		log.setTipo(tipo);
		log.setMonth(month);		
		log.setRegistros(registros);
		log.setDia(dia);
		try {
			getReplicaLogDao().salvar(log);
		} catch (Exception e) {
			logger.error("No se pudo guardar la bitacora: "+log,e);
		}		
		String res=log.validar()?"Carga de Periodo OK":"ERROR en carga de periodo: "+mes;
		logger.info(res);
		return log;
	}
	
	protected int contarRegistros(Periodo p,String tabla,String discriminator){
		String rowsSQL=ReplicationUtils.resolveSQLCount(p,tabla,discriminator);		
		int rows=getFactory().getJdbcTemplate(p).queryForInt(rowsSQL);
		return rows;
	}
	
	public void salvar(Collection beans){
		salvar(beans.toArray());
	}
	
	
	public void salvar(final Object... beans){
		Assert.notNull(getDao());		
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int saved=0;
				int buffer=0;
				for(Object bean:beans){
					session.saveOrUpdate(bean);
					if(buffer++%20==0){
						session.flush();
						session.clear();
					}
				}				
				return saved;
			}			
		});
		
	}
	
	/**
	 * 
	 */
	protected int contarBeans(Periodo p,Object... params){
		List<Periodo> meses=Periodo.periodosMensuales(p);
		Assert.notNull(getBeanClass(),"No se ha definido el tipo del bean (La clasea)  del replicador");
		String tabla=ClassUtils.getShortName(getBeanClass());		
		int beans=0;
		for(Periodo mes:meses){
			String hql=getHqlParaContarBeans();
			hql=hql.replaceAll("@entity", tabla);
			Object[] vals=new Object[2+params.length];
			vals[0]=getMes(mes);
			vals[1]=getYear(mes);
			int index=2;
			for(Object oo:params){
				vals[index++]=oo;
			}
			List<Long> l=getDao().getHibernateTemplate().find(hql, vals);			
			beans+=l.get(0);
		}
		return beans;		
	}
	
	/**
	 *  
	 * @return
	 */
	protected String getHqlParaContarBeans(){
		String hql="select count(*) from @entity e where e.mes=? and e.year=? ";
		return hql;
	}
	
	public void validarBulkImport(Periodo p) {
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			int beans=contarBeans(mes, new Object[0]);
			Assert.isTrue(0==beans,"Existen registros en el periodo seleccionado deben ser borrados antes de proseguir mes: "+mes);
		}		
	}
	
	public  static int getMes(final Periodo p){
		return Periodo.obtenerMes(p.getFechaFinal())+1;
	}
	public static int getYear(final Periodo p){
		return Periodo.obtenerYear(p.getFechaFinal());
	}
	
	protected void injectYearMonth(final Periodo mes,Collection beans){
		for(Object bean:beans){
			injectYearMonth(mes, bean);
		}
	}
	
	protected void injectYearMonth(final Periodo p,Object bean){		
		wrapper.setWrappedInstance(bean);
		wrapper.setPropertyValue("mes", getMes(p));
		wrapper.setPropertyValue("year", getYear(p));
	}

	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

	public JdbcTemplate getTargetJdbcTemplate() {
		return targetJdbcTemplate;
	}

	public void setTargetJdbcTemplate(JdbcTemplate targetJdbcTemplate) {
		this.targetJdbcTemplate = targetJdbcTemplate;
	}

	public ReplicaLogDao getReplicaLogDao() {
		return replicaLogDao;
	}

	public void setReplicaLogDao(ReplicaLogDao replicaLogDao) {
		this.replicaLogDao = replicaLogDao;
	}

	public HibernateDaoSupport getDao() {
		return dao;
	}

	public void setDao(HibernateDaoSupport dao) {
		this.dao = dao;
	}

	public Class getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}
	
	

	public boolean isFechado() {
		return fechado;
	}

	public void setFechado(boolean fechado) {
		this.fechado = fechado;
	}


	public void exportar(Object bean) {
		// TODO Auto-generated method stub
		
	}


	public List importar(Periodo periodo) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Map<String, String> getPropertyColumnMap() {
		return propertyColumnMap;
	}

	public void setPropertyColumnMap(Map<String, String> propertyColumnMap) {
		this.propertyColumnMap = propertyColumnMap;
	}	
	
	
	

	
}