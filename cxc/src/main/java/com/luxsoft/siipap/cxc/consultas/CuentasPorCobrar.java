package com.luxsoft.siipap.cxc.consultas;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXStatusBar;
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
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.utils.CheckBoxSelector;
import com.luxsoft.siipap.cxc.utils.FacturasPorFechaSelector;
import com.luxsoft.siipap.cxc.utils.MatcherEditors;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;
import com.luxsoft.siipap.swing.views2.DefaultTaskView;
import com.luxsoft.siipap.swing.views2.InternalTaskTab;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

public class CuentasPorCobrar extends DefaultTaskView{
	
	
	private CxCView cxcView;
	
	
	
	private void showCxC(){
		if(cxcView==null){
			cxcView=new CxCView();
			InternalTaskTab tab=createInternalTaskTab(cxcView);
			addTab(tab);
		}
	}
	
	@Override
	public void close() {		
		super.close();
	}

	@Override
	public void open() {		
		super.open();
		showCxC();
	}


	public class CxCView extends AbstractInternalTaskView{
		
		private EventList<Venta> source;		
		private EventList<Venta> filterList;
		private EventSelectionModel<Venta> selectionModel;
		private JXTable grid;
		
		private Action verFactura;
		private Action loadAction;
		private Action recalcularAction;
		private Action recalcularNotasAction;
		
		private JTextField numeroField;//=new JTextField(20);
		private CheckBoxSelector<Venta> paraDescuentoEditor;
		private CheckBoxSelector<Venta> conSaldoMatcherEditor;
		private CheckBoxSelector<Venta> vencidasMatcherEditor;
		private CheckBoxSelector<Venta> porVencerMatcherEditor;
		private CheckBoxSelector<Venta> pagadasEditor;
		private FacturasPorFechaSelector facturasPorFecha;
		private JComponent filterPanel;
		private JXBusyLabel blabel;
		private JProgressBar progress;
		
		private Totales totales;
		
		public CxCView(){
			setTitle("Facturas");
		}
		
		private void initActions(){
			verFactura=new DispatchingAction(this,"verFactura");
			CommandUtils.configAction(verFactura, "FacturasPorCliente.verFactura", "");
			loadAction=CommandUtils.createLoadAction(this, "load");
		}

		public JComponent getControl() {
			initActions();
			JPanel panel=new JPanel(new BorderLayout());
			panel.add(buildHeader(),BorderLayout.NORTH);
			panel.add(buildGridPanel(),BorderLayout.CENTER);
			panel.add(buildStatus(),BorderLayout.SOUTH);
			return panel;
		}
		
		private JComponent buildGridPanel(){
			initGlazedLists();			
			final String[] props={"id","clave","nombre","sucursal","tipo","numero","numeroFiscal","fecha","vencimiento","atraso","credito.reprogramarPago","total","descuento","descuentos","bonificaciones","pagos","saldo"};
			final String[] labels={"Id","Cliente","Nombre","Sucursal","Tipo","Numero","No Fiscal","Fecha","Vencimiento","Atraso","F.Pago","Total","Desc","Desc (A)","Bonificaciones","Pagos","Saldo"};
			final TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class,props,labels);
			final Comparator<Venta> comp=GlazedLists.beanPropertyComparator(Venta.class, "atraso");
			final SortedList<Venta> sortedList=new SortedList<Venta>(filterList,comp);
			final EventTableModel<Venta> tm=new EventTableModel<Venta>(sortedList,tf);
			selectionModel = new EventSelectionModel<Venta>(sortedList);
			grid=ComponentUtils.getStandardTable();		
			grid.setModel(tm);
			grid.setSelectionModel(selectionModel);
			ComponentUtils.addEnterAction(grid, verFactura);
			grid.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()==2){
						verFactura();
					}
				}
			});		
			//ComponentUtils.decorateActions(grid);
			new TableComparatorChooser<Venta>(grid,sortedList,true);
			ComponentUtils.decorateActions(grid);
			return new JScrollPane(grid);
		}
		
		private JComponent buildHeader(){
			Header h=new Header("Cuentas por Cobrar",getDescMessage());
			return h.getHeader();
		}
		
		private JComponent buildStatus(){
			final JXStatusBar status=new JXStatusBar();
			blabel=new JXBusyLabel();
			progress=new JProgressBar(1,100);
			progress.setStringPainted(true);
			status.add(blabel);
			status.add(progress);
			return status;
		}
		
		public JTextField getPorNumeroField(){
			if(numeroField==null){
				numeroField=new JTextField();
				numeroField.setColumns(10);				
			}
			return numeroField;
		}
		
		public JComponent getFiltrosPanel(){
			if(filterPanel==null){
				FormLayout layout=new FormLayout(
						"l:p,2dlu,l:max(p;100dlu)","");
				DefaultFormBuilder builder=new DefaultFormBuilder(layout);			
				builder.append("Factura",getPorNumeroField(),true);
				builder.append("Fecha de Pago",facturasPorFecha.getControl(),true);
				builder.append("Con Saldo",conSaldoMatcherEditor.getBox(),true);
				builder.append("Vencidas ",vencidasMatcherEditor.getBox(),true);
				builder.append("Por vencer",porVencerMatcherEditor.getBox(),true);
				builder.append("Pagadas",pagadasEditor.getBox(),true);
				builder.append("Para Descuento",paraDescuentoEditor.getBox(),true);			
				filterPanel= builder.getPanel();			
				filterPanel.setOpaque(false);			
			}
			return filterPanel;		
		}
		
		/**
		 * Inicializa GlazedList para ser usuado en el grid
		 *
		 */
		@SuppressWarnings("unchecked")
		private void initGlazedLists(){
			 
			/** Encadenamiento de listas en base a MatcherEditors especializados  **/
			// Origen o lista base
			source=GlazedLists.threadSafeList(new BasicEventList<Venta>());
			
			// Creamos los MatcherEditors adecuados
			paraDescuentoEditor=MatcherEditors.createFacturasParaNCDescuento();
			conSaldoMatcherEditor=MatcherEditors.createFacturasPendientesSelector();		
			vencidasMatcherEditor=MatcherEditors.createFacturasVencidasSelector();		
			porVencerMatcherEditor=MatcherEditors.createFacturasPorVencerSelector();		
			pagadasEditor=MatcherEditors.createFacturasPagadasSelector();
			facturasPorFecha=MatcherEditors.createFacturasPorFechaSelector("credito.reprogramarPago");		
			final TextFilterator<Venta> numeroFilterator=GlazedLists.textFilterator(new String[]{"numero"});
			final TextComponentMatcherEditor<Venta> numeroEditor=new TextComponentMatcherEditor<Venta>(getPorNumeroField(),numeroFilterator);
			
			// Aplicamos los editores en cascada con ayuda de CompositeMatcherEditor
			
			final EventList<MatcherEditor<Venta>> editores=new BasicEventList<MatcherEditor<Venta>>();
			editores.add(paraDescuentoEditor);
			editores.add(conSaldoMatcherEditor);
			editores.add(vencidasMatcherEditor);
			editores.add(porVencerMatcherEditor);		
			editores.add(pagadasEditor);
			editores.add(numeroEditor);
			editores.add(facturasPorFecha);
			
			final CompositeMatcherEditor<Venta> compositeEditor=new CompositeMatcherEditor<Venta>(editores);
			compositeEditor.setMode(CompositeMatcherEditor.AND);
			//Lista final obtenida
			filterList=new FilterList<Venta>(source,new ThreadedMatcherEditor<Venta>(compositeEditor));		
			totales=new Totales(filterList);
		}
		
		private String getDescMessage(){
			return "Al "+new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		}
		
		public void installDetallesPanel(JXTaskPane detalle) {
			detalle.add(totales.getPanel());
			
		}

		public void installFiltrosPanel(JXTaskPane filtros) {
			filtros.add(getFiltrosPanel());
		}
		
		public void verFactura(){
			
		}
		
		/**
		 * Recalcula la provision de todas las ventas 
		 *
		 */
		public void recalcularVentas(){
			if(MessageUtils.showConfirmationMessage("Este proceso recalcula los datos de las ventas a credito pendientes de pago" +
					"\npor lo que puede tardar varios minutos, Desea proceder?", "Recalcular ventas")){
				blabel.setBusy(true);
				source.clear();
				SwingWorker<String, Venta> worker=new SwingWorker<String, Venta>(){					
					 
					protected String doInBackground() throws Exception {
						final List<Long> ids=getManager().getListaDeVentasACreditoConSaldo();	
						final Set<Long> set=new TreeSet<Long>();
						set.addAll(ids);
						int total=set.size();
						blabel.setText("Ventas a procesar :"+total);
						int index=0;
						for(Long id:set){
							getManager().actualizarVenta(id);
							Venta venta=getManager().getVentasDao().buscarPorId(id);
							getManager().refresh(venta);
							publish(venta);							
							setProgress(100 * index / total);
							index++;
						}
						//int total=ids.size();
						/**
						for(int index=0;index<total;index++){						
							Venta venta=getManager().getVentasDao().buscarPorId(ids.get(index));
							getManager().actualizarVenta(venta);
							publish(venta);
							setProgress(100 * index / total);
						}
						*/
						return "OK";
						
					}
					
					protected void process(List<Venta> ventas) {
						for(Venta v:ventas){
							source.add(v);
						}
					}
					
					protected void done() {
						grid.packAll();
						blabel.setBusy(false);
					}					
				};
				worker.addPropertyChangeListener(new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getPropertyName().equals("state") && SwingWorker.StateValue.DONE == evt.getNewValue()){
							progress.setValue(0);
						}else if(evt.getPropertyName().equals("progress")){							
							progress.setValue((Integer)evt.getNewValue());
						}
					}					
				});
				worker.execute();
			}
			
		}
		
		public void recalcularCargos(){
			SwingWorker worker=new SwingWorker(){				
				protected Object doInBackground() throws Exception {
					ServiceLocator.getNotasManager().actualizarRevision();
					return null;
				}				
			};
			TaskUtils.executeSwingWorker(worker);
		}
		
		@Override
		public void instalOperacionesAction(JXTaskPane operaciones) {
			operaciones.add(loadAction);
			if(recalcularAction==null){
				recalcularAction=new DispatchingAction(this,"recalcularVentas");
				CommandUtils.configAction(recalcularAction, CXCActions.ActualizarVentas.getId(), "");				
			}
			if(recalcularNotasAction==null){
				recalcularNotasAction=new DispatchingAction(this,"recalcularCargos");
				CommandUtils.configAction(recalcularNotasAction, CXCActions.ActualizarCargos.getId(), "");
			}
			operaciones.add(recalcularAction);
			operaciones.add(recalcularNotasAction);
		}

		public void load(){
			LoadWorker worker=new LoadWorker(source);
			TaskUtils.executeSwingWorker(worker);
		}
		
		protected VentasManager getManager(){
			return ServiceLocator.getVentasManager();
		}
		
		/**
		 * Panel para registrar los totales
		 * 
		 * @author Ruben Cancino
		 *
		 */
		protected class Totales implements ListEventListener<Venta>{
			
			private JPanel control;
			private final EventList<Venta> source;
			private JFormattedTextField[] importes;
			
			public Totales(final EventList<Venta> ventas){			
				ventas.addListEventListener(GlazedLists.weakReferenceProxy(ventas, this));
				this.source=ventas;
				initComponents();
			}
			
			private void initComponents(){
				importes=new JFormattedTextField[4];
				importes[0]=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());			
				importes[1]=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
				importes[2]=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
				importes[3]=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
				for(JFormattedTextField tf:importes){
					tf.setEnabled(false);
					tf.setFocusable(false);
					//tf.setBorder(null);
				}
			}
			
			public JPanel getPanel(){
				if(control==null){
					final FormLayout layout=new FormLayout(
							"p,2dlu,f:p:g", "");
					final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
					builder.setDefaultDialogBorder();
					builder.append(DefaultComponentFactory.getInstance().createTitle("Venta Bruta "), importes[0]);
					builder.nextLine();
					builder.append(DefaultComponentFactory.getInstance().createTitle("Descuentos "), importes[1]);
					builder.nextLine();
					builder.append(DefaultComponentFactory.getInstance().createTitle("Pagos "), importes[2]);
					builder.nextLine();
					builder.append(DefaultComponentFactory.getInstance().createTitle("Saldo "), importes[3]);
					builder.nextLine();
					builder.getPanel().setOpaque(false);
					control=builder.getPanel();
				}
				return control;
			}
			
			private void updateTotales(){
				BigDecimal neta=BigDecimal.ZERO;
				BigDecimal desc=BigDecimal.ZERO;
				BigDecimal pago=BigDecimal.ZERO;
				BigDecimal saldo=BigDecimal.ZERO;
				
				for(Venta v:source){				
					neta=neta.add(v.getTotal().amount());
					desc=desc.add(BigDecimal.valueOf(v.getDescuentos()));
					pago=pago.add(BigDecimal.valueOf(v.getPagos()));
					saldo=saldo.add(v.getSaldo());
				}
				importes[0].setValue(neta);
				importes[1].setValue(desc);
				importes[2].setValue(pago);
				importes[3].setValue(saldo);
			}

			public void listChanged(ListEvent<Venta> listChanges) {
				/*
				while(listChanges.hasNext()){				
					listChanges.n
				}
				*/
				updateTotales();
			}
			
		}
		
		
		/**
		 * Worker para cargar datos en un EventList
		 * 
		 * @author Ruben Cancino
		 *
		 */
		@SuppressWarnings("unchecked")
		private class LoadWorker extends SwingWorker<List<Venta>, String>{			
			
			private WeakReference<EventList> source;
			
			public LoadWorker(final EventList source){
				this.source=new WeakReference<EventList>(source);
			}
			
			protected List<Venta> doInBackground() throws Exception {			
				return ServiceLocator.getVentasManager().buscarCuentasPorCobrar();
			}
			
			@SuppressWarnings("unchecked")
			protected void done() {
				try {
					List<Venta> found=get();				
					if(found!=null){
						System.out.println("Ventas: "+found.size());
						source.get().clear();
						source.get().addAll(found);
						grid.packAll();
					}				
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.showError("Error cargando las cuentas por cobrar");
				}
			}			
		}
		
		
	}
	
	

}
