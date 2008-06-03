package com.luxsoft.siipap.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

/**
 * Encapsula el estado y comportamiento de una autorizacion 
 * Habilitada para persistencia JPA para relaciones de composición
 * 
 * @author Ruben Cancino
 *
 */
@Embeddable
public class Autorizacion {
	
	@Column (name="FECHA_AUT")
	@Type (type="timestamp")
	private Date fechaAutorizacion;
	
	@Column (name="AUTORIZO", nullable=false, length=20)
	private String autorizazo;
	
	public Autorizacion(){}

	public Autorizacion(Date fechaAutorizacion, String autorizazo) {
		super();
		this.fechaAutorizacion = fechaAutorizacion;
		this.autorizazo = autorizazo;
	}

	public String getAutorizazo() {
		return autorizazo;
	}

	public void setAutorizazo(String autorizazo) {
		this.autorizazo = autorizazo;
	}

	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}

	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((autorizazo == null) ? 0 : autorizazo.hashCode());
		result = PRIME * result + ((fechaAutorizacion == null) ? 0 : fechaAutorizacion.hashCode());
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
		final Autorizacion other = (Autorizacion) obj;
		if (autorizazo == null) {
			if (other.autorizazo != null)
				return false;
		} else if (!autorizazo.equals(other.autorizazo))
			return false;
		if (fechaAutorizacion == null) {
			if (other.fechaAutorizacion != null)
				return false;
		} else if (!fechaAutorizacion.equals(other.fechaAutorizacion))
			return false;
		return true;
	}
	
	
	

}
