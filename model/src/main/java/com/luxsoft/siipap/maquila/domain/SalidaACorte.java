package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;

public class SalidaACorte extends MovimientoDeMaterial{
	
	private OrdenDeCorte orden;
	private Articulo destino;
	private EntradaDeMaterial entrada;	
	private BigDecimal estimadoMillares=BigDecimal.ZERO;
	private BigDecimal millaresEntregados=BigDecimal.ZERO;
	private CantidadMonetaria costoEstimado=CantidadMonetaria.pesos(0);
	private CantidadMonetaria costoDefinitivo=CantidadMonetaria.pesos(0);
	private double precioPorKiloHojeado=0;
	private EntradaDeHojas entradaReceptora;
	
	
	public Articulo getDestino() {
		return destino;
	}
	public void setDestino(Articulo destino) {
		this.destino = destino;
	}
	
	public EntradaDeMaterial getEntrada() {
		return entrada;
	}
	public void setEntrada(EntradaDeMaterial entrada) {
		Object old=this.entrada;		
		this.entrada = entrada;
		getPropertyChangeSupport().firePropertyChange("entrada",old,entrada);
	}
	
	public OrdenDeCorte getOrden() {
		return orden;
	}
	public void setOrden(OrdenDeCorte orden) {
		this.orden = orden;
	}
	
	
	public CantidadMonetaria getCostoDefinitivo() {
		return costoDefinitivo;
	}
	public void setCostoDefinitivo(CantidadMonetaria costoDefinitivo) {
		Object old=this.costoDefinitivo;
		this.costoDefinitivo = costoDefinitivo;
		getPropertyChangeSupport().firePropertyChange("costoDefinitivo",old,costoDefinitivo);
	}
	
	public CantidadMonetaria getCostoEstimado() {
		return costoEstimado;
	}
	public void setCostoEstimado(CantidadMonetaria costoEstimado) {
		Object old=this.costoEstimado;
		this.costoEstimado = costoEstimado;
		getPropertyChangeSupport().firePropertyChange("costoEstimado",old,costoEstimado);
	}
	/**
	public CantidadMonetaria getCostoPorMillarDefinitivo() {
		return costoPorMillarDefinitivo;
	}
	public void setCostoPorMillarDefinitivo(CantidadMonetaria costoPorMillarDefinitivo) {
		Object old=this.costoPorMillarDefinitivo;
		this.costoPorMillarDefinitivo = costoPorMillarDefinitivo;
		getPropertyChangeSupport().firePropertyChange("costoPorMillarDefinitivo",old,costoPorMillarDefinitivo);
	}
	
	public CantidadMonetaria getCostoPorMillarEstimado() {
		return costoPorMillarEstimado;
	}
	public void setCostoPorMillarEstimado(CantidadMonetaria costoPorMillarEstimado) {
		Object old=this.costoPorMillarEstimado;
		this.costoPorMillarEstimado = costoPorMillarEstimado;
		getPropertyChangeSupport().firePropertyChange("costoPorMillarEstimado",old,costoPorMillarEstimado);
	}
	**/
		
	public BigDecimal getEstimadoMillares() {
		return estimadoMillares;
	}
	public void setEstimadoMillares(BigDecimal estimadoMillares) {
		Object old=this.estimadoMillares;
		this.estimadoMillares = estimadoMillares;
		getPropertyChangeSupport().firePropertyChange("estimadoMillares",old,estimadoMillares);
	}
	
	
	public BigDecimal getMillaresEntregados() {
		return millaresEntregados;
	}
	public void setMillaresEntregados(BigDecimal millaresEntregados) {		
		Object old=this.millaresEntregados;
		this.millaresEntregados = millaresEntregados;
		getPropertyChangeSupport().firePropertyChange("millaresEntregados",old,millaresEntregados);
	}
	
	
	
	public EntradaDeHojas getEntradaReceptora() {
		return entradaReceptora;
	}
	public void setEntradaReceptora(EntradaDeHojas entradaReceptora) {
		this.entradaReceptora = entradaReceptora;
	}
	
	public BigDecimal getCosto(){
		return getKilos().multiply(BigDecimal.valueOf(getEntrada().getPrecioPorKilo())).setScale(2,RoundingMode.HALF_EVEN);
	}
	public CantidadMonetaria getCostoAsMoneda(){
		return CantidadMonetaria.pesos(getCosto().doubleValue());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		SalidaACorte sc=(SalidaACorte)obj;
		return new EqualsBuilder()			
			.append(getOrden(),sc.getOrden())
			.append(getArticulo(),sc.getArticulo())
			.append(getDestino(),sc.getDestino())
			.append(getCreado(),sc.getCreado())
			.isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(getOrden())
			.append(getArticulo())
			.append(getDestino())
			.append(getCreado())
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("ID: "+getId() )
		.append(" Bobina: "+getArticulo().getClave())		
		.append(" Articulo:"+getDestino().getClave())
		//.append(getKilos())		
		.toString();
	}
	public double getPrecioPorKiloHojeado() {
		return precioPorKiloHojeado;
	}
	public void setPrecioPorKiloHojeado(double precioPorKiloHojeado) {
		this.precioPorKiloHojeado = precioPorKiloHojeado;
	}
	
	
	

}
