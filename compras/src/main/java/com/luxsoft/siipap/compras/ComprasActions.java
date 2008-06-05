package com.luxsoft.siipap.compras;

import javax.swing.Action;

import org.springframework.util.StringUtils;

/**
 * Catalogo de acciones del modulo de Compras
 * 
 * @author Ruben Cancino
 *
 */
public enum ComprasActions {
	
	ShowRequisicionesView("Requisición de Productos")
	,ShowOrdenesView("Mantenimiento y consulta de ordnes de compra")
	,ShowOrdenesDeMaquilaView("Ordenes de Maquila")
	,ShowRecepcionesView("Recepción de compras y maquilas")	
	,ShowDevolucionesView("Devoluciones de compras")
	,CatalogoDeProveedores("Consulta y mantenimiento de proveedores")
	,CatalogoDePrecios("Consutla y mantenimiento de listas de precios")
	,CatalogoDeProductos("Consulta y mantenimiento de productos")
	,CatalogoDeLineas("Consulta y mantenimiento de Líneas de productos")
	,CatalogoDeClases("Consulta y mantenimiento de Clases de productos")
	,CatalogoDeMarcas("Consulta y mantenimiento de Marcas de productos")
	,ConsultasView("Consultas y reportes")
	,AltI("Consulta rápida de inventarios")
	,DepuracionDeOrdenes("Depuracion de O. Compra")
	;
	
	private final String descripcion;
	
	private ComprasActions(final String descripcion){
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
