package com.luxsoft.siipap.swing.form2;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.form2.BasicBindingFactory;
import com.luxsoft.siipap.swing.form2.BindingFactory;
import com.luxsoft.siipap.swing.form2.FormControl;
import com.luxsoft.siipap.swing.form2.IFormModel;

/**
 * Mantenimiento al catalogo de clientes
 * 
 * @author Ruben Cancino
 *
 */
public abstract class AbstractForm extends SXAbstractDialog{
	
	
	
	private Map<String, JComponent> components=new HashMap<String, JComponent>();
	protected final IFormModel model;
	private BindingFactory factory;
 
	public AbstractForm(final IFormModel model) {
		super("");
		this.model=model;
		model.getValidationModel().addPropertyChangeListener(ValidationResultModel.PROPERTYNAME_RESULT,new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				updateComponentTreeMandatoryAndSeverity();
			}			
		});
	}

	@Override
	protected JComponent buildContent() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		if(model.isReadOnly()){
			panel.add(buildButtonBarWithClose(),BorderLayout.SOUTH);			
		}else
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final FormLayout layout=new FormLayout("p","p,2dlu,40dlu");
		final PanelBuilder builder=new PanelBuilder(layout);
		final CellConstraints cc=new CellConstraints();
		builder.add(buildFormPanel(),cc.xy(1, 1));
		if(!model.isReadOnly())
			builder.add(buildValidationPanel(),cc.xy(1,3));
		return builder.getPanel();
	}
	
	protected JComponent getControl(final String property){
		JComponent c=components.get(property);
		if(c==null){
			c=createNewComponent(property);			
			components.put(property, c);
		}
		return c;
	}
	
	protected JComponent createNewComponent(final String property){
		JComponent c=createCustomComponent(property);
		if(c==null){
			FormControl fc=getFactory().getFormControl(property, model);		
			ValidationComponentUtils.setMessageKey(fc.getControl(), fc.getLabelKey());
			return fc.getControl();
		}else{
			FormControl fc=new FormControl(c,property);		
			ValidationComponentUtils.setMessageKey(fc.getControl(), fc.getLabelKey());
			return c;
		}
	}
	
	/**
	 * Template method para crear los controles de forma manual
	 * 
	 * @param property
	 * @return
	 */
	protected JComponent createCustomComponent(final String property){
		return null;
	}
	
	@Override
	protected void onWindowOpened() {
		updateComponentTreeMandatoryAndSeverity();
	}
	
	
	 protected void updateComponentTreeMandatoryAndSeverity() {
		 ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(getContentPane());
	     ValidationComponentUtils.updateComponentTreeSeverityBackground(getContentPane(),model.getValidationModel().getResult());
	     getOKAction().setEnabled(!model.getValidationModel().hasErrors());
	 }
	 
	 public BindingFactory getFactory(){
			if(factory==null)
				factory=new BasicBindingFactory();
			return factory;
	}
	 
	protected JComponent buildValidationPanel(){
		JComponent c=ValidationResultViewFactory.createReportList(model.getValidationModel());
		return c;
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract JComponent buildFormPanel();

	
	public static void showObject(Object bean){
		System.out.println(ToStringBuilder.reflectionToString(bean,ToStringStyle.MULTI_LINE_STYLE,false));
	}

}
