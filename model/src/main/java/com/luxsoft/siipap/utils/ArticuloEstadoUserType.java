/*
 * Created on 27/03/2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.utils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;


import com.luxsoft.siipap.domain.Articulo;

/**
 * @author Propietario
 *
 * TODO Documentar
 */
public class ArticuloEstadoUserType implements UserType{
    private static final int[] SQL_TYPES={Types.VARCHAR};
    
    public int[] sqlTypes() {            
        return SQL_TYPES;
    }
    
    public Class returnedClass() {
        return Articulo.Estado.class;
    }

    
    public boolean equals(Object x, Object y) throws HibernateException {
        return x==y;
    }

    
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String name=rs.getString(names[0]);
        return Articulo.Estado.getEstado(name.trim());
        /*
        if(name.startsWith("A")){
            e=Articulo.Estado.getEstado("ACTIVO");
        }else if(name.startsWith("S")){
            e=Articulo.Estado.getEstado("SUSPENDIDO");
        }else if(name.startsWith("R")){
            e=Articulo.Estado.getEstado("RECLASIFICADO");
        }else if(name.startsWith("D"))
            e=Articulo.Estado.getEstado("DESCONTINUADO");
        else 
            e=null;
        return e;
        */
    }

    
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if(value==null){
            st.setNull(index,Types.VARCHAR);
        }else{
            st.setString(index,((Articulo.Estado)value).getEstado());
        }
        
    }

    
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    
    public boolean isMutable() {
        
        return false;
    }

	public int hashCode(Object x) throws HibernateException {		
		return x.hashCode();
	}

	public Serializable disassemble(Object value) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		
		return original;
	}
    

}
