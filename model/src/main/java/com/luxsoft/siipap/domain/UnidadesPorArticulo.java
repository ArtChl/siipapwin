/*
 * Created on 29-abr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.luxsoft.utils.domain.MutableObject;





/**
 * @author Ruben Cancino
 * 
 */
public class UnidadesPorArticulo extends MutableObject{
	
	private Long id;
	private Articulo articulo;
	private Unidad unidad;
	private BigDecimal factor;
	private boolean enVenta=true;
	private boolean enCompras=true;
	private boolean enInventarios=true;
	private boolean enMaquila=true;
	
	

	public UnidadesPorArticulo() {
		super();		
	}
	
	public UnidadesPorArticulo(Articulo articulo, Unidad unidad) {
		super();
		this.articulo = articulo;
		this.unidad = unidad;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}
	public boolean isEnCompras() {
		return enCompras;
	}
	public void setEnCompras(boolean enCompras) {
		this.enCompras = enCompras;
	}
	public boolean isEnInventarios() {
		return enInventarios;
	}
	public void setEnInventarios(boolean enInventarios) {
		this.enInventarios = enInventarios;
	}
	public boolean isEnMaquila() {
		return enMaquila;
	}
	public void setEnMaquila(boolean enMaquila) {
		this.enMaquila = enMaquila;
	}
	public boolean isEnVenta() {
		return enVenta;
	}
	public void setEnVenta(boolean enVenta) {
		this.enVenta = enVenta;
	}
	public BigDecimal getFactor() {
		return factor;
	}
	public void setFactor(BigDecimal factor) {
		this.factor = factor;
	}
	public Unidad getUnidad() {
		return unidad;
	}
	public void setUnidad(Unidad unidad) {
		this.unidad = unidad;
	}
	
	 public boolean equals(Object obj) {
        boolean equals=false;
        if(obj!=null && UnidadesPorArticulo.class.isAssignableFrom(obj.getClass())){
            UnidadesPorArticulo name2=(UnidadesPorArticulo)obj;
            equals=new EqualsBuilder()
                     .append(articulo,name2.getArticulo())
					 .append(unidad,name2.getUnidad())
                     .isEquals();
        }
        return equals;
    }

    public int hashCode() {        
        return new HashCodeBuilder(17,37)
			.append(articulo)
        	.append(unidad)
        	.toHashCode();
    }
    
    public String toString(){
    	//return "test";
    	return articulo.getClave()+" > "+unidad.getClave();
        
    }
}
