package com.luxsoft.siipap.cxc.revision;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.Matchers;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.catalogos.ComentarioDeRevisionForm;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.cxc.utils.FacturasPorFechaSelector;
import com.luxsoft.siipap.cxc.utils.MatcherEditors;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Consulta para el mantenimiento  
 * 
 * @author Ruben Cancino
 *
 */
public class RecibidasCxCView extends AbstractInternalTaskView{
	
	private Periodo periodo;	
	private Action cambiarPeriodo;
	private Action marcarRecibidas;
	private Action marcarRevisada;
	private Action desmarcarRecibidas;
	private Action seleccionarCliente;
	private Action loadAction;
	private Action salvar;
	private Action reprogramarVentas;
	public Action reprogramarManual;
	
	private JXTable grid;
	private FacturasPorFechaSelector facturasPorFecha;
	private JTextField numeroField=new JTextField();
	private JTextField sucursalField=new JTextField();
	private JTextField clienteField=new JTextField();
	private EventList<Venta> source;	
	private SortedList<Venta> sortedSorce;
	private EventSelectionModel<Venta> selectionModel;
	
	private HeaderPanel header;
	
	
	public RecibidasCxCView(Periodo periodo) {
		super();
		this.periodo=periodo;
		init();
	}

	public RecibidasCxCView() {		
		Calendar c=Calendar.getInstance();
		c.add(Calendar.DATE, -1);		
		periodo=new Periodo(c.getTime());
		init();
	}
	
	private void init(){
		cambiarPeriodo=new DispatchingAction(this,"cambiarPeriodo");
		CommandUtils.configAction(cambiarPeriodo, "seleccionarPeriodo", "");
		
		marcarRecibidas=new DispatchingAction(this,"marcar");
		CommandUtils.configAction(marcarRecibidas,"marcarRecibidas" ,null);
		marcarRecibidas.setEnabled(false);
		
		marcarRevisada=new DispatchingAction(this,"marcarRevisada");
		CommandUtils.configAction(marcarRevisada, "marcarRevisadas", null);
		marcarRevisada.setEnabled(false);
		
		
		desmarcarRecibidas=new DispatchingAction(this,"desmarcar");
		CommandUtils.configAction(desmarcarRecibidas, "desmarcarSeleccion", "images/bullets/envvar_obj.gif");
		loadAction=CommandUtils.createLoadAction(this, "load");
		
		salvar=new DispatchingAction(this,"salvar");
		CommandUtils.configAction(salvar, "salvarSeleccion", "images2/SAVE.PNG");
		
		seleccionarCliente=new DispatchingAction(this,"seleccionarCliente");
		CommandUtils.configAction(seleccionarCliente, "seleccionarCliente", "");
		
		reprogramarVentas=new DispatchingAction(this,"reprogramarVentas");
		CommandUtils.configAction(reprogramarVentas, CXCActions.ReprogramarVentas.getId(), "");
		
		reprogramarManual=new DispatchingAction(this,"reprogramarManual");
		CommandUtils.configAction(reprogramarManual,CXCActions.ReprogramarVentasManual.getId(), "");
	}

	public JComponent getControl() {
		
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeader(),BorderLayout.NORTH);
		panel.add(buildGrid(),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildHeader(){
		header=ComponentUtils.getBigHeader(getDescriptionHeader(),"");		
		return header;
		
	}
	
	private RecibidaEstatusSelector recibidaSelector=new RecibidaEstatusSelector();
	
	private JComponent buildGrid(){
		source=GlazedLists.threadSafeList(new BasicEventList<Venta>());
		
		facturasPorFecha=MatcherEditors.createFacturasPorFechaSelector("fecha");
		
		final TextFilterator<Venta> clienteFilterator=GlazedLists.textFilterator(new String[]{"clave","nombre"});
		final TextComponentMatcherEditor<Venta> clienteEditor=new TextComponentMatcherEditor<Venta>(clienteField,clienteFilterator);
		
		final TextFilterator<Venta> sucfilterator=GlazedLists.textFilterator(new String[]{"sucursal"});
		TextComponentMatcherEditor<Venta> sucursalEditor=new TextComponentMatcherEditor<Venta>(sucursalField,sucfilterator);
		
		final TextFilterator<Venta> filterator=GlazedLists.textFilterator(new String[]{"numero"});
		TextComponentMatcherEditor<Venta> numeroEditor=new TextComponentMatcherEditor<Venta>(numeroField,filterator);
		
		
		
		final EventList<MatcherEditor<Venta>> editors=new BasicEventList<MatcherEditor<Venta>>();
		editors.add(clienteEditor);
		editors.add(facturasPorFecha);		
		editors.add(sucursalEditor);
		editors.add(numeroEditor);
		editors.add(recibidaSelector);
		final CompositeMatcherEditor<Venta> editor=new CompositeMatcherEditor<Venta>(editors);
		final FilterList<Venta> filterSource=new FilterList<Venta>(source,editor);
		Comparator<Venta> c1=GlazedLists.beanPropertyComparator(Venta.class, "sucursal");
		Comparator<Venta> c2=GlazedLists.beanPropertyComparator(Venta.class, "numero");
		final EventList<Comparator<Venta>> comparators=new BasicEventList<Comparator<Venta>>();
		comparators.add(c1);comparators.add(c2);
		sortedSorce=new SortedList<Venta>(filterSource,GlazedLists.chainComparators(comparators));
		selectionModel=new EventSelectionModel<Venta>(sortedSorce);
		selectionModel.addListSelectionListener(new SelectionHandler());
		final EventTableModel<Venta> tm=new EventTableModel<Venta>(sortedSorce,CXCTableFormats.getParaRevisionTF());
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
					editar();
			}			
		});
		ComponentUtils.decorateActions(grid);
		new TableComparatorChooser<Venta>(grid,sortedSorce,true);
		return UIFactory.createTablePanel(grid);
	}

	@Override
	public String getTitle() {
		return "Recibidas";
	}
	
	private JPanel filterPanel;
	
	
	public JComponent getFiltrosPanel(){
		if(filterPanel==null){
			FormLayout layout=new FormLayout(
					"l:p,2dlu,f:max(p;100dlu):g","");
			final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Cliente",clienteField,true);
			builder.append("Factura",numeroField,true);
			builder.append("Sucursal",sucursalField,true);
			builder.append("Estado",recibidaSelector.getSelector(),true);
			//builder.append("Dia",facturasPorFecha.getControl(),true);
			filterPanel=builder.getPanel();
			filterPanel.setOpaque(false);
		}
		return filterPanel;
	}
	
	
	private void updateSelection(){			
		header.setTitle(getDescriptionHeader());
		header.setDescription("");
	}
	
	private String getDescriptionHeader(){
		String pattern="Lista de facturas periodo:  {0}" ;
		return MessageFormat.format(pattern, periodo);
	}
	
	public Action getCambiarPeriodo() {
		return cambiarPeriodo;
	}
/*
	public Action getDesmarcarRecibidas() {
		return desmarcarRecibidas;
	}
*/
	public Action getMarcarRecibidas() {
		return marcarRecibidas;
	}
	public Action getMarcarRevizada(){
		return marcarRevisada;
	}
	
	public Action getLoadAction(){
		return loadAction;
	}
	public Action getSalvarAction(){
		return salvar;
	}
	public Action getReprogramarVentas(){
		return reprogramarVentas;
	}

	/**
	 * Permite editar un grupo de ventas
	 *
	 */
	private void editar(){		
		if(!selectionModel.getSelected().isEmpty()){
			List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(selectionModel.getSelected());
			for(Venta v:ventas){
				if(v.getCredito()!=null){
					ComentarioDeRevisionForm form=new ComentarioDeRevisionForm(v.getCredito());		
					form.open();		
					if(!form.hasBeenCanceled()){						
						ServiceLocator.getVentasManager().actualizarVenta(v);
						int index=source.indexOf(v);
						source.set(index, v);
					}
				}
			}
			
		}
	}
	
	public void marcar(){
		marcarRecepcion(true);
		analizarRevisadas();
	}
	public void marcarRevisada(){
		marcarRevicion(true);
	}
	
	public void desmarcar(){
		marcarRecepcion(false);
	}
	
	public void marcarRecepcion(final boolean val){		
		if(!selectionModel.getSelected().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(selectionModel.getSelected());
			for(Venta v:ventas){
				if(v.getCredito()==null)
					getManager().actualizarVenta(v);
				if (v.getCredito() != null) {
					v.getCredito().setRecibidaCXC(val);
					int index=source.indexOf(v);
					source.set(index, v);
				}				
			}
		}
	}
	
	public void marcarRevicion(final boolean val){		
		if(!selectionModel.getSelected().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(selectionModel.getSelected());
			for(Venta v:ventas){
				if(v.getCredito()!=null || v.getCredito().isRecibidaCXC()){
					if (v.getCredito() != null) {
						v.getCredito().setRevisada(val);
						int index=source.indexOf(v);
						source.set(index, v);
					}
				}
								
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	public void salvar(){
		
		if(!selectionModel.getSelected().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(selectionModel.getSelected());
			for(Venta v:ventas){
				/*
				if(v.getCredito()!=null){					
					v.actualizarDatosDeCredito();
				}*/
				if(v.getCredito()==null)
					getManager().actualizarVenta(v);
				final Date fecha = v.getCredito().isRecibidaCXC() ? new Date() : null;
				if(v.getCredito().getFechaRecepcionCXC()==null)
					v.getCredito().setFechaRecepcionCXC(fecha);
				if(!v.getCredito().isRecibidaCXC()){
					v.getCredito().setRevisada(false);
				}
				getManager().actualizarVenta(v);					
				System.out.println("Venta salvada: "+v.getId());				
				int index=source.indexOf(v);
				source.set(index, v);								
			}
			
		}
		
		/*
		final SwingWorker worker=new SwingWorker(){			
			protected Object doInBackground() throws Exception {
				for(int index=0;index<sortedSorce.size();index++){
					Venta v=sortedSorce.get(index);
					if(v.getCredito()!=null){						
						final Date fecha = v.getCredito().isRecibidaCXC() ? new Date() : null;
						v.getCredito().setFechaRecepcionCXC(fecha);
						if(!v.getCredito().isRecibidaCXC()){
							v.getCredito().setRevisada(false);
						}
						getManager().actualizarVenta(v);
					}					
				}
				return "OK";
			}			
			protected void done() {
				for(int index=0;index<sortedSorce.size();index++){
					Venta v=sortedSorce.get(index);					
					sortedSorce.set(index, v);
				}
			}
		};		
		TaskUtils.executeSwingWorker(worker);
		*/		
	}
	
	public void cambiarPeriodo(){
		final ValueHolder vm =new ValueHolder();		
		AbstractDialog dialog=Binder.createPeriodoSelector(vm);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			periodo=(Periodo)vm.getValue();
			updateSelection();
			load();
		}
		
	}
	
	public Date seleccionarFecha(){
		final ValueHolder vm =new ValueHolder(new Date());		
		AbstractDialog dialog=Binder.createDateSelector(vm);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			return (Date)vm.getValue();
		}
		return null;
		
	}
	
	
	public VentasManager getManager(){
		return ServiceLocator.getVentasManager();
	}
	
	public void reprogramarManual(){
		final Date fecha=seleccionarFecha();
		if(fecha==null) return;
		
		if(!selectionModel.getSelected().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(selectionModel.getSelected());
			
			for(Venta v:ventas){				
				if(v.getCredito()==null)
					getManager().actualizarVenta(v);
				if(fecha.before(v.getFecha())){
					//MessageUtils.showMessage("La fecha es anteriro a la fecha de la factura {0}", "Revisión");
					continue;
				}
				if(v.getCredito().getFechaRecepcionCXC()==null)
					v.getCredito().setFechaRecepcionCXC(fecha);				
				if(!v.getCredito().isRevisada()){
					v.getCredito().setFechaRevisionCxc(fecha);
					v.getCredito().actualizar(fecha);
					getManager().getVentasDao().salvar(v);
				}				
												
			}
			
			//Actualizar glazedlist
			for(Venta v:ventas){
				int index=source.indexOf(v);
				source.set(index, v);
			}
		}
	}
	
	public void reprogramarVentas(){
		if(MessageUtils.showConfirmationMessage("Permite recalcular las fechas de pago a partir del proximo día habil\n (Lunes a Sábado)", "")){
			SwingWorker<String, Venta> worker=new SwingWorker<String, Venta>(){
				@Override
				protected String doInBackground() throws Exception {
					List<Long> ids=getManager().getListaDeVentasACreditoConSaldo();						
					int total=ids.size();
					for(int index=0;index<total;index++){						
						Venta venta=getManager().getVentasDao().buscarPorId(ids.get(index));
						getManager().actualizarVenta(venta);
						publish(venta);
						setProgress(100 * index / total);
					}
					return "OK";
				}
				
			};
			ProcessWatcher watcher=new ProcessWatcher("Reprogramando..");
			worker.addPropertyChangeListener(watcher);
			worker.execute();
		}
	}
	
	public void load(){		
		final SwingWorker<List<Venta>, String> worker=new SwingWorker<List<Venta>, String>(){

			@Override
			protected List<Venta> doInBackground() throws Exception {
				System.out.println("Cargando ventas");
				return ServiceLocator.getVentasManager().buscarCuentasPorCobrarCre(periodo);
			}

			@Override
			protected void done() {
				try {
					final List<Venta> ventas=get();
					System.out.println("Ventas : "+ventas.size());
					source.clear();
					source.addAll(ventas);
					grid.packAll();
				} catch (Exception e) {
					MessageUtils.showError(e.getMessage(),e);
				}
			}
			
			
		};
		TaskUtils.executeSwingWorker(worker);
		
	}
	
	public void close(){
		source.clear();
	}

	private class ProcessWatcher extends SXAbstractDialog implements PropertyChangeListener{
		
		protected JProgressBar progress;
		
		
		
		public ProcessWatcher(String title){
			super(title);
		}

		@Override
		protected JComponent buildContent() {
			progress=new JProgressBar(1,100);
			progress.setStringPainted(true);
			return progress;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("state") && SwingWorker.StateValue.DONE == evt.getNewValue()){
				progress.setValue(0);
				dispose();
			}else if(evt.getPropertyName().equals("progress")){							
				progress.setValue((Integer)evt.getNewValue());
			}else if(SwingWorker.StateValue.STARTED.equals(evt.getNewValue())){
				open();
			}
		}
		
	}
	
	private class RecibidaEstatusSelector extends AbstractMatcherEditor<Venta> implements ActionListener{
		private JComboBox selector;
		
		public RecibidaEstatusSelector(){
			Integer[] vals={-1,0,1};
			selector=new JComboBox(vals);
			selector.addActionListener(this);
			selector.setRenderer(new RecRenderer());
		}
		
		public JComboBox getSelector(){
			return selector;
		}

		public void actionPerformed(ActionEvent e) {
			Integer i=(Integer)selector.getSelectedItem();
			switch (i) {
			case 0:
				fireChanged(Matchers.beanPropertyMatcher(Venta.class, "credito.recibidaCXC", Boolean.FALSE));
				break;
			case 1:
				fireChanged(Matchers.beanPropertyMatcher(Venta.class, "credito.recibidaCXC", Boolean.TRUE));
				break;
			case -1:
				fireMatchAll();
			default:
				break;
			}
			
		}
		
		private class RecRenderer extends DefaultListCellRenderer{

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				
				Component c =super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
				Integer r=(Integer)value;
				switch (r) {
				case 0:
					setText("Por recibir");
					break;
				case 1:
					setText("Recibida");
					break;
				case -1:
					setText("Todas");
					break;
				default:
					break;
				}
				return c;
			}
			
		}
		
		
	}
	
	private void analizarRecibidas(){
		boolean val=false;
		if(!selectionModel.isSelectionEmpty()){			
			for(Venta v:selectionModel.getSelected()){
				if(v.getCredito()!=null){
					if(!v.getCredito().isRecibidaCXC()){
						val=true;
						break;
					}
				}
			}
		}
		getMarcarRecibidas().setEnabled(val);
	}
	
	private void analizarRevisadas(){
		boolean val=false;
		if(!selectionModel.isSelectionEmpty()){			
			for(Venta v:selectionModel.getSelected()){
				if(v.getCredito()!=null){
					if(v.getCredito().isRecibidaCXC()){
						if(!v.getCredito().isRevisada()){
							val=true;
							break;
						}	
					}				
				}
			}
		}
		getMarcarRevizada().setEnabled(val);
	}
	
	private class SelectionHandler implements ListSelectionListener{

		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()){
				analizarRecibidas();
				analizarRevisadas();
			}
			
		}
		
	}
	

}
