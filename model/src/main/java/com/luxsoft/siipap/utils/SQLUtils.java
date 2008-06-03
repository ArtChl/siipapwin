package com.luxsoft.siipap.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;

public class SQLUtils {
	
	private static Logger logger=Logger.getLogger(SQLUtils.class);
	
	public static String loadSQLQueryFromResource(final String path) {
		try{
			InputStream io=SQLUtils.class
			.getClassLoader()
			.getResourceAsStream(path);
			Assert.notNull(io,"El recurso no existe: "+path);
			BufferedReader r=new BufferedReader(new InputStreamReader(io));
			String sql="";	
			String line;
			while ((line = r.readLine())!=null){
				if(line!=null){
					sql+=line+"\n";
				}		
			}	
			r.close();
			return sql;
		}catch (Exception e) {
			logger.error(e);
			String msg=MessageFormat.format("Imposible cargar el query {0} " +
					"error: {1}", path,e.getMessage());
			throw new RuntimeException(msg,e);
		}
		
	}
	
	public static CantidadMonetaria safeNullToPesos(Object val){
		if(val==null) 
			return CantidadMonetaria.pesos(0);
		Assert.isTrue(val instanceof Number,"No implementa Number :"+val.getClass().getName());
		Number n=(Number)val;
		return CantidadMonetaria.pesos(n.doubleValue());
		
	}
	
	public static double safeToDouble(Object val){
		if(val==null)
			return 0;
		Assert.isTrue(val instanceof Number,"No implementa Number :"+val.getClass().getName());
		Number n=(Number)val;
		return n.doubleValue();
	}
	
	@SuppressWarnings("unchecked")
	public static void printColumnNames(final String sql){
		JdbcTemplate tm=ServiceLocator.getJdbcTemplate();
		tm.setMaxRows(1);
		
		List<Map<String, Object>> rows=tm.queryForList(sql);
		StringBuffer buffer=new StringBuffer();
		for(Map<String, Object> row:rows){
			Set<String> columnas=row.keySet();
			
			buffer.append("{");
			for(String s:columnas){
				buffer.append("\"");				
				buffer.append(s);
				buffer.append("\"");
				buffer.append(",");
			}
			buffer.append("}");
		}
		System.out.println(buffer);
	}
	
	

}
