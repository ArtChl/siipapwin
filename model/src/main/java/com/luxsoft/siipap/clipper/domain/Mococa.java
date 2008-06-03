package com.luxsoft.siipap.clipper.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;



public class Mococa implements Serializable {

    private Long id;
    
    /**
     * Sucursal donde se elaboro el documento
     */
    private Integer MCASUCURSA;
    
    //Numero de documento
    private Double MCANUMDOCT;
    //Identificaci¢n de operacion del documento
    private Double MCAIDENOPE;
    //Fecha de elaboraci¢n del sistema
    private Date MCAFECHA;
    //Serie de la factura
    private String MCASERIEFA;
    //Tipo de factura
    private String MCATIPOFAC;
    //Etapa del documento
    private String MCAETAPA;
    //Consecutivo de la factura
    private String MCACONSECU;
    //Tipo de factura alternativo
    private String MCATIPOFAL;
    //Descripcion del tipo de factura
    private String MCADESTFAC;
    //Tipo de venta (CON=contado,CRE=credito)
    private String MCATIPOVTA;
    //Numero de Pedido en el caso de que se tomo un 
    //pedido para facturar y consecutivo de comprobante 
    //de pago, el segundo datos es a partir del 18/02/2002.
    private Double MCANODOCTO;
    //Numero fiscal
    private Double MCANOFISCA;
    //Serie fiscal
    private String MCASERIEFI;
    //Tipo de cliente
    private String MCACLITIPO;
    // Clasificaci¢n del cliente
    private Double MCACLASCLI;
    //Clave del cliente
    private String MCACLAVCLI;
    //Nombre del cliente
    private String MCANOMBCLI;
    //Clave del socio
    private String MCACLAVSOC;
    //Nombre del socio
    private String MCANOMBSOC;
    //RFC del cliente
    private String MCARFCCLIE;
    //Direcci¢n del cliente
    private String MCACALLE;
    //Colonia del cliente
    private String MCACOLONIA;
    //Delegacion del cliente
    private String MCADELEGAC;
    //Codigo Postal
    private String MCACODIGOP;
    //Telefono
    private String MCATELEFON;
    //Clave del Vendedor
    private Double MCAVENDEDO;
    //Clave del Cobrador
    private Double MCACOBRADO;
    //Clave del Chofer
    private Double MCACHOFER;
    //Zona correspondiente a la ruta de entrega
    private Double MCAZONA;
    //Fecha de deposito de cheque
    private Date MCAFEDEPCH;
    //Fecha vencimiento del documento
    private Date MCAVTO;
    //Dia de la semana para la toma a rev. por part. del cliente
    private Double MCADIAREVI;
    //Dia de la semana para pagos. por part. del cliente
    private Double MCADIAPAGO;
    //Nombre de descuento real
    private String MCANOMDESR;
    //Porcentaje de descuento real
    private String MCAPORDESR;
    //Nombre de descuento facturado
    private String MCANOMDESF;
    //Descuento facturado o aplicado
    private String MCAPORDESF;
    //Nombre de descuento adjuntado
    private String MCANOMCAD;
    //Porcentaje adjuntado
    private String MCAPORCAD;
    //Suma del total de las partidas antes de descuentos
    private Double MCAIMPARTI;
    //Concepto de cobro
    private String MCACONCEPT;
    //Importe del concepto  (MAQUILA = maniobras)
    private Double MCAIMPCONC;
    //Precio por corte
    private Double MCAIMPCORT;
    //No. de cortes a cobrar
    private Double MCACANTCOR;
    //Subtotal (suma de partidas mas descuentos y cortes)
    private Double MCASUBTOT;
    //Importe correspondiente al I.V.A.
    private Double MCAIMPUEST;
    //Total (Suma de subtotal mas I.V.A.)
    private Double MCATOTAL;
    //Suma de kilos facturados.
    private Double MCAKGSFACT;
    //Suma de unidades facturadas
    private Double MCACANTIDA;
    //Porcentaje de I.V.A.
    private Double MCAPORIVA;
    //Persona que elaboro el documento.
    private String MCAFACTURI;
    //Persona que surtio la factura.
    private String MCASURTIO;
    //Comentario capturado al elaborar la factura.
    private String MCACOMFAC;
    //Numero de cheque o tarjeta
    private String MCANOCHTAR;
    //Nombre del banco de donde corresponde el cheque o tarjeta
    private String MCABANCO;
    //Hora de elaboracion del documento.
    private String MCAHORAFAC;
    //Fecha real de elaboracion.
    private Date MCAFECREAL;
    //Hora real de elaboracion del documento (no procede)
    private String MCAHRSREAL;
    //Fecha de cancelacion.
    private Date MCAFECHCAN;
    //Persona que cancela.
    private String MCACANCELO;
    //Motivo de cancelacion.
    private String MCAMOTICAN;
    //Fecha de factura afectada (cobranza)
    private Date MCAFEFAAFE;
    //Sucursal de la factura afectada (cobranza)
    private Double MCASUFAAFE;
    //Serie de factura afectada (cobranza)
    private String MCASEFAAFE;
    //Tipo de factura afectada (cobranza)
    private String MCATIFAAFE;
    //Numero de factura afectada (cobranza)
    private Double MCANUFAAFE;
    //Operacion de la factura afectada (cobranza)
    private Double MCAOPFAAFE;
    //Tipo de movimiento
    private String MCATIPOMOV;
    //Numero de movimiento 
    private Double MCANUMMOVI;
    //Archivo
    private String MCAARCHIVO;
    //Movimiento de devolucion (RMD)
    private String MCAMVADEV;
    //Sucursal de la devolucion (RMD)
    private Double MCASUCDEV;
    //Fecha de devolucion (RMD)
    private Date MCAFECDEV;
    //Numero de devolucion (RMD)
    private Double MCANUMDEV;
    //Movimiento de pedido o cotizacion
    private String MCAMOPECO;
    //Fecha de impresion nota de credito
    private Date MCAIMPRESO;
    //Nota de credito provisional
    private Double MCANCRPROV;
    //Formato empleado al imprimir la factura.
    private String MCAFORMATO;
    //Fecha periodica de comisiones de los vendedores (credito)
    private String MCAFECOMVE;
    //Fecha de comision de cheque
    private String MCAFECOMCH;
    //Saldo en factura (importe)
    private Double MCASALFACT;
    //Lider (no se localiza dato para determinar que tipo de 
    //informacion corresponde
    private String MCALIDER;
    //Importe del articulo en bruto
    private Double MCAIMPARIN;
    //Subtotal del importe de la venta con I.V.A. integrado
    private Double MCASUBTOTI;
    //Impuesto del importe de la venta con I.V.A integrado (0)
    private Double MCAIMPUESI;
    //Total del importe de la venta efectuada con I.V.A integrado
    private Double MCATOTALI;
    //Control general (identifica la venta "I")
    private String MCACTRLGRA;
    //Forma como se efectuara la cobranza (efectivo, tarjeta, cheque, etc)
    private String MCAFORMPAG;
    //Cantidad entregada (asignaciones de chofer)
    private Double MCACANTENT;
    //Concepto de maniobra
    private String MCACONMANI;
    //Importe de maniobra
    private Double MCAIMPMANI;
    //Nombre de la persona que realizo el pedido
    private String MCANOMPEDI;
    //Nombre de la persona que realizo la cotizacion
    private String MCANOMCOTI;

    private String MES;


    private Set<Almace> partidas;

    
    public Mococa() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMCAARCHIVO() {
        return MCAARCHIVO;
    }
    public void setMCAARCHIVO(String mcaarchivo) {
        MCAARCHIVO = mcaarchivo;
    }
    public String getMCABANCO() {
        return MCABANCO;
    }
    public void setMCABANCO(String mcabanco) {
        MCABANCO = mcabanco;
    }
    public String getMCACALLE() {
        return MCACALLE;
    }
    public void setMCACALLE(String mcacalle) {
        MCACALLE = mcacalle;
    }
    public String getMCACANCELO() {
        return MCACANCELO;
    }
    public void setMCACANCELO(String mcacancelo) {
        MCACANCELO = mcacancelo;
    }
    public Double getMCACANTCOR() {
        return MCACANTCOR;
    }
    public void setMCACANTCOR(Double mcacantcor) {
        MCACANTCOR = mcacantcor;
    }
    public Double getMCACANTENT() {
        return MCACANTENT;
    }
    public void setMCACANTENT(Double mcacantent) {
        MCACANTENT = mcacantent;
    }
    public Double getMCACANTIDA() {
        return MCACANTIDA;
    }
    public void setMCACANTIDA(Double mcacantida) {
        MCACANTIDA = mcacantida;
    }
    public Double getMCACHOFER() {
        return MCACHOFER;
    }
    public void setMCACHOFER(Double mcachofer) {
        MCACHOFER = mcachofer;
    }
    public Double getMCACLASCLI() {
        return MCACLASCLI;
    }
    public void setMCACLASCLI(Double mcaclascli) {
        MCACLASCLI = mcaclascli;
    }
    public String getMCACLAVCLI() {
        return MCACLAVCLI;
    }
    public void setMCACLAVCLI(String mcaclavcli) {
        MCACLAVCLI = mcaclavcli;
    }
    public String getMCACLAVSOC() {
        return MCACLAVSOC;
    }
    public void setMCACLAVSOC(String mcaclavsoc) {
        MCACLAVSOC = mcaclavsoc;
    }
    public String getMCACLITIPO() {
        return MCACLITIPO;
    }
    public void setMCACLITIPO(String mcaclitipo) {
        MCACLITIPO = mcaclitipo;
    }
    public Double getMCACOBRADO() {
        return MCACOBRADO;
    }
    public void setMCACOBRADO(Double mcacobrado) {
        MCACOBRADO = mcacobrado;
    }
    public String getMCACODIGOP() {
        return MCACODIGOP;
    }
    public void setMCACODIGOP(String mcacodigop) {
        MCACODIGOP = mcacodigop;
    }
    public String getMCACOLONIA() {
        return MCACOLONIA;
    }
    public void setMCACOLONIA(String mcacolonia) {
        MCACOLONIA = mcacolonia;
    }
    public String getMCACOMFAC() {
        return MCACOMFAC;
    }
    public void setMCACOMFAC(String mcacomfac) {
        MCACOMFAC = mcacomfac;
    }
    public String getMCACONCEPT() {
        return MCACONCEPT;
    }
    public void setMCACONCEPT(String mcaconcept) {
        MCACONCEPT = mcaconcept;
    }
    public String getMCACONMANI() {
        return MCACONMANI;
    }
    public void setMCACONMANI(String mcaconmani) {
        MCACONMANI = mcaconmani;
    }
    public String getMCACONSECU() {
        return MCACONSECU;
    }
    public void setMCACONSECU(String mcaconsecu) {
        MCACONSECU = mcaconsecu;
    }
    public String getMCACTRLGRA() {
        return MCACTRLGRA;
    }
    public void setMCACTRLGRA(String mcactrlgra) {
        MCACTRLGRA = mcactrlgra;
    }
    public String getMCADELEGAC() {
        return MCADELEGAC;
    }
    public void setMCADELEGAC(String mcadelegac) {
        MCADELEGAC = mcadelegac;
    }
    public String getMCADESTFAC() {
        return MCADESTFAC;
    }
    public void setMCADESTFAC(String mcadestfac) {
        MCADESTFAC = mcadestfac;
    }
    public Double getMCADIAPAGO() {
        return MCADIAPAGO;
    }
    public void setMCADIAPAGO(Double mcadiapago) {
        MCADIAPAGO = mcadiapago;
    }
    public Double getMCADIAREVI() {
        return MCADIAREVI;
    }
    public void setMCADIAREVI(Double mcadiarevi) {
        MCADIAREVI = mcadiarevi;
    }
    public String getMCAETAPA() {
        return MCAETAPA;
    }
    public void setMCAETAPA(String mcaetapa) {
        MCAETAPA = mcaetapa;
    }
    public String getMCAFACTURI() {
        return MCAFACTURI;
    }
    public void setMCAFACTURI(String mcafacturi) {
        MCAFACTURI = mcafacturi;
    }
    public Date getMCAFECDEV() {
        return MCAFECDEV;
    }
    public void setMCAFECDEV(Date mcafecdev) {
        MCAFECDEV = mcafecdev;
    }
    public Date getMCAFECHA() {
        return MCAFECHA;
    }
    public void setMCAFECHA(Date mcafecha) {
        MCAFECHA = mcafecha;
    }
    public Date getMCAFECHCAN() {
        return MCAFECHCAN;
    }
    public void setMCAFECHCAN(Date mcafechcan) {
        MCAFECHCAN = mcafechcan;
    }
    public String getMCAFECOMCH() {
        return MCAFECOMCH;
    }
    public void setMCAFECOMCH(String mcafecomch) {
        MCAFECOMCH = mcafecomch;
    }
    public String getMCAFECOMVE() {
        return MCAFECOMVE;
    }
    public void setMCAFECOMVE(String mcafecomve) {
        MCAFECOMVE = mcafecomve;
    }
    public Date getMCAFECREAL() {
        return MCAFECREAL;
    }
    public void setMCAFECREAL(Date mcafecreal) {
        MCAFECREAL = mcafecreal;
    }
    public Date getMCAFEDEPCH() {
        return MCAFEDEPCH;
    }
    public void setMCAFEDEPCH(Date mcafedepch) {
        MCAFEDEPCH = mcafedepch;
    }
    public Date getMCAFEFAAFE() {
        return MCAFEFAAFE;
    }
    public void setMCAFEFAAFE(Date mcafefaafe) {
        MCAFEFAAFE = mcafefaafe;
    }
    public String getMCAFORMATO() {
        return MCAFORMATO;
    }
    public void setMCAFORMATO(String mcaformato) {
        MCAFORMATO = mcaformato;
    }
    public String getMCAFORMPAG() {
        return MCAFORMPAG;
    }
    public void setMCAFORMPAG(String mcaformpag) {
        MCAFORMPAG = mcaformpag;
    }
    public String getMCAHORAFAC() {
        return MCAHORAFAC;
    }
    public void setMCAHORAFAC(String mcahorafac) {
        MCAHORAFAC = mcahorafac;
    }
    public String getMCAHRSREAL() {
        return MCAHRSREAL;
    }
    public void setMCAHRSREAL(String mcahrsreal) {
        MCAHRSREAL = mcahrsreal;
    }
    public Double getMCAIDENOPE() {
        return MCAIDENOPE;
    }
    public void setMCAIDENOPE(Double mcaidenope) {
        MCAIDENOPE = mcaidenope;
    }
    public Double getMCAIMPARIN() {
        return MCAIMPARIN;
    }
    public void setMCAIMPARIN(Double mcaimparin) {
        MCAIMPARIN = mcaimparin;
    }
    public Double getMCAIMPARTI() {
        return MCAIMPARTI;
    }
    public void setMCAIMPARTI(Double mcaimparti) {
        MCAIMPARTI = mcaimparti;
    }
    public Double getMCAIMPCONC() {
        return MCAIMPCONC;
    }
    public void setMCAIMPCONC(Double mcaimpconc) {
        MCAIMPCONC = mcaimpconc;
    }
    public Double getMCAIMPCORT() {
        return MCAIMPCORT;
    }
    public void setMCAIMPCORT(Double mcaimpcort) {
        MCAIMPCORT = mcaimpcort;
    }
    public Double getMCAIMPMANI() {
        return MCAIMPMANI;
    }
    public void setMCAIMPMANI(Double mcaimpmani) {
        MCAIMPMANI = mcaimpmani;
    }
    public Date getMCAIMPRESO() {
        return MCAIMPRESO;
    }
    public void setMCAIMPRESO(Date mcaimpreso) {
        MCAIMPRESO = mcaimpreso;
    }
    public Double getMCAIMPUESI() {
        return MCAIMPUESI;
    }
    public void setMCAIMPUESI(Double mcaimpuesi) {
        MCAIMPUESI = mcaimpuesi;
    }
    public Double getMCAIMPUEST() {
        return MCAIMPUEST;
    }
    public void setMCAIMPUEST(Double mcaimpuest) {
        MCAIMPUEST = mcaimpuest;
    }
    public Double getMCAKGSFACT() {
        return MCAKGSFACT;
    }
    public void setMCAKGSFACT(Double mcakgsfact) {
        MCAKGSFACT = mcakgsfact;
    }
    public String getMCALIDER() {
        return MCALIDER;
    }
    public void setMCALIDER(String mcalider) {
        MCALIDER = mcalider;
    }
    public String getMCAMOPECO() {
        return MCAMOPECO;
    }
    public void setMCAMOPECO(String mcamopeco) {
        MCAMOPECO = mcamopeco;
    }
    public String getMCAMOTICAN() {
        return MCAMOTICAN;
    }
    public void setMCAMOTICAN(String mcamotican) {
        MCAMOTICAN = mcamotican;
    }
    public String getMCAMVADEV() {
        return MCAMVADEV;
    }
    public void setMCAMVADEV(String mcamvadev) {
        MCAMVADEV = mcamvadev;
    }
    public String getMCANOMCOTI() {
		return MCANOMCOTI;
	}
	public void setMCANOMCOTI(String mcanomcoti) {
		MCANOMCOTI = mcanomcoti;
	}
	public String getMCANOMPEDI() {
		return MCANOMPEDI;
	}
	public void setMCANOMPEDI(String mcanompedi) {
		MCANOMPEDI = mcanompedi;
	}
	public Double getMCANCRPROV() {
        return MCANCRPROV;
    }
    public void setMCANCRPROV(Double mcancrprov) {
        MCANCRPROV = mcancrprov;
    }
    public String getMCANOCHTAR() {
        return MCANOCHTAR;
    }
    public void setMCANOCHTAR(String mcanochtar) {
        MCANOCHTAR = mcanochtar;
    }
    public Double getMCANODOCTO() {
        return MCANODOCTO;
    }
    public void setMCANODOCTO(Double mcanodocto) {
        MCANODOCTO = mcanodocto;
    }
    public Double getMCANOFISCA() {
        return MCANOFISCA;
    }
    public void setMCANOFISCA(Double mcanofisca) {
        MCANOFISCA = mcanofisca;
    }
    public String getMCANOMBCLI() {
        return MCANOMBCLI;
    }
    public void setMCANOMBCLI(String mcanombcli) {
        MCANOMBCLI = mcanombcli;
    }
    public String getMCANOMBSOC() {
        return MCANOMBSOC;
    }
    public void setMCANOMBSOC(String mcanombsoc) {
        MCANOMBSOC = mcanombsoc;
    }
    public String getMCANOMCAD() {
        return MCANOMCAD;
    }
    public void setMCANOMCAD(String mcanomcad) {
        MCANOMCAD = mcanomcad;
    }
    public String getMCANOMDESF() {
        return MCANOMDESF;
    }
    public void setMCANOMDESF(String mcanomdesf) {
        MCANOMDESF = mcanomdesf;
    }
    public String getMCANOMDESR() {
        return MCANOMDESR;
    }
    public void setMCANOMDESR(String mcanomdesr) {
        MCANOMDESR = mcanomdesr;
    }
    public Double getMCANUFAAFE() {
        return MCANUFAAFE;
    }
    public void setMCANUFAAFE(Double mcanufaafe) {
        MCANUFAAFE = mcanufaafe;
    }
    public Double getMCANUMDEV() {
        return MCANUMDEV;
    }
    public void setMCANUMDEV(Double mcanumdev) {
        MCANUMDEV = mcanumdev;
    }
    public Double getMCANUMDOCT() {
        return MCANUMDOCT;
    }
    public void setMCANUMDOCT(Double mcanumdoct) {
        MCANUMDOCT = mcanumdoct;
    }
    public Double getMCANUMMOVI() {
        return MCANUMMOVI;
    }
    public void setMCANUMMOVI(Double mcanummovi) {
        MCANUMMOVI = mcanummovi;
    }
    public Double getMCAOPFAAFE() {
        return MCAOPFAAFE;
    }
    public void setMCAOPFAAFE(Double mcaopfaafe) {
        MCAOPFAAFE = mcaopfaafe;
    }
    public String getMCAPORCAD() {
        return MCAPORCAD;
    }
    public void setMCAPORCAD(String mcaporcad) {
        MCAPORCAD = mcaporcad;
    }
    public String getMCAPORDESF() {
        return MCAPORDESF;
    }
    public void setMCAPORDESF(String mcapordesf) {
        MCAPORDESF = mcapordesf;
    }
    public String getMCAPORDESR() {
        return MCAPORDESR;
    }
    public void setMCAPORDESR(String mcapordesr) {
        MCAPORDESR = mcapordesr;
    }
    public Double getMCAPORIVA() {
        return MCAPORIVA;
    }
    public void setMCAPORIVA(Double mcaporiva) {
        MCAPORIVA = mcaporiva;
    }
    public String getMCARFCCLIE() {
        return MCARFCCLIE;
    }
    public void setMCARFCCLIE(String mcarfcclie) {
        MCARFCCLIE = mcarfcclie;
    }
    public Double getMCASALFACT() {
        return MCASALFACT;
    }
    public void setMCASALFACT(Double mcasalfact) {
        MCASALFACT = mcasalfact;
    }
    public String getMCASEFAAFE() {
        return MCASEFAAFE;
    }
    public void setMCASEFAAFE(String mcasefaafe) {
        MCASEFAAFE = mcasefaafe;
    }
    public String getMCASERIEFA() {
        return MCASERIEFA;
    }
    public void setMCASERIEFA(String mcaseriefa) {
        MCASERIEFA = mcaseriefa;
    }
    public String getMCASERIEFI() {
        return MCASERIEFI;
    }
    public void setMCASERIEFI(String mcaseriefi) {
        MCASERIEFI = mcaseriefi;
    }
    public Double getMCASUBTOT() {
        return MCASUBTOT;
    }
    public void setMCASUBTOT(Double mcasubtot) {
        MCASUBTOT = mcasubtot;
    }
    public Double getMCASUBTOTI() {
        return MCASUBTOTI;
    }
    public void setMCASUBTOTI(Double mcasubtoti) {
        MCASUBTOTI = mcasubtoti;
    }
    public Double getMCASUCDEV() {
        return MCASUCDEV;
    }
    public void setMCASUCDEV(Double mcasucdev) {
        MCASUCDEV = mcasucdev;
    }
    public Integer getMCASUCURSA() {
        return MCASUCURSA;
    }
    public void setMCASUCURSA(Integer mcasucursa) {
        MCASUCURSA = mcasucursa;
    }
    public Double getMCASUFAAFE() {
        return MCASUFAAFE;
    }
    public void setMCASUFAAFE(Double mcasufaafe) {
        MCASUFAAFE = mcasufaafe;
    }
    public String getMCASURTIO() {
        return MCASURTIO;
    }
    public void setMCASURTIO(String mcasurtio) {
        MCASURTIO = mcasurtio;
    }
    public String getMCATELEFON() {
        return MCATELEFON;
    }
    public void setMCATELEFON(String mcatelefon) {
        MCATELEFON = mcatelefon;
    }
    public String getMCATIFAAFE() {
        return MCATIFAAFE;
    }
    public void setMCATIFAAFE(String mcatifaafe) {
        MCATIFAAFE = mcatifaafe;
    }
    public String getMCATIPOFAC() {
        return MCATIPOFAC;
    }
    public void setMCATIPOFAC(String mcatipofac) {
        MCATIPOFAC = mcatipofac;
    }
    public String getMCATIPOFAL() {
        return MCATIPOFAL;
    }
    public void setMCATIPOFAL(String mcatipofal) {
        MCATIPOFAL = mcatipofal;
    }
    public String getMCATIPOMOV() {
        return MCATIPOMOV;
    }
    public void setMCATIPOMOV(String mcatipomov) {
        MCATIPOMOV = mcatipomov;
    }
    public String getMCATIPOVTA() {
        return MCATIPOVTA;
    }
    public void setMCATIPOVTA(String mcatipovta) {
        MCATIPOVTA = mcatipovta;
    }
    public Double getMCATOTAL() {
        return MCATOTAL;
    }
    public void setMCATOTAL(Double mcatotal) {
        MCATOTAL = mcatotal;
    }
    public Double getMCATOTALI() {
        return MCATOTALI;
    }
    public void setMCATOTALI(Double mcatotali) {
        MCATOTALI = mcatotali;
    }
    public Double getMCAVENDEDO() {
        return MCAVENDEDO;
    }
    public void setMCAVENDEDO(Double mcavendedo) {
        MCAVENDEDO = mcavendedo;
    }
    public Date getMCAVTO() {
        return MCAVTO;
    }
    public void setMCAVTO(Date mcavto) {
        MCAVTO = mcavto;
    }
    public Double getMCAZONA() {
        return MCAZONA;
    }
    public void setMCAZONA(Double mcazona) {
        MCAZONA = mcazona;
    }
    public String getMES() {
        return MES;
    }
    public void setMES(String mes) {
        MES = mes;
    }
    
	public Set<Almace> getPartidas() {
		if(partidas==null){
			partidas=new HashSet<Almace>();
		}
		return partidas;
	}
	
	@SuppressWarnings("unused")
	private void setPartidas(Set<Almace> partidas) {
		this.partidas = partidas;
	}
	
	public void add(final Almace almace){
		almace.setMococa(this);
		getPartidas().add(almace);
	}
    
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .append("MCASUCURSA", getMCASUCURSA())
            .append("MCANUMDOCT", getMCANUMDOCT())
            .append("MCAIDENOPE", getMCAIDENOPE())
            .append("MCAFECHA", getMCAFECHA())
            .append("MCASERIEFA", getMCASERIEFA())
            .append("MCATIPOFAC", getMCATIPOFAC())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof Mococa) ) return false;
        Mococa castOther = (Mococa) other;
        return new EqualsBuilder()
            .append(this.getMCASUCURSA(), castOther.getMCASUCURSA())
            .append(this.getMCANUMDOCT(), castOther.getMCANUMDOCT())
            .append(this.getMCAIDENOPE(), castOther.getMCAIDENOPE())
            .append(this.getMCAFECHA(), castOther.getMCAFECHA())
            .append(this.getMCASERIEFA(), castOther.getMCASERIEFA())
            .append(this.getMCATIPOFAC(), castOther.getMCATIPOFAC())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getMCASUCURSA())
            .append(getMCANUMDOCT())
            .append(getMCAIDENOPE())
            .append(getMCAFECHA())
            .append(getMCASERIEFA())
            .append(getMCATIPOFAC())
            .toHashCode();
    }

}
