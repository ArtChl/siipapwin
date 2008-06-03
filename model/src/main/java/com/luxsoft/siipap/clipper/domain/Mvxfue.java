package com.luxsoft.siipap.clipper.domain;

import java.util.Date;

/*
 * MVXFUE: Guarda las partidas de los documentos que no afectan
 * a inventarios (SOL,MDE), no afectan a ARSALD.
 * Su estructura es casi identica que la estructura de ALMACE.
 * Se relaciona con ALXFUE.
 */


public class Mvxfue {
	
	private Long id;
	//Bandera que indica si ya se atendio la solicitud  
	private String MVAESTADO;
	//Sucursal donde se genera el mov.
	private Double MVASUCUR;
	//Tipo movimiento de inventario.
	private String MVATIPO;
	//NUmero de movimiento
	private Double MVANUMER;
	//No se encontro
	private String MVASERIE;
	//No se encontro
	private String MVATIPFA;
	//Fecha del mov. (Fecha propia de siipap)
	private Date MVAFECHA;
	//Comentario sobre el documento (mov.)
	private String MVACOMEN;
	//Clave del cliente
	private String MVACLIENTE;
	//Nombre del cliente
	private String MVANOMBCLI;
	//Clave de socio en caso de ser socio
	private String MVACLAVSOC;
	//Descuento
	private String MVADESDES;
	//Descuento
	private String MVAPORDES;
	//Descuento y/o cargo para el cliente
	private String MVADESCAR;
	//No se encontro
	private String MVAPORCAR;
	//No se encontro
	private Double MVANOCOR;
	//No se encontro
	private Double MVAIMCOR;
	//Sucursal solicitante 
	private Double MVASUCUSO;
	//Hace referencia hacia algun movimiento de inv. o factura Sucursal solicitante
	private Double MVASUCURE;
	//Hace referencia hacia algun movimiento de inv. o factura Sucursal que recibe
	private String MVATIPMRE;
	//Hace referencia hacia algun movimiento de inv. o factura Tipo de mov.
	private String MVASERDRE;
	//Hace referencia hacia algun movimiento de inv. o factura 
	private String MVATIPDRE;
	//Hace referencia hacia algun movimiento de inv. o factura Tipo de ref.
	private Double MVANUMERE;
	//Hace referencia hacia algun movimiento de inv. o factura Numero de ref.
	private Date MVAFECHRE;
	//No se encontro
	private Double MVAMOVCRE;
	//Tipo de documento referenciado 
	private String MVAREFERDE;
	//No se encontro
	private Double MVASUCUAT;
	//No se encontro
	private String MVATIPMAT;
	//No se encontro
	private Double MVANUMEAT;
	//No se encontro
	private String MVAOPERA;
	//No se encontro
	private String MVAUSUAR;
	//Fecha real del sistema.
	private Date MVAFEREAL;
	//Datos de la nota de crédito Sucursal 
	private Double MVASUCCXC;
	//Datos de la nota de crédito	
	private Double MVAIDECXC;
	//Datos de la nota de crédito
	private String MVASERCXC;
	//Datos de la nota de crédito
	private String MVATICXC;
	//Datos de la nota de crédito Numero
	private Double MVANUMCXC;
	//Datos de la nota de crédito Fecha.
	private Date MVAFECCXC;
	//No se encontro
	private Double MVAVENDORE;
	//No se encontro
	private String MVACONCEPT;
	//No se encontro
	private Double MVAIMPCONC;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id; 
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
	public String getMVAESTADO() {
		return MVAESTADO;
	}
	public void setMVAESTADO(String mvaestado) {
		MVAESTADO = mvaestado;
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
	public Double getMVAIDECXC() {
		return MVAIDECXC;
	}
	public void setMVAIDECXC(Double mvaidecxc) {
		MVAIDECXC = mvaidecxc;
	}
	public Double getMVAIMCOR() {
		return MVAIMCOR;
	}
	public void setMVAIMCOR(Double mvaimcor) {
		MVAIMCOR = mvaimcor;
	}
	public Double getMVAIMPCONC() {
		return MVAIMPCONC;
	}
	public void setMVAIMPCONC(Double mvaimpconc) {
		MVAIMPCONC = mvaimpconc;
	}
	public Double getMVAMOVCRE() {
		return MVAMOVCRE;
	}
	public void setMVAMOVCRE(Double mvamovcre) {
		MVAMOVCRE = mvamovcre;
	}
	public Double getMVANOCOR() {
		return MVANOCOR;
	}
	public void setMVANOCOR(Double mvanocor) {
		MVANOCOR = mvanocor;
	}
	public String getMVANOMBCLI() {
		return MVANOMBCLI;
	}
	public void setMVANOMBCLI(String mvanombcli) {
		MVANOMBCLI = mvanombcli;
	}
	public Double getMVANUMCXC() {
		return MVANUMCXC;
	}
	public void setMVANUMCXC(Double mvanumcxc) {
		MVANUMCXC = mvanumcxc;
	}
	public Double getMVANUMEAT() {
		return MVANUMEAT;
	}
	public void setMVANUMEAT(Double mvanumeat) {
		MVANUMEAT = mvanumeat;
	}
	public Double getMVANUMER() {
		return MVANUMER;
	}
	public void setMVANUMER(Double mvanumer) {
		MVANUMER = mvanumer;
	}
	public Double getMVANUMERE() {
		return MVANUMERE;
	}
	public void setMVANUMERE(Double mvanumere) {
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
	public Double getMVASUCCXC() {
		return MVASUCCXC;
	}
	public void setMVASUCCXC(Double mvasuccxc) {
		MVASUCCXC = mvasuccxc;
	}
	public Double getMVASUCUAT() {
		return MVASUCUAT;
	}
	public void setMVASUCUAT(Double mvasucuat) {
		MVASUCUAT = mvasucuat;
	}
	public Double getMVASUCUR() {
		return MVASUCUR;
	}
	public void setMVASUCUR(Double mvasucur) {
		MVASUCUR = mvasucur;
	}
	public Double getMVASUCURE() {
		return MVASUCURE;
	}
	public void setMVASUCURE(Double mvasucure) {
		MVASUCURE = mvasucure;
	}
	public Double getMVASUCUSO() {
		return MVASUCUSO;
	}
	public void setMVASUCUSO(Double mvasucuso) {
		MVASUCUSO = mvasucuso;
	}
	public String getMVATICXC() {
		return MVATICXC;
	}
	public void setMVATICXC(String mvaticxc) {
		MVATICXC = mvaticxc;
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
	public Double getMVAVENDORE() {
		return MVAVENDORE;
	}
	public void setMVAVENDORE(Double mvavendore) {
		MVAVENDORE = mvavendore;
	}
	
	
	
	

}
