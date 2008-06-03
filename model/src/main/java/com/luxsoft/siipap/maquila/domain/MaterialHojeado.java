package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.utils.domain.PersistentObject;

public abstract class MaterialHojeado extends PersistentObject{
	
	private Articulo articulo;
	private String clave;
	private String descripcion;
	private BigDecimal cantidad;
	private BigDecimal metros2=BigDecimal.ZERO;
	private BigDecimal kilos=BigDecimal.ZERO;
	private Date fecha=currentTime();
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	private String comentario;
	private Date creado=currentTime();
	private int version;
	
	
	
	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}
	public BigDecimal getCantidad() {
		return cantidad;
	}
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
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
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public BigDecimal getKilos() {
		return kilos;
	}
	public void setKilos(BigDecimal kilos) {
		this.kilos = kilos;
	}
	public BigDecimal getMetros2() {
		return metros2;
	}
	public void setMetros2(BigDecimal metros2) {
		this.metros2 = metros2;
	}
	
	
	public CantidadMonetaria getCosto() {
		return costo;
	}
	public void setCosto(CantidadMonetaria costo) {
		this.costo = costo;
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
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	
	
	

}
