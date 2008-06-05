package com.luxsoft.siipap.em.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

//import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.luxsoft.siipap.domain.Periodo;

public class SiipapJdbcTemplateFactory {
	
	private SiipapDataSourceFactory factory;
	private Map<Integer, JdbcTemplate> repository=new HashMap<Integer, JdbcTemplate>();
	
	//private Logger logger=Logger.getLogger(getClass());
	
	public JdbcTemplate getJdbcTemplate(){
		return getJdbcTemplate(new Date());
	}
	
	public JdbcTemplate getJdbcTemplate(int year){
		DataSource ds=factory.getDataSource(year);
		if(ds!=null){
			JdbcTemplate tm=repository.get(year);
			if(tm==null){
				tm=new JdbcTemplate(ds);
				repository.put(year,tm);
			}
			return tm;
		}
		return null;
	}
	
	/**
	 * Comodity para usar fecha en lugar de año
	 * @param date
	 * @return
	 */
	public JdbcTemplate getJdbcTemplate(Date date){
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.getTime();
		return getJdbcTemplate(c.get(Calendar.YEAR));
	}
	
	/**
	 * Comoditi que usa la fecha inicial para obtener el template
	 * @param p
	 * @return
	 */
	public JdbcTemplate getJdbcTemplate(Periodo p){
		return getJdbcTemplate(p.getFechaInicial());
	}

	public SiipapDataSourceFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapDataSourceFactory factory) {
		this.factory = factory;
	}
	
	

}
