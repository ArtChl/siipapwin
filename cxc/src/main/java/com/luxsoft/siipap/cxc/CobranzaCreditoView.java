package com.luxsoft.siipap.cxc;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.consultas.CTodosLosPagos;
import com.luxsoft.siipap.cxc.consultas.PagosAplicadosView;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.model.Cobradores;
import com.luxsoft.siipap.cxc.model.PagosDeCreditoModel;
import com.luxsoft.siipap.cxc.pagos.CarteraCredito;
import com.luxsoft.siipap.cxc.pagos.FacturasPorCliente;
import com.luxsoft.siipap.cxc.pagos.IControladorDePagos;
import com.luxsoft.siipap.cxc.pagos.PagosAplicados;

import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.binding.ClienteBinding;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.views2.DefaultTaskView;
import com.luxsoft.siipap.swing.views2.InternalTaskTab;

/**
 * Vista central para la administracion de la cobranza de las ventas
 * de tipo credito, 
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class CobranzaCreditoView extends DefaultTaskView{
	
	private InternalTaskTab carteraTab;
	private CarteraCredito clientesView;
	
	private InternalTaskTab facturasTab;
	private FacturasPorCliente facturasView;
	
	private InternalTaskTab pagosTab;
	private PagosAplicados pagosView;
	
	private IControladorDePagos controlador;
	private final PagosDeCreditoModel model;
	
	private InternalTaskTab todosLosPagosTab;
	private PagosAplicadosView todosLosPagosView;
	
	private InternalTaskTab todosLosPagosOldTab;
	private CTodosLosPagos todosLosPagosOldView;
	
	private JXTaskPane reportes;
	
	public CobranzaCreditoView(final PagosDeCreditoModel model){
		this.model=model;
	}
	
	protected void instalarTaskPanels(final JXTaskPaneContainer container){
		System.out.println("Insalando reportes");
		reportes=new JXTaskPane();
		reportes.setTitle("Reportes");
		reportes.add(createReport1Action());
		container.add(reportes);
	}

	@Override
	protected void instalarTaskElements() {
		consultas.add(createCarteraAction());
		consultas.add(createPagosAplicadosAction());
		final Action td=new DispatchingAction(this,"showTodosLosPagos");
		CommandUtils.configAction(td, CXCActions.PagosAplicados.getId(), null);
		consultas.add(td);
		consultas.add(createOldPagosAction());
		
	}
	
	private Action createReport1Action(){
		Action a=new DispatchingAction(this,"report1");
		CommandUtils.configAction(a, CXCReportes.DiarioDeCobranza.name(), "");
		return a;
	}
	
	private Action createCarteraAction(){
		final Action a=new DispatchingAction(this,"showCarteraTab");		
		CommandUtils.configAction(a, CXCActions.CarteraDeClientes.getId(), null);
		return a;
	}
	
	private Action createPagosAplicadosAction(){
		final Action a=new DispatchingAction(this,"showPagosAplicadosTab");		
		CommandUtils.configAction(a, CXCActions.PagosAplicadosPorCliente.getId(), null);
		return a;
	}
	
	private Action createOldPagosAction(){
		final Action a=new DispatchingAction(this,"showOldPagosTab");		
		CommandUtils.configAction(a, CXCActions.OldPagosAplicados.getId(), null);
		return a;
	}
	
	
	
	/**
	 * 
	 *
	 */
	public void showCarteraTab(){
		if(clientesView==null){			
			clientesView=new CarteraCredito(){
				public List<ClienteCredito> loadData() {
					return model.buscarClientesACredito();
				}				
				public void onSelection(ClienteCredito c) {
					model.setCliente(c.getCliente());
					drillToCliente();
					
				}
				@Override
				protected void modificarComentario(ClienteCredito c) {
					controlador.actualizarComentarioDeCxC(c);
				}
				
			};			
			carteraTab=createInternalTaskTab(clientesView);			
		}
		addTab(carteraTab);
		clientesView.load();
		
	}
	
	
	public void showPagosAplicadosTab(){
		if(pagosTab==null){
			pagosView=new PagosAplicados(model,getControlador());
			pagosTab=new InternalTaskTab(pagosView);
		}
		addTab(pagosTab);
	}
	
	public void drillToCliente(){		
		if(facturasTab==null){
			facturasView=new FacturasPorCliente(model,getControlador());			
			facturasTab=new InternalTaskTab(facturasView);
		}
		addTab(facturasTab);
		facturasView.getPorNumeroField().setText("");
		facturasView.load();
	}
	
	public void showTodosLosPagos(){
		if(todosLosPagosView==null){
			todosLosPagosView=new PagosAplicadosView();
			todosLosPagosTab=createInternalTaskTab(todosLosPagosView);
			todosLosPagosView.load();
		}
		addTab(todosLosPagosTab);
		
	}
	
	public void showOldPagosTab(){
		if(todosLosPagosOldView==null){
			todosLosPagosOldView=new CTodosLosPagos();
			todosLosPagosOldTab=createInternalTaskTab(todosLosPagosOldView);
			todosLosPagosOldView.load();
		}
		addTab(todosLosPagosOldTab);
	}

	public IControladorDePagos getControlador() {
		return controlador;
	}
	public void setControlador(IControladorDePagos controlador) {
		this.controlador = controlador;
	}
	
	
	
	@Override
	public void open() {
		showCarteraTab();
	}

	public void close(){
		model.setCliente(null);
		model.clear();
		if(clientesView!=null) clientesView.close();
		if(facturasView!=null)facturasView.close();
		if(todosLosPagosView!=null)todosLosPagosView.close();
		if(pagosView!=null) pagosView.close();
		if(todosLosPagosOldView!=null) todosLosPagosOldView.close();
	}
	
	public void report1(){
		final Map<String,Object> map =new HashMap<String, Object>();		
		ReportForm1 form=new ReportForm1();
		
		form.open();
		if(!form.hasBeenCanceled()){
			String id=getString(CXCReportes.DiarioDeCobranzaCre.toString());
			map.putAll(form.getParametros());
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+map.toString());
			}
			ReportUtils.viewReport(id, map);
		}		
	}
	
	/**
	 * Forma para el reporte de cobranza
	 * 
	 * @author RUBEN
	 *
	 */
	public  class ReportForm1 extends SXAbstractDialog{
		
		private final Map<String, Object> parametros;
		
		
		private JComboBox cobradores;
		private ValueModel clienteModel;
		private ValueModel fechaModel;
		private JComponent cliente;
		private JComponent fecha;

		public ReportForm1() {
			super("Diario de Cobranza");
			parametros=new HashMap<String, Object>();
			
		}
		
		private void initComponents(){
			clienteModel=new ValueHolder(null);
			fechaModel=new ValueHolder(new Date());
			final ClienteBinding b=new ClienteBinding(clienteModel);
			cliente=b.getControl();
			cobradores=new JComboBox(Cobradores.values());
			fecha=Binder.createDateComponent(fechaModel);
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
			builder.append("Cliente",cliente,5);
			builder.nextLine();
			builder.append("Cobrador",cobradores,true);
			builder.append("Fecha",fecha,true);
			panel.add(builder.getPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			
			super.doApply();
			if(clienteModel.getValue()!=null){
				Cliente c=(Cliente)clienteModel.getValue();
				parametros.put("CLIENTE", c.getClave());
			}
			else
				parametros.put("CLIENTE", "%");
			Cobradores c=(Cobradores)cobradores.getSelectedItem();			
			if(c.getNumero()==0){
				parametros.put("COBRADOR", "%");
				parametros.put("COBRADOR__NOMBRE", "TODOS");
				
			}
			else{
				parametros.put("COBRADOR", String.valueOf(c.getNumero()));
				parametros.put("COBRADOR_NOMBRE", c.toString());
				
			}
			parametros.put("FECHA", fechaModel.getValue());
			System.out.println("Parametros: "+parametros);
			
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		 
		
	}

}
