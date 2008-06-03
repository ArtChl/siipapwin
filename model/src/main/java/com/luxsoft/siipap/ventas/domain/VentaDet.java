package com.luxsoft.siipap.ventas.domain;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.inventarios.domain.CostoPromedio;
import com.luxsoft.siipap.inventarios.domain.Movimiento;
import com.luxsoft.utils.domain.PersistentObject;

public class VentaDet extends PersistentObject{
	
	//Datos de la venta
	private Venta venta;
	
	private Integer sucursal;
	
	private String tipo;
	
	private String serie;
	
	private String tipoFactura;
	
	private Long numero;
	
	private Date fecha;
	
	//Datos del producto
	private Articulo articulo;
	
	private String clave;
	
	private String descripcion;
	
	private double kilos;
	
	private String unidad;
	
	private int factorDeConversionUnitaria=1000;
	
	private int renglon=1;
	
	private double cantidad;
	
	private double devueltos;
	
	/**
	 * Precio original en la lista de precios al momento de la venta
	 * 
	 */
	private double precioLista;	
	
	/**
	 * Precio unitario impreso en la factura
	 */
	private double precioFacturado;
	
	/**
	 * En ventas de contado(mostrador y camioneta) este es el precio neto definitivo y valido para el calculo de
	 * la utilidad.
	 * 
	 * En ventas de credito es el precio estimado segun los terminos definidos por el dpto de Credito para cada caso
	 * y es util para un estimado de la utilidad aplicando el descuento pactado.
	 */
	private double precioReal;
	
	/**
	 * Importe impreso en la factura
	 * 
	 * cantidad*precioLista
	 */
	private double importeBruto;
	
	/**
	 *  cantidad*precioFacturado
	 */
	private double importeNeto;
	
	
	/**
	 * cantidad*precioReal
	 */
	private double importeReal;
	
	/**
	 * Referencia a almace. TEMPORAL 
	 * @deprecated
	 */
	private Movimiento movimiento; 
	
	private boolean eliminado=false;
	
	private Date fechaReal;
	
	private int mes;
	private int year;
	
	private Date creado=currentTime();
	
	private int version;
	
	private CostoPromedio costoHistorico;
	
	private CostoPromedio costoActualizado;
	
	
	
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
	
	
	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}
	public int getFactorDeConversionUnitaria() {
		return factorDeConversionUnitaria;
	}
	public void setFactorDeConversionUnitaria(int factorDeConversionUnitaria) {
		this.factorDeConversionUnitaria = factorDeConversionUnitaria;
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	public double getImporteBruto() {
		return getPrecioLista()*getCantidad();
	}
	
	public void setImporteBruto(double importeBruto) {
		this.importeBruto = importeBruto;
	}
	public double getImporteNeto() {
		return getPrecioFacturado()*(getCantidad()+getDevueltos());
	}
	public double getImporteSemiNeto(){
		return getPrecioFacturado()*(getCantidad());
	}
	public void setImporteNeto(double importeNeto) {
		this.importeNeto = importeNeto;
	}
	
	public double getPrecioFacturado() {
		return precioFacturado;
	}
	public void setPrecioFacturado(double precioFacturado) {
		this.precioFacturado = precioFacturado;
	}
	public double getPrecioLista() {
		return precioLista;
	}
	public void setPrecioLista(double precioLista) {
		this.precioLista = precioLista;
	}
	public double getKilos() {
		return kilos;
	}
	public void setKilos(double kilos) {
		this.kilos = kilos;
	}
	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	
	
	public int getRenglon() {
		return renglon;
	}
	public void setRenglon(int renglon) {
		this.renglon = renglon;
	}
	
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public Integer getSucursal() {
		return sucursal;
	}
	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getTipoFactura() {
		return tipoFactura;
	}
	public void setTipoFactura(String tipoFactura) {
		this.tipoFactura = tipoFactura;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	
	
	
	
	/**
	 * 
	 * @return
	 * @deprecated
	 */
	public Movimiento getMovimiento() {
		return movimiento;
	}
	/**
	 * 
	 * @param movimiento
	 * @deprecated
	 */
	public void setMovimiento(Movimiento movimiento) {
		this.movimiento = movimiento;
	}
	public Venta getVenta() {
		return venta;
	}
	public void setVenta(Venta venta) {
		this.venta = venta;
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
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		VentaDet det=(VentaDet)obj;
		
		return new EqualsBuilder()
		.append(getSucursal(),det.getSucursal())
		.append(getTipo(),det.getTipo())
		.append(getNumero(),det.getNumero())
		.append(getSerie(),det.getSerie())
		.append(getTipoFactura(),det.getTipoFactura())
		.append(getRenglon(),det.getRenglon())
		.append(getCreado(),det.getCreado())
		.append(getId(), det.getId())
		.isEquals();
		
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getSucursal())
		.append(getTipo())
		.append(getNumero())
		.append(getSerie())
		.append(getTipoFactura())
		.append(getRenglon())
		.append(getCreado())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Docto:",getNumero())
		.append("Suc: "+getSucursal())
		.append(getClave())
		.append(getCantidad())
		.toString();
	}
	public double getImporteReal() {
		return importeReal;
	}
	public void setImporteReal(double importeReal) {
		this.importeReal = importeReal;
	}
	public double getPrecioReal() {
		return precioReal;
	}
	public void setPrecioReal(double precioReal) {
		this.precioReal = precioReal;
	}
	public boolean isEliminado() {
		return eliminado;
	}
	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}
	public double getDevueltos() {
		return devueltos;
	}
	public void setDevueltos(double devueltos) {
		this.devueltos = devueltos;
	}
	public Date getFechaReal() {
		return fechaReal;
	}
	public void setFechaReal(Date fechaReal) {
		this.fechaReal = fechaReal;
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
	public CostoPromedio getCostoActualizado() {
		return costoActualizado;
	}
	public void setCostoActualizado(CostoPromedio costoActualizado) {
		this.costoActualizado = costoActualizado;
	}
	public CostoPromedio getCostoHistorico() {
		return costoHistorico;
	}
	public void setCostoHistorico(CostoPromedio costoHistorico) {
		this.costoHistorico = costoHistorico;
	}	
	
	

}
