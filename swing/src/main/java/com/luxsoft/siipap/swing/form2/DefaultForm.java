package com.luxsoft.siipap.swing.form2;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.util.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.domain.Linea;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;


/**
 * Dialogo que mantiene una forma sencilla de captura
 * 
 * @author Ruben Cancino
 *
 */
public class DefaultForm extends SXAbstractDialog {
		
	
	protected final IFormModel model;	
	private BindingFactory bindingFactory;
	protected Map<String, FormControl> controls;
	private String headerTitle;
	private String headerDescription;
	private String iconPath;
	private boolean readOnly=false;
	//private BeanPropertiesExtractor extractor;
	

	public DefaultForm(final IFormModel model,final String dialogTitle) {
		super(dialogTitle);
		this.model=model;
		init();
	}
	
	/**
	 * Inicializa la forma
	 * Es fundamental que este metodo sea ejecutado siempre
	 * al crear la forma
	 */
	protected void init(){
		final ValidationHandler handler=new ValidationHandler();		
		//model.addBeanPropertyChangeListener(handler);
		model.getValidationModel().addPropertyChangeListener(ValidationResultModel.PROPERTYNAME_ERRORS,handler);
		controls=new HashMap<String, FormControl>();
	}
	
	/**
	 * Template method para incializar custom componentes
	 */
	protected void initComponents(){
	}
		
	/**
	 * Template method para inicializar control de eventos de los componentes
	 * 
	 */
	protected void initEventHandling(){		
	}
	
	/**
	 * Crea el contenido, el ContentPanel, delegando los detalles a otros tmplate methods
	 * 
	 */
	@Override
	protected JComponent buildContent() {
		initComponents();		
		initEventHandling();
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
		final FormLayout layout=buildLayout();
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setRowGroupingEnabled(true);
		insertComponents(builder);
		if(isReadOnly())
			FormUtils.disableAllComponents(builder.getPanel());
		updateComponentTreeMandatoryAndSeverity(model.validate(), builder.getPanel());
		return builder.getPanel();
	}
	
	/**
	 * FormLayout par la forma, esta implementacion crea un layout
	 * para ser usuado en un DefaultformBuilder. Consta de 2 major columns
	 * en 5 columnas
	 * Este metodo puede ser re escrito a gusto
	 * @return
	 */
	protected FormLayout buildLayout(){
		final FormLayout layout=new FormLayout(
				"l:p,3dlu,f:max(p;50dlu) ,3dlu " +
				"l:p,3dlu,f:max(p;50dlu):g "
				,"");
		return layout;
	}
	
	/**
	 * Crea un panel para mostrar los resultados de validacion
	 * 
	 * @return
	 */	
	protected JComponent createValidationView(){
		return ValidationResultViewFactory.createReportList(model.getValidationModel());
	}
	
	@Override
	protected JComponent buildHeader() {		
		final HeaderPanel header=new HeaderPanel(
				getHeaderTitle()
				,getHeaderDescription()
				,getIconFromResource(getIconPath()));
		return header;
	}

	/**
	 * Actualiza en funcion del resultado de validacion el panel de captura
	 * 
	 * @param result
	 * @param panel
	 */
	protected void updateComponentTreeMandatoryAndSeverity(final ValidationResult result,final JComponent panel) {
		if(isReadOnly()) return;
		System.out.println("Actualizando componentes");
		 ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(panel);
	     ValidationComponentUtils.updateComponentTreeSeverityBackground(panel,result);
	}
	
	/**
	 * Template method para insertar componentes
	 * Las sub clases pueden implementar este metodo para insertar sus componentes
	 * 
	 * @param builder
	 */
	protected void insertComponents(final DefaultFormBuilder builder){
		//builder.append("Nombre",getControl("nombre").getControl(),5);
	}	
	
	protected FormControl getControl(final String propertyName){
		FormControl fc=controls.get(propertyName);
		if(fc==null){
			fc=getBindingFactory().getFormControl(propertyName,model);
			controls.put(propertyName, fc);
			/**
			if(model.isReadOnly()){
				fc.getControl().setEnabled(false);
			}else
				decorateControl(fc);
				**/
			//fc.getControl().setEnabled(false);
			decorateControl(fc);
			
		}
		return fc;
	}
	
	protected void decorateControl(final FormControl control){		
		ValidationComponentUtils.setMessageKey(control.getControl(), control.getLabelKey());
		
	}
	
	/** Form LifeCycle **/
	
	public void formOpened(){
		model.validate();
	}	
	
	@Override
	protected void onWindowOpened() {
		formOpened();
	}

	/**
	 * Actualiza la forma en funcion del modelo
	 */
	public void updateForm(){		
		updateComponentTreeMandatoryAndSeverity(model.getValidationModel().getResult(),(JComponent) getContentPane());
		getOKAction().setEnabled(!model.hasErrors());
		if(logger.isDebugEnabled()){
			String pattern="Forma {0} actualizada , errores: {1}";			
			logger.debug(MessageFormat.format(pattern, model.getId(),model.hasErrors()));
		}
	}

	public IFormModel getModel() {
		return model;
	}

	public BindingFactory getBindingFactory() {
		return bindingFactory;
	}
	public void setBindingFactory(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
	}
	/**
	public BeanPropertiesExtractor getExtractor() {
		return extractor;
	}
	public void setExtractor(BeanPropertiesExtractor extractor) {
		this.extractor = extractor;
	}
**/
	public String getHeaderTitle() {
		if(headerTitle==null){
			final String key=StringUtils.capitalize(model.getId())+".title";
			headerTitle=getString(key, key);
		}
		return headerTitle;
	}
	public void setHeaderTitle(String headerTitle) {
		
		this.headerTitle = headerTitle;
	}

	public String getHeaderDescription() {
		if(headerDescription==null){
			final String key=StringUtils.capitalize(model.getId())+".description";
			headerDescription=getString(key, key);
		}
		return headerDescription;
	}
	public void setHeaderDescription(String headerDescription) {
		this.headerDescription = headerDescription;
	}

	public String getIconPath() {
		if(iconPath==null){
			final String key=StringUtils.capitalize(model.getId())+".icon";
			iconPath=getString(key, key);
		}
		return iconPath;
	}
	public void setIconPath(String iconPath) {		
		this.iconPath = iconPath;
	}
	
	public boolean isReadOnly() {
		return model.isReadOnly();
	}
	
	public void setReadOnly(boolean readOnly) {
		
	}
	
	/**
	 * PropertyChangeListener para actualizar la forma al regsitrarse un cambio
	 * en model o en el validation model
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class ValidationHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			if(logger.isDebugEnabled()){
				String pattern="Cambios en el validation model detectados desde la forma {0} Propiedad: {1} Valor:{2}";
				logger.debug(MessageFormat.format(pattern,model.getId(), evt.getPropertyName(),evt.getNewValue()));
			}
			updateForm();
		}
	}	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		final BindingFactory factory=new BasicBindingFactory();
		final IFormModel model=new DefaultFormModel(Linea.class);
		final DefaultForm form=new DefaultForm(model,"Mantenimiendo de línea");
		form.setBindingFactory(factory);
		form.open();
	}

	

}
