package com.luxsoft.siipap.maquila.domain;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Direccion;
import com.luxsoft.utils.domain.PersistentObject;


public class Maquilador extends PersistentObject{
	
	private String clave;	
	private String nombre;
	private Direccion direccion=new Direccion();
	private String telefono1;
	private String telefono2; 
	private String fax;
	private String rfc="";
	private int diasDeCredito; //Dias de plazo para pago,Dias de credito que ofrece el proveedor
	private int vencimientoEstipulado; // A partid de la fecha de revision o a partir de la fecha de factura
	private String observaciones;
	private String representante;
	private String cuentaContable;
	private CantidadMonetaria tarifa;
	private boolean activo;
	
	private Set<Almacen> almacenes;
	
	public Maquilador(){
	}
	
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getCuentaContable() {
		return cuentaContable;
	}
	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}
	public int getDiasDeCredito() {
		return diasDeCredito;
	}
	public void setDiasDeCredito(int diasDeCredito) {
		this.diasDeCredito = diasDeCredito;
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
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getRepresentante() {
		return representante;
	}
	public void setRepresentante(String representante) {
		this.representante = representante;
	}
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
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
	public int getVencimientoEstipulado() {
		return vencimientoEstipulado;
	}
	public void setVencimientoEstipulado(int vencimientoEstipulado) {
		this.vencimientoEstipulado = vencimientoEstipulado;
	}
	
	public CantidadMonetaria getTarifa() {
		return tarifa;
	}

	public void setTarifa(CantidadMonetaria tarifa) {
		this.tarifa = tarifa;
	}

	public Set<Almacen> getAlmacenes() {
		if(almacenes==null){
			setAlmacenes(new HashSet<Almacen>());
		}
		return almacenes;
	}
	
	public void setAlmacenes(Set<Almacen> almacenes) {
		this.almacenes = almacenes;
	}
	
	public void addAlmacen(final Almacen almacen){
		almacen.setMaquilador(this);
		getAlmacenes().add(almacen);
	}
	public void removeAlmacen(final Almacen almacen){
		getAlmacenes().remove(almacen);
	}
	
	/**
	 * Small fix para garantizar que los almacenes y el maquilador mantengan su relacion bidireccional
	 *
	 */
	public void confirmAlmacenes(){
		for(Almacen a:getAlmacenes()){
			a.setMaquilador(this);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!obj.getClass().isAssignableFrom(getClass())) return false;
		Maquilador other=(Maquilador)obj;
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
