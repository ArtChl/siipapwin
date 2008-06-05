package com.luxsoft.siipap.swing.form;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;

import org.apache.log4j.Logger;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.PropertyValidationSupport;

public abstract class AbstractGenericFormModel<T,PK extends Serializable> extends PresentationModel implements GenericFormModel<T, Serializable>{
	
	protected Logger logger=Logger.getLogger(getClass());
	
	private Class beanClass;
	protected DefaultValidationResultModel validationModel;
	protected WeakReference<CommitListener> commitListener;
	private boolean enabled=true;
	private boolean dirty=false;

	public AbstractGenericFormModel() {
		super(null);		
		setBean(BeanUtils.instantiateClass(getBeanClass()));
		init();
	}

	public AbstractGenericFormModel(Object bean) {
		super(bean);		
		init();
	}
	
	private void init(){
		initModels();
		initEventHandling();
	}
	
	protected void initModels(){
		validationModel=new DefaultValidationResultModel();
	}
	
	protected void initEventHandling(){
		addBeanPropertyChangeListener(validationHandler);		
	}
	
	
	public void registerCommiHandler(CommitListener listener){
		commitListener=new WeakReference<CommitListener>(listener);
	}
	

	public PresentationModel getPresentationModel() {		
		return this;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getBeanClass(){
		if(beanClass==null){
			beanClass=(Class<T>)((ParameterizedType)getClass()
					.getGenericSuperclass())
					.getActualTypeArguments()[0];
		}
		return beanClass;
	}


	@SuppressWarnings("unchecked")
	public T getFormBean() {
		return (T)getBean();
	}

	public Serializable getId() {
		return (Serializable)getValue("id");
	}

	public boolean isNewBean() {
		return getId()==null;
	}

	public ValidationResultModel getValidationModel() {
		return validationModel;
	}

	public ValidationResult validate() {
		
		final String role=ClassUtils.getShortName(getBeanClass());
		final PropertyValidationSupport support =new PropertyValidationSupport(getBean(),role);
		
		final ClassValidator<T> validator=new ClassValidator<T>(getBeanClass());
		final InvalidValue[] invalid=validator.getInvalidValues(getFormBean());
		for(InvalidValue iv:invalid){
			String propName=iv.getPropertyName();						
			support.addError(propName, iv.getMessage());
		}
		return support.getResult();
	}
	
	
	public void updateValidation() {
		getValidationModel().setResult(validate());
	}
	
	private PropertyChangeListener validationHandler =new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			updateValidation();			
		}		
	};

	public void commit() {
		if(commitListener!=null){
			commitListener.get().commit(this);
		}
	}

	public boolean isEnabled() {		
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		boolean old=this.enabled;
		this.enabled = enabled;
		firePropertyChange("enabled",old,enabled);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		Object old=this.dirty;
		this.dirty = dirty;
		firePropertyChange("dirty", old, dirty);
	}

	

}
