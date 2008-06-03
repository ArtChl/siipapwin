package com.luxsoft.siipap.utils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.util.ReflectHelper;

/**
 * User type para salvar enumerations con name()
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class StringEnumUserType implements EnhancedUserType,ParameterizedType{
	
	private Class<Enum> enumClass;

	
	public Object fromXMLString(String xmlValue) {		
		return Enum.valueOf(enumClass, xmlValue);
	}

	public String objectToSQLString(Object value) {
		if(value instanceof EnumId){
			EnumId ei=(EnumId)value;
			return ei.getId();
		}
		return '\''+((Enum)value).name()+'\'';
	}

	public String toXMLString(Object value) {
		return ((Enum)value).name();
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {		
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {		
		return value;
	}

	public Serializable disassemble(Object value) throws HibernateException {		
		return (Serializable)value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if(x==y) return true;
		if(x==null || y==null) return false;
		return x.equals(y);
	}

	public int hashCode(Object x) throws HibernateException {		
		return x.hashCode();
	}

	public boolean isMutable() {		
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		String name=rs.getString(names[0]);		
		return rs.wasNull()?null:Enum.valueOf(enumClass, name);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		if(value==null){
			st.setNull(index, Hibernate.STRING.sqlType());
		}else{
			if(value instanceof EnumId){
				st.setString(index, ((EnumId)value).getId());
			}else
				st.setString(index, ((Enum)value).name());
		}
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {		
		return original;
	}

	public Class returnedClass() {
		return enumClass;
	}

	public int[] sqlTypes() {		
		return new int[]{Hibernate.STRING.sqlType()};
	}

	public void setParameterValues(Properties parameters) {
		String enumClassName=parameters.getProperty("enumClassname");
		try{
			enumClass=ReflectHelper.classForName(enumClassName);
		}catch (ClassNotFoundException cnfe) {
			throw new HibernateException("Enum class not found",cnfe);
		}
		
	}

}
