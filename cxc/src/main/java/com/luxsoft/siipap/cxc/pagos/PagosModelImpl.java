package com.luxsoft.siipap.cxc.pagos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.DepositoRow;
import com.luxsoft.siipap.cxc.domain.DepositoUnitario;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Implementacion de PagosModel, mantiene el estado y comportamiento
 * del proceso de pagos de facturas
 * 
 * @author Ruben Cancino
 *
 */
public class PagosModelImpl extends Model implements PagosModel {
	
	private final PagoM pagoM;
	
	private PresentationModel pagoMPModel;
	private ValidationResultModel validationModel;
	private EventList<Pago> pagos;
	private ComponentValueModel saldoTotal;
	
	@SuppressWarnings("unused")
	private PagosValidator pagosValidator;
	
	public PagosModelImpl(final Cliente cliente,final String tipo){
		pagoM=new PagoM(cliente,tipo);		
		pagoM.setTipoDeDocumento(tipo);
		pagoM.setCondonar(true);
		init();
		actualizar();
	}
	
	private void init(){
		pagosValidator=new PagosValidator(this);
	}	

	public PresentationModel getPagoMPModel() {
		if(pagoMPModel==null){
			pagoMPModel=new PresentationModel(getPagoM());
			pagoMPModel.getComponentModel("disponible").setEnabled(false);
			pagoMPModel.getComponentModel("tipoDeDocumento").setEnabled(false);
			pagoMPModel.getComponentModel("condonar").setEnabled(getPagoM().getId()==null);
			pagoMPModel.addBeanPropertyChangeListener("condonar", new CondarHandler());
			pagoMPModel.getComponentModel("importe").addValueChangeListener(new ImporteHandler());
		}
		return pagoMPModel;
	}
	
	public ValidationResultModel getValidationModel() {
		if(validationModel==null){
			validationModel=new DefaultValidationResultModel();			
		}
		return validationModel;
	}
	
	/**
	 * Acceso al bean
	 */
	public PagoM getPagoM(){
		return pagoM;
	}

	
	
	/**
	 * Registra un importe para el pago y regresa verdadero si este cubre
	 * el saldo de las facturas para aplicar dicho pago, si el importe no es 
	 * suficiente regresa falso
	 * 
	 * @param importe
	 * @return
	 */
	public boolean importeRegistrado(){
		CantidadMonetaria importe=getPagoM().getImporte();
		distribuirImporte();
		return importe.amount().doubleValue()>=getPendiente().amount().doubleValue();
	}
	
	/**
	 * Distribuye el importe del pago entre las facturas seleccionadas
	 * Solo funciona para altas
	 *
	 */
	private void distribuirImporte(){
		if(getPagoM().getId()!=null)
			return;
		for(Pago p:getPagos()){
			if(p.getId()==null)
				p.setImporte(CantidadMonetaria.pesos(0));
		}
		//CantidadMonetaria disponible=get
		CantidadMonetaria disponible=getPagoM().getImporte();//getPagoM().calcularDisponible();
		for(int i=0;i<getPagos().size();i++){
			Pago p=getPagos().get(i);
			p.setImporte(CantidadMonetaria.pesos(0));
			final CantidadMonetaria pago=estimarPago(p.getVenta());
			//final CantidadMonetaria disponible=getPagoM().getImporte();//getPagoM().calcularDisponible();
			if(disponible.amount().doubleValue()>=pago.amount().doubleValue()){
				p.setImporte(pago);
			}else{
				p.setImporte(disponible);
			}
			disponible=disponible.subtract(p.getImporte());
			getPagos().set(i, p);
			//getPagoM().calcularDisponible();
		}
		getPagoM().setDisponible(disponible.amount());
		actualizarDescuentos();
	}
	
	/**
	 * Regresa como propuesta de pago el saldo estimado de la factura
	 * mismo que puede ser el estimado real (con penas por mora) o
	 * el pactado al momento de la venta
	 */
	public CantidadMonetaria estimarPago(final Venta v){		
		if(getPagoM().isCondonar()){			
			return v.getSaldoEstimadoSinCargo();
		}
		else
			return v.getSaldoEstimado();
	}
	
	private void actualizarDescuentos(){
		for(int index=0;index<getPagos().size();index++){
			Pago p=getPagos().get(index);
			if(p.getVenta().getDescuento1()!=0)
				continue;
			final CantidadMonetaria totalV=p.getVenta().getTotal();
			final CantidadMonetaria devo=CantidadMonetaria.pesos(p.getVenta().getDevolucionesCred());
			final CantidadMonetaria total=totalV.add(devo);
			final double descuento;
			if(getPagoM().isCondonar()){
				descuento=p.getVenta().getDescuentoPactado();				 
			}else{
				descuento=p.getVenta().getDescuento();
			}
			final CantidadMonetaria impdesc=total.multiply(descuento/100);
			p.setDescuento(impdesc);
			getPagos().set(index, p);
		}
	}
	
	
	/**
	 * Lista de pagos individuales a facturas
	 * 
	 * @return
	 */
	public EventList<Pago> getPagos(){
		if(pagos==null){
			pagos=new BasicEventList<Pago>();
			pagos.addListEventListener(new PagosHandler());
		}
		return pagos;
	}
	
	/**
	 * Genera una aplicacion de pago para cada factura
	 * tomando como base el saldo-descuento 
	 * 
	 * @param facturas
	 */
	public EventList<Pago> generarPagos(final List<Venta> facturas){
		for(Venta v:facturas){
			Pago p=getPagoM().aplicarPago(v, v.getSaldoEstimado());
			getPagos().add(p);
			
		}
		getPorPagar().setValue(getPendiente());
		return getPagos();
	}
	/**
	private void actualizarPorPagar(){		
		CantidadMonetaria porPagar=CantidadMonetaria.pesos(0);
		
		for(Pago p:getPagos()){
			porPagar=porPagar.add(p.getPorPagar());
		}
		
		getPorPagar().setValue(porPagar);
	}
	**/
	

	/**
	 * ValueModel para el saldoPorPagar
	 */
	public ValueModel getPorPagar() {
		if(saldoTotal==null){
			saldoTotal=new ComponentValueModel(new ValueHolder(CantidadMonetaria.pesos(0)));
			saldoTotal.setEnabled(false);
		}
		return saldoTotal;
	}
	
	/**
	 * Regresa el saldo total de las facturas de los pagos
	 * 
	 */
	public CantidadMonetaria getPendiente(){
		CantidadMonetaria saldo=CantidadMonetaria.pesos(0);
		for(Pago p:getPagos()){
			
			//saldo=saldo.add(p.getVenta().getSaldoEnMoneda().subtract(p.getDescuento()));
			saldo=saldo.add(p.getPorPagar());
			
		}		
		return saldo;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.pagos.PagosModel#getPendienteDespuesDePago()
	 */
	public CantidadMonetaria getPendienteDespuesDePago(){
		double importe=getPagoM().getImporte().amount().doubleValue();
		double porPagar=getPendiente().amount().doubleValue();
		if(importe>=porPagar){
			return CantidadMonetaria.pesos(0);
		}else
			return CantidadMonetaria.pesos(porPagar-importe);
		
	}
	
	/**
	 * Actualiza el estado del bean PagosM 
	 *
	 */
	private void actualizar(){
		getPorPagar().setValue(getPendiente());
		//getPagoM().calcularDisponible();
		pagosValidator.validate();
		distribuirImporte();
	}
	
	
	
	
	/**
	 * Detecta cambios en la propiedad de PagoM.condonar
	 * para recalcular el pago de las facturas en funcion del
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class CondarHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			actualizar();
		}		
	}
	
	private class ImporteHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			actualizar();			
		}
	}
	
	/**
	 * Detecta modificaciones en la lista de pagos generados
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class PagosHandler implements ListEventListener<Pago>{

		public void listChanged(ListEvent<Pago> listChanges) {
			while(listChanges.hasNext()){
				listChanges.next();
				
				actualizar();				
			}
			
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<DepositoRow> buscarDepositosDisponibles() {
		String sql="select b.FORMADP,b.CUENTADESTINO,a.* from SW_DEPOSITOSDET a " +
		"left join SW_DEPOSITOS b on a.DEPOSITO_ID=b.DEOPSITO_ID " +
		"where pagoaplicado is null and clave is not null" +
		" and clave=? and b.origen=\'CRE\'";
		
		String clave=getPagoM().getCliente().getClave();
		
		List<DepositoRow> rows=ServiceLocator.getJdbcTemplate()
			.query(sql,new Object[]{clave},new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DepositoRow row=new DepositoRow();
				row.setDepositoId(rs.getLong("DEPOSITO_ID"));
				row.setBanco(rs.getString("BANCO"));
				row.setClave(rs.getString("CLAVE"));
				row.setNombre(rs.getString("NOMBRE"));
				row.setClienteId(rs.getLong("CLIENTEID"));
				row.setImporte(rs.getBigDecimal("IMPORTE"));
				row.setNumero(rs.getInt("NUMERO"));
				row.setFormaDePago(rs.getString("FORMADP"));
				row.setCuentaDeposito(rs.getString("CUENTADESTINO"));
				return row;
			}
		});
		return rows;
	}
	
	
}
