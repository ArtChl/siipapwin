package com.luxsoft.siipap.cxc.domain;

import java.text.MessageFormat;
import java.util.Date;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;

import com.luxsoft.utils.domain.MutableObject;

public class Cobrador extends MutableObject{
	
	
	@NotNull 
	private int clave;	
	
	@Length (max=40)
	private String nombre;
	
	@Range (min=0, max=5)
	private double comision;
	
	private boolean activo=true;
	
	private Date baja;
	
	private Date alta;
	
	public Cobrador() {
	}

	public Date getAlta() {
		return alta;
	}
	public void setAlta(Date alta) {
		this.alta = alta;
	}

	public Date getBaja() {
		return baja;
	}
	public void setBaja(Date baja) {
		this.baja = baja;
	}

	public int getClave() {
		return clave;
	}
	public void setClave(int clave) {
		this.clave = clave;
	}

	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		double oldValue=this.comision;
		this.comision = comision;
		getPropertyChangeSupport().firePropertyChange("comision", oldValue, comision);
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + clave;
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
		final Cobrador other = (Cobrador) obj;
		if (clave != other.clave)
			return false;
		return true;
	}
	
	public String toString(){
		String patter="{0} ({1})";
		return MessageFormat.format(patter, getNombre(),getClave());
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		boolean oldValue=this.activo;
		this.activo = activo;
		getPropertyChangeSupport().firePropertyChange("activo", oldValue, activo);
	}
	
}
