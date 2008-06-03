package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.CantidadMonetaria;

public class SalidaDeHojas extends MaterialHojeado{
	
	private AnalisisDeEntrada destino;
	private EntradaDeHojas origen;
	private CantidadMonetaria gastos=CantidadMonetaria.pesos(0);
	private double precioPorKiloFlete=0;
	

	
	

	public AnalisisDeEntrada getDestino() {
		return destino;
	}

	public void setDestino(AnalisisDeEntrada destino) {
		this.destino = destino;
	}

	public EntradaDeHojas getOrigen() {
		return origen;
	}

	public void setOrigen(EntradaDeHojas origen) {
		this.origen = origen;
	}

	public CantidadMonetaria getGastos() {
		return gastos;
	}

	public void setGastos(CantidadMonetaria gastos) {
		this.gastos = gastos;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		SalidaDeHojas s=(SalidaDeHojas)obj;
		return new EqualsBuilder()
		.append(getDestino().getId(),s.getDestino().getId())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getDestino().getId())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getClave())
		.append(getDescripcion())
		.append(getCantidad())
		.append(getFecha())
		.toString();
	}

	public double getPrecioPorKiloFlete() {
		return precioPorKiloFlete;
	}

	public void setPrecioPorKiloFlete(double precioPorKiloFlete) {
		this.precioPorKiloFlete = precioPorKiloFlete;
	}
/**
	public BigDecimal getCostoCalculado(){
		final BigDecimal costoM2=getOrigen().getCostoCalculado();
		final BigDecimal costoMil=costoM2.divide(divisor)
		return getMetros2().multiply(BigDecimal.valueOf(getOrigen().getOrigen().getEntrada().getPrecioPorM2()));
	}
	**/
	
	public CantidadMonetaria getCosto(){
		return getOrigen().getCosto().multiply(getCantidad());
	}

}
