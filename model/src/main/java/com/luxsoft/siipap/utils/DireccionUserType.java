/*
 * Created on 28/03/2005
 *
 * TODO Colocar informacion de licencia
 */
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

import com.luxsoft.siipap.domain.Direccion;


/**
 * Hibernate UserType para Direccion
 * 
 * @author Ruben Cancino 
 *
 */
public class DireccionUserType implements CompositeUserType{
    
    
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
                "calle",
            	"numero",
				"numeroExterior",
            	"colonia",
            	"cp",
            	"ciudad",
            	"estado",
            	"municipio",
				"pais"};
    }
    
    public Type[] getPropertyTypes() {
        return new Type[]{
                Hibernate.STRING,
                Hibernate.STRING,
                Hibernate.STRING,
                Hibernate.STRING,
                Hibernate.STRING,
                Hibernate.STRING,
                Hibernate.STRING,
				Hibernate.STRING,
				Hibernate.STRING};
    }
    
    public Object getPropertyValue(Object component, int property)
            throws HibernateException {
        Direccion dir=(Direccion)component;        
        switch (property) {
        case 0:
            return dir.getCalle();
        case 1:
            return dir.getNumero();
        case 2:
            return dir.getNumeroExterior();
        case 3:
            return dir.getColonia();
        case 4:
            return dir.getCp();
        case 5:
            return dir.getCiudad();
        case 6:
            return dir.getEstado();
        case 7:
            return dir.getMunicipio();
        case 8:
           	return dir.getPais();
        default:
            throw new RuntimeException("No existe esa propiedad en Direccion");
        }
    }
    
    public boolean isMutable() {    
        return true;
    }
    
    public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {        
        String calle=rs.getString(1);
        if(rs.wasNull()) return null;    	
    	Direccion dir=new Direccion();
    	dir.setCalle(calle);
    	dir.setNumero(rs.getString(2));
    	dir.setNumeroExterior(rs.getString(3));
    	dir.setColonia(rs.getString(4));
    	dir.setCp(rs.getString(5));
    	dir.setCiudad(rs.getString(6));
    	dir.setEstado(rs.getString(7));
    	dir.setMunicipio(rs.getString(8));
    	dir.setPais(rs.getString(9));
    	return dir;
    }
    
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SessionImplementor session) throws HibernateException, SQLException {
        if(value==null){
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
            st.setNull(index++,Types.CHAR);
        }else{
            Direccion dir=(Direccion)value;
            st.setString(index++,dir.getCalle());
            st.setString(index++,dir.getNumero());
            st.setString(index++,dir.getNumeroExterior());
            st.setString(index++,dir.getColonia());
            st.setString(index++,dir.getCp());
            st.setString(index++,dir.getCiudad());
            st.setString(index++,dir.getEstado());
            st.setString(index++,dir.getMunicipio());
            st.setString(index++,dir.getPais());
        }

    }
    
    public Class returnedClass() {
       return Direccion.class;
    }
    public void setPropertyValue(Object component, int property, Object value)
            throws HibernateException {
        Direccion dir=(Direccion)component;
        String s=(String)value;
        switch (property) {
        case 0:
            dir.setCalle(s);
        case 1:
            dir.setNumero(s);
        case 2:
            dir.setNumeroExterior(s);
        case 3:
            dir.setColonia(s);
        case 4:
            dir.setCp(s);
        case 5:
            dir.setCiudad(s);
        case 6:
            dir.setEstado(s);
        case 7:
            dir.setMunicipio(s);
        case 8:
            dir.setPais(s);            
        default:
            throw new RuntimeException("No existe esa propiedad en Direccion");
        }

    }
    
	public int hashCode(Object x) throws HibernateException {		
		return x.hashCode();
	}
	
	public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
		return original;
	}
}