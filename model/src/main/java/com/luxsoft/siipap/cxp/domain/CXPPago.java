package com.luxsoft.siipap.cxp.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * Pago de una factura, utiliza como referencia el numero de la factura a la cual se aplica el pago
 * 
 * @author Ruben Cancino
 *
 */
public class CXPPago extends CXP{
	
	//private CXPFactura factura;
	
	public CXPPago(){
		
	}
	
	
	 /*
	public CXPFactura getFactura() {
		return factura;
	}

	public void setFactura(CXPFactura factura) {
		this.factura = factura;
	}
*/

	/*
	public void setImporte(CantidadMonetaria importe){
		CantidadMonetaria m=new CantidadMonetaria(importe.amount().doubleValue()*-1,importe.getCurrency());
		super.setImporte(m);
	}
*/
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		CXPPago p=(CXPPago)obj;
		return new EqualsBuilder()
		.append(getProveedor(),p.getProveedor())
		.append(getReferencia(),p.getReferencia())
		.append(getId(),p.getId())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getProveedor())
		.append(getReferencia())
		.append(getId())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Prov:"+getClave())
		.append("Pago: "+getReferencia())
		.append("Fecha:",getFecha())
		.append("Importe:"+getImporteMN())
		.toString();
	}
	
	public void actualizarTotalesMN(){	
		setImporteMN(CantidadMonetaria.pesos(getImporte().multiply(getTc()).getAmount().doubleValue()));
		setImpuestoMN(new CantidadMonetaria(getImpuesto().multiply(getTc()).getAmount().doubleValue(),CantidadMonetaria.PESOS));
		setTotalMN(new CantidadMonetaria(getTotal().multiply(getTc()).getAmount().doubleValue(),CantidadMonetaria.PESOS));
	
	}

}
