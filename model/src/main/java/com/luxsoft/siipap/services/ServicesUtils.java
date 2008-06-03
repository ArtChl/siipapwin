package com.luxsoft.siipap.services;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

/**
 * Algunas operaciones relacionadas con servicios y daos
 * de uso comun
 * 
 * @author Ruben Cancino
 *
 */
public class ServicesUtils {
	
	
	public static synchronized Object getDataBaseLocationInfo(){
		return ServiceLocator.getJdbcTemplate().execute(new ConnectionCallback(){
			public Object doInConnection(Connection con) throws SQLException, DataAccessException {
				String s=con.getMetaData().getURL();
				return s;
			}
			
		});
	}

}
