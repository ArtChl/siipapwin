package com.luxsoft.siipap.cxc.pagos;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;
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
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.model.PagosDeCreditoModel;
import com.luxsoft.siipap.cxc.task.RecordatorioDePago;
import com.luxsoft.siipap.cxc.utils.Browsers;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.cxc.utils.CheckBoxSelector;
import com.luxsoft.siipap.cxc.utils.FacturasPorFechaSelector;
import com.luxsoft.siipap.cxc.utils.MatcherEditors;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.InternalTaskView;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Facturas por cliente. Las acciones que esta vista realiza son direccionadas a un Controlador
 * 
 * @author Ruben Cancino
 *
 */
public class FacturasPorCliente implements InternalTaskView{
	
	private final IControladorDePagos controlador;
	private final PagosDeCreditoModel model;	
	private HeaderPanel header;
	private JComponent filterPanel;
	
	/** Acciones que esta vista proporciona **/
	
	private Action cambiarPeriodo;
	private Action pagar;
	private Action pagoConOtros;
	private Action traspasoJuridico;	
	private Action pagoAutomatico;
	private Action pagoDiferenciaCambiaria;
	private Action pagarConNota;
	private Action generarDescuento;
	private Action actualizarVentas;
	private Action verFactura;
	private Action loadAction;
	private Action registrarAnticipo;
	private Action actualizarNF;
	private Action mostrarNotasDisponibles;
	private Action mostrarPagosConDisponible;
	private Action eliminarProvision;
	private Action recordatorioAction;
	
	/** Infraesetructura de filtros para GlazedLists **/
	
	private JTextField numeroField;//=new JTextField(20);
	private CheckBoxSelector<Venta> paraDescuentoEditor;
	private CheckBoxSelector<Venta> conSaldoMatcherEditor;
	private CheckBoxSelector<Venta> vencidasMatcherEditor;
	private CheckBoxSelector<Venta> porVencerMatcherEditor;
	private CheckBoxSelector<Venta> pagadasEditor;
	private FacturasPorFechaSelector facturasPorFecha;	
	private Totales totales;
	
	/**GlazedList stuff**/
	
	private EventList<Venta> source;
	private SortedList<Venta> sortedList;
	private EventList<Venta> filterList;
	private EventSelectionModel<Venta> selectionModel;
	private JXTable grid;
	
	
	public FacturasPorCliente(PagosDeCreditoModel model,IControladorDePagos controlador){
		this.model=model;
		this.controlador=controlador;
		model.addPropertyChangeListener(new SelectionHandler());
		
	}
	
	private void initActions(){
		cambiarPeriodo=new DispatchingAction(this,"cambiarPeriodo");
		CommandUtils.configAction(cambiarPeriodo, "seleccionarPeriodo", "");
		
		pagar=new DispatchingAction(this,"pagar");
		CommandUtils.configAction(pagar, "FacturasPorCliente.pagar", "images2/money.png");
		
		pagoConOtros=new DispatchingAction(this,"pagoConOtros");
		CommandUtils.configAction(pagoConOtros, "FacturasPorCliente.pagoConOtros", "");
		
		traspasoJuridico=new DispatchingAction(this,"traspasoJuridico");
		CommandUtils.configAction(traspasoJuridico, "FacturasPorCliente.traspasoJuridico", "");
		pagoAutomatico=new DispatchingAction(this,"pagoAutomatico");
		CommandUtils.configAction(pagoAutomatico, "FacturasPorCliente.pagoAutomatico", "images2/money_add.png");
		generarDescuento=new DispatchingAction(this,"generarDescuento");
		CommandUtils.configAction(generarDescuento, "FacturasPorCliente.generarDescuento", "");
		actualizarVentas=new DispatchingAction(this,"actualizarVentas");
		CommandUtils.configAction(actualizarVentas, "FacturasPorCliente.actualizarVentas", "");
		
		verFactura=new DispatchingAction(this,"verFactura");
		CommandUtils.configAction(verFactura, "FacturasPorCliente.verFactura", "");
		loadAction=CommandUtils.createLoadAction(this, "load");
		pagarConNota=new DispatchingAction(this,"pagarConNota");
		CommandUtils.configAction(pagarConNota, "FacturasPorCliente.pagarConNota", "");
		
		registrarAnticipo=new DispatchingAction(this,"registrarAnticipo");
		CommandUtils.configAction(registrarAnticipo, "FacturasPorCliente.registrarAnticipo", "");
		
		actualizarNF=new DispatchingAction(this,"actualizarNF");
		CommandUtils.configAction(actualizarNF, "FacturasPorCliente.actualizarNF", "");
		
		mostrarNotasDisponibles=new DispatchingAction(this,"mostrarNotasDisponibles");
		CommandUtils.configAction(mostrarNotasDisponibles, CXCActions.MostrarNotasDisponibles.getId(), "");
		
		eliminarProvision=new DispatchingAction(this,"eliminarProvision");
		CommandUtils.configAction(eliminarProvision, CXCActions.EliminarProvision.getId(), "");
		
		mostrarPagosConDisponible=new DispatchingAction(this,"mostrarPagosConDisponible");
		CommandUtils.configAction(mostrarPagosConDisponible, CXCActions.MostrarPagosConDisponible.getId(), "");
		
		recordatorioAction=new DispatchingAction(this,"recordatorio");
		CommandUtils.configAction(recordatorioAction, CXCActions.MandarRecordatorio.getId(), "");
		
		pagoDiferenciaCambiaria=new DispatchingAction(this,"diferienciaCambiaria");
		CommandUtils.configAction(pagoDiferenciaCambiaria, CXCActions.PagarDiferienciasCambiarias.getId(), null);
	}
	
	/**
	 * Inicializa GlazedList para ser usuado en el grid
	 *
	 */
	@SuppressWarnings("unchecked")
	private void initGlazedLists(){
		 
		/** Encadenamiento de listas en base a MatcherEditors especializados  **/
		// Origen o lista base
		source=model.getVentas();
		
		// Creamos los MatcherEditors adecuados
		paraDescuentoEditor=MatcherEditors.createFacturasParaNCDescuento();
		conSaldoMatcherEditor=MatcherEditors.createFacturasPendientesSelector();		
		vencidasMatcherEditor=MatcherEditors.createFacturasVencidasSelector();		
		porVencerMatcherEditor=MatcherEditors.createFacturasPorVencerSelector();		
		pagadasEditor=MatcherEditors.createFacturasPagadasSelector();
		facturasPorFecha=MatcherEditors.createFacturasPorFechaSelector("credito.reprogramarPago");		
		final TextFilterator<Venta> numeroFilterator=GlazedLists.textFilterator(new String[]{"numero"});
		final TextComponentMatcherEditor<Venta> numeroEditor=new TextComponentMatcherEditor<Venta>(getPorNumeroField(),numeroFilterator);
		
		final TextFilterator<Venta> fiscalFilterator=GlazedLists.textFilterator(new String[]{"numeroFiscal"});
		final TextComponentMatcherEditor<Venta> fiscalEditor=new TextComponentMatcherEditor<Venta>(getPorFiscalField(),fiscalFilterator);
		
		// Aplicamos los editores en cascada con ayuda de CompositeMatcherEditor
		
		final EventList<MatcherEditor<Venta>> editores=new BasicEventList<MatcherEditor<Venta>>();
		editores.add(paraDescuentoEditor);
		editores.add(conSaldoMatcherEditor);
		editores.add(vencidasMatcherEditor);
		editores.add(porVencerMatcherEditor);		
		editores.add(pagadasEditor);
		editores.add(numeroEditor);
		editores.add(fiscalEditor);
		editores.add(facturasPorFecha);
		
		final CompositeMatcherEditor<Venta> compositeEditor=new CompositeMatcherEditor<Venta>(editores);
		compositeEditor.setMode(CompositeMatcherEditor.AND);
		//Lista final obtenida
		filterList=new FilterList<Venta>(source,new ThreadedMatcherEditor<Venta>(compositeEditor));		
		totales=new Totales(filterList);
	}
	
	private void initComponents(){		
		final TableFormat<Venta> tf=CXCTableFormats.getVentasCreTF();
		//final Comparator<Venta> comp=GlazedLists.beanPropertyComparator(Venta.class, "atraso");
		sortedList=new SortedList<Venta>(filterList,null);
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
	}

	public JComponent getControl() {
		initActions();
		initGlazedLists();
		initComponents();
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildHeader(),BorderLayout.NORTH);
		panel.add(UIFactory.createTablePanel(grid),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildHeader(){		
		header=ComponentUtils.getBigHeader(getClienteHeader(),getDescriptionHeader());		
		return header;
	}
	
	public JTextField getPorNumeroField(){
		if(numeroField==null){
			numeroField=new JTextField();
			numeroField.setColumns(10);
			numeroField.addFocusListener(new FocusAdapter(){
				@Override
				public void focusLost(FocusEvent e) {
					grid.requestFocus();					
				}
				
			});
		}
		return numeroField;
	}
	
	private JTextField fiscalField;
	
	public JTextField getPorFiscalField(){
		if(fiscalField==null){
			fiscalField=new JTextField();
			fiscalField.setColumns(10);			
		}
		return fiscalField;
	}
	
	public JComponent getFiltrosPanel(){
		if(filterPanel==null){
			FormLayout layout=new FormLayout(
					"l:p,2dlu,l:max(p;100dlu)","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);			
			builder.append("Factura",getPorNumeroField(),true);
			builder.append("N. Fiscal",getPorFiscalField(),true);
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

	public Icon getIcon() {
		return CommandUtils.getIconFromResource("images2/bancos16.gif");
	}

	public String getTitle() {
		return "Facturas";
	}

	public void instalOperacionesAction(JXTaskPane operaciones) {
		operaciones.add(cambiarPeriodo);
		operaciones.add(loadAction);
		operaciones.add(pagar);
		operaciones.add(pagoConOtros);
		operaciones.add(pagarConNota);
		operaciones.add(traspasoJuridico);
		operaciones.add(verFactura);
		operaciones.add(actualizarNF);
	}

	public void instalProcesosActions(JXTaskPane procesos) {
		procesos.add(generarDescuento);
		procesos.add(registrarAnticipo);		
		procesos.add(pagoAutomatico);
		procesos.add(pagoDiferenciaCambiaria);
		procesos.add(mostrarNotasDisponibles);
		procesos.add(mostrarPagosConDisponible);
		procesos.add(actualizarVentas);	
		procesos.add(eliminarProvision);
		procesos.add(recordatorioAction);
	}

	public void installDetallesPanel(JXTaskPane detalle) {
		detalle.add(totales.getPanel());
		
	}

	public void installFiltrosPanel(JXTaskPane filtros) {
		filtros.add(getFiltrosPanel());
	}
	
	
	public void close() {
		this.source.clear();
		getPorNumeroField().setText("");
	}
	
	private String getClienteHeader(){
		if(model.getCliente()!=null){
			String pattern="{0} ({1})";
			return MessageFormat.format(pattern, 
					model.getCliente().getNombre(),model.getCliente().getClave());
		}
		return "";
	}
	
	private String getDescriptionHeader(){
		String pattern="Lista de facturas generadas periodo:  {0}" ;
		return MessageFormat.format(pattern, model.getPeriodo());
	}
	
	private void updateSelection(){		
		header.setTitle(getClienteHeader());
		header.setDescription(getDescriptionHeader());
	}
	
	private EventList<Venta> getSeleccionadas(){
		return selectionModel.getSelected();
	}
	
	/**** Metodos de acciones ****/
	
	public void cambiarPeriodo(){
		//final Periodo old=model.getPeriodo();
		//AbstractDialog dialog=Binder.createPeriodoSelector(model.getBufferedModel("periodo"));
		AbstractDialog dialog=Binder.createPeriodoSelector(model.getModel("periodo"));		
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			updateSelection();
			load();
			//System.out.println("Periodo seleccionado: "+model.getPeriodo());
			
		}
		
	}
	
	public void pagar(){
		if(!getSeleccionadas().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(selectionModel.getSelected());
			final PagoM pago=controlador.registrarPagoCreditoProvisionables(
					model.getCliente(),ventas);
			if(pago!=null){
				
				//Refrescando para proceder con descuentos
				for(Venta v:ventas){
					System.out.println("Refrescando la venta: "+v.getId());
					model.refrescar(v);
					int index=source.indexOf(v);
					source.set(index, v);
				}
				
				if(controlador.procedeDescuento(pago)){
					generarDescuento(pago.getFecha(),ventas,pago.isCondonar());
				}
				
				for(Venta v:ventas){					
					model.refrescar(v);					
					int index=source.indexOf(v);
					source.set(index, v);
				}
				controlador.registrarPagoAutomatico(ventas);
				for(Venta v:ventas){					
					model.refrescar(v);					
					int index=source.indexOf(v);
					source.set(index, v);
				}
				if(controlador.diferenciaCambiaria(pago)){
					for(Venta v:ventas){					
						model.refrescar(v);					
						int index=source.indexOf(v);
						source.set(index, v);
					}
				}
			}
			
		}
	}
	
	public void pagoConOtros(){
		if(!getSeleccionadas().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(getSeleccionadas());
			controlador.registrarPagoConOtros(model.getCliente(),ventas);
			for(Venta v:ventas){				
				model.refrescar(v);
				int index=sortedList.indexOf(v);
				sortedList.set(index, v);
			}
		}else
			MessageUtils.showMessage("Debe seleccionar una o mas ventas para efectuar el pago", "Pago con Otros");
	}
	
	/**
	 * Delega al controlador el pago de las facturas seleccionadas mediante una nota de credito 
	 *
	 */
	public void pagarConNota(){
		if(!getSeleccionadas().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(getSeleccionadas());
			controlador.registrarPagoConNota(model.getCliente(), ventas);
			for(Venta v:ventas){				
				model.refrescar(v);
				int index=sortedList.indexOf(v);
				sortedList.set(index, v);
			}			
		}else
			MessageUtils.showMessage("Debe seleccionar una o mas ventas para efectuar el pago", "Pago con Nota");
	}
	
	/**
	 * Aplica diferencias cambiarias
	 * 
	 *
	 */
	public void diferienciaCambiaria(){
		if(!getSeleccionadas().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(getSeleccionadas());
			controlador.diferenciaCambiaria(ventas);
			for(Venta v:ventas){				
				model.refrescar(v);
				int index=source.indexOf(v);
				source.set(index, v);
			}			
		}else
			MessageUtils.showMessage("Debe seleccionar una o mas ventas para efectuar el pago", "Pago con Nota");
	}
	
	public void traspasoJuridico(){
		if(!getSeleccionadas().isEmpty()){
			if(MessageUtils.showConfirmationMessage("Mandar a jurídico las facturas seleccionadas?", "Jurídico")){
				final List<Venta> ventas=new ArrayList<Venta>();
				ventas.addAll(getSeleccionadas());
				boolean res=controlador.trsladoAJuridico(ventas);
				if(res){
					for(Venta v:ventas){				
						model.refrescar(v);
						int index=source.indexOf(v);
						source.remove(index);
					}
				}				
			}
				
		}else
			MessageUtils.showMessage("Debe seleccionar una o mas ventas para efectuar el traslado", "Traslado a jurídico");		
	}
	
	public void pagoAutomatico(){
		if(!getSeleccionadas().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(getSeleccionadas());
			controlador.registrarPagoAutomatico(ventas);
			for(Venta v:ventas){				
				model.refrescar(v);
				int index=sortedList.indexOf(v);
				sortedList.set(index, v);
			}
		}
	}
	
	/**
	 * Registra un anticipo a la cuenta del cliente 
	 *
	 */
	public void registrarAnticipo(){
		controlador.registrarAnticipo(model.getCliente().generarCredito());
	}
	
	/**
	 * Metodo para genera notas de credito por descuento a las facturas 
	 * con pagos aplicados, es decir las que ya califican para consolidar su descuento
	 * 
	 *
	 */
	public void generarDescuento(){
		if(!getSeleccionadas().isEmpty()){			
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(getSeleccionadas());
			generarDescuento(new Date(),ventas,true);
			for(Venta v:ventas){			
				int index=sortedList.indexOf(v);			
				Venta nv=model.refrescar(v);			
				sortedList.set(index, nv);
			}			
		}		
	}
	
	/**
	 * Metodo para genera notas de credito por descuento
	 *
	 */
	public void generarDescuento(final Date fecha,final List<Venta> ventas,boolean condonar){	
		controlador.aplicarNotaDeDescuento(model.getCliente(), ventas,fecha,condonar);
		
	}
	
	/**
	 * Refresca de la base de datos y actualiza el list de manera adecuada para 
	 * que el usuario vea los cambios.
	 * Especialmente nos interesa el saldo
	 *
	
	private void refrescarSeleccion(){
		for(Venta v:getSeleccionadas()){			
			int index=sortedList.indexOf(v);			
			Venta nv=model.refrescar(v);			
			sortedList.set(index, nv);
		}
	}
	 */
	
	
	/**
	 * Actualiza las ventas unicamente las seleccionadas
	 */
	@SuppressWarnings("unchecked")
	public void actualizarVentas(){
		
		final List<Venta> ventas=new ArrayList<Venta>();
		ventas.addAll(getSeleccionadas());
		
		controlador.actualizarVentas(ventas);
		for(Venta v:ventas){
			int index=source.indexOf(v);
			source.set(index, v);					
		}
		/**
		final SwingWorker<String,Venta> worker=new SwingWorker<String,Venta>(){			
			protected String doInBackground() throws Exception {
				//
				for(Venta v:ventas){
					controlador.getVentasManager().actualizarVenta(v);
					publish(v);
				}
				return "OK";
			}
			@Override
			protected void process(List<Venta> chunks) {
				for(Venta v:chunks)
					System.out.println("Actualizando venta: "+v.getId());
			}

			protected void done() {
				for(Venta v:ventas){
					int index=source.indexOf(v);
					source.set(index, v);					
				}				
			}			
		};
		TaskUtils.executeSwingWorker(worker);
		**/
	}
	
	public void eliminarProvision(){
		if(!getSeleccionadas().isEmpty()){
			final List<Venta> ventas=getSeleccionadas();
			controlador.eliminarCreditoYProvision(ventas);
			for(Venta v:ventas){				
				//int index=sortedList.indexOf(v);
				//sortedList.set(index, v);
				int index=source.indexOf(v);
				source.set(index, v);
			}
		}
	}
	
	public void verFactura(){
		if(!getSeleccionadas().isEmpty()){
			controlador.mostrarVenta(getSeleccionadas().get(0));
		}
	}
	
	public void recordatorio(){
		if(!getSeleccionadas().isEmpty()){
			final List<Venta> ventas=new ArrayList<Venta>();
			ventas.addAll(getSeleccionadas());
			//CXCFiltros.filtrarVentasConSaldo(ventas);
			if(!ventas.isEmpty()){
				try {
					ClienteCredito cc=ventas.get(0).getCliente().getCredito();
					String email=cc.getEmail();
					if(StringUtils.isEmpty(email)){
						MessageUtils.showMessage(
								MessageFormat.format("El cliente {0} ({1}) " +
								"no tiene corre electrónico registrado",cc.getNombre(),cc.getClave()), "Recordatorio de pago");
					}
					RecordatorioDePago rec=new RecordatorioDePago();
					rec.execute(ventas);
				} catch (Exception e) {
					MessageUtils.showError("Error", e);
				}
			}else
				MessageUtils.showMessage("Debe seleccionar una o mas ventas con saldo para mandar un recordatorio ", "Recordatorio de pago");
		}
			
	}
	
	public void actualizarNF(){
		if(!getSeleccionadas().isEmpty()){
			Venta v=getSeleccionadas().get(0);
			//int index=sortedList.indexOf(v);	
			controlador.actualizarNumeroFiscal(v);
			getSeleccionadas().set(0, v);
		}
	}
	
	public void load(){
		LoadWorker worker=new LoadWorker(source);
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void mostrarNotasDisponibles(){
		Browsers.mostrarNotasDisponibles(model.getCliente());
	}
	
	public void mostrarPagosConDisponible(){
		Browsers.mostrarSaldosAFavor(model.getCliente());
	}
	
	/****** Fin metodos de acciones *************/
	
	private class SelectionHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			updateSelection();
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
			return model.getBuscarVentas();
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
				MessageUtils.showError("Error cargando facturas para el cliente: "+model.getCliente(),e);
			}
		}			
	}
	
	/**
	 * Panel para registrar los totales
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class Totales implements ListEventListener<Venta>{
		
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
			/**
			while(listChanges.hasNext()){				
				listChanges.n
				
				
			}
			*/
			//System.out.println("Cambios detectados: "+listChanges.getType());
			updateTotales();
		}
		
	}

}
