package com.luxsoft.siipap.clipper.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class Movcre implements Serializable {

    private Long id;
    //Sucursal donde se elaboro el documento
    private Integer MCRSUCURSA;
    //Identificaci¢n de operaci¢n del documento
    private Double MCRIDENOPE;
    //Fecha de elaboraci¢n del sistema
    private Date MCRFECHA;
    //Etapa del documento
    private String MCRETAPA;
    //Serie de la factura
    private String MCRSERIEFA;
    //Tipo de factura
    private String MCRTIPOFAC;
    //Consecutivo de la factura
    private String MCRCONSECU;
    //Tipo de factura alternativo
    private String MCRTIPOFAL;
    //Descripci¢n del tipo de factura
    private String MCRDESTFAC;
    //Tipo de venta (CON=contado,CRE=credito)
    private String MCRTIPOVTA;
    //Numero de documento
    private Double MCRNUMDOCT;
    //Numero de Pedido en el caso de que se tomo un pedido para 
    //facturar y consecutivo de comprobante de pago, el segundo 
    //datos es a partir del 18/02/2002.
    private Double MCRNODOCTO;
    //Numero fiscal
    private Double MCRNOFISCA;
    //Serie fiscal
    private String MCRSERIEFI;
    //Tipo de cliente
    private String MCRCLITIPO;
    //Clasificaci¢n del cliente
    private Double MCRCLASCLI;
    //Clave del cliente
    private String MCRCLAVCLI;
    //Nombre del cliente
    private String MCRNOMBCLI;
    //Clave del socio
    private String MCRCLAVSOC;
    //Nombre del socio
    private String MCRNOMBSOC;
    //RFC del cliente
    private String MCRRFCCLIE;
    //Direccion del cliente
    private String MCRCALLE;
    // Colonia del cliente
    private String MCRCOLONIA;
    // Delegacion del cliente
    private String MCRDELEGAC;
    //Codigo Postal
    private String MCRCODIGOP;
    //Telefono
    private String MCRTELEFON;
    //Clave del Vendedor
    private Double MCRVENDEDO;
    //Clave del Cobrador
    private Double MCRCOBRADO;
    //Clave del Chofer
    private Double MCRCHOFER;
    //Zona correspondiente a la ruta de entrega
    private Double MCRZONA;
    //Fecha de deposito de cheque
    private Date MCRFEDEPCH;
    //Status de la factura
    private String MCRSTATUS;
    //Fecha vencimiento del documento.
    private Date MCRVTO;
    //Dia de la semana para la toma a rev. por part. del cliente
    private Double MCRDIAREVI;
    //Dia de la semana para pagos. por part. del cliente
    private Double MCRDIAPAGO;
    //Nombre de descuento real
    private String MCRNOMDESR;
    //Porcentaje de descuento real
    private String MCRPORDESR;
    //Nombre de descuento facturado
    private String MCRNOMDESF;
    //Descuento facturado o aplicado
    private String MCRPORDESF;
    //Nombre de descuento adjuntado
    private String MCRNOMCAD;
    //Porcentaje adjuntado
    private String MCRPORCAD;
    //Suma del total de las partidas antes de descuentos
    private Double MCRIMPARTI;
    //Concepto de cobro.
    private String MCRCONCEPT;
    //Importe del concepto  (MAQUILA = maniobras)
    private Double MCRIMPCONC;
    //Precio por corte
    private Double MCRIMPCORT;
    //No. de cortes a cobrar
    private Double MCRCANTCOR;
    //Subtotal (suma de partidas mas descuentos y cortes)
    private Double MCRSUBTOT;
    //Importe correspondiente al I.V.A.
    private Double MCRIMPUEST;
    //Total (Suma de subtotal mas I.V.A.)
    private Double MCRTOTAL;
    //Suma de kilos facturados.
    private Double MCRKGSFACT;
    //Suma de unidades facturadas
    private Double MCRCANTIDA;
    //Porcentaje de I.V.A.
    private Double MCRPORIVA;
    //Persona que elaboro el documento.
    private String MCRFACTURI;
    //Persona que surtio la factura.
    private String MCRSURTIO;
    //Comentario capturado al elaborar la factura.
    private String MCRCOMFAC;
    //Numero de cheque o tarjeta
    private String MCRNOCHTAR;
    //Nombre del banco de donde corresponde el cheque o tarjeta
    private String MCRBANCO;
    //Hora de elaboracion del documento.
    private String MCRHORAFAC;
    //Fecha real de elaboracion.
    private Date MCRFECREAL;
    //Hora real de elaboracion del documento (no procede)
    private String MCRHRSREAL;
    //Fecha de cancelacion.
    private Date MCRFECHCAN;
    //Persona que cancela.
    private String MCRCANCELO;
    //Motivo de cancelacion.
    private String MCRMOTICAN;
    //Fecha de factura afectada (cobranza)
    private Date MCRFEFAAFE;
    //Sucursal de factura afectada (cobranza)
    private Double MCRSUFAAFE;
    //Serie de factura afectada (cobranza)
    private String MCRSEFAAFE;
    //Tipo de factura afectada (cobranza)
    private String MCRTIFAAFE;
    //Numero de factura afectada (cobranza)
    private Double MCRNUFAAFE;
    //Operacion de la factura afectada (cobranza)
    private Double MCROPFAAFE;
    //Tipo de movimiento
    private String MCRTIPOMOV;
    //Numero de movimiento
    private Double MCRNUMMOVI;
    //Archivo
    private String MCRARCHIVO;
    //Movimiento de devolucion (RMD)
    private String MCRMVADEV;
    //Sucursal de devolucion (RMD)
    private Double MCRSUCDEV;
    //Fecha de devolucion (RMD)
    private Date MCRFECDEV;
    //Numero de devolucion (RMD)
    private Double MCRNUMDEV;
    //Movimiento, pedido o cotizacion
    private String MCRMOPECO;
    //Fecha de impresion nota de credito
    private Date MCRIMPRESO;
    //Nota de crédito provisional
    private Double MCRNCRPROV;
    //Formato empleado al imprimir la factura.
    private String MCRFORMATO;
    //Fecha periodica de comisiones de los vendedores (credito)
    private String MCRFECOMVE;
    //Fecha periodica de comisiones de los vendedores (contado)
    private String MCRFECOMCO;
    //Fecha de comision de cheque
    private String MCRFECOMCH;
    //Importe de factura
    private Double MCRIMPFACT;
    //Saldo en factura (importe)
    private Double MCRSALFACT;
    //Cantidad entregada (asignaciones de chofer)
    private Double MCRCANTENT;
    //Concepto de maniobra
    private String MCRCONMANI;
    //Importe de maniobra
    private Double MCRIMPMANI;
    //Nombre de la persona que realizo la cotizacion
    private String MCRNOMCOTI;
    //Nombre de la persona que realizo el pedido
    private String MCRNOMPEDI;

    private String MES;


    private Set<Almace> partidas;
    
    public Movcre() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


	public Double getMCRDIAPAGO() {
		return MCRDIAPAGO;
	}
	public void setMCRDIAPAGO(Double mcrdiapago) {
		MCRDIAPAGO = mcrdiapago;
	}
	public String getMCRARCHIVO() {
		return MCRARCHIVO;
	}
	public void setMCRARCHIVO(String mcrarchivo) {
		MCRARCHIVO = mcrarchivo;
	}
	public String getMCRBANCO() {
		return MCRBANCO;
	}
	public void setMCRBANCO(String mcrbanco) {
		MCRBANCO = mcrbanco;
	}
	public String getMCRCALLE() {
		return MCRCALLE;
	}
	public void setMCRCALLE(String mcrcalle) {
		MCRCALLE = mcrcalle;
	}
	public String getMCRCANCELO() {
		return MCRCANCELO;
	}
	public void setMCRCANCELO(String mcrcancelo) {
		MCRCANCELO = mcrcancelo;
	}
	public Double getMCRCANTCOR() {
		return MCRCANTCOR;
	}
	public void setMCRCANTCOR(Double mcrcantcor) {
		MCRCANTCOR = mcrcantcor;
	}
	public Double getMCRCANTENT() {
		return MCRCANTENT;
	}
	public void setMCRCANTENT(Double mcrcantent) {
		MCRCANTENT = mcrcantent;
	}
	public Double getMCRCANTIDA() {
		return MCRCANTIDA;
	}
	public void setMCRCANTIDA(Double mcrcantida) {
		MCRCANTIDA = mcrcantida;
	}
	public Double getMCRCHOFER() {
		return MCRCHOFER;
	}
	public void setMCRCHOFER(Double mcrchofer) {
		MCRCHOFER = mcrchofer;
	}
	public Double getMCRCLASCLI() {
		return MCRCLASCLI;
	}
	public void setMCRCLASCLI(Double mcrclascli) {
		MCRCLASCLI = mcrclascli;
	}
	public String getMCRCLAVCLI() {
		return MCRCLAVCLI;
	}
	public void setMCRCLAVCLI(String mcrclavcli) {
		MCRCLAVCLI = mcrclavcli;
	}
	public String getMCRCLAVSOC() {
		return MCRCLAVSOC;
	}
	public void setMCRCLAVSOC(String mcrclavsoc) {
		MCRCLAVSOC = mcrclavsoc;
	}
	public String getMCRCLITIPO() {
		return MCRCLITIPO;
	}
	public void setMCRCLITIPO(String mcrclitipo) {
		MCRCLITIPO = mcrclitipo;
	}
	public Double getMCRCOBRADO() {
		return MCRCOBRADO;
	}
	public void setMCRCOBRADO(Double mcrcobrado) {
		MCRCOBRADO = mcrcobrado;
	}
	public String getMCRCODIGOP() {
		return MCRCODIGOP;
	}
	public void setMCRCODIGOP(String mcrcodigop) {
		MCRCODIGOP = mcrcodigop;
	}
	public String getMCRCOLONIA() {
		return MCRCOLONIA;
	}
	public void setMCRCOLONIA(String mcrcolonia) {
		MCRCOLONIA = mcrcolonia;
	}
	public String getMCRCOMFAC() {
		return MCRCOMFAC;
	}
	public void setMCRCOMFAC(String mcrcomfac) {
		MCRCOMFAC = mcrcomfac;
	}
	public String getMCRCONCEPT() {
		return MCRCONCEPT;
	}
	public void setMCRCONCEPT(String mcrconcept) {
		MCRCONCEPT = mcrconcept;
	}
	public String getMCRCONMANI() {
		return MCRCONMANI;
	}
	public void setMCRCONMANI(String mcrconmani) {
		MCRCONMANI = mcrconmani;
	}
	public String getMCRCONSECU() {
		return MCRCONSECU;
	}
	public void setMCRCONSECU(String mcrconsecu) {
		MCRCONSECU = mcrconsecu;
	}
	public Double getMCRNCRPROV() {
		return MCRNCRPROV;
	}
	public void setMCRNCRPROV(Double mcrncrprov) {
		MCRNCRPROV = mcrncrprov;
	}
	public String getMCRDELEGAC() {
		return MCRDELEGAC;
	}
	public void setMCRDELEGAC(String mcrdelegac) {
		MCRDELEGAC = mcrdelegac;
	}
	public String getMCRDESTFAC() {
		return MCRDESTFAC;
	}
	public void setMCRDESTFAC(String mcrdestfac) {
		MCRDESTFAC = mcrdestfac;
	}
	public Double getMCRDIAREVI() {
		return MCRDIAREVI;
	}
	public void setMCRDIAREVI(Double mcrdiarevi) {
		MCRDIAREVI = mcrdiarevi;
	}
	public String getMCRETAPA() {
		return MCRETAPA;
	}
	public void setMCRETAPA(String mcretapa) {
		MCRETAPA = mcretapa;
	}
	public String getMCRFACTURI() {
		return MCRFACTURI;
	}
	public void setMCRFACTURI(String mcrfacturi) {
		MCRFACTURI = mcrfacturi;
	}
	public Date getMCRFECDEV() {
		return MCRFECDEV;
	}
	public void setMCRFECDEV(Date mcrfecdev) {
		MCRFECDEV = mcrfecdev;
	}
	public Date getMCRFECHA() {
		return MCRFECHA;
	}
	public void setMCRFECHA(Date mcrfecha) {
		MCRFECHA = mcrfecha;
	}
	public Date getMCRFECHCAN() {
		return MCRFECHCAN;
	}
	public void setMCRFECHCAN(Date mcrfechcan) {
		MCRFECHCAN = mcrfechcan;
	}
	public String getMCRFECOMCH() {
		return MCRFECOMCH;
	}
	public void setMCRFECOMCH(String mcrfecomch) {
		MCRFECOMCH = mcrfecomch;
	}
	public String getMCRFECOMCO() {
		return MCRFECOMCO;
	}
	public void setMCRFECOMCO(String mcrfecomco) {
		MCRFECOMCO = mcrfecomco;
	}
	public String getMCRFECOMVE() {
		return MCRFECOMVE;
	}
	public void setMCRFECOMVE(String mcrfecomve) {
		MCRFECOMVE = mcrfecomve;
	}
	public Date getMCRFECREAL() {
		return MCRFECREAL;
	}
	public void setMCRFECREAL(Date mcrfecreal) {
		MCRFECREAL = mcrfecreal;
	}
	public Date getMCRFEDEPCH() {
		return MCRFEDEPCH;
	}
	public void setMCRFEDEPCH(Date mcrfedepch) {
		MCRFEDEPCH = mcrfedepch;
	}
	public Date getMCRFEFAAFE() {
		return MCRFEFAAFE;
	}
	public void setMCRFEFAAFE(Date mcrfefaafe) {
		MCRFEFAAFE = mcrfefaafe;
	}
	public String getMCRFORMATO() {
		return MCRFORMATO;
	}
	public void setMCRFORMATO(String mcrformato) {
		MCRFORMATO = mcrformato;
	}
	public String getMCRHORAFAC() {
		return MCRHORAFAC;
	}
	public void setMCRHORAFAC(String mcrhorafac) {
		MCRHORAFAC = mcrhorafac;
	}
	public String getMCRHRSREAL() {
		return MCRHRSREAL;
	}
	public void setMCRHRSREAL(String mcrhrsreal) {
		MCRHRSREAL = mcrhrsreal;
	}
	public Double getMCRIDENOPE() {
		return MCRIDENOPE;
	}
	public void setMCRIDENOPE(Double mcridenope) {
		MCRIDENOPE = mcridenope;
	}
	public Double getMCRIMPARTI() {
		return MCRIMPARTI;
	}
	public void setMCRIMPARTI(Double mcrimparti) {
		MCRIMPARTI = mcrimparti;
	}
	public Double getMCRIMPCONC() {
		return MCRIMPCONC;
	}
	public void setMCRIMPCONC(Double mcrimpconc) {
		MCRIMPCONC = mcrimpconc;
	}
	public Double getMCRIMPCORT() {
		return MCRIMPCORT;
	}
	public void setMCRIMPCORT(Double mcrimpcort) {
		MCRIMPCORT = mcrimpcort;
	}
	public Double getMCRIMPFACT() {
		return MCRIMPFACT;
	}
	public void setMCRIMPFACT(Double mcrimpfact) {
		MCRIMPFACT = mcrimpfact;
	}
	public Double getMCRIMPMANI() {
		return MCRIMPMANI;
	}
	public void setMCRIMPMANI(Double mcrimpmani) {
		MCRIMPMANI = mcrimpmani;
	}
	public Date getMCRIMPRESO() {
		return MCRIMPRESO;
	}
	public void setMCRIMPRESO(Date mcrimpreso) {
		MCRIMPRESO = mcrimpreso;
	}
	public Double getMCRIMPUEST() {
		return MCRIMPUEST;
	}
	public void setMCRIMPUEST(Double mcrimpuest) {
		MCRIMPUEST = mcrimpuest;
	}
	public Double getMCRKGSFACT() {
		return MCRKGSFACT;
	}
	public void setMCRKGSFACT(Double mcrkgsfact) {
		MCRKGSFACT = mcrkgsfact;
	}
	public String getMCRMOPECO() {
		return MCRMOPECO;
	}
	public void setMCRMOPECO(String mcrmopeco) {
		MCRMOPECO = mcrmopeco;
	}
	public String getMCRMOTICAN() {
		return MCRMOTICAN;
	}
	public void setMCRMOTICAN(String mcrmotican) {
		MCRMOTICAN = mcrmotican;
	}
	public String getMCRMVADEV() {
		return MCRMVADEV;
	}
	public void setMCRMVADEV(String mcrmvadev) {
		MCRMVADEV = mcrmvadev;
	}
	public String getMCRNOCHTAR() {
		return MCRNOCHTAR;
	}
	public void setMCRNOCHTAR(String mcrnochtar) {
		MCRNOCHTAR = mcrnochtar;
	}
	public Double getMCRNODOCTO() {
		return MCRNODOCTO;
	}
	public void setMCRNODOCTO(Double mcrnodocto) {
		MCRNODOCTO = mcrnodocto;
	}
	public String getMCRNOMCOTI() {
		return MCRNOMCOTI;
	}
	public void setMCRNOMCOTI(String mcrnomcoti) {
		MCRNOMCOTI = mcrnomcoti;
	}
	public String getMCRNOMPEDI() {
		return MCRNOMPEDI;
	}
	public void setMCRNOMPEDI(String mcrnompedi) {
		MCRNOMPEDI = mcrnompedi;
	}
	public Double getMCRNOFISCA() {
		return MCRNOFISCA;
	}
	public void setMCRNOFISCA(Double mcrnofisca) {
		MCRNOFISCA = mcrnofisca;
	}
	public String getMCRNOMBCLI() {
		return MCRNOMBCLI;
	}
	public void setMCRNOMBCLI(String mcrnombcli) {
		MCRNOMBCLI = mcrnombcli;
	}
	public String getMCRNOMBSOC() {
		return MCRNOMBSOC;
	}
	public void setMCRNOMBSOC(String mcrnombsoc) {
		MCRNOMBSOC = mcrnombsoc;
	}
	public String getMCRNOMCAD() {
		return MCRNOMCAD;
	}
	public void setMCRNOMCAD(String mcrnomcad) {
		MCRNOMCAD = mcrnomcad;
	}
	public String getMCRNOMDESF() {
		return MCRNOMDESF;
	}
	public void setMCRNOMDESF(String mcrnomdesf) {
		MCRNOMDESF = mcrnomdesf;
	}
	public String getMCRNOMDESR() {
		return MCRNOMDESR;
	}
	public void setMCRNOMDESR(String mcrnomdesr) {
		MCRNOMDESR = mcrnomdesr;
	}
	public Double getMCRNUFAAFE() {
		return MCRNUFAAFE;
	}
	public void setMCRNUFAAFE(Double mcrnufaafe) {
		MCRNUFAAFE = mcrnufaafe;
	}
	public Double getMCRNUMDEV() {
		return MCRNUMDEV;
	}
	public void setMCRNUMDEV(Double mcrnumdev) {
		MCRNUMDEV = mcrnumdev;
	}
	public Double getMCRNUMDOCT() {
		return MCRNUMDOCT;
	}
	public void setMCRNUMDOCT(Double mcrnumdoct) {
		MCRNUMDOCT = mcrnumdoct;
	}
	public Double getMCRNUMMOVI() {
		return MCRNUMMOVI;
	}
	public void setMCRNUMMOVI(Double mcrnummovi) {
		MCRNUMMOVI = mcrnummovi;
	}
	public Double getMCROPFAAFE() {
		return MCROPFAAFE;
	}
	public void setMCROPFAAFE(Double mcropfaafe) {
		MCROPFAAFE = mcropfaafe;
	}
	public String getMCRPORCAD() {
		return MCRPORCAD;
	}
	public void setMCRPORCAD(String mcrporcad) {
		MCRPORCAD = mcrporcad;
	}
	public String getMCRPORDESF() {
		return MCRPORDESF;
	}
	public void setMCRPORDESF(String mcrpordesf) {
		MCRPORDESF = mcrpordesf;
	}
	public String getMCRPORDESR() {
		return MCRPORDESR;
	}
	public void setMCRPORDESR(String mcrpordesr) {
		MCRPORDESR = mcrpordesr;
	}
	public Double getMCRPORIVA() {
		return MCRPORIVA;
	}
	public void setMCRPORIVA(Double mcrporiva) {
		MCRPORIVA = mcrporiva;
	}
	public String getMCRRFCCLIE() {
		return MCRRFCCLIE;
	}
	public void setMCRRFCCLIE(String mcrrfcclie) {
		MCRRFCCLIE = mcrrfcclie;
	}
	public Double getMCRSALFACT() {
		return MCRSALFACT;
	}
	public void setMCRSALFACT(Double mcrsalfact) {
		MCRSALFACT = mcrsalfact;
	}
	public String getMCRSEFAAFE() {
		return MCRSEFAAFE;
	}
	public void setMCRSEFAAFE(String mcrsefaafe) {
		MCRSEFAAFE = mcrsefaafe;
	}
	public String getMCRSERIEFA() {
		return MCRSERIEFA;
	}
	public void setMCRSERIEFA(String mcrseriefa) {
		MCRSERIEFA = mcrseriefa;
	}
	public String getMCRSERIEFI() {
		return MCRSERIEFI;
	}
	public void setMCRSERIEFI(String mcrseriefi) {
		MCRSERIEFI = mcrseriefi;
	}
	public String getMCRSTATUS() {
		return MCRSTATUS;
	}
	public void setMCRSTATUS(String mcrstatus) {
		MCRSTATUS = mcrstatus;
	}
	public Double getMCRSUBTOT() {
		return MCRSUBTOT;
	}
	public void setMCRSUBTOT(Double mcrsubtot) {
		MCRSUBTOT = mcrsubtot;
	}
	public Double getMCRSUCDEV() {
		return MCRSUCDEV;
	}
	public void setMCRSUCDEV(Double mcrsucdev) {
		MCRSUCDEV = mcrsucdev;
	}
	public Integer getMCRSUCURSA() {
		return MCRSUCURSA;
	}
	public void setMCRSUCURSA(Integer mcrsucursa) {
		MCRSUCURSA = mcrsucursa;
	}
	public Double getMCRSUFAAFE() {
		return MCRSUFAAFE;
	}
	public void setMCRSUFAAFE(Double mcrsufaafe) {
		MCRSUFAAFE = mcrsufaafe;
	}
	public String getMCRSURTIO() {
		return MCRSURTIO;
	}
	public void setMCRSURTIO(String mcrsurtio) {
		MCRSURTIO = mcrsurtio;
	}
	public String getMCRTELEFON() {
		return MCRTELEFON;
	}
	public void setMCRTELEFON(String mcrtelefon) {
		MCRTELEFON = mcrtelefon;
	}
	public String getMCRTIFAAFE() {
		return MCRTIFAAFE;
	}
	public void setMCRTIFAAFE(String mcrtifaafe) {
		MCRTIFAAFE = mcrtifaafe;
	}
	public String getMCRTIPOFAC() {
		return MCRTIPOFAC;
	}
	public void setMCRTIPOFAC(String mcrtipofac) {
		MCRTIPOFAC = mcrtipofac;
	}
	public String getMCRTIPOFAL() {
		return MCRTIPOFAL;
	}
	public void setMCRTIPOFAL(String mcrtipofal) {
		MCRTIPOFAL = mcrtipofal;
	}
	public String getMCRTIPOMOV() {
		return MCRTIPOMOV;
	}
	public void setMCRTIPOMOV(String mcrtipomov) {
		MCRTIPOMOV = mcrtipomov;
	}
	public String getMCRTIPOVTA() {
		return MCRTIPOVTA;
	}
	public void setMCRTIPOVTA(String mcrtipovta) {
		MCRTIPOVTA = mcrtipovta;
	}
	public Double getMCRTOTAL() {
		return MCRTOTAL;
	}
	public void setMCRTOTAL(Double mcrtotal) {
		MCRTOTAL = mcrtotal;
	}
	public Double getMCRVENDEDO() {
		return MCRVENDEDO;
	}
	public void setMCRVENDEDO(Double mcrvendedo) {
		MCRVENDEDO = mcrvendedo;
	}
	public Date getMCRVTO() {
		return MCRVTO;
	}
	public void setMCRVTO(Date mcrvto) {
		MCRVTO = mcrvto;
	}
	public Double getMCRZONA() {
		return MCRZONA;
	}
	public void setMCRZONA(Double mcrzona) {
		MCRZONA = mcrzona;
	}
	public String getMES() {
		return MES;
	}
	public void setMES(String mes) {
		MES = mes;
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

	
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .append("mcrsucursa", getMCRSUCURSA())
            .append("mcridenope", getMCRIDENOPE())
            .append("mcrfecha", getMCRFECHA())
            .append("mcrseriefa", getMCRSERIEFA())
            .append("mcrtipofac", getMCRTIPOFAC())
            .append("mcrnumdoct", getMCRNUMDOCT())
            .toString();
    }

}
