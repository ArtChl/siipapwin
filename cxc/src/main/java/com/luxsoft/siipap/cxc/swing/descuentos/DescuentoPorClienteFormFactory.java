package com.luxsoft.siipap.cxc.swing.descuentos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;
import com.luxsoft.siipap.cxc.managers.DescuentosManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form.AbstractForm;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;
import com.luxsoft.siipap.swing.form.CommitListener;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.form.FormModel;
import com.luxsoft.siipap.swing.utils.MessageUtils;

/**
 * TODO Modificar el alta por cliente 
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentoPorClienteFormFactory {
	
	private Logger logger=Logger.getLogger(getClass());
	private DescuentosManager manager;
	
	public DescuentoPorClienteForm getForm(Object bean){
		DSCFormModel model=new DSCFormModel(bean);
		
		model.registerCommiHandler(commitHandler);
		
		DescuentoPorClienteForm form=new DescuentoPorClienteForm(model);
		form.setManager(getManager());
		return form;
	}
	
	public FormDialog getFormIndialog(final Object bean,final boolean enabled){
		AbstractForm form=getForm(bean);
		form.getControl();
		form.setEnabled(enabled);
		FormDialog dialog=new FormDialog(form);
		dialog.setTitle("Descuento por Cliente");
		dialog.setDescription("Generación o modificación de un descuento por cliente");
		return dialog;
	}
	
	private CommitListener commitHandler=new CommitListener(){
		public void commit(final FormModel model) {
			
			if(!model.isEnabled()) return;
			DSCFormModel fm=(DSCFormModel)model;
			
			DescuentoPorCliente d=fm.getFormBean();			
			try {
				getManager().salvar(d);
				if(logger.isDebugEnabled()){
					logger.debug("Descuento por cliente salvado: "+d.getId());
				}
			} catch (Exception e) {				
				logger.error(e);
				MessageUtils.showError("Error al salvar Descuento por cliente", e);
			}			
		}		
	};
	
	public DescuentosManager getManager() {
		return manager;
	}

	public void setManager(DescuentosManager manager) {
		this.manager = manager;
	}
	
	
	public static class DescuentoPorClienteForm extends AbstractForm{
		
		public DescuentoPorClienteForm(final FormModel model) {
			super(model);
			model.getPresentationModel().getModel("cliente").addValueChangeListener(new ClienteHandler());
		}


		@Override
		protected JComponent buildFormPanel() {
			FormLayout layout=new FormLayout(
					"l:max(50;p),3dlu,max(p;70dlu):g(.5),3dlu" +
					",l:max(50;p),3dlu,max(p;70dlu):g(.5)"
					,""); //NO ROWS
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			//builder.setDefaultDialogBorder();
			builder.append("Cliente ",add("cliente"),5);
			builder.nextLine();		
			builder.append("Descuento",add("descuento"));
			//builder.append("Adicional",add("adicional"),true);
			
			builder.append("Activo",add("activo"),true);
			
			builder.append("Tipo Fac",add("tipoFac"));
			builder.append("Precio Neto",add("precioNeto"),true);
			
			builder.appendSeparator("Autorización");
			builder.append("Ejecutivo",add("autorizo"),true);
			builder.append("Fecha ",add("fechaAutorizacion"));
			
			CellConstraints cc=new CellConstraints();
			builder.appendRow(builder.getLineGapSpec());
			builder.appendRow("top:30dlu");
			builder.nextLine(2);
			builder.append("Comentario");
			builder.add(new JScrollPane(add("comentario")),cc.xyw(builder.getColumn(), builder.getRow(),5,"fill,fill"));
			
			return builder.getPanel();
		}
		
		
		private DescuentosManager manager;
		
		@Override
		protected JComponent getCustomComponent(String property, ComponentValueModel vm) {
			if("cliente".equals(property)){
				return Binder.createClientesBinding(vm);
			}else if("descuento".equals(property) || "adicional".equals(property)){
				JComponent c =Binder.createDescuentoBinding(vm);
				//c.setEnabled(false);
				return c;
			}else if("comentario".equals(property)){
				return BasicComponentFactory.createTextArea(vm);
			}
			return super.getCustomComponent(property, vm);
		}
		
		private class ClienteHandler implements PropertyChangeListener{

			public void propertyChange(PropertyChangeEvent evt) {
				Cliente c=(Cliente)evt.getNewValue();
				DescuentoPorCliente d=getManager().buscarDecuentoPorCliente(c);
				if(d!=null){
					MessageUtils.showMessage("Ya existe un descuento para este cliente del: "+d.getDescuento()
							, "Descuento existente");
					getFormModel().getPresentationModel().setValue("cliente", null);
				}
				
				
			}
			
		}

		public DescuentosManager getManager() {
			return manager;
		}


		public void setManager(DescuentosManager manager) {
			this.manager = manager;
		}
		
		
	}


	public static class DSCFormModel extends AbstractGenericFormModel<DescuentoPorCliente, Long> {

		public DSCFormModel() {
			super();			
		}

		public DSCFormModel(Object bean) {
			super(bean);
		}

		@Override
		protected void initModels() {			
			super.initModels();
			getComponentModel("autorizo").setEnabled(false);
			getComponentModel("fechaAutorizacion").setEnabled(false);
			getComponentModel("tipoFac").setEnabled(false);
			getComponentModel("adicional").setEnabled(false);
			getComponentModel("precioNeto").setEnabled(false);
			if(!isNewBean())
				getComponentModel("cliente").setEnabled(false);
			
		}
		
		
	}
	
	/**
	private static void pruebaAlta(){
		DescuentosManager manager=(DescuentosManager)ServiceLocator.getDaoContext().getBean("descuentosManager");
		DescuentoPorClienteFormFactory factory=new DescuentoPorClienteFormFactory();
		factory.setManager(manager);
		FormDialog dialog=factory.getFormIndialog(new DescuentoPorCliente(),true);
		dialog.open();
	}
	
	private static void pruebaModificacion(){
		DescuentosManager manager=(DescuentosManager)ServiceLocator.getDaoContext().getBean("descuentosManager");
		Cliente c=manager.getClienteDao().buscarPorClave("U050008");
		DescuentoPorCliente dc=manager.getDescuentoPorClienteDao().buscar(c);
		Assert.notNull(dc,"No existe un descuento fijo para el cliente: "+c.getId());
		System.out.println("Descuento localizado: "+dc);
			
		DescuentoPorClienteFormFactory factory=new DescuentoPorClienteFormFactory();
		factory.setManager(manager);
		FormDialog dialog=factory.getFormIndialog(dc,true);
		dialog.open();
	}
	

	public static void main(String[] args) {
		//pruebaAlta();
		pruebaModificacion();
	}

	**/

}
