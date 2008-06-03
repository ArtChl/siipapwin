/*
 * Created on 30/03/2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;




/**
 * Representacion del precio para un Articulo
 * 
 * @author Ruben Cancino 
 */
public class Precio extends PersistentObject {
    
    private Long id;
    
    private ListaDePrecios lista;
    //private Currency moneda;
    private CantidadMonetaria precio=CantidadMonetaria.pesos(0);
    private CantidadMonetaria neto=CantidadMonetaria.pesos(0);
    private ArticuloPorProveedor articuloProveedor;
    
    private BigDecimal descuento1=BigDecimal.ZERO;
    private BigDecimal descuento2=BigDecimal.ZERO;
    private BigDecimal descuento3=BigDecimal.ZERO;
    private BigDecimal descuento4=BigDecimal.ZERO;
    private BigDecimal descuento5=BigDecimal.ZERO;
    private BigDecimal descuento6=BigDecimal.ZERO;
    private BigDecimal descuento7=BigDecimal.ZERO;
    private BigDecimal descuento8=BigDecimal.ZERO;
    private BigDecimal financiero=BigDecimal.ZERO;
    
    private Date creado;
    private Date modificado;
    
    public Precio(){        
    }
    
    
    public ArticuloPorProveedor getArticuloProveedor() {
        return articuloProveedor;
    }
    public void setArticuloProveedor(ArticuloPorProveedor articuloProveedor) {
        this.articuloProveedor = articuloProveedor;
    }
    public BigDecimal getDescuento1() {
        return descuento1;
    }
    public void setDescuento1(BigDecimal descuento1) {
        this.descuento1 = descuento1;
    }
    public BigDecimal getDescuento2() {
        return descuento2;
    }
    public void setDescuento2(BigDecimal descuento2) {
        this.descuento2 = descuento2;
    }
    public BigDecimal getDescuento3() {
        return descuento3;
    }
    public void setDescuento3(BigDecimal descuento3) {
        this.descuento3 = descuento3;
    }
    public BigDecimal getDescuento4() {
        return descuento4;
    }
    public void setDescuento4(BigDecimal descuento4) {
        this.descuento4 = descuento4;
    }
    public BigDecimal getDescuento5() {
        return descuento5;
    }
    public void setDescuento5(BigDecimal descuento5) {
        this.descuento5 = descuento5;
    }
    public BigDecimal getDescuento6() {
        return descuento6;
    }
    public void setDescuento6(BigDecimal descuento6) {
        this.descuento6 = descuento6;
    }
    public BigDecimal getDescuento7() {
        return descuento7;
    }
    public void setDescuento7(BigDecimal descuento7) {
        this.descuento7 = descuento7;
    }
    public BigDecimal getDescuento8() {
        return descuento8;
    }
    public void setDescuento8(BigDecimal descuento8) {
        this.descuento8 = descuento8;
    }
    public BigDecimal getFinanciero() {
        return financiero;
    }
    public void setFinanciero(BigDecimal financiero) {
        this.financiero = financiero;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ListaDePrecios getLista() {
        return lista;
    }
    public void setLista(ListaDePrecios lista) {
        this.lista = lista;
    }
    /*
    public Currency getMoneda() {
        return moneda;
    }
    public void setMoneda(Currency moneda) {
        this.moneda = moneda;
    }*/
    public CantidadMonetaria getNeto() {
        return neto;
    }
    public void setNeto(CantidadMonetaria neto) {
    	Object old=this.neto;
        this.neto = neto;
        getPropertyChangeSupport().firePropertyChange("neto",old,neto);
    }
    
    public CantidadMonetaria getPrecio() {
        return precio;
    }
    public void setPrecio(CantidadMonetaria precio) {
        this.precio = precio;
    }
    public boolean equals(Object obj) {
    	if(obj==null)return false;
    	if(obj==this)return true;
    	Precio p=(Precio)obj;
        return new EqualsBuilder()
        //.append(getLista(),p.getLista())
        .append(getArticuloProveedor(),p.getArticuloProveedor())
        .append(getPrecio().getCurrency(),p.getPrecio().getCurrency())
        .isEquals();
    }
    
    public int hashCode() {
        return new HashCodeBuilder(1,7)
        //.append(lista)
        .append(articuloProveedor)
        .append(precio.getCurrency())
        .toHashCode();
    }
    
    public String toString(){
        return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
        	.append("Neto:"+getNeto())
        	.toString();
    }


	public Date getCreado() {
		return creado;
	}


	public void setCreado(Date creado) {
		this.creado = creado;
	}


	public Date getModificado() {
		return modificado;
	}


	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
	public void calcularNeto(){
		BigDecimal[] desc={descuento1,descuento2,descuento3,descuento4,descuento5,descuento6,descuento7,descuento8};
		CantidadMonetaria n=MonedasUtils.aplicarDescuentosEnCascada(precio,desc);
		setNeto(n);
	}
    
    

}
