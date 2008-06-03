package com.luxsoft.siipap.cxp.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * Nota de credito del proveedor. El numero de la nota de credito es el documento
 * 
 * @author Ruben Cancino
 *
 */
public class CXPNCredito extends CXP{
	
	
	private String documento;
	private CXPFactura factura;
	
	
	
	
	
	public CXPFactura getFactura() {
		return factura;
	}

	public void setFactura(CXPFactura factura) {
		this.factura = factura;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}
/*
	public void setImporte(CantidadMonetaria importe){
		if(importe.getAmount().doubleValue()>0){
			CantidadMonetaria m=new CantidadMonetaria(importe.amount().doubleValue()*-1,importe.getCurrency());
			super.setImporte(m);
		}else
			super.setImporte(importe);
		
		
	}
	
*/	

	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		CXPNCredito p=(CXPNCredito)obj;
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
		.append("Prov:"+getProveedor().getClave())
		.append("N.Credito: "+getReferencia())
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
