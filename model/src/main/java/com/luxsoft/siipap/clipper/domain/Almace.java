/*
 * Created on 05-dic-2004
 *
 * by Propietario
 */
package com.luxsoft.siipap.clipper.domain;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.MutableObject;


/**
 * @author Ruben Cancino
 *
 */
public class Almace extends MutableObject{
    
	
	private Long Id;
	
	/**
	 * Llave logica 
	 */
    private Integer ALMSUCUR;
    private Long ALMNUMER;
    private String ALMTIPO;    // length=3
    
    private String ALMSERIE=" ";	// length=1
    
    private String ALMTIPFA=" ";	// length=1
    
    private Integer ALMRENGL;
    
    private String ALMARTIC;	// length=10
    private String ALMNOMBR;	// length=35
    private Long ALMCANTI;
    private Integer ALMUNIXUNI;    
    private Double ALMKILOS;
    private Integer ALMGRAMS;
    private Date ALMFECHA;
    private Double ALMSEGS;
    private Date ALMFEREAL;
    
    private Double ALMPRECI;
    private Double ALMPREFA;
    private Double ALMPREFAI;
    private Long ALMCANT2;
    private Integer ALMSUCODE;
    private Integer ALMNUCORE;
    private Double ALMCOSTO;
    private Double ALMPRECPR;    
    private Double ALMDEVANFA;
    private Double ALMSALENFA;
    private Integer ALMRENGLFA;
    
    private Double ALMNUMCXC;
    private Double ALMGRUPOIM;
    private Integer ALMSUCCXC;
    private Integer ALMIDECXC;
    private Date ALMFECH2;
    private Date ALMFECCXC;
    private String ALMTIPIVA;	// length=1
    private String ALMOBSER;	// length=30
    private String ALMNOMDES;	// length=150
    private String ALMPORDES;	// length=50
    private String ALMNOMCAR;	// length=150
    private String ALMPORCAR;	// length=50
    private String ALMUNIDMED;	// length=3
    private String ALMHORA;		// length=8
    private String ALMUSUAR;	// length=10
    private String ALMSERCXC;	// length=1
    private String ALMTIPCXC;	// length=1
    private Double ALMPREKIL;
    
    //ALMDVSUC
    //ALMDVTIP
    //ALMDVTFA
    //ALMDVNFA
    //ALMDVFEC
    
    
    private Mvcomp mvcomp;
    private Mvalma mvalma;
    private Mococa mococa;
    private Mocomo mocomo;
    private Movcre movcre;

    
    public Almace(){
        
    }
    
    public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}


	/**
	 * Clave del articulo
	 * 
	 * @return
	 */
	public String getALMARTIC() {
		return ALMARTIC;
	}
	public void setALMARTIC(String almartic) {
		ALMARTIC = almartic;
	}



	public Long getALMCANT2() {
		return ALMCANT2;
	}
	public void setALMCANT2(Long almcant2) {
		ALMCANT2 = almcant2;
	}



	public Long getALMCANTI() {
		return ALMCANTI;
	}
	public void setALMCANTI(Long almcanti) {
		ALMCANTI = almcanti;
	}



	public Double getALMCOSTO() {
		return ALMCOSTO;
	}



	public void setALMCOSTO(Double almcosto) {
		ALMCOSTO = almcosto;
	}



	public Double getALMDEVANFA() {
		return ALMDEVANFA;
	}



	public void setALMDEVANFA(Double almdevanfa) {
		ALMDEVANFA = almdevanfa;
	}



	public Date getALMFECCXC() {
		return ALMFECCXC;
	}



	public void setALMFECCXC(Date almfeccxc) {
		ALMFECCXC = almfeccxc;
	}



	public Date getALMFECH2() {
		return ALMFECH2;
	}



	public void setALMFECH2(Date almfech2) {
		ALMFECH2 = almfech2;
	}



	public Date getALMFECHA() {
		return ALMFECHA;
	}



	public void setALMFECHA(Date almfecha) {
		ALMFECHA = almfecha;
	}



	public Date getALMFEREAL() {
		return ALMFEREAL;
	}



	public void setALMFEREAL(Date almfereal) {
		ALMFEREAL = almfereal;
	}



	public Integer getALMGRAMS() {
		return ALMGRAMS;
	}



	public void setALMGRAMS(Integer almgrams) {
		ALMGRAMS = almgrams;
	}



	public Double getALMGRUPOIM() {
		return ALMGRUPOIM;
	}



	public void setALMGRUPOIM(Double almgrupoim) {
		ALMGRUPOIM = almgrupoim;
	}



	public String getALMHORA() {
		return ALMHORA;
	}



	public void setALMHORA(String almhora) {
		ALMHORA = almhora;
	}



	public Integer getALMIDECXC() {
		return ALMIDECXC;
	}



	public void setALMIDECXC(Integer almidecxc) {
		ALMIDECXC = almidecxc;
	}



	public Double getALMKILOS() {
		return ALMKILOS;
	}



	public void setALMKILOS(Double almkilos) {
		ALMKILOS = almkilos;
	}



	public String getALMNOMBR() {
		return ALMNOMBR;
	}



	public void setALMNOMBR(String almnombr) {
		ALMNOMBR = almnombr;
	}



	public String getALMNOMCAR() {
		return ALMNOMCAR;
	}



	public void setALMNOMCAR(String almnomcar) {
		ALMNOMCAR = almnomcar;
	}



	public String getALMNOMDES() {
		return ALMNOMDES;
	}



	public void setALMNOMDES(String almnomdes) {
		ALMNOMDES = almnomdes;
	}



	public Integer getALMNUCORE() {
		return ALMNUCORE;
	}



	public void setALMNUCORE(Integer almnucore) {
		ALMNUCORE = almnucore;
	}



	public Double getALMNUMCXC() {
		return ALMNUMCXC;
	}



	public void setALMNUMCXC(Double almnumcxc) {
		ALMNUMCXC = almnumcxc;
	}



	public Long getALMNUMER() {
		return ALMNUMER;
	}



	public void setALMNUMER(Long almnumer) {
		ALMNUMER = almnumer;
	}



	public String getALMOBSER() {
		return ALMOBSER;
	}



	public void setALMOBSER(String almobser) {
		ALMOBSER = almobser;
	}



	public String getALMPORCAR() {
		return ALMPORCAR;
	}



	public void setALMPORCAR(String almporcar) {
		ALMPORCAR = almporcar;
	}



	public String getALMPORDES() {
		return ALMPORDES;
	}



	public void setALMPORDES(String almpordes) {
		ALMPORDES = almpordes;
	}



	public Double getALMPRECI() {
		return ALMPRECI;
	}



	public void setALMPRECI(Double almpreci) {
		ALMPRECI = almpreci;
	}



	public Double getALMPRECPR() {
		return ALMPRECPR;
	}



	public void setALMPRECPR(Double almprecpr) {
		ALMPRECPR = almprecpr;
	}



	public Double getALMPREFA() {
		return ALMPREFA;
	}



	public void setALMPREFA(Double almprefa) {
		ALMPREFA = almprefa;
	}



	public Double getALMPREFAI() {
		return ALMPREFAI;
	}



	public void setALMPREFAI(Double almprefai) {
		ALMPREFAI = almprefai;
	}



	public Double getALMPREKIL() {
		return ALMPREKIL;
	}



	public void setALMPREKIL(Double almprekil) {
		ALMPREKIL = almprekil;
	}



	public Integer getALMRENGL() {
		return ALMRENGL;
	}



	public void setALMRENGL(Integer almrengl) {
		ALMRENGL = almrengl;
	}



	public Integer getALMRENGLFA() {
		return ALMRENGLFA;
	}



	public void setALMRENGLFA(Integer almrenglfa) {
		ALMRENGLFA = almrenglfa;
	}



	public Double getALMSALENFA() {
		return ALMSALENFA;
	}



	public void setALMSALENFA(Double almsalenfa) {
		ALMSALENFA = almsalenfa;
	}



	public Double getALMSEGS() {
		return ALMSEGS;
	}



	public void setALMSEGS(Double almsegs) {
		ALMSEGS = almsegs;
	}



	public String getALMSERCXC() {
		return ALMSERCXC;
	}



	public void setALMSERCXC(String almsercxc) {
		ALMSERCXC = almsercxc;
	}



	/**
	 * Solo aplica para los moviminetos tipo FAC y sirve para distinguir registros
	 * segun la regla de negocios local para agrupar ventas
	 * 		
	 * 		Mostrador =A (Maestro en MOCOMO)
	 * 		Camioneda =C (Maestro en MOCOCA)
	 * 		Credito   =E (Maestro en MOVCRE)
	 * 
	 * 
	 * @return
	 */
	public String getALMSERIE() {
		return ALMSERIE;
	}
	public void setALMSERIE(String almserie) {
		ALMSERIE = almserie;
	}
	
	/**
	 * Se usa para diversos criterios en la regla de negocios
	 * 
	 * A= Iva Desglosado	(Aplica solo para ALMSERIE=A)
	 * B= Iva Sin Desglosar	(Aplica solo para ALMSERIE=A)
	 * C= Iva Desglosado 	(Aplica solo para ALMSERIE=C)
	 * D= Iva Sin Desglosar (Aplica solo para ALMSERIE=C)
	 * E= Iva Desglosado 	(Solo aplica para ALMSERIE=E) 
	 * G= Tipo de Pago PROCHEMEX (Credito autorizado por un contrato) Solo aplica para ALMSERIE=E)
	 * N= Los precios de las partidas son NETOS (Solo aplica para ALMSERIE=E)
	 * P= Ventas a Papelera Progreso
	 * Q= Los productos de la venta son SERVICIOS (Solo aplica para ALMSERIE=A)
	 * R= Los productos de la venta son SERVICIOS (Solo aplica para ALMSERIE=C)
	 * S= Los productos de la venta son SERVICIOS (Solo aplica para ALMSERIE=E)
	 * T= Los productos de la venta son SERVICIOS y sin IVA desglosado (Solo aplica para ALMSERIE=A)
	 * 
	 * @return
	 */
	public String getALMTIPFA() {
		return ALMTIPFA;
	}
	public void setALMTIPFA(String almtipfa) {
		ALMTIPFA = almtipfa;
	}



	public Integer getALMSUCCXC() {
		return ALMSUCCXC;
	}



	public void setALMSUCCXC(Integer almsuccxc) {
		ALMSUCCXC = almsuccxc;
	}



	public Integer getALMSUCODE() {
		return ALMSUCODE;
	}



	public void setALMSUCODE(Integer almsucode) {
		ALMSUCODE = almsucode;
	}



	public Integer getALMSUCUR() {
		return ALMSUCUR;
	}



	public void setALMSUCUR(Integer almsucur) {
		ALMSUCUR = almsucur;
	}



	public String getALMTIPCXC() {
		return ALMTIPCXC;
	}
	public void setALMTIPCXC(String almtipcxc) {
		ALMTIPCXC = almtipcxc;
	}

	
	
	
	public String getALMTIPIVA() {
		return ALMTIPIVA;
	}
	public void setALMTIPIVA(String almtipiva) {
		ALMTIPIVA = almtipiva;
	}
	

	public String getALMTIPO() {
		return ALMTIPO;
	}
	public void setALMTIPO(String almtipo) {
		ALMTIPO = almtipo;
	}


	public String getALMUNIDMED() {
		return ALMUNIDMED;
	}



	public void setALMUNIDMED(String almunidmed) {
		ALMUNIDMED = almunidmed;
	}



	public Integer getALMUNIXUNI() {
		return ALMUNIXUNI;
	}



	public void setALMUNIXUNI(Integer almunixuni) {
		ALMUNIXUNI = almunixuni;
	}



	public String getALMUSUAR() {
		return ALMUSUAR;
	}



	public void setALMUSUAR(String almusuar) {
		ALMUSUAR = almusuar;
	}



	



	public Mococa getMococa() {
		return mococa;
	}



	public void setMococa(Mococa mococa) {
		this.mococa = mococa;
	}



	public Mocomo getMocomo() {
		return mocomo;
	}



	public void setMocomo(Mocomo mocomo) {
		this.mocomo = mocomo;
	}



	public Movcre getMovcre() {
		return movcre;
	}



	public void setMovcre(Movcre movcre) {
		this.movcre = movcre;
	}



	public Mvalma getMvalma() {
		return mvalma;
	}



	public void setMvalma(Mvalma mvalma) {
		this.mvalma = mvalma;
	}



	public Mvcomp getMvcomp() {
		return mvcomp;
	}



	public void setMvcomp(Mvcomp mvcomp) {
		this.mvcomp = mvcomp;
	}



	public boolean equals(Object other){
		if(other==null) return false;
		if(other==this)return true;
        Almace castOther=(Almace)other;
        return new EqualsBuilder()
        		.append(this.ALMSUCUR,castOther.getALMSUCUR())
        		.append(this.ALMTIPO, castOther.getALMTIPO())
        		.append(this.ALMNUMER,castOther.getALMNUMER())
        		.append(this.ALMSERIE,castOther.getALMSERIE())
        		.append(this.ALMTIPFA,castOther.getALMTIPFA())
        		.append(this.ALMRENGL,castOther.getALMRENGL())        		
        		.isEquals();
    }
    
    public int hashCode(){
        return new HashCodeBuilder(17,37)
        	.append(this.ALMSUCUR)
        	.append(this.ALMTIPO)
        	.append(this.ALMNUMER)
        	.append(this.ALMSERIE)
        	.append(this.ALMTIPFA)
        	.append(this.ALMRENGL)
        	.toHashCode();
    }
    
    public String toString(){
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
        	.append(getALMSUCUR())
        	.append(getALMTIPO())
        	.append(getALMNUMER())
        	.append(getALMSERIE())
        	.append(getALMTIPFA())
        	.append(getALMRENGL())
        	.append(getALMARTIC())
        	.append(getALMFECHA())
        	.append(getALMCANTI())
        	.append(getALMFEREAL())
        	.append(getALMSEGS())
        	.toString();
    }
 }
