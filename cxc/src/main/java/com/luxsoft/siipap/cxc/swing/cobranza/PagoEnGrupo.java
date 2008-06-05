package com.luxsoft.siipap.cxc.swing.cobranza;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * 
 * Bean para encapsular el estado requerido para procesar pagos en grupo de facturas de credito 
 * 
 * @author Ruben Cancino
 *
 */
public class PagoEnGrupo extends Model{
	
	private String cliente="ddd";
	
	private String clave="";
	
	/**
	 * La fecha para todos los pagos  
	 */
	private Date fecha=new Date();
	
	private FormaDePago formaDePago=FormaDePago.H;
	
	private String referencia;
	
	private String banco;
	
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	
	private String comentario;
	
	private CantidadMonetaria saldo=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria saldoFacturas=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria porPagar=CantidadMonetaria.pesos(0);
	
	private List<Venta> ventas=new ArrayList<Venta>();
	
	private boolean aplicarDeMenos=false;
	
	private boolean condonarAbono=false;
	
	public PagoEnGrupo() {		
	}

	public PagoEnGrupo(String cliente, String clave, Date fecha, List<Venta> ventas) {		
		this.cliente = cliente;
		this.clave = clave;
		this.fecha = fecha;
		this.ventas = ventas;
	}
	
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		Object old=this.banco;
		this.banco = banco;
		firePropertyChange("banco", old, banco);
	}
	
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		Object old=this.comentario;
		this.comentario = comentario;
		firePropertyChange("comentario", old, comentario);
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		Object old=this.fecha;
		this.fecha = fecha;
		firePropertyChange("fecha", old, fecha);
	}
	
	public FormaDePago getFormaDePago() {
		return formaDePago;
	}
	public void setFormaDePago(FormaDePago formaDePago) {
		Object old=this.formaDePago;
		this.formaDePago = formaDePago;
		firePropertyChange("formaDePago", old, formaDePago);
	}
	
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		Object old=this.importe;
		this.importe = importe;
		firePropertyChange("importe", old, importe);
	}
	
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	
	public CantidadMonetaria getSaldo() {
		return saldo;
	}
	public void setSaldo(CantidadMonetaria saldo) {
		Object old=this.saldo;
		this.saldo = saldo;
		firePropertyChange("saldo", old, saldo);
	}
	
	public List<Venta> getVentas() {
		return ventas;
	}
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		Object old=this.clave;
		this.clave = clave;
		firePropertyChange("clave",old,clave);
	}
	
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		Object old=this.cliente;
		this.cliente = cliente;
		firePropertyChange("cliente", old, cliente);
	}
	
	
	public CantidadMonetaria getSaldoFacturas() {
		return saldoFacturas;
	}
	public void setSaldoFacturas(CantidadMonetaria saldoFacturas) {
		Object old=this.saldoFacturas;
		this.saldoFacturas = saldoFacturas;
		firePropertyChange("",old,saldoFacturas);
	}
	
	public void recalcular(){
		
		if(getVentas()==null || getVentas().isEmpty()) return;
		CantidadMonetaria iv=null;
		CantidadMonetaria pp=null;
		
		for(Venta v:getVentas()){
			final CantidadMonetaria ps=getPorPagar(v);
			v.setPago(ps);
			if(iv==null){
				iv=v.getSaldoEnMoneda();
				pp=ps;
				//pp=v.getSaldoEstimado();
				continue;
			}
			iv=iv.add(v.getSaldoEnMoneda());
						
			pp=pp.add(ps);
			
		}
		setSaldoFacturas(iv);
		setPorPagar(pp);
		setSaldo(getImporte().subtract(getPorPagar()));
		
	}
	
	public CantidadMonetaria getPorPagar(final Venta v){
		if(isCondonarAbono())
			return v.getSaldoEstimadoSinCargo();
		return v.getSaldoEstimado();
	}
	
	public double getDescuento(final Venta v){
		//if(v.getDescuento1()!=0)
			//return 0;
		if(isCondonarAbono())
			return v.getProvision().getDescuento1Real();
		return v.getProvision().getDescuentoFinal();
	}

	public boolean isAplicarDeMenos() {
		return aplicarDeMenos;
	}

	public void setAplicarDeMenos(boolean aplicarDeMenos) {
		boolean old=this.aplicarDeMenos;
		this.aplicarDeMenos = aplicarDeMenos;
		firePropertyChange("aplicarDeMenos", old, aplicarDeMenos);
	}

	public CantidadMonetaria getPorPagar() {
		return porPagar;
	}

	public void setPorPagar(CantidadMonetaria porPagar) {
		Object old=this.porPagar;
		this.porPagar = porPagar;
		firePropertyChange("porPagar", old, porPagar);
	}
	
	public CantidadMonetaria getImporteAOtrosPagos(){
		CantidadMonetaria mm =getImporte().subtract(getPorPagar());
		//CantidadMonetaria m=new CantidadMonetaria(mm.amount().abs().doubleValue(),mm.currency());
		return mm;
	}
	
	public Pago aplicarPago(final Venta v){
		Pago p=PagosFactory.getPago(v);
		p.setSucursal(v.getSucursal());
		p.setFormaDePago(getFormaDePago().getId());
		p.setDescFormaDePago(getFormaDePago().getDesc());
		p.setComentario(getComentario());
		p.setReferencia(getReferencia());
		p.setDescReferencia(getBanco());
		p.setFecha(getFecha());
		//Coordinar que el mes y año esten coordinados con la fecha
		int mes=Periodo.obtenerMes(getFecha())+1;
		int year=Periodo.obtenerYear(getFecha());
		p.setYear(year);
		p.setMes(mes);
		
		return p;
	}

	public boolean isCondonarAbono() {
		return condonarAbono;
	}

	public void setCondonarAbono(boolean condonarAbono) {
		boolean old=this.condonarAbono;
		this.condonarAbono = condonarAbono;
		firePropertyChange("condonarAbono", old, condonarAbono);
	}
	
	

}
