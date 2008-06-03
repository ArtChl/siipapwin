package com.luxsoft.siipap.ventas.agents;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class DataSourceTest extends TestCase {

	public void testDriverManagerDS() throws Exception {
		// try{

		BasicDataSource ds = new BasicDataSource();

		Properties props = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("jdbc.properties");
		props.load(is);

		String driver = props.getProperty("jdbc.driverClassName");
		String url = props.getProperty("jdbc.url");
		String user = props.getProperty("jdbc.username");
		String pass = props.getProperty("jdbc.password");
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pass);

		String msg = MessageFormat.format(
				"Conectando con Driver:{0} URL:{1} User:{2} Pass:{3}", driver,
				url, user, pass);
		System.out.println(msg);

		JdbcTemplate template = new JdbcTemplate(ds);
		int rows = template.queryForInt("select count(*) from sw_familias");
		System.out.println("Rows: " + rows);

	}

	public void testSpringDS() {

		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:swx-dao-ctx.xml");
		JdbcTemplate template = (JdbcTemplate) ctx.getBean("jdbcTemplate");
		int rows = template.queryForInt("select count(*) from sw_familias");
		System.out.println("Rows: " + rows);
	}

}
