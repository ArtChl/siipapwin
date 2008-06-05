package com.luxsoft.siipap.swing.form;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.util.StringUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

/**
 * Dialogo sencillo que extiende SXAbstractDialog para presentar un control de tipo Form
 * Escucha los cambios en el ValidationResultModel de la forma para encender o pagar el boton de
 * OK
 *   
 * 
 * @author Ruben Cancino
 *
 */
public  class FormDialog extends SXAbstractDialog{
	
	private Form form;
	private String title="";
	private String description="";
	private Icon icon;
	private String iconPath;
	
	public FormDialog(final Form form) {
		this("Sin Titulo",form);
	}

	public FormDialog(String title, Form form) {
		super(title);
		this.form = form;
		form.getFormModel().getValidationModel().addPropertyChangeListener(validationHandler);
		if(form.getFormModel() instanceof GenericFormModel){
			AbstractGenericFormModel gf=(AbstractGenericFormModel)form.getFormModel();
			//gf.addPropertyChangeListener("dirty", new DirtyHandler());
		}
		getOKAction().setEnabled(!form.getFormModel().getValidationModel().hasErrors());
	}


	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildFormPanel(),BorderLayout.CENTER);
		if(form.getFormModel().isEnabled())
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		else
			panel.add(buildButtonBarWithClose());
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
	
	protected JComponent buildEditorPanel(){
		return form.getControl();
	}
	
	protected JComponent buildValidationViewPanel(){
		return ValidationResultViewFactory.createReportList(form.getFormModel().getValidationModel());
	}
	
	
	
	@Override
	protected JComponent buildHeader() {
		if(getTitle()!=null){
			return new HeaderPanel(getTitle(),getDescription(),getIcon());
		}
		return null;
	}

	@Override
	public void doApply() {
		if(!form.getFormModel().isEnabled()){
			super.doApply();
			return;
		}
		form.getFormModel().updateValidation();
		if(!form.getFormModel().getValidationModel().hasErrors()){
			form.getFormModel().commit();
			super.doApply();
		}else{
			return;
		}
		
	}

	public Form getForm() {
		return form;
	}
	
	private PropertyChangeListener validationHandler=new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			getOKAction().setEnabled(!form.getFormModel().getValidationModel().hasErrors());
			
		}
		
	};
	
	private class DirtyHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			Boolean val=(Boolean)evt.getNewValue();
			getOKAction().setEnabled(val);
		}
		
	}
	
	

	@Override
	protected void setDefaultButton(JButton button) {
		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Icon getIcon() {
		if(icon==null && StringUtils.hasText(getIconPath())){
			icon=getIconFromResource(getIconPath());
		}
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	

}
