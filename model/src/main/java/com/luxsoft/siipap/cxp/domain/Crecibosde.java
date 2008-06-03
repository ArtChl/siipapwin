package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;

public class Crecibosde extends PersistentObject{
	
	
	private Long NUMERO; //NUMERO CHAR(8)
	private String FACTURA;//FACTURA CHAR(12)
	private String MONEDA; //MONEDA CHAR(4)
	private Date FECHA=Calendar.getInstance().getTime(); //FECHA DATE
	private Currency moneda=CantidadMonetaria.PESOS;
	private BigDecimal tc=BigDecimal.ONE; //TC  FLOAT(126)
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0); //IMPORTE FLOAT(126)
	private CantidadMonetaria impuesto;//IMPUESTO FLOAT(126)
	private CantidadMonetaria total; //TOTAL FLOAT(126)
	private CantidadMonetaria importeMN; //IMPORTEMN FLOAT(126)
	private CantidadMonetaria impuestoMN;//IMPUESTOMN FLOAT(126)
	private CantidadMonetaria totalMN; //TOTALMN FLOAT(126)
	private Date vencimiento; //VTO DATE
	private String NC; //NC CHAR(10)
	private CantidadMonetaria importeNC; //IMPNC FLOAT(126)
	private Long conceptoNC; //CONNC CHAR(4)
	private String NC1; //NC1 CHAR(10)
	private CantidadMonetaria importeNC1; //IMPNC1 FLOAT(126)
	private Long conceptoNC1; //CONNC1 CHAR(4)
	private String NC2; //NC2 CHAR(10)
	private CantidadMonetaria importeNC2; //IMPNC2 FLOAT(126)
	private Long conceptoNC2; //CONNC2 CHAR(4)
	private BigDecimal ajuste; //AJUSTE FLOAT(126)
	private BigDecimal descuentoF; ////DSCTOF FLOAT(126)
	private Date vencimientoDF; //VTOD DATE
	private CantidadMonetaria IMPORTEO; //IMPORTEO FLOAT(126)
	private boolean verificado; //VERIF CHAR(1)
	private String observaciones; //OBS CHAR(40)
	
	//private Facxp analisis;	
	private Crecibos crecibos;
	private Proveedor proveedor;
	//private CXPFactura cargo;
	
	
	
	private Date creado=Calendar.getInstance().getTime();
	private Date modificado=Calendar.getInstance().getTime();
	
	
	
	public BigDecimal getAjuste() {
		return ajuste;
	}
	public void setAjuste(BigDecimal ajuste) {
		this.ajuste = ajuste;
	}
	/*
	public Facxp getAnalisis() {
		return analisis;
	}
	public void setAnalisis(Facxp analisis) {
		this.analisis = analisis;
	}*/
	
	public Long getConceptoNC() {
		return conceptoNC;
	}
	public void setConceptoNC(Long conceptoNC) {
		this.conceptoNC = conceptoNC;
	}
	public Long getConceptoNC1() {
		return conceptoNC1;
	}
	public void setConceptoNC1(Long conceptoNC1) {
		this.conceptoNC1 = conceptoNC1;
	}
	public Long getConceptoNC2() {
		return conceptoNC2;
	}
	public void setConceptoNC2(Long conceptoNC2) {
		this.conceptoNC2 = conceptoNC2;
	}
	public BigDecimal getDescuentoF() {
		return descuentoF;
	}
	public void setDescuentoF(BigDecimal descuentoF) {
		this.descuentoF = descuentoF;
	}
	public String getFACTURA() {
		return FACTURA;
	}
	public void setFACTURA(String factura) {
		FACTURA = factura;
	}
	public Date getFECHA() {
		return FECHA;
	}
	public void setFECHA(Date fecha) {
		FECHA = fecha;
	}
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		Object old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
	}
	public CantidadMonetaria getImporteMN() {
		return importeMN;
	}
	public void setImporteMN(CantidadMonetaria importeMN) {
		this.importeMN = importeMN;
	}
	public CantidadMonetaria getImporteNC() {
		return importeNC;
	}
	public void setImporteNC(CantidadMonetaria importeNC) {
		this.importeNC = importeNC;
	}
	public CantidadMonetaria getImporteNC1() {
		return importeNC1;
	}
	public void setImporteNC1(CantidadMonetaria importeNC1) {
		this.importeNC1 = importeNC1;
	}
	public CantidadMonetaria getImporteNC2() {
		return importeNC2;
	}
	public void setImporteNC2(CantidadMonetaria importeNC2) {
		this.importeNC2 = importeNC2;
	}
	public CantidadMonetaria getIMPORTEO() {
		return IMPORTEO;
	}
	public void setIMPORTEO(CantidadMonetaria importeo) {
		IMPORTEO = importeo;
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
	public Currency getMoneda() {
		return moneda;
	}
	public void setMoneda(Currency moneda) {
		this.moneda = moneda;
	}
	public String getMONEDA() {
		return MONEDA;
	}
	public void setMONEDA(String moneda) {
		MONEDA = moneda;
	}
	public String getNC() {
		return NC;
	}
	public void setNC(String nc) {
		NC = nc;
	}
	public String getNC1() {
		return NC1;
	}
	public void setNC1(String nc1) {
		NC1 = nc1;
	}
	public String getNC2() {
		return NC2;
	}
	public void setNC2(String nc2) {
		NC2 = nc2;
	}
	public Long getNUMERO() {
		return NUMERO;
	}
	public void setNUMERO(Long numero) {
		NUMERO = numero;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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
	public Date getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(Date vencimiento) {
		Object old=this.vencimiento;
		this.vencimiento = vencimiento;
		getPropertyChangeSupport().firePropertyChange("vencimiento",old,vencimiento);
	}
	public Date getVencimientoDF() {
		return vencimientoDF;
	}
	public void setVencimientoDF(Date vencimientoDF) {
		this.vencimientoDF = vencimientoDF;
	}
	public boolean isVerificado() {
		return verificado;
	}
	public void setVerificado(boolean verificado) {
		this.verificado = verificado;
	}
	public Crecibos getCrecibos() {
		return crecibos;
	}
	public void setCrecibos(Crecibos crecibos) {
		this.crecibos = crecibos;
	}
	
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
	
	
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;		
		if(obj==this) return true;
		Crecibosde de=(Crecibosde)obj;
		return new EqualsBuilder()
			.append(getProveedor(),de.getProveedor())
			.append(getFACTURA().trim(),de.getFACTURA().trim())
			//.append(getCreado(),de.getCreado())
			.isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(getProveedor())
			.append(getFACTURA().trim())
			//.append(getCreado())
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getFECHA())
		.append(getFACTURA())
		.append(getImporteMN())
		.toString();
	}
	
	public void calcularTotales(){
		setMoneda(getTotal().getCurrency());
		setImporte(MonedasUtils.calcularImporteDelTotal(getTotal()));		
		setImpuesto(MonedasUtils.calcularImpuesto(getImporte()));
		calcularImportesMN();
	}
	
	/**
	 * Calcula el importe en Moneda nacional al tipo de cambio establecido en el Recibo
	 *
	 */
	private void calcularImportesMN(){
		BigDecimal tc=getTc();
		CantidadMonetaria importe=getImporte().multiply(tc);
		setImporteMN(importe);
		setImpuestoMN(MonedasUtils.calcularImpuesto(importe));
		setTotalMN(MonedasUtils.calcularTotal(importe));
	}
	
	public void calcularVencimiento(){
		Calendar c=Calendar.getInstance();
		final Date inicio;
		if(getProveedor().getVencimientoEstipulado()==1){
			inicio=getFECHA();
		}else{
			inicio=getCreado();
		}
		c.setTime(inicio);
		c.getTime();
		int dias=getProveedor().getDiasDeCredito();
		c.add(Calendar.DATE,dias);
		setVencimiento(c.getTime());
	}
	
	/*
	public boolean isAnalizada() {
		return getAnalisis()!=null;
	}
	*/
	/*
	public CXPFactura getCargo() {
		return cargo;
	}
	public void setCargo(CXPFactura cargo) {
		this.cargo = cargo;
	}
	
	*/

}
