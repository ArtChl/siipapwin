/*
 * Created on 21-abr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Sucursal;

/**
 * 
 * @author Ruben Cancino 
 */
public interface SucursalDao {
	
	public Sucursal crearSucursal(Sucursal sucursal);
	public Sucursal actualizarSucursal(Sucursal sucursal);
	public Sucursal buscarSucursal(int clave);
	public void eliminaSucursal(Sucursal sucursal);
	
	public int contarSucursales();
	public List buscarSucursales();

}
