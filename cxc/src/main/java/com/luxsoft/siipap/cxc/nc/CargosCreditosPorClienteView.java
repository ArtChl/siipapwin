package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.model.PagosFactoryImpl;
import com.luxsoft.siipap.cxc.model2.NCPorClienteModel;
import com.luxsoft.siipap.cxc.model2.PagoDeCargosModel;
import com.luxsoft.siipap.cxc.pagos.PagoDeCargosForm;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.cxc.utils.CheckBoxSelector;
import com.luxsoft.siipap.cxc.utils.MatcherEditors;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;

/**
 * Vista para el mantenimiento de Notas de Cargo por cliente
 * 
 * @author Ruben Cancino
 *
 */
public class CargosCreditosPorClienteView extends AbstractInternalTaskView{
	
	private final NCPorClienteModel model;
	private final ControladorDeNotas controlador;
	
	private FilterList<NotaDeCredito> notasFiltradas;
	private EventSelectionModel<NotaDeCredito> selectionModel;
	private EventList<NotasDeCreditoDet> partidas;
	private JXTable grid;
	private JXTable childGrid;
	
	private Action cambiarPeriodo;
	private Action verNota;
	private Action eliminarNota;
	private Action cancelarNota;	
	private Action loadAction;
	
	
	private HeaderPanel header;
	private JTextField numero=new JTextField();
	private JTextField numeroFiscal=new JTextField();
	
	private CheckBoxSelector<NotaDeCredito> cargosEditor;
	private CheckBoxSelector<NotaDeCredito> conSaldoEditor;
	private CheckBoxSelector<NotaDeCredito> sinSaldoEditor;
	
	private CargosHelper helper;

	
	public CargosCreditosPorClienteView(final NCPorClienteModel model,final ControladorDeNotas controlador){
		this.model=model;
		this.controlador=controlador;
		model.addPropertyChangeListener(new SelectionHandler());
	}
	
	private void initActions(){
		cambiarPeriodo=new DispatchingAction(this,"cambiarPeriodo");
		CommandUtils.configAction(cambiarPeriodo, "seleccionarPeriodo", "");
		loadAction=CommandUtils.createLoadAction(this, "load");
		
		verNota=new DispatchingAction(this,"verNota");
		CommandUtils.configAction(verNota, "NotasPorCliente.verNota", "");
		
		eliminarNota=new DispatchingAction(this,"eliminarNota");
		CommandUtils.configAction(eliminarNota,"NotasPorCliente.eliminarNota","");
		
		cancelarNota=new DispatchingAction(this,"cancelarNota");
		CommandUtils.configAction(cancelarNota,CXCActions.CancelarNotas.getId(),"");
		
		
		
		helper=new CargosHelper();
	}

	public JComponent getControl() {
		initActions();
		final JPanel panel=new JPanel(new BorderLayout());
		
		final JSplitPane sp=new JSplitPane(
				JSplitPane.VERTICAL_SPLIT
				,buildNotasGrid()
				,buildPartidasGrid());
		sp.setDividerLocation(.4);
		sp.setResizeWeight(.4);
		panel.add(sp,BorderLayout.CENTER);
		panel.add(buildHeader(),BorderLayout.NORTH);
		return panel;
	}
	
	private JComponent buildHeader(){		
		header=ComponentUtils.getBigHeader(getClienteHeader(),getDescriptionHeader());
		return header;
	}
	
	private JComponent  buildNotasGrid(){		
		initGlazedLists();
		grid=ComponentUtils.getStandardTable();
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addEnterAction(grid, new AbstractAction("enter"){
			public void actionPerformed(ActionEvent e) {
				verNota();
			}
		});
		grid.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) verNota();
			}
		});
		
		final TableFormat<NotaDeCredito> tf=CXCTableFormats.getNotaDeCreditoTF();
		final Comparator<NotaDeCredito> c1=GlazedLists.beanPropertyComparator(NotaDeCredito.class, "id");
		final SortedList<NotaDeCredito> sortedNotas=new SortedList<NotaDeCredito>(notasFiltradas,GlazedLists.reverseComparator(c1));
		final EventTableModel<NotaDeCredito> tm=new EventTableModel<NotaDeCredito>(sortedNotas,tf);
		selectionModel=new EventSelectionModel<NotaDeCredito>(sortedNotas);
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser<NotaDeCredito>(grid,sortedNotas,true);		
		return new JScrollPane(grid);
	}
	
	private void initGlazedLists(){		
				
		//Filtrar por numero
		final TextFilterator<NotaDeCredito> numeroFilterator=GlazedLists.textFilterator(new String[]{"numero"});
		final MatcherEditor<NotaDeCredito> editor1=new TextComponentMatcherEditor<NotaDeCredito>(numero,numeroFilterator);
		
		//Filtrar por numero fiscal
		final TextFilterator<NotaDeCredito> numeroFiscalFilterator=GlazedLists.textFilterator(new String[]{"numeroFiscal"});
		final MatcherEditor<NotaDeCredito> editor2=new TextComponentMatcherEditor<NotaDeCredito>(numeroFiscal,numeroFiscalFilterator);
		
		//Filtrar por tipo
		//final TextFilterator<NotaDeCredito> tipoFilterator=GlazedLists.textFilterator(new String[]{"tipo"});
		//final MatcherEditor<NotaDeCredito> editor3=new TextComponentMatcherEditor<NotaDeCredito>(tipo,tipoFilterator);
		
		cargosEditor=MatcherEditors.createSelectorDeCargos();
		conSaldoEditor=MatcherEditors.createSelectorNotasConSaldo();
		sinSaldoEditor=MatcherEditors.createSelectorNotasSinSaldo();
		
		final Matcher<NotaDeCredito> noCargosPorChequeDevuelto=new Matcher<NotaDeCredito>(){
			public boolean matches(NotaDeCredito item) {				
				return !StringUtils.isBlank(item.getTipo());
			}			
		};
		
		final Matcher<NotaDeCredito> noJuridico=new Matcher<NotaDeCredito>(){
			public boolean matches(NotaDeCredito item) {				
				return !item.getOrigen().equals("JUR");
			}			
		};
		
		final EventList<MatcherEditor<NotaDeCredito>> editors=new BasicEventList<MatcherEditor<NotaDeCredito>>();		
		editors.add(editor1);
		editors.add(editor2);
		editors.add(cargosEditor);
		editors.add(conSaldoEditor);
		editors.add(sinSaldoEditor);
		editors.add(GlazedLists.fixedMatcherEditor(noCargosPorChequeDevuelto));
		editors.add(GlazedLists.fixedMatcherEditor(noJuridico));
		
		
		final CompositeMatcherEditor<NotaDeCredito> mainEditor=new CompositeMatcherEditor<NotaDeCredito>(editors);
		notasFiltradas=new FilterList<NotaDeCredito>(model.getNotas(),new ThreadedMatcherEditor<NotaDeCredito>(mainEditor));		
	}
	
	private JComponent buildPartidasGrid(){
		childGrid=ComponentUtils.getStandardTable();
		/**
		partidas=new CollectionList<NotaDeCredito, NotasDeCreditoDet>(selectionModel.getSelected()
				,new CollectionList.Model<NotaDeCredito, NotasDeCreditoDet>(){
					public List<NotasDeCreditoDet> getChildren(NotaDeCredito parent) {
						return parent.getPartidas();
					}			
		});			
		**/
		partidas=model.getNotasSource();
		final NotasToNotasDet matcher=new NotasToNotasDet(selectionModel.getSelected());
		selectionModel.addListSelectionListener(matcher);
		partidas=new FilterList<NotasDeCreditoDet>(partidas,matcher);	
		
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(partidas,CXCTableFormats.getNotaDeCreditoDetTF());
		childGrid.setModel(tm);
		return new JScrollPane(childGrid);
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
		String pattern="Lista de Notas de crédito/cargo generadas periodo:  {0}" ;
		return MessageFormat.format(pattern, model.getPeriodo());
	}
	
	private void updateSelection(){		
		header.setTitle(getClienteHeader());
		header.setDescription(getDescriptionHeader());
	}
	
	public Icon getIcon() {
		return CommandUtils.getIconFromResource("images2/application_side_contract.png");
	}

	public String getTitle() {
		return "Notas";
	}

	public void instalOperacionesAction(JXTaskPane operaciones) {
		operaciones.add(cambiarPeriodo);
		operaciones.add(eliminarNota);
		operaciones.add(cancelarNota);		
		operaciones.add(loadAction);		
		operaciones.add(verNota);
		helper.installActions(operaciones);
	} 
	
	@Override
	public void installFiltrosPanel(JXTaskPane filtros) {
		filtros.add(getFiltrosPanel());
	}

	private EventList<NotaDeCredito> getSeleccionadas(){
		return selectionModel.getSelected();
	}
	
	private JComponent filtrosPanel;
	
	private JComponent getFiltrosPanel(){
		if(filtrosPanel==null){
			DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:40dlu,2dlu,max(p;50dlu):g",""));
			builder.append("Numero",numero,true);
			builder.append("Fiscal",numeroFiscal,true);
			builder.append("Cargos",cargosEditor.getBox(),true);
			builder.append("Con Saldo",conSaldoEditor.getBox(),true);
			builder.append("Sin Saldo",sinSaldoEditor.getBox(),true);
			builder.getPanel().setOpaque(false);
			filtrosPanel= builder.getPanel();
		}		
		return filtrosPanel;
	}
	
	/**** Metodos de acciones ****/
	
	public void cambiarPeriodo(){
		AbstractDialog dialog=Binder.createPeriodoSelector(model.getModel("periodo"));
		dialog.open();
		if(!dialog.hasBeenCanceled())
			load();
		
	}
	
	public void load(){
		LoadWorker worker=new LoadWorker();
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void verNota(){
		if(!getSeleccionadas().isEmpty()){
			controlador.mostrarNota(getSeleccionadas().get(0));
		}
	}
	
	public void eliminarNota(){
		if(!getSeleccionadas().isEmpty()){
			final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
			notas.addAll(getSeleccionadas());
			String msg=MessageFormat.format("Ha seleccionado {0} notas de credito/cargo para eliminar, continuar con la eliminación?", notas.size());
			if(MessageUtils.showConfirmationMessage(msg, "Eliminación de Notas")){
				for(NotaDeCredito n:notas){
					controlador.eliminarNota(n);
					this.notasFiltradas.remove(n);	
				}
			}
			
		}
	}
	
	public void cancelarNota(){
		if(!getSeleccionadas().isEmpty()){
			final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
			notas.addAll(getSeleccionadas());
			String msg=MessageFormat.format("Ha seleccionado {0} notas de credito/cargo para cancelar, es correcto ?", notas.size());
			if(MessageUtils.showConfirmationMessage(msg, "Cancelación de Notas")){
				for(NotaDeCredito n:notas){
					if(n.getSerie().equals("M")){
						controlador.cancelarNotaDeCargo(n);
						load();				
					}						
				}
			}
			
		}
	}
	
	
	
	public void close() {
		model.getNotasSource().clear();
	}
	
	/****** Fin metodos de acciones *************/
	
	
	/**
	 * Worker para cargar datos en un EventList
	 * 
	 * @author Ruben Cancino
	 *
	 */
	@SuppressWarnings("unchecked")
	private class LoadWorker extends SwingWorker{
		protected Object doInBackground() throws Exception {
			model.loadNotas();
			return "OK";
		}		
		@SuppressWarnings("unchecked")
		protected void done() {
			grid.packAll();
		}			
	}
	
	/**
	 * Handler para controlar la seleccion de cliente
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class SelectionHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			updateSelection();
		}		
	}
	
	/**
	 * Matcher para filtrar las {@link NotasDeCreditoDet} en funcion de una seleccion de {@link NotaDeCredito}
	 * 
	 * @author Ruben Cancino
	 *
	 */
	public class NotasToNotasDet extends AbstractMatcherEditor<NotasDeCreditoDet> implements ListSelectionListener{
		
		private final EventList<NotaDeCredito> selection;
		
		public NotasToNotasDet(final EventList<NotaDeCredito> selected){
			this.selection=selected;
		}

		public void valueChanged(ListSelectionEvent e) {
			if(selection.isEmpty()) fireMatchNone();
			fireChanged(new ToNota());
			
		}
		
		private class ToNota implements Matcher<NotasDeCreditoDet>{

			public boolean matches(NotasDeCreditoDet item) {
				return selection.contains(item.getNota());
			}
			
		}
		
	}
	
	/**
	 * Clase especial para los procesos relacionados con el pago  de cargos
	 * 
	 * @author Ruben Cancino
	 *
	 */
	public class CargosHelper{
		
		private Action pagoNormal;
		private Action pagoConNota;
		private Action pagoAutomatico;
		private Action pagoConDisponible;
		private Action traspasoJuridico;
		
		final PagosFactoryImpl fac;
		
		public CargosHelper(){
			init();
			fac=new PagosFactoryImpl();
		}
		
		private void init(){
			pagoNormal=new DispatchingAction(this,"pagoNormal");
			CommandUtils.configAction(pagoNormal, CXCActions.PagarCargoNoraml.getId(), "");
			
			pagoConNota=new DispatchingAction(this,"pagoConNota");
			CommandUtils.configAction(pagoConNota, CXCActions.PagarCargoConNota.getId(), "");
			
			pagoConDisponible=new DispatchingAction(this,"pagoConDisponible");
			CommandUtils.configAction(pagoConDisponible, CXCActions.PagarCargoConDisponible.getId(), "");
			
			pagoAutomatico=new DispatchingAction(this,"pagoAutomatico");
			CommandUtils.configAction(pagoAutomatico,CXCActions.PagoAutomaticoDeCargos.getId(), "");
		}
		
		public void installActions(final JXTaskPane tasks){				
			tasks.add(pagoNormal);
			tasks.add(pagoConNota);
			tasks.add(pagoConDisponible);
			tasks.add(pagoAutomatico);
			
			traspasoJuridico=new DispatchingAction(this,"traspasoJuridico");
			CommandUtils.configAction(traspasoJuridico, "FacturasPorCliente.traspasoJuridico", "");
			tasks.add(traspasoJuridico);
		}
		
		public void pagoNormal(){
			final List<NotaDeCredito> cargos=getCargos();
			if(cargos!=null){
				final PagoM pago=fac.crearPago(model.getCliente(), cargos);				
				final PagoDeCargosModel model=new PagoDeCargosModel(pago);
				final PagoDeCargosForm form=new PagoDeCargosForm(model);
				form.open();
				if(!form.hasBeenCanceled()){
					ServiceLocator.getPagosManager().salvarGrupoDePagos(pago);
					refrecar(cargos);
				}
			}
		}
		
		public void pagoConNota(){
			final List<NotaDeCredito> cargos=getCargos();
			if(cargos!=null){
				PagoConNota pago=controlador.aplicarPagoConNota(model.getCliente(), cargos);
				if(pago!=null)
					refrecar(cargos);
			}
			
		}
		
		public void pagoConDisponible(){
			final List<NotaDeCredito> cargos=getCargos();
			if(cargos!=null){
				SwingWorker<List<PagoM>, String> worker=new SwingWorker<List<PagoM>, String>(){

					@Override
					protected List<PagoM> doInBackground() throws Exception {
						return controlador.buscarDisponibles(model.getCliente());
					}
					@Override
					protected void done() {
						try {
							final List<PagoM> disponibles=get();
							PagoConOtros pago=controlador.aplicarPagoConOtros(model.getCliente(), cargos,disponibles);
							if(pago!=null){
								load();
								/**
								for(NotaDeCredito c:cargos){
									controlador.refrescar(c);
									int index=model.getNotas().indexOf(c);
									
								}
								**/
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					
				};
				TaskUtils.executeSwingWorker(worker);				
			}
			
		}
		
		private List<NotaDeCredito> getCargos(){
			if(!getSeleccionadas().isEmpty() && hayNotasDeCargo(getSeleccionadas())){
				final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
				notas.addAll(getSeleccionadas());
				return notas;
			}else{
				MessageUtils.showMessage("No se han seleccionado Notas de cargo", "Pago de Cargos");
				return null;
			}
		}
		
		private boolean hayNotasDeCargo(final EventList<NotaDeCredito> notas){
			for(NotaDeCredito n:notas){
				if(n.getSerie().equals("M"))
					return true;
			}
			return false;
		}
		
		private void refrecar(final List<NotaDeCredito> notas){
			for(NotaDeCredito n:notas){
				controlador.refrescar(n);
				int index=notasFiltradas.indexOf(n);
				//notasFiltradas.set(index, n);
			}
		}
		
		public void traspasoJuridico(){
			if(!getSeleccionadas().isEmpty()){
				final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
				for(NotaDeCredito n:getSeleccionadas()){
					if(n.getSerie().equals("M")){
						notas.add(n);
					}
				}				
				final ValueHolder fh=new ValueHolder();
				final SXAbstractDialog dialog=Binder.createDateSelector(fh);
				dialog.setTitle("Fehca de traspaso");
				dialog.open();
				if(!dialog.hasBeenCanceled()){
					for(NotaDeCredito cargo:notas){
						ServiceLocator.getJuridicoManager().transferirJuridico(cargo, (Date)fh.getValue());
					}
				}
				MessageUtils.showMessage("Traspaso terminado", "Jurídico");
				
			}
		}
		
		public void pagoAutomatico(){
			if(!getSeleccionadas().isEmpty()){
				final List<NotaDeCredito> cargos=new ArrayList<NotaDeCredito>();
				cargos.addAll(getSeleccionadas());
				if(controlador.registrarPagoAutomatico(cargos))
					load();
				
			}
		}
		
	}
	

}
