package com.luxsoft.siipap.em.dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * Crea una fuente de datos ODBC-JDBC para el año seleccionado
 * 
 * @author Ruben Cancino
 *
 */
public class SiipapDataSourceFactory {
	
	private Logger logger=Logger.getLogger(getClass());
	private final Map<Integer, DataSource> repositorio;
	
	
	
	public SiipapDataSourceFactory() {
		repositorio=new HashMap<Integer, DataSource>();
	}



	public DataSource getDataSource(int year){		
		//logger.debug("Localizando DataSource  para el año: "+year);
		DataSource ds=repositorio.get(year);
		if(ds==null){
			if(logger.isDebugEnabled()){
				logger.debug("No existe un DataSource par ese año creando una nueva");
			}
			ds=createNewDataSource(year);
			testDS(ds);
			repositorio.put(year, ds);
		}
		return ds;		
		
	}
	
	private DataSource createNewDataSource(int year){
		Assert.isTrue(year>=2002,"Solo se permiten hacer fuentes de datos ODBC de SIIPAP (DBF) a partir del 2002");		
		logger.info("Creando una nueva fuente de datos para: "+year);		
		BasicDataSource ds=new BasicDataSource();
		ds.setDriverClassName("sun.jdbc.odbc.JdbcOdbcDriver");
		if(isCurrentYear(year))
			ds.setUrl("jdbc:odbc:SIIPAP");
		else
			ds.setUrl("jdbc:odbc:SIIPAP"+"_"+year);
		return ds;
	}
	
	public boolean isCurrentYear(int year){
		Calendar c=Calendar.getInstance();
		int current=c.get(Calendar.YEAR);
		return current==year;
	}
	
	private void testDS(DataSource ds){
		try {
			ds.getConnection();			
		} catch (Exception e) {
			logger.error("Error creando ds",e);
		}
		
	}
	
	public void close() throws SQLException{
		Iterator<Integer> years=repositorio.keySet().iterator();
		while(years.hasNext()){
			DataSource ds=repositorio.get(years.next());
			((BasicDataSource)ds).close();
		}
	}

}
