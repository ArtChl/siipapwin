/**
 * 
 */
package com.luxsoft.siipap.cxc.consultas;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * JavaBean para representar de manera uniforme un movimiento
 * de la cuenta de un cliente
 * 
 * @author Ruben Cancino
 *
 */
public class Movimiento implements Comparable<Movimiento>{
	
	final private Integer orden;
	protected String mov;
	protected Long id;
	protected Long documento;
	protected String serie;
	protected String tipo;
	protected Long cargoId;
	protected Integer sucursal;
	protected Date fecha;
	protected Date revision;
	protected Date vencimiento;
	protected String formaDePago;
	protected String pagoRef;
	protected double descuento;
	protected BigDecimal importe=BigDecimal.ZERO;
	protected boolean aplicable;
	
	private BigDecimal saldoCargo=BigDecimal.ZERO;
	
	private BigDecimal saldoAcumulado=BigDecimal.ZERO;
	
	private BigDecimal saldoAFavor=BigDecimal.ZERO;
	
	private BigDecimal notaAFavor=BigDecimal.ZERO;
	
	private BigDecimal aplicacionesAnteriores=BigDecimal.ZERO;
	
	public Movimiento(final Integer orden){
		this.orden=orden;
	}
	
	public Movimiento(final Venta v){
		this.mov="FAC";
		this.id=v.getId();
		this.documento=v.getNumero();
		this.serie=v.getSerie();
		this.tipo=v.getTipo();
		this.cargoId=v.getId();
		this.sucursal=v.getSucursal();
		this.fecha=v.getFecha();
		this.revision=v.getCredito().getFechaRevisionCxc();
		this.vencimiento=v.getCredito().getVencimiento();
		this.formaDePago="";
		this.pagoRef="";
		this.descuento=0;
		this.importe=v.getTotal().amount();
		this.aplicable=false;		
		this.orden=new Integer(0);		
	}
	
	public Movimiento(final NotaDeCredito cargo){
		this.mov="CAR";		
		this.id=cargo.getId();
		this.documento=cargo.getNumero();
		this.serie=cargo.getSerie();
		this.tipo=cargo.getTipo();
		this.cargoId=cargo.getId();
		this.sucursal=1;
		this.fecha=cargo.getFecha();
		this.revision=null;
		this.vencimiento=org.apache.commons.lang.time.DateUtils.addDays(cargo.getFecha(), cargo.getCliente().getCredito().getPlazo());
		this.formaDePago="";
		this.pagoRef="";
		this.descuento=0;
		this.importe=cargo.getImporte().multiply(1.15d).amount();
		this.aplicable=false;		
		this.orden=new Integer(1);		
	}
	
	public Movimiento(final NotasDeCreditoDet abono){
		this.mov="NCR";		
		this.id=abono.getId();
		this.documento=abono.getNumDocumento();
		this.serie=abono.getSerie();
		this.tipo=abono.getTipo();
		this.cargoId=abono.getFactura().getId();
		this.sucursal=abono.getSucDocumento();
		this.fecha=abono.getFecha();
		this.revision=null;
		this.vencimiento=null;
		this.formaDePago="";
		String c1;
		if(abono.getTipo().equals("J")){
			c1="DEV";
		}else{
			if(abono.getTipo().equals("L"))
				c1="BON";
			else{
				c1="DES";
			}
		}
		this.pagoRef=MessageFormat.format("{0} {1} {2}", c1,abono.getTipo(),abono.getNumero());
		this.descuento=abono.getDescuento();
		if(abono.getNota().isAplicable())
			this.notaAFavor=abono.getImporte().abs().amount();
		else
			this.importe=abono.getImporte().amount();
		this.aplicable=abono.getNota().isAplicable();		
		this.orden=new Integer(2);		
	}
	
	public Movimiento(final Pago pago){
		this.mov="PAG";		
		this.id=pago.getId();
		this.documento=pago.getNumero();
		this.serie="";
		this.tipo="";
		Long targetId=pago.getVenta()!=null?pago.getVenta().getId():pago.getNota().getId();
		
		this.cargoId=targetId;
		this.sucursal=pago.getSucursal();
		this.fecha=pago.getFecha();
		this.revision=null;
		this.vencimiento=null;
		this.formaDePago=pago.getDescFormaDePago();
		this.pagoRef=MessageFormat.format("{0} {1}", pago.getReferencia(),pago.getPagoM().getBanco());
		this.descuento=0;
		this.importe=pago.getImporte().amount().multiply(BigDecimal.valueOf(-1d));
		if(pago.getFormaDePago().equals("S"))
			this.saldoAFavor=this.importe;
		if(pago.getFormaDePago().equals("T"))
			this.notaAFavor=this.importe;
		this.aplicable=false;		
		this.orden=new Integer(3);		
	}
	
	public Movimiento(final PagoM pago){
		this.mov="DIS";		
		this.id=pago.getId();
		this.documento=0l;
		this.serie="";
		this.tipo="";
		this.cargoId=null;
		this.sucursal=1;
		this.fecha=pago.getFecha();
		this.revision=null;
		this.vencimiento=null;
		this.formaDePago=pago.getFormaDePagoAsString();
		this.pagoRef=MessageFormat.format("{0} {1}", pago.getBanco(),pago.getReferencia());
		this.descuento=0;
		
		CantidadMonetaria aplicado=CantidadMonetaria.pesos(0);
		for(Pago p:pago.getPagos()){
			Date fPago=org.apache.commons.lang.time.DateUtils.truncate(pago.getFecha(), Calendar.DATE);
			Date fAplica=org.apache.commons.lang.time.DateUtils.truncate(p.getFecha(), Calendar.DATE);
			if(fPago.equals(fAplica))
				aplicado=aplicado.add(p.getImporte());
		}
		CantidadMonetaria disponible=pago.getImporte().subtract(aplicado);
		this.importe=BigDecimal.ZERO;
		this.saldoAFavor=disponible.amount();
		this.aplicable=false;		
		this.orden=new Integer(3);		
	}
	
	public String getMov() {
		return mov;
	}
	public void setMov(String mov) {
		this.mov = mov;
	}	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDocumento() {
		return documento;
	}
	public void setDocumento(Long documento) {
		this.documento = documento;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public Long getCargoId() {
		return cargoId;
	}
	public void setCargoId(Long cargoId) {
		this.cargoId = cargoId;
	}
	
	public Integer getSucursal() {
		return sucursal;
	}
	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Date getRevision() {
		return revision;
	}
	public void setRevision(Date revision) {
		this.revision = revision;
	}
	public Date getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}
	public String getFormaDePago() {
		return formaDePago;
	}
	public void setFormaDePago(String formaDePago) {
		this.formaDePago = formaDePago;
	}
	public String getPagoRef() {
		return pagoRef;
	}
	public void setPagoRef(String pagoRef) {
		this.pagoRef = pagoRef;
	}
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	public boolean isAplicable() {
		return aplicable;
	}
	public void setAplicable(boolean aplicable) {
		this.aplicable = aplicable;
	}
	public BigDecimal getSaldoAcumulado() {
		return saldoAcumulado;
	}
	public void setSaldoAcumulado(BigDecimal saldoAcumulado) {
		this.saldoAcumulado = saldoAcumulado;
	}
	public BigDecimal getSaldoAFavor() {
		return saldoAFavor;
	}
	public void setSaldoAFavor(BigDecimal saldoAFavor) {
		this.saldoAFavor = saldoAFavor;
	}
	public BigDecimal getNotaAFavor() {
		return notaAFavor;
	}
	public void setNotaAFavor(BigDecimal notaAFavor) {
		this.notaAFavor = notaAFavor;
	}
	public BigDecimal getSaldoCargo() {
		return saldoCargo;
	}
	public void setSaldoCargo(BigDecimal saldoCargo) {
		this.saldoCargo = saldoCargo;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Integer getOrden() {
		return orden;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mov == null) ? 0 : mov.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movimiento other = (Movimiento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mov == null) {
			if (other.mov != null)
				return false;
		} else if (!mov.equals(other.mov))
			return false;
		return true;
	}

	public int compareTo(Movimiento o) {
		if(fecha.equals(o.getFecha())){
			return orden.compareTo(o.getOrden());
		}
		return fecha.compareTo(o.getFecha());
	}

	public BigDecimal getAplicacionesAnteriores() {
		return aplicacionesAnteriores;
	}

	public void setAplicacionesAnteriores(BigDecimal aplicacionesAnteriores) {
		this.aplicacionesAnteriores = aplicacionesAnteriores;
	}
	

	
	
}