package com.luxsoft.siipap.cxc;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

import com.jgoodies.uif.builder.MenuBuilder;
import com.luxsoft.siipap.swing.impl.MenuFactoryImpl;

public class CXCMenuFactory extends MenuFactoryImpl{
	
	@SuppressWarnings("unused")
	private Logger logger=Logger.getLogger(getClass());

	
	
	protected void buildCustomMenus(List<JMenu> customMenus){
		customMenus.add(buildCredito());
		customMenus.add(buildContado());
		customMenus.add(buildChequesDevueltos());
		customMenus.add(buildTramiteJuridico());
		customMenus.add(buildConsultas());
		customMenus.add(buildMantenimiento());
		//customMenus.add(buildProcesos());
		
	}
	
	protected JMenuBar buildMenuBar(){
		JMenuBar bar=super.buildMenuBar();
		//bar.setBackground(new Color(255,255,255));
		return bar;
	}
	
	protected JMenu buildCredito(){		
		MenuBuilder builder=new MenuBuilder("Crédito",'C');
		builder.add(getActionManager().getAction(CXC.DESCUENTOS_VIEW));
		builder.add(getActionManager().getAction(CXC.PROVISION_VIEW));
		builder.add(getActionManager().getAction(CXC.CREDITO_NOTAS_VIEW));
		builder.add(getActionManager().getAction(CXC.CREDITO_APLICAR_PAGO_VIEW));
		builder.add(getActionManager().getAction(CXC.REVISION_COBROS_VIEW));
		builder.add(getActionManager().getAction(CXCActions.MostrarDepositos.getId()));
		JMenu mnu=builder.getMenu();
		return mnu;
	}
	protected JMenu buildContado(){		
		MenuBuilder builder=new MenuBuilder("Contado",'m');
		builder.add(buildNotasContado());
		
		{
			/** SUB Menu de Cobranza Camioneta**/
			MenuBuilder b2=new MenuBuilder("Cobranza (CAM)",'z');			
			b2.add(getActionManager().getAction(CXC.CONTADO_APLICAR_PAGO_VIEW));			
			b2.add(getActionManager().getAction(CXC.CONTADO_TRASPASO_JURIDICO));
			builder.add(b2.getMenu());
		}
		return builder.getMenu();
	}
	
	private JMenu buildNotasContado(){		
		MenuBuilder builder=new MenuBuilder("Notas",'t');
		builder.add(getActionManager().getAction(CXC.CONTADO_NOTAS_MOS_VIEW));
		builder.add(getActionManager().getAction(CXC.CONTADO_NOTAS_CAM_VIEW));		
		return builder.getMenu();
	}
	
	
	
	protected JMenu buildChequesDevueltos(){		
		MenuBuilder builder=new MenuBuilder("Cheques Dev",'h');
		builder.add(getActionManager().getAction(CXCActions.MostrarChequesView.getId()));
		/*
		{
			
			MenuBuilder b1=new MenuBuilder("Documentos",'d');
			b1.add(getActionManager().getAction(CXC.CHEQUE_DEVUELTOS));
			b1.add(getActionManager().getAction(CXC.CHEQUE_NOTAS_VIEW));
			b1.add(getActionManager().getAction(CXC.CHEQUE_TARJETAS));
			builder.add(b1.getMenu());
		}
		
	
		{
			
			MenuBuilder b2=new MenuBuilder("Cobranza (CHE)",'z');			
			b2.add(getActionManager().getAction(CXC.CHEQUE_APLICAR_PAGO_VIEW));			
			b2.add(getActionManager().getAction(CXC.CHEQUE_TRASPASO_JURIDICO));
			builder.add(b2.getMenu());
		}
		*/
		return builder.getMenu();
		
	}
	
	protected JMenu buildTramiteJuridico(){		
		MenuBuilder builder=new MenuBuilder("Jurídico",'J');
		builder.add(getActionManager().getAction(CXC.JUR_ASIGNACION));
		builder.add(getActionManager().getAction(CXC.JUR_NOTAS_VIEW));
		{
			/** SUB Menu de Cobranza Juridico **/
			MenuBuilder b2=new MenuBuilder("Cobranza (JUR)",'z');			
			b2.add(getActionManager().getAction(CXC.JUR_APLICAR_PAGO_VIEW));			
			b2.add(getActionManager().getAction(CXC.JUR_TRASLADO_HISTORICO));
			builder.add(b2.getMenu());
		}
		return builder.getMenu();
	}
	
	private JMenu buildConsultas(){		
		MenuBuilder builder=new MenuBuilder("Consultas",'n');
		builder.add(getActionManager().getAction(CXC.CONSUTLAD_VIEW));
		builder.add(getActionManager().getAction("showReportesAction"));
		builder.add(getActionManager().getAction(CXCActions.ShowAnalisisDeCarteraView.getId()));
		builder.add(getActionManager().getAction(CXCActions.MostrarCXCView.getId()));
		return builder.getMenu();
	}	
	
	
	protected JMenu buildMantenimiento(){		
		MenuBuilder builder=new MenuBuilder("Mantenimiento",'t');	
		{
			/** SUB Menu de Catalogos**/
			MenuBuilder b2=new MenuBuilder("Catálogos",'c');			
			b2.add(getActionManager().getAction(CXC.CATALOGO_CLASIFICACION_CLIENTES));
			b2.add(getActionManager().getAction(CXC.CATALOGO_CLIENTES));
			b2.add(getActionManager().getAction(CXC.CATALOGO_SOCIOS));
			b2.add(getActionManager().getAction(CXC.CATALOGO_COBRADORES));			
			b2.add(getActionManager().getAction(CXC.CATALOGO_VENDEDORES));
			b2.add(getActionManager().getAction(CXC.CATALOGO_ABOGADOS));			
			//b2.add(getActionManager().getAction(CXC.CAT));
			builder.add(b2.getMenu());
		}		
		builder.add(buildProcesos());
		builder.add(getActionManager().getAction(CXC.MTO_NOTAS));
		builder.add(getActionManager().getAction(CXC.MTO_COMISIONES));
		builder.add(getActionManager().getAction(CXC.MTO_CONSECUTIVO_CLIENTES));
		builder.add(getActionManager().getAction(CXC.MTO_CONSECUTIVOS_DOCUMENTOS));
		builder.add(getActionManager().getAction(CXC.MTO_FORMAS_DE_PAGO));
		//builder.add(getActionManager().getAction("cargaSaldoInicial"));
		
		return builder.getMenu();
	}
	
	private JMenu buildProcesos(){		
		MenuBuilder builder=new MenuBuilder("Procesos",'P');
		{
			final MenuBuilder b2=new MenuBuilder("Comisiones",'s');			
			b2.add(getActionManager().getAction(CXCActions.CalcularComisionesVend.getId()));
			b2.add(getActionManager().getAction(CXCActions.CalcularComisionesCob.getId()));
			builder.add(b2.getMenu());
		}
		
		builder.add(getActionManager().getAction(CXCActions.ActualizarProvision.getId()));
		builder.add(getActionManager().getAction(CXCActions.GenerarPolizaCredito1.getId()));
		return builder.getMenu();
	}
	
	
	
	
}
