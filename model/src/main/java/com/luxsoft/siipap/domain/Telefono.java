/*
 * Created on 27/10/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;



/**
 * @author Ruben 
 */
public class Telefono implements Serializable{
    
    private String lada;
	private String telefono;
	
	public Telefono(){
	    
	}
	public Telefono (final String telefono,final String lada){
	    this.telefono=telefono;
	    this.lada=lada;
	}	
	
	public void setLada(String lada) {
		this.lada = lada;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getLada() {
		return lada;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
    public boolean equals(Object obj) {
        boolean equals=false;
        if(obj!=null && Telefono.class.isAssignableFrom(obj.getClass())){
            Telefono tel=(Telefono)obj;
            equals=new EqualsBuilder()
                     .append(telefono,tel.getTelefono())
                     .append(lada,tel.getLada())
                     .isEquals();
        }
        return equals;
    }

    public int hashCode() {        
        return new HashCodeBuilder(3,17)
        	.append(telefono)
        	.append(lada)
        	.toHashCode();
    }
    
    public String toString(){
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
        	.append("lada",lada)
        	.append("telefono",telefono)
        	.toString();
    }

	
}

