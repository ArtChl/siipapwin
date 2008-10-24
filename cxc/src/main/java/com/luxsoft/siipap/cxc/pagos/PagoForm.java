package com.luxsoft.siipap.cxc.pagos;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.consultas.ConsultaUtils;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para la generacion de pagos
 * 
 * @author Ruben Cancino
 *
 */
public class PagoForm extends SXAbstractDialog{
	
	
	
	private final PagosModel model;
	private EventSelectionModel<Pago> selectionModel;
	private JXTable grid;
	
	private Action insertAction;
	private Action deleteAction;
	
	/**Form components **/
	private JComponent fecha;
	private JComboBox formaDePago;
	private JTextField referencia;
	private JComboBox bancoOrigen;
	private JComboBox cuentaDeposito;
	private JFormattedTextField importe;	
	private JFormattedTextField saldoTotal;	
	private JFormattedTextField disponible;
	private JTextArea comentario;
	private JCheckBox condonar;
	private JTextField tipoDoctos;
	private JFormattedTextField saldoPendiente;
	
	private HeaderPanel header;
	
	private Logger logger=Logger.getLogger(getClass());
	
	public PagoForm(final PagosModel pagoModel){
		super("Pagos");
		this.model=pagoModel;
	}
	
	private void initComponents(){
		fecha=Binder.createDateComponent(model.getPagoMPModel().getModel("fecha"));
		formaDePago=CXCBindings.createFormaDePagoBinding(model.getPagoMPModel().getModel("formaDePago"));
		formaDePago.setEnabled(false);
		referencia=BasicComponentFactory.createTextField(model.getPagoMPModel().getComponentModel("referencia"));
		referencia.setEnabled(false);
		bancoOrigen=CXCBindings.createBancosBinding(model.getPagoMPModel().getModel("banco"));
		bancoOrigen.setEnabled(false);
		cuentaDeposito=CXCBindings.createCuentasDeposito(model.getPagoMPModel().getModel("cuentaDeposito"));
		cuentaDeposito.setEnabled(false);
		importe=Binder.createCantidadMonetariaBinding(model.getPagoMPModel().getComponentModel("importe"));
		importe.setEditable(false);
		disponible=Binder.createMonetariNumberBinding(model.getPagoMPModel().getComponentModel("disponible"));
		saldoTotal=Binder.createCantidadMonetariaBinding(model.getPorPagar());
		comentario=BasicComponentFactory.createTextArea(model.getPagoMPModel().getComponentModel("comentario"),false);
		condonar=BasicComponentFactory.createCheckBox(model.getPagoMPModel().getComponentModel("condonar"), "Pagar sin cargos");
		tipoDoctos=BasicComponentFactory.createTextField(model.getPagoMPModel().getComponentModel("tipoDeDocumento"));
		header=new HeaderPanel("Pago de facturas "+model.getPagoMPModel().getValue("tipoDeDocumento")
				,model.getPagoMPModel().getValue("cliente").toString()
				,getIconFromResource("images/siipapwin/cxc64.gif"));
		saldoPendiente=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
		saldoPendiente.setEnabled(false);
		saldoPendiente.setToolTipText("Saldo pendiente de todas las facturas ");
	}
	
	private void initDecorateComponents(){
		ValidationComponentUtils.setMandatory(importe, true);
		ValidationComponentUtils.setMessageKey(importe, "Pago.importe");
		ValidationComponentUtils.setMessageKey(referencia, "Pago.[referencia]");
		ValidationComponentUtils.setMessageKey(comentario, "Pago.comentario");
		ValidationComponentUtils.setMessageKey(formaDePago, "Pago.formaDePago");
	}
	
	private void initEventHandling(){	
		System.out.println("Inicializando bean..");
		model.getPagoMPModel().addBeanPropertyChangeListener(new ProcesoHandler());
		ValidationHandler handler=new ValidationHandler();		
		model.getValidationModel().addPropertyChangeListener(handler);
		model.getPagoMPModel().addBeanPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				Number saldo=model.getPendienteDespuesDePago().amount();
				saldoPendiente.setValue(saldo);
			}			
		});
		
	}
	
	protected JComponent getControl(){
		return (JComponent)getContentPane();
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		initDecorateComponents();
		initEventHandling();
		JPanel panel=new JPanel(new BorderLayout());		
		panel.add(createMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	protected JComponent createMainPanel(){
		final FormLayout layout=new FormLayout(
				"max(p;380dlu)"
				,"p,3dlu,220dlu,3dlu,50dlu");
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		builder.add(createMasterPanel(),cc.xy(1, 1));
		builder.add(createDetailsPanel(),cc.xy(1, 3));
		builder.add(createValidationView(),cc.xy(1,5));
		//model.updateValidation();
		updateComponentTreeMandatoryAndSeverity(model.getValidationModel().getResult(), builder.getPanel());
		return builder.getPanel();
	}
	
	JButton locButton;
	
	private JButton createLocbutton(){
		if(locButton==null){
			locButton=new JButton("Localizar pago");
		}
		return locButton;
	}
	
	private JComboBox buildPagosbox(final ValueModel vm){
		SelectionInList sl=new SelectionInList(model.buscarDepositosDisponibles(),vm);
		return BasicComponentFactory.createComboBox(sl);
	}
	
	
	
	
	protected JComponent createMasterPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,f:max(p;50dlu):g(.3) ,3dlu " +
				"l:p,2dlu,f:max(p;50dlu):g(.3) "
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setRowGroupingEnabled(true);
		builder.append("Fecha",fecha);
		builder.append("F. Pago",formaDePago,true);
		builder.append("Banco",bancoOrigen);
		builder.append("Referencia",referencia,true);
		builder.append("Cuenta Depósito",cuentaDeposito,true);
		builder.append("Importe",importe);
		builder.append("Pago",buildPagosbox(model.getPagoMPModel().getModel("depositoRow")),true);
		builder.append("Disponible",disponible);
		builder.append("Por pagar",saldoTotal);
		builder.append("Pendiente",saldoPendiente,true);
		
		final CellConstraints cc=new CellConstraints();		
		builder.append("Comentario");
		builder.appendRow(new RowSpec("17dlu"));
		builder.add(new JScrollPane(comentario),
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		builder.nextLine(2);
		builder.append("Condonar",condonar);
		builder.append("Tipo de Facturas",tipoDoctos,true);
		ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent createDetailsPanel(){
				
		final TableFormat<Pago> tf=CXCTableFormats.getPagoConVentaTF();
		final EventList<Pago> pagos=model.getPagos();
		final SortedList<Pago> spagos=new SortedList<Pago>(pagos,null);
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(spagos,tf);
		selectionModel=new EventSelectionModel<Pago>(spagos);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser<Pago>(grid,spagos,true);
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addInsertAction(grid,getInsertAction());
		ComponentUtils.addDeleteAction(grid, getDeleteAction());
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {				
				if(e.getClickCount()==2)
					view();
			}			
		});
		grid.getColumn("Desc (%)").setCellRenderer(Renderers.getPorcentageRenderer());
		grid.getColumn("Cgo").setCellRenderer(Renderers.getPorcentageRenderer());
		grid.getColumn("Desc Aplic").setCellRenderer(Renderers.getCantidadNormalTableCellRenderer());
		final JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	private JComponent createValidationView(){
		return ValidationResultViewFactory.createReportList(model.getValidationModel());
	}	
	
	@Override
	protected JComponent buildHeader() {
		return header;		
	}

	private void updateForm(){
		if(logger.isDebugEnabled()){
			logger.debug("Actualizando validaciones");
		}
		updateComponentTreeMandatoryAndSeverity(model.getValidationModel().getResult(), getControl());
		getOKAction().setEnabled(!model.getValidationModel().hasErrors());
		grid.packAll();
		
	}
	
	private void updateComponentTreeMandatoryAndSeverity(final ValidationResult result,final JComponent panel) {
		 ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(panel);
	     ValidationComponentUtils.updateComponentTreeSeverityBackground(panel,result);
	}
	
	@Override
	protected void onWindowOpened() {
		iniciarPago();
		
	}

	/** LifeCycle **/
	
	public void iniciarPago(){		
		grid.packAll();
		//model.validate();
		updateForm();
	}

	
	
	/*
	 * Registro del importe del pago
	 * 
	 * @param model
	 */
	public void registrarImporte(final PagosModel model){
		if(logger.isDebugEnabled()){
			logger.debug("Registrandi un importe de: "+model.getPagoMPModel().getValue("importe"));
		}
		/*
		if(model.importeRegistrado()){
			JOptionPane.showMessageDialog(getControl(),
					"El importe del pago es inferior al saldo de las facturas" +
					" seleccionadas" +
					"\nSe aplicara a las facturas en el orden actual de las mismas" +
					"\nLos pagos en cero no serán aplicados" );
		}*/
		
	}
	
	public Action getInsertAction(){
		if(insertAction==null){
			insertAction=CommandUtils.createInsertAction(this, "inser");
		}
		return insertAction;
	}
	protected Action getDeleteAction(){
		if(deleteAction==null){
			deleteAction=CommandUtils.createDeleteAction(this, "delete");
		}
		return deleteAction;
	}
	
	public void view(){
		if(selectionModel.getSelected().isEmpty())
			return;
		Venta v=((Pago)selectionModel.getSelected().get(0)).getVenta();
		ConsultaUtils.mostrarVenta(v);
	}
	
	/**
	 * Acceso al modelo
	 * 
	 * @return
	 */
	public PagosModel getModel() {
		return model;
	}

	/**
	 * Controla el proceso de pago en base a los cambios en las propiedades
	 * del bean PagoM
	 * 
	 * @author RUBEN
	 *
	 */
	private class ProcesoHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			if("formaDePago".equals(evt.getPropertyName())){
				
			}else if("importe".equals(evt.getPropertyName())){
				grid.packAll();
				registrarImporte(model);
			}
			
		}
	}
	
	/**
	 * Escucha los cambios en el bean para habilitar o deshabilitar
	 * la accion para terminacion del proces, solo en caso de que exista
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class ValidationHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			//System.out.println("Actualizando validacion..");
			updateForm();
		}
	}

}
