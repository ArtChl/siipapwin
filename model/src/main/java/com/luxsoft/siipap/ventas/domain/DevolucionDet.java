package com.luxsoft.siipap.ventas.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * Detalle sobre devoluciones
 * 
 * @author Ruben Cancino
 *
 */
public class DevolucionDet extends PersistentObject{
	
	private Devolucion devolucion;
	
	private VentaDet ventaDet;
	
	private Articulo articulo;
	
	private String clave;
	
	private double cantidad;
	
	/**
	 * Cantidad * ventaDet.precioReal
	 */
	private double importe;
	
	private int sucursal;
	
	private Long numero;
	
	private int factorDeConversionUnitaria=1000;
	
	private Date fecha;
	
	private Date fechaReal;
	
	private NotaDeCredito nota;
	
	private int renglon;
	
	private long cxcnumero;
	
	private String tipocxc;
	
	private int mes;
	
	private int year;
	
	private Date creado=currentTime();
	
	private int version;

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Devolucion getDevolucion() {
		return devolucion;
	}

	public void setDevolucion(Devolucion devolucion) {
		this.devolucion = devolucion;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		double old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
	}

	public VentaDet getVentaDet() {
		return ventaDet;
	}

	public void setVentaDet(VentaDet ventaDet) {
		this.ventaDet = ventaDet;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		DevolucionDet d=(DevolucionDet)obj;
		return false;
		/*return new EqualsBuilder()
		//.append(getDevolucion(),d.getDevolucion())
		//.append(getCreado(),d.getCreado())
		.isEquals();*/
	}

	@Override
	public int hashCode() {
		 
		return new HashCodeBuilder(17,35)
		//.append(getDevolucion())
		//.append(getCreado())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append(getClave())
		.append(getCantidad())
		.toString();
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getFactorDeConversionUnitaria() {
		return factorDeConversionUnitaria;
	}

	public void setFactorDeConversionUnitaria(int factorDeConversionUnitaria) {
		this.factorDeConversionUnitaria = factorDeConversionUnitaria;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public int getSucursal() {
		return sucursal;
	}

	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFechaReal() {
		return fechaReal;
	}

	public void setFechaReal(Date fechaReal) {
		this.fechaReal = fechaReal;
	}

	public NotaDeCredito getNota() {
		return nota;
	}

	public void setNota(NotaDeCredito nota) {
		this.nota = nota;
	}

	public int getRenglon() {
		return renglon;
	}

	public void setRenglon(int renglon) {
		this.renglon = renglon;
	}

	public long getCxcnumero() {
		return cxcnumero;
	}

	public void setCxcnumero(long cxcnumero) {
		this.cxcnumero = cxcnumero;
	}

	public String getTipocxc() {
		return tipocxc;
	}

	public void setTipocxc(String tipocxc) {
		this.tipocxc = tipocxc;
	}
	
	/**Propiedades no peersisteibles**/
	
	private double descuento;

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	
	public void actualizar(){
		double neto=getCantidad()*getVentaDet().getPrecioFacturado();
		setImporte(Math.abs(neto));		
	}
	
	/**
	 * Forma dinamica de obtener el importe
	 * 
	 * @return
	 */
	public CantidadMonetaria getImporteAsMoneda(){
		double val=getCantidad()*getVentaDet().getPrecioFacturado();
		return CantidadMonetaria.pesos(val);
		
	}
	/*
	public CantidadMonetaria getImporteNetoAsMoneda(){
		double val=getCantidad()*getVentaDet().getPrecioFacturado();
		CantidadMonetaria bruto=CantidadMonetaria.pesos(val);
		return bruto.divide(getDescuento());
		
	}
	*/

}
