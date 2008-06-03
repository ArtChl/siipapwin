package com.luxsoft.siipap.domain;

import javax.persistence.Entity;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.luxsoft.utils.domain.MutableObject;

@Entity
public class Marca extends MutableObject{
	
	private Long id;
	
	@Length (min=0,max=40, message="longitud máxima es de 40 caracteres")
	@NotEmpty (message=" no puede ser nulo")	
	private String nombre;
	
	public Marca(){
		
	}
	public Marca(String nombre){
		this.nombre=nombre;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(final String nombre) {
		Object oldValue=this.nombre;
		this.nombre = nombre;
		getPropertyChangeSupport().firePropertyChange("nombre", oldValue, nombre);
	}
	
	public String toString(){
		return getNombre();
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((nombre == null) ? 0 : nombre.hashCode());
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
		final Marca other = (Marca) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}
	
	

}
