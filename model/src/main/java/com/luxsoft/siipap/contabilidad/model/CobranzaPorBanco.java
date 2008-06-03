package com.luxsoft.siipap.contabilidad.model;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.domain.CantidadMonetaria;

public class CobranzaPorBanco implements Comparable<CobranzaPorBanco>{
	
	
	private String banco;
	private String cuenta;
	private Long deposito;
	private CantidadMonetaria importe;
	private FormaDePago formaDePago;
	private int sucursalId;
	
	
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}
	
	
	public double getImporteAsDouble(){
		return getImporte().amount().doubleValue();
	}
	
	/**"BANAMEX   (1858193)"
						,"HSBC	    (4019118074)"
						,"SCOTTIA   (1691945)"
						,"SANTANDER (92000395043)"
	 * 
	 * @return
	 */
	public String getCuentaContable(){
		String cta=getBanco();
		if(cta.startsWith("BANCOMER")){
			return "102-0001-000";
		}else if(cta.startsWith("BANAMEX")){
			return "102-0002-000";
		}else if(cta.startsWith("HSBC")){
			return "102-0004-000";
		}else if(cta.startsWith("SCOTTIA")){
			return "102-0005-000";
		}else if(cta.startsWith("SANTANDER")){
			return "102-0008-000";
		}else
			return "ERRROR";
	}
	
	public String toString(){		
		return ToStringBuilder
		.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public String getConcepto(){
		String pattern="{0}{1} {2} ";
		String res= MessageFormat.format(pattern, ""
				,getBanco()
				,getDeposito()!=null?"Dep:"+getDeposito():"F.P: "+StringUtils.substring(getFormaDePago().toString(), 0,7));
		return StringUtils.substring(res, 0, 27);
	}
	

	
	public Long getDeposito() {
		return deposito;
	}
	public void setDeposito(Long deposito) {
		this.deposito = deposito;
	}
	
	
	public int compareTo(CobranzaPorBanco o) {
		return getCuenta().compareTo(o.getCuenta());
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	
	public FormaDePago getFormaDePago() {
		return formaDePago;
	}
	public void setFormaDePago(FormaDePago formaDePago) {
		this.formaDePago = formaDePago;
	}
	public int getSucursalId() {
		return sucursalId;
	}
	public void setSucursalId(int sucursalId) {
		this.sucursalId = sucursalId;
	}
	

}
