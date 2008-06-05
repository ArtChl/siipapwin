package com.luxsoft.siipap.cxc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractApplicationStarter;


public class CXC extends AbstractApplicationStarter{
	
	public static final String DESCUENTOS_VIEW="showDescuentosView";
	public static final String PROVISION_VIEW="showProvisionesView";
	public static final String CREDITO_NOTAS_VIEW="showCreditoNotasView";
	public static final String CREDITO_REVISION="revisionDeFactura";
	public static final String CREDITO_APLICAR_PAGO_VIEW="showAplicarPagoCreditoView";
	public static final String CREDITO_TRASPASO_JURIDICO="traspasarAJuridicoCredito";
	public static final String CONTADO_NOTAS_MOS_VIEW="showContadoNotasMosView";
	public static final String CONTADO_NOTAS_CAM_VIEW="showContadoNotasCamView";
	public static final String CONTADO_APLICAR_PAGO_VIEW="showAplicarPagoCamionetaView";
	public static final String CONTADO_TRASPASO_JURIDICO="traspasarAJuridicoCamioneta";
	public static final String REVISION_COBROS_VIEW="showRevisionCobrosView";
	
	
	public static final String CHEQUE_DEVUELTOS="showMantenimientoCheques";
	public static final String CHEQUE_NOTAS_VIEW="showChequesNotasView";
	public static final String CHEQUE_TARJETAS="showMantenimientoTarjetas";
	public static final String CHEQUE_APLICAR_PAGO_VIEW="showAplicarPagoChequeView";
	public static final String CHEQUE_TRASPASO_JURIDICO="traspasarAJuridicoCheque";
	
	public static final String JUR_ASIGNACION="asignacionJuridico";
	public static final String JUR_NOTAS_VIEW="showJurNotasView";
	public static final String JUR_APLICAR_PAGO_VIEW="showAplicarPagoJurView";
	public static final String JUR_TRASLADO_HISTORICO="trasladarHistorico";
	
	//Catalogos
	public static final String CATALOGO_CLASIFICACION_CLIENTES="clasificacionClientes";	
	public static final String CATALOGO_CLIENTES="catalogoClientes";	
	public static final String CATALOGO_SOCIOS="catalogoSocios";
	public static final String CATALOGO_COBRADORES="catalogoCobradores";
	public static final String CATALOGO_VENDEDORES="catalogoVendedores";
	public static final String CATALOGO_ABOGADOS="catalogoAbogados";	
	//public static final String CATALOGO_OPERADORES="";
	
	//
	public static final String MTO_NOTAS="tablasNotas";	
	public static final String MTO_COMISIONES="comisions";
	public static final String MTO_CONSECUTIVO_CLIENTES="consecutivoClientes";	
	public static final String MTO_FORMAS_DE_PAGO="formasDePago";
	public static final String MTO_CONSECUTIVOS_DOCUMENTOS="consecutivos";
	
		
	public static final String CONSUTLAD_VIEW="showConsultasView";
	
	public static final String ACTUALIZAR_VENTASCREDITO="actualizarVentasACredito";
	public static final String MANDAR_AVISO_DE_ATRASO="mandarAvisoDeAtraso";
	

	@Override
	protected String[] getContextPaths() {
		return new String[]{
				"swx-cxc-services.xml",
				"swx-cxc-ctx.xml"
				,"swx-cxc-views.xml"
				,"swx-cxc-actions-ctx.xml"
		};
	}

	
	@Override
	protected ApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext(getContextPaths(),ServiceLocator.getDaoContext());
		
	}

	public static void main(String[] args) {
		new CXC().start();
	}
	
}
