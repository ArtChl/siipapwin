package com.luxsoft.siipap.compras.domain;

import java.util.Date;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.utils.domain.MutableObject;

public class Requisicion extends MutableObject{
	
	private Long id;
	private int version;
	
	private Integer sucursal;
	private Date fecha;
	private MetodoDeEstimacion metodo;
	private Periodo periodo;
	private String comentario;
	
	private Date creado;
	private Date modificado;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getSucursal() {
		return sucursal;
	}
	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public MetodoDeEstimacion getMetodo() {
		return metodo;
	}
	public void setMetodo(MetodoDeEstimacion metodo) {
		this.metodo = metodo;
	}
	public Periodo getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
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
		result = prime * result + ((creado == null) ? 0 : creado.hashCode());
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
		final Requisicion other = (Requisicion) obj;
		if (creado == null) {
			if (other.creado != null)
				return false;
		} else if (!creado.equals(other.creado))
			return false;
		return true;
	}
	
	
	

}
