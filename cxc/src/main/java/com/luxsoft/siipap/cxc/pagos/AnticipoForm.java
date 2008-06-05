package com.luxsoft.siipap.cxc.pagos;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.form2.FormUtils;

/**
 * Forma para la generación de anticipos
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class AnticipoForm extends SXAbstractDialog{
	
	private final AnticipoFormModel model;
	
	
	private JComponent fecha;
	private JComponent banco;
	private JComponent referencia;
	private JComponent cuentaDeposito;
	private JComponent importe;
	private JComponent comentario;
	private boolean readOnly=false;

	public AnticipoForm(final AnticipoFormModel model) {
		super("Anticipos");
		this.model=model;
		this.model.getValidationModel().addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				getOKAction().setEnabled(!model.getValidationModel().hasErrors());
				updateComponentTreeMandatoryAndSeverity(model.getValidationModel().getResult(), (JComponent)getContentPane());
			}			
		});
		
	}
	
	private void initComponents(){
		if(readOnly){
			fecha=Binder.createDateComponent(model.getComponentModel("fecha"));
			banco=BasicComponentFactory.createTextField(model.getModel("banco"));
			cuentaDeposito=BasicComponentFactory.createTextField(model.getModel("cuentaDeposito"));
		}else{
			fecha=Binder.createDateComponent(model.getModel("fecha"));
			banco=CXCBindings.createBancosBinding(model.getModel("banco"));
			cuentaDeposito=CXCBindings.createCuentasDeposito(model.getModel("cuentaDeposito"));
		}
		referencia=BasicComponentFactory.createTextField(model.getModel("referencia"));
		comentario=BasicComponentFactory.createTextArea(model.getModel("comentario"), false);
		importe=Binder.createCantidadMonetariaBinding(model.getModel("importe"));
		
	}
	
	@Override
	protected JComponent buildContent() {
		initComponents();
		decorateComponents();
		final FormLayout layout=new FormLayout(
				"p:g"
				,"p,3dlu,min(40dlu;p),3dlu,p");
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		builder.add(createFormPanel(),cc.xy(1, 1));
		if(!isReadOnly())
			builder.add(createValidationView(),cc.xy(1,3));
		
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(builder.getPanel(),BorderLayout.CENTER);
		if(isReadOnly()){
			panel.add(buildButtonBarWithClose(),BorderLayout.SOUTH);
		}else
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		
		return panel;
	}
	
	/**
	 * Crea el panel principal de la forma, es decir el panel con los controles
	 * para la captura, usando DefulaFormBuilder y JGoodies' FormLayout
	 * @return
	 */
	protected JComponent createFormPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,3dlu,f:max(p;70dlu):g "				
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setRowGroupingEnabled(true);
		//builder.appendTitle("Registro de Anticipo");
		
		builder.append("Fecha",fecha,true);
		builder.append("Banco",banco,true);
		builder.append("Referencia",referencia,true);
		builder.append("Cuenta Destino",cuentaDeposito,true);
		builder.append("Importe",importe,true);
		final CellConstraints cc=new CellConstraints();		
		builder.append("Comentario");
		builder.appendRow(new RowSpec("17dlu"));
		builder.add(new JScrollPane(comentario),
				cc.xywh(builder.getColumn(), builder.getRow(),1,2));
		//builder.nextLine(2);
		
		if(isReadOnly())
			FormUtils.disableAllComponents(builder.getPanel());
		else
			updateComponentTreeMandatoryAndSeverity(model.validate(), builder.getPanel());
		
		return builder.getPanel();
	}
	
	
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Anticipo","Registro y/o consulta de anticipo a clientes");
	}

	/**
	 * Crea un panel para mostrar los resultados de validacion
	 * 
	 * @return
	 */	
	protected JComponent createValidationView(){
		return ValidationResultViewFactory.createReportList(model.getValidationModel());
	}
	
	/**
	 * Actualiza en funcion del resultado de validacion el panel de captura
	 * 
	 * @param result
	 * @param panel
	 */
	protected void updateComponentTreeMandatoryAndSeverity(final ValidationResult result,final JComponent panel) {
		if(isReadOnly()) return;		
		 ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(panel);
	     ValidationComponentUtils.updateComponentTreeSeverityBackground(panel,result);
	}
	
	
	private void decorateComponents(){
		ValidationComponentUtils.setMandatory(importe, true);
		ValidationComponentUtils.setMessageKey(importe, "PagoM.importe");
		
		ValidationComponentUtils.setMandatory(referencia, true);		
		ValidationComponentUtils.setMessageKey(referencia, "PagoM.referencia");
		
		ValidationComponentUtils.setMandatory(banco, true);		
		ValidationComponentUtils.setMessageKey(banco, "PagoM.banco");
	}


	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}	

	public AnticipoFormModel getModel() {
		return model;
	}	
	

}
