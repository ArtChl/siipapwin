package com.luxsoft.siipap.inventarios.domain;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.utils.domain.PersistentObject;

public class Transformacion extends PersistentObject{
	
	
	private Salida origen;
	private Entrada destino;
	private CantidadMonetaria costoDeMaterial=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importePorCorte=CantidadMonetaria.pesos(0);
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	
	private String claveOrigen;
	private String claveDestino;
	private int year;
	private int mes;
	
	
	private Date creado=Calendar.getInstance().getTime();
	private Date modificado;
	
	
	
	public CantidadMonetaria getCosto() {
		return costo;
	}
	public void setCosto(CantidadMonetaria costo) {
		this.costo = costo;
	}
	public CantidadMonetaria getCostoDeMaterial() {
		return costoDeMaterial;
	}
	public void setCostoDeMaterial(CantidadMonetaria costoDeMaterial) {
		this.costoDeMaterial = costoDeMaterial;
	}
	public Entrada getDestino() {
		return destino;
	}
	public void setDestino(Entrada destino) {
		this.destino = destino;
	}
	public CantidadMonetaria getImportePorCorte() {
		return importePorCorte;
	}
	public void setImportePorCorte(CantidadMonetaria importePorCorte) {
		this.importePorCorte = importePorCorte;
	}
	public Salida getOrigen() {
		return origen;
	}
	public void setOrigen(Salida origen) {
		this.origen = origen;
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
	
	public CantidadMonetaria calcularCosto(){		
		return getCostoDeMaterial().add(getImportePorCorte());
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		Transformacion t=(Transformacion)obj;
		return new EqualsBuilder()
		.append(this.getOrigen(),t.getOrigen())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getOrigen())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getOrigen().getALMARTIC())
		.append("-->")
		.append("Cve Origen:",getDestino().getALMARTIC())
		.append("Cve Destino:",getOrigen().getALMNUMER())
		.append(getCosto())
		.append("Año:",getYear())		
		.append("Mes",getMes())
		.toString();
	}
	public String getClaveDestino() {
		return claveDestino;
	}
	public void setClaveDestino(String claveDestino) {
		this.claveDestino = claveDestino;
	}
	public String getClaveOrigen() {
		return claveOrigen;
	}
	public void setClaveOrigen(String claveOrigen) {
		this.claveOrigen = claveOrigen;
	}
	
	

}
