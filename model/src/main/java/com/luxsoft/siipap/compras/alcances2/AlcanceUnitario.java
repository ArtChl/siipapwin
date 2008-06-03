package com.luxsoft.siipap.compras.alcances2;

import java.util.Date;

import com.luxsoft.siipap.domain.Articulo;


/**
 * Modelo de dominio que proporciona y analiza informacion relacionada con la disponibilidad
 * de articulos para su venta
 * 
 * @author Ruben Cancino
 *
 */
public class AlcanceUnitario {
	
	private Articulo articulo;
		
	/** Datos estadisticos **/
	
	private double existencias;	
	
	private double pendientes;
	
	private double hojeado;
	
	private double porHojear;
	
	private double ventasMensuales;
	
	private Date creado=new Date();
	
	private ReporteDeAlcance alcance;

	public ReporteDeAlcance getAlcance() {
		return alcance;
	}

	public void setAlcance(ReporteDeAlcance alcance) {
		this.alcance = alcance;
	}	

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public double getExistencias() {
		return existencias;
	}

	public void setExistencias(double existencias) {
		this.existencias = existencias;
	}

	

	public double getPendientes() {
		return pendientes;
	}

	public void setPendientes(double pendientes) {
		this.pendientes = pendientes;
	}

	

	public double getHojeado() {
		return hojeado;
	}

	public void setHojeado(double hojeado) {
		this.hojeado = hojeado;
	}

	public double getPorHojear() {
		return porHojear;
	}

	public void setPorHojear(double porHojear) {
		this.porHojear = porHojear;
	}

	public double getVentasMensuales() {
		return ventasMensuales;
	}

	public void setVentasMensuales(double ventasMensuales) {
		if(ventasMensuales<=0)
			throw new IllegalArgumentException("Las ventas mensuales deben ser >0");
		this.ventasMensuales = ventasMensuales;
	}
	
	/**
	 * Obtiene el alcance del inventario segun las ventas
	 * mensuales
	 * 
	 * @return Los meses con disponibilidad
	 */
	public double getAlcanceInventario(){		
		return getExistencias()/getVentasMensuales();
	}

	/**
	 * Obtiene el alcance de los pedidos pendientes segun 
	 * las ventas mensuales
	 *  
	 * @return 
	 */
	public double getAlcancePedidos(){		
		return getPendientes()/getVentasMensuales();
	}
	
	/**
	 * Regresa el alcance del inventario hojeado de maquila
	 * segun las ventas mensuales
	 * 
	 * @return
	 */
	public double getAlcanceHojeado(){		
		return getHojeado()/getVentasMensuales();
	}
	
	/**
	 * Regresa el alcance del inventario por hojear de maquila
	 * segun las ventas mensuales
	 * 
	 * @return
	 */
	public double getAlcancePorHojear(){
		return getPorHojear()/getVentasMensuales();
	}
	
	/**
	 * Regresa el alcance estimado total segun las ventas mensuales
	 * @return
	 */
	public double getAlcanceEstimadoTotal(){
		return getAlcanceInventario()
		+getAlcancePedidos()
		+getAlcanceHojeado()
		+getAlcancePorHojear();
	}

}
