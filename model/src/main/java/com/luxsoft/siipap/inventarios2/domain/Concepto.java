package com.luxsoft.siipap.inventarios2.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;

import com.luxsoft.utils.domain.MutableObject;

/**
 * Concepto contables para los movimientos de inventario
 * 
 * @author Ruben Cancino
 *
 */
@Entity
@Table (name="SW_CONCEPTOS")
public class Concepto extends MutableObject{
	
	@Id  @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	/**
	 * Tipo de movimiento Entrada/Salida (E/S)
	 */
	@Length(min=1, max=1)
	private String tipo;
	
	/**
	 * Clave del movimiento
	 */
	@Length (min=3,max=3, message="La clave debe ser de 3 posiciones")	
	private String clave;
	
	/**
	 * 
	 */
	@Length (max=255, message="La descripción del concepto es de maximo 70 caracteres")	
	private String descripcion;
	
	
	@Length (max=15)	
	private String cuenta;	
	
	
	private boolean automatico=false;
	
	private Date creado=new Date();
	
	private Date modificado;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}


	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}


	public boolean isAutomatico() {
		return automatico;
	}
	public void setAutomatico(boolean automatico) {
		this.automatico = automatico;
	}
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clave == null) ? 0 : clave.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
		final Concepto other = (Concepto) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(clave)
		.append("(")
		.append(tipo)
		.append(")")
		.toString();
	}
	

}
