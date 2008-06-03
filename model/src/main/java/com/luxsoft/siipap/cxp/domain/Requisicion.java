package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * Requisición de Cheque para pago de facturas por pagar
 * 
 * @author Ruben Cancino
 *
 */
public class Requisicion extends PersistentObject{
	
	private Proveedor proveedor;
	private Date fecha=Calendar.getInstance().getTime();
	private String tipoDePago="CHEQUE";
	private Currency moneda=CantidadMonetaria.PESOS;
	private BigDecimal tipoDeCambio=BigDecimal.ONE;
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private String elaboro;
	private Date creado=Calendar.getInstance().getTime();
	private Date modificado;
	private boolean descuentoAplicable=true;
	private boolean aplicada=false;
	private boolean ncaplicada=false;
	private String observaciones;
	
	private Long numero;  //Compatibilidad con old siipapw
	
	private List<RequisicionDetalle> partidas=new ArrayList<RequisicionDetalle>();

	
	
	public boolean isAplicada() {
		return aplicada;
	}

	public void setAplicada(boolean aplicada) {
		this.aplicada = aplicada;
	}

	public boolean isDescuentoAplicable() {
		return descuentoAplicable;
	}

	public void setDescuentoAplicable(boolean descuentoAplicable) {
		this.descuentoAplicable = descuentoAplicable;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public String getElaboro() {
		return elaboro;
	}

	public void setElaboro(String elaboro) {
		this.elaboro = elaboro;
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
		Object old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
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

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public BigDecimal getTipoDeCambio() {
		return tipoDeCambio;
	}

	public void setTipoDeCambio(BigDecimal tipoDeCambio) {
		this.tipoDeCambio = tipoDeCambio;
	}

	public String getTipoDePago() {
		return tipoDePago;
	}

	public void setTipoDePago(String tipoDePago) {
		this.tipoDePago = tipoDePago;
	}

	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	
	public List<RequisicionDetalle> getPartidas() {
		return partidas;
	}

	public void setPartidas(List<RequisicionDetalle> partidas) {
		this.partidas = partidas;
	}

	public boolean addPartida(final RequisicionDetalle detalle){
		detalle.setRequisicion(this);		
		detalle.setProveedor(getProveedor());
		return getPartidas().add(detalle);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		Requisicion p=(Requisicion)obj;
		return new EqualsBuilder()
		.append(getProveedor(),p.getProveedor())
		.append(getCreado(),p.getCreado())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getProveedor())
		.append(getCreado())
		.toHashCode();
	}	
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Req:",getId())
		.append("Prov:"+getProveedor())
		.append("Fecha:",getFecha())
		.append("Importe:",getImporte())
		.toString();
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public boolean isNcaplicada() {
		return ncaplicada;
	}

	public void setNcaplicada(boolean ncaplicada) {
		this.ncaplicada = ncaplicada;
	}
	
	

}
