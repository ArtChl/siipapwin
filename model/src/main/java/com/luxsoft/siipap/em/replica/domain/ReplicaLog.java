package com.luxsoft.siipap.em.replica.domain;

import java.util.Date;
/**
 * Vista util:
 * 
 * create or replace view  V_REPLOCA as
 *	select entity,tabla,tipo,month,year,sum(beans)as BEANS,sum(registros) as REGISTROS from LX_REPLICALOG group by entity,tabla,tipo,month,year
 * 
 * @author RUBEN
 *
 */
public class ReplicaLog {
	
	private Long id;
	private String entity;
	private String tabla;
	private String tipo;
	private int month;
	private int year;
	private String periodo;
	private Date dia;
	private boolean fechado=true;
	private int registros;
	private int beans;
	private Date creado=new Date();
	
	public int getBeans() {
		return beans;
	}
	public void setBeans(int beans) {
		this.beans = beans;
	}
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public boolean isFechado() {
		return fechado;
	}
	public void setFechado(boolean fechado) {
		this.fechado = fechado;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getRegistros() {
		return registros;
	}
	public void setRegistros(int registros) {
		this.registros = registros;
	}
	
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public boolean validar(){
		return getRegistros()==getBeans();
		
	}
	
	/**
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((entity == null) ? 0 : entity.hashCode());
		result = PRIME * result + month;
		result = PRIME * result + ((tabla == null) ? 0 : tabla.hashCode());
		result = PRIME * result + ((tipo == null) ? 0 : tipo.hashCode());
		result = PRIME * result + year;
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
		final ReplicaLog other = (ReplicaLog) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (month != other.month)
			return false;
		if (tabla == null) {
			if (other.tabla != null)
				return false;
		} else if (!tabla.equals(other.tabla))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	**/
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Date getDia() {
		return dia;
	}
	public void setDia(Date dia) {
		this.dia = dia;
	}
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((dia == null) ? 0 : dia.hashCode());
		result = PRIME * result + ((entity == null) ? 0 : entity.hashCode());
		result = PRIME * result + ((tabla == null) ? 0 : tabla.hashCode());
		result = PRIME * result + ((tipo == null) ? 0 : tipo.hashCode());
		result = PRIME * result + year;
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
		final ReplicaLog other = (ReplicaLog) obj;
		if (dia == null) {
			if (other.dia != null)
				return false;
		} else if (!dia.equals(other.dia))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (tabla == null) {
			if (other.tabla != null)
				return false;
		} else if (!tabla.equals(other.tabla))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	
	

}
