package com.luxsoft.siipap.cxc.chequed;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JComponent;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.selectores.Selectores;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.MessageUtils;


public class ChequeDevueltoPagoForm extends AbstractForm{

	public ChequeDevueltoPagoForm(ChequeDevueltoPagoFormModel model) {
		super(model);
		model.getModel("formaDePago").addValueChangeListener( new FormaDePagoHandler());
	}

	@Override
	protected JComponent buildFormPanel() {
		final FormLayout layout=new FormLayout(
				"50dlu,2dlu,60dlu,3dlu,50dlu,2dlu,p:g"
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.appendSeparator("Cheque devuelto");
		getControl("numero").setEnabled(false);
		builder.append("Cheque",getControl("numero"));
		getControl("cliente").setEnabled(false);
		builder.append("Cliente",getControl("cliente"));
		getControl("fechaCheque").setEnabled(false);
		builder.append("Fecha",getControl("fechaCheque"));
		getControl("importeCheque").setEnabled(false);
		builder.append("Saldo",getControl("importeCheque"));
		builder.appendSeparator("Pago");
		builder.append("Fecha",getControl("fecha"));
		builder.append("Forma",getControl("formaDePago"));
		builder.append("Referencia",getControl("referencia"));
		builder.append("Banco",getControl("banco"));
		builder.append("Importe",getControl("importe"));
		getControl("cuentaDestino").setEnabled(false);
		builder.append("Cta Destino",getControl("cuentaDestino"));
		builder.append("Comentario",getControl("comentario"),5);
		getControl("notaRef").setEnabled(false);
		builder.append("Origen",getControl("notaRef"),5);
		return builder.getPanel();
	}
	
	
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if("formaDePago".equals(property)){
			JComponent c=CXCBindings.createFormaDePagoCompletaBinding(model.getModel(property));
			return c;
		}if("banco".equals(property)){
			return CXCBindings.createBancosBinding(model.getComponentModel(property));
		}
		return super.createCustomComponent(property);
	}
	
	
	public static PagoM pagar(final ChequeDevuelto ch){
		final ChequeDevueltoPagoFormModel model=new ChequeDevueltoPagoFormModel(ch);
		final ChequeDevueltoPagoForm form =new ChequeDevueltoPagoForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			System.out.println("Se solicita un pago de cheque con los parametros: "+model.getBaseBean());
			final PagoM p=model.procesar();
			return (PagoM)ServiceLocator.getUniversalDao().save(p);
		}else{
			return null;
		}
		
		
	}
	
	public void seleccionarNota(){
		logger.info("Seleccionando nota de credito");
		Cliente c=(Cliente)model.getValue("cheque.cliente");
		if(c==null ) return;
		final EventList<NotaDeCredito> notas=GlazedLists.eventList(ServiceLocator.getNotasManager().buscarNotasDeCreditoDisponibles(c));
		
		if(notas.isEmpty()){
			MessageUtils.showMessage(MessageFormat.format("El cliente {0} ({1})\n No tiene notas disponibles para usar como forma de pago"
					, c.getNombre(),c.getClave()), "Notas disponibles");
			return ;
		}		
		final NotaDeCredito origen=Selectores.seleccionarNotaDeCredito(c, notas);
		if(origen!=null){
			model.setValue("nota", origen);
		}
		if(origen!=null)
			logger.info("Nota seleccionada: "+origen.getId());
	}
	
	public void seleccionarOrigen(){
		logger.info("Seleccionando Disponible para pago");
		Cliente c=(Cliente)model.getValue("cheque.cliente");
		if(c==null ) return;
		final List<PagoM> pagos=ServiceLocator.getPagosManager().buscarSaldosAFavor(c);
		if(pagos.isEmpty()){
			MessageUtils.showMessage(MessageFormat.format("El cliente {0} ({1})\n No tiene disponibles para usar como forma de pago"
					, c.getNombre(),c.getClave()), "Disponibles");
			return ;
		}
		final PagoM origen=Selectores.seleccionarPagoM(c, pagos);
		if(origen!=null){
			model.setValue("otros", origen);
		}
		if(origen!=null)
			logger.info("Pago seleccionado seleccionada: "+origen.getId());
	}
	
	private class FormaDePagoHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue()!=null){
				if(evt.getNewValue().equals(FormaDePago.T)){					
					seleccionarNota();
				}else if(evt.getNewValue().equals(FormaDePago.S)){
					seleccionarOrigen();
				}
			}			
		}		
	}

	public static void main(String[] args) {
		final ChequeDevuelto ch=new ChequeDevuelto();
		ch.setImporte(BigDecimal.valueOf(650));
		ch.setCliente(new Cliente("TEST","CLIENTE TEST U8NION DE CREDITO DE LA INDUSTRIA "));
		
		final ChequeDevueltoPagoFormModel model=new ChequeDevueltoPagoFormModel(ch);
		final ChequeDevueltoPagoForm form =new ChequeDevueltoPagoForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			System.out.println("Se solicita un pago de cheque con los parametros: "+model.getBaseBean());
		}
		
	}

}
