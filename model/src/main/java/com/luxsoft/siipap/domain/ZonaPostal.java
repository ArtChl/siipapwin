package com.luxsoft.siipap.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;

import com.luxsoft.utils.domain.MutableObject;

@Entity
@Table (name="SW_ZONAS")
public class ZonaPostal extends MutableObject{
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)	
	@Column(name="ZONA_ID")	
	private Long id;
	
	@Length(min=6,max=6)
	private String zip;
	
	@Length(max=60)
	private String colonia;
	
	@Length(max=36)
	private String municipio;
	
	@Length(max=20)
	private String estado;
	
	@Length(max=40)
	private String ciudad;
	
	

	public ZonaPostal(String zip, String colonia, String municipio, String estado, String ciudad) {
		super();
		this.zip = zip;
		this.colonia = colonia;
		this.municipio = municipio;
		this.estado = estado;
		this.ciudad = ciudad;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getColonia() {
		return colonia;
	}

	public void setColonia(String colonia) {
		this.colonia = colonia;
	}	

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getZip())
		.append(getColonia())
		.append(getMunicipio())
		.append(getCiudad())
		.append(getEstado())
		.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((ciudad == null) ? 0 : ciudad.hashCode());
		result = PRIME * result + ((colonia == null) ? 0 : colonia.hashCode());
		result = PRIME * result + ((estado == null) ? 0 : estado.hashCode());
		result = PRIME * result + ((municipio == null) ? 0 : municipio.hashCode());
		result = PRIME * result + ((zip == null) ? 0 : zip.hashCode());
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
		final ZonaPostal other = (ZonaPostal) obj;
		if (ciudad == null) {
			if (other.ciudad != null)
				return false;
		} else if (!ciudad.equals(other.ciudad))
			return false;
		if (colonia == null) {
			if (other.colonia != null)
				return false;
		} else if (!colonia.equals(other.colonia))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (municipio == null) {
			if (other.municipio != null)
				return false;
		} else if (!municipio.equals(other.municipio))
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	
	
	

}
