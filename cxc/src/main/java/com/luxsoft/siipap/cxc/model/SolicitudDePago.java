package com.luxsoft.siipap.cxc.model;

import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Bean para el procesamiento de pagos a ventas
 * 
 * @author Ruben Cancino
 *
 */
public class SolicitudDePago extends Model{
	
	/**
	{"venta.tipo","venta.numero","venta.fecha","venta.saldo"
			,"desc1","impDesc1","subTotal1"
			,"desc2","impDesc2","subTotal2"
			,"pago","saldo","otrosProds"
			,"notaCredito"};	
	**/
	
	private Venta venta;
	private double desc1;
	private double desc2;
	private CantidadMonetaria pago;
	private CantidadMonetaria otrosProductos;
	private CantidadMonetaria notaDeCredito;
	
	
	
	
	public double getDesc1() {
		return desc1;
	}
	public void setDesc1(double desc1) {
		this.desc1 = desc1;
	}
	public double getDesc2() {
		return desc2;
	}
	public void setDesc2(double desc2) {
		this.desc2 = desc2;
	}
	public CantidadMonetaria getNotaDeCredito() {
		return notaDeCredito;
	}
	public void setNotaDeCredito(CantidadMonetaria notaDeCredito) {
		this.notaDeCredito = notaDeCredito;
	}
	public CantidadMonetaria getOtrosProductos() {
		return otrosProductos;
	}
	public void setOtrosProductos(CantidadMonetaria otrosProductos) {
		this.otrosProductos = otrosProductos;
	}
	public CantidadMonetaria getPago() {
		return pago;
	}
	public void setPago(CantidadMonetaria pago) {
		this.pago = pago;
	}
	
	public void setPagoAsNumber(Number n){
		setPago(CantidadMonetaria.pesos(n.doubleValue()));
	}
	public Venta getVenta() {
		return venta;
	}
	public void setVenta(Venta venta) {
		this.venta = venta;
	}
	
	/** Calculados **/
	
	public CantidadMonetaria getSaldo(){
		return CantidadMonetaria.pesos(getVenta().getSaldo().doubleValue());
	}
	
	public CantidadMonetaria getImpDesc1(){
		if(getVenta()==null) return CantidadMonetaria.pesos(0);		
		return getSaldo().multiply(getDesc1());
	}
	
	public CantidadMonetaria getSubTotal1(){
		//return getSaldo().subtract(getImpDesc1());
		return getSaldo();
	}
	
	public CantidadMonetaria getImpDesc2(){
		if(getSubTotal1().amount().doubleValue()<=0) return CantidadMonetaria.pesos(0);
		return getSubTotal1().multiply(getDesc2());
	}
	
	public CantidadMonetaria getSubTotal2(){
		//return getSubTotal1().subtract(getSubTotal2());
		return getSaldo();
	}

}
