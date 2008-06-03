package com.luxsoft.siipap.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.MutableObject;

public class ArticuloRow extends MutableObject implements Comparable{
	
	private String clave;
	private String descripcion1;    
	private BigDecimal kilos;
	private int gramos;
	private String unidad;
	private String familia;
	private String famDesc;
	private BigDecimal largo=BigDecimal.valueOf(0);
	private BigDecimal ancho=BigDecimal.valueOf(0);
	private int calibre;
	private double area;
	
	public ArticuloRow(){
	}
	
	
	
	public ArticuloRow(String clave, String descripcion1, BigDecimal kilos, int gramos, String familia,String familiaDesc) {
		super();
		this.clave = clave;
		this.descripcion1 = descripcion1;
		this.kilos = kilos;
		this.gramos = gramos;
		this.familia = familia;
		this.famDesc=familiaDesc;
	}



	public ArticuloRow(BigDecimal ancho, int calibre, String clave, String descripcion1, String familia, int gramos, BigDecimal kilos, BigDecimal largo, String unidad,double area) {				
		this.ancho = ancho;
		this.calibre = calibre;
		this.clave = clave;
		this.descripcion1 = descripcion1;
		this.familia = familia;
		this.gramos = gramos;
		this.kilos = kilos;
		this.largo = largo;
		this.unidad = unidad;
		this.area=area;
	}


	public BigDecimal getAncho() {
		return ancho;
	}
	public void setAncho(BigDecimal ancho) {
		this.ancho = ancho;
	}
	public int getCalibre() {
		return calibre;
	}
	public void setCalibre(int calibre) {
		this.calibre = calibre;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getDescripcion1() {
		return descripcion1;
	}
	public void setDescripcion1(String descripcion1) {
		this.descripcion1 = descripcion1;
	}
	public String getFamilia() {
		return familia;
	}
	public void setFamilia(String familia) {
		this.familia = familia;
	}
	public int getGramos() {
		return gramos;
	}
	public void setGramos(int gramos) {
		this.gramos = gramos;
	}
	public BigDecimal getKilos() {
		return kilos;
	}
	public void setKilos(BigDecimal kilos) {
		this.kilos = kilos;
	}
	public BigDecimal getLargo() {
		return largo;
	}
	public void setLargo(BigDecimal largo) {
		this.largo = largo;
	}
	
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}	

	public double getArea() {
		return area;
	}
	public void setArea(double area) {
		this.area = area;
	}

	public String getFamDesc() {
		return famDesc;
	}
	public void setFamDesc(String famDesc) {
		this.famDesc = famDesc;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getClave())
		.append(getDescripcion1())
		.append(getFamilia())
		.append(getFamDesc())
		.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		ArticuloRow row=(ArticuloRow)obj;
		return getClave().equals(row.getClave());
	}


	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(getClave())
			.toHashCode();
	}

	public int compareTo(Object o) {
		ArticuloRow ao=(ArticuloRow)o;
		return getClave().compareToIgnoreCase(ao.getClave());
	}

}

