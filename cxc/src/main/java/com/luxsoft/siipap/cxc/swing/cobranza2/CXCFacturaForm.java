package com.luxsoft.siipap.cxc.swing.cobranza2;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Mantenimiento de  factura
 * Lo mas importante de este componente es que pueda dar altas y bajas
 * de pagos por factura, de manera correcta * 
 * Delegando para esto la responsabilidad a el componente CXCPAgosController
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class CXCFacturaForm extends SXAbstractDialog{
	
	private final CXCFacturaFormModel model;
	private CXCPagosController pagosController;
	
	private EventList<NotasDeCreditoDet> notas;
	private EventList<Pago> pagos; 
	private EventSelectionModel<Pago> selection;
	private JXTable pagosGrid;
	private JXTable notasGrid;
	
	
	private JFormattedTextField numero;
	private JComponent fecha;
	private JFormattedTextField total;
	private JFormattedTextField saldo;

	public CXCFacturaForm(final CXCFacturaFormModel model) {
		super("Mantenimiento de Pagos por Factura");
		this.model=model;
	}
	
	private void initComponents(){		
		numero=Binder.createNumberBinding(model.getFacturaModel().getComponentModel("numero"),0);
		numero.setFocusable(false);
		fecha=BasicComponentFactory.createDateField(model.getFacturaModel().getComponentModel("fecha"));
		fecha.setFocusable(false);
		total=Binder.createCantidadMonetariaBinding(model.getFacturaModel().getComponentModel("total"));
		total.setFocusable(false);
		saldo=Binder.createMonetariNumberBinding(model.getFacturaModel().getComponentModel("saldo"));
		saldo.setFocusable(false);
	}

	@Override
	protected JComponent buildContent() {
		initComponents();		
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildFormPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithClose(),BorderLayout.SOUTH);
		return panel;
	}
	
	protected JComponent buildFormPanel(){
		FormLayout layout=new FormLayout("p","p,3dlu,40dlu");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(buildEditorPanel(),cc.xy(1, 1));
		builder.add(buildValidationViewPanel(),cc.xy(1, 3));
		return builder.getPanel();		
	}
	
	private JComponent buildEditorPanel(){
		FormLayout layout=new FormLayout(
				"l:40dlu,2dlu,max(p;70dlu), 2dlu, " +
				"l:40dlu,2dlu,max(p;70dlu):g"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);		
		builder.append("Numero",numero);
		builder.append("Fecha",fecha);
		builder.append("Total",total);
		builder.append("Saldo",saldo);
		builder.nextLine();
		builder.appendSeparator("Notas de Credito");
		builder.nextLine();
		builder.append(buildNotasGrid(),7);		
		builder.appendSeparator("Pagos");		
		builder.append(buildPagosGrid(),7);
		JPanel p=builder.getPanel();
		return p;
	}
	
	private JComponent buildNotasGrid(){
		final String[] props={"id","nota.numero","tipo","serie","fecha","importe"};
		final String[] cols={"Id","numero","tipo","serie","Fecha","importe"};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class,props, cols);
		notas=GlazedLists.threadSafeList(new BasicEventList<NotasDeCreditoDet>());
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(notas,tf);
		notasGrid=ComponentUtils.getStandardTable();
		notasGrid.setModel(tm);
		JComponent c=UIFactory.createTablePanel(notasGrid);
		c.setPreferredSize(new Dimension(300,80));
		return c;		
	}
	
	private JComponent buildPagosGrid(){
		final JPanel pp=new JPanel(new BorderLayout());		
		final Action ia=CommandUtils.createInsertAction(this, "aplicarPago");
		final Action da=CommandUtils.createDeleteAction(this, "delete");
		final Action ea=CommandUtils.createEditAction(this, "edit");
		final ToolBarBuilder builder=new ToolBarBuilder();
		builder.add(ia);builder.add(da);builder.add(ea);
		pp.add(builder.getToolBar(),BorderLayout.NORTH);
		
		final String[] props={"id","formaDePago","referencia","fecha","importe","comentario","notaDelPago.numero"};
		final String[] cols={"id","F.Pago","Referencia","Fecha","importe","comentario","Nota(Pago)"};
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class,props, cols);
		pagos=GlazedLists.threadSafeList(new BasicEventList<Pago>());
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(pagos,tf);
		selection=new EventSelectionModel<Pago>(pagos);
		selection.setSelectionMode(ListSelection.SINGLE_SELECTION);
		pagosGrid=ComponentUtils.getStandardTable();
		pagosGrid.setModel(tm);
		pagosGrid.setSelectionModel(selection);
		JComponent c=UIFactory.createTablePanel(pagosGrid);
		c.setPreferredSize(new Dimension(350,100));
		pp.add(c,BorderLayout.CENTER);		
		return pp;
		
	}
	
	protected JComponent buildValidationViewPanel(){
		return ValidationResultViewFactory.createReportList(model.getValidationModel());
		
	}
	
	public void delete(){
		if(!selection.getSelected().isEmpty()){
			if(getPagosController()!=null){
				Pago p=selection.getSelected().get(0);
				boolean val=getPagosController().eliminarPago(p);
				if(val){
					selection.getSelected().clear();
					model.refreshVenta();
				}
			}	
		}
	}
	
	public void aplicarPago(){
		if(getPagosController()!=null ){
			final Pago p=getPagosController().aplicarPago(model.getFactura());
			if(p!=null){
				pagos.add(p);
				model.refreshVenta();
			}
			
			
		}		
	}
	
	public void edit(){
		if(!selection.getSelected().isEmpty()){
			if(getPagosController()!=null){
				final Pago p=selection.getSelected().get(0);
				getPagosController().editarPago(p);
			}
		}
	}
	
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel(model.getFacturaModel().getModel("clave").toString()
				,model.getFacturaModel().getModel("nombre").toString()
				,getIconFromResource("images/siipapwin/cxc64.gif"));
	}

	private void load(){
		pagos.addAll(model.getPagos());
		notas.addAll(model.getNotas());
		notasGrid.packAll();
		pagosGrid.packAll();
	}
	
	@Override
	protected void onWindowOpened() {
		load();
	}
	
	/** Collaboradores ***/
	
	public CXCPagosController getPagosController() {
		return pagosController;
	}

	public void setPagosController(CXCPagosController pagosController) {
		this.pagosController = pagosController;
	}
	

	public static void main(String[] args) {
		
		//final Venta v=DatosDePrueba.ventasDePrueba().get(0);
		//Buscamos una venta con saldo
		final Venta v=DatosDePrueba.buscarVenta(2476024L);
		
		//Configuramos el controlador de pagos
		final CXCPagosController pagosController=new CXCPagosController();
		pagosController.setManager(ServiceLocator.getCXCManager());
		pagosController.setVentasManager(ServiceLocator.getVentasManager());
		
		//Preparamos el modelo para la forma
		final CXCFacturaFormModel model=new CXCFacturaFormModel(v);
		model.setCxcManager(ServiceLocator.getCXCManager());
		model.setVentasManager(ServiceLocator.getVentasManager());
		//Mostramos la forma
		final CXCFacturaForm dialog=new CXCFacturaForm(model);		
		dialog.setPagosController(pagosController);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			
		}
		
	}

	

}
