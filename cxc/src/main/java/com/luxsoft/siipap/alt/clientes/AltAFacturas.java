package com.luxsoft.siipap.alt.clientes;

import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.FunctionList.Function;
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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.ActionLabel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.utils.CheckBoxSelector;
import com.luxsoft.siipap.cxc.utils.MatcherEditors;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.DockingUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.ventas.dao.DevolucionDao;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * Panel que presenta una serie de lista relacionadas con
 * facturas. En este caso de un mismo cliente
 * 
 * @author Ruben Cancino
 *
 */
public class AltAFacturas extends AbstractView{
	
	private RootWindow rootWindow;
	private ViewMap views;
	
	private EventList<VentaDet> source;
	private EventList<Venta> ventas;
	private FilterList<Venta> ventasFiltradas;	
	private EventList<VentaDet> partidas;
	
	private EventList<Pago> pagos;
	private EventList<NotasDeCreditoDet> notas;
	private EventList<DevolucionDet> devoluciones;
	
	private EventSelectionModel<Venta> seleccionVenta;
	private JXTable ventasGrid;
	
	private JTextField numeroField=new JTextField(10);
	private CheckBoxSelector<Venta> conSaldoMatcherEditor;
	private CheckBoxSelector<Venta> vencidasMatcherEditor;
	private CheckBoxSelector<Venta> porVencerMatcherEditor;
	
	private JXTable partidasGrid;
	private JXTable pagosGrid;
	private JXTable notasGrid;
	private JXTable devosGrid;
	
	private View ventasView;
	private View partidasView;
	private View notasView;
	private View pagosView;
	private View devosView;
	
	private final AltAModel model;
	
	
	
	public AltAFacturas(final AltAModel model) {		
		this.model = model;
	}

	private void initComponents(){
		initDockingWindows();
		views=new ViewMap();		
		rootWindow=DockingUtil.createRootWindow(views, true);
		DockingUtils.configRootWindow(rootWindow);
		createDefaultLayout();
	}
	
	public void createDefaultLayout(){
		SplitWindow sp0=new SplitWindow(false,.5f,notasView,pagosView);
		SplitWindow sp1=new SplitWindow(false,.33f,devosView,sp0);
		SplitWindow botWindow=new SplitWindow(true,.6f,partidasView,sp1);
		SplitWindow mainWindow=new SplitWindow(false,ventasView,botWindow);
		
		rootWindow.setWindow(mainWindow);
	}
	
	private View[] initDockingWindows(){
		ventasView=new View("Ventas",null,buildFacturasPanel());
		
		partidasView=new View("Partidas",null,buildPartidasPanel());
		notasView=new View("Notas",null,buildNotasPanel());		
		pagosView=new View("Pagos",null,buildPagosPanel());
		devosView=new View("Devoluciones",null,buildDevosPanel());
		View[] vr=new View[]{
				ventasView,
				partidasView,
				notasView,
				pagosView,
				devosView
				};
		decoreateView(vr);
		return vr;
		
	}
	
	private void decoreateView(final View... vistas){
		for(View v:vistas){	
			v.getWindowProperties()
			.setCloseEnabled(false);
		}
	}
	

	@Override
	protected JComponent buildContent() {
		initGlazedLists();
		initComponents();
		/**
		FormLayout layout=new FormLayout(
				"p,2dlu,p,2dlu,max(p;100dlu),2dlu,max(p;100dlu)",
				"t:p,2dlu,p,2dlu,p,2dlu,p"
				);		
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		
		builder.add(getFacturasFiltrosPanel(),cc.xy(1, 1));
		builder.add(buildFacturasPanel(),cc.xyw(1, 3, 4));		
		builder.add(buildPagosPanel(),cc.xy(3,5));
		builder.add(buildNotasPanel(),cc.xy(5, 5));
		JPanel p=builder.getPanel();
		return p;
		**/
		return rootWindow;
	}
	
	/**
	 * Inicializa GlazedList para ser usuado en el grid
	 *
	 */
	private void initGlazedLists(){
		//El origen 
		source=GlazedLists.threadSafeList(new BasicEventList<VentaDet>());
		
		//
		final Function<VentaDet, Venta> function=new Function<VentaDet, Venta>(){
			public Venta evaluate(VentaDet det) {				
				return det.getVenta();
			}			
		};
		
		final FunctionList<VentaDet, Venta> transformedVentas=new FunctionList<VentaDet, Venta>(source,function);
		Comparator<Venta> comparator=GlazedLists.beanPropertyComparator(Venta.class, "id");
		ventas=new UniqueList<Venta>(transformedVentas,comparator);
		
		conSaldoMatcherEditor=MatcherEditors.createFacturasPendientesSelector();
		//Vencidas
		vencidasMatcherEditor=MatcherEditors.createFacturasVencidasSelector();
		//Pendientes
		porVencerMatcherEditor=MatcherEditors.createFacturasPorVencerSelector();
		
		//Por numero
		final TextFilterator<Venta> numeroFilterator=GlazedLists.textFilterator(new String[]{"numero"});
		final TextComponentMatcherEditor<Venta> numeroEditor=new TextComponentMatcherEditor<Venta>(numeroField,numeroFilterator);
		
		final EventList<MatcherEditor<Venta>> editores=new BasicEventList<MatcherEditor<Venta>>();
		editores.add(conSaldoMatcherEditor);
		editores.add(vencidasMatcherEditor);
		editores.add(porVencerMatcherEditor);		
		editores.add(numeroEditor);
		final CompositeMatcherEditor<Venta> compositeEditor=new CompositeMatcherEditor<Venta>(editores);
		compositeEditor.setMode(CompositeMatcherEditor.AND);
		
		ventasFiltradas=new FilterList<Venta>(ventas,new ThreadedMatcherEditor<Venta>(compositeEditor));
		final Comparator<Venta> comp=GlazedLists.beanPropertyComparator(Venta.class, "atraso");
		sortedList=new SortedList<Venta>(ventasFiltradas,comp);		
		seleccionVenta = new EventSelectionModel<Venta>(sortedList);
		
		PartidasMatcherEditor partidasSelector=new PartidasMatcherEditor(seleccionVenta.getSelected());
		seleccionVenta.addListSelectionListener(partidasSelector);
		partidas=new FilterList<VentaDet>(source,partidasSelector);
		
		seleccionVenta.addListSelectionListener(new VentaSelector(seleccionVenta.getSelected()));
		pagos=GlazedLists.threadSafeList(new BasicEventList<Pago>());
		notas=GlazedLists.threadSafeList(new BasicEventList<NotasDeCreditoDet>());
		devoluciones=GlazedLists.threadSafeList(new BasicEventList<DevolucionDet>());
	}
	
	SortedList<Venta> sortedList;
	
	private JComponent buildFacturasPanel(){
		
		final JPanel fpanel=new JPanel(new BorderLayout(4,4));
		
		final String[] props={"id","sucursal","tipo","numero","numeroFiscal","fecha","vencimiento","total","saldo","atraso","credito.reprogramarPago"};
		final String[] labels={"Id","Sucursal","Tipo","Numero","No Fiscal","Fecha","Vencimiento","Total","Saldo","Atraso","F.Pago"};
		final TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class,props,labels);
		//final Comparator<Venta> comp=GlazedLists.beanPropertyComparator(Venta.class, "atraso");
		//SortedList<Venta> sortedList=new SortedList<Venta>(ventasFiltradas,comp);
		final EventTableModel<Venta> tm=new EventTableModel<Venta>(sortedList,tf);
		//seleccionVenta = new EventSelectionModel<Venta>(sortedList);
		ventasGrid=ComponentUtils.getStandardTable();		
		ventasGrid.setModel(tm);
		ventasGrid.setSelectionModel(seleccionVenta);		
		ComponentUtils.decorateActions(ventasGrid);
		new TableComparatorChooser<Venta>(ventasGrid,sortedList,true);
		JScrollPane sp=new JScrollPane(ventasGrid);		 
		
		fpanel.add(sp,BorderLayout.CENTER);
		fpanel.add(getFacturasFiltrosPanel(),BorderLayout.NORTH);
		
		return fpanel;
	}
	
	public JComponent getFacturasFiltrosPanel(){
		FormLayout layout=new FormLayout(
				"l:p,2dlu,l:p, 2dlu," +
				"l:p,2dlu,l:p, 2dlu," +
				"l:p,2dlu,l:p, 2dlu," +
				"l:p,2dlu,l:p, 2dlu," +
				"l:p,2dlu,l:p","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		//builder.setDefaultDialogBorder();
		//builder.appendSeparator("Filtros");
		builder.append("Factura",numeroField);		
		builder.append("Con Saldo",conSaldoMatcherEditor.getBox());
		builder.append("Vencidas ",vencidasMatcherEditor.getBox());
		builder.append("Por vencer",porVencerMatcherEditor.getBox());
		final ActionLabel al=new ActionLabel("10/01/2007");
		//final JLabel pl=DefaultComponentFactory.getInstance().createTitle("01/01/2007");
		builder.append("Periodo",al);
		
		JPanel filtrosPanel= builder.getPanel();
		filtrosPanel.setOpaque(false);
		return filtrosPanel;
	}
	
	private JComponent buildPartidasPanel(){
		final String[] props={"numero","clave","descripcion","cantidad","devueltos","precioFacturado","importeNeto"};
		final String[] labels={"Fac","Articulo","Descripcion","Cantidad","Devueltos","Precio","Importe"};
		final TableFormat<VentaDet> tf=GlazedLists.tableFormat(VentaDet.class,props,labels);
		final EventTableModel<VentaDet> tm=new EventTableModel<VentaDet>(partidas,tf);
		partidasGrid=ComponentUtils.getStandardTable();
		partidasGrid.setModel(tm);
		final JScrollPane sp=new JScrollPane(partidasGrid);
		return sp;
		
	}
	
	private JComponent buildPagosPanel(){
		pagosGrid=ComponentUtils.getStandardTable();
		final String[] props={"id","numero","nota","fecha","importe"};
		final String[] cols={"Id","Factura","Nota.C","Fecha","Importe"};
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class, props,cols);
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(pagos,tf);
		pagosGrid.setModel(tm);
		pagosGrid.setColumnControlVisible(false);		
		JScrollPane sp=new JScrollPane(pagosGrid);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		return sp;
	}
	
	private JComponent buildNotasPanel(){
		notasGrid=ComponentUtils.getStandardTable();
		final String[] props={"id","numero","tipo","serie","factura.numero","nota.fecha","descuento","importe"};
		final String[] cols={"Id","No","T","S","Fac","Fecha","Desc","Importe"};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class, props,cols);
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(notas,tf);
		notasGrid.setModel(tm);
		notasGrid.setColumnControlVisible(false);		
		//JScrollPane sp=new JScrollPane(notasGrid);
		//JScrollPane sp=(JScrollPane)UIFactory.createTablePanel(notasGrid);
		//sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
		return UIFactory.createTablePanel(notasGrid);
	}
	
	private JComponent buildDevosPanel(){
		devosGrid=ComponentUtils.getStandardTable();
		final String[] props={"id","sucursal","numero","fecha","ventaDet.id","ventaDet.numero","nota.numero","clave","cantidad"};
		final String[] cols={"Id","Suc","RMD","Fecha","V.Id","Factura","Nota","Articulo","Cantidad"};
		final TableFormat<DevolucionDet> tf=GlazedLists.tableFormat(DevolucionDet.class, props,cols);
		final EventTableModel<DevolucionDet> tm=new EventTableModel<DevolucionDet>(devoluciones,tf);
		devosGrid.setModel(tm);
		devosGrid.setColumnControlVisible(false);		
		JScrollPane sp=new JScrollPane(devosGrid);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		return sp;
	}
	
	public void load(){
		source.clear();		
		SwingWorker<List<VentaDet>, String> worker=new SwingWorker<List<VentaDet>, String>(){			
			protected List<VentaDet> doInBackground() throws Exception {				
				return ServiceLocator.getVentasManager()
				.getVentasDao()
				.buscarVentasDetPorCliente(getCliente().getClave(), getPeriodo());
			}			
			protected void done() {
				try {
					List<VentaDet> ventas=get();					
					source.addAll(ventas);
					logger.debug("Cargando informacion.......");
					partidasGrid.packAll();
				} catch (Exception e) {
					MessageUtils.showError("Error cargando datos", e);
				}
			}			
		};
		worker.execute();
	}
	
	private Cliente getCliente(){
		return (Cliente)model.getValue("cliente");
	}
	private Periodo getPeriodo(){
		return (Periodo)model.getValue("periodo");
	}
	
	private class PartidasMatcherEditor extends AbstractMatcherEditor<VentaDet> implements ListSelectionListener{
		
		private final EventList<Venta> selected;
		
		public PartidasMatcherEditor(final EventList<Venta> selected){
			this.selected=selected;
		}

		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()){
				if(selected.isEmpty())
					fireMatchNone();
				else{					
					fireChanged(new PartidaMatcher());
				}
			}
		}
		
		private class PartidaMatcher implements Matcher<VentaDet>{
			public boolean matches(VentaDet item) {
				for(Venta v:selected){					
					if(v.getId()==item.getVenta().getId())
						return true;
				}
				return false;
			}			
		}
		
	}
	
	private class VentaSelector implements ListSelectionListener{
		
		private final EventList<Venta> selected;
		
		public VentaSelector(final EventList<Venta> selected){
			this.selected=selected;
		}

		public void valueChanged(ListSelectionEvent e) {
			pagos.clear();
			notas.clear();
			devoluciones.clear();
			if(!e.getValueIsAdjusting()){
				SwingWorker<String, List> worker=new SwingWorker<String, List>(){
					
					protected String doInBackground() throws Exception {
						for(Venta v: selected){
							List<List> data=new ArrayList<List>();
							data.add(0,getPagos(v));
							data.add(1,getNotas(v));
							data.add(2,getDevos(v));
							process(data);
							
						}						
						return "DONE";
					}					
					protected void process(List<List> datos) {
						
						pagos.addAll(datos.get(0));						
						notas.addAll(datos.get(1));
						devoluciones.addAll(datos.get(2));
					}
				};
				worker.execute();
			}
			
		}
		
		private List<Pago> getPagos(final Venta v){
			return ServiceLocator.getCXCManager().buscarPagos(v);
		}
		private List<NotasDeCreditoDet> getNotas(final Venta v){
			return ServiceLocator.getCXCManager().buscarNotas(v);
		}
		private List<DevolucionDet> getDevos(final Venta v){
			DevolucionDao dao=(DevolucionDao)ServiceLocator.getDaoContext().getBean("devolucionDao");
			return dao.buscarDevoluciones(v);
		}
		
	}
	
	public static void main(String[] args) {
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				AltAModel model=new AltAModel();
				AltAFacturas fac=new AltAFacturas(model);
				return fac.getContent();
			}
			
		};
		dialog.open();
	}

}
