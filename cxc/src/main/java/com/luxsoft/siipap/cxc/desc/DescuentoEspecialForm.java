package com.luxsoft.siipap.cxc.desc;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.component.UIFButton;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.selectores.Selectores;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.IFormModel;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Venta;


public class DescuentoEspecialForm extends AbstractForm{
	
	private final EventList<Venta> ventas;
	private JComboBox clienteBox;	
	private JButton selectorBtn;
	private JTextField ventaField;

	public DescuentoEspecialForm(IFormModel model) {
		super(model);
		ventas=new BasicEventList<Venta>();
		model.getModel("venta").addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				Object v=evt.getNewValue();
				if(v!=null)
					ventaField.setText(v.toString());
			}			
		});
	}

	@Override
	protected JComponent buildFormPanel() {
		final FormLayout layout=new FormLayout(
				 "max(p;40dlu),2dlu,max(p;50dlu), 2dlu "
				+"max(p;40dlu),2dlu,max(p;70dlu), 2dlu " 
				+"max(p;40dlu),2dlu,max(p;50dlu):g, 2dlu "
				//+"max(p;40dlu),2dlu,max(p;50dlu) "
				,""
				);
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Cliente",getControl("cliente"),9);
		builder.nextLine();
		ventaField=new JTextField(70);
		ventaField.setEditable(false);
		selectorBtn=new UIFButton(buscarVentasAction);
		selectorBtn.setText("...");
		selectorBtn.setFocusable(false);
		
		JPanel vp=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
		vp.add(ventaField);
		vp.add(selectorBtn);
		vp.setBorder(null);
		
		builder.append("Venta",vp,9);
		builder.nextLine();
		builder.append("descuento",getControl("descuento"),true);
		builder.append("Autorizo",getControl("autorizo"));
		builder.append("Fecha",getControl("fechaAutorizacion"),true);
		builder.append("Comentario",getControl("comentario"),9);
		return builder.getPanel();
	}
	
	
	@Override
	protected JComponent createCustomComponent(String property) {
		
		if("cliente".equals(property)){
			clienteBox=Binder.createClientesCreditoBindingBox(model.getModel(property));
			return clienteBox;			
		}if("autorizo".equals(property)){
			return CXCBindings.createEjecutivosBinding(model.getModel("autorizo"));			
		}		
		return super.createCustomComponent(property);
	}

	private void seleccionarVenta(){
		
		
		final ClienteCredito c=(ClienteCredito)clienteBox.getSelectedItem();
		
		if(c==null){
			ventas.clear();
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Cargando ventas para cliente: "+c);
		}
		
		final SwingWorker<List<Venta>, String> worker=new SwingWorker<List<Venta>, String>(){
			@Override
			protected List<Venta> doInBackground() throws Exception {
				return ServiceLocator.getVentasManager().getVentasDao().buscarVentasCreditoConSaldo(c.getClave());
			}
			@Override
			protected void done() {
				try {
					ventas.addAll(get());
					Venta v=Selectores.seleccionarVentaCredito(c.getCliente(), ventas);
					model.setValue("venta", v);
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
					ventas.clear();
				}
			}
			
		};
		TaskUtils.executeSwingWorker(worker);
		
		
	}
	
	private Action buscarVentasAction=new AbstractAction("..."){
		public void actionPerformed(ActionEvent e) {
			seleccionarVenta();
		}		
	};
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		
	}

}
