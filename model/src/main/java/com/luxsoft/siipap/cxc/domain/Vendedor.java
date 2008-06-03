package com.luxsoft.siipap.cxc.domain;



import java.util.Date;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;

import com.luxsoft.siipap.domain.Direccion;
import com.luxsoft.utils.domain.MutableObject;

public class Vendedor extends MutableObject {

	private long clave;
	
	@Length (max=70)
	private String nombre;
	
	@Email
	private String email;
	
	private Direccion direccion;
	
	private Date creado;
	
	private Date modificado;
	
	private boolean activo=true;
	
	public long getClave() {
		return clave;
	}
	public void setClave(long clave) {
		this.clave = clave;
	}
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	public Direccion getDireccion() {
		return direccion;
	}
	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
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
		result = PRIME * result + (int) (clave ^ (clave >>> 32));
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
		final Vendedor other = (Vendedor) obj;
		if (clave != other.clave)
			return false;
		return true;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		boolean oldValue=this.activo;
		this.activo = activo;
		getPropertyChangeSupport().firePropertyChange("activo", oldValue, activo);
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
	
}
