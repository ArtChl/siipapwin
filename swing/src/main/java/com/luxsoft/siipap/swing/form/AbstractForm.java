package com.luxsoft.siipap.swing.form;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.WeakHashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.hibernate.validator.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.AbstractControl;

/**
 * Implementacion base de Form que que ayuda en la creacion de formas
 * 
 * @author Ruben Cancino
 *
 */
public abstract class AbstractForm extends AbstractControl implements Form{
	
	private final FormModel model;
	//private boolean enabled=true;
	protected WeakHashMap<String, JComponent> componentMap=new WeakHashMap<String, JComponent>();
	

	public AbstractForm(final FormModel model) {		
		this.model = model;
	}

	public FormModel getFormModel() {		
		return model;
	}
	
	/**
	 * Template method para inicializar componentes 
	 *
	 */
	protected void initComponents(){
		
	}
	
	/**
	 * Para que la validacion funcione debemos registrar un PropertyChangeListener en el ValidationResultModel
	 * del FormModel
	 *
	 */
	protected void initEventHandling(){
		getFormModel().getValidationModel().addPropertyChangeListener(
				ValidationResultModel.PROPERTYNAME_RESULT,
				new ValidationChangeHandler());
	}
	
	/**
	 * Template method para inicializar anotaciones de componentes de forma manual
	 *
	 */
	protected void initComponentAnnotations() {
		
	}

	/**
	 * Construye el panel principal de la forma ejecutando template methods para crear componentes,inicializar anotaciones
	 * y eventos, Actualiza el panel para la validacion 
	 */
	@Override
	protected JComponent buildContent() {
		initComponents();
		initComponentAnnotations();
		initEventHandling();
		JComponent c =buildFormPanel();
		model.updateValidation();
		updateComponentTreeMandatoryAndSeverity(getFormModel().getValidationModel().getResult(), c);
		
		return c;
	}	
	
	/**
	 * Crea el panel principal de la forma
	 * @return
	 */
	protected abstract JComponent buildFormPanel();
	
	/**
	 * Genera un componente a partir de la propiedad especificada
	 * 
	 * @param property
	 * @return
	 */
	public final JComponent add(String property){
		try {
			ComponentValueModel vm=getFormModel().getPresentationModel().getComponentModel(property);
			JComponent c=getCustomComponent(property,vm);
			if(c==null){
				c=bind(property,vm);
			}
			componentMap.put(property, c);			
			annotateComponent(c,property);
			if(!isEnabled()) c.setEnabled(false);
			//c.setEnabled(isEnabled());
			return c;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			JComponent c=getDefaultComponent();
			c.setToolTipText(e.getMessage());
			if(c instanceof JTextField){
				ValidationComponentUtils.setErrorBackground((JTextField)c);
			}
			return c;
		}
	}
	
	/**
	 * Template method para generar custom components a usar en la forma
	 * 
	 * @param property
	 * @param vm
	 * @return
	 */
	protected JComponent getCustomComponent(final String property,final ComponentValueModel vm){
		return null;
	}
	
	protected JComponent getDefaultComponent(){
		return new JTextField();
	}
	
	protected void annotateComponent(final JComponent c,final String property){
		String key=ClassUtils.getShortName(getBean().getClass())+"."+property;
		boolean mandatory=isMandatory(property);
		if(mandatory)
			ValidationComponentUtils.setMandatory(c, mandatory);
		ValidationComponentUtils.setMessageKey(c, key);
		if(logger.isDebugEnabled()){
			if(mandatory){
				String msg1=MessageFormat.format("Componente para propiedad {0}, {1}",getPropertyPath(property)," es mandatorio");
				logger.debug(msg1);
			}			
			logger.debug("Componente registrado con Key:"+key);
		}
		
	}
	
	/**
	 * Utilizando Hibernate Validator trata de detectar si un campo es mandatorio desde las annotaciones del Bean
	 * 
	 * @param property
	 * @return
	 */
	protected boolean isMandatory(final String property){
		try {
			boolean val = findField(property,getBeanClass()).isAnnotationPresent(NotNull.class);
			if(val){
				String msg=MessageFormat.format("La propiedad {0} del bean {1} es mandatoria", property,getBeanClassName());
				System.out.println(msg);
			}			
			return val;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}
	
	/**
	 * Busca en forma recusiva un campo para la propiedad
	 * 
	 * @param property
	 * @param clazz
	 * @return
	 */
	protected Field findField(final String property,final Class clazz){
		try {
			Field f=clazz.getDeclaredField(property);
			return f;
		} catch (NoSuchFieldException e) {
			if(clazz.getSuperclass()!=null){
				//System.out.println("No econtro "+property+" buscando en: "+clazz.getSuperclass().getName());
				return findField(property, clazz.getSuperclass());
			}
			else
				return null;
		}
	}
	
	protected Class getBeanClass(){
		return getFormModel().getPresentationModel().getBean().getClass();
	}
	protected String getBeanClassName(){
		return ClassUtils.getShortName(getBeanClass());
	}
	
	protected String getPropertyPath(final String property){
		return getBeanClassName()+"."+property;
	}
	
	/**
	 * TODO Mover a otra clase vinculando con una interfaz
	 * 
	 * @param property
	 * @return
	 */
	protected JComponent bind(final String property,final ComponentValueModel vm){
		
		Class clazz=null;
		PropertyDescriptor pd=BeanUtils.getPropertyDescriptor(getBean().getClass(),property);
		clazz=pd.getPropertyType();
		
		if(logger.isDebugEnabled()){
			logger.debug("Localizando propiedad para bean:"+getBean()+"  Property: "+property+" type: "+clazz.getName());
		}
		
		
		if(String.class.equals(clazz)){
			JComponent c= BasicComponentFactory.createTextField(vm);
			c.setEnabled(vm.isEnabled());
			c.setVisible(vm.isVisible());
			return c;
		}else if(Boolean.class.equals(clazz) || ClassUtils.isAssignable(clazz,Boolean.class )){
			JCheckBox c=BasicComponentFactory.createCheckBox(vm, "");
			c.setEnabled(vm.isEnabled());
			c.setVisible(vm.isVisible());
			return c;
		}else if(Date.class.equals(clazz)){
			if(vm.isEnabled())
				return Binder.createDateComponent(vm);
			else{
				JFormattedTextField c=BasicComponentFactory.createFormattedTextField(vm, DateFormat.getDateInstance());
				c.setEditable(vm.isEditable());
				c.setVisible(vm.isVisible());
				c.setFocusable(vm.isEnabled());
				return c;
			}
		}else if(Double.class.equals(clazz) || double.class.equals(clazz)){
			JFormattedTextField c=BasicComponentFactory.createFormattedTextField(vm, NumberFormat.getNumberInstance());
			c.setEnabled(vm.isEnabled());
			c.setVisible(vm.isVisible());
			return c;
		}else if("year".equals(property)){
			JComboBox c=Binder.createYearBinding(vm);
			c.setEnabled(vm.isEnabled());
			c.setVisible(vm.isVisible());
			return c;
		}else if(CantidadMonetaria.class.equals(clazz)){
			return Binder.createCantidadMonetariaBinding(vm);
		}else if(Cliente.class.equals(clazz)){			
			return Binder.createClientesBinding(vm);
		}else if("mes".equals(property)){
			JComboBox c=Binder.createMesBinding(vm);
			c.setEnabled(vm.isEnabled());
			c.setVisible(vm.isVisible());
			return c;
		}else{
			JTextField tf=new JTextField();
			tf.setToolTipText("No binding posible para la propiedad: "+ClassUtils.getShortName(getBean().getClass())+"."+property+" Type: "+clazz);
			ValidationComponentUtils.setWarningBackground(tf);
			return tf;
		}			
	}
	
	public Object getBean(){
		return getFormModel().getPresentationModel().getBean();
	}

	public void setEnabled(boolean enabled) {
		for(JComponent c:componentMap.values()){
			c.setEnabled(enabled);
		}
		getFormModel().setEnabled(enabled);
	}

	public boolean isEnabled() {
		return getFormModel().isEnabled();
	}
	
	protected void decorateF2Label(JLabel l){
		l.setHorizontalTextPosition(SwingConstants.LEADING);
		l.setIconTextGap(8);
		l.setHorizontalAlignment(SwingConstants.LEFT);
		l.setIcon(getIconFromResource("images2/database_refresh.png"));
	}
	
	/**
	 * Utility metodo para inhabilitar textos
	 * @param fields
	 */
	protected void decorateDisable(JTextField... fields ){
		for(JTextField tf:fields){
			decorateDisable(tf);
		}
	}
	/**
	 * Utility metodo para inhabilitar textos
	 * @param fields
	 */
	protected void decorateDisable(JTextField tf){
		tf.setEditable(false);
		tf.setFocusable(false);
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
	

}
