package com.luxsoft.siipap.cxp.domain;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Precio;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.Salida;
import com.luxsoft.utils.domain.PersistentObject;

public class CompraIngresada extends PersistentObject{
	
	private Entrada com;
	private Facxpde analisis;
	private Salida dec;
	private long cantidadIngresada;
	private long cantidadAnalizada;
	private long cantidadDevuelta;
	private Precio precio;
	/*
	private String articulo;
	private String proveedor;
	private String factura;
	private String remision;
	*/
	private Date creado=Calendar.getInstance().getTime();
	private Date modificado;
	

	public Facxpde getAnalisis() {
		return analisis;
	}

	public void setAnalisis(Facxpde analisis) {
		this.analisis = analisis;
	}

	public long getCantidadAnalizada() {
		return cantidadAnalizada;
	}

	public void setCantidadAnalizada(long cantidadAnalizada) {
		this.cantidadAnalizada = cantidadAnalizada;
	}

	public long getCantidadDevuelta() {
		return cantidadDevuelta;
	}

	public void setCantidadDevuelta(long cantidadDevuelta) {
		this.cantidadDevuelta = cantidadDevuelta;
	}

	public long getCantidadIngresada() {
		return cantidadIngresada;
	}

	public void setCantidadIngresada(long cantidadIngresada) {
		this.cantidadIngresada = cantidadIngresada;
	}

	public Entrada getCom() {
		return com;
	}

	public void setCom(Entrada com) {
		this.com = com;
	}

	public Salida getDec() {
		return dec;
	}

	public void setDec(Salida dec) {
		this.dec = dec;
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

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		CompraIngresada a=(CompraIngresada)obj;
		return new EqualsBuilder()
			.append(this.com,a.getCom())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(com)
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append(com)
			.append(" > ")
			.append(analisis)
			.toString();
	}

	public Precio getPrecio() {
		return precio;
	}

	public void setPrecio(Precio precio) {
		this.precio = precio;
	}
	
	

}
