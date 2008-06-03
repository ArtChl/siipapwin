package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * Cargo a la cuenta del proveedor por concepto de una Factura analizada
 * 
 * @author Ruben Cancino
 *
 */
public class CXPFactura extends CXP{
	
	private Facxp analisis;	
	//private CantidadMonetaria saldo;
	//private CantidadMonetaria saldoMN;
	private double saldo;
	private double saldoMN;
	private BigDecimal descuentoFinanciero=BigDecimal.ZERO;
	private Date vencimiento;
	private Date vencimientoDF;
	
	//private Crecibosde recibo;
	
	
	private List<CXP> movimientos=new ArrayList<CXP>();
	
	public CXPFactura(){		
	}
	
	/**
	 * @deprecated Facxp es un bean descontinuado usar Analisis
	 * @param fac
	 */
	public CXPFactura(Facxp fac){
		actualizar(fac);
		
	}
	
	public CXPFactura(Analisis a){
		a.setCargo(this);		
		setProveedor(a.getProveedor());
		setClave(a.getProveedor().getClave());
		setFecha(a.getFecha());
		setReferencia(a.getFactura());
		setImporte(a.getImporte());
		setImporteMN(a.getImporteMN());
		setImpuesto(a.getImpuesto());
		setImpuestoMN(a.getImpuestoMN());
		setMoneda(a.getImporte().currency());
		//setSaldo(a.getTotal());		**Nuevo Saldo
		//setSaldoMN(a.getTotalMN());	**Nuevo Saldo
		setTc(a.getTc());
		setTotal(a.getTotal());
		setTotalMN(a.getTotalMN());
		setModificado(currentTime());
	}
	
	
	public BigDecimal getDescuentoFinanciero() {
		return descuentoFinanciero;
	}

	public void setDescuentoFinanciero(BigDecimal descuentoFinanciero) {
		this.descuentoFinanciero = descuentoFinanciero;
	}
/*
	public CantidadMonetaria getSaldo() {
		return saldo;
	}

	public void setSaldo(CantidadMonetaria saldo) {
		this.saldo = saldo;
	}

	public CantidadMonetaria getSaldoMN() {
		return saldoMN;
	}

	public void setSaldoMN(CantidadMonetaria saldoMN) {
		this.saldoMN = saldoMN;
	}
*/
	
	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public Date getVencimientoDF() {
		return vencimientoDF;
	}

	public void setVencimientoDF(Date vencimientoDF) {
		this.vencimientoDF = vencimientoDF;
	}

	public Facxp getAnalisis() {
		return analisis;
	}

	public void setAnalisis(Facxp analisis) {
		this.analisis = analisis;
	}
	
	public CXPNCredito crearAbono(){
		CXPNCredito cred=new CXPNCredito();
		cred.setClave(getClave());
		cred.setFactura(this);
		cred.setMoneda(getMoneda());
		cred.setNombre(getNombre());
		cred.setProveedor(getProveedor());
		cred.setReferencia(getReferencia());
		cred.setTc(getTc());
		return cred;
	}
	
	public void aplicarNotaDeCredito(final CXPNCredito cred){
		//Assert.isTrue(cred.getTotal().getAmount().doubleValue()<0,"La Nota de credito no es negativa");
		//setSaldo(getSaldo().add(cred.getTotal()));
		//setSaldoMN(getSaldoMN().add(cred.getTotalMN()));
	}
	
	public CXPPago aplicarPago(){
		CXPPago pago=new CXPPago();
		BeanUtils.copyProperties(this,pago,new String[]{"id","creado","modificado"});
		pago.setFactura(this);
		pago.setTc(getTc());		
		pago.setImporte(getImporte());
		pago.setImpuesto(getImpuesto());
		pago.actualizarTotales();
		pago.actualizarTotalesMN();
		//setSaldo(getSaldo().subtract(getSaldo()));
		//setSaldoMN(getSaldoMN().subtract(getSaldoMN()));
		return pago;
	}
	
	 
	
	public CXPPago aplicarPago(CantidadMonetaria monto){
		return aplicarPago(monto,getTc());
	}	
	
	public CXPPago aplicarPago(CantidadMonetaria monto,BigDecimal tc){
		CXPPago pago=new CXPPago();
		CXP cxp=(CXP)this;		
		BeanUtils.copyProperties(cxp,pago,new String[]{"id","creado","modificado"});
		pago.setFactura(this);
		pago.setTc(tc);		
		pago.setImporte(monto);
		pago.actualizarTotales();
		pago.actualizarTotalesMN();
		Assert.notNull(pago.getProveedor());
		//CantidadMonetaria s=getSaldo().add(pago.getTotal());
		//CantidadMonetaria smn=getSaldoMN().add(pago.getTotalMN());
		//setSaldo(s);
		//setSaldoMN(smn);
		return pago;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		CXPFactura f=(CXPFactura)obj;
		return new EqualsBuilder()
		.append(getProveedor(),f.getProveedor())
		.append(getReferencia(),f.getReferencia())
		.append(getId(),f.getId())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getProveedor())
		.append(getReferencia())
		.append(getId())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Prov:"+getProveedor().getClave())
		.append("Fac: "+getReferencia())
		.append("Fecha:",getFecha())
		.append("Importe:"+getImporteMN())
		.toString();
	}

	
	
	public List<CXP> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<CXP> movimientos) {
		this.movimientos = movimientos;
	}

	public boolean tieneMovimientos(){
		return !getMovimientos().isEmpty();
	}
	
	public void actualizar(Facxp fac){
		fac.setCargo(this);
		setAnalisis(fac);
		setProveedor(fac.getProveedor());
		setClave(fac.getProveedor().getClave());
		setFecha(fac.getFECHA());
		setReferencia(fac.getFACTURA());
		setDescuentoFinanciero(BigDecimal.valueOf(fac.getDSCTOF()));
		setImporte(fac.getImporte());
		setImporteMN(fac.getImporteMN());
		setImpuesto(fac.getImpuesto());
		setImpuestoMN(fac.getImpuestoMN());
		setMoneda(fac.getTipoDeMoneda());
		//setSaldo(fac.getTotal()); 			**Nuevo Saldo
		//setSaldoMN(fac.getTotalMN());			**Nuevo Saldo
		setTc(BigDecimal.valueOf(fac.getTC()));
		setTotal(fac.getTotal());
		setTotalMN(fac.getTotalMN());
		setVencimiento(fac.getVTO());
		setVencimientoDF(fac.getVTOD());
		setModificado(currentTime());
	}
	/*
	public void actualizar(Crecibosde det){
		setRecibo(det);
		det.setCargo(this);
		setProveedor(det.getProveedor());
		setClave(det.getProveedor().getClave());
		setFecha(det.getFECHA());
		setReferencia(det.getFACTURA());		
		setImporte(det.getImporte());
		setImporteMN(det.getImporteMN());
		setImpuesto(det.getImpuesto());
		setImpuestoMN(det.getImpuestoMN());
		setMoneda(det.getMoneda());
		setSaldo(det.getTotal());
		setSaldoMN(det.getTotalMN());
		setTc(det.getTc());
		setTotal(det.getTotal());
		setTotalMN(det.getTotalMN());
		setDescuentoFinanciero(det.getProveedor().getDescuentoFinanciero());		
		setModificado(currentTime());
		calcularVencimiento();
		calcularDescuentoFinanciero();
	}
	*/
	public void calcularVencimiento(){
		Calendar c=Calendar.getInstance();
		final Date inicio;
		if(getProveedor().getVencimientoEstipulado()==1){
			inicio=getFecha();
		}else{
			inicio=getCreado();
		}
		c.setTime(inicio);
		c.getTime();
		int dias=getProveedor().getDiasDeCredito();
		c.add(Calendar.DATE,dias);
		setVencimiento(c.getTime());
	}
	
	public void calcularDescuentoFinanciero(){
		BigDecimal df=getProveedor().getDescuentoFinanciero()!=null?getProveedor().getDescuentoFinanciero():BigDecimal.ZERO;
		setDescuentoFinanciero(df);
		if(df.equals(BigDecimal.ZERO))return;
		Calendar c=Calendar.getInstance();
		final Date inicio;
		if(getProveedor().getVencimientoEstipulado()==1){
			inicio=getFecha();
		}else{
			inicio=getCreado();
		}
		c.setTime(inicio);
		c.getTime();
		int dias=getProveedor().getDiasDF();
		c.add(Calendar.DATE,dias);
		setVencimientoDF(c.getTime());
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public double getSaldoMN() {
		return saldoMN;
	}

	public void setSaldoMN(double saldoMN) {
		this.saldoMN = saldoMN;
	}
	
	

}
