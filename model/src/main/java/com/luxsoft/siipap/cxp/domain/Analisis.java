package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;

public class Analisis extends PersistentObject{
	
	
	
	private Proveedor proveedor;
	private String clave;
	private String nombre;
	private String factura;
	private Date fecha=currentTime();	
	private BigDecimal tc=BigDecimal.ONE;
	private Currency moneda=CantidadMonetaria.PESOS;
	
	private CantidadMonetaria importe;
	private CantidadMonetaria impuesto;
	private CantidadMonetaria total;	
	
	private CantidadMonetaria importef=CantidadMonetaria.pesos(0);
	private CantidadMonetaria impuestof=CantidadMonetaria.pesos(0);
	private CantidadMonetaria totalf=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria importeMN=CantidadMonetaria.pesos(0);
	private CantidadMonetaria impuestoMN=CantidadMonetaria.pesos(0);
	private CantidadMonetaria totalMN=CantidadMonetaria.pesos(0);
	
	private int numero;
	
	private CXPFactura cargo;
	
	private boolean impreso;
	
	private Date creado=currentTime();
	private int version;
	
	private Set<AnalisisDet> partidas=new HashSet<AnalisisDet>();
	
	
	
	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public CXPFactura getCargo() {
		return cargo;
	}

	public void setCargo(CXPFactura cargo) {
		this.cargo = cargo;
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

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
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

	public boolean isImpreso() {
		return impreso;
	}

	public void setImpreso(boolean impreso) {
		this.impreso = impreso;
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

	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Set<AnalisisDet> getPartidas() {
		return partidas;
	}

	public void setPartidas(Set<AnalisisDet> partidas) {
		this.partidas = partidas;
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
	
	

	public CantidadMonetaria getImportef() {
		return importef;
	}

	public void setImportef(CantidadMonetaria importef) {
		this.importef = importef;
	}

	public CantidadMonetaria getImpuestof() {
		return impuestof;
	}

	public void setImpuestof(CantidadMonetaria impuestof) {
		this.impuestof = impuestof;
	}

	public CantidadMonetaria getTotalf() {
		return totalf;
	}

	public void setTotalf(CantidadMonetaria totalf) {
		this.totalf = totalf;
	}
	
	public boolean agregarPartida(AnalisisDet detalle){
		detalle.setAnalisis(this);
		detalle.setNetoMN(detalle.getNeto().multiply(getTc()));
		if(getImporte()==null || (getImporte().amount().doubleValue()==0)){
			setImporte(detalle.getImporte());
		}else{
			setImporte(getImporte().add(detalle.getImporte()));
		}
		return getPartidas().add(detalle);
	}
	
	public void calcularImportesEnMN(){
		setImporteMN(CantidadMonetaria.pesos(getImporte().multiply(getTc()).getAmount().doubleValue()));
		setImpuestoMN(new CantidadMonetaria(getImpuesto().multiply(getTc()).getAmount().doubleValue(),CantidadMonetaria.PESOS));
		setTotalMN(new CantidadMonetaria(getTotal().multiply(getTc()).getAmount().doubleValue(),CantidadMonetaria.PESOS));
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	

	/**
	 * Propiedad no persistente para facilitar el manejo de moneda desde la UI
	 * 
	 * @return
	 */
	public Currency getMoneda() {
		return moneda;
	}

	public void setMoneda(Currency moneda) {
		this.moneda = moneda;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		Analisis a=(Analisis)obj;
		return new EqualsBuilder()
		.append(getClave(),a.getClave())
		.append(getFactura(),a.getFactura())
		.append(getCreado(),a.getCreado())
		.isEquals();
		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getClave())
		.append(getFactura())
		.append(getCreado())
		.toHashCode();
	}

	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Proveedor",getClave())
		.append("Factura",getFactura())
		.append(getFecha())
		.append("  Importe: ",getImporte())
		.append("  Importe F: ",getImportef())
		//.append(getTotal())
		.toString();
	}

}
