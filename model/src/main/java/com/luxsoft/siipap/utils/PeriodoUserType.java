/*
 * Created on 30/03/2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.utils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;


import com.luxsoft.siipap.domain.Periodo;


/**
 * Periodo como tipo de dato Hibernate
 * 
 * @author Ruben Cancino
 */
public class PeriodoUserType implements CompositeUserType {
    
    public Object assemble(Serializable cached, SessionImplementor session,
            Object owner) throws HibernateException {
        
        return cached;
    }
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }
    public Serializable disassemble(Object value, SessionImplementor session)
            throws HibernateException {
        return (Serializable)value;
    }
    
    public boolean equals(Object x, Object y) throws HibernateException {
        if(x==y) return true;
        if(x==null || y==null)return false;
        return x.equals(y);
    }
    public String[] getPropertyNames() {        
        return new String[]{
                "fechaInicial",
            	"fechaFinal",
            	};
    }
    
    public Type[] getPropertyTypes() {
        return new Type[]{
                Hibernate.DATE,
                Hibernate.DATE
                };
    }
    
    public Object getPropertyValue(Object component, int property)
            throws HibernateException {
        Periodo p=(Periodo)component;        
        switch (property) {
        case 0:
            return p.getFechaInicial();
        case 1:
            return p.getFechaFinal();        
        default:
            throw new RuntimeException("No existe esa propiedad en Periodo");
        }
    }
    
    public boolean isMutable() {    
        return true;
    }
    
    public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {        
        Date d1=rs.getDate(1);
        Periodo p=new Periodo();
        if(rs.wasNull()) return null;    	
    	p.setFechaInicial(d1);
    	p.setFechaFinal(rs.getDate(1));
    	return p;
    }
    
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SessionImplementor session) throws HibernateException, SQLException {
        if(value==null){
            st.setNull(index++,Types.DATE);
            st.setNull(index++,Types.DATE);            
        }else{
            Periodo p=(Periodo)value;
            st.setObject(index++,p.getFechaInicialAsString());
            st.setObject(index++,p.getFechaFinalAsString());
        }

    }
    
    public Class returnedClass() {
       return Periodo.class;
    }
    public void setPropertyValue(Object component, int property, Object value)
            throws HibernateException {
        Periodo p=(Periodo)component;
        Date d1=(Date)value;
        switch (property) {
        case 0:
            p.setFechaInicial(d1);
        case 1:
            p.setFechaFinal(d1);        
        default:
            throw new RuntimeException("No existe esa propiedad en Periodo");
        }

    }
	public int hashCode(Object x) throws HibernateException {		
		return x.hashCode();
	}
	public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {		
		return original;
	} 

}
