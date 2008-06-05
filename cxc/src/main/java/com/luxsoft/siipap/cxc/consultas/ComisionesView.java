package com.luxsoft.siipap.cxc.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.ComisionDeVentas;
import com.luxsoft.siipap.cxc.model2.ComisionDao;
import com.luxsoft.siipap.cxc.utils.CheckBoxSelector;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.DefaultTaskView;
import com.luxsoft.siipap.ventas.domain.Venta;


public class ComisionesView extends DefaultTaskView{
	
	TableModel tmodel;
	private JXTable grid;
	private JPanel filterPanel;
	private EventList<ComisionDeVentas> source;
	private EventSelectionModel<ComisionDeVentas> selectionModel;
	private CheckBoxSelector<ComisionDeVentas> selector;
	private JTextField vendedor=new JTextField(10);
	private JTextField cliente=new JTextField(10);
	private JPanel totalesPanel;
	private JFormattedTextField pagos=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
	private JFormattedTextField comision=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
	public final HeaderPanel header;
	
	private String target;
	private Periodo periodo=Periodo.periodoDeloquevaDelMes();
	

	public ComisionesView(final String target) {
		super();
		this.target=target;
		Assert.isTrue( ("vendedor".equals(target)||"cobrador".equals(target)),"Esta vista requiere target cobrador/vendedor no permite: "+target);
		header=new HeaderPanel("Ventas comisionables por "+StringUtils.capitalize(target),"");
		perlabel=new JLabel(periodo.toString());
		actualizarHeader();
	}
	
	protected JComponent createDocumentPanel() {
		final JPanel panel=new JPanel(new BorderLayout());
		//panel.add(header,BorderLayout.NORTH);
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(850,520));
		return panel;
	}
	
	private JComponent buildGridPanel(){
		grid=ComponentUtils.getStandardTable();
		source=GlazedLists.threadSafeList(new BasicEventList<ComisionDeVentas>());
		final EventList<ComisionDeVentas> eventList=filterList(source);
		final SortedList<ComisionDeVentas> sortedList=new SortedList<ComisionDeVentas>(eventList,null);
		final String targetName=StringUtils.capitalize(target);
		final TableFormat<ComisionDeVentas> tf=GlazedLists.tableFormat(ComisionDeVentas.class
				, new String[]{
					target,"clave","nombre","numero","pagosComisionables","descuento","comision","importe","aplicado"
					,"venta","sucursal","serie","tipo","fechaVenta","vencimiento","fechaPago","atraso"							  
							  ,"total","notasAplicadas","ventaNeta","pagos"
							  ,"saldo","cancelComentario"}
		
				, new String[]{
					targetName,"Cliente","Nombre","Fac","Pagos (comi)","Desc","comision","Importe","Aplicado"
					,"Id","Suc"	,"S","T","F.Venta","Vence","F.Pago","Atraso"							  
			  				,"Total","Notas","V.Neta","Pagos"
			  				,"Saldo","Cancelacion"}
		
		);
		
		final EventTableModel<ComisionDeVentas> tm=new EventTableModel<ComisionDeVentas>(sortedList,tf);
		selectionModel=new EventSelectionModel<ComisionDeVentas>(sortedList);
		selectionModel.addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					actualizarTotales();
				}
				
			}
			
		});
		tmodel=tm;
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);		
		grid.packAll();
		ComponentUtils.decorateActions(grid);		
		grid.getColumn("Importe").setCellRenderer(Renderers.getCantidadNormalTableCellRenderer());
		grid.getColumnExt("Id").setVisible(false);
		grid.getColumnExt("Cancelacion").setVisible(false);
		new TableComparatorChooser<ComisionDeVentas>(grid,sortedList,true);
		return new JScrollPane(grid);

	}
	

	
	private EventList<ComisionDeVentas> filterList(final EventList<ComisionDeVentas> list){
		
		final EventList<MatcherEditor<ComisionDeVentas>> editors=new BasicEventList<MatcherEditor<ComisionDeVentas>>();
		
		//Vendedor/Cobrador matcher
		final TextFilterator<ComisionDeVentas> vendedorFilter=GlazedLists.textFilterator(new String[]{target});
		TextComponentMatcherEditor<ComisionDeVentas> editor1=new TextComponentMatcherEditor<ComisionDeVentas>(vendedor,vendedorFilter);
		editors.add(editor1);
		
		final TextFilterator<ComisionDeVentas> clienteFilter=GlazedLists.textFilterator(new String[]{"clave","nombre"});
		final TextComponentMatcherEditor<ComisionDeVentas> editor2=new TextComponentMatcherEditor<ComisionDeVentas>(cliente,clienteFilter);
		editors.add(editor2);
		
		
		
		editors.add(getSelector());
		
		
		final CompositeMatcherEditor<ComisionDeVentas> compositeEditor=new CompositeMatcherEditor<ComisionDeVentas>(editors);
		final FilterList<ComisionDeVentas> filterList=new FilterList<ComisionDeVentas>(list,compositeEditor);
		
		return filterList;
	}
	
	private CheckBoxSelector<ComisionDeVentas> getSelector(){
		if(selector==null){
			selector=new AplicablesEditor();
			((AplicablesEditor)selector).unSelect();
		}
		return selector;
	}
	
	private JLabel perlabel;
	
	public JComponent getFiltrosPanel(){
		if(filterPanel==null){
			FormLayout layout=new FormLayout(
					"l:p,2dlu,f:p:g","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);			
			builder.append("Periodo",perlabel,true);
			builder.append(target.equals("vendedor")?"Vendedor":"Cobrador",vendedor,true);
			builder.append("Cliente",cliente,true);	
			builder.append("Aplicadas",getSelector().getBox(),true);
			filterPanel= builder.getPanel();			
			filterPanel.setOpaque(false);			
		}
		return filterPanel;		
	}
	
	
	public JComponent getTotalesPanel(){
		if(totalesPanel==null){
			pagos.setEditable(false);
			pagos.setHorizontalAlignment(SwingConstants.RIGHT);
			pagos.setOpaque(false);
			comision.setEditable(false);
			comision.setHorizontalAlignment(SwingConstants.RIGHT);
			comision.setOpaque(false);
			FormLayout layout=new FormLayout(
					"l:p,2dlu,f:p:g","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);			
			builder.append("Tot Pagos",pagos,true);
			builder.append("Comisión",comision,true);						
			totalesPanel= builder.getPanel();			
			totalesPanel.setOpaque(false);			
		}
		return totalesPanel;		
	}
	
	
	@Override
	protected void instalarTaskPanels(JXTaskPaneContainer container) {		
		container.remove(consultas);
		container.remove(procesos);
		filtros.add(getFiltrosPanel());
		detalles.add(getTotalesPanel());
		detalles.setExpanded(true);
	}
	
	protected void instalarTaskElements(){		
		operaciones.add(CommandUtils.createRefreshAction(this, "load"));
		final Action aplicar=new DispatchingAction(this,"aplicar");
		CommandUtils.configAction(aplicar, CXCActions.AplicarComisiones.getId(), null);
		operaciones.add(aplicar);
		final Action periodoAction=new DispatchingAction(this,"cambiarPeriodo");
		CommandUtils.configAction(periodoAction, "seleccionarPeriodo", "");
		operaciones.add(periodoAction);		
		final Action cancelarComision=CommandUtils.createDeleteAction(this, "cancelarComision");
		CommandUtils.configAction(cancelarComision, CXCActions.CancelarComisiones.getId(), null);
		operaciones.add(cancelarComision);
		operaciones.add(CommandUtils.createPrintAction(this, "printReport"));
		final Action eliminarComision=new DispatchingAction(this,"eliminarComision");
		//eliminarComision.putValue(Action.SHORT_DESCRIPTION, "Eliminar");
		CommandUtils.configAction(eliminarComision, CXCActions.EliminarComisiones.getId(), null);
		operaciones.add(eliminarComision);
		operaciones.add(createActionCom());
	}
			
	
	public void actualizarHeader(){
		header.setDescription("Periodo: "+periodo.toString());
		perlabel.setText(periodo.toString());
	}
	
	public void load(){
		try {
			ComisionDao dao=new ComisionDao();
			final List<ComisionDeVentas> coms;
			if("vendedor".equals(target))
				coms=dao.buscarComisionesVendedor(periodo);
			else
				coms=dao.buscarComisionesCobrador(periodo);
			source.clear();
			source.addAll(coms);
			grid.packAll();
		} catch (Exception e) {
			MessageUtils.showError("Error cargando ComisionDeVentass",e);
		}
		
		/*
		SwingWorker<List<ComisionDeVentas>, String> worker=new SwingWorker<List<ComisionDeVentas>, String>(){
			@Override
			protected List<ComisionDeVentas> doInBackground() throws Exception {				
				ComisionDao dao=new ComisionDao();
				List<ComisionDeVentas> coms;
				if("vendedor".equals(target))
					coms=dao.buscarComisionesVendedor(periodo);
				else
					coms=dao.buscarComisionesCobrador(periodo);
				return coms;
			}
			@Override
			protected void done() {
				try {
					source.clear();
					source.addAll(get());
					grid.packAll();
					afterLoad();
				} catch (Exception e) {
					MessageUtils.showError("Error cargando ComisionDeVentass",e);
				}				
			}			
			
		};
		TaskUtils.executeSwingWorker(worker);
		*/
	}
	/*
	public void afterLoad(){
		
	}*/
	
	public void aplicar(){
		if(!selectionModel.getSelected().isEmpty()){
			final List<ComisionDeVentas> comisiones=new ArrayList<ComisionDeVentas>();
			comisiones.addAll(selectionModel.getSelected());
			for(ComisionDeVentas c:comisiones){
				final Venta v=ServiceLocator.getCXCManager().getVentasDao().buscarPorId(c.getVenta());
				if("vendedor".equals(target)){
					v.setComisionVenta(c.getComision());
					v.setPagoComisionVendedor(new Date());
					v.setImpComisionVent(c.getImporte());
					
				}else{
					v.setComisionCobrador(c.getComision());
					v.setPagoComisionCobrador(new Date());
					v.setImpComisionCob(c.getImporte());
				}
				v.setPagoComisionable(c.getPagosComisionables());
				c.setAplicado(true);
				ServiceLocator.getVentasManager().getVentasDao().salvar(v);
				
			}
			load();
			/*
			for(ComisionDeVentas cc:comisiones){
				int index=source.indexOf(cc);
				source.set(index,cc);
			}
			*/
			//load();
		}
		
	}
	
	public void cancelarComision(){
		if(!selectionModel.getSelected().isEmpty()){
			Object comentario=JOptionPane.showInputDialog(grid, "Motivo de cancelación", "Cancelación de comisión", JOptionPane.WARNING_MESSAGE);
			final List<ComisionDeVentas> comisiones=new ArrayList<ComisionDeVentas>();
			comisiones.addAll(selectionModel.getSelected());
			for(ComisionDeVentas c:comisiones){
				//c.setComision(0);
				final Venta v=ServiceLocator.getCXCManager().getVentasDao().buscarPorId(c.getVenta());
				if("vendedor".equals(target)){
					v.setComisionVenta(c.getComision());
					v.setPagoComisionVendedor(new Date());
					v.setImpComisionVent(BigDecimal.ZERO);
					
				}else{
					v.setComisionCobrador(c.getComision());
					v.setPagoComisionCobrador(new Date());
					v.setImpComisionCob(BigDecimal.ZERO);
				}
				v.setPagoComisionable(c.getPagosComisionables());
				c.setAplicado(true);
				v.setCancelComiVent(comentario.toString()); 
				ServiceLocator.getVentasManager().getVentasDao().salvar(v);
				
			}
			load();
			/*
			for(ComisionDeVentas cc:comisiones){
				int index=source.indexOf(cc);
				source.set(index,cc);
			}
			*/
		}
	}
	
	public void eliminarComision(){
		if(!selectionModel.getSelected().isEmpty()){			
			final List<ComisionDeVentas> comisiones=new ArrayList<ComisionDeVentas>();
			comisiones.addAll(selectionModel.getSelected());
			for(ComisionDeVentas c:comisiones){
				c.setComision(0);
				final Venta v=ServiceLocator.getCXCManager().getVentasDao().buscarPorId(c.getVenta());
				if("vendedor".equals(target)){
					v.setComisionVenta(0);
					v.setPagoComisionVendedor(null);
					v.setImpComisionVent(BigDecimal.ZERO);
					
				}else{
					v.setComisionCobrador(0);
					v.setPagoComisionCobrador(null);
					v.setImpComisionCob(BigDecimal.ZERO);
				}
				v.setPagoComisionable(BigDecimal.ZERO);
				c.setAplicado(false);
				v.setCancelComiVent(null); 
				ServiceLocator.getVentasManager().getVentasDao().salvar(v);
				
			}
			load();
			/*
			for(ComisionDeVentas cc:comisiones){
				int index=source.indexOf(cc);
				source.set(index,cc);
			}
			*/
		}
	}
	
	public void cambiarPeriodo(){
		ValueHolder holder=new ValueHolder(periodo);
		AbstractDialog dialog=Binder.createPeriodoSelector(holder);
		dialog.open();
		if(!dialog.hasBeenCanceled()){			
			periodo=(Periodo)holder.getValue();
			load();
			actualizarHeader();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void printReport(){
		
		final Periodo per=new Periodo(periodo.getFechaInicial(),periodo.getFechaFinal());
		final ValueHolder holder=new ValueHolder(per);
		AbstractDialog dialog=Binder.createPeriodoSelector(holder);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			String reportPath="reportes/ComisionPorVendedor.jasper";
			if("cobrador".equals(target))
				reportPath="reportes/ComisionPorCobrador.jasper";
			final Map map=new HashMap();
			
			
			map.put("COBRADOR", StringUtils.isEmpty(vendedor.getText())?"%":vendedor.getText());
			map.put("FECHA_INI", per.getFechaInicial());
			map.put("FECHA_FIN", per.getFechaFinal());
			ReportUtils.viewReport(reportPath, map);
		}
	}
	
	

	
	
	public Action createAplicarAction(Object model,String method){
		Action aplicar=new DispatchingAction(model,method);
		if(Application.isLoaded()){
			Application.instance().getActionManager().configure(aplicar, "aplicarComision");
		}else{
			aplicar.putValue(Action.SHORT_DESCRIPTION, "Aplicar");
			aplicar.putValue(Action.LONG_DESCRIPTION, "Aplica la comision propuesta a las ComisionDeVentass seleccionadas");
			aplicar.putValue(Action.SMALL_ICON, getIconFromResource("images2/script_add.png"));
		}
		return aplicar;
	}
	
	private void actualizarTotales(){
		System.out.println("Actualizando totales:");
		if(!selectionModel.getSelected().isEmpty()){
			final List<ComisionDeVentas> comisiones=new ArrayList<ComisionDeVentas>();
			comisiones.addAll(selectionModel.getSelected());
			BigDecimal pag=BigDecimal.ZERO;
			BigDecimal comi=BigDecimal.ZERO;
			for(ComisionDeVentas c:comisiones){
				pag=pag.add(c.getPagosComisionables());
				comi=comi.add(c.getImporte());
			}
			pagos.setValue(pag.doubleValue());
			comision.setValue(comi.doubleValue());
		}
	}
	
	public void close(){	
		source.clear();
	}
	
	private Action createActionCom(){
		AbstractAction ac=new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				
				showReport c=new showReport();
				c.open();
			}
			
		};
		ac.putValue(Action.NAME, "ejecuta Reporte");
		return ac;
	}
	
	private class showReport extends SXAbstractDialog{
		Map<String, Object> parametros;
		
		public Map<String, Object> getParametros() {
			return parametros;
		}
		public showReport() {
			super("Reporte...");
			parametros=new HashMap<String, Object>();
		}

		public JComponent displayReport(){
			//Assert.isTrue( ("vendedor".equals(target)||"cobrador".equals(target)),"Esta vista requiere target cobrador/vendedor no permite: "+target);
			  net.sf.jasperreports.engine.JasperPrint jasperPrint = null;
              DefaultResourceLoader loader = new DefaultResourceLoader();
              Resource res = null;
			if("vendedor".equals(target)){
				 parametros.put("FECHA_INI",perlabel.getText());
	              res = loader.getResource("reportes/ComVend.jasper");
			}if("cobrador".equals(target)){
				 parametros.put("FECHA_INI",perlabel.getText());
	              res = loader.getResource("reportes/ComCob.jasper");
			}
			   
                try
                {
                    java.io.InputStream io = res.getInputStream();
                    try
                    {
                        jasperPrint = JasperFillManager.fillReport(io, getParametros(), new JRTableModelDataSource(tmodel));
                    }
                    catch(JRException e)
                    {
                        e.printStackTrace();
                    }
                }
                catch(IOException ioe)
                {
                    ioe.printStackTrace();
                }
                JRViewer jasperViewer = new JRViewer(jasperPrint);
                jasperViewer.setPreferredSize(new Dimension(1000, 600));
                return jasperViewer;
			
		/*	parametros.put("FECHA", picker.getDate());
			parametros.put("STATUS",status.getText() );
			parametros.put("AVISO", aviso.getText());
			JasperPrint jasperPrint = null;
			try {
				jasperPrint = JasperFillManager.fillReport("src/main/resources/sql/ClientesGeneral.jasper", getParametros(),
						new JRTableModelDataSource(tm));
			} catch (JRException e) {
				e.printStackTrace();
			}
			JRViewer jasperViewer = new JRViewer(jasperPrint);
            jasperViewer.setPreferredSize(new Dimension(1000,600));
           
             return jasperViewer;*/
			}

		@Override
		protected JComponent buildContent() {
			return displayReport();
		}

		@Override
		protected void setResizable() {
		setResizable(true);
		}
		
	}
	
	public void mostrarVentaMensual(){
		if(!selectionModel.getSelected().isEmpty()){
			@SuppressWarnings("unused")
			ComisionDeVentas c=selectionModel.getSelected().get(0);
			//ServiceLocator.getVentasManager().getVentasDao().acumuladoDelMesCredito(cliente, year, mes)
		}
	}
	
	private class AplicablesEditor extends CheckBoxSelector<ComisionDeVentas>{
		

		public void unSelect() {			
			System.out.println("Detonando");
			fireChanged(new PorAplicableMatcher());
		}

		@Override
		protected Matcher<ComisionDeVentas> getSelectMatcher(Object... obj) {
			return new AplicableMatcher();
		}
		
		private class AplicableMatcher implements Matcher<ComisionDeVentas>{
			public boolean matches(ComisionDeVentas item) {				
				return item.isAplicado();
			}
		}
		
		private class PorAplicableMatcher implements Matcher<ComisionDeVentas>{
			public boolean matches(ComisionDeVentas item) {				
				return !item.isAplicado();
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){
			
			final ComisionesView view=new ComisionesView("vendedor");

			
			
			@Override
			protected JComponent buildContent() {	
				//setModal(false);
				return view.getContent();
			}

			@Override
			protected JComponent buildHeader() {
				return view.header;
			}

			@Override
			protected void setResizable() {
				setResizable(true);
			}

			@Override
			protected void onWindowOpened() {
				//view.load();
			}	
			
			
			
		};
		//dialog.setModal(false);
		dialog.open();		
		System.exit(0);
	}
	
	


}
