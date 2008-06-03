package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.utils.domain.PersistentObject;

public class Crecibos extends PersistentObject{
	
	
	private int NUMERO; 		//NUMERO CHAR(8)
	private String CLAVE; 		//CLAVE  CHAR(4)
	private String PROVEEDOR; 	//PROVEEDOR CHAR(55)
	private Long CXP; 			//CXP CHAR(8)
	private Long CRECIBO; 		//CRECIBO CHAR(10)
	private Date FCRECIBO=Calendar.getInstance().getTime(); //FCREIBO DATE
	private String ELABORO; 	//ELABORO CHAR(20)
	private BigDecimal tc1=BigDecimal.ONE; //TC1 FLOAT(126)
	private BigDecimal tc2; 	//TC2 FLOAT(126)
	private BigDecimal tc3; 	//TC3 FLOAT(126)
	private BigDecimal tc4; 	//TC4 FLOAT(126)
	private BigDecimal tc5; 	//TC5 FLOAT(126)	
	private CantidadMonetaria importeNC=CantidadMonetaria.pesos(0); //IMPNC  FLOAT(126)
	private String conceptoNC; 	//NNC CHAR(100)
	
	
	private Proveedor proveedor;
	
	private Date creado=Calendar.getInstance().getTime();
	private Date modificado=Calendar.getInstance().getTime();
	
	private Set<Crecibosde> partidas=new HashSet<Crecibosde>();
	

	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		Object old=this.proveedor;
		this.proveedor = proveedor;
		getPropertyChangeSupport().firePropertyChange("proveedor",old,proveedor);
	}
	
	public String getCLAVE() {
		return CLAVE;
	}
	public void setCLAVE(String clave) {
		CLAVE = clave;
	}
	public Long getCRECIBO() {
		return CRECIBO;
	}
	public void setCRECIBO(Long crecibo) {
		CRECIBO = crecibo;
	}
	public Long getCXP() {
		return CXP;
	}
	public void setCXP(Long cxp) {
		CXP = cxp;
	}
	public String getELABORO() {
		return ELABORO;
	}
	public void setELABORO(String elaboro) {
		ELABORO = elaboro;
	}
	public Date getFCRECIBO() {
		return FCRECIBO;
	}
	public void setFCRECIBO(Date fcrecibo) {
		FCRECIBO = fcrecibo;
	}
	
	public String getConceptoNC() {
		return conceptoNC;
	}
	public void setConceptoNC(String conceptoNC) {
		this.conceptoNC = conceptoNC;
	}
	public CantidadMonetaria getImporteNC() {
		return importeNC;
	}
	public void setImporteNC(CantidadMonetaria importeNC) {
		this.importeNC = importeNC;
	}
	public int getNUMERO() {
		return NUMERO;
	}
	public void setNUMERO(int numero) {
		NUMERO = numero;
	}
	public String getPROVEEDOR() {
		return PROVEEDOR;
	}
	public void setPROVEEDOR(String proveedor) {
		PROVEEDOR = proveedor;
	}
	public BigDecimal getTc1() {
		return tc1;
	}
	public void setTc1(BigDecimal tc1) {
		this.tc1 = tc1;
	}
	public BigDecimal getTc2() {
		return tc2;
	}
	public void setTc2(BigDecimal tc2) {
		this.tc2 = tc2;
	}
	public BigDecimal getTc3() {
		return tc3;
	}
	public void setTc3(BigDecimal tc3) {
		this.tc3 = tc3;
	}
	public BigDecimal getTc4() {
		return tc4;
	}
	public void setTc4(BigDecimal tc4) {
		this.tc4 = tc4;
	}
	public BigDecimal getTc5() {
		return tc5;
	}
	public void setTc5(BigDecimal tc5) {
		this.tc5 = tc5;
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
	
	

	public Set<Crecibosde> getPartidas() {
		return partidas;
	}
	public void setPartidas(Set<Crecibosde> partidas) {
		this.partidas = partidas;
	}
	
	public boolean addCrecibosde(Crecibosde det){
		det.setCrecibos(this);
		return getPartidas().add(det);
	}
	public boolean removeCrecibosde(Crecibosde det){
		//det.setCrecibos(null);
		return getPartidas().remove(det);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		Crecibos c=(Crecibos)obj;
		return new EqualsBuilder()
			.append(c.getProveedor(),getProveedor())
			.append(getCreado(),c.getCreado())
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
		.append(getProveedor())
		.append(getFCRECIBO())
		.append(getId())
		.toString();
	}
	
	public void fijarProveedor(){
		if(getProveedor()!=null){
			setPROVEEDOR(getProveedor().getNombre());
			setCLAVE(getProveedor().getClave());
		}
	}

}
