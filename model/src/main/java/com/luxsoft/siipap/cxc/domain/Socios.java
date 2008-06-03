package com.luxsoft.siipap.cxc.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;



import com.luxsoft.utils.domain.PersistentObject;


public class Socios extends PersistentObject {

	/**
	 * clave, se refiere a la clave del socio
	 * nombre,se refiere a el nombre del socio
	 * vendedor,se refiere al vendedor del socio
	 * lugar,se refiere a la razon social y la direccion
	 * bacoco
	 * bacove
	 */
	private String clave;
	private String nombre;
	private int vendedor;
	private String lugar;
	private double socbacoco;
	private double socbacove;
	

	
	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getSocbacoco() {
		return socbacoco;
	}

	public void setSocbacoco(double socbacoco) {
		this.socbacoco = socbacoco;
	}

	public double getSocbacove() {
		return socbacove;
	}

	public void setSocbacove(double socbacove) {
		this.socbacove = socbacove;
	}

	public int getVendedor() {
		return vendedor;
	}

	public void setVendedor(int vendedor) {
		this.vendedor = vendedor;
	}

	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!obj.getClass().isAssignableFrom(getClass())) return false;
		Socios other=(Socios)obj;
		return new EqualsBuilder()
			.append(clave,other.getClave())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(clave)
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append(getClave())
			.append(getNombre())			
			.toString();
		
	}

	
	
	
}
