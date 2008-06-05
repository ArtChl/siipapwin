package com.luxsoft.siipap.inventarios.consultas;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.text.NumberFormatter;

import net.infonode.tabbedpanel.TabbedPanel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.inventarios.model.AnalisisDeInventarioDeMaquilaModel;
import com.luxsoft.siipap.inventarios.model.TotalizadorDeMaquila;
import com.luxsoft.siipap.inventarios.utils.InventarioUtils;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.InventarioMaq;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;
import com.luxsoft.siipap.swing.views2.InternalTaskTab;

/**
 *  Vista interna de las entradas de inventario
 *  
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class EntradasDeMaterialView extends AbstractInternalTaskView implements ActionListener{
	
	private final AnalisisDeInventarioDeMaquilaModel model;
	
	private TabbedPanel tabPanel;
	private JXTable grid;
	private JPanel filtroPanel;
	private JPanel totalesPanel;
	
	private EventList<EntradaDeMaterial> entradasFiltradas;
	private EventSelectionModel<EntradaDeMaterial> sm;
	
	//Filtros
	private JFormattedTextField fechaCorte;
	private JFormattedTextField idFilter;
	private JFormattedTextField entradaFilter;
	private JFormattedTextField claveFilter;
	
	private Action selectAction;
	private Action loadAction;
	
	private SalidasACorteView cortesView;
	private SalidasACorteEnProcesoView procesoView;
	private SalidaDeBobinasView bobinasView;
	private TrasladosView trasladosView;
	
	private SalidasDeHojasView salidaDeHojasView;
	private EntradaDeHojasView entradaDeHojasView;
	private final TotalizadorDeMaquila totalizador=new TotalizadorDeMaquila();
	
	
	public EntradasDeMaterialView(final AnalisisDeInventarioDeMaquilaModel model){
		this.model=model;
		setTitle("Entradas");
	}
	
	private void initActions(){
		loadAction=CommandUtils.createLoadAction(this, "load");
		selectAction=new DispatchingAction(this,"select");
	}

	public JComponent getControl() {
		initActions();
		final JPanel panel=new JPanel(new BorderLayout());
		JSplitPane sp=new JSplitPane(JSplitPane.VERTICAL_SPLIT
				,buildEntradasPanel()
				,buildDetallesPanel());
		sp.setResizeWeight(.5);
		panel.add(sp,BorderLayout.CENTER);
		return panel;
	}
	
	
	private MatcherEditor getFiltroPorId(){
		//NumberFormatter nf=FormatUtils.getLongFormatter(false);			
		idFilter=new JFormattedTextField();
		idFilter.setColumns(10);
		idFilter.addActionListener(this);
		final TextFilterator<EntradaDeMaterial> filterator=GlazedLists.textFilterator(new String[]{"id"});
		final TextComponentMatcherEditor<EntradaDeMaterial> editor1=new TextComponentMatcherEditor<EntradaDeMaterial>(idFilter,filterator);
		return editor1;
	}
	
	private MatcherEditor getFiltroPorEntrada(){						
		entradaFilter=new JFormattedTextField();
		entradaFilter.addActionListener(this);
		final TextFilterator<EntradaDeMaterial> filterator=GlazedLists.textFilterator(new String[]{"entradaDeMaquilador"});
		final TextComponentMatcherEditor<EntradaDeMaterial> editor1=new TextComponentMatcherEditor<EntradaDeMaterial>(entradaFilter,filterator);
		return editor1;
	}
	
	private MatcherEditor getFiltroPorClave(){
		claveFilter=new JFormattedTextField();
		claveFilter.addActionListener(this);
		final TextFilterator<EntradaDeMaterial> filterator=GlazedLists.textFilterator(new String[]{"articulo.clave"});
		final TextComponentMatcherEditor<EntradaDeMaterial> editor1=new TextComponentMatcherEditor<EntradaDeMaterial>(claveFilter,filterator);
		return editor1;
	}
	
	@SuppressWarnings("unchecked")
	private JComponent buildEntradasPanel(){
		
		final EventList<MatcherEditor<EntradaDeMaterial>> editores=new BasicEventList<MatcherEditor<EntradaDeMaterial>>();			
		editores.add(getFiltroPorId());
		editores.add(getFiltroPorEntrada());
		editores.add(getFiltroPorClave());
		
		final CompositeMatcherEditor<EntradaDeMaterial> compositeEditor=new CompositeMatcherEditor<EntradaDeMaterial>(editores);
		compositeEditor.setMode(CompositeMatcherEditor.AND);
		
		entradasFiltradas=new FilterList<EntradaDeMaterial>(model.getEntradas(),new ThreadedMatcherEditor<EntradaDeMaterial>(compositeEditor));
		//entradasFiltradas.addListEventListener(new EventHandler());
		
		final SortedList<EntradaDeMaterial> sortedList=new SortedList<EntradaDeMaterial>(entradasFiltradas,null);
		
		final EventTableModel<EntradaDeMaterial> tm=new EventTableModel<EntradaDeMaterial>(sortedList,InventarioUtils.getStandardTableFormat());
		sm=new EventSelectionModel<EntradaDeMaterial>(sortedList);
		
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(sm);
		
		ComponentUtils.decorateActions(grid);		
		ComponentUtils.decorateForDobleClick(grid, selectAction);
		
		new TableComparatorChooser<EntradaDeMaterial>(grid,sortedList,true);		
		return UIFactory.createTablePanel(grid);
	}
	
	private JComponent buildDetallesPanel(){
		
		cortesView=new SalidasACorteView(entradasFiltradas,model.getSalidasACorte());
		procesoView=new SalidasACorteEnProcesoView(entradasFiltradas,model.getSalidasEnProceso());
		bobinasView=new SalidaDeBobinasView(entradasFiltradas,model.getSalidaDeBobinas());
		trasladosView=new TrasladosView(entradasFiltradas,model.getTraslados());
		salidaDeHojasView=new SalidasDeHojasView(entradasFiltradas,model.getSalidaDeHojas());		
		entradaDeHojasView=new EntradaDeHojasView(entradasFiltradas,model.getEntradasDeHojas());
		
		tabPanel=new TabbedPanel();		 
		tabPanel.addTab(new InternalTaskTab(cortesView));		 
		tabPanel.addTab(new InternalTaskTab(procesoView));		 
		tabPanel.addTab(new InternalTaskTab(bobinasView));		 
		tabPanel.addTab(new InternalTaskTab(trasladosView));
		tabPanel.addTab(new InternalTaskTab(salidaDeHojasView));
		tabPanel.addTab(new InternalTaskTab(entradaDeHojasView));
		
		return tabPanel;
	}
	
	
	public void select(){
		if(getEntradaSeleccionada()!=null){				
			final InventarioMaq maq=ServiceLocator.getMaquilaManager()
			.cargarInventario(getEntradaSeleccionada().getId(),model.getFechaDeCorte());
			AnalisisDeEntradaModel model=new AnalisisDeEntradaModel(maq);
			AnalisisDeEntrada form=new AnalisisDeEntrada(model);
			form.setModal(false);
			form.open();
		}
	}
	
	public EntradaDeMaterial getEntradaSeleccionada(){
		if(!sm.getSelected().isEmpty()){
			EntradaDeMaterial e=sm.getSelected().get(0);
			return e;
		}
		return null;
	}
	
	public void load(){
		final SwingWorker<List ,List> worker=new SwingWorker<List, List>(){
							
			@Override
			protected List doInBackground() throws Exception {
				final List l=new ArrayList();
				l.add(model.buscarEntradas());
				l.add(model.buscarSalidasACorte());
				l.add(model.buscarSalidasDeMaterial());
				l.add(model.buscarSalidaDeBobinas());
				model.cargarInvnentarioDeHojas();
				
				return l;
			}

			protected void done() {
				try {
					List<EntradaDeMaterial> data0=(List<EntradaDeMaterial>) get().get(0);
					model.getEntradas().clear();
					model.getEntradas().addAll(data0);
					
					
					List<SalidaACorte> data1=(List<SalidaACorte>) get().get(1);
					model.getSalidas().clear();
					model.getSalidas().addAll(data1);
					cortesView.pack();
					
					
					
					List<SalidaDeBobinas> data2=(List<SalidaDeBobinas>) get().get(2);
					model.getSalidasDeMaterial().clear();
					model.getSalidasDeMaterial().addAll(data2);
					
				 	List<SalidaDeBobinas> data3=(List<SalidaDeBobinas>) get().get(3);
					model.getSalidaDeBobinas().clear();
					model.getSalidaDeBobinas().addAll(data3);		
					
					recalcularTotales();
					pack();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void pack(){
		grid.packAll();
		procesoView.pack();
		bobinasView.pack();
		
	}
	
	public JPanel getFiltrosPanel(){
		if(filtroPanel==null){
			this.fechaCorte=Binder.createDateField(model.getModel("fechaDeCorte"));
			fechaCorte.addActionListener(this);
			final DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:p,2dlu,f:p:g",""));
			builder.append("Fecha de Corte",fechaCorte,true);
			builder.append("Id",idFilter,true);
			builder.append("Entrada",entradaFilter,true);
			builder.append("Clave",claveFilter,true);
			builder.getPanel().setOpaque(false);
			filtroPanel=builder.getPanel();
		}
		return filtroPanel;
	}
	
	public JPanel getTotalesPanel(){
		if(totalesPanel==null){
			MaquilaTotalesPanel tot=new MaquilaTotalesPanel(totalizador);
			totalesPanel=(JPanel)tot.getControl();
		}
		return totalesPanel;
	}

	@Override
	public void instalOperacionesAction(JXTaskPane operaciones) {		
		operaciones.add(loadAction);
	}

	@Override
	public void installFiltrosPanel(JXTaskPane filtros) {
		filtros.add(getFiltrosPanel());
	}
	
	public void installDetallesPanel(JXTaskPane detalle) {
		detalle.add(getTotalesPanel());
	}
	
	private void recalcularTotales(){
		totalizador.calcularEntradasDeBobinas(entradasFiltradas);		
		
		cortesView.totalizar(totalizador);
		procesoView.totalizar(totalizador);
		trasladosView.totalizar(totalizador);
		bobinasView.totalizar(totalizador);
		
		entradaDeHojasView.totalizar(totalizador);
		salidaDeHojasView.totalizar(totalizador);
		
		totalizador.recalcularTotales();
	}
	
	/**
	 * Detecta cambios en las listas para informar que el valor de las mismas a cambiado
	 * 
	 * @author Ruben Cancino
	 *
	
	private class EventHandler implements ListEventListener{
		public void listChanged(ListEvent listChanges) {
			while(listChanges.next()){
				
			}				
			recalcularTotales();	
		}		
	}
	*/
	

	public void actionPerformed(ActionEvent e) {
		recalcularTotales();
		
	}
	
}