/*
 * Created on 10/09/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.MutableObject;







/**
 * 
 * @author Ruben Cancino
 * 
 */

public class Sucursal extends MutableObject implements Serializable{
	
	private Long id;
	private int clave;
	private String nombre;
	private int SUCCLAVE;
	private String SUCNOMBRE;
	private String SUCDIREC;
	private String SUCTEL1;
	private String SUCTEL2;
	private String SUCFAX;
	private int SUCPROMIN;
	private int SUCPROMAX;
	private String SUCVENTA;
	private String SUCTIPFAC;
	private String SUCFACNEG;
	private String  SUCCOMPR;
	private Date SUCFECHA;
	private String SUCMATRIZ;
	
	private int SUCCONCOT;
	private int SUCCONPED;
	private int SUCANTCOT;
	private int SUCANTPED;
	private String SUCPMOCOT;
	private String SUCPMOPED;
	private String SUCPMOFCO;
	private String SUCPMOFCR;
	private String SUCPMOPAG;
	private String SUCPMOINV;
	private String SUCPMOGEN;
	private String SUCFACCOB;
	private String SUCFACVEN;
	private String SUCPMOSOL;
	private String SUCPASSWO;
	private String SUCDEPPED;
	
	private int SUCCONDEP;
	private String SUCLPREC;
	private String SUCPASSWU;
	
	
	
	
	public Sucursal (){
	}
	public int getClave() {
		return clave;
	}

	public void setClave(int clave) {
		this.clave = clave;
	}
	public Long getId() {
		return id;
	}


	public int getSUCCLAVE() {
		return SUCCLAVE;
	}
	public void setSUCCLAVE(int succlave) {
		SUCCLAVE = succlave;
	}
	public String getSUCNOMBRE() {
		return SUCNOMBRE;
	}
	public void setSUCNOMBRE(String sucnombre) {
		SUCNOMBRE = sucnombre;
	}
	public void setId(Long id) {
		this.id = id;
	}




	public String getNombre() {
		return nombre;
	}




	public void setNombre(String nombre) {
		this.nombre = nombre;
	}




	public int getSUCANTCOT() {
		return SUCANTCOT;
	}




	public void setSUCANTCOT(int sucantcot) {
		SUCANTCOT = sucantcot;
	}




	public int getSUCANTPED() {
		return SUCANTPED;
	}




	public void setSUCANTPED(int sucantped) {
		SUCANTPED = sucantped;
	}




	public String getSUCCOMPR() {
		return SUCCOMPR;
	}




	public void setSUCCOMPR(String succompr) {
		SUCCOMPR = succompr;
	}




	public int getSUCCONCOT() {
		return SUCCONCOT;
	}




	public void setSUCCONCOT(int succoncot) {
		SUCCONCOT = succoncot;
	}




	public int getSUCCONDEP() {
		return SUCCONDEP;
	}




	public void setSUCCONDEP(int succondep) {
		SUCCONDEP = succondep;
	}




	public int getSUCCONPED() {
		return SUCCONPED;
	}




	public void setSUCCONPED(int succonped) {
		SUCCONPED = succonped;
	}




	public String getSUCDEPPED() {
		return SUCDEPPED;
	}




	public void setSUCDEPPED(String sucdepped) {
		SUCDEPPED = sucdepped;
	}




	public String getSUCDIREC() {
		return SUCDIREC;
	}




	public void setSUCDIREC(String sucdirec) {
		SUCDIREC = sucdirec;
	}




	public String getSUCFACCOB() {
		return SUCFACCOB;
	}




	public void setSUCFACCOB(String sucfaccob) {
		SUCFACCOB = sucfaccob;
	}




	public String getSUCFACNEG() {
		return SUCFACNEG;
	}




	public void setSUCFACNEG(String sucfacneg) {
		SUCFACNEG = sucfacneg;
	}




	public String getSUCFACVEN() {
		return SUCFACVEN;
	}




	public void setSUCFACVEN(String sucfacven) {
		SUCFACVEN = sucfacven;
	}




	public String getSUCFAX() {
		return SUCFAX;
	}




	public void setSUCFAX(String sucfax) {
		SUCFAX = sucfax;
	}




	public Date getSUCFECHA() {
		return SUCFECHA;
	}




	public void setSUCFECHA(Date sucfecha) {
		SUCFECHA = sucfecha;
	}




	public String getSUCLPREC() {
		return SUCLPREC;
	}




	public void setSUCLPREC(String suclprec) {
		SUCLPREC = suclprec;
	}




	public String getSUCMATRIZ() {
		return SUCMATRIZ;
	}




	public void setSUCMATRIZ(String sucmatriz) {
		SUCMATRIZ = sucmatriz;
	}




	public String getSUCPASSWO() {
		return SUCPASSWO;
	}




	public void setSUCPASSWO(String sucpasswo) {
		SUCPASSWO = sucpasswo;
	}




	public String getSUCPASSWU() {
		return SUCPASSWU;
	}




	public void setSUCPASSWU(String sucpasswu) {
		SUCPASSWU = sucpasswu;
	}




	public String getSUCPMOCOT() {
		return SUCPMOCOT;
	}




	public void setSUCPMOCOT(String sucpmocot) {
		SUCPMOCOT = sucpmocot;
	}




	public String getSUCPMOFCO() {
		return SUCPMOFCO;
	}




	public void setSUCPMOFCO(String sucpmofco) {
		SUCPMOFCO = sucpmofco;
	}




	public String getSUCPMOFCR() {
		return SUCPMOFCR;
	}




	public void setSUCPMOFCR(String sucpmofcr) {
		SUCPMOFCR = sucpmofcr;
	}




	public String getSUCPMOGEN() {
		return SUCPMOGEN;
	}




	public void setSUCPMOGEN(String sucpmogen) {
		SUCPMOGEN = sucpmogen;
	}




	public String getSUCPMOINV() {
		return SUCPMOINV;
	}




	public void setSUCPMOINV(String sucpmoinv) {
		SUCPMOINV = sucpmoinv;
	}




	public String getSUCPMOPAG() {
		return SUCPMOPAG;
	}




	public void setSUCPMOPAG(String sucpmopag) {
		SUCPMOPAG = sucpmopag;
	}




	public String getSUCPMOPED() {
		return SUCPMOPED;
	}




	public void setSUCPMOPED(String sucpmoped) {
		SUCPMOPED = sucpmoped;
	}




	public String getSUCPMOSOL() {
		return SUCPMOSOL;
	}




	public void setSUCPMOSOL(String sucpmosol) {
		SUCPMOSOL = sucpmosol;
	}




	public int getSUCPROMAX() {
		return SUCPROMAX;
	}




	public void setSUCPROMAX(int sucpromax) {
		SUCPROMAX = sucpromax;
	}




	public int getSUCPROMIN() {
		return SUCPROMIN;
	}




	public void setSUCPROMIN(int sucpromin) {
		SUCPROMIN = sucpromin;
	}




	public String getSUCTEL1() {
		return SUCTEL1;
	}




	public void setSUCTEL1(String suctel1) {
		SUCTEL1 = suctel1;
	}




	public String getSUCTEL2() {
		return SUCTEL2;
	}




	public void setSUCTEL2(String suctel2) {
		SUCTEL2 = suctel2;
	}




	public String getSUCTIPFAC() {
		return SUCTIPFAC;
	}




	public void setSUCTIPFAC(String suctipfac) {
		SUCTIPFAC = suctipfac;
	}




	public String getSUCVENTA() {
		return SUCVENTA;
	}




	public void setSUCVENTA(String sucventa) {
		SUCVENTA = sucventa;
	}
	
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
			.append(getClave())
			.append(getNombre())
			.toString();
	}




	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		Sucursal ss=(Sucursal)obj;
		return new EqualsBuilder()
			.append(getClave(),ss.getClave())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(getClave())
			.toHashCode();
	}
	
}

	
	