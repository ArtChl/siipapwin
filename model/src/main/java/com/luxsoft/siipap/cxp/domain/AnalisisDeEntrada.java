package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.Salida;
import com.luxsoft.utils.domain.PersistentObject;

public class AnalisisDeEntrada extends PersistentObject{
	
	//Referencias
	private Entrada com;
	private Salida dec;
	
	//private Set<AnalisisDet> analisis=new HashSet<AnalisisDet>();
	
	//Propiedades del analisis
	private BigDecimal ingresada=BigDecimal.ZERO;
	private BigDecimal analizada=BigDecimal.ZERO;
	private BigDecimal porAnalizar=BigDecimal.ZERO;;
	
	//Propiedades del articulo	
	private String clave;
	private String descripcion;
	private Integer factorUnitario;
	private String unidad;
	
	//Propiedades de la Entrada	
	private Long 	FACREM;
	private Date 	FECREM;	
	private Integer SUCURSAL;
	private String 	PROVNOMBR;
	private String 	PROVCLAVE;
	private Long 	NUMCOM;
	private Date   	FECCOM;
	private Long 	SUCCOM;
	private String 	TIPCOM;
	private Long 	COM;
	private int renglon;
	private Date 	FENT;

	//Transaccional
	private Date creado=currentTime();
	private int version;
	
	//Nuevos campos ocupados por Maquila
	private double analizadoHojas;
	private double analizadoBobinas;	
	private double analizadoCXP;
	
	
	
	public AnalisisDeEntrada(){
		
	}
	
	public AnalisisDeEntrada(final Entrada com){
		this.com=com;
		this.clave=com.getALMARTIC();
		this.descripcion=com.getALMNOMBR();
		this.unidad=com.getALMUNIDMED();
		this.factorUnitario=com.getALMUNIXUNI();
		
		this.SUCURSAL=com.getALMSUCUR();
		Assert.notNull(com.getMvcomp(),"Entrada com sin referencia a Mvccom:  "+com);
		this.FACREM=com.getMvcomp().getMVCFACREM();
		this.FECREM=com.getMvcomp().getMVCFECREM();
		this.PROVNOMBR=com.getMvcomp().getMVCNOMBPRO();		
		PROVCLAVE=com.getMvcomp().getMVCPROVEE();
		NUMCOM=com.getMvcomp().getMVCNUMCOM();
		FECCOM=com.getMvcomp().getMVCFECCOM();
		SUCCOM=com.getMvcomp().getMVCSUCCOM();
		TIPCOM=com.getMvcomp().getMVCTIPCOM();
		COM=com.getALMNUMER();
		FENT=com.getALMFECHA();
		this.renglon=com.getALMRENGL();
		setCantidad(com.getALMCANTI());
		
	}
	
	private void setCantidad(Long cantidad){
		BigDecimal unis=BigDecimal.valueOf(getFactorUnitario());
		BigDecimal unidades=BigDecimal.valueOf(cantidad);
		BigDecimal res=unidades.divide(unis,5,RoundingMode.HALF_EVEN);
		setIngresada(res);
	}
	
	
/*
	public Set<AnalisisDet> getAnalisis() {
		return analisis;
	}

	public void setAnalisis(Set<AnalisisDet> analisis) {
		this.analisis = analisis;
	}*/

	public int getRenglon() {
		return renglon;
	}

	public void setRenglon(int renglon) {
		this.renglon = renglon;
	}

	public BigDecimal getAnalizada() {
		return analizada;
	}

	public void setAnalizada(BigDecimal analizada) {
		this.analizada = analizada;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Entrada getCom() {
		return com;
	}

	public void setCom(Entrada com) {
		this.com = com;
	}

	public Long getCOM() {
		return COM;
	}

	public void setCOM(Long com) {
		COM = com;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public Salida getDec() {
		return dec;
	}

	public void setDec(Salida dec) {
		this.dec = dec;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getFACREM() {
		return FACREM;
	}

	public void setFACREM(Long facrem) {
		FACREM = facrem;
	}

	public Integer getFactorUnitario() {
		return factorUnitario;
	}

	public void setFactorUnitario(Integer factorUnitario) {
		this.factorUnitario = factorUnitario;
	}

	public Date getFECCOM() {
		return FECCOM;
	}

	public void setFECCOM(Date feccom) {
		FECCOM = feccom;
	}

	public Date getFECREM() {
		return FECREM;
	}

	public void setFECREM(Date fecrem) {
		FECREM = fecrem;
	}

	public Date getFENT() {
		return FENT;
	}

	public void setFENT(Date fent) {
		FENT = fent;
	}

	public BigDecimal getIngresada() {
		return ingresada;
	}

	public void setIngresada(BigDecimal ingresada) {
		this.ingresada = ingresada;
	}

	public Long getNUMCOM() {
		return NUMCOM;
	}

	public void setNUMCOM(Long numcom) {
		NUMCOM = numcom;
	}

	public BigDecimal getPorAnalizar() {
		return porAnalizar;
	}

	public void setPorAnalizar(BigDecimal porAnalizar) {
		this.porAnalizar = porAnalizar;
	}

	public String getPROVCLAVE() {
		return PROVCLAVE;
	}

	public void setPROVCLAVE(String provclave) {
		PROVCLAVE = provclave;
	}

	public String getPROVNOMBR() {
		return PROVNOMBR;
	}

	public void setPROVNOMBR(String provnombr) {
		PROVNOMBR = provnombr;
	}

	public Long getSUCCOM() {
		return SUCCOM;
	}

	public void setSUCCOM(Long succom) {
		SUCCOM = succom;
	}

	public Integer getSUCURSAL() {
		return SUCURSAL;
	}

	public void setSUCURSAL(Integer sucursal) {
		SUCURSAL = sucursal;
	}

	public String getTIPCOM() {
		return TIPCOM;
	}

	public void setTIPCOM(String tipcom) {
		TIPCOM = tipcom;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

		@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		AnalisisDeEntrada ae=(AnalisisDeEntrada)obj;
		return new EqualsBuilder()
		.append(getCom().getId(),ae.getCom().getId())
		.isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getCom().getId())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Suc",getSUCURSAL())
		.append("Com",getCOM())
		.append("Fecha",getFENT())
		.append(getClave())
		.append("Ingresadas",getIngresada())
		.append("Analizadas",getAnalizada())
		.toString();
	}
	
	public void aplicarAnalisis(AnalisisDet det){
		det.setCantidad(getPorAnalizar());
	}
	
	public void agregarAnalisis(AnalisisDet a){
		BigDecimal res=getAnalizada().add(a.getCantidad());
		Assert.isTrue(getIngresada().compareTo(res)>0,"Lo analizado no puede superar lo ingresado");
		a.setEntrada(this);
		setAnalizada(res);
	}
	
	public void eliminarAnalisis(AnalisisDet a){
		Assert.isTrue(getAnalizada().compareTo(a.getCantidad())>=0,"La cantidad no corresponde");		
		BigDecimal res=getAnalizada().subtract(a.getCantidad());
		setAnalizada(res);
		
	}

	public double getAnalizadoBobinas() {
		return analizadoBobinas;
	}

	public void setAnalizadoBobinas(double analizadoBobinas) {
		this.analizadoBobinas = analizadoBobinas;
	}

	public double getAnalizadoHojas() {
		return analizadoHojas;
	}

	public void setAnalizadoHojas(double analizadoHojas) {
		this.analizadoHojas = analizadoHojas;
	}
	
	public BigDecimal getPorAnalizarHojas(){
		BigDecimal hojeado=new BigDecimal(getAnalizadoHojas(),new MathContext(5));
		return getIngresada().subtract(hojeado);
		
	}
	
	public BigDecimal getPorAnalizarKilos(){
		BigDecimal kilos=new BigDecimal(getAnalizadoBobinas(),new MathContext(5));
		return getIngresada().subtract(kilos);
		
	}

	public double getAnalizadoCXP() {
		return analizadoCXP;
	}

	public void setAnalizadoCXP(double analizadoCXP) {
		this.analizadoCXP = analizadoCXP;
	}

	
	
	

}
