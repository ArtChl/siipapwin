package com.luxsoft.siipap.cxc.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.dialect.FirebirdDialect;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.utils.domain.MutableObject;


/**
 * Descuento  a un cliente. Aplica a todas las ventas
 * siempre y cuando no exista un descuento mas especifico que este
 * 
 * 
 * 
 * @author Ruben Cancino
 *
 */
public  class AbstractDescuento extends MutableObject implements Descuento,Serializable{
	
	private Long id;
	
	/**
	 * El cliente beneficiario
	 */	
	@NotNull(message="Es Obligatorio seleccionar un Cliente")
	private Cliente cliente;
	
	/**
	 * Clave del cliente
	 */
	private String clave;
	
	/**
	 * Nombre del cliente
	 */
	private String nombre;
	
	/**
	 * Descripcion idenftificadore del descuenteo en Siipap (DBF)
	 */
	private String descSiipap="ND"; //Del DBF
	
	/**
	 * Monto del descuento
	 */
	//@Min(value=1 ,message="Descuentos menores al 1% no tienen sentido")
	private double descuento;
	
	/**
	 * Tipo de factura al que aplica (AB/CD/ENPX)
	 * Por compatibiliada con SIIPAP pero puede cambiar al momento de que el punto
	 * de ventas se migre a SiipapWin 
	 */
	private String tipoFac="E";
	
	/**
	 * Fecha en que se bloqueo este descuento
	 */
	private Date baja;
		
	/**
	 * 
	 */
	private boolean activo=true;
	
	/**
	 * Fecha en que se dio de alta el descuento
	 * 
	 */
	private Date alta=new Date();
	
	private Date modificado=new Date();
	
	private boolean precioNeto=false;
	
	
	
	/**
	 * Usuario que autorizo el descuento
	 */
	private String autorizo;
	
	/**
	 * 
	 */
	@Length(max=55,message="Longitud máxima para el comentario 55")
	private String comentario;
	
	
	private Date fechaAutorizacion=new Date();
	
	/**
	 * El descuento puede tener un monto adicional autorizado
	 * 
	 */
	private double adicional=0;
	
	
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
		Object old=this.cliente;
		this.cliente = cliente;
		getPropertyChangeSupport().firePropertyChange("cliente",old,cliente);
	}
	
	public String getDescSiipap() {
		return descSiipap;
	}
	public void setDescSiipap(String descSiipap) {		
		this.descSiipap = descSiipap;
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
	
	
	public String getTipoFac() {
		return tipoFac;
	}
	public void setTipoFac(String tipoFac) {
		this.tipoFac = tipoFac;
	}
	
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		double old=this.descuento;
		this.descuento = descuento;
		getPropertyChangeSupport().firePropertyChange("descuento",old,descuento);
	}
	
	
	
	public Date getAlta() {
		return alta;
	}
	public void setAlta(Date alta) {
		this.alta = alta;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		Object old=this.activo;
		this.activo = activo;
		getPropertyChangeSupport().firePropertyChange("activo", old, activo);
	}
	public String getAutorizo() {
		return autorizo;
	}
	public void setAutorizo(String autorizo) {
		this.autorizo = autorizo;
	}
	public Date getBaja() {
		return baja;
	}
	public void setBaja(Date baja) {
		this.baja = baja;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		Object old=this.comentario;
		this.comentario = comentario;
		propertyChangeSupport.firePropertyChange("comentario", old, comentario);
	}
	
	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}
	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}
	
	
	
	public double getAdicional() {
		return adicional;
	}
	public void setAdicional(double adicional) {
		this.adicional = adicional;
	}
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	protected boolean esMismoCliente(Venta v){
		return getClave().trim().equals(v.getClave().trim());
	}
	
	
	public boolean isPrecioNeto() {
		return precioNeto;
	}
	public void setPrecioNeto(boolean precioNeto) {
		this.precioNeto = precioNeto;
	}
	public CantidadMonetaria calcularDescuento(final Venta v){
		if(esMismoCliente(v) && isActivo()){
			if(getTipoFac().equals(v.getTipo())){
				double desc=getDescuento()/100;
				CantidadMonetaria imp=v.getImporteBruto().multiply(desc);
				if(getAdicional()!=0){
					CantidadMonetaria nvoImporte=v.getImporteBruto().subtract(imp);
					CantidadMonetaria adi=nvoImporte.multiply(getAdicional());
					imp=imp.add(adi);
				}
				return imp;
			}else
				return new CantidadMonetaria(0,v.getImporteBruto().currency());
			
		}
		return new CantidadMonetaria(0,v.getImporteBruto().currency());
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((cliente == null) ? 0 : cliente.hashCode());
		result = PRIME * result + ((tipoFac == null) ? 0 : tipoFac.hashCode());
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
		final AbstractDescuento other = (AbstractDescuento) obj;
		if (cliente == null) {
			if (other.cliente != null)
				return false;
		} else if (!cliente.equals(other.cliente))
			return false;
		if (tipoFac == null) {
			if (other.tipoFac != null)
				return false;
		} else if (!tipoFac.equals(other.tipoFac))
			return false;
		return true;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Cliente:",getCliente())
		.append("Desc SIIPAP:",getDescSiipap())
		.append("TipoFac:",getTipoFac())
		.append("Desc: ",getDescuento())
		.append("Adic: ",getAdicional())
		.append("Activo: ",isActivo())
		.append("PrecioNeto:",isPrecioNeto())
		.toString();
		
	}
	
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
	

}
