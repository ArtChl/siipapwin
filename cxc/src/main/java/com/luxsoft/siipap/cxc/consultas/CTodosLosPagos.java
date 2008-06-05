package com.luxsoft.siipap.cxc.consultas;

import java.awt.BorderLayout;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.Action;
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
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.utils.MatcherEditors;
import com.luxsoft.siipap.cxc.utils.PagosPorFechaSelector;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;



/**
 * Consulta para busqueda de todos los pagos en un periodo
 * 
 * @author Ruben Cancino
 *
 */
public class CTodosLosPagos extends AbstractInternalTaskView{
	
	private Periodo periodo=Periodo.getPeriodoDelMesActual();
	private EventList<Pago> pagosSource;
	private FilterList<Pago> pagosFiltrados;
	private PagosPorFechaSelector fechaSelector; 
	private JTextField documento;
	private JTextField cliente;
	
	private Action loadAction;
	private Action cambiarPeriodo;
	
	private JXTable grid;
	private JPanel  filterPanel;
	
	private void initComponents(){
		documento=new JTextField();
		cliente=new JTextField();
	}


	public JComponent getControl() {
		initComponents();
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeader(),BorderLayout.NORTH);
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildHeader(){
		return new HeaderPanel("Pagos aplicados"
				,getDescription());
	}
	
	private String getDescription(){
		String pattern="Lista de todos los pagos generados tanto en SiipapWin como en Siipap" +
				" para el periodo: {0}";
		return MessageFormat.format(pattern, periodo.toString());
	}
	
	private JComponent buildGridPanel(){
		initGlazedLists();
		final String[] props={"id","cliente.clave","cliente.nombre","fecha","numero","formaDePago","referencia","importe","comentario"};
		final String[] names={"id","Cliente","Nombre","Fecha","Numero","F.P","Referencia","Importe","Comentario"};
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class, props,names);
		
		final SortedList<Pago> sortedPagos=new SortedList<Pago>(pagosFiltrados,null);
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(sortedPagos,tf);
		
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		new TableComparatorChooser<Pago>(grid,sortedPagos,true);
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	private void initGlazedLists(){
		pagosSource=GlazedLists.threadSafeList(new BasicEventList<Pago>());
		
		//Filtros
		final TextFilterator<Pago> documentoFilterator=GlazedLists.textFilterator(new String[]{"numero"});
		final TextComponentMatcherEditor<Pago> documentoMatcher=new TextComponentMatcherEditor<Pago>(documento,documentoFilterator);
		
		final TextFilterator<Pago> clienteFilterator=GlazedLists.textFilterator(new String[]{"cliente.clave","cliente.nombre"});
		final TextComponentMatcherEditor<Pago> clienteMatcher=new TextComponentMatcherEditor<Pago>(cliente,clienteFilterator);		
		 
		fechaSelector=MatcherEditors.createPagosPorFechaSelector("fecha");
		
		final EventList<MatcherEditor<Pago>> editors=new BasicEventList<MatcherEditor<Pago>>();
		editors.add(documentoMatcher);
		editors.add(clienteMatcher);
		editors.add(fechaSelector);
		
		pagosFiltrados=new FilterList<Pago>(pagosSource,new ThreadedMatcherEditor<Pago>(new CompositeMatcherEditor<Pago>(editors)));
		
	}
	
	private JComponent getFiltersPanel(){
		if(filterPanel==null){
			final DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:p,2dlu,max(p;60dlu)",""));
			builder.append("Cliente",cliente,true);
			builder.append("Factura",documento,true);
			builder.append("Fecha F",fechaSelector.getControl(),true);
			builder.getPanel().setOpaque(false);
			filterPanel=builder.getPanel();
		}
		return filterPanel;
	}
	
	
	@Override
	public void instalOperacionesAction(JXTaskPane operaciones) {
		if(loadAction==null)
			loadAction=CommandUtils.createLoadAction(this, "load");
		if(cambiarPeriodo==null){
			cambiarPeriodo=new DispatchingAction(this,"cambiarPeriodo");
			CommandUtils.configAction(cambiarPeriodo, "seleccionarPeriodo", "");
		}
		operaciones.add(loadAction);
	}

	@Override
	public void installFiltrosPanel(JXTaskPane filtros) {
		filtros.add(getFiltersPanel());
	}
	
	public void cambiarPeriodo(){
		AbstractDialog dialog=Binder.createPeriodoSelector(new ValueHolder(periodo));
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			if(periodo.getDias()>=120){
				MessageUtils.showMessage("El número maximo de dias para carrgar es de 120, generar una seleccion menor", "Periodo");
				return;
			}
			load();
		}
		
	}

	
	public void load(){
		SwingWorker<List<Pago>, String> worker=new SwingWorker<List<Pago>, String>(){

			@Override
			protected List<Pago> doInBackground() throws Exception {
				return ServiceLocator.getPagosManager().buscarPagosAplicadosOld(periodo);
			}
			@Override
			protected void done() {
				try {
					pagosSource.clear();
					pagosSource.addAll(get());
					grid.packAll();
				} catch (Exception e) {
					MessageUtils.showError("Error", e);
					e.printStackTrace();
				}
			}
			
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void close(){
		pagosSource.clear();
	}
	

}
