package com.luxsoft.siipap.cxc.pagos;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.model.PagosDeCreditoModel;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.ResourcesUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.InternalTaskView;

/**
 * Consulta con informacion de los pagos de un cliente
 * Presenta algunas funciones para su mantenimiento
 * 
 * La consulta presenta dos listas una por PagoM y otra por
 * Pago. La unica funcion relevante es la capacidad de eliminar pagos o aplicaciones
 * es decir PagoM o pagos
 * 
 * @author Ruben Cancino
 *
 */
public class PagosAplicados implements InternalTaskView{
	
	private final IControladorDePagos controlador;
	private final PagosDeCreditoModel model;

	private HeaderPanel header;
	private JPanel filterPanel;
	private SelectorDePagos masterSelector;
	private JXTable detailGrid;
		
	private EventList<Pago> pagos;
	private EventSelectionModel<Pago> detailSelection; 
	
	/***Accions ***/
	private Action loadAction;
	private Action cambiarPeriodo;
	private Action deleteAction;
	
	public PagosAplicados(final PagosDeCreditoModel model,final IControladorDePagos controlador ) {		
		this.controlador = controlador;
		this.model = model;
		model.addPropertyChangeListener("cliente", new SelectionHandler());
	}

	public JComponent getControl() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeader(),BorderLayout.NORTH);
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final JSplitPane sp=new JSplitPane(
				JSplitPane.VERTICAL_SPLIT
				,buildMasterGrid()
				,buildDetailGrid()
				);
		sp.setResizeWeight(.3);
		return sp;
	}
	
	private JComponent buildMasterGrid(){
		pagos=model.getPagos();
		masterSelector=new SelectorDePagos(pagos){
			@Override
			protected boolean eliminarPago(PagoM pago) {
				return controlador.eliminarPagoM(pago);
			}			
		};
		final JScrollPane sp=new JScrollPane(masterSelector.getGrid());
		return sp;
	}
	
	private JComponent buildDetailGrid(){
		final String[] props=new String[]{"pagoM.id","id","clave","documento","numeroFiscal","importe","venta.descuentos","venta.devolucionesCred","venta.saldo"};
		final String[] cols=new String[]{"PagoId","id","clave","Documento","N.Fiscal","Importe","Descuentos","Devoluciones","Saldo"};
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class,props,cols);
		final FilterList<Pago> pagosFiltrados=new FilterList<Pago>(pagos,masterSelector);		
		final SortedList<Pago> sortedPagos=new SortedList<Pago>(pagosFiltrados,null);
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(sortedPagos,tf);
		detailSelection=new EventSelectionModel<Pago>(sortedPagos);
		detailGrid=ComponentUtils.getStandardTable();
		detailGrid.setModel(tm);
		detailGrid.setSelectionModel(detailSelection);		
		final JPopupMenu mnu=ComponentUtils.createPopupMenu("eliminar", getEliminarPagoAction());
		ComponentUtils.decorateForPopup(detailGrid, mnu);
		new TableComparatorChooser<Pago>(detailGrid,sortedPagos,true);
		final JScrollPane sp=new JScrollPane(detailGrid);		
		return sp;
	}
	
	private JComponent buildHeader(){		
		header=ComponentUtils.getBigHeader(getClienteHeader(),getDescriptionHeader());
			
		
		return header;
	}

	public Icon getIcon() {		
		return ResourcesUtils.getIconFromResource("");
	}

	public String getTitle() {
		return "Pagos";
	}

	public void instalOperacionesAction(JXTaskPane operaciones) {		
		if(loadAction==null){
			loadAction=CommandUtils.createLoadAction(this, "load");
			loadAction.putValue(Action.SHORT_DESCRIPTION, "Cargar pagos");
		}
		if(cambiarPeriodo==null){
			cambiarPeriodo=new DispatchingAction(this,"cambiarPeriodo");
			CommandUtils.configAction(cambiarPeriodo, "seleccionarPeriodo", "");
		}		
		operaciones.add(loadAction);
		operaciones.add(cambiarPeriodo);		
	}
	
	public Action getEliminarPagoAction(){
		if(deleteAction==null){
			deleteAction=new DispatchingAction(this,"eliminarPago");
			CommandUtils.configAction(deleteAction, CXCActions.EliminarPago.getId(), "");
		}
		return deleteAction;
	}

	public void instalProcesosActions(JXTaskPane procesos) {
		procesos.add(masterSelector.getEliminarAction());
		procesos.add(getEliminarPagoAction());
	}

	public void installDetallesPanel(JXTaskPane detalle) {
				
	}

	public void installFiltrosPanel(JXTaskPane filtros) {
		if(filterPanel==null){
			filterPanel=new JPanel();
			filterPanel.setOpaque(false);
		}
		filtros.add(filterPanel);
	}	
	
	public void close() {
		this.pagos.clear();
	}
	
	
	
	public void cambiarPeriodo(){
		AbstractDialog dialog=Binder.createPeriodoSelector(model.getModel("periodo"));
		dialog.open();
		if(!dialog.hasBeenCanceled()){			
			updateSelection();
			load();
		}
	}
	
	private String getClienteHeader(){
		if(model.getCliente()!=null){
			String pattern="{0} ({1})";
			return MessageFormat.format(pattern, 
					model.getCliente().getNombre(),model.getCliente().getClave());
		}
		return "";
	}
	
	private String getDescriptionHeader(){
		String pattern="Lista de pagos aplicados para el perio:  {0}" ;
		return MessageFormat.format(pattern, model.getModel("periodo"));
	}
	
	private void updateSelection(){		
		header.setTitle(getClienteHeader());
		header.setDescription(getDescriptionHeader());		
	}
	
	public void eliminarPago(){
		if(!detailSelection.isSelectionEmpty()){
			Pago p=detailSelection.getSelected().get(0);
			if(controlador.eliminarPago(p)){
				detailSelection.getSelected().remove(p);
			}
		}
	}
	
	public void load(){
		SwingWorker<List<Pago>, String> worker=new SwingWorker<List<Pago>, String>(){
			
			protected List<Pago> doInBackground() throws Exception {
				return model.buscarPagos();
			}
			protected void done() {
				try {
					pagos.clear();
					pagos.addAll(get());
					System.out.println("Pagos :"+pagos.size()+" Para el periodo :");
					masterSelector.getGrid().packAll();
					detailGrid.packAll();
				} catch (Exception e) {
					MessageUtils.showError("Error cargando facturas para el cliente: "+model.getCliente(),e);
				}
			}			
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	
	private class SelectionHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			updateSelection();
		}		
	}
		
	/**
	 * This {@link MatcherEditor} matches Pagos if their PagoM is selected.
	 * 
	 * @author Ruben Cancino
	 *
	 */
	public class SelectorDePagos extends AbstractMatcherEditor<Pago> implements ListSelectionListener{
		
		private EventList<PagoM> source;
		private EventList<PagoM> pagosSeleccionados;
		
		private JXTable grid;
		
		public SelectorDePagos(final EventList<Pago> pagos){
			source=new FunctionList<Pago, PagoM>(pagos, new ToPagoM());
			source=new UniqueList<PagoM>(source,GlazedLists.beanPropertyComparator(PagoM.class, "id"));
			init();
		}
		
		private void init(){
			final SortedList<PagoM> sortedPagos=new SortedList<PagoM>(source,null);
			
			final TableFormat<PagoM> tf=CXCTableFormats.getPagoMTF();
			final EventTableModel<PagoM> tm=new EventTableModel<PagoM>(sortedPagos,tf);
			
			grid=ComponentUtils.getStandardTable();
			grid.setModel(tm);
			
			final EventSelectionModel<PagoM> selectionModel =new EventSelectionModel<PagoM>(sortedPagos);
			pagosSeleccionados=selectionModel.getSelected();
			grid.setSelectionModel(selectionModel);
			selectionModel.addListSelectionListener(this);
			new TableComparatorChooser<PagoM>(grid,sortedPagos,true);
			
			final JPopupMenu mnu=ComponentUtils.createPopupMenu("Opciones", getEliminarAction());
			ComponentUtils.decorateForPopup(grid, mnu);			
		}
		
		public JXTable getGrid(){
			return this.grid;
		}		
		
		public EventList<PagoM> getSelected(){
			return pagosSeleccionados;
		}
		
		private Action deleteAction;
		
		public Action getEliminarAction(){
			if(deleteAction==null){
				deleteAction=new DispatchingAction(this,"delete");
				CommandUtils.configAction(deleteAction, CXCActions.EliminarPagoM.getId(), "");
			}
			return deleteAction;
		}

		public void valueChanged(ListSelectionEvent e) {
			Matcher<Pago> matcher=new PagoMMatcher(pagosSeleccionados);
			fireChanged(matcher);
		}
		
		public void delete(){
			if(pagosSeleccionados.isEmpty())
				return;
			if(eliminarPago(pagosSeleccionados.get(0)))
				pagosSeleccionados.remove(0);
		}
		
		protected boolean eliminarPago(final PagoM pago){
			return true;
		}
		
		
		
		private class ToPagoM implements ca.odell.glazedlists.FunctionList.Function<Pago, PagoM>{

			public PagoM evaluate(Pago s) {
				return s.getPagoM();
			}			
		}
		
		private class PagoMMatcher implements Matcher<Pago>{
			
			private final EventList<PagoM> seleccion;

			public PagoMMatcher(EventList<PagoM> pagosSelccionados) {				
				this.seleccion = pagosSelccionados;
			}

			public boolean matches(Pago item) {
				if(item==null) return false;
				if(seleccion.isEmpty()) return true;
				return seleccion.contains(item.getPagoM());
			}
			
		}

		
		
	}
	
	

}
