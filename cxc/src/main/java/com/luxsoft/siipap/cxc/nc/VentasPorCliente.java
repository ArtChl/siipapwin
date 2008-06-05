package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.model2.NCPorClienteModel;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.cxc.utils.CheckBoxSelector;
import com.luxsoft.siipap.cxc.utils.MatcherEditors;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.InternalTaskView;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Facturas por cliente. Las acciones que esta vista realiza
 *  son direccionadas al {@link ControladorDeNotas}
 * 
 * @author Ruben Cancino
 *
 */
public class VentasPorCliente implements InternalTaskView{
	
	private ControladorDeNotas controlador;	
	private final NCPorClienteModel model;
	
	private HeaderPanel header;
	private JComponent filterPanel;
	
	
	/** Acciones que esta vista proporciona **/
	
	private Action cambiarPeriodo;	
	private Action generarDescuento;
	private Action generarNotaDeCargo;
	private Action generarNotaPorDevolucion;
	private Action generarNotaPorBonificacion;
	private Action generarNotaPorDesuentoFinanciero;
	private Action verFactura;
	private Action loadAction;
	
	/** Infraesetructura de filtros para GlazedLists **/
	
	private JTextField numeroField=new JTextField(20);
	private JTextField numeroFiscal=new JTextField(20);
	private CheckBoxSelector<Venta> paraDescuentoEditor;
	private CheckBoxSelector<Venta> conSaldoMatcherEditor;
	private CheckBoxSelector<Venta> vencidasMatcherEditor;
	private CheckBoxSelector<Venta> porVencerMatcherEditor;
	private CheckBoxSelector<Venta> pagadasEditor;	
	
	/**GlazedList stuff**/
	
	private EventList<Venta> source;
	private SortedList<Venta> sortedList;
	private EventList<Venta> filterList;
	private EventSelectionModel<Venta> selectionModel;
	private JXTable grid;
	
	
	public VentasPorCliente(final NCPorClienteModel model){
		this.model=model;
		model.addPropertyChangeListener(new SelectionHandler());
	}
	
	
		
	
	
	private void initActions(){
		cambiarPeriodo=new DispatchingAction(this,"cambiarPeriodo");
		CommandUtils.configAction(cambiarPeriodo, "seleccionarPeriodo", "");		
		
		loadAction=CommandUtils.createLoadAction(this, "load");
		
		generarDescuento=new DispatchingAction(this,"generarDescuento");
		CommandUtils.configAction(generarDescuento, CXCActions.GenerarDescuentosPorAnticipado.getId(), "");
		
		
		generarNotaDeCargo=new DispatchingAction(this,"generarNotaDeCargo");
		CommandUtils.configAction(generarNotaDeCargo, "FacturasPorCliente.generarNotaDeCargo", "");
		
		verFactura=new DispatchingAction(this,"verFactura");
		CommandUtils.configAction(verFactura, "FacturasPorCliente.verFactura", "");
		
		generarNotaPorDevolucion=new DispatchingAction(this,"devolucion");
		CommandUtils.configAction(generarNotaPorDevolucion, CXCActions.GenerarNotaPorDevolucion.getId(), "");
		
		generarNotaPorBonificacion=new DispatchingAction(this,"bonificacion");
		CommandUtils.configAction(generarNotaPorBonificacion,CXCActions.GenerarNotaPorBonificacion.getId() , "");
		
		generarNotaPorDesuentoFinanciero=new DispatchingAction(this,"descuentoFinanciero");
		CommandUtils.configAction(generarNotaPorDesuentoFinanciero,CXCActions.GenerarNotaPorDesuentoFinanciero.getId() , "");
		
	}
	
	/**
	 * Inicializa GlazedList para ser usuado en el grid
	 *
	 */
	@SuppressWarnings("unchecked")
	private void initGlazedLists(){
		 
		/** Encadenamiento de listas en base a MatcherEditors especializados  **/
		// Origen o lista base
		//source=new BasicEventList<Venta>();
		source=model.getVentas();
		
		// Creamos los MatcherEditors adecuados
		paraDescuentoEditor=MatcherEditors.createFacturasParaNCDescuento();
		conSaldoMatcherEditor=MatcherEditors.createFacturasPendientesSelector();		
		vencidasMatcherEditor=MatcherEditors.createFacturasVencidasSelector();		
		porVencerMatcherEditor=MatcherEditors.createFacturasPorVencerSelector();		
		pagadasEditor=MatcherEditors.createFacturasPagadasSelector();
		
		final TextFilterator<Venta> numeroFilterator=GlazedLists.textFilterator(new String[]{"numero"});
		final TextComponentMatcherEditor<Venta> numeroEditor=new TextComponentMatcherEditor<Venta>(numeroField,numeroFilterator);
		
		final TextFilterator<Venta> fiscalFilterator=GlazedLists.textFilterator(new String[]{"numeroFiscal"});
		final TextComponentMatcherEditor<Venta> fiscalEditor=new TextComponentMatcherEditor<Venta>(numeroFiscal,fiscalFilterator);
		
		// Aplicamos los editores en cascada con ayuda de CompositeMatcherEditor
		
		final EventList<MatcherEditor<Venta>> editores=new BasicEventList<MatcherEditor<Venta>>();
		editores.add(paraDescuentoEditor);
		editores.add(conSaldoMatcherEditor);
		editores.add(vencidasMatcherEditor);
		editores.add(porVencerMatcherEditor);		
		editores.add(pagadasEditor);
		editores.add(numeroEditor);
		editores.add(fiscalEditor);
		
		final CompositeMatcherEditor<Venta> compositeEditor=new CompositeMatcherEditor<Venta>(editores);
		compositeEditor.setMode(CompositeMatcherEditor.AND);
		//Lista final obtenida
		filterList=new FilterList<Venta>(source,new ThreadedMatcherEditor<Venta>(compositeEditor));		
	}
	
	private void initComponents(){		
		final TableFormat<Venta> tf=CXCTableFormats.getVentasCreTF();
		final Comparator<Venta> comp=GlazedLists.beanPropertyComparator(Venta.class, "atraso");
		sortedList=new SortedList<Venta>(filterList,comp);
		final EventTableModel<Venta> tm=new EventTableModel<Venta>(sortedList,tf);
		selectionModel = new EventSelectionModel<Venta>(sortedList);
		grid=ComponentUtils.getStandardTable();		
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		ComponentUtils.addEnterAction(grid, verFactura);
		grid.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					verFactura();
				}
			}
		});		
		//ComponentUtils.decorateActions(grid);
		new TableComparatorChooser<Venta>(grid,sortedList,true);
		ComponentUtils.decorateActions(grid);
	}

	public JComponent getControl() {
		initActions();
		initGlazedLists();
		initComponents();
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeader(),BorderLayout.NORTH);
		panel.add(new JScrollPane(grid),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildHeader(){		
		header=ComponentUtils.getBigHeader(getClienteHeader(),getDescriptionHeader());
			
		
		return header;
	}
	
	public JTextField getPorNumeroField(){		
		return numeroField;
	}
	
	public JComponent getFiltrosPanel(){
		if(filterPanel==null){
			FormLayout layout=new FormLayout(
					"l:p,2dlu,f:p:g","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			//builder.setDefaultDialogBorder();
			//builder.appendSeparator("Por tipo");
			
			builder.append("Factura",getPorNumeroField(),true);
			builder.append("N.Fiscal",numeroFiscal,true);
			builder.append("Con Saldo",conSaldoMatcherEditor.getBox(),true);
			builder.append("Vencidas ",vencidasMatcherEditor.getBox(),true);
			builder.append("Por vencer",porVencerMatcherEditor.getBox(),true);
			builder.append("Pagadas",pagadasEditor.getBox(),true);
			builder.append("Para Descuento",paraDescuentoEditor.getBox(),true);			
			filterPanel= builder.getPanel();
			filterPanel.setOpaque(false);
			
		}
		return filterPanel;
		
	}

	public Icon getIcon() {
		return CommandUtils.getIconFromResource("images2/bancos16.gif");
	}

	public String getTitle() {
		return "Facturas";
	}

	public void instalOperacionesAction(JXTaskPane operaciones) {
		operaciones.add(cambiarPeriodo);
		operaciones.add(loadAction);		
		operaciones.add(verFactura);
		operaciones.add(generarNotaDeCargo);
		operaciones.add(generarDescuento);
		operaciones.add(generarNotaPorDevolucion);
		operaciones.add(generarNotaPorBonificacion);
		operaciones.add(generarNotaPorDesuentoFinanciero);
	}

	public void instalProcesosActions(JXTaskPane procesos) {
		
	}

	public void installDetallesPanel(JXTaskPane detalle) {
		
		
	}

	public void installFiltrosPanel(JXTaskPane filtros) {
		filtros.add(getFiltrosPanel());
	}
	
	
	public void close() {
		model.close();
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
		String pattern="Lista de facturas generadas periodo:  {0}" ;
		return MessageFormat.format(pattern, model.getPeriodo());
	}
	
	private void updateSelection(){		
		header.setTitle(getClienteHeader());
		header.setDescription(getDescriptionHeader());
	}
	
	private EventList<Venta> getSeleccionadas(){
		return selectionModel.getSelected();
	}
	
	/**** Metodos de acciones ****/
	
	public void cambiarPeriodo(){
		AbstractDialog dialog=Binder.createPeriodoSelector(model.getModel("periodo"));
		dialog.open();
		if(!dialog.hasBeenCanceled())
			load();
		
	}
	
	/**
	 * Metodo para genera notas de credito por descuento anticipado para las ventas seleccionadas
	 * 
	 * Si el cliente no califica para este tipo de notas, el dialogo resultante estara vacio 
	 *
	 */
	public void generarDescuento(){
		if(!getSeleccionadas().isEmpty()){
			if(model.getCliente().getCredito().isNotaAnticipada()){
				final List<Venta> ventas=new ArrayList<Venta>();
				ventas.addAll(getSeleccionadas());
				List<NotaDeCredito> notas=controlador.generarNotasDeDescuentoPorAnticipado(model.getCliente(), ventas,new Date());
				
				if(notas!=null && (!notas.isEmpty()) && MessageUtils.showConfirmationMessage("Desea imprimir las notas generadad", "Notas de Crédito")){
					ImpresionDeNotas.imprimir(notas);
				}
				for(Venta v:ventas){				
					System.out.println("Refrescando la venta: "+v.getId());
					model.refrescar(v);
					int index=sortedList.indexOf(v);
					sortedList.set(index, v);
				}
			}else{
				MessageUtils.showMessage("Este cliente no esta configurado para generar notas de crédito por anticipado"
						, "Notas de crédito anticipadas");
			}
			
		}else{
			MessageUtils.showMessage("Requiere seleccionar una o mas facturas", "Notas de crédito anticipadas");
		}
	}
	
	public void generarNotaDeCargo(){
		if(!getSeleccionadas().isEmpty()){
			List<NotaDeCredito> notas=controlador.generarNotasDeCargo(model.getCliente(), getSeleccionadas());
			if(notas!=null ){
				MessageUtils.showMessage("Prepare su impresora ", "Impresión de Notas");
				ImpresionDeNotas.imprimir(notas);
				
			}
		}else{
			MessageUtils.showMessage("Debe seleccionar un a o mas facturas para aplicar el cargo", "Cargos por cliente");
		}
	}
	
	/**
	 * Dispara el proceso de Generacion de Nota de credito por devolucion
	 *
	 */
	public void devolucion(){
		final List<NotaDeCredito> notas=controlador.aplicarNotaPorDevolucion(model.getCliente());
		if(notas!=null && !notas.isEmpty()){
			for(NotaDeCredito n:notas){
				model.getNotasSource().addAll(n.getPartidas());
			}
		}	
	}
	
	/**
	 * Genera notas por bonificacion
	 * 
	 *
	 */
	public void bonificacion(){
		final List<Venta> ventas=new ArrayList<Venta>();
		ventas.addAll(getSeleccionadas());
		final List<NotaDeCredito> notas=controlador.aplicarNotaPorBonificacion(model.getCliente(), ventas);
		if(notas!=null && !notas.isEmpty()){
			refrescarVentas(ventas);
		}	
	}
	
	/**
	 * Genera notas popr descuento financiero 
	 *
	 */
	public void descuentoFinanciero(){
		final List<Venta> ventas=new ArrayList<Venta>();
		ventas.addAll(getSeleccionadas());
		final List<NotaDeCredito> notas=controlador.aplicarNotaPorDescuentoFinanciero(model.getCliente(), ventas);
		if(notas!=null && !notas.isEmpty()){
			refrescarVentas(ventas);
		}	
	}
	
	/**
	 * Refresca un grupo de ventas
	 * 
	 * @param ventas
	 */
	private void refrescarVentas(final List<Venta> ventas){
		for(Venta v:ventas){
			this.model.refrescar(v);
			int index=sortedList.indexOf(v);
			sortedList.set(index, v);
		}
	}
	
	/**
	 * Muestra el detalle de una factura
	 * 
	 *
	 */
	public void verFactura(){
		if(!getSeleccionadas().isEmpty()){
			controlador.mostrarVenta(getSeleccionadas().get(0));
		}
	}
	
	public void load(){
		LoadWorker worker=new LoadWorker(source);
		TaskUtils.executeSwingWorker(worker);
	}
	
	public ControladorDeNotas getControlador() {
		return controlador;
	}

	public void setControlador(ControladorDeNotas controlador) {
		this.controlador = controlador;
	}
	
	public NCPorClienteModel getModel() {
		return model;
	}
	
	/****** Fin metodos de acciones *************/
	
	private class SelectionHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			updateSelection();
		}		
	}
	
	/**
	 * Worker para cargar datos en un EventList
	 * 
	 * @author Ruben Cancino
	 *
	 */
	@SuppressWarnings("unchecked")
	private class LoadWorker extends SwingWorker<List<Venta>, String>{
		
		
		private WeakReference<EventList> source;
		
		public LoadWorker(final EventList source){
			this.source=new WeakReference<EventList>(source);
		}
		
		protected List<Venta> doInBackground() throws Exception {			
			return model.getBuscarVentas();
		}
		
		@SuppressWarnings("unchecked")
		protected void done() {
			try {
				if(source.get()!=null){
					source.get().clear();
					source.get().addAll(get());
					grid.packAll();
				}				
			} catch (Exception e) {
				MessageUtils.showError("Error cargando facturas para el cliente: "+model.getCliente(),e);
			}
		}			
	}
}
