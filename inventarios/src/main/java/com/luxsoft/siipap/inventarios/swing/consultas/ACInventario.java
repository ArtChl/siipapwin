package com.luxsoft.siipap.inventarios.swing.consultas;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import net.infonode.tabbedpanel.titledtab.TitledTab;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.Filterator;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor.Event;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.inventarios.domain.InventarioMensual;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Analisis de Costo a partir del inventario
 * 
 * En virtud al elevado nivel de recursos que esta vista demanda, cuando se cierra se
 * terminan todos sus componentes y se descarta para ser recolectada por GC
 * 
 * @author Ruben Cancino
 *
 */
public class ACInventario extends AbstractView implements AnalisisSupport{
	
	private TitledTab tab;
	private JXTable grid;
	private JTextField yearField;
	private MesEditor mesEditor;
	private JTextField claveField;
	private JTextField descField;
	
	private JLabel totalIni;
	private JLabel totalCxp;
	private JLabel totalMaq;
	private JLabel totalGas;
	private JLabel totalComs;
	private JLabel totalFin;
	private JLabel totalCv1;
	private JLabel totalCv2;
	
	
	
	private Action loadAction;
	
	private final ACInventarioModel model;
	
	public ACInventario(){
		this.model=new ACInventarioModel();
	}
	
	public TitledTab getTab(){
		if(tab==null){
			tab=new TitledTab("Inventario",getIconFromResource("images2/categories.png"),getContent(),null);
			tab.putClientProperty(AnalisisSupport.SUPPORT_KEY,this);
		}
		return tab;
	}
	
	private void initActions(){
		loadAction=CommandUtils.createLoadAction(this, "load");
	}

	@Override
	protected JComponent buildContent() {
		initActions();
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildGridPanel(){
		
		yearField=new JTextField(10);
		final TextFilterator<InventarioMensual> yearFilterator=GlazedLists.textFilterator(new String[]{"year"});
		final MatcherEditor<InventarioMensual> yearEditor=new TextComponentMatcherEditor<InventarioMensual>(yearField,yearFilterator);
		
		
		//final TextFilterator<InventarioMensual> mesFilterator=GlazedLists.textFilterator(new String[]{"mes"});
		mesEditor=new MesEditor();
		
		claveField=new JTextField(10);
		final TextFilterator<InventarioMensual> claveFilterator=GlazedLists.textFilterator(new String[]{"clave"});
		final MatcherEditor<InventarioMensual> claveEditor=new TextComponentMatcherEditor<InventarioMensual>(claveField,claveFilterator);
		
		descField=new JTextField(10);
		final TextFilterator<InventarioMensual> descFilterator=GlazedLists.textFilterator(new String[]{"articulo.descripcion1"});
		final MatcherEditor<InventarioMensual> descEditor=new TextComponentMatcherEditor<InventarioMensual>(descField,descFilterator);
		
		final EventList<MatcherEditor<InventarioMensual>> editors=new BasicEventList<MatcherEditor<InventarioMensual>>();
		editors.add(yearEditor);
		editors.add(mesEditor);
		editors.add(claveEditor);
		editors.add(descEditor);
		
		
		final CompositeMatcherEditor<InventarioMensual> ceditor=new CompositeMatcherEditor<InventarioMensual>(editors);
		ceditor.setMode(CompositeMatcherEditor.AND);
		ceditor.addMatcherEditorListener(new MatcherEditor.Listener<InventarioMensual>(){
			public void changedMatcher(Event matcherEvent) {
				model.updateTotales();				
			}			
		});
		
		final FilterList<InventarioMensual> filterList=new FilterList<InventarioMensual>(model.getInventario(),ceditor);
		
		
		final String[] props={"clave","articulo.descripcion1","inicial","costoInicial","costoCxp","costoMaq","gastosMaq","saldo","costo","costoPromedio","costoDeVentas","costoDeVentasF","comsSA","movimientos","movimientosCosto","mes"};
		final String[] labels={"Articulo","Descripción","Inv ini(U)","Inv ini ($)","CxP","Maquila (M.P)","Maq (Gastos)","Inv Final (U)","Inv Final ($)","C.Promedio","Costo V.","Costo V(Inv)","Coms (SA)","Movs(int)","Costo(Movs)","Mes"};
		final TableFormat<InventarioMensual> tf=GlazedLists.tableFormat(InventarioMensual.class,props, labels);
		//final EventTableModel<InventarioMensual> tm=new EventTableModel<InventarioMensual>(filterList,tf);
		
		final SortedList<InventarioMensual> sortedList=new SortedList<InventarioMensual>(filterList,null);
		final EventTableModel<InventarioMensual> tm=new EventTableModel<InventarioMensual>(sortedList,tf);
		
		model.registerInventarioFiltrado(filterList);
		grid=ComponentUtils.getStandardTable();
		
		grid.setModel(tm);
		
		final EventSelectionModel<InventarioMensual> selection=new EventSelectionModel<InventarioMensual>(filterList);
		grid.setSelectionModel(selection);
		ComponentUtils.decorateActions(grid);
		new TableComparatorChooser<InventarioMensual>(grid,sortedList,true);
		JComponent c=UIFactory.createTablePanel(grid);
		return c;
	}

	public JComponent getFilterPanel() {
		FormLayout layout=new FormLayout(
				"l:p,2dlu,f:p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();		
		builder.append("Año",yearField,true);
		builder.append("Mes",mesEditor.getInput(),true);
		builder.append("Clave",claveField,true);
		builder.append("Descripción",descField,true);
		builder.appendSeparator();
		NumberFormat format=NumberFormat.getCurrencyInstance();
		totalIni=BasicComponentFactory.createLabel(model.getModel("invInicial"), format);
		totalIni.setHorizontalAlignment(SwingConstants.RIGHT);
		totalCxp=BasicComponentFactory.createLabel(model.getModel("totalCxp"), format);
		totalCxp.setHorizontalAlignment(SwingConstants.RIGHT);
		totalMaq=BasicComponentFactory.createLabel(model.getModel("totalMaq"), format);
		totalMaq.setHorizontalAlignment(SwingConstants.RIGHT);
		totalGas=BasicComponentFactory.createLabel(model.getModel("gastosMaq"), format);
		totalGas.setHorizontalAlignment(SwingConstants.RIGHT);
		totalComs=BasicComponentFactory.createLabel(model.getModel("comsSA"), format);
		totalComs.setHorizontalAlignment(SwingConstants.RIGHT);
		totalFin=BasicComponentFactory.createLabel(model.getModel("invFinal"), format);
		totalFin.setHorizontalAlignment(SwingConstants.RIGHT);
		totalCv1=BasicComponentFactory.createLabel(model.getModel("costoVentasCalculado"), format);
		totalCv1.setHorizontalAlignment(SwingConstants.RIGHT);
		totalCv2=BasicComponentFactory.createLabel(model.getModel("costoVentasReal"), format);
		totalCv2.setHorizontalAlignment(SwingConstants.RIGHT);
		builder.append("Inventario Ini",totalIni,true);
		builder.append("Compras CxP ",totalCxp,true);
		builder.append("Compras Maq ",totalMaq,true);
		builder.append("Gastos  Maq ",totalGas,true);
		builder.append("Inventario Fin",totalFin,true);
		builder.append("Costo de V (C) ",totalCv1,true);
		builder.append("Costo de V (R) ",totalCv2,true);
		
		JPanel	filtrosPanel= builder.getPanel();
		filtrosPanel.setOpaque(false);
		return filtrosPanel;
	}

	public Action[] getOperaciones() {
		return new Action[]{
			loadAction	
		};
	}
	
	public void load(){
		SwingWorker worker=new SwingWorker(){
			
			protected Object doInBackground() throws Exception {
				model.load();
				return "OK";
			}
			protected void done() {
				grid.packAll();
			}			
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void close(){
		model.close();
		content=null;		
	}
	
	
	private class MesEditor extends AbstractMatcherEditor<InventarioMensual> implements PropertyChangeListener{
		
		private JComboBox  input;
		private ValueModel vm=new ValueHolder(new Integer(13));
		
		public MesEditor(){
			input=Binder.createMesBindingConTodos(vm);
			vm.addValueChangeListener(this);
		}
		
		public JComboBox getInput(){
			return input;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if(getMes()==13)
				fireMatchAll();
			else if(getMes()<=0 || getMes()>13)
				fireMatchNone();
			else
				fireChanged(new MesMatcher());
			
		}
		
		protected int getMes(){
			Integer i=(Integer)vm.getValue();
			return i;
		}

		private class MesMatcher implements Matcher<InventarioMensual>{

			public boolean matches(InventarioMensual item) {
				return item.getMes()==getMes();
			}
			
		}
				
		
		
	}

}
