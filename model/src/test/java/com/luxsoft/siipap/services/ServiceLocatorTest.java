package com.luxsoft.siipap.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import junit.framework.TestCase;

/**
 * Prueba que de manera correcta se incialize el singleton
 * 
 * ServiceManager 
 * 
 * @author Ruben Cancino
 *
 */
public class ServiceLocatorTest extends TestCase{
	
	
	
	
	public void testDataSource(){
		DataSource ds=ServiceLocator.getDataSource();
		assertNotNull(ds);
	}
	
	public void testJdbcTemplate(){
		JdbcTemplate tm=ServiceLocator.getJdbcTemplate();
		tm.query("SELECT * FROM SW_SUCURSALES", new RowCallbackHandler(){
			public void processRow(ResultSet rs) throws SQLException {
				System.out.println(rs.getString(1));
			}			
		});
		assertNotNull(tm);
	}

}
