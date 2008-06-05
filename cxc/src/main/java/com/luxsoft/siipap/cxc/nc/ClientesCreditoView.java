package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.aspectj.bridge.MessageUtil;
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

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.ResourcesUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.InternalTaskView;


/**
 * Vista de los clientes a credito. 
 * 
 * @author Ruben Cancino
 *
 */
public abstract class ClientesCreditoView implements InternalTaskView{
	
	
	
	private JXTable grid;
	private JTextField inputField;
	private JComponent filterPanel;
	private EventList<ClienteCredito> clientes;
	private EventSelectionModel<ClienteCredito> selection;
	private Action selectAction;
	private Action loadAction;
	private Action imprimirNotasDescuento;
	

	public JComponent getControl() {	
		
		grid=ComponentUtils.getStandardTable();
		inputField=new JTextField(20);
		
		clientes=GlazedLists.eventList(new BasicEventList<ClienteCredito>());
		
		final TextFilterator<ClienteCredito> filterator=GlazedLists.textFilterator(new String[]{"cliente.nombre","cliente.clave"});
		final TextComponentMatcherEditor<ClienteCredito> editor=new TextComponentMatcherEditor<ClienteCredito>(inputField,filterator);
		final FilterList<ClienteCredito> filterList=new FilterList<ClienteCredito>(clientes,editor);
		final SortedList<ClienteCredito> sortedList=new SortedList<ClienteCredito>(filterList,null);
		
				
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
		return new Header("Cartera de Clientes","Lista de clientes (Credito)").getHeader();
	}
	
	public JComponent getFilterPanel(){
		if(filterPanel==null){
			FormLayout layout=new FormLayout(
					"3dlu,l:50dlu,p:g",
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
		
	}

	public void instalProcesosActions(JXTaskPane procesos) {
		if(imprimirNotasDescuento==null){
			imprimirNotasDescuento=new DispatchingAction(this,"imprimirNotasDescuento");
			CommandUtils.configAction(imprimirNotasDescuento, CXCActions.ImprimirNotasDescuento.getId(), "");
		}
		procesos.add(imprimirNotasDescuento);
	}

	public void installDetallesPanel(JXTaskPane detalle) {
		
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
	
	/**
	 * Imprime las notas de descuento seleccionadas para un periodo en particular 
	 *
	 */
	public void imprimirNotasDescuento(){
		ValueModel periodo=new ValueHolder(Periodo.hoy());
		AbstractDialog dialog=Binder.createPeriodoSelector(periodo);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			System.out.println("Cargando notas tipo U sin imprimir para el periodo :"+periodo.getValue());
			loadNotasParaDescuentoImprimibles((Periodo)periodo.getValue());
		}
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
	}
	
	protected abstract List<NotaDeCredito> getNotasParaImprimir(final Periodo p);
	
	private void loadNotasParaDescuentoImprimibles(final Periodo p){
		ImpresionDeNotas.imprimir(getNotasParaImprimir(p));
		/**
		SwingWorker<List<NotaDeCredito>,String> worker=new SwingWorker<List<NotaDeCredito>, String>(){
			
			
			
			protected List<NotaDeCredito> doInBackground() throws Exception {
				
				return getNotasParaImprimir(p);
			}
			
			protected void done() {
				try {
					final List<NotaDeCredito> notas=get();
					System.out.println("Notas a imprimir: "+notas.size());
					long next=ServiceLocator.getNotasManager().nextNumero(notas.get(0).getTipo());					
					final FormaDeImpresion impresor=new FormaDeImpresion(notas,next);
					impresor.open();
					if(!impresor.hasBeenCanceled()){
						Runtime r=Runtime.getRuntime();
						final SortedList<NotaDeCredito> notas2=new SortedList<NotaDeCredito>(GlazedLists.eventList(notas),GlazedLists.beanPropertyComparator(NotaDeCredito.class, "numero"));
						for(NotaDeCredito n:notas2){
							ServiceLocator.getNotasManager().imprimirNotaDeDescuento(n);
							Process p=r.exec(new String[]{"IMPRNOTA.BAT"});
							int res=p.waitFor();
							
						}
					
					}
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.showError("Error al cargar notas", e);
				}
			}
			
		};
		TaskUtils.executeSwingWorker(worker);
		**/
	}	

}
