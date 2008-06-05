package com.luxsoft.siipap.inventarios;

import javax.swing.Action;

import org.springframework.util.StringUtils;

/**
 * Catalogo de acciones del modulo de CXC
 * 
 * @author Ruben Cancino
 *
 */
public enum InvActions {
	
	MostrarConsultas("Mostrar Consultas"),
	ShowAnalisisDeMovimientosView("Análisis de Movimientos"),
	ShowXcoDecsView("Salidas XCO/DEC"),
	ShowAnalisisDeCostosView("Análisis de Costos"),
	ShowAnalisisDeCostosPorArticuloView("Análisis de costos por artículo"),
	ShowAnalisisDeCostosGlobal("Análisis de costos global"),
	ShowInventarioCosteado("Analisis de Invenrtario costeado"),
	ShowReportsView("Vista de reportes")
	,Clasificacion("Consulta y mantenimiento a las clasificaciones de movimientos")
	,MovimientosGenericos("Entradas y Salidas genericos")
	,TrasladoDeMaterial("Consulta y mantenimiento de traslados de material")
	,TransformacionDeMaterial("Transformacion de material")
	,InventarioView("Inventario de material")
	,RecepcionDeCompra("Recepcion de material comprado")
	,RecepcionDeMaquila("Recepcion de material cortado")
	,ConteoFisico("Consulta y mantenimiento al conteo de inventario ")
	,ShowInventarioDeMaquilaView("Consulta de análisi sobre el inventario de maquila")
	;
	
	
	private final String descripcion;
	
	private InvActions(final String descripcion){
		this.descripcion=descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public String getId() {
		return StringUtils.uncapitalize(name());
	}

	public void decorate(final Action action){
		action.putValue(Action.NAME, name());
		action.putValue(Action.SHORT_DESCRIPTION, getDescripcion());
		action.putValue(Action.LONG_DESCRIPTION, getDescripcion());
	}
	
	

}
