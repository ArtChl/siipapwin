package com.luxsoft.siipap.inventarios.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.InventarioMensual;

/**
 * Manager para el acceso y mantenimiento de informacion relacionada
 * con inventarios
 *  
 *  Facade
 * 
 * @author Ruben Cancino
 *
 */
public interface InventariosManager {
	
	
		
	/**
	 * Busca las entradas relacionadas con compras
	 * 
	 * @param p
	 * @return
	 */
	public List<AnalisisDeEntrada> buscarEntradasCom(final Periodo p,final String clave);
	
	/**
	 * Actualiza el inventario costeado mensual
	 * Toma en consideracion que el inicio del periodo es enero y que el primer año de
	 * operacion es el 2007
	 * 
	 * @param year
	 * @param mes
	 * @param clave
	 * @return
	 */
	public InventarioMensual actualizarInventario(final int year,final int mes,final String clave);
	
	/**
	 * Localiza el inventario mensual para el articulo indicado
	 * 
	 * @param year
	 * @param mes
	 * @param clave
	 * @return
	 */
	public InventarioMensual buscarInventario(final int year,final int mes, final String clave);
	
	/**
	 * 
	 * @return
	 */
	public Collection<String> buscarArticulos();
	
	
	/**
	 * Carga todos los registros del inventario inicial
	 * 
	 * @return
	 */
	public List<InventarioMensual> inventarioCosteado();
	
	
	/**
	 * FAC-RMD+XRM
	 * 
	 * @param clave
	 * @param p
	 * @return
	 */
	public BigDecimal ventasNetasUnidades(final String clave,final Periodo p);
	
	/**
	public void actualizarVentasNetas(InventarioMensual im);
	
	/**
	 * Busca posibles registros de InventarioMensual que tengan saldo <>0 y costo promedio=0
	 * para tratar de asignarles un costo
	 * 
	 * @param year
	 * @param mes
	 */	
	public void actualizarInventariosSospechosos(final int year,final int mes);

}
