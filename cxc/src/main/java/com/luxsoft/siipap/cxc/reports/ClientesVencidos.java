package com.luxsoft.siipap.cxc.reports;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;

/**
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class ClientesVencidos extends SWXAction{

	@Override
	protected void execute() {
		
		final ReportForm form=new ReportForm();
		form.open();
		if(!form.hasBeenCanceled()){
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+form.getParametros());
			}			
			ReportUtils.viewReport("reportes/ClientesVencidos.jasper", form.getParametros());
		}
		form.dispose();
	}
	
	/**
	 * Forma para el reporte de cobranza
	 * 
	 * @author RUBEN
	 *
	 */
	public  class ReportForm extends SXAbstractDialog{
		
		private final Map<String, Object> parametros;		
		protected JXDatePicker f1;
		
		
		public ReportForm() {
			super("Provisión de descuentos");
			parametros=new HashMap<String, Object>();
		}
		
		private void initComponents(){			
			f1=new JXDatePicker();			
			f1.setFormats(new String[]{"dd/MM/yyyy"});
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			JPanel panel=new JPanel(new BorderLayout());
			final FormLayout layout=new FormLayout(
					"l:40dlu,3dlu,f:p:g",
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Fecha de Corte",f1,true);
			panel.add(builder.getPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			
			super.doApply();
			parametros.put("FECHA_DE_CORTE", f1.getDate());			
			System.out.println("Parametros: "+parametros);
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		
	}
	
	public static void main(String[] args) {
		ClientesVencidos action=new ClientesVencidos();
		action.execute();
	}

}
