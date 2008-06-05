package com.luxsoft.siipap.em.replica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.TypeMismatchException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.CantidadMonetaria;

public class DefaultMapper implements RowMapper{
	
	
	private Logger logger=Logger.getLogger(getClass());
	private Class beanClass;
	private String prefix="";	
	private String origen="";
	private BeanWrapperImpl wrapper;
	private Map<String,String> propertyColumnMap;
	
	public DefaultMapper(){		
		
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Assert.notNull(getPropertyColumnMap(),"Needs a Map of Properties - Columns");		
		
		if(wrapper==null)
			wrapper=createWrapper();
		wrapper.setWrappedInstance(BeanUtils.instantiateClass(getBeanClass()));
		//if(StringUtils.isEmpty(getOrigen())&& wrapper.isWritableProperty("origen"))
		if(wrapper.isWritableProperty("origen")){
			wrapper.setPropertyValue("origen", getOrigen());
		}
			
		
				
		
		for(Map.Entry<String, String> entry:getPropertyColumnMap().entrySet()){
			final String property=entry.getKey();
			
			final String column=getPrefix()+entry.getValue();
			try {
				rs.findColumn(column);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("Esta columna no esta definida en el DBF: "+column);
				continue;
			}
			
			if(wrapper.getPropertyType(property).equals(CantidadMonetaria.class)){
				Number val=(Number)rs.getObject(column);
				
				if(val!=null){
					CantidadMonetaria monto=CantidadMonetaria.pesos(val.doubleValue());
					wrapper.setPropertyValue(property, monto);
				}
				continue;
			}
			Object val=rs.getObject(column);
			
			if(val!=null){
				try{
					wrapper.setPropertyValue(property,val);
					//if("numDocumento".equals(property)){
						//System.out.println("Procesando: "+property+" val  "+wrapper.getPropertyValue(property));
					//}
				}catch(TypeMismatchException e){
					//e.printStackTrace();
					logger.error("Tipo de dato incorrecto: "+val);
					continue;
				}
			}
		}
		
		return wrapper.getWrappedInstance();
		
	}
	
	protected BeanWrapperImpl createWrapper(){
		return new BeanWrapperImpl(getBeanClass());
	}
	
	public Class getBeanClass() {
		return beanClass;
	}
	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}
	
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Map<String, String> getPropertyColumnMap() {
		return propertyColumnMap;
	}
	public void setPropertyColumnMap(Map<String, String> propertyColumnMap) {
		this.propertyColumnMap = propertyColumnMap;
	}
}
