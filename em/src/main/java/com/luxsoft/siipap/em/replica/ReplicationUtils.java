package com.luxsoft.siipap.em.replica;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.utils.SiipapDateUtils;

@SuppressWarnings("unchecked")
public class ReplicationUtils {
	
	private static Logger logger=Logger.getLogger(ReplicationUtils.class);
	
	/**
	 * Obtiene el SQL adecuado para una tabla fechada de SIIPAP  
	 * 
	 * @param date
	 * @param tablePrefix
	 * @param descriminator
	 * @return
	 */
	public static String resolveSQL(final Periodo periodo,String tablePrefix,String descriminator){
		String sql="select * from @TABLE @WHERE";
		sql=sql.replaceAll("@TABLE",resolveTable(tablePrefix,periodo.getFechaInicial()));
		sql=sql.replaceAll("@WHERE",resolveWherePart(periodo,descriminator));
		if(logger.isDebugEnabled()){
			logger.debug("Query resuleto: "+sql);
		}
		return sql;
	}
	
	public static String resolveTable(final String prefix,final Date date){		
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.getTime();
		String month=String.valueOf(c.get(Calendar.MONTH)+1);
		String suf=StringUtils.leftPad(month,2,'0');
		return prefix+suf;
	}
	
	public static String resolveWherePart(Periodo periodo,String descriminator){		
		String sql=" where @FECHA between @VAL1 and @VAL2 ";
		sql=sql.replaceFirst("@FECHA",descriminator);
		sql=sql.replaceFirst("@VAL1",SiipapDateUtils.getSiipapDate(periodo.getFechaInicial()));
		sql=sql.replaceFirst("@VAL2",SiipapDateUtils.getSiipapDate(periodo.getFechaFinal()));
		return sql;
	}
	
	/**
	 * Obtiene el SQL count(*) adecuado para una tabla fechada de SIIPAP  
	 * 
	 * @param date
	 * @param tablePrefix
	 * @param descriminator
	 * @return
	 */
	public static String resolveSQLCount(final Periodo periodo,String tablePrefix,String descriminator){
		String sql="select count(*) from @TABLE @WHERE";
		sql=sql.replaceAll("@TABLE",resolveTable(tablePrefix,periodo.getFechaInicial()));
		sql=sql.replaceAll("@WHERE",resolveWherePart(periodo,descriminator));
		if(logger.isDebugEnabled()){
			logger.debug("Query resuleto: "+sql);
		}
		return sql;
	}
	
	/**
	 * Transforma una lista de registros del DBF en una lista de Beans
	 * Esta implementacion delega todo al método transformarRegistro. La razon de existir de este metodo
	 * se basa en la posibilidad de que en algunas ocaciones sea posible mejorar el performance en base a lecturas
	 * en batch. Hasta el momento no ha sido utilizado en ninguna implementacion.
	 * 
	 */
	
	public static List transformarRegistros(final List registros,final Class clazz) {
		List<Map> rows=new ArrayList<Map>(registros);
		List objects=new ArrayList();
		for(Map row:rows){
			try{
				Object obj=transformarRegistro(row,clazz);
				objects.add(obj);
			}catch(Exception ex){
				logger.error("Imposible generar instancia a partir de registro: "+row,ex);
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("Objectos  creados del DBF: "+objects.size());
		}
		
		return objects;
	}
	
	/**
	 * Trasformación estandar mapea el nombre de un
	 * campo con el de una propiedad del bean, puede ser util para
	 * asignar valores del bean al
	 * 
	 * @param registro
	 * @return
	 */	
	@SuppressWarnings("unchecked")
	public static Object transformarRegistro(final Map registro,final Class clazz) {

		BeanWrapperImpl wrapper=new BeanWrapperImpl(clazz);
		Set<Map.Entry<String,?>> mapeo=registro.entrySet();		
		for(Map.Entry<String,?> entry:mapeo){
			String property=entry.getKey();
			
			if(!wrapper.isWritableProperty(property)){
				continue;
			}
			Class propertyType=wrapper.getPropertyType(property);
			Object value=entry.getValue();
			if(value!=null){
				Object newVal=wrapper.convertIfNecessary(value, propertyType);
				if(newVal!=null)
					wrapper.setPropertyValue(property, newVal);
			}
			
			
		}
		return  wrapper.getWrappedInstance();		
	}
	
	/**
	 * Valida que el periodo proporcionado esta comprendido en el mismo año mes
	 * 
	 * @param mes
	 */
	public static void validarMismoMes(final Periodo mes){
		final int year1=Periodo.obtenerYear(mes.getFechaInicial());
		final int year2=Periodo.obtenerYear(mes.getFechaFinal());		
		Assert.isTrue(year1==year2,"El periodo no esta contenido en un mismo año"+mes);
		final int mes1=Periodo.obtenerMes(mes.getFechaInicial());
		final int mes2=Periodo.obtenerMes(mes.getFechaFinal());
		Assert.isTrue(mes1==mes2,"El periodo no esta contenido en un mismo mes "+mes);
	}
	
	public static void printCamposDeDBFParaXML(String tabla,final int year){
		JdbcTemplate template=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(year);
		template.query("select * from "+tabla+" where 1=2", new ResultSetExtractor(){

			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				ResultSetMetaData meta=rs.getMetaData();
				int cols=meta.getColumnCount();
				for(int i=1;i<=cols;i++){
					String col=meta.getColumnName(i);
					String msg=MessageFormat.format("<entry key=\"\" 			value=\"{0}\" />", col);
					System.out.println(msg);
				}
				return null;
			}
			
		});
	}
	
	

}
