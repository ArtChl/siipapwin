package com.luxsoft.siipap.clipper.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


public class Arsald implements Serializable {

    private Long id;
    private Integer SASUCURSAL;
    private String SAARTICULO;
    private Long SASALDINI;
    private Long SASALDACT;
    private Long SASALANTAJ;
    private Long SASALDESAJ;
    private Date SAULTOPER;
    private Date SAFECHAJU;
    private Long SASALDEXTE;
    private Date SAFECHEXTE;
    private Long SAINIDETAJ;
    private Long SATOTPEDPE;
    private Date SAULTPEDEL;
    private Date SAULTENTPE;
    private Long SATOTPEDCL;
    private Date SAULTVENTA;
    private Long SAACUVENTA;
    private BigDecimal SAACUIMPVT;
    private Long SAACUDEVVE;
    private Long SAACUIMPDV;
    private Long SAMOVXATE;
    private Long SAUNIENCOR;
    private String SAMODENCOR;
    private String periodo;

    
    public Arsald() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSAACUDEVVE() {
        return SAACUDEVVE;
    }
    public void setSAACUDEVVE(Long saacudevve) {
        SAACUDEVVE = saacudevve;
    }
    public Long getSAACUIMPDV() {
        return SAACUIMPDV;
    }
    public void setSAACUIMPDV(Long saacuimpdv) {
        SAACUIMPDV = saacuimpdv;
    }
    public BigDecimal getSAACUIMPVT() {
        return SAACUIMPVT;
    }
    public void setSAACUIMPVT(BigDecimal saacuimpvt) {
        SAACUIMPVT = saacuimpvt;
    }
    public Long getSAACUVENTA() {
        return SAACUVENTA;
    }
    public void setSAACUVENTA(Long saacuventa) {
        SAACUVENTA = saacuventa;
    }
    public String getSAARTICULO() {
        return SAARTICULO;
    }
    public void setSAARTICULO(String saarticulo) {
        SAARTICULO = saarticulo;
    }
    public Date getSAFECHAJU() {
        return SAFECHAJU;
    }
    public void setSAFECHAJU(Date safechaju) {
        SAFECHAJU = safechaju;
    }
    public Date getSAFECHEXTE() {
        return SAFECHEXTE;
    }
    public void setSAFECHEXTE(Date safechexte) {
        SAFECHEXTE = safechexte;
    }
    public Long getSAINIDETAJ() {
        return SAINIDETAJ;
    }
    public void setSAINIDETAJ(Long sainidetaj) {
        SAINIDETAJ = sainidetaj;
    }
    public String getSAMODENCOR() {
        return SAMODENCOR;
    }
    public void setSAMODENCOR(String samodencor) {
        SAMODENCOR = samodencor;
    }
    public Long getSAMOVXATE() {
        return SAMOVXATE;
    }
    public void setSAMOVXATE(Long samovxate) {
        SAMOVXATE = samovxate;
    }
    public Long getSASALANTAJ() {
        return SASALANTAJ;
    }
    public void setSASALANTAJ(Long sasalantaj) {
        SASALANTAJ = sasalantaj;
    }
    public Long getSASALDACT() {
        return SASALDACT;
    }
    public void setSASALDACT(Long sasaldact) {
        SASALDACT = sasaldact;
    }
    public Long getSASALDESAJ() {
        return SASALDESAJ;
    }
    public void setSASALDESAJ(Long sasaldesaj) {
        SASALDESAJ = sasaldesaj;
    }
    public Long getSASALDEXTE() {
        return SASALDEXTE;
    }
    public void setSASALDEXTE(Long sasaldexte) {
        SASALDEXTE = sasaldexte;
    }
    public Long getSASALDINI() {
        return SASALDINI;
    }
    public void setSASALDINI(Long sasaldini) {
        SASALDINI = sasaldini;
    }
    public Integer getSASUCURSAL() {
        return SASUCURSAL;
    }
    public void setSASUCURSAL(Integer sasucursal) {
        SASUCURSAL = sasucursal;
    }
    public Long getSATOTPEDCL() {
        return SATOTPEDCL;
    }
    public void setSATOTPEDCL(Long satotpedcl) {
        SATOTPEDCL = satotpedcl;
    }
    public Long getSATOTPEDPE() {
        return SATOTPEDPE;
    }
    public void setSATOTPEDPE(Long satotpedpe) {
        SATOTPEDPE = satotpedpe;
    }
    public Date getSAULTENTPE() {
        return SAULTENTPE;
    }
    public void setSAULTENTPE(Date saultentpe) {
        SAULTENTPE = saultentpe;
    }
    public Date getSAULTOPER() {
        return SAULTOPER;
    }
    public void setSAULTOPER(Date saultoper) {
        SAULTOPER = saultoper;
    }
    public Date getSAULTPEDEL() {
        return SAULTPEDEL;
    }
    public void setSAULTPEDEL(Date saultpedel) {
        SAULTPEDEL = saultpedel;
    }
    public Date getSAULTVENTA() {
        return SAULTVENTA;
    }
    public void setSAULTVENTA(Date saultventa) {
        SAULTVENTA = saultventa;
    }
    public Long getSAUNIENCOR() {
        return SAUNIENCOR;
    }
    public void setSAUNIENCOR(Long sauniencor) {
        SAUNIENCOR = sauniencor;
    }
    
    

    public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	
	public boolean equals(Object other){
        Arsald castOther=(Arsald)other;
        return new EqualsBuilder()
        		.append(this.SASUCURSAL,castOther.getSASUCURSAL())
        		.append(this.SAARTICULO,castOther.getSAARTICULO())
        		.append(this.periodo,castOther.getPeriodo())
        		.isEquals();
    }
    
    public int hashCode(){
        return new HashCodeBuilder(17,37)
        	.append(this.SASUCURSAL)
        	.append(this.SAARTICULO)
        	.append(this.periodo)
        	.toHashCode();
    }

	public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .append("sasucursal", getSASUCURSAL())
            .append("saarticulo", getSAARTICULO())
            .append("periodo", getSAARTICULO())
            .toString();
    }

}
