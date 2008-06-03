package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class EntradaDeHojas extends MaterialHojeado{
	
	private SalidaACorte origen;
	private BigDecimal disponible=BigDecimal.ZERO;
	
	public BigDecimal getMerma(){
		return getOrigen().getMetros2().abs().subtract(getMetros2().abs());
	}

	public SalidaACorte getOrigen() {
		return origen;
	}

	public void setOrigen(SalidaACorte origen) {
		Object old=this.origen;
		this.origen = origen;
		getPropertyChangeSupport().firePropertyChange("origen",old,origen);
	}	

	public BigDecimal getDisponible() {
		return disponible;
	}

	public void setDisponible(BigDecimal disponible) {
		this.disponible = disponible;
	}
	
	/**
	public BigDecimal getCostoCalculado(){
		return getMetros2().multiply(BigDecimal.valueOf(getOrigen().getEntrada().getPrecioPorM2()));
	}
	**/
	
	public CantidadMonetaria getCostoCalculado(){
		return getCosto().multiply(getCantidad());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		EntradaDeHojas e=(EntradaDeHojas)obj;
		return new EqualsBuilder()
		.append(getOrigen().getId(),e.getOrigen().getId())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getOrigen().getId())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("ID: "+getId())
		.append(getClave())
		.append(getDescripcion())
		.append(getCantidad())
		.append(getFecha())
		.toString();
	}
	
	

}
