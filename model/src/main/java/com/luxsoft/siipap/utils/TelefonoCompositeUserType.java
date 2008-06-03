package com.luxsoft.siipap.utils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import com.luxsoft.siipap.domain.Telefono;

public class TelefonoCompositeUserType implements CompositeUserType{
	
	public Class returnedClass() {
		return Telefono.class;
	}
	
	public boolean equals(Object x, Object y) throws HibernateException {
		if(x==y) return true;
        if(x==null || y==null)return false;
        return x.equals(y);
	}
	
	public int hashCode(Object o){
        return o.hashCode();
    }
	
	public Object deepCopy(Object value) throws HibernateException {
		return value;
    }
	
	public boolean isMutable(){
        return false;
    }
	
	public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
		String value=rs.getString(names[0]);
		if(rs.wasNull()) return null;
		String lada=rs.getString(names[1]);
		return new Telefono(value,lada);
	}
	
	public void nullSafeSet(PreparedStatement st, Object value, int index,
            SessionImplementor session) throws HibernateException, SQLException {
		if(value==null){
			st.setNull(index,Types.VARCHAR);
            st.setNull(index+1,Types.VARCHAR);
		}else{
			Telefono telefono=(Telefono)value;
			st.setString(index,telefono.getTelefono());
			st.setString(index+1,telefono.getLada());
		}
	}
	
	public String[] getPropertyNames() {
        return new String[]{"telefono","lada"};
    }
	
	public Type[] getPropertyTypes() {
        return new Type[]{Hibernate.STRING,Hibernate.STRING};
    }
	
	public Object getPropertyValue(Object component, int property)
    throws HibernateException {
		Telefono telefono=(Telefono)component;
		if(property==0)
			return telefono.getTelefono();
		return telefono.getLada();
	}
	
	public void setPropertyValue(Object component, int property, Object value)
    	throws HibernateException {
		throw new UnsupportedOperationException("Telefono es Inmutable");

	}
	
	public Object assemble(Serializable cached, SessionImplementor session,
            Object owner) throws HibernateException {
        return cached;
    }

	public Serializable disassemble(Object value, SessionImplementor session)
    	throws HibernateException {
		return (Serializable)value;

	}

	public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
		return original;
	}
}
