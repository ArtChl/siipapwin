package com.luxsoft.siipap.cxc.pagos;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.clipper.ExportadorCliente;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.catalogos.CatalogosController;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.utils.Browsers;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.ResourcesUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.InternalTaskView;

/**
 * Vista de la cartera de clienes de tipo credito
 * 
 * @author Ruben Cancino
 *
 */
public class CarteraCredito implements InternalTaskView{
	
	private JXTable grid;
	private JTextField inputField;
	private JComponent filterPanel;
	private EventList<ClienteCredito> clientes;
	private SortedList<ClienteCredito> sortedList;
	private EventSelectionModel<ClienteCredito> selection;
	private Action selectAction;
	private Action loadAction;
	private Action comentarioAction;
	private Action recordatorioAction;
	private Action mantenimientoAction;
	private Action mostrarPagosConDisponible;

	public JComponent getControl() {	
		
		grid=ComponentUtils.getStandardTable();
		inputField=new JTextField(20);
		
		clientes=GlazedLists.eventList(new BasicEventList<ClienteCredito>());
		
		final TextFilterator<ClienteCredito> filterator=GlazedLists.textFilterator(new String[]{"cliente.nombre","cliente.clave"});
		final TextComponentMatcherEditor<ClienteCredito> editor=new TextComponentMatcherEditor<ClienteCredito>(inputField,filterator);
		final FilterList<ClienteCredito> filterList=new FilterList<ClienteCredito>(clientes,editor);
		sortedList=new SortedList<ClienteCredito>(filterList,null);
		
				
		final TableFormat<ClienteCredito> tf=CXCTableFormats.getClienteCreditoTf();		
		final EventTableModel<ClienteCredito> tm=new EventTableModel<ClienteCredito>(sortedList,tf);		
		selection=new EventSelectionModel<ClienteCredito>(sortedList);
		selection.setSelectionMode(ListSelection.SINGLE_SELECTION);
		grid.setModel(tm);
		grid.setSelectionModel(selection);
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addEnterAction(grid, getSelectAction());
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					select();
				}
			}			
		});
		new TableComparatorChooser<ClienteCredito>(grid,sortedList,true);
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeader(),BorderLayout.NORTH);		
		panel.add(new JScrollPane(grid),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildHeader(){
		//return new HeaderPanel("Cartera de Clientes","");
		return new Header("Cartera de Clientes","Lista de clientes (Credito)").getHeader();
	}
	
	public JComponent getFilterPanel(){
		if(filterPanel==null){
			FormLayout layout=new FormLayout(
					"3dlu,l:65dlu,p:g",
					"p");
			CellConstraints cc=new CellConstraints();
			PanelBuilder builder=new PanelBuilder(layout);
			JLabel l=DefaultComponentFactory.getInstance().createTitle("Cliente: ");
			builder.add(l,cc.xy(2,1));
			builder.add(inputField,cc.xy(3,1));
			filterPanel=builder.getPanel();
			filterPanel.setOpaque(false);
		}
		return filterPanel;
	}
	
	
	private Action getSelectAction(){
		if(selectAction==null){
			selectAction=new DispatchingAction(this,"onSelection");
		}
		return selectAction;
	}
	private Action getComentarioAction(){
		if(comentarioAction==null){
			comentarioAction=new DispatchingAction(this,"comentario");
			CommandUtils.configAction(comentarioAction, "CXC.comentarioAction","");
		}
		return comentarioAction;
	}
	
	private Action getMantenimientoAction(){
		if(mantenimientoAction==null){
			mantenimientoAction=new DispatchingAction(this,"mantenimiento");
			CommandUtils.configAction(mantenimientoAction, CXCActions.MantenimientoDeClientesCredito.getId(), "");
		}
		return mantenimientoAction;
	}
	
	private Action getRecordatorioAction(){
		if(recordatorioAction==null){
			recordatorioAction=new DispatchingAction(this,"recordatorio");
			CommandUtils.configAction(recordatorioAction, "CarteraView.mandarRecordatorio", "");
		}
		return recordatorioAction;
	}
	
	
	
	private void select(){
		if(!selection.getSelected().isEmpty()){
			onSelection(selection.getSelected().get(0));
		}
	}
	
	/**
	 * Metodo ejecutado al hacer doble click en un cliente, se ocupa para taladrar
	 * desde la vista que la creo. 
	 * 
	 * @param selected
	 */
	protected void onSelection(ClienteCredito selected){
		
	}

	public Icon getIcon() {
		return ResourcesUtils.getIconFromResource("images2/layout.png");
	}

	public String getTitle() {
		return "Clientes";
	}

	public void instalOperacionesAction(JXTaskPane operaciones) {
		operaciones.add(getLoadAction());
		operaciones.add(getComentarioAction());
		operaciones.add(getMantenimientoAction());
		//operaciones.add(getRecordatorioAction());
		if(mostrarPagosConDisponible==null){
			mostrarPagosConDisponible=new DispatchingAction(this,"mostrarPagosConDisponible");
			CommandUtils.configAction(mostrarPagosConDisponible, CXCActions.MostrarPagosConDisponible.getId(), "");
		}
		operaciones.add(mostrarPagosConDisponible);
		operaciones.add(createExportarSaldoAction());
		
	}
	
	private Action createExportarSaldoAction(){
		final Action a=new DispatchingAction(this,"exportarSaldo");		
		CommandUtils.configAction(a, "Exportar Saldo", null);
		return a;
	}

	public void instalProcesosActions(JXTaskPane procesos) {
		// TODO Auto-generated method stub
		
	}

	public void installDetallesPanel(JXTaskPane detalle) {
		// TODO Auto-generated method stub
		
	}

	public void installFiltrosPanel(JXTaskPane filtros) {
		filtros.add(getFilterPanel());
		
	}
	
	
	public void close() {
		clientes.clear();
		
	}
	
	private Action getLoadAction(){
		if(loadAction==null){
			loadAction=CommandUtils.createLoadAction(this, "load");
		}
		return loadAction;
	}
	
	public void comentario(){
		if(!selection.getSelected().isEmpty()){
			ClienteCredito c=selection.getSelected().get(0);
			modificarComentario(c);
			selection.getSelected().set(0, c);
		}
	}
	
	public void mantenimiento(){
		if(!selection.getSelected().isEmpty()){
			ClienteCredito c=selection.getSelected().get(0);
			CatalogosController.actualizarClienteCredito(c);
			
		}
	}
	
	public void exportarSaldo(){
		if(!selection.getSelected().isEmpty()){
			for( ClienteCredito c:selection.getSelected()){
				final ExportadorCliente exportador=(ExportadorCliente)ServiceLocator.getDaoContext().getBean("exportadorClientes");
				final String res=exportador.exportarSaldo(c.getClave());
				MessageUtils.showMessage(res, "Exportación");
			}
		}
	}
	
	/**
	 * En esta implementacin como no se tiene acceso al CXCManager
	 * este metodo sera sobre escrito en la vista controladora
	 * ConbranzaCreditoView 
	 * 
	 * @param c
	 */
	protected void modificarComentario(final ClienteCredito c){
		
	}
	
	/**
	 * Template method para obtener la lista de clientes de esta cartera 
	 * 
	 * @return
	 */
	public List<ClienteCredito> loadData(){
		return null;
	}
	
	public void load(){
		SwingWorker<List<ClienteCredito>, String> worker=new SwingWorker<List<ClienteCredito>, String>(){
			@Override
			protected List<ClienteCredito> doInBackground() throws Exception {
				return loadData();
			}
			protected void done() {
				try {
					clientes.clear();
					clientes.addAll(get());
					grid.packAll();
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}
		};
		TaskUtils.executeSwingWorker(worker);
		//actualizarClientes();
	}
	/*
	public void actualizarClientes(){
		SwingWorker<String, ClienteCredito> worker=new SwingWorker<String, ClienteCredito>(){

			@SuppressWarnings("unchecked")
			@Override
			protected String doInBackground() throws Exception {
				List<Map<String, Object>> rows=ServiceLocator.getJdbcTemplate().queryForList(
						"select clave,nombre,vendedor,ATRASO_MAX,ULTIMAVENTA,VTA_NETA_CRE,pagos,saldo,SALDO_VENC,FACTS_VENC,ULTIMOPAGO from V_CLIENTESCRE");
				for(Map<String, Object> row:rows){
					final String clave=(String)row.get("clave");
					final ClienteCredito cc=buscarCliente(clave);
					if(cc!=null){
						int atraso=((BigDecimal)row.get("ATRASO_MAX")).intValue();
						Date uv=(Date)row.get("ULTIMAVENTA");
						final BigDecimal venta=((BigDecimal)row.get("VTA_NETA_CRE"));
						final BigDecimal pagos=((BigDecimal)row.get("pagos"));
						final BigDecimal saldo=((BigDecimal)row.get("saldo"));
						final BigDecimal saldov=((BigDecimal)row.get("SALDO_VENC"));
						int vencidas=((BigDecimal)row.get("FACTS_VENC")).intValue();
						final Date ultimo=((Date)row.get("ULTIMOPAGO"));
						cc.setAtrasoMaximo(atraso);
						cc.setPagos(pagos);
						cc.setSaldo(saldo);
						cc.setSaldoVencido(saldov);
						cc.setFacturasVencidas(vencidas);
						cc.setUltimoPago(ultimo);
						cc.setVentaNeta(CantidadMonetaria.pesos(venta.doubleValue()));
						cc.setUltimaVenta(uv);
					}
					//System.out.println("Actualizando cliente: "+cc);
					publish(cc);
				}
				return "OK";
			}

			@Override
			protected void process(List<ClienteCredito> chunks) {
				for(ClienteCredito cc:chunks){
					int index=clientes.indexOf(cc);
					if(cc!=null)
						clientes.set(index, cc);
					
				}
				grid.packAll();
			}
			
		};
		
		worker.execute();
	}
	*/
	private ClienteCredito buscarCliente(final String clave){
		return (ClienteCredito)CollectionUtils.find(sortedList, new Predicate(){
			public boolean evaluate(Object object) {
				ClienteCredito cc=(ClienteCredito)object;
				return cc.getClave().equalsIgnoreCase(clave);
			}			
		});
	}
	
	public void mostrarPagosConDisponible(){
		Browsers.mostrarSaldosAFavor();
	}
	
	
	
	
	

}
