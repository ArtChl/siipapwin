package com.luxsoft.siipap.cxc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Diferentes metodos para filtrar informacion
 * 
 * 
 * @author Ruben Cancino 
 *
 */
public class CXCFiltros {
	
	
	
	
	/**
	 * Filtra la collecion de ventas para que solo tenga elementos de un tipo
	 * 
	 *  
	 * @param ventas
	 */
	public static void filtrarVentasParaUnTipo(final Collection<Venta> ventas,final String tipo){		
		CollectionUtils.filter(ventas, new Predicate(){
			public boolean evaluate(Object object) {
				Venta v=(Venta)object;				
				return tipo.equals(v.getTipo());
			}			
		});
	}
	
	
	/**
	 * Filtra las ventas que califican para pagos automaticos
	 * es decir con saldo <=100 pesos
	 * Si la colleccion tiene ventas de tipo E y S, estas son filtradas 
	 * 
	 * 
	 * @param ventas
	 */
	public static void filtrarParaPagoAutomatico(Collection<Venta> ventas) {		
		CollectionUtils.filter(ventas, new Predicate(){
			public boolean evaluate(Object object) {
				Venta v=(Venta)object;				
				return 
					//v.isProvisionable()
					//&& 
					v.getSaldo().doubleValue()<=PagosFactory.TOLERANCIA_AUTOMATICA.doubleValue()
					&& v.getSaldo().doubleValue()>0;
			}			
		});		
	}
	
	/**
	 * Elimina de la coleccion las ventas con saldo 0
	 * 
	 * @param ventas
	 */
	public static void filtrarVentasConSaldo(Collection<Venta> ventas){
		CollectionUtils.filter(ventas, new Predicate(){
			public boolean evaluate(Object object) {
				Venta v=(Venta)object;				
				return v.getSaldo().abs().doubleValue()>0;
			}			
		});
	}
	
	/**
	 * Elimina de la coleccion los cargos con saldo 0
	 * 
	 * @param ventas
	 */
	public static void filtrarNotasDeCargoConSaldo(Collection<NotaDeCredito> cargos){
		CollectionUtils.filter(cargos, new Predicate(){
			public boolean evaluate(Object object) {
				NotaDeCredito n=(NotaDeCredito)object;				
				return n.getSaldoDelCargo()>0;
			}			
		});
	}
	
	/**
	 * Filtra las notas de cargo que califican para pagos automaticos
	 * es decir con saldo <=100 pesos
	 * Si la colleccion tiene ventas de tipo E y S, estas son filtradas 
	 * 
	 * 
	 * @param ventas
	 */
	public static void filtrarParaPagoDeNotasDeCargoAutomatico(Collection<NotaDeCredito> cargos) {		
		CollectionUtils.filter(cargos, new Predicate(){
			public boolean evaluate(Object object) {
				NotaDeCredito n=(NotaDeCredito)object;				
				return n.getSaldoDelCargo()<=5;
			}			
		});		
	}
	
	public static void filtrarParaUnMismoCliente(Collection<Venta> ventas){
		
	}
	
	/**
	 * Elimina de la coleccion los cargos con saldo 0
	 * 
	 * @param ventas
	 */
	public static void filtrarCargosConSaldo(Collection<NotaDeCredito> cargos){
		CollectionUtils.filter(cargos, new Predicate(){
			public boolean evaluate(Object object) {
				NotaDeCredito n=(NotaDeCredito)object;				
				return Math.abs(n.getSaldoDelCargo())>0;
			}			
		});
	}
	
	public static void filtrarCargosMismoCliente(Collection<NotaDeCredito> cargos){
		if(cargos.isEmpty())
			return;
		final List<NotaDeCredito> miscargos=new ArrayList<NotaDeCredito>();
		miscargos.addAll(cargos);
		final String clave=miscargos.get(0).getClave();
		CollectionUtils.filter(miscargos, new Predicate(){
			public boolean evaluate(Object object) {
				NotaDeCredito n=(NotaDeCredito)object;				
				return n.getClave().equals(clave);
			}			
		});
	}
	
	/**
	 * Filtra las ventas que califican para pagos automaticos
	 * es decir con saldo <=100 pesos
	 * Si la colleccion tiene ventas de tipo E y S, estas son filtradas 
	 * 
	 * 
	 * @param ventas
	 */
	public static void filtrarParaDiferenciaCambiara(Collection<Venta> ventas) {		
		CollectionUtils.filter(ventas, new Predicate(){
			public boolean evaluate(Object object) {
				Venta v=(Venta)object;				
				return (v.getTipo().equals("X") || v.getTipo().equals("M"));
			}			
		});		
	}

}
