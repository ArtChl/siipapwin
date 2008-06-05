package com.luxsoft.siipap.cxc.swing.cobranza;

import java.awt.BorderLayout;
import java.util.Comparator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.luxsoft.siipap.cxc.CXC;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.swing.task.RevisionDeFacturasDialog;
import com.luxsoft.siipap.cxc.swing.task.TraspasoAJuridicoDialog;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.crud.BasicCURDGridPanel;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Vista principal para el mantenimiento de cobranza como son pagos
 * a facturas pendientes de clientes
 *  
 * 
 * @author Ruben Cancino
 *@deprecated Sustituida por CobranzaView
 */
@SuppressWarnings("unchecked")
public class CobranzaViewB extends AbstractView{
	
	private CobranzaViewModel model;	
	private TabbedPanel tabbedPanel;
	private TitledTab clientesTab;
	private TitledTab facturasTab;
	
	private BasicCURDGridPanel clientesGrid;
	private BasicCURDGridPanel facturasGrid;
	private JComponent facturasPanel;
	
	private Action revisionAction;	
	private Action pagarAction;
	private Action pagarEnGrupo;
	private Action traspasoAction;
	private Action actualizarVenta;
	private Action mandarAviso;
	
	/** Vistas **/
		
		
	
	@Override
	protected JComponent buildContent() {		
		JPanel content=new JPanel(new BorderLayout());		
		FormLayout layout=new FormLayout("f:180dlu,p:g","f:p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(createFilterPanel(),cc.xy(1,1 ));
		builder.add(createBrowserPanel(),cc.xy(2, 1));		
		content.add(builder.getPanel(),BorderLayout.CENTER);		
		return content;
	}
	
	private JComponent createFilterPanel(){
		
		JXTaskPaneContainer container=new JXTaskPaneContainer();
		
		JXTaskPane operaciones=new JXTaskPane();
		operaciones.setSpecial(true);
		operaciones.setTitle("Operaciones");
		{
			
			operaciones.add(getRevisionAction());
			operaciones.add(getPagarAction());			
			operaciones.add(getPagarEnGrupo());			
			operaciones.add(getMandarAviso());
			operaciones.add(getTraspasoAction());
			operaciones.add(getActualizarVenta());
		}		
		container.add(operaciones);
		
		JXTaskPane carteraGeneral=new JXTaskPane();
		carteraGeneral.setTitle("Cartera General");
		carteraGeneral.setExpanded(false);
		carteraGeneral.setSpecial(true);
		container.add(carteraGeneral);		
		
		JXTaskPane carteraSeleccion=new JXTaskPane();
		carteraSeleccion.setTitle("Cartera Selección");
		carteraSeleccion.setExpanded(false);
		carteraSeleccion.setSpecial(true);		
		container.add(carteraSeleccion);		
		
		return container;
	}
	
	private JComponent createBrowserPanel(){	
		tabbedPanel=new TabbedPanel();
		
		JPanel p=new JPanel(new BorderLayout(5,10));
		
		ToolBarBuilder tb=new ToolBarBuilder();
		tb.add(CommandUtils.createRefreshAction(this, "load"));
		p.add(tb.getToolBar(),BorderLayout.NORTH);		
		p.add(createClientesBrowser(),BorderLayout.CENTER);
		p.setBorder(Borders.DLU4_BORDER);
		clientesTab=new TitledTab("",null,p,null);		
		tabbedPanel.addTab(clientesTab);
		return tabbedPanel;
	}
	
	private JComponent createClientesBrowser(){
		
		final EventList source=model.getClientes();
		final String[] props={"cliente.nombre","saldo","saldoVencido","facturasVencidas","atrasoMaximo"};
		final String[] cols={"Cliente","Saldo","S.Vencido","F.Vencidas","Atraso"};		
		final TableFormat<ClienteCredito> tf=GlazedLists.tableFormat(ClienteCredito.class,props,cols);
		
		clientesGrid=new ClientesGrid(source,tf){
			@Override
			protected void select(List selected) {	
				if(selected.isEmpty()) return;
				ClienteCredito cc=(ClienteCredito)selected.get(0);
				drill(cc.getCliente());
			}			
		};
		TextFilterator textf=GlazedLists.textFilterator(new String[]{"cliente.nombre"});
		clientesGrid.setTextFilterator(textf);
		return clientesGrid.getControl();
	}
	
	public void drill(final Cliente c){
		showVentasConSaldoPorCliente();
		
		SwingWorker<List<Venta>, String> worker=new SwingWorker<List<Venta>, String>(){			
			protected List<Venta> doInBackground() throws Exception {
				return model.loadVentas(c);
			}
			@Override
			protected void done() {
				try {					
					model.getVentas().clear();
					List<Venta> ventas=get();
					System.out.println("Ventas: "+ventas.size());
					model.getVentas().addAll(ventas);
					facturasGrid.pack();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		};
		TaskUtils.executeSwingWorker(worker);			
	}
	
	public void showVentasConSaldoPorCliente(){
		if(facturasPanel==null){
			facturasPanel=createFacturasPorClientePanel();
			facturasTab=new TitledTab("Facturas",null,facturasPanel,null);
			tabbedPanel.addTab(facturasTab);
			facturasTab.setSelected(true);
			//tabbedPanel.setSelectedComponent(facturasPanel);
		}else{
			facturasTab.setSelected(true);
			//tabbedPanel.setSelectedComponent(facturasPanel);
		}		
	}
	
	private JComponent createFacturasPorClientePanel(){
		final String[] props={"id","sucursal","tipo","numero","numeroFiscal","fecha","vencimiento","total","saldo","atraso","credito.reprogramarPago"};
		final String[] labels={"Id","Sucursal","Tipo","Numero","No Fiscal","Fecha","Vencimiento","Total","Saldo","Atraso","F.Pago"};
		final TableFormat tf=GlazedLists.tableFormat(Venta.class,props,labels);
		facturasGrid=new BasicCURDGridPanel(model.getVentas(),tf);
		
		CobranzaUtils.decorateGridForFacturasPendientes((JXTable)facturasGrid.getGrid());
		Comparator c1=GlazedLists.beanPropertyComparator(Venta.class, "atraso");
		facturasGrid.setDefaultComparator(GlazedLists.reverseComparator(c1));
		facturasGrid.setTextFilterator(GlazedLists.textFilterator(props));
		
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(CobranzaUtils.createNiceClienteHeader(model.getClienteModel()),BorderLayout.NORTH);
		panel.add(facturasGrid.getControl(),BorderLayout.CENTER);
		return panel;
	}


	@Override
	protected void dispose() {		
		model.dispose();
	}	

	
	public void revision(){
		if((facturasGrid==null) || facturasGrid.getSelected().isEmpty()) return;
		int index=facturasGrid.getSelectinModel().getMinSelectionIndex();
		RevisionDeFacturasDialog dialog=new RevisionDeFacturasDialog(facturasGrid.getSelected());
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			//TODO Salvar las revisiones
		}
	}
	
	public void traspaso(){
		if((facturasGrid==null) || facturasGrid.getSelected().isEmpty()) return;
		TraspasoAJuridicoDialog dialog=new TraspasoAJuridicoDialog(facturasGrid.getSelected());
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			//TODO Pasar a juridico o pre juridico
		}
	}
	
	public void pagar(){		
		
	}
	
	
	public void pagarEnGrupo(){
		if((facturasGrid==null) || facturasGrid.getSelected().isEmpty()) return;
		EventList<Venta> ventas=facturasGrid.getSelected();
		if(!ventas.isEmpty()){
			PagoEnGrupoForm.aplicarPago(ventas);
			//TODO Las modificaciones en el saldo de la factura se tienen que ver en el grid
		}
	}
	
	public Action getPagarAction() {
		if(pagarAction==null){
			pagarAction=new DispatchingAction(this,"pagar");
			configure(pagarAction, "pagarFacturasAction");
		}
		return pagarAction;
	}

	public Action getRevisionAction() {
		if(revisionAction==null){
			revisionAction=new DispatchingAction(this,"revision");
			configure(revisionAction, "revizarFacturasAction");
		}
		return revisionAction;
	}

	public Action getTraspasoAction() {
		if(traspasoAction==null){
			traspasoAction=new DispatchingAction(this,"traspaso");
			configure(traspasoAction, "traspasoJuridicoAction");
		}
		return traspasoAction;
	}
	public Action getPagarEnGrupo(){
		if(pagarEnGrupo==null){
			pagarEnGrupo=new DispatchingAction(this,"pagarEnGrupo");
			configure(pagarEnGrupo, "pagarEnGrupo");
		}
		return pagarEnGrupo;
	}
	
	public Action getActualizarVenta(){
		if(this.actualizarVenta==null){
			actualizarVenta=new DispatchingAction(this,"actualizarVenta");
			configure(actualizarVenta, CXC.ACTUALIZAR_VENTASCREDITO);
		}
		return actualizarVenta;
	}
	
	public void actualizarVenta(){
		if(facturasGrid.getSelected().isEmpty()) return;
		Venta v=(Venta)facturasGrid.getSelected().get(0);
		try {			
			model.actualizarVenta(v);
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.showError("Error actualizando venta: "+v.getId(), e);
		}
	}
	
	public Action getMandarAviso(){
		if(this.mandarAviso==null){
			mandarAviso=new DispatchingAction(this,"mandarAviso");
			configure(mandarAviso, CXC.MANDAR_AVISO_DE_ATRASO);
			
		}
		return mandarAviso;
	}
	
	public void configure(final Action action,final String id){
		if(getActionConfigurer()!=null){
			getActionConfigurer().configure(action, id);
		}
	}

	public void load(){
		SwingWorker<List<ClienteCredito>, String> worker=new SwingWorker<List<ClienteCredito>, String>(){

			@Override
			protected List<ClienteCredito> doInBackground() throws Exception {
				return model.loadClientes();
			}
			protected void done() {
				try {
					model.getClientes().clear();
					model.getClientes().addAll(get());
					clientesGrid.pack();
				} catch (Exception e) {
					logger.error(e);
				}
			}
		};
		TaskUtils.executeSwingWorker(worker);
	}
	

	public CobranzaViewModel getModel() {
		return model;
	}

	public void setModel(CobranzaViewModel model) {
		this.model = model;
	}
	
	/** Collaboradores **/
	
	private class ClientesGrid extends BasicCURDGridPanel{

		public ClientesGrid(EventList source, TableFormat tableFormat) {
			super(source, tableFormat);
			
		}
		
		public JTable getGrid() {
			if(drillGrid==null){
				drillGrid=new CobranzaUtils.FacturasGrid();
				JXTable grid=(JXTable)drillGrid;
				grid.setColumnControlVisible(true);
				grid.setHorizontalScrollEnabled(true);
				grid.setSortable(false);							
				grid.setRolloverEnabled(false);				
				grid.setRolloverEnabled(true);
				
				
			}
			return drillGrid;
		}
		
		
	}
	
	public static void main(String[] args) {
		final CobranzaViewModel model=new CobranzaViewModel();
		model.setManager(ServiceLocator.getCXCManager());
		model.setVentasManager(ServiceLocator.getVentasManager());
		final CobranzaViewB view=new CobranzaViewB();
		view.setModel(model);
		SXAbstractDialog dialog=new SXAbstractDialog("test"){

			@Override
			protected JComponent buildContent() {
				return view.getContent();
			}
			
		};
		dialog.open();
		System.exit(0);
	}
	
}
