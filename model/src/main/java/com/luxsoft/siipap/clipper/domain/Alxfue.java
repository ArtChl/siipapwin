package com.luxsoft.siipap.clipper.domain;


import java.util.Date;

/*
 * ALMXFUE: Guarda las partidas de los documentos que no afectan
 * a inventarios (SOL,MDE), no afectan a ARSALD.
 * Su estructura es casi identica que la estructura de ALMACE.
 * Se relaciona con MVXFUE.
 */

public class Alxfue {
	
	
	private Long id;
	//Clave de sucursal
	private Double ALMSUCUR;
	//Tipo movimiento de inventario
	private String ALMTIPO;
	//Número de movto. (Consecutivo) del movimiento.
	private Double ALMNUMER;
	//Partida ¢ renglon del movimiento.
	private Double ALMRENGL;
	//Fecha de operaci¢n (fecha interna del sistema)
	private Date ALMFECHA;
	//Codigo del articulo.
	private String ALMARTIC;
	//Descripci¢n del articulo.
	private String ALMNOMBR;
	//Kilos del articulo.
	private Double ALMKILOS;
	//Unidades afectadas.
	private Double ALMCANTI;
	//Unidades entregadas por x sucursal.
	private Double ALMCANTATE;
	//Numero de serie de la factura emitida
	private String ALMSERIE;
	//Tipo de factura emitida
	private String ALMTIPFA;
    //Precio 
	private Double ALMPRECI;
    //Precio total factura
	private Double ALMPREFA;
    //No se encuentra
	private String ALMTIPIVA;
	//Observaciones sobre la partida
	private String ALMOBSER;
    //Se emplea para la fecha de impresión del protocolo (en mov. de compra)
	private Date ALMFECH2; 
	//Hace referencia a entradas por compra
	private Double ALMCANT2;
	//Hace referencia a entradas por compra
	private Double ALMNUCORE;
	//Hace referencia a entradas por compra
	private Double ALMCOSTO;
	//Hace referencia a entradas por compra
	private Double ALMPRECPR;
	//Hace referencia a entradas por compra
	private String ALMNOMDES;
	//Hace referencia a entradas por compra
	private String ALMPORDES;
	//Hace referencia a entradas por compra
	private String ALMNOMCAR;
	//No se encuentra
	private String ALMPORCAR;
	//Clave de unidad de medida.
	private String ALMUNIDMED;
	//Factor conversi¢n de unidad de medida.
	private Double ALMUNIXUNI;
	//Actualizan los datos de la factura en el RMD y también de la factura
	private Double ALMDEVANFA;
	//Actualizan los datos de la factura en el RMD y también de la factura
	private Double ALMSALENFA;
	//Actualizan los datos de la factura en el RMD y también de la factura
	private Double ALMRENGFA;
	//Nota de crédito
	private Double ALMGRUPOIM;
	//No se encuentra
	private String ALMHORA;
	//No se encuentra
	private Double ALMSEGS;
	//usuario quien realiza el movimiento
	private String ALMUSUAR;
	//Fecha del servidor
	private Date ALMFEREAL;
		
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getALMARTIC() {
		return ALMARTIC;
	}
	public void setALMARTIC(String almartic) {
		ALMARTIC = almartic;
	}
	public Double getALMCANT2() {
		return ALMCANT2;
	}
	public void setALMCANT2(Double almcant2) {
		ALMCANT2 = almcant2;
	}
	public Double getALMCANTATE() {
		return ALMCANTATE;
	}
	public void setALMCANTATE(Double almcantate) {
		ALMCANTATE = almcantate;
	}
	public Double getALMCANTI() {
		return ALMCANTI;
	}
	public void setALMCANTI(Double almcanti) {
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
	public Double getALMNUCORE() {
		return ALMNUCORE;
	}
	public void setALMNUCORE(Double almnucore) {
		ALMNUCORE = almnucore;
	}
	public Double getALMNUMER() {
		return ALMNUMER;
	}
	public void setALMNUMER(Double almnumer) {
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
	public Double getALMRENGFA() {
		return ALMRENGFA;
	}
	public void setALMRENGFA(Double almrengfa) {
		ALMRENGFA = almrengfa;
	}
	public Double getALMRENGL() {
		return ALMRENGL;
	}
	public void setALMRENGL(Double almrengl) {
		ALMRENGL = almrengl;
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
	public String getALMSERIE() {
		return ALMSERIE;
	}
	public void setALMSERIE(String almserie) {
		ALMSERIE = almserie;
	}
	public Double getALMSUCUR() {
		return ALMSUCUR;
	}
	public void setALMSUCUR(Double almsucur) {
		ALMSUCUR = almsucur;
	}
	public String getALMTIPFA() {
		return ALMTIPFA;
	}
	public void setALMTIPFA(String almtipfa) {
		ALMTIPFA = almtipfa;
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
	public Double getALMUNIXUNI() {
		return ALMUNIXUNI;
	}
	public void setALMUNIXUNI(Double almunixuni) {
		ALMUNIXUNI = almunixuni;
	}
	public String getALMUSUAR() {
		return ALMUSUAR;
	}
	public void setALMUSUAR(String almusuar) {
		ALMUSUAR = almusuar;
	}
	
	
		

}
