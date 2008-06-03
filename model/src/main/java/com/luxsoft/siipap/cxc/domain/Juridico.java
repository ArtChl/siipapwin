package com.luxsoft.siipap.cxc.domain;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * Informacion relacionada con cuentas por cobrar con problemas de cobranza 
 * 
 * La informacion se importa de la tabla: MOVJUR
 * 
 * Y se puede relacionar con cargos pagos o abonos a facturas
 *  
 * @author Ruben Cancino
 *
 */
public class Juridico extends PersistentObject{
	
	private NotaDeCredito nota;
	private Venta venta;
	private ChequeDevuelto cheque;
	
	private String origen;
	private int claveAbogado;
	private String nombreAbogado;
	private Date fechaTraspaso;
	private String comentarios;
	private double saldoDoc; 
	private int year;
	private int mes;
	
	private Date creado=new Date();

	public int getClaveAbogado() {
		return claveAbogado;
	}

	public void setClaveAbogado(int claveAbogado) {
		this.claveAbogado = claveAbogado;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public Date getFechaTraspaso() {
		return fechaTraspaso;
	}

	public void setFechaTraspaso(Date fechaTraspaso) {
		this.fechaTraspaso = fechaTraspaso;
	}

	public String getNombreAbogado() {
		return nombreAbogado;
	}

	public void setNombreAbogado(String nombreAbogado) {
		this.nombreAbogado = nombreAbogado;
	}

	public NotaDeCredito getNota() {
		return nota;
	}

	public void setNota(NotaDeCredito nota) {
		this.nota = nota;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public double getSaldoDoc() {
		return saldoDoc;
	}

	public void setSaldoDoc(double saldoDoc) {
		this.saldoDoc = saldoDoc;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof Juridico)) {
			return false;
		}
		Juridico rhs = (Juridico) object;
		return new EqualsBuilder()
		.append(this.getId(), rhs.getId())		
		.isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(1126325853, -384620877)
		.append(getId())
		.toHashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this).append("origen", this.origen).append(
				"nota", this.nota).append("saldoDoc", this.saldoDoc).append(
				"id", this.getId()).append("nombreAbogado", this.nombreAbogado)
				.append("comentarios", this.comentarios).append("claveAbogado",
						this.claveAbogado).append("propertyChangeSupport",
						this.getPropertyChangeSupport()).append("creado",
						this.creado)
				.append("fechaTraspaso", this.fechaTraspaso).toString();
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public ChequeDevuelto getCheque() {
		return cheque;
	}

	public void setCheque(ChequeDevuelto cheque) {
		this.cheque = cheque;
	}
	
	
	
	
}
