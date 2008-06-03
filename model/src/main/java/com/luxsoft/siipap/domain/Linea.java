package com.luxsoft.siipap.domain;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.luxsoft.siipap.annotations.UIProperty;
import com.luxsoft.utils.domain.MutableObject;

@Entity
public class Linea extends MutableObject{
	
	@UIProperty (readOnly=true, label="Id")
	private Long id;
	
	/**
	 * 
	 */
	@Length (min=0,max=40, message="longitud máxima es de 40 caracteres")
	@NotEmpty (message="El nombre de la lína no puede ser nulo")
	@UIProperty (label="Linea")
	private String nombre;
	
	@Length (min=0,max=255,message="la longitud  no puede ser mayor a 255")
	private String descripcion;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		String old=this.nombre;
		this.nombre = nombre;
		getPropertyChangeSupport().firePropertyChange("nombre", old, nombre);
	}
	
	public String toString(){
		/**
		return new ToStringBuilder(this
				,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Nombre",getNombre())
		.append("Desc",getDescripcion())
		.toString();
		**/
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
		final Linea other = (Linea) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		Object oldValue=this.descripcion;
		this.descripcion = descripcion;
		getPropertyChangeSupport().firePropertyChange("descripcion", oldValue, descripcion);
	}
	
	 
	

}
