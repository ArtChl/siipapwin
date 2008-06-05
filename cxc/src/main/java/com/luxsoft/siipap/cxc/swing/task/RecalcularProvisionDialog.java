package com.luxsoft.siipap.cxc.swing.task;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.Validator;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Permite de forma manual actualizar:
 * 
 *  Todas las ventas o 
 *  
 *  Una venta en particular
 *  
 *  TODO Cambiar nombres para hacer referencia a venta y no a provision
 * 
 * @author Ruben Cancino
 *
 */
public class RecalcularProvisionDialog extends SXAbstractDialog{
	
	private final RecalculoModel model;
	private JCheckBox box;
	private JFormattedTextField numero;
	private JPanel editorPanel;
	
	

	public RecalcularProvisionDialog() {
		super("Actualizar Ventas");
		model=new RecalculoModel();
		model.getValidationModel().addPropertyChangeListener(ValidationResultModel.PROPERTYNAME_RESULT,
				new ValidationChangeHandler());
	}
	
	private void initComponents(){
		box=BasicComponentFactory.createCheckBox(model.getModel("porFactura"), "");
		numero=BasicComponentFactory.createLongField(model.getModel("numero"));
		ValidationComponentUtils.setMandatory(numero, true);
		ValidationComponentUtils.setMessageKey(numero, "Venta.ID");
		numero.setEnabled(false);
		box.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				numero.setEnabled(box.isSelected());
				if(!box.isSelected()){
					numero.setValue(new Long(0));
					
				}
			}			
		});
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel content=new JPanel(new BorderLayout());
		content.add(buildEditorPanel(),BorderLayout.CENTER);
		content.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return content;
	}

	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Actualización de Provisiones"
				,"Permite actualizar todas las provisiones o la de una venta\n en particular"
				,getIconFromResource("images/siipapwin/cxc64.gif"));
	}
	
	private JComponent buildEditorPanel(){
		FormLayout layout=new FormLayout("l:40dlu,3dlu,p,3dlu,l:p,3dlu,100dlu","p,3dlu,30dlu");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();		
		int row=1;
		builder.add(new JLabel("Por venta"),cc.xy(1, row));
		builder.add(box,cc.xy(3, row));
		builder.addLabel("Id", cc.xy(5, row));
		builder.add(numero,cc.xy(7, row));
		builder.add(ValidationResultViewFactory.createReportIconAndTextPane(model.getValidationModel()),cc.xyw(1, 3,7));
		editorPanel= builder.getPanel();
		return editorPanel;
	}
	public RecalculoModel getModel() {
		return model;
	}
	
	private void updateComponentTreeMandatoryAndSeverity(ValidationResult result) {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(editorPanel);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(editorPanel,result);
    }
	
	 /**
     * Updates the component background in the mandatory panel and the
     * validation background in the severity panel. Invoked whenever
     * the observed validation result changes. 
     */
    private final class ValidationChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
        	System.out.println("Validando");
            updateComponentTreeMandatoryAndSeverity(
                    (ValidationResult) evt.getNewValue());
        }
    }
    
	
	public class RecalculoModel extends PresentationModel implements Validator{
		
		private long numero;
		private boolean porFactura;
		private final ValidationResultModel validationModel;		
		private VentasDao ventaDao;

		public RecalculoModel() {
			super(null);
			setBean(this);
			validationModel=new DefaultValidationResultModel();
			addBeanPropertyChangeListener(validationHandler);
		}

		public long getNumero() {
			return numero;
		}

		public void setNumero(long numero) {
			long old=this.numero;
			this.numero = numero;
			firePropertyChange("numero",old, numero);
		}
		

		public boolean isPorFactura() {
			return porFactura;
		}

		public void setPorFactura(boolean porFactura) {
			boolean old=this.porFactura;
			this.porFactura = porFactura;
			firePropertyChange("porFactura", old, porFactura);
		}
		
		public ValidationResultModel getValidationModel(){
			return this.validationModel;
		}
		

		public ValidationResult validate() {
			PropertyValidationSupport support=new PropertyValidationSupport(RecalculoModel.this,"Venta");
			if(isPorFactura()){
				if(getNumero()<0)
					support.addError("ID","Venta ID invalido");
				else{
					if(getVentaDao()!=null){
						Venta v=getVentaDao().buscarPorId(getNumero());
						if(v==null)
							support.addError("ID", "No existe la venta con ID: "+getName());						
					}else
						System.out.println("No existe dao para validar la venta_id");
				}
			}
			
			return support.getResult();
		}
		
		private PropertyChangeListener validationHandler =new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				validationModel.setResult(validate());
			}	
		};		

		public VentasDao getVentaDao() {
			return ventaDao;
		}

		public void setVentaDao(VentasDao ventaDao) {
			this.ventaDao = ventaDao;
		}
		
		public String toString(){
			return "Recalcular por factura: "+isPorFactura()+" ID: "+getNumero();
		}

		
		
	}
	
	public static void main(String[] args) {
		RecalcularProvisionDialog dialog=new RecalcularProvisionDialog();
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			System.out.println("Ejecutando: "+dialog.getModel().getBean());
		}
	}

	

}
