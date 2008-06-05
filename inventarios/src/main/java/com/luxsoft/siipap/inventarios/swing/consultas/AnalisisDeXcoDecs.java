package com.luxsoft.siipap.inventarios.swing.consultas;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
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

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.dao.MovimientoDao;
import com.luxsoft.siipap.inventarios.domain.Salida;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;


/**
 * Lista de XCO,DEC por vincular a coms
 *  
 * @author Ruben Cancino
 *
 */
public class AnalisisDeXcoDecs extends AbstractView implements AnalisisSupport{
	
	private ValueHolder periodo=new ValueHolder(Periodo.getPeriodoDelMesActual());
	
	private JXTable grid;
	
	private EventList<Salida> salidas;
	private FilterList<Salida> salidasFiltradas;
	private SortedList<Salida> sortedList;
	private EventSelectionModel<Salida> selectionModel;
	
	private Action reloadAction;
	private Action seleccionarPeriodo;
	private JLabel periodoLabel;
	
	public AnalisisDeXcoDecs(){		
		initActions();
	}
	
	private void initActions(){
		reloadAction=CommandUtils.createRefreshAction(this, "refresh");
		seleccionarPeriodo=new DispatchingAction(this,"seleccionar");
		CommandUtils.configAction(seleccionarPeriodo, "seleccionarPeriodo", "images2/calendar.png");
	}
	
	private void initComponents(){		
		periodoLabel=DefaultComponentFactory.getInstance().createTitle(periodo.getValue().toString());
	}

	@Override
	protected JComponent buildContent() {		
		initComponents();
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeaderPanel(),BorderLayout.NORTH);
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildHeaderPanel(){
		JXHeader hx=new JXHeader("Vinculación de movimientos ","Movimientos de inventario (Salidas) " +
				"de tipo XCO y DEC ");
		//hx.setFont(hx.getFont().deriveFont(18f));
		//hx.setAlpha(.2f);		
		return hx;
	}
	
	private JComponent buildGridPanel(){
		initGlazedLists();
		grid=ComponentUtils.getStandardTable();
		final String[] props={"ALMARTIC","ALMNOMBR","ALMTIPO","ALMNUMER","destino.ALMNUMER","ALMFECHA","cantidad","ALMUNIDMED"};
		final String[] cols={"Artículo","Descripción","Tipo","Numero","COM","Fecha","Cantidad","Unidad"};
		TableFormat<Salida> tf=GlazedLists.tableFormat(Salida.class, props,cols);
		
		final EventTableModel<Salida> tm = new EventTableModel<Salida>(sortedList,tf);
		selectionModel=new EventSelectionModel<Salida>(sortedList);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
					vincular();
			}			
		});
		new TableComparatorChooser<Salida>(grid,sortedList,true);
		ComponentUtils.decorateActions(grid);
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	private JTextField claveField;
	
	private void initGlazedLists(){
		//La lista debe incluir todos los registros XCO y DEC
		salidas=GlazedLists.threadSafeList(new BasicEventList<Salida>());
		
		//Editor para filtrar por Clave 
		claveField=new JTextField(15);
		TextFilterator<Salida> claveFilterator=GlazedLists.textFilterator(new String[]{"ALMARTIC","ALMNOMBR"});
		TextComponentMatcherEditor<Salida> claveEditor=new TextComponentMatcherEditor<Salida>(claveField,claveFilterator);
		
		
		final EventList<MatcherEditor<Salida>> editores=new BasicEventList<MatcherEditor<Salida>>();				
		editores.add(claveEditor);
		final CompositeMatcherEditor<Salida> compositeEditor=new CompositeMatcherEditor<Salida>(editores);
		compositeEditor.setMode(CompositeMatcherEditor.AND);
		
		salidasFiltradas=new FilterList<Salida>(salidas,new ThreadedMatcherEditor<Salida>(compositeEditor));
		sortedList=new SortedList<Salida>(salidasFiltradas,null);
	}
	
	public void refresh(){
		SwingWorker<List<Salida>, String> worker=new SwingWorker<List<Salida>, String>(){			
			protected List<Salida> doInBackground() throws Exception {
				return getSalidas();
			}			
			protected void done() {
				try {
					salidas.clear();
					salidas.addAll(get());
					grid.packAll();
				} catch (Exception e) {
					MessageUtils.showError("Error buscando salidas DEC,XCO", e);
				}
			}
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	private List<Salida> getSalidas(){
		MovimientoDao dao=ServiceLocator.getMovimientoDao();
		Periodo p=(Periodo)periodo.getValue();
		return dao.buscarSalidas(p, "DEC","XCO");
	}
	
	public Action getReloadAction(){
		return reloadAction;
	}
	public Action getSeleccionarPeriodo() {
		return seleccionarPeriodo;
	}
	
	public JTextField getClaveFilter(){
		return this.claveField;
	}
	
	public JComponent createFiltrosPanel(){
		FormLayout layout=new FormLayout(
				"l:p,2dlu,R:p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		builder.append("Periodo",periodoLabel);
		builder.append("Articulo",getClaveFilter(),true);
		JPanel	filtrosPanel= builder.getPanel();
		filtrosPanel.setOpaque(false);
		return filtrosPanel;
	} 

	public void seleccionar(){
		AbstractDialog dialog=Binder.createPeriodoSelector(periodo);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			periodoLabel.setText(periodo.getValue().toString());
			refresh();
		}
	}
	
	public void vincular(){
		if(!selectionModel.getSelected().isEmpty()){
			final Salida selected=selectionModel.getSelected().get(0);
			SalidaDecForm form=new SalidaDecForm(selected){

				@Override
				public List<AnalisisDeEntrada> buscarComs() {					
					List<AnalisisDeEntrada> found=ServiceLocator.getAnalisisDeEntradaDao()
					.buscarEntradasPorAnalizar(selected.getALMARTIC(),selected.getALMFECHA());					
					return found;
				}
				
			};
			form.open();
			if(!form.hasBeenCanceled()){
				System.out.println("Salvando salida: "+selected);
				ServiceLocator.getMovimientoDao().update(selected);
			}
		}
		
	}
	
	public void close(){
		salidas.clear();
	}
	
	private JComponent filtros;
	
	public JComponent getFilterPanel() {
		if(filtros==null) 
			filtros=createFiltrosPanel();
		return filtros;
	}

	public Action[] getOperaciones() {
		return new Action[]{getReloadAction(),getSeleccionarPeriodo()};
	}
	
	
	public static void main(String[] args) {
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				JPanel panel=new JPanel(new BorderLayout());
				AnalisisDeXcoDecs c=new AnalisisDeXcoDecs();
				panel.add(c.getContent(),BorderLayout.CENTER);
				panel.add(c.createFiltrosPanel(),BorderLayout.WEST);
				
				JToolBar bar=new JToolBar();
				bar.add(c.getReloadAction());
				bar.add(c.getSeleccionarPeriodo());
				panel.add(bar,BorderLayout.NORTH);
				return panel;
			}
			
		};
		dialog.open();
	}

	

}
