package com.luxsoft.siipap.ventas.dao;

import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public interface VentasDao {
	
	public void salvar(Venta venta);
	
	public void sincronizar(Venta v);
	
	public void eliminar(Long id);
	
	public void eliminar(Venta venta);	 
	
	public Venta buscarPorId(Long id);
	
	public void inicializarPartidas(final Venta v);
	
	public int salvarVentas(final Collection<Venta> ventas);	
	
	
	public Venta buscarVenta(final Integer sucursal,final String serie,final String tipo,final long numero);
	
	public Venta buscarVenta(final Integer sucursal ,final String tipo,final long numero);
	
	public List<Venta> buscarVentasPorCliente(final String clave,int year,int mes);
	
	public List<VentaDet> buscarVentasDetPorCliente(final String clave,final Periodo p);
	
	/**
	 * Busca todas las ventas de credito de un cliente a partir del 2007 y/o
	 * con saldo del  2006
	 * 
	 * @param clave
	 * @return
	 */
	public List<Venta> buscarVentasCreditoPorCliente(final String clave);
	
	/**
	 * Busca las ventas de credito para un cliente en el año y mes indicado
	 * 
	 * @param clave
	 * @param year
	 * @param mes
	 * @return
	 */
	public List<Venta> buscarVentasCreditoPorCliente(final String clave,int year,int mes);
	
	/**
	 * Busca las ventas de credito para un cliente para el periodo indicado
	 * 
	 * @param clave
	 * @param periodo
	 * @return
	 */
	public List<Venta> buscarVentasCreditoPorCliente(final String clave,final Periodo periodo);
	
	
	
	
	public List<Venta> buscarVentasConSaldo(final String clave);
	
	public List<Venta> buscarVentasCreditoConSaldo(final String clave);
	
	/**
	 * Calcula el acumulado del mes para el cliente determinado (En pesos)
	 * 
	 * @param cliente
	 * @param year
	 * @param mes
	 * @return
	 */
	public CantidadMonetaria acumuladoDelMesCredito(String cliente,int year,int mes);
	
	/**
	 * Calcula el acumulado del mes para el cliente determinado 
	 * 
	 * @param cliente
	 * @param year
	 * @param mes
	 * @return
	 */
	public CantidadMonetaria acumuladoDelMesCredito(String cliente,int year,int mes,Currency moneda);
	
	/**
	 * Acumulado de ventas para un grupo de clientes conocido como corporativo
	 * @param cliente
	 * @param year
	 * @param mes
	 * @return
	 */
	public CantidadMonetaria acumuladoDelMesPorCorporativo(String cliente,int year,int mes);
	
	/**
	 * Regresa una lista de las ventas a credito por revisar en el dia determinado y con saldo>0
	 * 
	 *  
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Venta> buscarVentasParaRevisar(final Date fecha);
	
	
	/**
	 * Regresa una lista de las ventas a credito por cobrar en el dia determinado y con saldo>0
	 * 
	 *  
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Venta> buscarVentasParaCobrar(final Date fecha);
	
	/**
	 * Busca las ventas acumuladas del año
	 * 
	 * @return
	 */
	public List<Venta> buscarVentasYTD(final Cliente c);
	
	public List<Long> buscarVentasIdsConSaldo();
	
	public List<Venta> buscarVentas(final Date dia);
	
	
	/**
	 * 
	 * @param cliente
	 * @param periodo
	 * @return
	 */
	//public CantidadMonetaria acumuladoDelCliente(String cliente,Periodo periodo);
	
	/**
	 * 
	 * @param cliente
	 * @param periodo
	 * @param credito
	 * @return
	 */
	//public CantidadMonetaria acumuladoDelCliente(String cliente,Periodo periodo,boolean credito);
	
	
	
}
