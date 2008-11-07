package com.luxsoft.siipap.em.importar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.DockingUtils;
import com.luxsoft.siipap.ventas.domain.Venta;


@SuppressWarnings("serial")
public class VentasReplicaPanel extends JPanel{
	
	private final VentasReplicaModel model;
	
	
	public VentasReplicaPanel(){
		super(new BorderLayout());
		this.model=new VentasReplicaModel(){
			public void afterDataLoaded() {
				dataLoaded();
			}
		};
		setPreferredSize(new Dimension(900,900));
		initComponents();
		
	}
	
	private void initComponents(){
		add(getToolbar(),BorderLayout.NORTH);
		add(buildDocumentPanel(),BorderLayout.CENTER);
	}
	
	
	private JComponent buildDocumentPanel(){
		JSplitPane sp=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setTopComponent(buildTopPanel());
		sp.setBottomComponent(buildGridPanel());
		sp.setResizeWeight(.5);
		sp.setOneTouchExpandable(true);
		sp.setBorder(null);
		return sp;
	}
	
	private JComponent buildTopPanel(){
		FormLayout layout=new FormLayout("l:p,3dlu,l:p","t:p");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(buildFormPanel(),cc.xy(1, 1));
		builder.add(buildFilterPanel(),cc.xy(3, 1));
		return builder.getPanel();
	}
	
	private JComponent buildFormPanel(){
		FormLayout layout=new FormLayout("p,2dlu,max(p;60dlu),3dlu,p,2dlu,p","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.appendTitle("Configuracion");
		builder.nextLine();
		builder.append("Fecha",Binder.createDateComponent(model.getModel("dia")));
		builder.nextLine();
		JTextField odbc=BasicComponentFactory.createTextField(model.getModel("odbc"));
		odbc.setEditable(false);
		builder.append("ODBC",odbc);
		return builder.getPanel();
	}
	
	private JTextField clienteField=new JTextField(10);
	private JTextField sucursalField=new JTextField(10);
	private JTextField numeroField=new JTextField(10);
	private JTextField serieField=new JTextField(10);
	private JTextField tipoField=new JTextField(10);
	
	
	private JComponent buildFilterPanel(){
		FormLayout layout=new FormLayout("p,2dlu,f:max(p;100dlu),","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.appendSeparator("Filtros");
		builder.append("Sucursal",sucursalField);
		builder.append("Cliente",clienteField);
		builder.append("Factura",numeroField);
		builder.append("Serie",serieField);
		builder.append("Tipo",tipoField);
		 
		builder.setDefaultDialogBorder();
		return builder.getPanel();
	}
	
	
	private RootWindow rootWindow;
	
	private JComponent buildGridPanel(){
		rootWindow=new RootWindow(new ViewMap());
		DockingUtils.configRootWindow(rootWindow);
		DockingUtil.addWindow(createVentasFaltantesView(), rootWindow);
		DockingUtil.addWindow(createVentasSobrantesView(), rootWindow);
		DockingUtil.addWindow(createVentasSiipapWindView(), rootWindow);
		DockingUtil.addWindow(createVentasSiipapDbfView(), rootWindow);
		
		return rootWindow;
	}
	
	private JXTable winGrid;
	
	
	private View createVentasSiipapWindView(){
		winGrid=ComponentUtils.getStandardTable();
		EventList<Venta> source=decorate(model.getVentasWin());
		SortedList<Venta> sorted=new SortedList<Venta>(source,null);
		EventTableModel<Venta> tm=new EventTableModel<Venta>(sorted,getVentasTableFormat());
		winGrid.setModel(tm);
		new TableComparatorChooser(winGrid,sorted,true);
		JScrollPane sp=new JScrollPane(winGrid);
		sp.setBorder(null);
		View v=new View("Win",null,sp);
		return v;
	}
	
	private JXTable dbfGrid;
	
	private View createVentasSiipapDbfView(){
		dbfGrid=ComponentUtils.getStandardTable();
		EventList<Venta> source=decorate(model.getVentasDbf());
		SortedList<Venta> sorted=new SortedList<Venta>(source,null);
		EventTableModel<Venta> tm=new EventTableModel<Venta>(sorted,getVentasTableFormat());
		dbfGrid.setModel(tm);
		new TableComparatorChooser(dbfGrid,sorted,true);
		JScrollPane sp=new JScrollPane(dbfGrid);
		sp.setBorder(null);
		View v=new View("Siipap DBF",null,sp);
		return v;
	}
	
	private JXTable faltantesGrid;
	
	private View createVentasFaltantesView(){
		faltantesGrid=ComponentUtils.getStandardTable();
		EventList<Venta> source=decorate(model.getFaltantes());
		SortedList<Venta> sorted=new SortedList<Venta>(source,null);
		EventTableModel<Venta> tm=new EventTableModel<Venta>(sorted,getVentasTableFormat());
		faltantesGrid.setModel(tm);
		new TableComparatorChooser(faltantesGrid,sorted,true);
		JScrollPane sp=new JScrollPane(faltantesGrid);
		sp.setBorder(null);
		View v=new View("Faltantes SW",null,sp);
		return v;
	}
	
	private JXTable sobrantesGrid;
	
	private View createVentasSobrantesView(){
		sobrantesGrid=ComponentUtils.getStandardTable();
		EventList<Venta> source=decorate(model.getSobrantes());
		SortedList<Venta> sorted=new SortedList<Venta>(source,null);
		EventTableModel<Venta> tm=new EventTableModel<Venta>(sorted,getVentasTableFormat());
		sobrantesGrid.setModel(tm);
		new TableComparatorChooser(sobrantesGrid,sorted,true);
		JScrollPane sp=new JScrollPane(sobrantesGrid);
		sp.setBorder(null);
		View v=new View("Sobrantes SW",null,sp);
		return v;
	}
	
	private TableFormat<Venta> getVentasTableFormat(){
		String[] props={"id","sucursal","clave","nombre","fecha","numero","serie","tipo"};
		String[] names={"Id","Suc","Cliente","Nombre","Fecha","Fac","S","T"};
		return GlazedLists.tableFormat(Venta.class, props,names);
	}
	
	EventList<MatcherEditor<Venta>> editors;
	MatcherEditor<Venta> editor;
	
	private void initEditors(){
		editors=new BasicEventList<MatcherEditor<Venta>>();
		editor=new CompositeMatcherEditor<Venta>(editors);
		addTextFilterator(sucursalField, "sucursal");
		addTextFilterator(clienteField, "clave","nombre");
		addTextFilterator(numeroField, "numero");
		addTextFilterator(serieField, "serie");
		addTextFilterator(tipoField, "tipo");
	}
	
	@SuppressWarnings("unchecked")
	private void addTextFilterator(final JTextField input,String...props){
		TextFilterator tf=GlazedLists.textFilterator(props);
		editors.add(new TextComponentMatcherEditor<Venta>(input,tf));
	}
	
	private EventList<Venta> decorate(final EventList<Venta> source){
		if(editor==null)
			initEditors();
		final FilterList<Venta> filterList=new FilterList<Venta>(source,editor);
		return filterList;
	}
 	
	private JToolBar toolBar;
	
	public JToolBar getToolbar(){
		if(toolBar==null){
			ToolBarBuilder builder=new ToolBarBuilder();
			builder.add(CommandUtils.createRefreshAction(this, "updateData"));
			toolBar=builder.getToolBar();
		}
		return toolBar;
	}
	
	public void updateData(){
		model.loadData();
	}
	
	private void dataLoaded(){
		winGrid.packAll();
	}

}
