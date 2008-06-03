/*
 * Created on 03-may-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.services;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.DefaultLocatorFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.cxc.managers.ChequesDevManager;
import com.luxsoft.siipap.cxc.managers.DescuentosManager;
import com.luxsoft.siipap.cxc.managers.JuridicoManager;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.cxc.managers.PagosManager;
import com.luxsoft.siipap.cxc.services.ActualizadorDeClientes;
import com.luxsoft.siipap.cxp.dao.AnalisisDeEntradaDao;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.dao2.UniversalDao;
import com.luxsoft.siipap.inventarios.dao.MovimientoDao;
import com.luxsoft.siipap.inventarios.services.InventariosManager;
import com.luxsoft.siipap.managers.CatalogosManager;
import com.luxsoft.siipap.maquila.manager.MaquilaManager;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * 
 * @author Ruben Cancino
 * 
 */
public final class ServiceLocator {
	
	private static Logger logger=Logger.getLogger(ServiceLocator.class);
												
												 
	public static final String DAO_CONTEXT="daoContext";
	
	public static final String DEFAULT_FACTORY_RESOURCE="classpath:factoryLocator.xml";
	
	public static String beanFactoryLocatorResource=DEFAULT_FACTORY_RESOURCE;
    
			
	private static BeanFactoryLocator locator;
	
	public static final String DEFAULT_CX="factoryLocator.xml";
	
	
	
	private ServiceLocator(){
	}
	
	

	public static BeanFactoryLocator getLocator() {
		if(locator==null){
			locator=DefaultLocatorFactory.getInstance(getBeanFactoryLocatorResource());
		}
		return locator;
	}
	
	public static String getBeanFactoryLocatorResource() {
		return beanFactoryLocatorResource;
	}

	public static void setBeanFactoryLocatorResource(
			String beanFactoryLocatorResource) {
		ServiceLocator.beanFactoryLocatorResource = beanFactoryLocatorResource;
	}
	
	public static synchronized ApplicationContext getContext(final String context)
    {
		try{
			ApplicationContext ctx= (ApplicationContext)getLocator()
						.useBeanFactory(context)
						.getFactory();
			return ctx;
		}catch(Exception e){
			logger.error("Error al cargar contexto: "+context,e);
			throw new RuntimeException(e);
		}
    }
	
	
	
	public static ApplicationContext getDaoContextPruebas(){
		System.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
		System.setProperty("db.driver", "oracle.jdbc.driver.OracleDriver");
		System.setProperty("db.url", "jdbc:oracle:thin:@server_ofi_d:1521:pruebas");		
		System.setProperty("db.user", "siipap");
		System.setProperty("db.password", "sys");
		System.setProperty("exportador.enabled", "true");		
		System.setProperty("exportador.path", "C:\\");
		return getContext(DAO_CONTEXT);
	}

	public static ApplicationContext getDaoContext(){
		System.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
		System.setProperty("db.driver", "oracle.jdbc.driver.OracleDriver");
		System.setProperty("db.url", "jdbc:oracle:thin:@server_ofi_d:1521:siipapw");		
		System.setProperty("db.user", "siipap");
		System.setProperty("db.password", "sis");
		System.setProperty("exportador.enabled", "true");
		System.setProperty("exportador.path", "G:\\SIIPAP\\ARCHIVOS\\COMUNICA");
		return getContext(DAO_CONTEXT);
	}
	
	public static ApplicationContext getDaoQroLocalContext(){
		System.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
		System.setProperty("db.driver", "oracle.jdbc.driver.OracleDriver");
		System.setProperty("db.url", "jdbc:oracle:thin:@server_ofi_d:1521:qro");		
		System.setProperty("db.user", "siipap");
		System.setProperty("db.password", "sis");
		System.setProperty("exportador.enabled", "false");
		System.setProperty("exportador.path", "G:\\SIIPAP\\ARCHIVOS\\COMUNICA");
		return getContext(DAO_CONTEXT);
	}
	
	/** Algunos servicios muy utiles **/
	
	public static DataSource getDataSource(){
		return (DataSource)getDaoContext().getBean("dataSource");
	}
	
	public static JdbcTemplate getJdbcTemplate(){
		return (JdbcTemplate)getDaoContext().getBean("jdbcTemplate");
	}
	public static SessionFactory getSessionFactory(){
		return (SessionFactory)getDaoContext().getBean("sessionFactory");
	}
	
	public static ArticuloDao getArticuloDao(){
		return (ArticuloDao)getDaoContext().getBean("articuloDao");
	}
	
	public static ClienteDao  getClienteDao(){
		return (ClienteDao)getDaoContext().getBean("clienteDao");
	}
	
	public static CXCManager getCXCManager(){
		return (CXCManager)getDaoContext().getBean("cxcManager");
	}
	
	public static PagosManager getPagosManager(){
		return (PagosManager)getDaoContext().getBean("pagosManager");
	}
	
	public static VentasManager getVentasManager(){
		return (VentasManager)getDaoContext().getBean("ventasManager");
	}
	public static DescuentosManager getDescuentosManager(){
		return (DescuentosManager)getDaoContext().getBean("descuentosManager");
	}
	public static NotasManager getNotasManager(){
		return (NotasManager)getDaoContext().getBean("notasManager");
	}
	
	public static MovimientoDao getMovimientoDao(){
		return (MovimientoDao)getDaoContext().getBean("movimientoDao");
	}
	
	public static AnalisisDeEntradaDao getAnalisisDeEntradaDao(){
		return (AnalisisDeEntradaDao)getDaoContext().getBean("analisisDeEntradaDao");
	}
	
	public static InventariosManager getInventariosManager(){
		return (InventariosManager)getDaoContext().getBean("inventariosManager");
	}
	
	public static InventariosManager getInventariosManagerQro(){
		return (InventariosManager)getDaoContext().getBean("inventariosManagerQro");
	}
	
	public static ActualizadorDeClientes getActualizadorDeClientes(){
		return (ActualizadorDeClientes)getDaoContext().getBean("actualizarClientes");
	}
	
	public static MaquilaManager getMaquilaManager(){
		return (MaquilaManager)getDaoContext().getBean("maquilaManager");
	}
	
	public static JuridicoManager getJuridicoManager(){
		return (JuridicoManager)getDaoContext().getBean("juridicoManager");
	}
	
	public static CatalogosManager getCatalogosManager(){
		return(CatalogosManager)getDaoContext().getBean("catalogosManager");
	}
	
	private static HibernateDaoSupport support;
	
	public static HibernateDaoSupport getDaoSupport(){
		if(support==null){
			support=new HibernateDaoSupport(){
				
			};
			support.setSessionFactory(getSessionFactory());
		}
		return support;
	}
	
	public static UniversalDao getUniversalDao(){
		return (UniversalDao)getDaoContext().getBean("universalDao");
	}
	
	public static ChequesDevManager getChequesDevManager(){
		return (ChequesDevManager)getDaoContext().getBean("chequesDevueltosManager");
	}
	
}
