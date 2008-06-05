package com.luxsoft.siipap.inventarios.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.infonode.tabbedpanel.titledtab.TitledTab;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

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
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.cxp.domain.AnalisisDet;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.Periodo;

import com.luxsoft.siipap.inventarios.services.InventariosManager;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Consulta para el analisis de costos por articulo
 * 
 * @author Ruben Cancino
 *
 */
public class ACPorArticulo extends AbstractView implements AnalisisSupport{
	
	private Action seleccionarPeriodo;
	private Action seleccionarArticulo;
	private Action loadData;
	private TitledTab tab;
	
	private HeaderPanel header; 
	private final ACPorArticuloModel model;

	public ACPorArticulo(final ACPorArticuloModel model) {
		this.model=model;
		PropertyChangeListener handler=new HeaderHandler();
		model.getModel("articulo").addValueChangeListener(handler);
		model.getModel("periodo").addValueChangeListener(handler);
	}
	
	public TitledTab getTab(){
		if(tab==null){
			tab=new TitledTab("Artículo",getIconFromResource("images2/box.png"),getContent(),null);
			tab.putClientProperty(AnalisisSupport.SUPPORT_KEY,this);
		}
		return tab;
	}
	
	private void initActions(){
		seleccionarPeriodo=new DispatchingAction(this,"seleccionarPeriodo");
		getActionConfigurer().configure(seleccionarPeriodo, "seleccionarPeriodo");
		seleccionarArticulo=new DispatchingAction(this,"seleccionarArticulo");
		getActionConfigurer().configure(seleccionarArticulo, "seleccionarArticulo");
		loadData=CommandUtils.createLoadAction(this, "load");
	}

	@Override
	protected JComponent buildContent() {
		initActions();
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeader(),BorderLayout.NORTH);
		panel.add(buildAnalisisPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildHeader(){
		header=new HeaderPanel("Artículo","Descripción");
		return header;
	}
	
	private JComponent buildAnalisisPanel(){
		FormLayout layout=new FormLayout("p:g(.8),3dlu,p:g(.2)"
				,"p,t:p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(DefaultComponentFactory.getInstance().createTitle("Coms"),cc.xy(1,1));
		builder.add(buildComsPanel(),cc.xy(1, 2));
		builder.add(DefaultComponentFactory.getInstance().createTitle("CxP"),cc.xy(3,1));
		builder.add(buildDetailPanel(),cc.xy(3, 2));
		return builder.getPanel();
	}
	
	private JComponent buildDetailPanel(){
		JPanel panel=new JPanel(new VerticalLayout(3));		
		panel.add(buildCxpPanel());
		panel.add(DefaultComponentFactory.getInstance().createTitle("Maquila (Hoj)"));
		panel.add(buildMaqHojPanel());
		panel.add(DefaultComponentFactory.getInstance().createTitle("Maquila (Bob)"));
		panel.add(buildMaqBobPanel());		
		panel.add(DefaultComponentFactory.getInstance().createTitle("Análisi de Costo"));
		panel.add(buildCostoPanel());
		return panel;
	}
	
	private JTextField numeroInput;
	private EventList<AnalisisDeEntrada> coms;
	private EventSelectionModel<AnalisisDeEntrada> selectionModel;
	private JXTable comsGrid;
	
	private JComponent buildComsPanel(){
		final String[] props={"COM","FENT","SUCURSAL","clave","descripcion","ingresada"};
		final String[] cols={"Com","Fecha","Suc","Artículo","Descripción","Ingresadas"};
		final TableFormat<AnalisisDeEntrada> tf=GlazedLists.tableFormat(AnalisisDeEntrada.class,props, cols);
		coms=model.getEntradas();
		
		numeroInput=new JTextField();
		final TextFilterator<AnalisisDeEntrada> numeroFilterator=GlazedLists.textFilterator(new String[]{"COM"});
		final MatcherEditor<AnalisisDeEntrada> numeroEditor=new TextComponentMatcherEditor<AnalisisDeEntrada>(numeroInput,numeroFilterator);
		
		final EventList<MatcherEditor<AnalisisDeEntrada>> editors=new BasicEventList<MatcherEditor<AnalisisDeEntrada>>();
		editors.add(numeroEditor);
		
		final CompositeMatcherEditor<AnalisisDeEntrada> matcherEditor=new CompositeMatcherEditor<AnalisisDeEntrada>(editors);
		matcherEditor.setMode(CompositeMatcherEditor.AND);
		
		final FilterList<AnalisisDeEntrada> comsFiltrados=new FilterList<AnalisisDeEntrada>(coms,matcherEditor);
		
		final SortedList<AnalisisDeEntrada> sList=new SortedList<AnalisisDeEntrada>(comsFiltrados,null);
		final EventTableModel<AnalisisDeEntrada> tm=new EventTableModel<AnalisisDeEntrada>(sList,tf);
		
		comsGrid=ComponentUtils.getStandardTable();
		comsGrid.setModel(tm);
		
		selectionModel=new EventSelectionModel<AnalisisDeEntrada>(sList);
		
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		comsGrid.setSelectionModel(selectionModel);
		JScrollPane sp=new JScrollPane(comsGrid);
		return sp;
	}
	
	private EventList<AnalisisDet> cxpList;
	
	private JComponent buildCxpPanel(){		
		final String[] props={"ARTCLAVE","cantidad","costo","importe","netoMN","analisis.id","analisis.fecha","analisis.tc","analisis.clave"};
		final String[] cols={"ARTCLAVE","cantidad","costo","importe","netoMN","Analisis","Fecha","TC","Clave"};
		final TableFormat<AnalisisDet> tf=GlazedLists.tableFormat(AnalisisDet.class, props, cols);
		cxpList=GlazedLists.threadSafeList(new BasicEventList<AnalisisDet>());
		final EventTableModel<AnalisisDet> tm=new EventTableModel<AnalisisDet>(cxpList,tf);
		final JXTable cxpGrid=ComponentUtils.getStandardTable();
		cxpGrid.setModel(tm);
		final JComponent c=UIFactory.createTablePanel(cxpGrid);
		c.setPreferredSize(new Dimension(350,90));
		return c;
	}
	
	private EventList<SalidaDeHojas> maqHojList;
	
	private JComponent buildMaqHojPanel(){
		final String[] props={"origen.id","origen.costo","cantidad","precioPorKiloFlete"};
		final String[] cols={"Origen","Costo","Cantidad","Gastos"};
		final TableFormat<SalidaDeHojas> tf=GlazedLists.tableFormat(SalidaDeHojas.class, props, cols);
		maqHojList=GlazedLists.threadSafeList(new BasicEventList<SalidaDeHojas>());
		final EventTableModel<SalidaDeHojas> tm=new EventTableModel<SalidaDeHojas>(maqHojList,tf);
		final JXTable maqHojGrid=ComponentUtils.getStandardTable();
		maqHojGrid.setModel(tm);
		final JComponent c=UIFactory.createTablePanel(maqHojGrid);
		c.setPreferredSize(new Dimension(350,90));
		return c;
	}
	
	private EventList<SalidaDeBobinas> maqBobList;
	
	private JComponent buildMaqBobPanel(){
		final String[] props={"entrada.id","kilos","entrada.precioPorKilo","importe"};
		final String[] cols={"Entrada","Kilos","P Kilo","Importe"};
		final TableFormat<SalidaDeBobinas> tf=GlazedLists.tableFormat(SalidaDeBobinas.class, props, cols);
		maqBobList=GlazedLists.threadSafeList(new BasicEventList<SalidaDeBobinas>());
		final EventTableModel<SalidaDeBobinas> tm=new EventTableModel<SalidaDeBobinas>(maqBobList,tf);
		final JXTable maqBobGrid=ComponentUtils.getStandardTable();
		maqBobGrid.setModel(tm);
		final JComponent c=UIFactory.createTablePanel(maqBobGrid);
		c.setPreferredSize(new Dimension(350,90));
		return c;
	}
	
	private JComponent buildCostoPanel(){
		
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,r:50dlu, 2dlu " +
				"l:p,2dlu,r:50dlu"
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		final JLabel cantidad=DefaultComponentFactory.getInstance().createTitle("Cantidad");
		cantidad.setHorizontalAlignment(JLabel.CENTER);
		final JLabel costo=DefaultComponentFactory.getInstance().createTitle("Costo");
		costo.setHorizontalAlignment(JLabel.CENTER);
		final JLabel total=DefaultComponentFactory.getInstance().createTitle("Total");
		final JLabel costop=DefaultComponentFactory.getInstance().createTitle("Costo P.");
		
		
		builder.append("",cantidad);
		builder.append("",costo,true);
		//builder.append("",costo,true);
		
		builder.append("Entradas",new JLabel("0"));		
		builder.append("",new JLabel("N"),true);
		
		builder.append("Analizadas CxP",new JLabel("0"));
		builder.append("",new JLabel("N"),true);
		
		builder.append("Analizadas Maq (Hoj)",new JLabel("0"));
		builder.append("",new JLabel("N"),true);
		
		builder.append("Analisadas Maq (Bob)",new JLabel("0"));
		builder.append("",new JLabel("N"),true);
		builder.appendSeparator();
		builder.append(total,new JLabel("0"));
		builder.append("",new JLabel("N"),true);
		builder.appendSeparator();
		builder.append(costop,new JLabel());
		
		return builder.getPanel();
	}

	public JComponent getFilterPanel() {
		FormLayout layout=new FormLayout(
				"l:p,2dlu,R:p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();		
		builder.append("Por COM",new JTextField(20),true);
		JPanel	filtrosPanel= builder.getPanel();
		filtrosPanel.setOpaque(false);
		return filtrosPanel;
		
	}

	public Action[] getOperaciones() {
		Action[] actions=new Action[]{
			seleccionarPeriodo,
			seleccionarArticulo,
			loadData
		};
		return actions;
	}
	
	public void seleccionarPeriodo(){
		AbstractDialog dialog=Binder.createPeriodoSelector(model.getModel("periodo"));
		dialog.open();
	}
	
	public void seleccionarArticulo(){
		final ArticuloPicker piker=new ArticuloPicker(model.getModel("articulo"));
		piker.load();
		SXAbstractDialog dialog=new SXAbstractDialog("Catálogo de artículos"){			
			protected JComponent buildContent() {				
				final JPanel p=new JPanel(new BorderLayout());
				p.add(piker.getControl(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}			
		};
		dialog.open();
		System.out.println("seleccion: "+model.getArticulo());
		if(!dialog.hasBeenCanceled()){
			model.load();
		}
		piker.close();
	}
	
	public void load(){
		SwingWorker worker=new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				model.load();
				return "OK";
			}			
			protected void done() {
				comsGrid.packAll();
			}
			
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void select(final AnalisisDeEntrada e){
		
	}
	
	public void close(){
		//tab.putClientProperty(AnalisisSupport.SUPPORT_KEY, null);
		model.close();
	}
	
	private void updateHeader(){
		if(model.getArticulo()!=null){
			String p1="{0} ({1})";				
			header.setTitle(MessageFormat.format(p1, model.getArticulo().getDescripcion1(),model.getArticulo().getClave()));
			String p2="Periodo: {0}";
			header.setDescription(MessageFormat.format(p2, model.getPeriodo().toString()));
			
		}else{
			header.setTitle("Artículo por seleccionar");
			header.setDescription("Seleccione un articulo desde la barra de tareas");
		}

	}
	
	public class HeaderHandler implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {
			updateHeader();							
		}
		
	}

	
}
