package com.luxsoft.siipap.cxc.reports;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.model.Cobradores;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;

/**
 * Genera el reporte de Estado de cuenta de un cliente
 * 
 * @author Ruben Cancino
 *
 */
public class CobranzaCredito extends SWXAction{

	@Override
	protected void execute() {
		
		final ReportForm form=new ReportForm();
		form.open();
		if(!form.hasBeenCanceled()){
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+form.getParametros());
			}
			ReportUtils.viewReport("reportes/Cobranza4.jasper", form.getParametros());
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
		
		private Cliente cliente;
		private Date fechaInicial;
		private int cobrador;
		
		
		private final PresentationModel model;
		
		private JComponent jCliente;
		private JComponent jFechaIni;
		private JComponent jcobrador;
		//private final DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		
		

		public ReportForm() {
			super("Cobranza Crédito");
			parametros=new HashMap<String, Object>();
			model=new PresentationModel(this);
		}
		
		private void initComponents(){			
			jCliente=Binder.createClientesBinding(model.getModel("cliente"));
			jFechaIni=Binder.createDateComponent(model.getModel("fechaInicial"));
			jcobrador=CXCBindings.createCobradorBinding(model.getModel("cobrador"));
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			JPanel panel=new JPanel(new BorderLayout());
			final FormLayout layout=new FormLayout(
					"l:40dlu,3dlu,p, 3dlu, " +
					"l:40dlu,3dlu,p:g " +
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Cliente",jCliente,5);
			builder.nextLine();
			builder.append("Cobrador",jcobrador,true);
			builder.append("Fecha ",jFechaIni,true);
			
			
			panel.add(builder.getPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			
			super.doApply();
			if(model.getValue("cliente")!=null){
				Cliente c=(Cliente)model.getValue("cliente");
				parametros.put("CLIENTE", c.getClave());
			}
			else
				parametros.put("CLIENTE", "%");
			final Date fecha=(Date)model.getValue("fechaInicial");
			parametros.put("FECHA",fecha );
			String cob=getCobrador()==0?"%":String.valueOf(getCobrador());
			parametros.put("COBRADOR", cob);
			parametros.put("COBRADOR_NOMBRE", Cobradores.getCobrador(getCobrador()).toString());
			System.out.println("Parametros: "+parametros);
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}

		public Cliente getCliente() {
			return cliente;
		}
		public void setCliente(Cliente cliente) {
			Object oldValue=this.cliente;
			this.cliente = cliente;
			firePropertyChange("cliente", oldValue, cliente);
		}

		public Date getFechaInicial() {
			return fechaInicial;
		}
		public void setFechaInicial(Date fechaInicial) {
			Object oldValue=this.fechaInicial;
			this.fechaInicial = fechaInicial;
			firePropertyChange("fechaInicial", oldValue, fechaInicial);
		}

		public int getCobrador() {
			return cobrador;
		}

		public void setCobrador(int cobrador) {
			int oldValue=this.cobrador;
			this.cobrador = cobrador;
			firePropertyChange("cobrador", oldValue, cobrador);
		}
		
	}
	
	public static void main(String[] args) {
		CobranzaCredito action=new CobranzaCredito();
		action.execute();
	}

}
