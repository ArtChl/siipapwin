package com.luxsoft.siipap.cxp.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Nota de cargo de un proveedor
 * 
 * @author Ruben Cancino
 *
 */
public class CXPNCargo extends CXP{

	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		CXPNCargo f=(CXPNCargo)obj;
		return new EqualsBuilder()
		.append(getProveedor(),f.getProveedor())
		.append(getReferencia(),f.getReferencia())
		.append(getId(),f.getId())
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
		.append("N.Cargo: "+getReferencia())
		.append("Fecha:",getFecha())
		.append("Importe:"+getImporteMN())
		.toString();
	}
}
