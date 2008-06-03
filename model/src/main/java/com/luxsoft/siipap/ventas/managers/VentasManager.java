package com.luxsoft.siipap.ventas.managers;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaACredito;

/**
 * Interfaz Manager para la administracion de ventas
 * La implementación debe contemplar el manejo de transacciones
 * para mantener la integridad
 * 
 * El manejo y administracion de ventas implica modificaciones en varias tablas
 * por lo que la implementacion debe controlar de manera adecuada las transacciones 
 * de bases de datos
 * 
 * @author Ruben Cancino
 *
 */
public interface VentasManager {
	
	
	/**
	 * Re-carga la venta con la base de datos
	 * 
	 * @param v
	 */
	public void refresh(final Venta v);

	/**
	 * Punto central para salvar ventas
	 * 
	 * @param v
	 */
	public void actualizarVenta(final Venta v);
	
	/**
	 * Actualiza la venta de id indicado
	 * @param id
	 */
	public void actualizarVenta(Long id);
	
	/**
	 * Elimina una venta 
	 * 
	 * @param id
	 */
	public void eliminarVenta(Long id);
	
	/**
	 * Elimina el bean {@link VentaACredito} y el bean {@link Provision} vinculados on la venta
	 * 
	 * @param v
	 */
	public void eliminarVentaCredito(final Venta v);
		
	
	/**
	 * Regresa una lista con los ids de las ventas a credito con saldo
	 * 
	 * @return
	 */
	public List<Long> getListaDeVentasACreditoConSaldo();
		
	/**
	 * Regresa un arreglo de dos listas de ventas una para cobro y otra para revision
	 * 
	 * @return
	 */
	public List<Venta>[] buscarCobranzaDelDia(final Date dia);
	
	/**
	 * Busca toda la cuenta por cobrar
	 * @return
	 */
	public List<Venta> buscarCuentasPorCobrar();
	
	
	
	/**
	 * Regresa el delegado DAO responsable de las ventas
	 * @return
	 */
	public VentasDao getVentasDao();
	
	/**
	 * Actualiza en todas las ventas provisionables
	 *
	 */
	public void actualizarVentas();
	
	
	/**
	 * Aplica la provision de manera inmediata
	 * 
	 * @param v
	 */
	public void aplicarProvision(final Venta v);
	
	/**
	 * 
	 * @param c
	 */
	public void actualizarVentasYtd(final Cliente c);
	
	
	/**
	 * Localiza todas las ventas del periodo con saldo y de tipo CRE
	 * 
	 * @param p
	 * @return
	 */
	public List<Venta> buscarCuentasPorCobrarCre(final Periodo p);
	
	
	
	

}