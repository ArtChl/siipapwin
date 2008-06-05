package com.luxsoft.siipap.cxc;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uif.component.PopupButton;
import com.luxsoft.siipap.swing.impl.ToolbarFactoryImpl;
import com.luxsoft.siipap.swing.utils.CommandUtils;

public class CXCToolbarFactory extends ToolbarFactoryImpl{
	
	
	protected void addCustomButtons(ToolBarBuilder builder){
		builder.add(getActionManager().getAction(CXCActions.ShowAltA.getId()));
		builder.add(getActionManager().getAction(CXCActions.MostrarCXCView.getId()));
		
		buildReportButton(builder.getToolBar());
	}
	
	private void buildReportButton(final JToolBar bar){		
		final JButton btn=new JButton("Reportes (CRE)",CommandUtils.getIconFromResource("images/misc2/report_picture.png"));
		final JPopupMenu pm=new JPopupMenu();
		pm.add(getActionManager().getAction(CXCActions.EstadoDeCuentaReport.getId()));
		pm.add(getActionManager().getAction(CXCReportes.CobranzaCredito.getId()));
		pm.add(getActionManager().getAction(CXCReportes.PagosConNotaCre.getId()));
		pm.add(getActionManager().getAction(CXCReportes.AuxiliarNCCre.getId()));
		pm.add(getActionManager().getAction(CXCReportes.Provision.getId()));
		pm.add(getActionManager().getAction(CXCReportes.ClientesVencidos.getId()));
		pm.add(getActionManager().getAction(CXCReportes.Depositos.getId()));
		pm.add(getActionManager().getAction(CXCReportes.ChequeDevueltoContaForm.getId()));
		pm.add(getActionManager().getAction(CXCReportes.VentasPorVendedorReport.getId()));
		pm.add(getActionManager().getAction(CXCReportes.VentasCreditoContadoReport.getId()));
		pm.add(getActionManager().getAction(CXCReportes.ClientesCreditoReport.getId()));
		pm.add(getActionManager().getAction(CXCReportes.ClienteCreditoDetalleReport.getId()));
		 
		
		PopupButton pop=new PopupButton(btn,pm);
		pop.addTo(bar);
	}

}
