package com.luxsoft.siipap.cxc.swing.cobranza;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Currency;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.Validator;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.jgoodies.validation.util.ValidationResultModelContainer;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.cxc.model.PagoValidator;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * PresentationModel pattern para el comportamiento y estado de PagoForm, en esta clase
 * estan registradas las reglas de negicio para el comportamiento de la forma, pero en general
 * del proceso que implica aplicar un pago a una factura
 * 
 * A su vez esta clase envuelve a un bean de tipo Pago para controlar principalmente la asignacion 
 * de propiedades a este Para que este modelo opere correctamente el bean debe haber sido inicializado
 * y las propiedades de fecha y venta deben haber sido establecidas tambien.
 * 
 *  Proporciona un acceso a ComponentValueModel de solo lectura para las propiedades de Pago.venta
 * 
 * 	Si el tipo de pago es por nota de credito se habilitan el ComponentValueModel correspondiente 
 * a la propiedad Pago.notaDelPago
 * 
 * @author Ruben Cancino
 *
 */
public class PagoFormModel extends AbstractGenericFormModel<Pago, Long>{
	
		
	private OtrosProductosAdapter otrosProductos;
	private final ValidationResultModel validationResultModel;
	private ValidationResultModelContainer validationContainer;
	
	
	protected Logger logger=Logger.getLogger(getClass());
	
	private ComponentValueModel saldoNotaDeCredito=new ComponentValueModel(new ValueHolder());
	private ComponentValueModel saldoFactura=new ComponentValueModel(new ValueHolder(CantidadMonetaria.pesos(0)));
	private ComponentValueModel saldoCliente=new ComponentValueModel(new ValueHolder(CantidadMonetaria.pesos(0)));
	

	public PagoFormModel(Object bean) {
		super(bean);
		validationResultModel =new DefaultValidationResultModel();
		 validationContainer=new ValidationResultModelContainer(new SimpleValidationMessage("NA"));//
		 validationContainer.add(validationResultModel);
		validar();		
		initValueModels();
		initBusinessRules();	
		initEventHandling();
	}
	
	private void validar(){
		Assert.notNull(getPago().getVenta());
	}

	private void initValueModels(){
		saldoNotaDeCredito.setEnabled(false);	
		saldoFactura.setEditable(false);
		saldoCliente.setEditable(false);
		getComponentModel("notaDelPago").setEnabled(false);
		//getComponentModel("fecha").setEnabled(false);
	}
	
	private void initBusinessRules(){
		getComponentModel("formaDePago2").addPropertyChangeListener(new FormaDePagoConNotaHandler());
		getComponentModel("notaDelPago").addValueChangeListener(new NotaHandler());
		getComponentModel("importe").addValueChangeListener(new NuevoSaldoHandler());
		updateValidationResult();
	}
	
	/**
     * Listens to changes in all properties of the current Pago.
     * 
     */
    protected void initEventHandling() {
    	super.initEventHandling();
        PropertyChangeListener handler = new ValidationUpdateHandler();
        addBeanPropertyChangeListener(handler);
        getBeanChannel().addValueChangeListener(handler);
    }
	
	public ValueModel getSaldoNotaDeCredito() {
		return saldoNotaDeCredito;
	}
	public ValueModel getSaldoFactura(){
		return saldoFactura;
	}
	
	public PresentationModel getOtrosProductosModel(){
		if(otrosProductos==null)
			otrosProductos=new OtrosProductosAdapter();
		return otrosProductos.getModel();
	}
	
	public ComponentValueModel getOtrosPordsAFavorModel(){
		return getOtrosProductosModel().getComponentModel(OtrosProductosAdapter.OTROS_PRODSAFAVOR_PROP_NAME);
	}
	public ComponentValueModel getOtrosPordsDeMenosModel(){
		return getOtrosProductosModel().getComponentModel(OtrosProductosAdapter.OTROS_PRODSDEMENOS_PROP_NAME);
	}
	public ComponentValueModel getAplicarOtrosProdsDeMenosModel(){
		return getOtrosProductosAdapter().getAplicarDeMenosModel();
	}
	
	public ComponentValueModel getSaldoCliente() {
		return saldoCliente;
	}
	public void setSaldoCliente(ComponentValueModel saldoAFavor) {
		this.saldoCliente = saldoAFavor;
	}
	
	public void updateSaldoCliente(CantidadMonetaria nvoSaldo){
		getSaldoCliente().setValue(nvoSaldo);
	}

	public Venta getVenta(){
		return getPago().getVenta();
	}
	
	public Pago getPago(){
		return (Pago)getBean();
	}
	
	
	
	public CantidadMonetaria getSaldoDelClienteEnMoneda(){
		return (CantidadMonetaria)getSaldoCliente().getValue();
	}
	
	private OtrosProductosAdapter getOtrosProductosAdapter(){
		return (OtrosProductosAdapter)getOtrosProductosModel().getBean();
	}
	
	public ValidationResultModel getValidationResultModel() {
		//return validationResultModel;
		return validationContainer;
	}

    public void updateValidationResult() {        
        ValidationResult result = new PagoValidator(getPago()).validate();        
        validationResultModel.setResult(result);
        getOtrosProductosAdapter().updateValidation();
        /**
        if(logger.isDebugEnabled()){
        	//logger.debug(validationResultModel.getResult().getErrors());
        	logger.debug(getValidationResultModel().getResult().getErrors());
        }
        **/
    }
	
	public void comit(){
		if(logger.isDebugEnabled()){
			Pago otros=getOtrosProductosAdapter().resolverPago();
			Pago pago=getPago();
			pago.setImporte(pago.getImporte().subtract(otros.getImporte()));
			//logger.debug("Venta a salvar:" +getVenta());
			logger.debug("Pago: "+pago);
			logger.debug("Otros productos: "+otros);			
			if(otros!=null){
				//getManager().aplicarPagoPorVenta(getPago(),getOtrosProductosAdapter().resolverPago());
				/**
				 * final Pago p=getPago();
				p.setImporte(p.getImporte().subtract(otros.getImporte()));
				getManager().aplicarPagoPorVenta(p,otros);
				 */
				getManager().aplicarPagoPorVenta(pago,otros);
			}
			else
				getManager().aplicarPagoPorVenta(getPago());
		}
		super.release();
	}
	
	public CXCManager getManager() {
		return (CXCManager)ServiceLocator.getDaoContext().getBean("cxcManager");
	}

	
		

	private class 	FormaDePagoConNotaHandler implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {			
			if(evt.getNewValue()!=null){
				FormaDePago f=(FormaDePago)evt.getNewValue();
				if(f.equals(FormaDePago.T)){
					//notaDeCredito.setEnabled(true);
					saldoNotaDeCredito.setEnabled(false);
					getComponentModel("notaDelPago").setEnabled(true);
					getComponentModel("referencia").setEnabled(false);
					getComponentModel("descFormaDePago").setEnabled(false);
				}else{
					//notaDeCredito.setEnabled(false);
					saldoNotaDeCredito.setEnabled(false);
					getComponentModel("notaDelPago").setEnabled(false);
					getComponentModel("referencia").setEnabled(true);
					getComponentModel("descFormaDePago").setEnabled(true);
				}				
			}			
		}		
	}
	
	private class NotaHandler implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue()!=null){
				NotaDeCredito n=(NotaDeCredito)evt.getNewValue();				
				saldoNotaDeCredito.setValue(n.getSaldo());
			}			
		}		
	}
	
	/**
	 * Actualiza el saldo de la factura en funcion del pago
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class NuevoSaldoHandler implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {
			//Calculamos el nuevo saldo
			CantidadMonetaria saldo=getVenta().getSaldoEnMoneda();
			CantidadMonetaria importe=getPago().getImporte();
			CantidadMonetaria diff=saldo.subtract(importe);
			if(diff.amount().doubleValue()>0){
				getSaldoFactura().setValue(diff);
			}else{
				getSaldoFactura().setValue(CantidadMonetaria.pesos(0));
			}
			if(logger.isDebugEnabled()){
				logger.debug("Differencia entre importe y factura: "+diff);
			}			
		}		
	}
	
	/**
     * Validates the order using an OrderValidator and 
     * updates the validation result.
     */
    private final class ValidationUpdateHandler implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent evt) {        	
            updateValidationResult();
        }

    }

	
	/**
	 * Controla el manejo de los pagos de tipo Otros productos
	 * 
	 * Tinen la obligacion de escuchar los cambios en el Modelo padre 
	 * 
	 * 
	 * @author Ruben Cancino
	 *
	 */
	public class OtrosProductosAdapter extends Model implements PropertyChangeListener,Validator{
		
		public static final String OTROS_PRODSAFAVOR_PROP_NAME="otrosPordAFfavor";
		public static final String OTROS_PRODSDEMENOS_PROP_NAME="otrosProdDeMenos";
		public static final String APLICARDEMENOS_PROP_NAME="aplicarDeMenos";
		public  final CantidadMonetaria LIMITE_DEMENOS=CantidadMonetaria.pesos(100);
		
		private CantidadMonetaria otrosPordAFfavor;
		private CantidadMonetaria otrosProdDeMenos;
		private boolean aplicarDeMenos=false;
		
		private final PresentationModel model;
		private final ValidationResultModel validationModel;
		
		
		public OtrosProductosAdapter() {
			model=new PresentationModel(this);
			model.getComponentModel(OtrosProductosAdapter.OTROS_PRODSAFAVOR_PROP_NAME).setEnabled(false);				
			model.getComponentModel(OtrosProductosAdapter.OTROS_PRODSDEMENOS_PROP_NAME).setEnabled(false);
			model.getComponentModel(OtrosProductosAdapter.OTROS_PRODSDEMENOS_PROP_NAME).addValueChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					updateValidationResult();					
				}				
			});
			PagoFormModel.this.getComponentModel("importe").addPropertyChangeListener(this);
			validationModel=new DefaultValidationResultModel();
			PagoFormModel.this.validationContainer.add(validationModel);
			
		}

		public PresentationModel getModel(){
			return model;
		}
		
		public ValidationResultModel getValidationModel(){
			return validationModel;
		}
		
		public ComponentValueModel getAplicarDeMenosModel(){
			return model.getComponentModel(APLICARDEMENOS_PROP_NAME);
		}
		
		public boolean isAplicarDeMenos() {
			return aplicarDeMenos;
		}
		public void setAplicarDeMenos(boolean aplicarDeMenos) {
			boolean old=this.aplicarDeMenos;
			this.aplicarDeMenos = aplicarDeMenos;
			firePropertyChange("aplicarDeMenos", old, aplicarDeMenos);
		}

		public CantidadMonetaria getOtrosPordAFfavor() {
			return otrosPordAFfavor;
		}
		public void setOtrosPordAFfavor(CantidadMonetaria otrosPordAFfavor) {
			Object old=this.otrosPordAFfavor;
			this.otrosPordAFfavor = otrosPordAFfavor;
			firePropertyChange("otrosPordAFfavor", old, otrosPordAFfavor);
		}

		public CantidadMonetaria getOtrosProdDeMenos() {
			return otrosProdDeMenos;
		}
		public void setOtrosProdDeMenos(CantidadMonetaria otrosProdDeMenos) {
			Object old=this.otrosProdDeMenos;
			this.otrosProdDeMenos = otrosProdDeMenos;
			firePropertyChange("otrosProdDeMenos",old,otrosProdDeMenos);
		}

		/**
		 * Controla la generacion del monto para el pago de otros productos
		 * Ya sea a favor o en contra
		 */
		public void propertyChange(PropertyChangeEvent evt) {			
			if(ComponentValueModel.PROPERTYNAME_VALUE.equals(evt.getPropertyName())){
				
				//Si el saldo de la factura es menor al importe del pago actualizar el saldo a favro
				final CantidadMonetaria saldo=PagoFormModel.this.getVenta().getSaldoEnMoneda();
				final CantidadMonetaria importe=PagoFormModel.this.getPago().getImporte();
				final CantidadMonetaria diferencia=saldo.subtract(importe);
				final Currency moneda=diferencia.currency();
				
				CantidadMonetaria afavor=CantidadMonetaria.pesos(0);
				CantidadMonetaria deMenos=CantidadMonetaria.pesos(0);
					
				if(diferencia.getAmount().doubleValue()<0){
					//Tenemos saldo a favor
					afavor=new CantidadMonetaria(-diferencia.getAmount().doubleValue(),moneda);					
				}else{					
					if(diferencia.getAmount().abs().doubleValue()<=LIMITE_DEMENOS.amount().doubleValue()){
						deMenos=new CantidadMonetaria(diferencia.getAmount().doubleValue(),moneda);
						model.getComponentModel(OTROS_PRODSDEMENOS_PROP_NAME).setEnabled(true);
					}else{						
						model.getComponentModel(OTROS_PRODSDEMENOS_PROP_NAME).setEnabled(false);
					}
				}
				setOtrosPordAFfavor(afavor);
				setOtrosProdDeMenos(deMenos);
				if(logger.isDebugEnabled()){
					logger.debug("La difenecia en otros productos es: "+diferencia);
					logger.debug("De Menos:"+getOtrosProdDeMenos());
					logger.debug("A Favor:"+getOtrosPordAFfavor());
				}
			}			
		}
		
		
		public boolean esPagoDeMenosValido(){
			
			return validationModel.hasErrors();
		}
		
		
		
		/**
		 * Desifra el pago por otros si es necesario
		 * 
		 * @return
		 */
		public Pago resolverPago(){
			if(getOtrosPordAFfavor().getAmount().doubleValue()>0){
				//Existe saldo a favor y generamos el pago correspondiente a otros productos
				Pago p=new Pago();
				p.setFormaDePago2(FormaDePago.U);
				p.setFecha(getPago().getFecha());
				p.setVenta(getPago().getVenta());
				p.setClave(getPago().getClave());
				p.setImporte(getOtrosPordAFfavor());		
				
				
				p.setDescReferencia("A favor del Cliente");
				p.setComentario(getPago().getComentario());
				p.setDescFormaDePago(p.getFormaDePago2().getDesc());
				p.setReferencia(getPago().getFormaDePago2().getId()+" "+getPago().getReferencia()+" "+p.getFormaDePago2().getId());
				p.setFormaDePago(p.getFormaDePago2().getId());
				
				return p;
			}else{
				if(isAplicarDeMenos()){
					System.out.println("Aplicar de menos");
					if(!esPagoDeMenosValido()){
						System.out.println("Es valido");
						Pago p=new Pago();
						
						p.setFormaDePago2(FormaDePago.D);
						p.setDescReferencia("Ajuste Menor a $100");
						p.setComentario(getPago().getComentario());
						p.setDescFormaDePago(p.getFormaDePago2().getDesc());
						p.setReferencia(getPago().getFormaDePago2().getId()+" "+getPago().getReferencia()+" "+p.getFormaDePago2().getId());
						p.setFormaDePago(p.getFormaDePago2().getId());
						
						p.setFecha(getPago().getFecha());
						p.setClave(getPago().getClave());
						p.setVenta(getPago().getVenta());
						p.setImporte(getOtrosProdDeMenos());
						return p;
					}
				}
			}
			return null;
		}
		
		public CantidadMonetaria calcularMaximoDeMenos(){			
			final CantidadMonetaria saldo=PagoFormModel.this.getVenta().getSaldoEnMoneda();
			final CantidadMonetaria importe=PagoFormModel.this.getPago().getImporte();
			final CantidadMonetaria diferencia=saldo.subtract(importe);
			if(diferencia.amount().abs().doubleValue()>100.00)
				return CantidadMonetaria.pesos(0);
			return diferencia;
		}	
			

		/**
		 * No se pueden generar OtrosProductos con monto mayor al saldo de la factura
		 * No se pueden generar OtrosProductos con monto mayor a 100
		 */
		public ValidationResult validate() {
			PropertyValidationSupport support = 
	            new PropertyValidationSupport(this, "O. Productos");
			
			if(getOtrosProdDeMenos()!=null &&(getOtrosProdDeMenos().amount().doubleValue()>0)){				
				final double maximo=calcularMaximoDeMenos().amount().doubleValue();
				final double monto=getOtrosProdDeMenos().amount().doubleValue();
				if(monto>100.00 || monto>maximo){
					support.addError("Gastos ", "El maximo permitido es: "+calcularMaximoDeMenos());					
				}else if(monto<0)
					support.addError("Gastos ", "El Rango permitido es: $0.01 a "+calcularMaximoDeMenos());
				
				
			}			
			if(getOtrosPordAFfavor()!=null && (getOtrosPordAFfavor().amount().doubleValue()>0)){
				support.addWarning("Otros Productos ", "Se generara un Pago automatico por el saldo a favor");
			}
			return support.getResult();
		}
		
		public void updateValidation(){
			getValidationModel().setResult(validate());
		}
		
		
	}


	

}
