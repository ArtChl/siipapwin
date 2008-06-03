/*
 * Created on 12/07/2005
 *
 * TODO Colocar información de Licencia
 */

package com.luxsoft.siipap.cxp.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Precio;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;


/**
 * @author Rubén Lugo
 *
 */

public  class  Facxpde extends PersistentObject{
	
	
	private Integer NUMERO;
	private String REFERENCIA;
	private String ARTCLAVE;
	private String ARTNOMBR;
	private String UNIDAD;
	private String LLAVED;
	
	private Long CANTIDAD;
		
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private CantidadMonetaria neto=CantidadMonetaria.pesos(0);
	private CantidadMonetaria netoMN=CantidadMonetaria.pesos(0);
	
	private Double DESCUENTO1=0d;
	private Double DESCUENTO2=0d;
	private Double DESCUENTO3=0d;
	private Double DESCUENTO4=0d;
	private Double DESCUENTO5=0d;
	private Double DESCUENTO6=0d;
	private Double DESCUENTO7=0d;
	private Double DESCUENTO8=0d;
	private Double DESCUENTOF=0d;
	private String LISTA;
	private String MONEDAP;
	private Double TCP;
	private String CLAVE;
	private Long FACREM;
	private Date FECREM;
	private Integer RENGL;
	private Integer SUCURSAL;
	private String PROVNOMBR;
	private String PROVCLAVE;
	private Double NUMCOM;
	private Date FECCOM;
	private Double SUCCOM;
	private String TIPCOM;
	private Long COM;
	private long CANTDEV;
	private String DEVO;
	private Date FENT;
	private Facxp facxp;

	private Precio precio; //Precio de lista de precios
	private CompraIngresada compraIngresada; 
	
	

	public Facxpde(){
	}
	
	public BigDecimal getCantidadUnitaria(){
		if(getUNIDAD().equalsIgnoreCase("MIL")){
			BigDecimal val=BigDecimal.valueOf(getCANTIDAD());
			BigDecimal uni=BigDecimal.valueOf(1000);
			return val.divide(uni,3,RoundingMode.HALF_EVEN);
		}
		return BigDecimal.valueOf(getCANTIDAD());
			
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
	public long getCANTDEV() {
		return CANTDEV;
	}
	public void setCANTDEV(long cantdev) {
		CANTDEV = cantdev;
	}
	public Long getCANTIDAD() {
		return CANTIDAD;
	}
	public void setCANTIDAD(Long cantidad) {
		Object old=cantidad;		
		CANTIDAD = cantidad;
		getPropertyChangeSupport().firePropertyChange("CANTIDAD",old,cantidad);
	}
	public String getCLAVE() {
		return CLAVE;
	}
	public void setCLAVE(String clave) {
		CLAVE = clave;
	}
	
	public Long getCOM() {
		return COM;
	}
	
	public void setCOM(Long com) {
		COM = com;
	}
	
	public Double getDESCUENTO1() {
		return DESCUENTO1;
	}
	public void setDESCUENTO1(Double descuento1) {
		Object old=this.DESCUENTO1;
		DESCUENTO1 = descuento1;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO1",old,descuento1);
	}
	public Double getDESCUENTO2() {
		return DESCUENTO2;
	}
	public void setDESCUENTO2(Double descuento2) {
		Object old=this.DESCUENTO2;
		DESCUENTO2 = descuento2;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO2",old,descuento2);
	}
	
	public Double getDESCUENTO3() {
		return DESCUENTO3;
	}
	public void setDESCUENTO3(Double descuento3) {
		Object old=this.DESCUENTO3;
		DESCUENTO3 = descuento3;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO3",old,descuento3);
	}
	public Double getDESCUENTO4() {
		return DESCUENTO4;
	}
	public void setDESCUENTO4(Double descuento4) {
		Object old=this.DESCUENTO4;
		DESCUENTO4 = descuento4;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO4",old,descuento4);
	}
	public Double getDESCUENTO5() {
		return DESCUENTO5;
	}
	public void setDESCUENTO5(Double descuento5) {
		Object old=this.DESCUENTO5;
		DESCUENTO5 = descuento5;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO5",old,descuento5);
	}
	public Double getDESCUENTO6() {
		return DESCUENTO6;
	}
	public void setDESCUENTO6(Double descuento6) {
		Object old=this.DESCUENTO6;
		DESCUENTO6 = descuento6;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO6",old,descuento6);
	}
	public Double getDESCUENTO7() {
		return DESCUENTO7;
	}
	public void setDESCUENTO7(Double descuento7) {
		Object old=this.DESCUENTO7;
		DESCUENTO7 = descuento7;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO7",old,descuento7);
	}
	public Double getDESCUENTO8() {
		return DESCUENTO8;
	}
	public void setDESCUENTO8(Double descuento8) {
		Object old=this.DESCUENTO8;
		DESCUENTO8 = descuento8;
		getPropertyChangeSupport().firePropertyChange("DESCUENTO8",old,descuento8);
	}
	public Double getDESCUENTOF() {
		return DESCUENTOF;
	}
	public void setDESCUENTOF(Double descuentof) {
		Object old=this.DESCUENTOF;
		DESCUENTOF = descuentof;
		getPropertyChangeSupport().firePropertyChange("DESCUENTOF",old,descuentof);
	}
	public String getDEVO() {
		return DEVO;
	}
	public void setDEVO(String devo) {
		DEVO = devo;
	}
	public Long getFACREM() {
		return FACREM;
	}
	public void setFACREM(Long facrem) {
		FACREM = facrem;
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
	
	public String getLISTA() {
		return LISTA;
	}
	public void setLISTA(String lista) {
		LISTA = lista;
	}
	public String getLLAVED() {
		return LLAVED;
	}
	public void setLLAVED(String llaved) {
		LLAVED = llaved;
	}
	public String getMONEDAP() {
		return MONEDAP;
	}
	public void setMONEDAP(String monedap) {
		MONEDAP = monedap;
	}
	
	public Double getNUMCOM() {
		return NUMCOM;
	}
	public void setNUMCOM(Double numcom) {
		NUMCOM = numcom;
	}
	public Integer getNUMERO() {
		return NUMERO;
	}
	public void setNUMERO(Integer numero) {
		NUMERO = numero;
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
	public String getREFERENCIA() {
		return REFERENCIA;
	}
	public void setREFERENCIA(String referencia) {
		REFERENCIA = referencia;
	}
	public Integer getRENGL() {
		return RENGL;
	}
	public void setRENGL(Integer rengl) {
		RENGL = rengl;
	}
	public Double getSUCCOM() {
		return SUCCOM;
	}
	public void setSUCCOM(Double succom) {
		SUCCOM = succom;
	}
	public Integer getSUCURSAL() {
		return SUCURSAL;
	}
	public void setSUCURSAL(Integer sucursal) {
		SUCURSAL = sucursal;
	}
	public Double getTCP() {
		return TCP;
	}
	public void setTCP(Double tcp) {
		TCP = tcp;
	}
	public String getTIPCOM() {
		return TIPCOM;
	}
	public void setTIPCOM(String tipcom) {
		TIPCOM = tipcom;
	}
	public String getUNIDAD() {
		return UNIDAD;
	}
	public void setUNIDAD(String unidad) {
		UNIDAD = unidad;
	}
	public Facxp getFacxp() {
		return facxp;
	}
	public void setFacxp(Facxp facxp) {
		this.facxp = facxp;
	}	

	public CompraIngresada getCompraIngresada() {
		return compraIngresada;
	}
	public void setCompraIngresada(CompraIngresada com) {
		Object old=this.compraIngresada;
		this.compraIngresada = com;
		getPropertyChangeSupport().firePropertyChange("compraIngresada",old,com);
	}

	public Precio getPrecio() {
		return precio;
	}

	public void setPrecio(Precio precio) {
		Object old=this.precio;
		this.precio = precio;
		getPropertyChangeSupport().firePropertyChange("precio",old,precio);
	}

	public CantidadMonetaria getCosto() {
		return costo;
	}

	public void setCosto(CantidadMonetaria costo) {
		Object old=this.costo;
		this.costo = costo;
		getPropertyChangeSupport().firePropertyChange("costo",old,costo);
	}

	public CantidadMonetaria getImporte() {
		return importe;
	}

	public void setImporte(CantidadMonetaria importe) {
		Object old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
	}

	public CantidadMonetaria getNeto() {
		return neto;
	}

	public void setNeto(CantidadMonetaria neto) {
		Object old=this.neto;		
		this.neto = neto;
		getPropertyChangeSupport().firePropertyChange("neto",old,neto);
	}

	public CantidadMonetaria getNetoMN() {
		return netoMN;
	}

	public void setNetoMN(CantidadMonetaria netoMN) {
		this.netoMN = netoMN;
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
        if(obj==null) return false;
        if(obj==this) return true;
        Facxpde num=(Facxpde)obj;
        return new EqualsBuilder()
                .append(this.ARTCLAVE,num.getARTCLAVE())
                .append(this.creado,num.getCreado())
                .isEquals();
    }

    public int hashCode() {        
        return new HashCodeBuilder(17,35)
        	.append(ARTCLAVE)
        	.append(creado)
        	.toHashCode();
    }
	
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)			
			.append(getARTCLAVE())			
			.append(getFACREM())
			.append(getFECREM())
			.append(getCOM())
			.append(getNeto())
			.toString();
	}
	
	public String toString2(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)			
			.append(getARTCLAVE())			
			.append(getFACREM())
			.append(getFECREM())
			.append(getCOM())
			.append("Precio:"+getCosto())
			.append("Neto:"+getNeto())
			.append("Importe:"+getImporte())
			//.append("Precio LP:"+getPrecio().getNeto())
			.toString();
	}
	
	/**
	 * Recalcula los costos a partir del Precio
	 *
	 */
	/*
	public void recalcularNeto(){
		if(getPrecio()!=null){			
			setCosto(getPrecio().getPrecio());
			setNeto(getPrecio().getNeto());			
		}
		recalcularNetoConDescuentos();
	}	
	*/
	
	public void reset(Currency c){		
		setDESCUENTO1(0d);
		setDESCUENTO2(0d);
		setDESCUENTO3(0d);
		setDESCUENTO4(0d);
		setDESCUENTO5(0d);
		setDESCUENTO6(0d);
		setDESCUENTO7(0d);
		setDESCUENTO8(0d);
		setCosto(new CantidadMonetaria(0d,c));
		setNeto(getCosto());
		setNetoMN(CantidadMonetaria.pesos(0d));
		setImporte(new CantidadMonetaria(0d,c));
	}
	
	/**
	 * Calculamos el importe suponiendo que el neto esta bien
	 *
	 */
	public void recalcularImportesConNeto(){
		BigDecimal unidades=BigDecimal.valueOf(getCompraIngresada().getCom().getALMUNIXUNI());
		BigDecimal cantidad=BigDecimal.valueOf(getCANTIDAD());
		cantidad=cantidad.divide(unidades,3,RoundingMode.HALF_EVEN);
		recalcularNetoConDescuentos();
		System.out.println("Precio: "+getCosto());
		System.out.println("Neto: "+getNeto());
		System.out.println("Importe Antes: "+getImporte());
		CantidadMonetaria importe=getNeto().multiply(cantidad);
		setImporte(importe);
		System.out.println("Importe Despues: "+getImporte());
	}
	
	public void recalcularImportes(){		
		if(getCompraIngresada()!=null){
			//recalcularNeto();
			BigDecimal unidades=BigDecimal.valueOf(getCompraIngresada().getCom().getALMUNIXUNI());
			BigDecimal cantidad=BigDecimal.valueOf(getCANTIDAD());
			cantidad=cantidad.divide(unidades,3,RoundingMode.HALF_EVEN);
			
			//Calculamos el subtotal
			CantidadMonetaria subTotal=getCosto().multiply(cantidad);
			System.out.println("Sub Total: "+subTotal);
			//Aplicamos los descuentos
			/*
			BigDecimal[] desc={
					 BigDecimal.valueOf(getDESCUENTO1().doubleValue())
					,BigDecimal.valueOf(getDESCUENTO2().doubleValue())
					,BigDecimal.valueOf(getDESCUENTO3().doubleValue())
					,BigDecimal.valueOf(getDESCUENTO4().doubleValue())
					,BigDecimal.valueOf(getDESCUENTO5().doubleValue())
					,BigDecimal.valueOf(getDESCUENTO6().doubleValue())
					,BigDecimal.valueOf(getDESCUENTO7().doubleValue())
					,BigDecimal.valueOf(getDESCUENTO8().doubleValue())
			};
			
			for(BigDecimal d:desc){
				System.out.println("Descuentos: "+d);
			}	
			*/
			Double[] desc={getDESCUENTO1(),getDESCUENTO2(),getDESCUENTO3(),getDESCUENTO4(),getDESCUENTO5(),getDESCUENTO6(),getDESCUENTO7(),getDESCUENTO8()};
			CantidadMonetaria total=MonedasUtils.aplicarDescuentosEnCascada(subTotal,desc);
			System.out.println("Importe calculado: "+total);
			setImporte(total);
		}
	}
	
	/**
	 * Commodity para fijar los descuentos a partir de Precio
	 *
	 */
	public void fijarDescuentosConPrecio(){
		if(getPrecio()!=null){
			setDESCUENTO1(getPrecio().getDescuento1().doubleValue());
			setDESCUENTO2(getPrecio().getDescuento2().doubleValue());
			setDESCUENTO3(getPrecio().getDescuento3().doubleValue());
			setDESCUENTO4(getPrecio().getDescuento4().doubleValue());
			setDESCUENTO5(getPrecio().getDescuento5().doubleValue());
			setDESCUENTO6(getPrecio().getDescuento6().doubleValue());
			setDESCUENTO7(getPrecio().getDescuento7().doubleValue());
			setDESCUENTO8(getPrecio().getDescuento8().doubleValue());
		}
	}
	
	
	
	/**
	 * Recalcula los importes a partir de los descuentos
	 *
	 */
	public void recalcularNetoConDescuentos(){
		if(getCosto()!=null){
			CantidadMonetaria precio=getCosto();
			if(getDESCUENTO1()!=null && getDESCUENTO1()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO1());
				precio=precio.subtract(d1);
			}
			if(getDESCUENTO2()!=null && getDESCUENTO2()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO2());
				precio=precio.subtract(d1);
			}
			if(getDESCUENTO3()!=null && getDESCUENTO3()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO3());
				precio=precio.subtract(d1);
			}
			if(getDESCUENTO4()!=null && getDESCUENTO4()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO4());
				precio=precio.subtract(d1);
			}
			if(getDESCUENTO5()!=null && getDESCUENTO4()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO5());
				precio=precio.subtract(d1);
			}
			if(getDESCUENTO6()!=null && getDESCUENTO6()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO6());
				precio=precio.subtract(d1);
			}
			if(getDESCUENTO7()!=null && getDESCUENTO7()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO7());
				precio=precio.subtract(d1);
			}
			if(getDESCUENTO8()!=null && getDESCUENTO8()>0){
				CantidadMonetaria d1=precio.multiply(getDESCUENTO8());
				precio=precio.subtract(d1);
			}
			setNeto(precio);
			calcularNetoMN();
		}
	}
	
	public void calcularNetoMN(){
		if(getTCP()!=null && !(getCosto().getCurrency().equals(CantidadMonetaria.PESOS))){			
			BigDecimal tc=BigDecimal.valueOf(getTCP());
			CantidadMonetaria imn=new CantidadMonetaria(getNeto().multiply(tc).amount().doubleValue(),CantidadMonetaria.PESOS);
			setNetoMN(imn);
		}else if(getTCP()!=null && (getCosto().getCurrency().equals(CantidadMonetaria.PESOS))){			
			setNetoMN(getNeto());
		}
	}
	
}
