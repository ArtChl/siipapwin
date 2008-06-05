package com.luxsoft.siipap.cxc.chequed;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.component.UIFButton;
import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.selectores.Selectores;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.form2.IFormModel;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;

public class ChequeDevueltoForm extends AbstractForm{
	
	private JTextField pagoField;

	public ChequeDevueltoForm(IFormModel model) {
		super(model);
		
	}

	@Override
	protected JComponent buildFormPanel() {
		final FormLayout layout= new FormLayout(
				"p,2dlu,60dlu,3dlu,l:p,2dlu,max(p;70dlu):g"
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Fecha",getControl("fecha"));
		builder.append("Sucursal",getControl("sucursal"),true);
		builder.append("Cliente",getControl("cliente"),5);		
		builder.nextLine();
		builder.append("Pago",getControl("origen"),5);		
		builder.nextLine();
		builder.append("Banco",getControl("banco"),true);
		builder.append("Número",getControl("numero"),true);
		builder.append("Importe",getControl("importe"),true);
		
		builder.append("Comentario",getControl("comentario"),5);
		return builder.getPanel();
	}	
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if("cliente".equals(property)){
			//return Binder.createClientesCreditoBindingBox(model.getModel(property));
			//return new JComboBox();
			return Binder.createClientesBinding(model.getModel(property));
		}else if("origen".equals(property)){
			
			final UIFButton btn=new UIFButton("...");
			btn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					seleccionarPago();
				}				
			});
			btn.setText("...");
			
			pagoField=new JTextField(50);
			pagoField.setEditable(false);
			pagoField.addKeyListener(new KeyAdapter(){				
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_F2){
						seleccionarPago();
					}
				}				
			});
			final JPanel panel=ComponentUtils.createLookupPanel(pagoField, btn);
			return panel;
		}else if("importe".equals(property)){
			return Binder.createBigDecimalMonetaryBinding(model.getModel(property));
		}else if("banco".equals(property)){
			return CXCBindings.createBancosBinding(model.getModel(property));
		}else if("sucursal".equals(property)){
			return Binder.createSucursalesBinding(model.getModel(property));
		}
		return super.createCustomComponent(property);
	}
	
	private void actualizarOrigen(){
		PagoM pago=(PagoM)model.getValue("origen");
		if(pago!=null){
			final String pattern="Cheque: {0} Banco: {1} Importe: {2} Fecha:{3} Id:{4}";
			pagoField.setText(MessageFormat.format(pattern,pago.getReferencia(),pago.getBanco(),pago.getImporte(),pago.getFecha(),pago.getId()));
		}else{
			pagoField.setText("");
		}
	}
	
	private void seleccionarPago(){
		final Cliente c=(Cliente)model.getValue("cliente");
		if(c!=null){
			final SwingWorker<List<PagoM>, String> worker=new SwingWorker<List<PagoM>, String>(){
				
				protected List<PagoM> doInBackground() throws Exception {
					return ServiceLocator.getPagosManager().buscarPagosAplicadosConCheque(c.getClave());
				}	
				
				protected void done() {
					try {
						final List<PagoM> pagos=get();
						if(!pagos.isEmpty()){
							PagoM pago=Selectores.seleccionarPagoM(c, pagos);
							model.setValue("origen", pago);
							pagoField.setText(pago.toString());
							model.setValue("banco", pago.getBanco());
							model.setValue("numero", pago.getReferencia());
							model.setValue("importe", pago.getImporte().amount());
						}else{
							MessageUtils.showMessage("No existen pagos con cheques para este clientel", "Cheques");
						}
						actualizarOrigen();
					} catch (Exception e) {
						logger.error(e);
					}
				}				
				
			};
			TaskUtils.executeSwingWorker(worker);
			
		}else{
			MessageUtils.showMessage("Debe seleccionar un cliente", "Cheques");
		}
		
	}
	
	
	
	public static void main(String[] args) {
		DefaultFormModel model=new DefaultFormModel(new ChequeDevuelto());
		ChequeDevueltoForm form=new ChequeDevueltoForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			final ChequeDevuelto ch=(ChequeDevuelto)model.getBaseBean();
			ServiceLocator.getChequesDevManager().salvar(ch);
		}
		System.exit(0);
	}

}
