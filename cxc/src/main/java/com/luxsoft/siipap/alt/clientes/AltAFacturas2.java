package com.luxsoft.siipap.alt.clientes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

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
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
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
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
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
public class AltAFacturas2 extends AbstractView{
	
	
	private EventList<VentaDet> source;
	private EventList<Venta> ventas;
	private FilterList<Venta> ventasFiltradas;	
	private EventList<VentaDet> partidas;
	private SortedList<Venta> sortedList;
	
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
	private JTable pagosGrid;
	private JTable notasGrid;
	private JTable devosGrid;
	
	private PropertyChangeListener workerListener;
	
	private ActionLabel actionLabel;
	
	//private final AltAModel model;
	private final AltA altA;
	
	public AltAFacturas2(final AltA alta) {		
		this.altA=alta;
	}

	@Override
	protected JComponent buildContent() {
		initGlazedLists();		
		FormLayout layout=new FormLayout(
				"p,2dlu,p:g",
				"min(p;150dlu),2dlu,f:p:g"
				);		
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		
		builder.add(buildFacturasPanel(),cc.xyw(1, 1,3));
		builder.add(buildPartidasPanel(),cc.xy(1, 3));		
		builder.add(buildCargosAbonosPanel(),cc.xy(3,3));
		JPanel p=builder.getPanel();
		return p;
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
	
	
	
	private JComponent buildFacturasPanel(){
		
		final JPanel fpanel=new JPanel(new BorderLayout(4,4));		
		final String[] props={"id","sucursal","tipo","numero","numeroFiscal","fecha","vencimiento","total","saldo","atraso","credito.reprogramarPago","importeCortes"};
		final String[] labels={"Id","Suc","Tipo","Numero","No Fiscal","Fecha","Vencimiento","Total","Saldo","Atraso","F.Pago","Cortes"};
		final TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class,props,labels);
		final EventTableModel<Venta> tm=new EventTableModel<Venta>(sortedList,tf);
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
	
	private void setPeriodo(){		
		actionLabel.setText(altA.getModel().getPeriodo().toString());
	}
	
	public JComponent getFacturasFiltrosPanel(){
		FormLayout layout=new FormLayout(
				"l:p,2dlu,l:p, 2dlu," +
				"l:p,2dlu,l:p, 2dlu," +
				"l:p,2dlu,l:p, 2dlu," +
				"l:p,2dlu,l:p, 2dlu," +
				"r:p:g,2dlu,r:p","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Factura",numeroField);		
		builder.append("Con Saldo",conSaldoMatcherEditor.getBox());
		builder.append("Vencidas ",vencidasMatcherEditor.getBox());
		builder.append("Por vencer",porVencerMatcherEditor.getBox());
		
		builder.append("Periodo:  ",getPeriodoLabel());		
		JPanel filtrosPanel= builder.getPanel();
		filtrosPanel.setOpaque(false);
		return filtrosPanel;
	}
	
	private JComponent buildPartidasPanel(){
		final String[] props={"numero","clave","descripcion","cantidad","devueltos","precioFacturado","importeNeto"};
		final String[] labels={"Fac","Articulo","Desc","Cantidad","Dev","Precio","Importe"};
		final TableFormat<VentaDet> tf=GlazedLists.tableFormat(VentaDet.class,props,labels);
		final EventTableModel<VentaDet> tm=new EventTableModel<VentaDet>(partidas,tf);
		partidasGrid=ComponentUtils.getStandardTable();
		partidasGrid.setModel(tm);
		partidasGrid.packAll();
		final JScrollPane sp=new JScrollPane(partidasGrid);
		sp.setPreferredSize(new Dimension(500,150));
		JPanel p=new JPanel(new BorderLayout(2,3));
		p.add(DefaultComponentFactory.getInstance().createTitle("Partidas"),BorderLayout.NORTH);
		p.add(sp,BorderLayout.CENTER);
		return p;
		
	}

	private JComponent buildCargosAbonosPanel(){
		JPanel panel=new JPanel(new VerticalLayout(3));
		panel.add(DefaultComponentFactory.getInstance().createTitle("Devoluciones"));
		panel.add(buildDevosPanel());
		panel.add(DefaultComponentFactory.getInstance().createTitle("Notas"));
		panel.add(buildNotasPanel());
		panel.add(DefaultComponentFactory.getInstance().createTitle("Pagos"));
		panel.add(buildPagosPanel());
		return panel;
	}
	
	private JComponent buildPagosPanel(){		
		final String[] props={"numero","nota","fecha","importe"};
		final String[] cols={"Factura","Nota.C","Fecha","Importe"};
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class, props,cols);
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(pagos,tf);
		pagosGrid=new JTable(tm);
		JComponent c=UIFactory.createTablePanel(pagosGrid);
		c.setPreferredSize(new Dimension(350,70));
		return c;
	}
	
	private JComponent buildNotasPanel(){		
		final String[] props={"numero","tipo","serie","factura.numero","nota.fecha","descuento","importe","nota.saldo"};
		final String[] cols={"No","T","S","Fac","Fecha","Desc","Importe","Saldo"};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class, props,cols);
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(notas,tf);		
		notasGrid=new JTable(tm);
		notasGrid.getColumn("Importe").setPreferredWidth(400);
		notasGrid.getColumn("Saldo").setPreferredWidth(400);
		notasGrid.getColumn("Desc").setPreferredWidth(100);
		notasGrid.getColumn("Fecha").setPreferredWidth(200);
		notasGrid.getColumn("Fac").setPreferredWidth(200);
		notasGrid.getColumn("No").setPreferredWidth(200);
		notasGrid.getColumn("T").setPreferredWidth(50);
		notasGrid.getColumn("S").setPreferredWidth(50);		
		JComponent c=UIFactory.createTablePanel(notasGrid);
		c.setPreferredSize(new Dimension(350,70));
		return c;
		
	}
	
	private JComponent buildDevosPanel(){		
		final String[] props={"id","numero","fecha","ventaDet.numero","nota.numero"};
		final String[] cols={"Id","Rmd","Fecha","Fac","Nota"};
		final TableFormat<DevolucionDet> tf=GlazedLists.tableFormat(DevolucionDet.class, props,cols);
		final EventTableModel<DevolucionDet> tm=new EventTableModel<DevolucionDet>(devoluciones,tf);
		devosGrid=new JTable(tm);
		JComponent c=UIFactory.createTablePanel(devosGrid);
		c.setPreferredSize(new Dimension(350,70));
		return c;
	}
	
	private ActionLabel getPeriodoLabel(){
		if(actionLabel==null){
			actionLabel=new ActionLabel(new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					seleccionarPeriodo();
				}			
			});
			setPeriodo();		
			
		}
		return actionLabel;
	}
	
	public void seleccionarPeriodo(){
		AbstractDialog dialog=Binder.createPeriodoSelector(
				altA.getModel().getBufferedModel("periodo"),altA.getWindow().getFrame());
		
		dialog.open();
		
		if(!dialog.hasBeenCanceled()){
			altA.getModel().triggerCommit();
			setPeriodo();
			load();
		}
	}
	
	public void load(){
		if(altA.getModel().getCliente()==null)
			return;
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
					ventasGrid.packAll();
					partidasGrid.packAll();					
				} catch (Exception e) {
					MessageUtils.showError("Error cargando datos", e);
				}
			}			
		};
		
		if(workerListener!=null)
			worker.addPropertyChangeListener(workerListener);
		worker.execute();
	}
	
	private Cliente getCliente(){
		return (Cliente)altA.getModel().getValue("cliente");
	}
	private Periodo getPeriodo(){
		return (Periodo)altA.getModel().getValue("periodo");
	}
	
	public void registerWorkerListener(final PropertyChangeListener listener){
		this.workerListener=listener;
	}
	
	/**
	 * MatcherEdutir con implementacion de ListSelectionListener para filtrar 
	 * VentasDet a partir de Ventas
	 * 
	 * @author Ruben Cancino
	 *
	 */
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
	
	/**
	 * ListSelectionListener que carga las notas,pagos y devoluciones realcionadas
	 * con un grupo de ventas 
	 * 
	 * @author Ruben Cancino
	 *
	 */
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
					@SuppressWarnings("unchecked")
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
		SWExtUIManager.setup();
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				AltAModel model=new AltAModel();
				AltA alta=new AltA(model);				
				AltAFacturas2 fac=new AltAFacturas2(alta);
				return fac.getContent();
			}
			
		};
		dialog.open();
	}

}
