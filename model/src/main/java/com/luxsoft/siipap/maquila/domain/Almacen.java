package com.luxsoft.siipap.maquila.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Direccion;
import com.luxsoft.utils.domain.PersistentObject;


public class Almacen extends PersistentObject implements Comparable{
	
	private Maquilador maquilador;
	private String nombre; 
	private Direccion direccion=new Direccion();
	private String telefono1;
	private String telefono2;
	private String fax;
	private boolean matriz=false;
	
	public Almacen(){
		
	}
	
	public Almacen(Maquilador maquilador){
		setMaquilador(maquilador);
	}
	
	public Direccion getDireccion() {
		return direccion;
	}
	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public Maquilador getMaquilador() {
		return maquilador;
	}
	public void setMaquilador(Maquilador maquilador) {
		this.maquilador = maquilador;
	}
	public boolean isMatriz() {
		return matriz;
	}
	public void setMatriz(boolean matriz) {
		this.matriz = matriz;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTelefono1() {
		return telefono1;
	}
	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}
	public String getTelefono2() {
		return telefono2;
	}
	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!obj.getClass().isAssignableFrom(obj.getClass())) return false;
		Almacen other=(Almacen)obj;
		return new EqualsBuilder()
			.append(maquilador,other.getMaquilador())
			.append(nombre,other.getNombre())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(maquilador)
			.append(nombre)
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getNombre())
		//.append(getMaquilador().getClave())
		.toString();
	}

	public int compareTo(Object o) {
		if(o ==null)return -1;
		if(o==this) return 0;
		Almacen a=(Almacen)o;
		return getNombre().compareTo(a.getNombre());
	}	
	
	

}
