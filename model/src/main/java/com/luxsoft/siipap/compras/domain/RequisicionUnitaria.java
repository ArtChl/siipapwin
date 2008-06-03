package com.luxsoft.siipap.compras.domain;

import java.math.BigDecimal;

import com.luxsoft.utils.domain.MutableObject;

public class RequisicionUnitaria extends MutableObject{
	
	private String clave;
	private String descripcion;
	private BigDecimal sugerido=BigDecimal.ZERO;
	
	
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public BigDecimal getSugerido() {
		return sugerido;
	}
	public void setSugerido(BigDecimal sugerido) {
		this.sugerido = sugerido;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clave == null) ? 0 : clave.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RequisicionUnitaria other = (RequisicionUnitaria) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
	
		

}
