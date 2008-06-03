package com.luxsoft.siipap.clipper.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
 

/** @author Hibernate CodeGenerator */
public class Mvalma implements Serializable {

    private Long id;
    private String MVATIPO;
    private Long MVANUMER;
    private String MVASERIE=" ";
    private String MVATIPFA=" ";
    private Date MVAFECHA;
    private String MVACOMEN;
    private String MVACLIENTE;
    private String MVANOMBCLI;
    private String MVACLAVSOC;
    private String MVADESDES;
    private String MVAPORDES;
    private String MVADESCAR;
    private String MVAPORCAR;
    private long MVANOCOR;
    private long MVAIMCOR;
    private long MVASUCUSO;
    private long MVASUCURE;
    private Integer MVASUCUR;
    private String MVATIPMRE;
    private String MVASERDRE;
    private String MVATIPDRE;
    private long MVANUMERE;
    private Date MVAFECHRE;
    private long MVAMOVCRE;
    private String MVAREFERDE;
    private long MVASUCUAT;
    private String MVATIPMAT;
    private long MVANUMEAT;
    private String MVAOPERA;
    private String MVAUSUAR;
    private Date MVAFEREAL;
    private long MVASUCCXC;
    private long MVAIDECXC;
    private String MVASERCXC;
    private String MVATIPCXC;
    private long MVANUMCXC;
    private Date MVAFECCXC;
    private long MVAVENDORE;
    private String MVACONCEPT;
    private long MVAIMPCONC;
    
    private String MVANOMBSO;
    private String MVACALLE;
    private String MVACOLONIA;
    private String MVADELEGAC;
    private String MVATELEFON;
    private String MVADIREC;
    private long MVAKGSFACT;    
    private String periodo;
    
    private Date creado;
    private Set<Almace> partidas;


    public Mvalma() {
    }

    public Long getId() {
        return this.id;
    }
	
    public void setId(Long id) {
		this.id = id;
	}
    
    

	public Long getMVANUMER() {
		return MVANUMER;
	}
	public void setMVANUMER(Long mvanumer) {
		MVANUMER = mvanumer;
	}
	public Integer getMVASUCUR() {
		return MVASUCUR;
	}
	public void setMVASUCUR(Integer mvasucur) {
		MVASUCUR = mvasucur;
	}
	
	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getMVACALLE() {
		return MVACALLE;
	}
	public void setMVACALLE(String mvacalle) {
		MVACALLE = mvacalle;
	}
	public String getMVACLAVSOC() {
		return MVACLAVSOC;
	}
	public void setMVACLAVSOC(String mvaclavsoc) {
		MVACLAVSOC = mvaclavsoc;
	}
	public String getMVACLIENTE() {
		return MVACLIENTE;
	}
	public void setMVACLIENTE(String mvacliente) {
		MVACLIENTE = mvacliente;
	}
	public String getMVACOLONIA() {
		return MVACOLONIA;
	}
	public void setMVACOLONIA(String mvacolonia) {
		MVACOLONIA = mvacolonia;
	}
	public String getMVACOMEN() {
		return MVACOMEN;
	}
	public void setMVACOMEN(String mvacomen) {
		MVACOMEN = mvacomen;
	}
	public String getMVACONCEPT() {
		return MVACONCEPT;
	}
	public void setMVACONCEPT(String mvaconcept) {
		MVACONCEPT = mvaconcept;
	}
	public String getMVADELEGAC() {
		return MVADELEGAC;
	}
	public void setMVADELEGAC(String mvadelegac) {
		MVADELEGAC = mvadelegac;
	}
	public String getMVADESCAR() {
		return MVADESCAR;
	}
	public void setMVADESCAR(String mvadescar) {
		MVADESCAR = mvadescar;
	}
	public String getMVADESDES() {
		return MVADESDES;
	}
	public void setMVADESDES(String mvadesdes) {
		MVADESDES = mvadesdes;
	}
	public String getMVADIREC() {
		return MVADIREC;
	}
	public void setMVADIREC(String mvadirec) {
		MVADIREC = mvadirec;
	}
	public Date getMVAFECCXC() {
		return MVAFECCXC;
	}
	public void setMVAFECCXC(Date mvafeccxc) {
		MVAFECCXC = mvafeccxc;
	}
	public Date getMVAFECHA() {
		return MVAFECHA;
	}
	public void setMVAFECHA(Date mvafecha) {
		MVAFECHA = mvafecha;
	}
	public Date getMVAFECHRE() {
		return MVAFECHRE;
	}
	public void setMVAFECHRE(Date mvafechre) {
		MVAFECHRE = mvafechre;
	}
	public Date getMVAFEREAL() {
		return MVAFEREAL;
	}
	public void setMVAFEREAL(Date mvafereal) {
		MVAFEREAL = mvafereal;
	}
	public long getMVAIDECXC() {
		return MVAIDECXC;
	}
	public void setMVAIDECXC(long mvaidecxc) {
		MVAIDECXC = mvaidecxc;
	}
	public long getMVAIMCOR() {
		return MVAIMCOR;
	}
	public void setMVAIMCOR(long mvaimcor) {
		MVAIMCOR = mvaimcor;
	}
	public long getMVAIMPCONC() {
		return MVAIMPCONC;
	}
	public void setMVAIMPCONC(long mvaimpconc) {
		MVAIMPCONC = mvaimpconc;
	}
	public long getMVAKGSFACT() {
		return MVAKGSFACT;
	}
	public void setMVAKGSFACT(long mvakgsfact) {
		MVAKGSFACT = mvakgsfact;
	}
	public long getMVAMOVCRE() {
		return MVAMOVCRE;
	}
	public void setMVAMOVCRE(long mvamovcre) {
		MVAMOVCRE = mvamovcre;
	}
	public long getMVANOCOR() {
		return MVANOCOR;
	}
	public void setMVANOCOR(long mvanocor) {
		MVANOCOR = mvanocor;
	}
	public String getMVANOMBSO() {
		return MVANOMBSO;
	}
	public void setMVANOMBSO(String mvanombso) {
		MVANOMBSO = mvanombso;
	}
	
	public String getMVANOMBCLI() {
		return MVANOMBCLI;
	}
	public void setMVANOMBCLI(String mvanombcli) {
		MVANOMBCLI = mvanombcli;
	}
	public long getMVANUMCXC() {
		return MVANUMCXC;
	}
	public void setMVANUMCXC(long mvanumcxc) {
		MVANUMCXC = mvanumcxc;
	}
	public long getMVANUMEAT() {
		return MVANUMEAT;
	}
	public void setMVANUMEAT(long mvanumeat) {
		MVANUMEAT = mvanumeat;
	}
	
	public long getMVANUMERE() {
		return MVANUMERE;
	}
	public void setMVANUMERE(long mvanumere) {
		MVANUMERE = mvanumere;
	}
	public String getMVAOPERA() {
		return MVAOPERA;
	}
	public void setMVAOPERA(String mvaopera) {
		MVAOPERA = mvaopera;
	}
	public String getMVAPORCAR() {
		return MVAPORCAR;
	}
	public void setMVAPORCAR(String mvaporcar) {
		MVAPORCAR = mvaporcar;
	}
	public String getMVAPORDES() {
		return MVAPORDES;
	}
	public void setMVAPORDES(String mvapordes) {
		MVAPORDES = mvapordes;
	}
	public String getMVAREFERDE() {
		return MVAREFERDE;
	}
	public void setMVAREFERDE(String mvareferde) {
		MVAREFERDE = mvareferde;
	}
	public String getMVASERCXC() {
		return MVASERCXC;
	}
	public void setMVASERCXC(String mvasercxc) {
		MVASERCXC = mvasercxc;
	}
	public String getMVASERDRE() {
		return MVASERDRE;
	}
	public void setMVASERDRE(String mvaserdre) {
		MVASERDRE = mvaserdre;
	}
	public String getMVASERIE() {
		return MVASERIE;
	}
	public void setMVASERIE(String mvaserie) {
		MVASERIE = mvaserie;
	}
	public long getMVASUCCXC() {
		return MVASUCCXC;
	}
	public void setMVASUCCXC(long mvasuccxc) {
		MVASUCCXC = mvasuccxc;
	}
	public long getMVASUCUAT() {
		return MVASUCUAT;
	}
	public void setMVASUCUAT(long mvasucuat) {
		MVASUCUAT = mvasucuat;
	}
	
	public long getMVASUCURE() {
		return MVASUCURE;
	}
	public void setMVASUCURE(long mvasucure) {
		MVASUCURE = mvasucure;
	}
	public long getMVASUCUSO() {
		return MVASUCUSO;
	}
	public void setMVASUCUSO(long mvasucuso) {
		MVASUCUSO = mvasucuso;
	}
	public String getMVATELEFON() {
		return MVATELEFON;
	}
	public void setMVATELEFON(String mvatelefon) {
		MVATELEFON = mvatelefon;
	}
	public String getMVATIPCXC() {
		return MVATIPCXC;
	}
	public void setMVATIPCXC(String mvatipcxc) {
		MVATIPCXC = mvatipcxc;
	}
	public String getMVATIPDRE() {
		return MVATIPDRE;
	}
	public void setMVATIPDRE(String mvatipdre) {
		MVATIPDRE = mvatipdre;
	}
	public String getMVATIPFA() {
		return MVATIPFA;
	}
	public void setMVATIPFA(String mvatipfa) {
		MVATIPFA = mvatipfa;
	}
	public String getMVATIPMAT() {
		return MVATIPMAT;
	}
	public void setMVATIPMAT(String mvatipmat) {
		MVATIPMAT = mvatipmat;
	}
	public String getMVATIPMRE() {
		return MVATIPMRE;
	}
	public void setMVATIPMRE(String mvatipmre) {
		MVATIPMRE = mvatipmre;
	}
	public String getMVATIPO() {
		return MVATIPO;
	}
	public void setMVATIPO(String mvatipo) {
		MVATIPO = mvatipo;
	}
	public String getMVAUSUAR() {
		return MVAUSUAR;
	}
	public void setMVAUSUAR(String mvausuar) {
		MVAUSUAR = mvausuar;
	}
	public long getMVAVENDORE() {
		return MVAVENDORE;
	}
	public void setMVAVENDORE(long mvavendore) {
		MVAVENDORE = mvavendore;
	}
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public Set<Almace> getPartidas() {
		if(partidas==null)
			partidas=new HashSet<Almace>();
		return partidas;
	}
	@SuppressWarnings("unused")
	private void setPartidas(Set<Almace> partidas) {
		this.partidas = partidas;
	}
	
	public void agregarAlmace(final Almace a){
		a.setMvalma(this);
		getPartidas().add(a);
	}
	
    public boolean equals(Object other){
        if(!(other instanceof Almace)){
            return false;
        }
        Mvalma castOther=(Mvalma)other;
        return new EqualsBuilder()
        		.append(MVASUCUR,castOther.getMVASUCUR())
        		.append(MVATIPO,castOther.getMVATIPO())
        		.append(MVANUMER,castOther.getMVANUMER())
        		.isEquals();
    }
    
    public int hashCode(){
        return new HashCodeBuilder(17,37)
			.append(MVASUCUR)
			.append(MVATIPO)
			.append(MVANUMER)
        	.toHashCode();
    }

	
	public String toString() {
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
            .append("id", getId())
            .append("Sucursal", getMVASUCUR())
            .append("Tipo", getMVATIPO())
			.append("Numer", getMVANUMER())
            .toString();
    }
}
