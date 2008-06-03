package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * Entidad generica que representa un movimiento en la cuenta
 * de un proveedor, ya sea abono o bien cargo. La dirferencia radica
 * en el signo del importe (+) cargo (-) abono
 * 
 * @author Ruben Cancino
 *
 */
public abstract class CXP extends PersistentObject{
	
	private Proveedor proveedor;
	private String clave;
	private String nombre;
	private String referencia;
	private Date fecha=currentTime();
	private Currency moneda=CantidadMonetaria.PESOS;
	private BigDecimal tc=BigDecimal.ONE;
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private CantidadMonetaria impuesto=CantidadMonetaria.pesos(0);
	private CantidadMonetaria total=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importeMN=CantidadMonetaria.pesos(0);
	private CantidadMonetaria impuestoMN=CantidadMonetaria.pesos(0);
	private CantidadMonetaria totalMN=CantidadMonetaria.pesos(0);
	private RequisicionDetalle requisicion;
	private String comentario;
	private Date creado=currentTime();
	private Date modificado;
	
	private CXPFactura factura;
	
	public CXPFactura getFactura() {
		return factura;
	}

	public void setFactura(CXPFactura factura) {
		this.factura = factura;
	}
	
	
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}
	public CantidadMonetaria getImporteMN() {
		return importeMN;
	}
	public void setImporteMN(CantidadMonetaria importeMN) {
		this.importeMN = importeMN;
	}
	public CantidadMonetaria getImpuesto() {
		return impuesto;
	}
	public void setImpuesto(CantidadMonetaria impuesto) {
		this.impuesto = impuesto;
	}
	public CantidadMonetaria getImpuestoMN() {
		return impuestoMN;
	}
	public void setImpuestoMN(CantidadMonetaria impuestoMN) {
		this.impuestoMN = impuestoMN;
	}
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	public Currency getMoneda() {
		return moneda;
	}
	public void setMoneda(Currency moneda) {
		this.moneda = moneda;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	public BigDecimal getTc() {
		return tc;
	}
	public void setTc(BigDecimal tc) {
		this.tc = tc;
	}
	public CantidadMonetaria getTotal() {
		return total;
	}
	public void setTotal(CantidadMonetaria total) {
		this.total = total;
	}
	public CantidadMonetaria getTotalMN() {
		return totalMN;
	}
	public void setTotalMN(CantidadMonetaria totalMN) {
		this.totalMN = totalMN;
	}
	
	public boolean actualizarTotales(){
		setImpuesto(MonedasUtils.calcularImpuesto(getImporte()));
		setTotal(MonedasUtils.calcularTotal(getImporte()));
		actualizarTotalesMN();
		return true;
	}
	
	public void actualizarTotalesMN(){
		/*
		setImporteMN(CantidadMonetaria.pesos(getImporte().multiply(getTc()).getAmount().doubleValue()));
		setImpuestoMN(new CantidadMonetaria(getImpuesto().multiply(getTc()).getAmount().doubleValue(),CantidadMonetaria.PESOS));
		setTotalMN(new CantidadMonetaria(getTotal().multiply(getTc()).getAmount().doubleValue(),CantidadMonetaria.PESOS));
		*/
	}

	public RequisicionDetalle getRequisicion() {
		return requisicion;
	}

	public void setRequisicion(RequisicionDetalle requisicion) {
		this.requisicion = requisicion;
	}
	
	

}
