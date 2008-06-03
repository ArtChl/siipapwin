package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * Partidas unitarias de requisicion de cheques, es decir
 * solicitudes de pago. Estas deben ser en la misma moneda que el maestro
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class RequisicionDetalle extends PersistentObject{
	
	 	 
	private Proveedor proveedor;
	private String factura;
	private BigDecimal descuentoFinanciero=BigDecimal.ZERO;
	private CantidadMonetaria importe;
	private CantidadMonetaria importeMN;
	
	
	private Requisicion requisicion;
	private Long numero; //Compatibilidad
	
	private CXPFactura cargo;
	
	
	private Date creado=Calendar.getInstance().getTime();;
	private Date modificado;
	
	public RequisicionDetalle(){
		
	}
	
	public RequisicionDetalle(CXPFactura factura){
		setProveedor(factura.getProveedor());
		setFactura(factura.getReferencia().trim());
		//setDescuentoFinanciero(factura.getDescuentoFinanciero());
		setDescuentoFinanciero(getProveedor().getDescuentoFinanciero());
		setImporte(factura.getTotal());
		//setImporteMN(factura.getTotalMN());
		setCargo(factura);
	}
	
	
	
	/**
	 * Calcula el importe de la partida en moneda nacional
	 *
	 */
	public void calcularImportesMN(boolean conDescuentoF){		
		Assert.notNull(getRequisicion(),"No se puede reaclcular la partida sin el maestro");
		System.out.println("Calculando importe en moneda naciuonal");
		CantidadMonetaria neto=getImporte();
		if(conDescuentoF){
			neto=MonedasUtils.aplicarDescuentosEnCascada(getImporte(),getDescuentoFinanciero());
			setImporte(neto);
		}
		CantidadMonetaria monto=getImporte().multiply(getRequisicion().getTipoDeCambio());
		CantidadMonetaria pesos=new CantidadMonetaria(monto.amount().doubleValue(),CantidadMonetaria.PESOS);
		setImporteMN(pesos);
		
	}
	
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	public BigDecimal getDescuentoFinanciero() {
		return descuentoFinanciero;
	}
	public void setDescuentoFinanciero(BigDecimal descuentoFinanciero) {
		this.descuentoFinanciero = descuentoFinanciero;
	}
	
	/**
	 * El total de lo analizado que se obtiene del Cargo CXPFactura (SW_CXP)
	 * en su propiedad de Total. Este dato es en la moneda original
	 * del analisis. Este monto se usa en
	 * la generacion automatica de pagos por requisicion y es el 
	 * monto del pago en la moneda original 
	 * 
	 * @return
	 */
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}
	
	/**
	 * Es lo mismo que  importe pero en moneda nacional al tipo
	 * de cambio del dia de la requisicion. Este monto se usa en
	 * la generacion automatica de pagos por requisicion y es el 
	 * monto del pago en moneda nacional 
	 * 
	 * @return
	 */
	public CantidadMonetaria getImporteMN() {
		return importeMN;
	}
	public void setImporteMN(CantidadMonetaria importeMN) {
		Object old=importeMN;
		this.importeMN = importeMN;
		getPropertyChangeSupport().firePropertyChange("importeMN",old,importeMN);
	}
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	public Requisicion getRequisicion() {
		return requisicion;
	}
	public void setRequisicion(Requisicion requisicion) {
		this.requisicion = requisicion;
	}
	
	
	
	public CXPFactura getCargo() {
		return cargo;
	}

	public void setCargo(CXPFactura cargo) {
		this.cargo = cargo;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		RequisicionDetalle d=(RequisicionDetalle)obj;
		return new EqualsBuilder()
		//.append(getProveedor(),d.getProveedor())
		.append(getFactura(),d.getFactura())
		//.append(getCreado(),d.getCreado())
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		//.append(getProveedor())
		.append(getFactura())
		//.append(getCreado())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Proveedor:"+getProveedor())
		.append("Factura:"+getFactura())
		.append("Importe:"+getImporte())
		.toString();
	}
	/*
	public BigDecimal getMonto() {
		return monto;
	}
	
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}
	*/
	

}
