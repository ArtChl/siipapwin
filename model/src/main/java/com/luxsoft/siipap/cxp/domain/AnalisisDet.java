package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Precio;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;

public class AnalisisDet extends PersistentObject{
	
	private Analisis analisis;
	
	private String ARTCLAVE;
	private String ARTNOMBR;
	private String UNIDAD;
	private BigDecimal cantidad;
		
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private CantidadMonetaria neto=CantidadMonetaria.pesos(0);
	private CantidadMonetaria netoMN=CantidadMonetaria.pesos(0);
	
	private BigDecimal desc1=BigDecimal.ZERO;
	private BigDecimal desc2=BigDecimal.ZERO;
	private BigDecimal desc3=BigDecimal.ZERO;
	private BigDecimal desc4=BigDecimal.ZERO;
	private BigDecimal desc5=BigDecimal.ZERO;
	private BigDecimal desc6=BigDecimal.ZERO;
	private BigDecimal desc7=BigDecimal.ZERO;
	private BigDecimal desc8=BigDecimal.ZERO;
	
	private BigDecimal tc=BigDecimal.ONE;
	
	
	private Precio precio; //Precio de lista de precios
	private AnalisisDeEntrada entrada; 

	private Date creado=currentTime();
	private int version;
	
	//Compatibilidad con facxp
	private int numero;
	private Integer sucursal;
	private Long com;
	private Integer renglon;
	private Date fent;
	 
	
	public AnalisisDet() {
		
	}
	

	public AnalisisDet(AnalisisDeEntrada entrada) {		
		this.entrada = entrada;
		setARTCLAVE(entrada.getClave());
		setARTNOMBR(entrada.getDescripcion());
		//setCantidad(entrada.getIngresada());
		setCantidad(entrada.getPorAnalizar());
		setUNIDAD(entrada.getUnidad());
	}
	
	


	public int getNumero() {
		return numero;
	}


	public void setNumero(int numero) {
		this.numero = numero;
	}


	public Analisis getAnalisis() {
		return analisis;
	}

	public void setAnalisis(Analisis analisis) {
		Object old=this.analisis;		
		this.analisis = analisis;
		getPropertyChangeSupport().firePropertyChange("analisis",old,analisis);
	}

	public String getARTCLAVE() {
		return ARTCLAVE;
	}

	public void setARTCLAVE(String artclave) {
		ARTCLAVE = artclave;
	}

	public String getARTNOMBR() {
		return ARTNOMBR;
	}

	public void setARTNOMBR(String artnombr) {
		ARTNOMBR = artnombr;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		Object old=this.cantidad;
		this.cantidad = cantidad;
		getPropertyChangeSupport().firePropertyChange("cantidad",old,cantidad);
	}

	public CantidadMonetaria getCosto() {
		return costo;
	}

	public void setCosto(CantidadMonetaria costo) {
		Object old=this.costo;		
		this.costo = costo;
		getPropertyChangeSupport().firePropertyChange("costo",old,costo);
	}

	public BigDecimal getDesc1() {
		return desc1;
	}

	public void setDesc1(BigDecimal desc1) {
		Object old=this.desc1;
		this.desc1 = desc1;
		getPropertyChangeSupport().firePropertyChange("desc1",old,desc1);
	}

	public BigDecimal getDesc2() {
		return desc2;
	}

	public void setDesc2(BigDecimal desc2) {
		Object old=this.desc2;
		this.desc2 = desc2;
		getPropertyChangeSupport().firePropertyChange("desc2",old,desc2);
	}

	public BigDecimal getDesc3() {
		return desc3;
	}

	public void setDesc3(BigDecimal desc3) {
		Object old=this.desc3;
		this.desc3 = desc3;
		getPropertyChangeSupport().firePropertyChange("desc3",old,desc3);
	}

	public BigDecimal getDesc4() {
		return desc4;
	}

	public void setDesc4(BigDecimal desc4) {
		Object old=this.desc4;
		this.desc4 = desc4;
		getPropertyChangeSupport().firePropertyChange("desc4",old,desc4);
	}

	public BigDecimal getDesc5() {
		return desc5;
	}

	public void setDesc5(BigDecimal desc5) {
		Object old=this.desc5;
		this.desc5 = desc5;
		getPropertyChangeSupport().firePropertyChange("desc5",old,desc5);
	}

	public BigDecimal getDesc6() {
		return desc6;
	}

	public void setDesc6(BigDecimal desc6) {
		Object old=this.desc6;
		this.desc6 = desc6;
		getPropertyChangeSupport().firePropertyChange("desc6",old,desc6);
	}

	public BigDecimal getDesc7() {
		return desc7;
	}

	public void setDesc7(BigDecimal desc7) {
		Object old=this.desc7;
		this.desc7 = desc7;
		getPropertyChangeSupport().firePropertyChange("desc7",old,desc7);
	}

	public BigDecimal getDesc8() {
		return desc8;
	}

	public void setDesc8(BigDecimal desc8) {
		Object old=this.desc8;
		this.desc8 = desc8;
		getPropertyChangeSupport().firePropertyChange("desc8",old,desc8);
	}

	public AnalisisDeEntrada getEntrada() {
		return entrada;
	}

	public void setEntrada(AnalisisDeEntrada entrada) {
		this.entrada = entrada;
	}

	public CantidadMonetaria getImporte() {
		return importe;
	}

	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}

	public CantidadMonetaria getNeto() {
		return neto;
	}

	public void setNeto(CantidadMonetaria neto) {
		this.neto = neto;
	}

	public CantidadMonetaria getNetoMN() {
		return netoMN;
	}

	public void setNetoMN(CantidadMonetaria netoMN) {
		this.netoMN = netoMN;
	}

	public Precio getPrecio() {
		return precio;
	}

	public void setPrecio(Precio precio) {
		Object old=this.precio;
		this.precio = precio;
		getPropertyChangeSupport().firePropertyChange("precio",old,precio);
	}

	public BigDecimal getTc() {
		return tc;
	}

	public void setTc(BigDecimal tc) {
		this.tc = tc;
	}

	public String getUNIDAD() {
		return UNIDAD;
	}

	public void setUNIDAD(String unidad) {
		UNIDAD = unidad;
	}
	
	

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public BigDecimal[] descuentos(){
		return new BigDecimal[]{getDesc1(),getDesc2(),getDesc3(),getDesc4(),getDesc5(),getDesc6(),getDesc7(),getDesc8()};
	}
	

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		AnalisisDet d=(AnalisisDet)obj;
		return new EqualsBuilder()
		.append(getEntrada(),d.getEntrada())		
		.append(getCreado(),d.getCreado())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getEntrada())		
		.append(getCreado())
		.toHashCode();
	}

	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getEntrada())
		//.append("Analizado: "+getCantidad())
		.append("Articulo: ",getARTCLAVE())
		.append("Analizado: ",getCantidad())
		.append("Importe:",getImporte())
		.toString();
	}
	
	
	public void calcularImportes(){
		setNeto(MonedasUtils.aplicarDescuentosEnCascada(getCosto(),descuentos()));
		setImporte(MonedasUtils.calcularImporteConDescuentos(getCosto(),getCantidad(),descuentos()));
	}

	/** Compatibilidad con FACXPDE **/
	/** Propiedades no persistentes **/
	
	public Long getCom() {
		return com;
	}
	public void setCom(Long com) {
		this.com = com;
	}

	public Integer getRenglon() {
		return renglon;
	}
	public void setRenglon(Integer renglon) {
		this.renglon = renglon;
	}

	public Integer getSucursal() {
		return sucursal;
	}
	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}

	public Date getFent() {
		return fent;
	}
	public void setFent(Date fent) {
		this.fent = fent;
	}
	
	
}
