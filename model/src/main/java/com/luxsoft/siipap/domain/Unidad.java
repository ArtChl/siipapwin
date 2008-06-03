/*
 * Created on 28/03/2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.luxsoft.utils.domain.PersistentObject;




/**
 * Unidad de medida para Articulos
 * 
 * @author Ruben Cancino
 */
public class Unidad extends PersistentObject implements Comparable{
    
    	
	private String clave;
	private String descripcion;			
	private int cantidad;
    
    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public boolean equals(Object obj) {
        boolean equals=false;
        if(obj!=null && Unidad.class.isAssignableFrom(obj.getClass())){
            Unidad name2=(Unidad)obj;
            equals=new EqualsBuilder()
                     .append(clave,name2.getClave())
                     .isEquals();
        }
        return equals;
    }

    public int hashCode() {        
        return new HashCodeBuilder(17,37)
        	.append(clave)
        	.toHashCode();
    }
    
    public String toString(){
        return getDescripcion();
    }

	public int compareTo(Object o) {
		Unidad uu=(Unidad)o;
		return getClave().compareToIgnoreCase(uu.getClave());
	}


}
