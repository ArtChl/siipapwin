package com.luxsoft.siipap.cxc.swing.cobranza;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.CXCReportes;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.model.Cobradores;
import com.luxsoft.siipap.cxc.model.Sucursales;
import com.luxsoft.siipap.cxc.revision.RecibidasCxCView;
import com.luxsoft.siipap.cxc.swing.task.RevisionDeFacturasDialog;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.binding.ClienteBinding;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.crud.BasicCURDGridPanel;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Vista para el mantenimiento y procesos de revisiones y cobros
 * de las cuentas por cobrar
 * 
 * @author Ruben Cancino
 *
 */
public class RevisionCobrosView extends AbstractView{

	
	private JTabbedPane tabbedPanel;
	private ValueModel fechaHolder=new ValueHolder(new Date());
	private Action revisionAction;
	private BasicCURDGridPanel revisionPanel;
	private BasicCURDGridPanel pagosPanel;
	private RecibidasCxCView recibidasView;
	private VentasManager manager;
	private final RevisionSupport support;
	
	
	
	public RevisionCobrosView() {
		support=new RevisionSupport();
	}

	private void initEventHandling(){
		fechaHolder.addValueChangeListener(dateHandler);		
	}
	
	protected JComponent buildContent() {	
		initEventHandling();
		JPanel content=new JPanel(new BorderLayout());		
		FormLayout layout=new FormLayout("f:180dlu,p:g","f:p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(createFilterPanel(),cc.xy(1,1 ));
		builder.add(createBrowserPanel(),cc.xy(2, 1));		
		content.add(builder.getPanel(),BorderLayout.CENTER);		
		return content;
	}
	
	private JComponent createFilterPanel(){
		
		JXTaskPaneContainer container=new JXTaskPaneContainer();
		
		JXTaskPane operaciones=new JXTaskPane();
		operaciones.setLayout(new VerticalLayout(3));
		operaciones.setTitle("Operaciones");
		operaciones.setSpecial(true);
		{	
			operaciones.add(createDateSelector());
			operaciones.add(createLoadAction());
			operaciones.add(getRevisionAction());			
			//operaciones.add(getActualizarVentasAction());		
			operaciones.add(getActualizarComentarioAction());
			
		}		
		container.add(operaciones);
		
		JXTaskPane aRevision=new JXTaskPane();
		aRevision.setTitle("Resumen a Rvisión");
		aRevision.setExpanded(false);	
		aRevision.setSpecial(true);
		container.add(aRevision);
		
		recibidasView=new RecibidasCxCView();
		final JXTaskPane recibidas=new JXTaskPane();
		recibidas.setTitle("Recepciones");
		recibidas.setSpecial(true);
		
		recibidas.add(recibidasView.getCambiarPeriodo());
		recibidas.add(recibidasView.getLoadAction());
		recibidas.add(recibidasView.getReprogramarVentas());
		recibidas.add(recibidasView.getMarcarRecibidas());
		recibidas.add(recibidasView.getMarcarRevizada());
		recibidas.add(recibidasView.reprogramarManual);
		//recibidas.add(recibidasView.getDesmarcarRecibidas());
		recibidas.add(recibidasView.getSalvarAction());
		recibidas.add(recibidasView.getFiltrosPanel());
		eneableRecibidasTasks(false);
		container.add(recibidas);
		
		//final JXTaskPane filtros=new JXTaskPane();
		//filtros.add(recibidasView.getFiltrosPanel());
		//container.add(filtros);
		
		JXTaskPane aCobro=new JXTaskPane();
		aCobro.setTitle("Reportes");
		aCobro.setIcon(getIconFromResource("images2/report_go.png"));
		aCobro.add(createReporte1());
		aCobro.add(createReporte2());
		
		aCobro.setExpanded(false);
		aCobro.setSpecial(true);
		container.add(aCobro);		
		
		
		
		return container;
	}
	
	private JComponent createBrowserPanel(){
		tabbedPanel=new JTabbedPane();
		tabbedPanel.addTab("A Revisión", buildRevisionPanel());
		tabbedPanel.addTab("A Cobro", buildPagosPanel());		
		tabbedPanel.addTab("Recibidas", recibidasView.getControl());
		tabbedPanel.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {				
				boolean val=tabbedPanel.getSelectedIndex()==2;				
				recibidasView.getCambiarPeriodo().setEnabled(val);
				recibidasView.getLoadAction().setEnabled(val);
				//recibidasView.getMarcarRecibidas().setEnabled(val);
				//recibidasView.getMarcarRevizada().setEnabled(val);
				//recibidasView.getDesmarcarRecibidas().setEnabled(val);
				recibidasView.getSalvarAction().setEnabled(val);
				
				
			}			
		});
		return tabbedPanel;
	}
	
	private void eneableRecibidasTasks(boolean val){		
		recibidasView.getCambiarPeriodo().setEnabled(val);
		recibidasView.getLoadAction().setEnabled(val);
		recibidasView.getMarcarRecibidas().setEnabled(val);
		recibidasView.getMarcarRevizada().setEnabled(val);
		//recibidasView.getDesmarcarRecibidas().setEnabled(val);
		recibidasView.getSalvarAction().setEnabled(val);
	}
	
	@SuppressWarnings("unchecked")
	private JComponent buildRevisionPanel(){
		final String[] props={
				"id"
				,"clave"
				,"nombre"
				,"cobrador"
				,"sucursal"
				,"tipo"
				,"numero"
				,"numeroFiscal"
				,"fecha"
				//,"credito.fechaRevision"
				,"credito.fechaRevisionCxc"
				,"credito.revisada"				
				,"credito.recibidaCXC"
				,"credito.fechaRecepcionCXC"
				,"credito.vencimiento"
				,"credito.reprogramarPago"
				,"saldo"
				,"atraso"};
		
		final String[] labels={
				"Id"
				,"Cliente"
				,"Nombre"
				,"Cobrador"
				,"Sucursal"
				,"Tipo"
				,"Numero"
				,"N.Fiscal"
				,"Fecha"
				//,"Revisión"
				,"Rev CXC"
				,"Revisada"				
				,"Recibida CXC"
				,"F.Rec CXC"
				,"Vencimiento"
				,"F. Pago"
				,"Saldo"
				,"Atraso"};		
		//final boolean[] editables={false,false,false,false,false,false,false,false,false,true,false,true,false,false,false,false,false};
		final TableFormat tf=GlazedLists.tableFormat(Venta.class,props,labels);
		EventList<Venta> source=GlazedLists.threadSafeList(new BasicEventList<Venta>());						
		revisionPanel=new CobranzaGrid(source,tf);//new BasicCURDGridPanel(tf);		
		return revisionPanel.getControl();
	}
	
	private JComponent buildPagosPanel(){
		final String[] props={
				"clave"
				,"nombre"
				,"cobrador"
				,"sucursal"
				,"tipo"
				,"numero"
				,"numeroFiscal"
				,"fecha"
				,"credito.fechaRevisionCxc"
				,"credito.vencimiento"
				,"credito.revisada"
				,"credito.recibidaCXC"
				,"total"
				,"saldo"
				,"atraso"};
		
		final String[] labels={
				"Cliente"
				,"Nombre"
				,"Cobrador"
				,"Sucursal"
				,"Tipo"
				,"Numero"
				,"N.Fiscal"
				,"Fecha"
				,"Rev  (CxC)"
				,"Vencimiento"
				,"Revisada"
				,"Recibida CXC"
				,"Total"
				,"Saldo"
				,"Atraso"};
		
		final TableFormat tf=GlazedLists.tableFormat(Venta.class,props,labels);
		EventList<Venta> cobros=GlazedLists.threadSafeList(new BasicEventList<Venta>());
		pagosPanel=new CobranzaGrid(cobros,tf);
		return pagosPanel.getControl();
	}
	
	private JComponent createDateSelector(){		
		PanelBuilder builder=new PanelBuilder(new FormLayout("l:50dlu,2dlu,60dlu:g","p"));
		builder.setDefaultDialogBorder();
		CellConstraints cc=new CellConstraints();
		builder.add(DefaultComponentFactory.getInstance().createTitle("Fecha"),cc.xy(1, 1) );
		builder.add(Binder.createDateComponent(fechaHolder),cc.xy(3, 1));
		JComponent c=builder.getPanel();
		c.setOpaque(false);
		return c;		
	}	
	
	private Action createReporte1(){
		Action a=new DispatchingAction(this,"reporte1");		
		getActionConfigurer().configure(a, "reportAction");
		a.putValue(Action.NAME, "Revisión y Cobro");
		return a;
		
	}
	
	private Action createReporte2(){
		Action a=new DispatchingAction(this,"reporte2");		
		getActionConfigurer().configure(a, "reportAction");
		a.putValue(Action.NAME, "Recepción de Facturas");
		return a;
		
	}
	
	public Action getRevisionAction() {
		if(revisionAction==null){
			revisionAction=new DispatchingAction(this,"revision");
			if(getActionConfigurer()!=null)
				getActionConfigurer().configure(revisionAction, "revizarFacturasAction");
			
		}
		return revisionAction;
	}
	
	private Action createLoadAction(){
		return CommandUtils.createLoadAction(this, "load");
	}
	
	private Action actualizarVentasAction;
	
	/*
	private Action getActualizarVentasAction(){
		if(actualizarVentasAction==null){
			actualizarVentasAction=new AbstractAction(""){
				public void actionPerformed(ActionEvent e) {
					revisadas();
				}			
			};
			if(getActionConfigurer()!=null)
				getActionConfigurer().configure(actualizarVentasAction, CXCActions.ActualizarVentasEnCobranza.getId());
		}
		return actualizarVentasAction;
		
	}
	*/
	
	private Action actualizarComentario;
	
	private Action getActualizarComentarioAction(){
		if(actualizarComentario==null){
			actualizarComentario=new DispatchingAction(this,"actualizarComentario");
			CommandUtils.configAction(actualizarComentario, CXCActions.ActualizarComentariosDeVentasCredito.getId(), "");
		}
		return actualizarComentario;
	}
	
	@SuppressWarnings("unchecked")
	public void actualizarComentario(){
		
		if(tabbedPanel.getSelectedComponent()==revisionPanel.getControl()){
			final List<Venta> seleccionadas=new ArrayList<Venta>();
			seleccionadas.addAll(revisionPanel.getSelected());
			for(Venta v:seleccionadas){
				if(v.getCredito()!=null){
					support.actualizarComentarios(v.getCredito());
				}
			}
			/**
			for(Venta v:seleccionadas){
				int index=revisionPanel.getSource().indexOf(v);
				revisionPanel.getSource().set(index, v);
			}
			**/
		}else if(tabbedPanel.getSelectedIndex()==2){
			final List<Venta> seleccionadas=new ArrayList<Venta>();
			seleccionadas.addAll(pagosPanel.getSelected());
			for(Venta v:seleccionadas){
				if(v.getCredito()!=null){
					support.actualizarComentarios(v.getCredito());
				}
			}
			/**
			for(Venta v:seleccionadas){
				int index=pagosPanel.getSource().indexOf(v);
				pagosPanel.getSource().set(index, v);
			}
			**/
		}else
			return;
		
	}
	
	
	/**
	 * Activa el mantenimiento de facturas para los parametros de revision
	 * 
	 *
	 */
	@SuppressWarnings("unchecked")
	
	public void revision(){
		if(revisionPanel.getSelected().isEmpty()) return;
		RevisionDeFacturasDialog dialog=new RevisionDeFacturasDialog(revisionPanel.getSelected());		
		dialog.open();		
		if(!dialog.hasBeenCanceled()){
			dialog.commit();
			for(Object o:revisionPanel.getSelected()){
				Venta v=(Venta)o;
				String msg=MessageFormat.format("Cobrador: {0} Dia Pago: {1} Dia Rev {2} Prox Rev: {3}", v.getCobrador(),v.getDiaPago(),v.getDiaRevision(),v.getCredito().getFechaRevisionCxc());
				System.out.println(msg);
				getManager().actualizarVenta(v);
			}
		}
	}
	
	
	public void cambiarFecha(){
		SXAbstractDialog dialog=Binder.createDateSelector(this.fechaHolder);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			load();
		}
	}
	
	/**
	 * Carga los grids con facturas a revision y a pago
	 *
	 */
	public void load(){
		final Date d=(Date)fechaHolder.getValue();
		if(d==null) return;
		SwingWorker<List[],String> worker=new SwingWorker<List[],String>(){
			
			protected List[] doInBackground() throws Exception {
				return manager.buscarCobranzaDelDia(d);
			}
			
			@SuppressWarnings("unchecked")
			protected void done() {
				//revisionPanel.getSource().getReadWriteLock().writeLock().lock();
				//pagosPanel.getSource().getReadWriteLock().writeLock().lock();
				try {
					List[] lists=get();
					revisionPanel.getSource().clear();					
					revisionPanel.getSource().addAll(lists[0]);
					revisionPanel.pack();
					
					pagosPanel.getSource().clear();					
					pagosPanel.getSource().addAll(lists[1]);
					pagosPanel.pack();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					//revisionPanel.getSource().getReadWriteLock().writeLock().unlock();
					//pagosPanel.getSource().getReadWriteLock().writeLock().unlock();
					if(pagosPanel!=null)
						pagosPanel.pack();
					revisionPanel.pack();
				}
			}
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	/**
	 * Actualiza las ventas modificadas desde el grid, 
	 * particularmente modifica la recepcion
	 *
	 */
	/*
	public void revisadas(){
		if(revisionPanel.getSelected().isEmpty())return;
		SwingWorker worker=new SwingWorker(){			
			protected Object doInBackground() throws Exception {
				actualizarVentas();
				return null;
			}
			protected void done() {
				load();
			}			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	*/
	/**
	public void actualizarVentas(){
		for(Object o:revisionPanel.getSource()){
			Venta v=(Venta)o;
			try {
				if(v.getCredito().isRecibidaCXC())
					v.getCredito().setFechaRecepcionCXC(new Date());
				else
					v.getCredito().setFechaRecepcionCXC(null);
				getManager().actualizarVenta(v);
			} catch (Exception e) {
				MessageUtils.showError("Error actualizando venta", e);
				continue;
			}
		}
	}
	
	**/
	
	public void close(){
		try {
			revisionPanel.getSource().clear();
			pagosPanel.getSource().clear();
			recibidasView.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void reporte1(){
		final Map<String,Object> map =new HashMap<String, Object>();
		map.put("FECHA", fechaHolder.getValue());
		ReportForm1 form=new ReportForm1();
		form.open();
		if(!form.hasBeenCanceled()){
			String id=getString(CXCReportes.DiarioDeCobranza.toString());
			map.putAll(form.getParametros());
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+map.toString());
			}
			ReportUtils.viewReport(id, map);
		}		
	}
	
	public void reporte2(){
		final Map<String,Object> map =new HashMap<String, Object>();
		//map.put("FECHA", fechaHolder.getValue());
		ReportForm2 form=new ReportForm2();
		form.open();
		if(!form.hasBeenCanceled()){
			String id=getString(CXCReportes.RecepcionDeFacturas.toString());
			map.putAll(form.getParametros());
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+map.toString());
			}
			ReportUtils.viewReport(id, map);
		}		
	}
	
	private PropertyChangeListener dateHandler=new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			load();			
		}
	};

	public VentasManager getManager() {
		return manager;
	}

	public void setManager(VentasManager manager) {
		this.manager = manager;
		support.setManager(manager);
	}
	
	
	/**
	 * Forma para el reporte de cobranza
	 * 
	 * @author RUBEN
	 *
	 */
	public static class ReportForm1 extends SXAbstractDialog{
		
		private final Map<String, Object> parametros;
		
		
		private JComboBox cobradores;
		private ValueModel clienteModel;
		private JComponent cliente;

		public ReportForm1() {
			super("Diario de Cobranza");
			parametros=new HashMap<String, Object>();
		}
		
		private void initComponents(){
			clienteModel=new ValueHolder(null);
			final ClienteBinding b=new ClienteBinding(clienteModel);
			cliente=b.getControl();
			cobradores=new JComboBox(Cobradores.values());
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			JPanel panel=new JPanel(new BorderLayout());
			final FormLayout layout=new FormLayout(
					"l:40dlu,3dlu,p, 3dlu, " +
					"l:40dlu,3dlu,p:g " +
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Cliente",cliente,5);
			builder.nextLine();
			builder.append("Cobrador",cobradores,true);
			
			panel.add(builder.getPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			
			super.doApply();
			if(clienteModel.getValue()!=null){
				Cliente c=(Cliente)clienteModel.getValue();
				parametros.put("CLIENTE", c.getClave());
			}
			else
				parametros.put("CLIENTE", "%");
			Cobradores c=(Cobradores)cobradores.getSelectedItem();			
			if(c.getNumero()==0){
				parametros.put("COBRADOR", "%");
				parametros.put("COBRADOR__NOMBRE", "TODOS");
				
			}
			else{
				parametros.put("COBRADOR", String.valueOf(c.getNumero()));
				parametros.put("COBRADOR_NOMBRE", c.toString());
				
			}	
			System.out.println("Parametros: "+parametros);
			
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		 
		
	}
	
	/**
	 * Forma para el reporte de Recepcion de facturas
	 * 
	 * @author RUBEN
	 *
	 */
	public static class ReportForm2 extends SXAbstractDialog{
		
		private final Map<String, Object> parametros;
		
		
		private JComboBox sucursales;
		private JXDatePicker fecha;
		

		public ReportForm2() {
			super("Recepción de facturas");
			parametros=new HashMap<String, Object>();
		}
		
		private void initComponents(){
			fecha=new JXDatePicker();
			fecha.setFormats(new String[]{"dd/MM/yyyy"});
			sucursales=new JComboBox(Sucursales.values());
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			JPanel panel=new JPanel(new BorderLayout());
			final FormLayout layout=new FormLayout(
					"l:40dlu,3dlu,p, 3dlu, " +
					"l:40dlu,3dlu,p:g " +
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Sucursal",sucursales,5);
			builder.nextLine();
			//builder.append("Fecha",fecha,true);
			
			panel.add(builder.getPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			
			super.doApply();
			Sucursales s=(Sucursales)sucursales.getSelectedItem();
			parametros.put("SUCURSAL", s.getNumero());
			//parametros.put("FECHA", fecha.getDate());
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		 
		
	}
	
	
	public class CobranzaGrid extends BasicCURDGridPanel{
		
		JTextField cobrador=new JTextField(20);
		JTextField cliente=new JTextField(20);



		public CobranzaGrid(EventList source, TableFormat tableFormat) {
			super(source, tableFormat);
			// TODO Auto-generated constructor stub
		}

		protected JComponent buildContent() {
			JComponent parent=super.buildContent();
			JPanel p=new JPanel(new BorderLayout(5,5));
			p.add(parent,BorderLayout.CENTER);
			p.add(buildFilterPanel(),BorderLayout.NORTH);
			return p;
		}
		
		private JComponent buildFilterPanel(){
			FormLayout layout=new FormLayout("p,3dlu,p,3dlu,p,3dlu,p:g","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);			
			builder.append("Cobrador",cobrador);
			builder.append("Cliente",cliente);
			return builder.getPanel();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected EventList pipeLists(final EventList source) {
			
			final TextFilterator<Venta> cFilterator=GlazedLists.textFilterator(new String[]{"cobrador"});
			final MatcherEditor<Venta> cobradoresEditor=new TextComponentMatcherEditor<Venta>(cobrador,cFilterator);
			final FilterList<Venta> cobradoresList=new FilterList<Venta>(source,new ThreadedMatcherEditor<Venta>(cobradoresEditor)); 
			
			final TextFilterator<Venta> clienteFilterator=GlazedLists.textFilterator(new String[]{"clave","nombre"});
			final MatcherEditor<Venta> clienteEditor=new TextComponentMatcherEditor<Venta>(cliente,clienteFilterator);
			final FilterList<Venta> clientesList=new FilterList<Venta>(cobradoresList,new ThreadedMatcherEditor<Venta>(clienteEditor));
			return clientesList;
			
			//return cobradoresList;
		}
		
	}
	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		/**
		ImprimirForm form=new ImprimirForm();
		form.open();
		if(!form.hasBeenCanceled()){
			System.out.println(form.getParametros());
		}
		**/
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				RevisionCobrosView view=new RevisionCobrosView();
				return view.getContent();
			}

			@Override
			protected void setResizable() {
				setResizable(true);
			}
			
			
		};
		dialog.open();
		System.exit(0);
	}
	
}
