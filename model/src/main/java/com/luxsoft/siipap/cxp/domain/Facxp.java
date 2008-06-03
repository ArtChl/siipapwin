/*
 * Created on 12/07/2005
 * TODO Colocar información de Licencia
 * 
 */

package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.MutableObject;




/**
 * @author Rubén Lugo
 *
 */
@SuppressWarnings("unused") 
public class Facxp extends MutableObject{
	
	private Long id;
	private Integer NUMERO;
	private String CLAVE;
	private String PROVEEDOR;
	private String REFERENCIA;
	private String FACTURA;
	private String CXP;
	private Date FECHA=Calendar.getInstance().getTime();
	private String MONEDA;
	private Double TC=new Double(1);
	
	
	
	
	
	private String POLIZA;
	private String CRECIBO;
	private Date FCREIBO;
	private Date VTO;
	private Double DSCTOF;
	private Date VTOD;
	private String ELABORO;
	private String LLAVEG;
	private Double TC1=new Double(0);
	private Double TC2=new Double(0);
	private Double TC3;
	private Double TC4;
	private Double TC5;
	private String NC;
	private Double IMPNC;
	private String CONNC;
	
	
	
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private CantidadMonetaria impuesto=CantidadMonetaria.pesos(0);
	private CantidadMonetaria total=CantidadMonetaria.pesos(0);	
	
	private CantidadMonetaria importeEnFactura;
	private CantidadMonetaria impuestoEnFactura;
	private CantidadMonetaria totalEnFactura;
	
	private CantidadMonetaria importeMN;
	private CantidadMonetaria impuestoMN;
	private CantidadMonetaria totalMN;
	
	private Date FECHAC;
	private String IMPRESO;
	
	
	
	private Currency tipoDeMoneda=Currency.getInstance(new Locale("es","mx"));
	private Proveedor proveedor;
	private CXPFactura cargo;
		
	private Set<Facxpde> partidas;
 
	public Facxp(){
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCLAVE() {
		return CLAVE;
	}
	public void setCLAVE(String clave) {
		CLAVE = clave;
	}
	public String getCONNC() {
		return CONNC;
	}
	public void setCONNC(String connc) {
		CONNC = connc;
	}
	public String getCRECIBO() {
		return CRECIBO;
	}
	public void setCRECIBO(String crecibo) {
		CRECIBO = crecibo;
	}
	public String getCXP() {
		return CXP;
	}
	public void setCXP(String cxp) {
		CXP = cxp;
	}
	public Double getDSCTOF() {
		return DSCTOF;
	}
	public void setDSCTOF(Double dsctof) {
		DSCTOF = dsctof;
	}
	public String getELABORO() {
		return ELABORO;
	}
	public void setELABORO(String elaboro) {
		ELABORO = elaboro;
	}
	public String getFACTURA() {
		return FACTURA;
	}
	public void setFACTURA(String factura) {
		FACTURA = factura;
	}
	public Date getFCREIBO() {
		return FCREIBO;
	}
	public void setFCREIBO(Date fcreibo) {
		FCREIBO = fcreibo;
	}
	public Date getFECHA() {
		return FECHA;
	}
	public void setFECHA(Date fecha) {
		Object old=this.FECHA;
		FECHA = fecha;
		getPropertyChangeSupport().firePropertyChange("FECHA",old,fecha);
	}
	
	public Date getFECHAC() {
		return FECHAC;
	}
	public void setFECHAC(Date fechac) {
		FECHAC = fechac;
	}
	public Double getIMPNC() {
		return IMPNC;
	}
	public void setIMPNC(Double impnc) {
		IMPNC = impnc;
	}
	
	public String getIMPRESO() {
		return IMPRESO;
	}
	public void setIMPRESO(String impreso) {
		IMPRESO = impreso;
	}
	
	public String getLLAVEG() {
		return LLAVEG;
	}
	public void setLLAVEG(String llaveg) {
		LLAVEG = llaveg;
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
	public Integer getNUMERO() {
		return NUMERO;
	}
	public void setNUMERO(Integer numero) {
		NUMERO = numero;
	}
	public String getPOLIZA() {
		return POLIZA;
	}
	public void setPOLIZA(String poliza) {
		POLIZA = poliza;
	}
	public String getPROVEEDOR() {
		return PROVEEDOR;
	}
	public void setPROVEEDOR(String proveedor) {
		PROVEEDOR = proveedor;
	}
	public String getREFERENCIA() {
		return REFERENCIA;
	}
	public void setREFERENCIA(String referencia) {
		REFERENCIA = referencia;
	}
	public Double getTC() {
		return TC;
	}
	public void setTC(Double tc) {
		TC = tc;
	}
	public Double getTC1() {
		return TC1;
	}
	public void setTC1(Double tc1) {
		TC1 = tc1;
	}
	public Double getTC2() {
		return TC2;
	}
	public void setTC2(Double tc2) {
		TC2 = tc2;
	}
	public Double getTC3() {
		return TC3;
	}
	public void setTC3(Double tc3) {
		TC3 = tc3;
	}
	public Double getTC4() {
		return TC4;
	}
	public void setTC4(Double tc4) {
		TC4 = tc4;
	}
	public Double getTC5() {
		return TC5;
	}
	public void setTC5(Double tc5) {
		TC5 = tc5;
	}
	
	
	public Date getVTO() {
		return VTO;
	}
	public void setVTO(Date vto) {
		VTO = vto;
	}
	public Date getVTOD() {
		return VTOD;
	}
	public void setVTOD(Date vtod) {
		VTOD = vtod;
	}

	public Set<Facxpde> getPartidas() {
		if(partidas==null){
			partidas=new HashSet<Facxpde>();
		}
		return partidas;
	}
	
	protected void setPartidas(Set<Facxpde> partidas) {
		this.partidas=partidas;
	}
	
	@SuppressWarnings("unchecked")
	public void agregarPartidas(final Facxpde facxpde) {
		facxpde.setFacxp(this);
		getPartidas().add(facxpde);
		recalcularImportes();
	}
	public void eliminarPartida(final Facxpde facxpde){
		facxpde.setFacxp(null);
		getPartidas().remove(facxpde);
		recalcularImportes();
	}

	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
	public CantidadMonetaria getImporte() {
		return importe;
	}

	public void setImporte(CantidadMonetaria importe) {
		Object old=this.importe;		
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
	}

	public CantidadMonetaria getImporteEnFactura() {
		return importeEnFactura;
	}

	public void setImporteEnFactura(CantidadMonetaria importeEnFactura) {
		Object old=this.importeEnFactura;
		this.importeEnFactura = importeEnFactura;
		getPropertyChangeSupport().firePropertyChange("importeEnFactura",old,importeEnFactura);
	}

	public CantidadMonetaria getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(CantidadMonetaria impuesto) {
		Object old=this.impuesto;
		this.impuesto = impuesto;
		getPropertyChangeSupport().firePropertyChange("impuesto",old,impuesto);
	}

	public CantidadMonetaria getImpuestoEnFactura() {
		return impuestoEnFactura;
	}

	public void setImpuestoEnFactura(CantidadMonetaria impuestoEnFactura) {
		Object old=this.impuestoEnFactura;
		this.impuestoEnFactura = impuestoEnFactura;
		getPropertyChangeSupport().firePropertyChange("impuestoEnFactura",old,impuestoEnFactura);
	}

	public Currency getTipoDeMoneda() {
		return tipoDeMoneda;
	}

	public void setTipoDeMoneda(Currency tipoDeMoneda) {
		this.tipoDeMoneda = tipoDeMoneda;
	}

	public CantidadMonetaria getTotal() {
		return total;
	}

	public void setTotal(CantidadMonetaria total) {
		Object old=this.total;
		this.total = total;
		getPropertyChangeSupport().firePropertyChange("total",old,total);
	}

	public CantidadMonetaria getTotalEnFactura() {
		return totalEnFactura;
	}

	public void setTotalEnFactura(CantidadMonetaria totalEnFactura) {
		Object old=this.totalEnFactura;
		this.totalEnFactura = totalEnFactura;
		getPropertyChangeSupport().firePropertyChange("totalEnFactura",old,totalEnFactura);
	}
	
	

	public CantidadMonetaria getImporteMN() {
		return importeMN;
	}

	public void setImporteMN(CantidadMonetaria importeMN) {
		this.importeMN = importeMN;
	}

	public CantidadMonetaria getImpuestoMN() {
		return impuestoMN;
	}

	public void setImpuestoMN(CantidadMonetaria impuestoMN) {
		this.impuestoMN = impuestoMN;
	}

	public CantidadMonetaria getTotalMN() {
		return totalMN;
	}

	public void setTotalMN(CantidadMonetaria totalMN) {
		this.totalMN = totalMN;
	}
	
	private Date creado=Calendar.getInstance().getTime();
	private Date modificado;

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

	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
        Facxp num=(Facxp)obj;
        return new EqualsBuilder()
                  .append(proveedor,num.getProveedor())
                  .append(FACTURA,num.getFACTURA())
                  .isEquals();
    }

    public int hashCode() {        
        return new HashCodeBuilder(3,7)
        	.append(getProveedor())
        	.append(getFACTURA())
        	.toHashCode();
    }
    
    public String toString(){
    	return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
    		.append(getPROVEEDOR())
    		.append(getFACTURA())
    		.append(getFECHA())
    		.toString();
    }

	
    private double TOTALF;
	private double IMPUESTOF;
	private double IMPORTEF;
    

	public double getIMPORTE() {
		return getImporte().getAmount().doubleValue();
	}
	public void setIMPORTE(double importe) {		
	}
	
	public double getIMPUESTO() {
		return getImpuesto().getAmount().doubleValue();
	}
	public void setIMPUESTO(double impuesto) {		
	}
	public double getTOTAL() {
		return getTotal().getAmount().doubleValue();
	}
	public void setTOTAL(double total) {
	}


	public double getIMPORTEF() {
		return IMPORTEF;
	}

	public void setIMPORTEF(double importef) {
		IMPORTEF = importef;
	}

	
	public double getIMPUESTOF() {
		return IMPUESTOF;
	}

	public void setIMPUESTOF(double impuestof) {
		IMPUESTOF = impuestof;
	}

	

	public double getTOTALF() {
		return TOTALF;
	}

	public void setTOTALF(double totalf) {
		TOTALF = totalf;
	}
	
	
		
	/**
	 * Recalcula importeEnFactura,impuestoEnFactura y totalEnFactura  en base a 
	 * IMPORTEF,IMPUESTOF,TOTALF
	 */
	private void calcularImportesDeFactura(){
		
		Facxp analisis=this;
		
		/*
		CantidadMonetaria importef=new CantidadMonetaria(analisis.getIMPORTEF(),analisis.getTipoDeMoneda());
		CantidadMonetaria totalf=new CantidadMonetaria(analisis.getTOTALF(),analisis.getTipoDeMoneda());
		CantidadMonetaria impuestof=new CantidadMonetaria(analisis.getIMPUESTOF(),analisis.getTipoDeMoneda());
		
		analisis.setImporteEnFactura(importef);
		analisis.setTotalEnFactura(totalf);
		analisis.setImpuestoEnFactura(impuestof);
		*/
		if(analisis.getTipoDeMoneda().equals(CantidadMonetaria.DOLARES)){
			analisis.setTC(analisis.getTC2());
		}else if(analisis.getTipoDeMoneda().equals(CantidadMonetaria.EUROS)){
			analisis.setTC(analisis.getTC3());
		}
		
		BigDecimal tc=BigDecimal.valueOf(analisis.getTC().doubleValue());
		analisis.setImporteMN(analisis.getImporteEnFactura().multiply(tc));
		analisis.setImpuestoMN(analisis.getImpuestoEnFactura().multiply(tc));
		analisis.setTotalMN(analisis.getTotalEnFactura().multiply(tc));
		
	}
	
	public void recalcularImportes(){
		CantidadMonetaria nvo=new CantidadMonetaria(0,getTipoDeMoneda());
		for(Facxpde d:getPartidas()){
			System.out.println("Agregando importe por: "+d.getImporte());
			nvo=nvo.add(d.getImporte());
		}
		setImporte(nvo);
		setImpuesto(MonedasUtils.calcularImpuesto(getImporte()));
		setTotal(MonedasUtils.calcularTotal(getImporte()));
		BigDecimal tc=BigDecimal.valueOf(getTC().doubleValue());
		if(getTotal().getCurrency().equals(CantidadMonetaria.DOLARES)){
			tc=BigDecimal.valueOf(getTC2().doubleValue());
		}else if(getTotal().getCurrency().equals(CantidadMonetaria.EUROS)){
			tc=BigDecimal.valueOf(getTC3().doubleValue());
		}
		setTC(tc.doubleValue());
		setImporteMN(new CantidadMonetaria(getImporteEnFactura().multiply(tc).amount().doubleValue(),CantidadMonetaria.PESOS));
		setImpuestoMN(new CantidadMonetaria(getImpuestoEnFactura().multiply(tc).amount().doubleValue(),CantidadMonetaria.PESOS));
		setTotalMN(new CantidadMonetaria(getTotalEnFactura().multiply(tc).amount().doubleValue(),CantidadMonetaria.PESOS));
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
		setVTO(c.getTime());
	}
	
	public void calcularDescuentoFinanciero(){
		BigDecimal df=getProveedor().getDescuentoFinanciero()!=null?getProveedor().getDescuentoFinanciero():BigDecimal.ZERO;
		setDSCTOF(df.doubleValue());
		if(df.equals(BigDecimal.ZERO))return;
		Calendar c=Calendar.getInstance();
		final Date inicio;
		if(getProveedor().getVencimientoEstipulado()==1){
			inicio=getFECHA();
		}else{
			inicio=getCreado();
		}
		c.setTime(inicio);
		c.getTime();
		int dias=getProveedor().getDiasDF();
		c.add(Calendar.DATE,dias);
		setVTOD(c.getTime());
	}

	public CXPFactura getCargo() {
		return cargo;
	}

	public void setCargo(CXPFactura cargo) {
		this.cargo = cargo;
	}
    
	
 
}

