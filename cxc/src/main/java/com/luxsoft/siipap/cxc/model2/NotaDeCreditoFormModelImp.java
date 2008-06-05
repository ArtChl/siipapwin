package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.ConceptoDeBonificacion;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.ventas.domain.Venta;

public class NotaDeCreditoFormModelImp extends DefaultFormModel implements NotaDeCreditoFormModel{
	
	private EventList<NotasDeCreditoDet> partidas;
	private List<Venta> ventas;
	private boolean porDescuento=true;
	private PropertyAdapter porDescAdapter;
	
	private static List<String> BONIFICACIONES=new ArrayList<String>();
	 
	static{
		BONIFICACIONES.add("C");
		BONIFICACIONES.add("F");
		BONIFICACIONES.add("L");
		BONIFICACIONES.add("Y");
	}


	public NotaDeCreditoFormModelImp(final NotaDeCredito nota,final List<Venta> ventas) {
		super(nota, false);
		getModel("descuento").addValueChangeListener(descuentoHandler);
		getModel("importe").addValueChangeListener(importeHandler);
		getComponentModel("impuesto").setEnabled(false);
		getComponentModel("total").setEnabled(false);
		this.ventas=ventas;		
		if(nota.getTipo().equals("V"))
			getComponentModel("aplicable").setEnabled(false);
	}
	
	
	

	@Override
	public ValueModel getModel(String propertyName) {
		if(propertyName.equals("porDescuento")){
			if(porDescAdapter==null){
				porDescAdapter=new PropertyAdapter(this,"porDescuento");
				
			}
			return porDescAdapter;
		}else
			return super.getModel(propertyName);
	}




	public EventList<NotasDeCreditoDet> getPartidas() {
		if(partidas==null){			
			partidas=new ObservableElementList<NotasDeCreditoDet> (new BasicEventList<NotasDeCreditoDet>(),GlazedLists.beanConnector(NotasDeCreditoDet.class));
			
			for(Venta v:ventas){
				NotasDeCreditoDet det=NotasUtils.getNotaDet(v);
				partidas.add(det);
			}
			partidas.addListEventListener(new ListHandler());
		}
		return partidas;
	}
	
	public NotaDeCredito getNota(){
		return (NotaDeCredito)getBaseBean();
	}
	
	/**
	 * En virtud de que las notas solo tiene capacidad de 11 partidas
	 * este metodo regresa una lista con las notas correctamente generadas
	 * a partir de las ventas seleccionadas
	 * 
	 * @return
	 */
	public List<NotaDeCredito> procesar(){
		for(NotasDeCreditoDet det:getPartidas()){
			//det.setImporte(MonedasUtils.calcularTotal(det.getImporte()));
		}
		List<NotaDeCredito> notas=NotasUtils.getNotasFromDetalles(getPartidas());
		/**
		if(notas==null && getNota().getBonificacion().equals(ConceptoDeBonificacion.RECLAMACION)){
			//La nota se tiene que hacer un un detalle
			notas=new ArrayList<NotaDeCredito>();
			notas.add(NotasUtils.preparaNotaDeReclamacion(getNota()));
		}
		**/
		
		for(NotaDeCredito n:notas){
			n.setTipo(getNota().getTipo());
			n.setSerie(getNota().getSerie());
			n.setDescuento(getNota().getDescuento());
			n.setComentario(getNota().getComentario());
			n.setFecha(getNota().getFecha());
			n.setOrigen(getNota().getOrigen());			
			n.setAplicable(getNota().isAplicable());
			n.setBonificacion(getNota().getBonificacion());
			n.actualizar();
			
		}
		return notas;
	}
	
	/**
	 * Genera el importe de la nota en funcion de un descuento general
	 * 
	 * @param desc
	 */
	public void aplicarDescuento(){
		if(getNota().getTipo().equals("V")){			
			for(NotasDeCreditoDet det:getPartidas()){
				final Venta factura=det.getFactura();
				double desc=0;
				if(getNota().getBonificacion().equals(ConceptoDeBonificacion.FINANCIERO))
					 desc=NotasUtils.calcularDescuentoFinanciero(factura, getNota().getFecha(), getNota().getDescuento());
				else if(getNota().getBonificacion().equals(ConceptoDeBonificacion.ADICIONAL)){					
					desc=getNota().getDescuento();
				}
				det.setDescuento(desc);				
			}
			
		}else{
			for(NotasDeCreditoDet det:getPartidas()){
				det.setDescuento(getNota().getDescuento());
			}
		}
		
	}
	
	/**
	 * Genera el importe de las partidas a partir de un importe fijo definido
	 * 
	 * en el maestro {@link NotaDeCredito}
	 * 
	 * 
	 */
	public void aplicarImporte(){		
		final CantidadMonetaria pago=getNota().getImporte();
		if(pago.amount().doubleValue()==0)
			return;
		//Obtnere el total de los saldos
		double totalAPagar=0;
		for(NotasDeCreditoDet partida:getPartidas()){
			totalAPagar=totalAPagar+partida.getFactura().getTotalSinDevoluciones().amount().doubleValue();
		}
		
		//Prorrateamos entre las partidas
		for(int index=0;index<getPartidas().size();index++){
			NotasDeCreditoDet det=(NotasDeCreditoDet)getPartidas().get(index);
			double saldo=det.getFactura().getTotalSinDevoluciones().amount().doubleValue();
			//Obtenemos la participacion de la partida
			double participacion=saldo/totalAPagar;
			final CantidadMonetaria importe=pago.multiply(participacion);
			det.setImporte(MonedasUtils.calcularTotal(importe).abs().multiply(-1));
			det.setDescuento(participacion*100);
			getPartidas().set(index, det);
		}
	}
	
	/**
	 * Actualiza los totales de la nota
	 */
	public void actualizar(){
		if(getNota().getTipo().equals("V") || isPorDescuento())
			aplicarDescuento();
		else
			aplicarImporte();
	}
	
	/**
	 * Actualiza el importe de la nota asumiendo que el descuento fue asignado 
	 * en todas las partidas y lo calcula en base al la venta neta 
	 *
	 */
	private void actualizarImportePorDescuento(){
		
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(int index=0;index<getPartidas().size();index++){
			NotasDeCreditoDet det=(NotasDeCreditoDet)getPartidas().get(index);
			double desc=det.getDescuento()/100;
			
			final Venta v=det.getFactura();
			final CantidadMonetaria devoluciones=v.getDevoluciones();
			final CantidadMonetaria ventaNeta=v.getTotal().add(devoluciones);
			CantidadMonetaria importe=CantidadMonetaria.pesos(0);
			if(BONIFICACIONES.contains(getNota().getTipo())){
				importe=ventaNeta.multiply(desc);
			}else if(getNota().getTipo().endsWith("V")){
				
				if(v.getSaldo().abs().doubleValue()>0){
					Assert.notNull(v.getCredito(),"Venta sin entidad VentaACredito generada");
					double descuento=v.getCredito().getDescuento()/100;
					final CantidadMonetaria descuento1=ventaNeta.multiply(descuento);
					final CantidadMonetaria restante=ventaNeta.subtract(descuento1);					
					importe=restante.multiply(desc);						
				}
			}
			det.setImporte(importe.multiply(-1));
			total=total.add(det.getImporte());
			getPartidas().set(index, det);
		}
		getNota().setTotalAsMoneda(total);
		getNota().setImporte(MonedasUtils.calcularImporteDelTotal(total));
		getNota().setIva(MonedasUtils.calcularImpuesto(getNota().getImporte()));		
	}
	
	
	
	/**
	 * Determina si el calculo sera mediante descuento o mediante
	 * importe fijo asignado
	 * 
	 * @return
	 */
	public boolean isPorDescuento() {
		return porDescuento;
	}
	public void setPorDescuento(boolean porDescuento) {
		boolean oldValue=this.porDescuento;
		this.porDescuento = porDescuento;
		firePropertyChange("porDescuento", oldValue, porDescuento);
	}
	
	private PropertyChangeListener descuentoHandler=new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {			
			//actualizar();
			if(isPorDescuento()){
				aplicarDescuento();
				actualizarImportePorDescuento();
			}
			
		}		
	};
	
	private PropertyChangeListener importeHandler=new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			if(!getNota().getTipo().equals("V")){
				if(!isPorDescuento())
					//actualizar();
					aplicarImporte();
				//actualizarImporte();
			}
			
		}
	};
	
	private class ListHandler implements ListEventListener<NotasDeCreditoDet>{
		public void listChanged(ListEvent<NotasDeCreditoDet> listChanges) {
			while(listChanges.next()){
				if(listChanges.getType()==ListEvent.DELETE 
						|| listChanges.getType()==ListEvent.UPDATE
						){
					if(isPorDescuento())
						actualizarImportePorDescuento();
					else
						aplicarImporte();
				}
				
			}
		}		
	}
	

}
