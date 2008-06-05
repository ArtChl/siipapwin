package com.luxsoft.siipap.swing;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.NotTransactional;

/**
 * Probar el correcto funcionamiento del acceso a la base de
 * datos para las pruebas de los selectores y el binding
 * 
 * @author Ruben Cancino
 *
 */
public class DBAccessTest extends AbstractDbAccessTests{
	
	protected JdbcTemplate jdbcTemplate;
	//public static final String DAO_PATH="swx-dao-test-ctx.xml";

	/**
	@Override
	protected String[] getConfigLocations() {
		return new String[]{
				DAO_PATH
				}; 
	}
	**/
	
	@NotTransactional
	public void testJdbcTemplate(){
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_FAMILIAS");
		assertTrue(expected>0);
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}



	
	

}
