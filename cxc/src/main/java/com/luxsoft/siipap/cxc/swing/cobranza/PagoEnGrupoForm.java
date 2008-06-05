package com.luxsoft.siipap.cxc.swing.cobranza;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.ObservableElementList.Connector;
import ca.odell.glazedlists.gui.TableFormat;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.cxc.swing.binding.CantidadMonetariaBinding;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.crud.BasicCURDGridPanel;
import com.luxsoft.siipap.swing.form.Form;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.form.FormModel;

import com.luxsoft.siipap.swing.utils.BrowserUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para pagar un grupo de facturas
 * 
 * @author Ruben Cancino
 *
 */
public class PagoEnGrupoForm implements Form{
	
	private final PagoEnGrupoFormModel model;
	
	
	
	private JComponent fecha;
	private JComboBox formaDePago;
	private JTextField referencia;
	private JComboBox banco;
	private JComponent importe;
	private JTextField comentario;
	private JFormattedTextField saldo;
	private JFormattedTextField saldoFacturas;
	private JFormattedTextField porPagar;
	private JCheckBox aplicarD;
	private JCheckBox condonar;
	
	
	
	
	@SuppressWarnings("unchecked")
	public PagoEnGrupoForm(final List ventas){
		this(new PagoEnGrupoFormModel(ventas));		
	}
	
	public PagoEnGrupoForm(final PagoEnGrupoFormModel model){
		this.model=model;
		getFormModel().getValidationModel().addPropertyChangeListener(
				ValidationResultModel.PROPERTYNAME_RESULT,
				new ValidationChangeHandler());
	}

	public FormModel getFormModel() {
		return model;
	}
	
	private void initComponents(){
		ComponentValueModel fvm=model.getComponentModel("fecha");
		if(fvm.isEnabled())
			fecha=Binder.createDateComponent(fvm);
		else
			fecha=BasicComponentFactory.createDateField(fvm);
		formaDePago=CXCBindings.createFormaDePagoBinding(model.getModel("formaDePago"));
		referencia=BasicComponentFactory.createTextField(model.getModel("referencia"));
		banco=CXCBindings.createBancosBinding(model.getModel("banco"));
		comentario=BasicComponentFactory.createTextField(model.getModel("comentario"));
		CantidadMonetariaBinding b1=new CantidadMonetariaBinding(model.getModel("importe"));
		importe=b1.getControl();		
		CantidadMonetariaBinding b2=new CantidadMonetariaBinding(model.getModel("saldo"));
		saldo=b2.getControl();
		saldo.setEditable(false);
		saldo.setFocusable(false);
		CantidadMonetariaBinding b3=new CantidadMonetariaBinding(model.getModel("saldoFacturas"));
		saldoFacturas=b3.getControl();
		saldoFacturas.setEditable(false);
		saldoFacturas.setFocusable(false);
		
		CantidadMonetariaBinding b4=new CantidadMonetariaBinding(model.getModel("porPagar"));
		porPagar=b4.getControl();
		porPagar.setEditable(false);
		porPagar.setFocusable(false);
		
		aplicarD=BasicComponentFactory.createCheckBox(model.getComponentModel("aplicarDeMenos"), "Ajuste menor a $100.00");
		aplicarD.setToolTipText("Genera un pago automatico tipo D al concepto de otros productos");
		condonar=BasicComponentFactory.createCheckBox(model.getComponentModel("condonarAbono"), "Condonar cargos");
	}
	
	private void initDecorateComponents(){
		ValidationComponentUtils.setMandatory(importe, true);
		ValidationComponentUtils.setMessageKey(importe, "Importe ");
	}
	
	private void initEventHandlig(){
		
	}
	public void updateComponents(){
		
	}
	

	public JComponent getControl() {
		initComponents();
		initDecorateComponents();
		initEventHandlig();
		final FormLayout layout=new FormLayout("max(p;380dlu)"
				,"p,3dlu,f:max(120dlu;p):g,3dlu,50dlu");
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		builder.add(createEditorPanel(),cc.xy(1, 1));
		builder.add(createFacturasPanel(),cc.xy(1, 3));
		//builder.add(createValidationView(),cc.xy(1,5));
		model.updateValidation();
		updateComponentTreeMandatoryAndSeverity(getFormModel().getValidationModel().getResult(), builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent createEditorPanel(){
		FormLayout layout=new FormLayout("l:50dlu,3dlu,max(p;90dlu),5dlu,l:50dlu,3dlu,max(p;90dlu)","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		//builder.setDefaultDialogBorder();
		builder.append("Fecha",fecha,true);
		builder.append("Forma de Pago",formaDePago);
		builder.append("Referencia",referencia,true);
		builder.append("Banco",banco,true);
		builder.append("Importe",importe);
		builder.append("Cargos",condonar,true);
		builder.append("Por Pagar",porPagar);
		builder.append("Saldo F.",saldoFacturas,true);
		builder.append("Diferencia",saldo);
		builder.append("Aplicar ",aplicarD,true);
		builder.append("Comentario",comentario,5);
		
		return builder.getPanel();
	}
	
	@SuppressWarnings("unchecked")
	private JComponent createFacturasPanel(){
		String[] props={"numero","fecha","total","saldo","descuento1","descuento1Numero",
				//"provision.descuento1Real",
				"provision.descuento1",
				"provision.descuentoNeto1",
				//"provision.descuentoFinal",				
				"pago"};
		String[] labels={"Factura","Fecha","Total","Saldo","Desc","Nota D1","Provisión","Prov (Cargo)","Pago"};
		
		TableFormat tf=GlazedLists.tableFormat(props,labels);
		Connector connector=GlazedLists.beanConnector(Venta.class);
		EventList source=new ObservableElementList(model.getVentas(),connector);
		BasicCURDGridPanel ventasPanel=new BasicCURDGridPanel(source,tf);
		ventasPanel.getControl();
		ventasPanel.getGrid().getColumnModel().getColumn(1).setCellRenderer(BrowserUtils.getDateRenderer());
		ventasPanel.getGrid().getColumnModel().getColumn(2).setCellRenderer(Renderers.getCantidadMonetariaTableCellRenderer());
		ventasPanel.getGrid().getColumnModel().getColumn(3).setCellRenderer(Renderers.getCantidadNormalTableCellRenderer());
		ventasPanel.getGrid().getColumnModel().getColumn(4).setCellRenderer(Renderers.getPorcentageRenderer());
		ventasPanel.getGrid().getColumnModel().getColumn(6).setCellRenderer(Renderers.getPorcentageRenderer());
		ventasPanel.getGrid().getColumnModel().getColumn(7).setCellRenderer(Renderers.getPorcentageRenderer());
		ventasPanel.getGrid().getColumnModel().getColumn(8).setCellRenderer(Renderers.getCantidadMonetariaTableCellRenderer());
		ventasPanel.getControl();
		ventasPanel.pack();
		return ventasPanel.getControl();
	}
	
	
	public PagoEnGrupoFormModel getPagoEnGrupoModel(){
		return this.model;
	}
	
	
	

	public void setEnabled(boolean enabled) {
		//No se ocupa aqui
	}
	
	 private void updateComponentTreeMandatoryAndSeverity(final ValidationResult result,final JComponent panel) {
		 ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(panel);
	     ValidationComponentUtils.updateComponentTreeSeverityBackground(panel,result);
	 }
	
	/**
     * Updates the component background in the mandatory panel and the
     * validation background in the severity panel. Invoked whenever
     * the observed validation result changes. 
     */
    private final class ValidationChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            updateComponentTreeMandatoryAndSeverity(
                    (ValidationResult) evt.getNewValue(),getControl());
        }
    }
    
    
    public static void aplicarPago(List<Venta> ventas){
    	
		PagoEnGrupoFormModel model=new PagoEnGrupoFormModel(ventas);		
		PagoEnGrupoForm form=new PagoEnGrupoForm(model);		
		FormDialog d=new FormDialog(form);
		d.setTitle("Pagos con descuentos");
		d.setDescription("Pago de una o más facturas para el cliente: "+model.getFormBean().getClave()+" "+model.getFormBean().getCliente());
		d.setIconPath("images/siipapwin/cxc64.gif");		
		try {
			d.open();
			if(!d.hasBeenCanceled()){
				PagoEnGrupoResult r=new PagoEnGrupoResult(model.getPagos(),model.getNotasDet(),d);
				r.open();
				if(!r.hasBeenCanceled()){
					model.persistir();
					r.dispose();
				}
				d.dispose();
			}
			
		} catch (Exception e) {
			MessageUtils.showError("Error salvando pagos", e);
		}finally{
			d.dispose();
		}
	}
	
	public static void main(String[] args) {
		List<Venta> ventas=DatosDePrueba.ventasDePrueba();		
		aplicarPago(ventas);
	}

}
