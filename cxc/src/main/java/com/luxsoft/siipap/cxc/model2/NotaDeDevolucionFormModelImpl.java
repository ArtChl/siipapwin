package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;


/**
 * 
 * @author Ruben Cancino
 *
 */
public class NotaDeDevolucionFormModelImpl extends DefaultFormModel implements NotaDeDevolucionFormModel{
	
	private EventList<DevolucionDet> partidas;
	private ValueHolder comentario=new ValueHolder("");
	private ValueHolder fecha=new ValueHolder(new Date());
	
	

	public NotaDeDevolucionFormModelImpl(final Devolucion devo) {
		super(devo);
		devo.updateData();
		addBeanPropertyChangeListener(handler);
		
	}

	public Devolucion getDevolucion() {
		return (Devolucion)getBaseBean();
	}

	public EventList<DevolucionDet> getPartidas() {
		if(partidas==null){
			partidas=new BasicEventList<DevolucionDet>();
			partidas.addAll(getDevolucion().getPartidas());
			actualizarImporte();
		}
		return partidas;
	}
	
	public void actualizarImporte(){
		System.out.println("Actualizando devolucion");
		getDevolucion().actualizarImporte();
	}
	
	

	/**
	 * Genera las notas necesarias para la Devolucion
	 * 
	 * @return
	 */
	public List<NotaDeCredito> procesar(){		
		List<NotaDeCredito> notas=NotasUtils.generarNotasDeCreditoParaDevolucion(getDevolucion());
		final String tipo;
		final String serie;
		String origen=getDevolucion().getVenta().getOrigen();
		if(origen.equals("CRE")){
			tipo="J";
			serie="J";
		}else if(origen.equals("CAM")){
			tipo="I";
			serie="H";
		}else {
			tipo="H";
			serie="H";
		}		
		for(NotaDeCredito n:notas){
			n.setTipo(tipo);
			n.setClave(getDevolucion().getCliente());
			n.setCliente(getDevolucion().getVenta().getCliente());
			n.setSerie(serie);
			n.setComentario(comentario.getValue().toString());
			n.setFecha((Date)fecha.getValue());
			n.setOrigen(origen);
			n.setDevolucion(getDevolucion());
			
		}
		
		return notas;
	}

	private PropertyChangeListener handler=new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {			
			actualizarImporte();
		}
	};

	public ValueModel getComentarioModel() {
		return comentario;
	}

	public ValueModel getFechaModel() {
		return fecha;
	}
	
}