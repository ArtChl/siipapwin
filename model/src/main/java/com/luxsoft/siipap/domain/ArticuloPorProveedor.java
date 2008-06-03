/*
 * Created on 10/09/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.MutableObject;





/**
 * @author Ruben
 * @hibernate.class table="ARTICULOXPROVEEDOR"
 *
 */
public class ArticuloPorProveedor extends MutableObject implements Serializable{

	private Long id;
	private Articulo articulo;
	private Proveedor proveedor;
	private String codigoDelProveedor;
	private String descripcionDelProveedor;
	private Date creado;
	
	private Set<Precio> precios;

	
	public ArticuloPorProveedor (){
	}
	
	public Long getId() {
	    return id;
	}
	
	@SuppressWarnings("unused")
	private void setId(Long id) {   
        this.id = id;
	}	
	
		
    public Articulo getArticulo() {
        return articulo;
    }
    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }
    
    public String getCodigoDelProveedor() {
        return codigoDelProveedor;
    }
    public void setCodigoDelProveedor(String codigoDelProveedor) {
        this.codigoDelProveedor = codigoDelProveedor;
    }
    
    public Date getCreado() {
        return creado;
    }
    public void setCreado(Date creado) {
        this.creado = creado;
    }
    
    public String getDescripcionDelProveedor() {
        return descripcionDelProveedor;
    }
    public void setDescripcionDelProveedor(String descripcionDelProveedor) {
        this.descripcionDelProveedor = descripcionDelProveedor;
    }
    
    public Proveedor getProveedor() {
        return proveedor;
    }
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
       
    public Set<Precio> getPrecios() {
    	if(precios==null)
    		precios=new HashSet<Precio>();
        return precios;
    }
    
    @SuppressWarnings("unused")
	private void setPrecios(Set<Precio> precios) {
        this.precios = precios;
    }
    
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
        ArticuloPorProveedor name=(ArticuloPorProveedor)obj;
        return new EqualsBuilder()
                     .append(proveedor,name.getProveedor())
                     .append(articulo,name.getArticulo())
                     .isEquals();
    }

    public int hashCode() {        
        return new HashCodeBuilder(13,27)
        	.append(proveedor)
        	.append(articulo)
        	.hashCode();
    }
    
    public String toString(){
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
        	.append("Proveedor:",proveedor)
        	.append("Articulo:",articulo)
        	.toString();
    }
}
