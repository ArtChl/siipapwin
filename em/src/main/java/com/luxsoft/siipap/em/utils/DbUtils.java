package com.luxsoft.siipap.em.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import com.luxsoft.siipap.em.managers.EMServiceLocator;

public class DbUtils {

	public static String getSiipapOdbc() {
		Object res=(Object)EMServiceLocator.instance().getSiipapTemplateFactory()
				.getJdbcTemplate().execute(new ConnectionCallback() {
					public Object doInConnection(Connection con) throws SQLException, DataAccessException {
						return con.getMetaData().getURL();
					}

				});
		return res!=null?res.toString():"ERR SIN ODBC";
	}

}
