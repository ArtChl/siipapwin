package com.luxsoft.siipap.swing.form2;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.luxsoft.siipap.domain.Linea;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

/**
 * Form que permite declarar las propiedades a editar
 * en un List 
 * 
 * TODO Implementar el patron strategy para tener varias formas de instalar
 * 		componentes
 * 
 * @author Ruben Cancino
 *
 */
public class SimpleForm extends DefaultForm{
	
	private Map<String, String> properties=new HashMap<String, String>();
	

	public SimpleForm(IFormModel model, String dialogTitle) {
		super(model, dialogTitle);		
	}	
	
	@Override
	protected void insertComponents(final DefaultFormBuilder builder) {
		
		for(Entry<String, String > entry:getProperties().entrySet()){
			String prop=entry.getKey();
			final FormControl fc=getControl(prop);
			builder.append(fc.getLabel(),fc.getControl(),5);
			builder.nextLine();			
		}
		
	}

	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}


	public static void main(String[] args) {
		SWExtUIManager.setup();
		final BasicBindingFactory factory=new BasicBindingFactory();
		factory.setExtractor(new BeanPropertiesExtractorImpl());
		final IFormModel model=new DefaultFormModel(Linea.class);
		final SimpleForm form=new SimpleForm(model,"Mantenimiendo de línea");
		form.setBindingFactory(factory);		
		//form.setReadOnly(true);
		
		form.getProperties().put("nombre","oc:true");
		form.getProperties().put("id","");
		form.open();		
		if(!form.hasBeenCanceled()){
			System.out.println("Bean editado: "+model.getBaseBean());
		}
	}
	

}
