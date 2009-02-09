package com.luxsoft.siipap.cxc.reports;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.Cliente;
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
public class EstadoDeCuentaReport extends SWXAction{

	@Override
	protected void execute() {
		
		final ReportForm form=new ReportForm();
		form.open();
		if(!form.hasBeenCanceled()){
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+form.getParametros());
			}
			if(form.getCliente()!=null && form.getTodos().isSelected()==false){
				System.out.println("Reporte 1");
				//ReportUtils.viewReport("reportes/EstadoDeCuentaCte.jasper", form.getParametros());
				ReportUtils.viewReport("reportes/EstadoDeCuentaCteConta.jasper", form.getParametros());
			}
			if(form.getCliente()==null && form.getTodos().isSelected()==true ){
				System.out.println("Reporte 2");
				//ReportUtils.viewReport("reportes/EstadoDeCuentaCte.jasper", form.getParametros());
				ReportUtils.viewReport("reportes/EstadoDeCuentaCteConta.jasper", form.getParametros());
			}		
			if(form.getCliente()==null && form.getTodos().isSelected()==false ){
				System.out.println("Reporte 3");
				ReportUtils.viewReport("reportes/EstadoDeCuentaGral.jasper", form.getParametros());
			}
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
		private Date fechaFinal;
		private JCheckBox todos;
		
		private final PresentationModel model;
		
		private JComponent jCliente;
		private JComponent jFechaIni;
		@SuppressWarnings("unused")
		private JComponent jFechaFin;
		
		

		public ReportForm() {
			super("Estado de Cuenta");
			parametros=new HashMap<String, Object>();
			model=new PresentationModel(this);
		}
		
		private void initComponents(){
			/**
			final EventList<ClienteCredito> source=GlazedLists.eventList(clientes);
			final Format format=new Format(){
				@Override
				public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
					ClienteCredito c=(ClienteCredito)obj;
					toAppendTo.append(c.getNombre());
					toAppendTo.append(" (");
					toAppendTo.append(c.getClave());
					toAppendTo.append(" )");
					return toAppendTo;
				}
				@Override
				public Object parseObject(String source, ParsePosition pos) {					
					return source;
				}
				
			};			
			final TextFilterator<ClienteCredito> filterator=GlazedLists.textFilterator(new String[]{"clave","nombre"});			
			jCliente=Binder.createBindingBox(model.getModel("cliente"),source,filterator,format);
			**/
			todos=new JCheckBox();
			todos.setSelected(false);
			jCliente=Binder.createClientesBinding(model.getModel("cliente"));
			jFechaIni=Binder.createDateComponent(model.getModel("fechaInicial"));
			jFechaFin=Binder.createDateComponent(model.getModel("fechaFinal"));
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			JPanel panel=new JPanel(new BorderLayout());
			CellConstraints cc=new CellConstraints();
			final FormLayout layout=new FormLayout(
					"l:40dlu,30dlu,60dlu, 3dlu, " +
					"l:40dlu,30dlu,p:g " +
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Cliente",jCliente,5);
			builder.nextLine();
			builder.append("Fecha corte ",jFechaIni,true);
			//builder.append("Fecha Final",jFechaFin,true);
			builder.append("Todos",todos);
			panel.add(builder.getPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			
			super.doApply();
			if(model.getValue("cliente")!=null && todos.isSelected()==false){
				Cliente c=(Cliente)model.getValue("cliente");
				parametros.put("CLIENTE", c.getClave());
				parametros.put("FECHA", model.getValue("fechaInicial"));
				System.out.println("Opcion 1");
			}
			if(model.getValue("cliente")==null && todos.isSelected()==true){
				parametros.put("CLIENTE", "%");
				parametros.put("FECHA", model.getValue("fechaInicial"));
				System.out.println("Opcion 2");
			}
			
			
			else if(model.getValue("cliente")==null && todos.isSelected()==false){
				parametros.put("CLIENTE", "%");
				parametros.put("FECHA", model.getValue("fechaInicial"));
			//parametros.put("FECHA_FINAL", model.getValue("fechaFinal"));
			System.out.println("Parametros: "+parametros);
			System.out.println("Opcion 3");
			}
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

		public Date getFechaFinal() {
			return fechaFinal;
		}
		public void setFechaFinal(Date fechaFinal) {
			Object oldValue=this.fechaFinal;
			this.fechaFinal = fechaFinal;
			firePropertyChange("fechaFinal", oldValue, fechaFinal);
		}

		public Date getFechaInicial() {
			return fechaInicial;
		}
		public void setFechaInicial(Date fechaInicial) {
			Object oldValue=this.fechaInicial;
			this.fechaInicial = fechaInicial;
			firePropertyChange("fechaInicial", oldValue, fechaInicial);
		}

		public JCheckBox getTodos() {
			return todos;
		}

		public void setTodos(JCheckBox todos) {
			this.todos = todos;
		}
		
		
		 
		
	}
	
	public static void main(String[] args) {
		EstadoDeCuentaReport action=new EstadoDeCuentaReport();
		action.execute();
	}

}
