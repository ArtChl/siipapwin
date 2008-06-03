package com.luxsoft.siipap.cxc.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.utils.domain.MutableObject;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentoPorVolumen extends MutableObject implements Descuento{
	
	private Long id;
	private Cliente cliente;
	private String clave;
	private String nombre;
	private String origen;	
	private Date baja;
	private boolean activo=true;
	private Date factivacion=new Date();
	
	//@Min(value=20000,message="Importe mínimo para descuentos $20,000.00")
	@NotNull
	private double maximo=20000.00;
	
	//@Min(value=1,message="Descuento por volumen  menor a 1% no tiene sentido")
	private double descuento;
	
	private int year;
	private int mes;
	private Date alta;
	
	public DescuentoPorVolumen(){
		Date d=new Date();
		year=Periodo.obtenerYear(d);
		mes=Periodo.obtenerMes(d)+1;
	}
	
	/**
	 * Usuario que autorizo el descuento
	 */
	private String autorizo;
	
	/**
	 * 
	 */
	private String comentario;
	
	
	private Date fechaAutorizacion;
	
	private Date creado=new Date();
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		boolean old=this.activo;
		this.activo = activo;
		getPropertyChangeSupport().firePropertyChange("activo", old,activo);
		setFactivacion(new Date());
	}
	
	public Date getFactivacion() {
		return factivacion;
	}
	
	private void setFactivacion(Date factivacion) {
		Object old=this.factivacion;
		this.factivacion = factivacion;
		getPropertyChangeSupport().firePropertyChange("factivacion", old, factivacion);
	}
	
	
	
	public Date getBaja() {
		return baja;
	}
	public void setBaja(Date baja) {
		this.baja = baja;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
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
	
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		double old=this.descuento;
		this.descuento = descuento;
		getPropertyChangeSupport().firePropertyChange("descuento",old,descuento);
	}
	
	public double getMaximo() {
		return maximo;
	}
	public void setMaximo(double maximo) {
		double old=this.maximo;
		this.maximo = maximo;
		getPropertyChangeSupport().firePropertyChange("maximo",old,maximo);
	}
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((creado == null) ? 0 : creado.hashCode());
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
		final DescuentoPorVolumen other = (DescuentoPorVolumen) obj;
		if (creado == null) {
			if (other.creado != null)
				return false;
		} else if (!creado.equals(other.creado))
			return false;
		return true;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Año",getYear())
		.append("Mes",getMes())
		.append("Activo",isActivo())
		.append("Maximo",getMaximo())
		.append("Descuento",getDescuento())	
		.append("Origen",getOrigen())
		.toString();
	}
	
	public String getAutorizo() {
		return autorizo;
	}
	public void setAutorizo(String autorizo) {
		this.autorizo = autorizo;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}
	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}
	public Date getAlta() {
		return alta;
	}
	public void setAlta(Date alta) {
		this.alta = alta;
	}
	
	

}
