package com.luxsoft.siipap.cxc.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.hibernate.validator.Max;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.utils.domain.MutableObject;

/**
 * Desceunto  que se puede aplica posterior a un descuento fijo por cliente 
 * o a un descuento por volumen (En cascada) o bien como un descuento fijo especial
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentoEspecial extends MutableObject implements Descuento{
	
	
	private Long id;
	
	/**
	 * 
	 */
	@NotNull (message="No puede ser nulo")
	private ClienteCredito cliente;
	
	/**
	 * La venta en cuestion
	 * 
	 */
	@NotNull (message="No puede ser nulo")	
	private Venta venta;
	
		
	/**
	 * Quien autorizo
	 */
	@Length (max=35)
	@NotNull
	private String autorizo;
	
	/**
	 * 
	 */
	@Length (max=80)
	private String comentario;
	
	/**
	 * 
	 */
	private Date fechaAutorizacion;
	
	
	private double descuento;
	


	private Date creado;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAutorizo() {
		return autorizo;
	}
	public void setAutorizo(String autorizo) {
		Object oldValue=this.autorizo;
		this.autorizo = autorizo;
		getPropertyChangeSupport().firePropertyChange("autorizo", oldValue, autorizo);
	}

	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		Object oldValue=this.comentario;
		this.comentario = comentario;
		getPropertyChangeSupport().firePropertyChange("comentario", oldValue, comentario);
	}

	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		double oldValue=this.descuento;
		this.descuento = descuento;
		getPropertyChangeSupport().firePropertyChange("descuento", oldValue, descuento);
	}

	public Date getFechaAutorizacion() {		
		return fechaAutorizacion;
	}

	public void setFechaAutorizacion(Date fechaAutorizacion) {
		Object oldValue=this.fechaAutorizacion;
		this.fechaAutorizacion = fechaAutorizacion;
		getPropertyChangeSupport().firePropertyChange("fechaAutorizacion", oldValue, fechaAutorizacion);
	}	

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		Object oldValue=this.venta;
		this.venta = venta;
		getPropertyChangeSupport().firePropertyChange("venta", oldValue, venta);
	}
	
	public CantidadMonetaria getImporte(){
		if(getVenta()!=null){
			CantidadMonetaria monto=getVenta().getTotal();
			return monto.multiply(getDescuento()).divide(100);
		}
		return CantidadMonetaria.pesos(0);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((venta == null) ? 0 : venta.hashCode());
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
		final DescuentoEspecial other = (DescuentoEspecial) obj;
		if (venta == null) {
			if (other.venta != null)
				return false;
		} else if (!venta.equals(other.venta))
			return false;
		return true;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
		.append("Venta ",getVenta().getNumero())
		.append("Desc ",getDescuento())		
		.append("Autorizo",getAutorizo())
		.append(getComentario())
		.toString();
	}

	

	public ClienteCredito getCliente() {
		return cliente;
	}

	public void setCliente(ClienteCredito cliente) {
		Object oldValue=this.cliente;
		this.cliente = cliente;
		getPropertyChangeSupport().firePropertyChange("cliente", oldValue, cliente);
	}

	
}
