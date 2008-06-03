package com.luxsoft.siipap.clipper.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class Mocomo implements Serializable {

    private Long id;
    //Sucursal donde se elaboro el documento
    private Integer MCMSUCURSA;
    //Identificacion de operacion del documento
    private Double MCMIDENOPE;
    //Fecha de elaboraci¢n del sistema
    private Date MCMFECHA;
    //Etapa del documento
    private String MCMETAPA;
    //Serie de la factura
    private String MCMSERIEFA;
    //Tipo de factura
    private String MCMTIPOFAC;
     //Consecutivo de la factura
    private String MCMCONSECU;
    //Tipo de factura alternativo
    private String MCMTIPOFAL;
    //Numero de documento
    private Double MCMNUMDOCT;
    //Numero de Pedido en el caso de que se tomo un pedido para facturar
    //y consecutivo de comprobante de pago, el segundo datos es a partir 
    //del 18/02/2002. 
    private Double MCMNODOCTO;
     //Numero fiscal
    private Double MCMNOFISCA;
    //Serie fiscal
    private String MCMSERIEFI;
    //Tipo de cliente
    private String MCMCLITIPO;
    //Clasificaci¢n del cliente
    private Double MCMCLASCLI;
    //Clave del cliente
    private String MCMCLAVCLI;
    //Nombre del cliente
    private String MCMNOMBCLI;
    //Clave del socio
    private String MCMCLAVSOC;
    //Nombre del socio
    private String MCMNOMBSOC;
    //RFC del cliente
    private String MCMRFCCLIE;
    //Direccion del cliente
    private String MCMCALLE;
    //Colonia del cliente
    private String MCMCOLONIA;
    //Delegaci¢n del cliente
    private String MCMDELEGAC;
    //Codigo Postal
    private String MCMCODIGOP;
    //Telefono
    private String MCMTELEFON;
    //Clave del Vendedor
    private Double MCMVENDEDO;
    //Clave del Cobrador
    private Double MCMCOBRADO;
    //Clave del Chofer
    private Double MCMCHOFER;
    //Zona correspondiente a la ruta de entrega
    private Double MCMZONA;
    //Fecha de deposito de cheque
    private Date MCMFEDEPCH;
    //Fecha vencimiento del documento.
    private Date MCMVTO;
    //Nombre de descuento real
    private String MCMNOMDESR;
    //Porcentaje de descuento real
    private String MCMPORDESR;
    //Nombre de descuento facturado
    private String MCMNOMDESF;
    //Descuento facturado o aplicado
    private String MCMPORDESF;
    //Nombre de descuento adjuntado
    private String MCMNOMCAD;
    //Porcentaje adjuntado
    private String MCMPORCAD;
    //Suma del total de las partidas antes de descuentos
    private Double MCMIMPARTI;
    //Concepto de cobro.
    private String MCMCONCEPT;
    //Importe del concepto  (MAQUILA = maniobras)
    private Double MCMIMPCONC;
    //Precio por corte
    private Double MCMIMPCORT;
    //No. de cortes a cobrar
    private Double MCMCANTCOR;
    //Subtotal (suma de partidas mas descuentos y cortes)
    private Double MCMSUBTOT;
    //Importe correspondiente al I.V.A.
    private Double MCMIMPUEST;
    //Total (Suma de subtotal mas I.V.A.)
    private Double MCMTOTAL;
    //Importe del articulo en bruto
    private Double MCMIMPARIN;
    //Subtotal del importe de la venta con I.V.A integrado
    private Double MCMSUBTOTI;
    //Impuesto del importe de la venta con I.V.A integrado
    private Double MCMIMPUESI;
    //Total del importe de la venta efectuada con I.V.A integrado
    private Double MCMTOTALI;
    //Total de kilos facturados
    private Double MCMKGSFACT;
    //Cantidad de piezas de la venta facturadas
    private Double MCMCANTIDA;
    //Porcentaje de I.V.A.
    private Double MCMPORIVA;
    //Persona que elaboro el documento.
    private String MCMFACTURI;
    //Persona que surtio la factura.
    private String MCMSURTIO;
    //Comentario capturado al elaborar la factura.
    private String MCMCOMFAC;
    //Numero de cheque o tarjeta
    private String MCMNOCHTAR;
    //Nombre del banco de donde corresponde el cheque o tarjeta
    private String MCMBANCO;
    //Hora de elaboracion del documento.
    private String MCMHORAFAC;
    //Hora real de facturacion
    private String MCMHRSREAL;
    //Fecha real de elaboracion.
    private Date MCMFECREAL;
    //Fecha de cancelacion.
    private Date MCMFECHCAN;
    //Persona que cancela.
    private String MCMCANCELO;
    //Motivo de cancelacion.
    private String MCMMOTICAN;
    //Sucursal de la factura afectada (cobranza)
    private Double MCMSUFAAFE;
    //Serie de factura afectada (cobranza)
    private String MCMSEFAAFE;
    //Tipo de factura afectada (cobranza)
    private String MCMTIFAAFE;
    //Numero de factura afectada (cobranza)
    private Double MCMNUFAAFE;
    //Tipo de movimiento
    private String MCMTIPOMOV;
    //Numero de movimiento
    private Double MCMNUMMOVI;
    //Archivo
    private String MCMARCHIVO;
    //Movimiento de devolucion (RMD)
    private String MCMMVADEV;
    //Sucursal de devolucion (RMD)
    private Double MCMSUCDEV;
    //Fecha de devolucion (RMD)
    private Date MCMFECDEV;
    //Numero de devolucion (RMD)
    private Double MCMNUMDEV;
    //Formato utilizado para el movimiento
    private String MCMFORMATO;
    //Fecha de impresion nota de credito
    private Date MCMIMPRESO;
    //Fecha periodica de comisiones de los vendedores (credito)
    private String MCMFECOMVE;
    //Fecha de comision de cheque
    private String MCMFECOMCH;
    //Lider (no se localiza dato para determinar que tipo de 
    //informacion le corresponde)
    private String MCMLIDER;
    //Control general (identifica la venta "I")
    private String MCMCTRLGRA;
    //Forma como se efectuara la cobranza (efectivo, cheque, tarjeta, etc)
    private String MCMFORMPAG;
    //Cantidad entregada (asignacion de chofer)
    private Double MCMCANTENT;
    //Concepto de maniobra
    private String MCMCONMANI;
    //Importe de maniobra
    private Double MCMIMPANI;
    //Nombre de la persona que realizo la cotizacion
    private String MCMNOMCOTI;
    //Nombre de la persona que realizo el pedido
    private String MCMNOMPEDI;
    
    private String MES;

    private Set<Almace> partidas;
    
    public Mocomo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

 
	public String getMCMARCHIVO() {
		return MCMARCHIVO;
	}
	public void setMCMARCHIVO(String mcmarchivo) {
		MCMARCHIVO = mcmarchivo;
	}
	public String getMCMBANCO() {
		return MCMBANCO;
	}
	public void setMCMBANCO(String mcmbanco) {
		MCMBANCO = mcmbanco;
	}
	public String getMCMCALLE() {
		return MCMCALLE;
	}
	public void setMCMCALLE(String mcmcalle) {
		MCMCALLE = mcmcalle;
	}
	public String getMCMCANCELO() {
		return MCMCANCELO;
	}
	public void setMCMCANCELO(String mcmcancelo) {
		MCMCANCELO = mcmcancelo;
	}
	public Double getMCMCANTCOR() {
		return MCMCANTCOR;
	}
	public void setMCMCANTCOR(Double mcmcantcor) {
		MCMCANTCOR = mcmcantcor;
	}
	public Double getMCMCANTENT() {
		return MCMCANTENT;
	}
	public void setMCMCANTENT(Double mcmcantent) {
		MCMCANTENT = mcmcantent;
	}
	public Double getMCMCANTIDA() {
		return MCMCANTIDA;
	}
	public void setMCMCANTIDA(Double mcmcantida) {
		MCMCANTIDA = mcmcantida;
	}
	public Double getMCMCHOFER() {
		return MCMCHOFER;
	}
	public void setMCMCHOFER(Double mcmchofer) {
		MCMCHOFER = mcmchofer;
	}
	public Double getMCMCLASCLI() {
		return MCMCLASCLI;
	}
	public void setMCMCLASCLI(Double mcmclascli) {
		MCMCLASCLI = mcmclascli;
	}
	public String getMCMCLAVCLI() {
		return MCMCLAVCLI;
	}
	public void setMCMCLAVCLI(String mcmclavcli) {
		MCMCLAVCLI = mcmclavcli;
	}
	public String getMCMCLAVSOC() {
		return MCMCLAVSOC;
	}
	public void setMCMCLAVSOC(String mcmclavsoc) {
		MCMCLAVSOC = mcmclavsoc;
	}
	public String getMCMCLITIPO() {
		return MCMCLITIPO;
	}
	public void setMCMCLITIPO(String mcmclitipo) {
		MCMCLITIPO = mcmclitipo;
	}
	public Double getMCMCOBRADO() {
		return MCMCOBRADO;
	}
	public void setMCMCOBRADO(Double mcmcobrado) {
		MCMCOBRADO = mcmcobrado;
	}
	public String getMCMCODIGOP() {
		return MCMCODIGOP;
	}
	public void setMCMCODIGOP(String mcmcodigop) {
		MCMCODIGOP = mcmcodigop;
	}
	public String getMCMCOLONIA() {
		return MCMCOLONIA;
	}
	public void setMCMCOLONIA(String mcmcolonia) {
		MCMCOLONIA = mcmcolonia;
	}
	public String getMCMCOMFAC() {
		return MCMCOMFAC;
	}
	public void setMCMCOMFAC(String mcmcomfac) {
		MCMCOMFAC = mcmcomfac;
	}
	public String getMCMCONCEPT() {
		return MCMCONCEPT;
	}
	public void setMCMCONCEPT(String mcmconcept) {
		MCMCONCEPT = mcmconcept;
	}
	public String getMCMCONMANI() {
		return MCMCONMANI;
	}
	public void setMCMCONMANI(String mcmconmani) {
		MCMCONMANI = mcmconmani;
	}
	public String getMCMCONSECU() {
		return MCMCONSECU;
	}
	public void setMCMCONSECU(String mcmconsecu) {
		MCMCONSECU = mcmconsecu;
	}
	public String getMCMCTRLGRA() {
		return MCMCTRLGRA;
	}
	public void setMCMCTRLGRA(String mcmctrlgra) {
		MCMCTRLGRA = mcmctrlgra;
	}
	public String getMCMDELEGAC() {
		return MCMDELEGAC;
	}
	public void setMCMDELEGAC(String mcmdelegac) {
		MCMDELEGAC = mcmdelegac;
	}
	public String getMCMETAPA() {
		return MCMETAPA;
	}
	public void setMCMETAPA(String mcmetapa) {
		MCMETAPA = mcmetapa;
	}
	public String getMCMFACTURI() {
		return MCMFACTURI;
	}
	public void setMCMFACTURI(String mcmfacturi) {
		MCMFACTURI = mcmfacturi;
	}
	public Date getMCMFECDEV() {
		return MCMFECDEV;
	}
	public void setMCMFECDEV(Date mcmfecdev) {
		MCMFECDEV = mcmfecdev;
	}
	public Date getMCMFECHA() {
		return MCMFECHA;
	}
	public void setMCMFECHA(Date mcmfecha) {
		MCMFECHA = mcmfecha;
	}
	public Date getMCMFECHCAN() {
		return MCMFECHCAN;
	}
	public void setMCMFECHCAN(Date mcmfechcan) {
		MCMFECHCAN = mcmfechcan;
	}
	public String getMCMFECOMCH() {
		return MCMFECOMCH;
	}
	public void setMCMFECOMCH(String mcmfecomch) {
		MCMFECOMCH = mcmfecomch;
	}
	public String getMCMFECOMVE() {
		return MCMFECOMVE;
	}
	public void setMCMFECOMVE(String mcmfecomve) {
		MCMFECOMVE = mcmfecomve;
	}
	public Date getMCMFEDEPCH() {
		return MCMFEDEPCH;
	}
	public void setMCMFEDEPCH(Date mcmfedepch) {
		MCMFEDEPCH = mcmfedepch;
	}
	public Date getMCMFECREAL() {
		return MCMFECREAL;
	}
	public void setMCMFECREAL(Date mcmfecreal) {
		MCMFECREAL = mcmfecreal;
	}
	public String getMCMFORMATO() {
		return MCMFORMATO;
	}
	public void setMCMFORMATO(String mcmformato) {
		MCMFORMATO = mcmformato;
	}
	public String getMCMFORMPAG() {
		return MCMFORMPAG;
	}
	public void setMCMFORMPAG(String mcmformpag) {
		MCMFORMPAG = mcmformpag;
	}
	public String getMCMHORAFAC() {
		return MCMHORAFAC;
	}
	public void setMCMHORAFAC(String mcmhorafac) {
		MCMHORAFAC = mcmhorafac;
	}
	public String getMCMHRSREAL() {
		return MCMHRSREAL;
	}
	public void setMCMHRSREAL(String mcmhrsreal) {
		MCMHRSREAL = mcmhrsreal;
	}
	public Double getMCMIDENOPE() {
		return MCMIDENOPE;
	}
	public void setMCMIDENOPE(Double mcmidenope) {
		MCMIDENOPE = mcmidenope;
	}
	public Double getMCMIMPANI() {
		return MCMIMPANI;
	}
	public void setMCMIMPANI(Double mcmimpani) {
		MCMIMPANI = mcmimpani;
	}
	public Double getMCMIMPARIN() {
		return MCMIMPARIN;
	}
	public void setMCMIMPARIN(Double mcmimparin) {
		MCMIMPARIN = mcmimparin;
	}
	public Double getMCMIMPARTI() {
		return MCMIMPARTI;
	}
	public void setMCMIMPARTI(Double mcmimparti) {
		MCMIMPARTI = mcmimparti;
	}
	public Double getMCMIMPCONC() {
		return MCMIMPCONC;
	}
	public void setMCMIMPCONC(Double mcmimpconc) {
		MCMIMPCONC = mcmimpconc;
	}
	public Double getMCMIMPCORT() {
		return MCMIMPCORT;
	}
	public void setMCMIMPCORT(Double mcmimpcort) {
		MCMIMPCORT = mcmimpcort;
	}
	public Date getMCMIMPRESO() {
		return MCMIMPRESO;
	}
	public void setMCMIMPRESO(Date mcmimpreso) {
		MCMIMPRESO = mcmimpreso;
	}
	public Double getMCMIMPUESI() {
		return MCMIMPUESI;
	}
	public void setMCMIMPUESI(Double mcmimpuesi) {
		MCMIMPUESI = mcmimpuesi;
	}
	public Double getMCMIMPUEST() {
		return MCMIMPUEST;
	}
	public void setMCMIMPUEST(Double mcmimpuest) {
		MCMIMPUEST = mcmimpuest;
	}
	public Double getMCMKGSFACT() {
		return MCMKGSFACT;
	}
	public void setMCMKGSFACT(Double mcmkgsfact) {
		MCMKGSFACT = mcmkgsfact;
	}
	public String getMCMLIDER() {
		return MCMLIDER;
	}
	public void setMCMLIDER(String mcmlider) {
		MCMLIDER = mcmlider;
	}
	public String getMCMMOTICAN() {
		return MCMMOTICAN;
	}
	public void setMCMMOTICAN(String mcmmotican) {
		MCMMOTICAN = mcmmotican;
	}
	public String getMCMMVADEV() {
		return MCMMVADEV;
	}
	public void setMCMMVADEV(String mcmmvadev) {
		MCMMVADEV = mcmmvadev;
	}
	public String getMCMNOMCOTI() {
		return MCMNOMCOTI;
	}
	public void setMCMNOMCOTI(String mcmnomcoti) {
		MCMNOMCOTI = mcmnomcoti;
	}
	public String getMCMNOMPEDI() {
		return MCMNOMPEDI;
	}
	public void setMCMNOMPEDI(String mcmnompedi) {
		MCMNOMPEDI = mcmnompedi;
	}
	public String getMCMNOCHTAR() {
		return MCMNOCHTAR;
	}
	public void setMCMNOCHTAR(String mcmnochtar) {
		MCMNOCHTAR = mcmnochtar;
	}
	public Double getMCMNODOCTO() {
		return MCMNODOCTO;
	}
	public void setMCMNODOCTO(Double mcmnodocto) {
		MCMNODOCTO = mcmnodocto;
	}
	public Double getMCMNOFISCA() {
		return MCMNOFISCA;
	}
	public void setMCMNOFISCA(Double mcmnofisca) {
		MCMNOFISCA = mcmnofisca;
	}
	public String getMCMNOMBCLI() {
		return MCMNOMBCLI;
	}
	public void setMCMNOMBCLI(String mcmnombcli) {
		MCMNOMBCLI = mcmnombcli;
	}
	public String getMCMNOMBSOC() {
		return MCMNOMBSOC;
	}
	public void setMCMNOMBSOC(String mcmnombsoc) {
		MCMNOMBSOC = mcmnombsoc;
	}
	public String getMCMNOMCAD() {
		return MCMNOMCAD;
	}
	public void setMCMNOMCAD(String mcmnomcad) {
		MCMNOMCAD = mcmnomcad;
	}
	public String getMCMNOMDESF() {
		return MCMNOMDESF;
	}
	public void setMCMNOMDESF(String mcmnomdesf) {
		MCMNOMDESF = mcmnomdesf;
	}
	public String getMCMNOMDESR() {
		return MCMNOMDESR;
	}
	public void setMCMNOMDESR(String mcmnomdesr) {
		MCMNOMDESR = mcmnomdesr;
	}
	public Double getMCMNUFAAFE() {
		return MCMNUFAAFE;
	}
	public void setMCMNUFAAFE(Double mcmnufaafe) {
		MCMNUFAAFE = mcmnufaafe;
	}
	public Double getMCMNUMDEV() {
		return MCMNUMDEV;
	}
	public void setMCMNUMDEV(Double mcmnumdev) {
		MCMNUMDEV = mcmnumdev;
	}
	public Double getMCMNUMDOCT() {
		return MCMNUMDOCT;
	}
	public void setMCMNUMDOCT(Double mcmnumdoct) {
		MCMNUMDOCT = mcmnumdoct;
	}
	public Double getMCMNUMMOVI() {
		return MCMNUMMOVI;
	}
	public void setMCMNUMMOVI(Double mcmnummovi) {
		MCMNUMMOVI = mcmnummovi;
	}
	public String getMCMPORCAD() {
		return MCMPORCAD;
	}
	public void setMCMPORCAD(String mcmporcad) {
		MCMPORCAD = mcmporcad;
	}
	public String getMCMPORDESF() {
		return MCMPORDESF;
	}
	public void setMCMPORDESF(String mcmpordesf) {
		MCMPORDESF = mcmpordesf;
	}
	public String getMCMPORDESR() {
		return MCMPORDESR;
	}
	public void setMCMPORDESR(String mcmpordesr) {
		MCMPORDESR = mcmpordesr;
	}
	public Double getMCMPORIVA() {
		return MCMPORIVA;
	}
	public void setMCMPORIVA(Double mcmporiva) {
		MCMPORIVA = mcmporiva;
	}
	public String getMCMRFCCLIE() {
		return MCMRFCCLIE;
	}
	public void setMCMRFCCLIE(String mcmrfcclie) {
		MCMRFCCLIE = mcmrfcclie;
	}
	public String getMCMSEFAAFE() {
		return MCMSEFAAFE;
	}
	public void setMCMSEFAAFE(String mcmsefaafe) {
		MCMSEFAAFE = mcmsefaafe;
	}
	public String getMCMSERIEFA() {
		return MCMSERIEFA;
	}
	public void setMCMSERIEFA(String mcmseriefa) {
		MCMSERIEFA = mcmseriefa;
	}
	public String getMCMSERIEFI() {
		return MCMSERIEFI;
	}
	public void setMCMSERIEFI(String mcmseriefi) {
		MCMSERIEFI = mcmseriefi;
	}
	public Double getMCMSUBTOT() {
		return MCMSUBTOT;
	}
	public void setMCMSUBTOT(Double mcmsubtot) {
		MCMSUBTOT = mcmsubtot;
	}
	public Double getMCMSUBTOTI() {
		return MCMSUBTOTI;
	}
	public void setMCMSUBTOTI(Double mcmsubtoti) {
		MCMSUBTOTI = mcmsubtoti;
	}
	public Double getMCMSUCDEV() {
		return MCMSUCDEV;
	}
	public void setMCMSUCDEV(Double mcmsucdev) {
		MCMSUCDEV = mcmsucdev;
	}
	public Integer getMCMSUCURSA() {
		return MCMSUCURSA;
	}
	public void setMCMSUCURSA(Integer mcmsucursa) {
		MCMSUCURSA = mcmsucursa;
	}
	public Double getMCMSUFAAFE() {
		return MCMSUFAAFE;
	}
	public void setMCMSUFAAFE(Double mcmsufaafe) {
		MCMSUFAAFE = mcmsufaafe;
	}
	public String getMCMSURTIO() {
		return MCMSURTIO;
	}
	public void setMCMSURTIO(String mcmsurtio) {
		MCMSURTIO = mcmsurtio;
	}
	public String getMCMTELEFON() {
		return MCMTELEFON;
	}
	public void setMCMTELEFON(String mcmtelefon) {
		MCMTELEFON = mcmtelefon;
	}
	public String getMCMTIFAAFE() {
		return MCMTIFAAFE;
	}
	public void setMCMTIFAAFE(String mcmtifaafe) {
		MCMTIFAAFE = mcmtifaafe;
	}
	public String getMCMTIPOFAC() {
		return MCMTIPOFAC;
	}
	public void setMCMTIPOFAC(String mcmtipofac) {
		MCMTIPOFAC = mcmtipofac;
	}
	public String getMCMTIPOFAL() {
		return MCMTIPOFAL;
	}
	public void setMCMTIPOFAL(String mcmtipofal) {
		MCMTIPOFAL = mcmtipofal;
	}
	public String getMCMTIPOMOV() {
		return MCMTIPOMOV;
	}
	public void setMCMTIPOMOV(String mcmtipomov) {
		MCMTIPOMOV = mcmtipomov;
	}
	public Double getMCMTOTAL() {
		return MCMTOTAL;
	}
	public void setMCMTOTAL(Double mcmtotal) {
		MCMTOTAL = mcmtotal;
	}
	public Double getMCMTOTALI() {
		return MCMTOTALI;
	}
	public void setMCMTOTALI(Double mcmtotali) {
		MCMTOTALI = mcmtotali;
	}
	public Double getMCMVENDEDO() {
		return MCMVENDEDO;
	}
	public void setMCMVENDEDO(Double mcmvendedo) {
		MCMVENDEDO = mcmvendedo;
	}
	public Date getMCMVTO() {
		return MCMVTO;
	}
	public void setMCMVTO(Date mcmvto) {
		MCMVTO = mcmvto;
	}
	public Double getMCMZONA() {
		return MCMZONA;
	}
	public void setMCMZONA(Double mcmzona) {
		MCMZONA = mcmzona;
	}
	public String getMES() {
		return MES;
	}
	public void setMES(String mes) {
		MES = mes;
	}

	public Set getPartidas() {
		if(partidas==null)
			partidas=new HashSet<Almace>();
		return partidas;
	}
	
	@SuppressWarnings("unused")
	private void setPartidas(Set<Almace> partidas) {
		this.partidas = partidas;
	}
	
	/**
	 * TODO Pendiente pot completar
	 */
	public boolean equals(Object other){
        Mocomo castOther=(Mocomo)other;
        return new EqualsBuilder()
        		.append(this.MCMSUCURSA,castOther.getMCMSUCURSA())
        		.append(this.MCMTIPOMOV, castOther.getMCMTIPOMOV())
        		.isEquals();
    }
    
	/**
	 * TODO Pendiente por completar
	 */
    public int hashCode(){
        return new HashCodeBuilder(17,37)
        	.toHashCode();
    }

	
	
	public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .append("mcmsucursa", getMCMSUCURSA())
            .append("mcmidenope", getMCMIDENOPE())
            .append("mcmfecha", getMCMFECHA())
            .append("mcmseriefa", getMCMSERIEFA())
            .append("mcmtipofac", getMCMTIPOFAC())
            .append("mcmnumdoct", getMCMNUMDOCT())
            .toString();
    }

}
