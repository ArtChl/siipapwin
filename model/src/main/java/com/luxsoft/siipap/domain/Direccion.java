/*
 * Created on 27/10/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.domain;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.utils.domain.MutableObject;





/**
 * 
 * @author Ruben Cancino
 *
 * 
 */
@Embeddable
public class Direccion extends MutableObject{
	
	@NotNull
	@Length(max=50,message="Longitud Máxima 50")
	@Column(name="CALLE", length=50,nullable=false)
	private String calle="";
	
	@NotNull	
	@Length(max=10,message="Longitud Máxima 10")
	@Column(name="NUMERO",length=10,nullable=false)
	private String numero="";
	
	@NotNull
	@Length(max=10,message="Longitud Máxima 10")
	@Column(name="NUMEROEXT",length=10,nullable=false)
	private String numeroExterior="";
	
	@NotNull
	@Length(max=60)
	@Column(name="COLONIA",length=60,nullable=false)
	private String colonia="";
	
	
	@NotNull
	@Length(max=6)
	@Column(name="ZIP",length=6,nullable=false)
	private String cp="";
	
	@NotNull
	@Length(max=36)
	@Column(name="MUNICIPIO",length=36,nullable=false)
	private String municipio="";
	
	@NotNull
	@Length(max=40)
	@Column(name="CIUDAD",length=40,nullable=false)
	private String ciudad="";
	
	@NotNull
	@Length(max=20)
	@Column(name="ESTADO",length=20,nullable=false)
	private String estado="";
	
	
	
	@NotNull
	@Length(max=200)
	@Column(name="PAIS")
	private String pais="México";
	
		
	@Column(name="LOCALE")
	private Locale localidad=new Locale("es","mx");

	public Direccion (){
	}
	
	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		Object old=this.calle;
		this.calle = calle;
		getPropertyChangeSupport().firePropertyChange("calle", old, calle);
	}
	
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		Object old=this.ciudad;
		this.ciudad = ciudad;
		getPropertyChangeSupport().firePropertyChange("ciudad", old, ciudad);
	}
	
	public String getColonia() {
		return colonia;
	}
	public void setColonia(String colonia) {
		Object old=this.colonia;
		this.colonia = colonia;
		getPropertyChangeSupport().firePropertyChange("colonia", old, colonia);
	}
	
	public String getCp() {
		return cp;
	}
	public void setCp(final String cp) {
		Object old=this.cp;
		this.cp = cp;
		getPropertyChangeSupport().firePropertyChange("cp", old, cp);
	}
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		Object old=this.numero;
		this.numero = numero;
		getPropertyChangeSupport().firePropertyChange("numero", old, numero);
	}	
	
	public String getNumeroExterior() {
		return numeroExterior;
	}
	public void setNumeroExterior(String numeroExterior) {
		Object old=this.numeroExterior;
		this.numeroExterior = numeroExterior;
		getPropertyChangeSupport().firePropertyChange("numeroExterior", old, numeroExterior);
	}
	
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		Object old=this.pais;
		this.pais = pais;
		getPropertyChangeSupport().firePropertyChange("pais", old, pais);
	}
	
	public Locale getLocalidad() {
		return localidad;
	}
	public void setLocalidad(Locale localidad) {
		this.localidad = localidad;
	}

	
    
    @Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((calle == null) ? 0 : calle.hashCode());
		result = PRIME * result + ((ciudad == null) ? 0 : ciudad.hashCode());
		result = PRIME * result + ((colonia == null) ? 0 : colonia.hashCode());
		result = PRIME * result + ((cp == null) ? 0 : cp.hashCode());
		result = PRIME * result + ((estado == null) ? 0 : estado.hashCode());
		result = PRIME * result + ((localidad == null) ? 0 : localidad.hashCode());
		result = PRIME * result + ((municipio == null) ? 0 : municipio.hashCode());
		result = PRIME * result + ((numero == null) ? 0 : numero.hashCode());
		result = PRIME * result + ((numeroExterior == null) ? 0 : numeroExterior.hashCode());
		result = PRIME * result + ((pais == null) ? 0 : pais.hashCode());
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
		final Direccion other = (Direccion) obj;
		if (calle == null) {
			if (other.calle != null)
				return false;
		} else if (!calle.equals(other.calle))
			return false;
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
		if (cp == null) {
			if (other.cp != null)
				return false;
		} else if (!cp.equals(other.cp))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (localidad == null) {
			if (other.localidad != null)
				return false;
		} else if (!localidad.equals(other.localidad))
			return false;
		if (municipio == null) {
			if (other.municipio != null)
				return false;
		} else if (!municipio.equals(other.municipio))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (numeroExterior == null) {
			if (other.numeroExterior != null)
				return false;
		} else if (!numeroExterior.equals(other.numeroExterior))
			return false;
		if (pais == null) {
			if (other.pais != null)
				return false;
		} else if (!pais.equals(other.pais))
			return false;
		return true;
	}

	public String toString(){
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
        	.append("Calle: ",calle)
        	.append("No Int: ",numero)
			.append("No Ext:,",numeroExterior)
        	.append("Col: ",colonia)        	        	
        	.append("Mpo: ",municipio)
        	.append("Ciudad: ",ciudad)
        	.append("Estado: ",estado)        	
        	.append("Pais: ",getPais())
        	.append("CP: ",cp)
        	.toString();
    }
}	