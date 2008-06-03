package com.luxsoft.siipap.compras.alcances2;

import java.util.Date;

import com.luxsoft.siipap.domain.Articulo;

/**
 * Interfaz para obtener informacion realcionada con pedidos
 * de material a un proveedor especifico. Aisla la dependencia que el reporte de alcances
 * tiene con este tipo de informacion
 * 
 * 
 * @author Ruben Cancino
 *
 */
public interface PedidoInfo {
	
	public Articulo getArticulo();
	
	public String getDescripcionDelProveedor();
	
	public int getOrdenDeCompra();
	
	public String getDescripcion();
	
	public String getComentarios();
	
	public double getCantidadPedida();
	
	public double getCantidadRecibida();
	
	public Date getFechaDeEntrega();
	
	public Date getFecha();

}
