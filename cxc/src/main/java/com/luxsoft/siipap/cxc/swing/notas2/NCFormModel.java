package com.luxsoft.siipap.cxc.swing.notas2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.jgoodies.validation.ValidationResult;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.NotasFactory;


import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;

public class NCFormModel extends AbstractGenericFormModel<NotaDeCredito, Long>{
	
	private EventList<NotasDeCreditoDet> notasDet;
	private ListEventListener sincronizador;
	
	public NCFormModel(final NotaDeCredito bean){
		super(bean);			
		notasDet=GlazedLists.eventList(new BasicEventList<NotasDeCreditoDet>());
		
		sincronizador=GlazedLists.syncEventListToList(notasDet, bean.getPartidas());
		notasDet.addListEventListener(new PartidasHandler());
		
	}
	
	
	/**
	 * Actualiza los cambios del cliente limpiando la lista de partidas de la nota
	 * esto por que no puede haber partidas de notas que no sean del mismo cliente
	 * que el maestro
	 *
	 */
	private void updateCliente(){
		notasDet.clear();
	}

	@Override
	protected void initModels() {			
		super.initModels();
		getComponentModel("importe").setEditable(false);			
		getComponentModel("iva").setEditable(false);
		getComponentModel("totalAsMoneda").setEditable(false);			
	}
	

	@Override
	protected void initEventHandling() {			
		super.initEventHandling();
		getModel("cliente").addValueChangeListener(new ClienteHandler());
	}

	@Override
	public ValidationResult validate() {			
		ValidationResult r=super.validate();
		if(notasDet.size()==0)
			r.addError("No se han asignado partidas (Ventas/Devoluciones/Etc)");
		return r;
	}
	
	/**
	 * Actualiza las partidas, principamente informa al bean que recalcule 
	 * el importe de la nota de credito en funcion de sus partidas, asi como 
	 * informar al ValidationModel
	 *
	 */
	private void updatePartidas(){			
		getFormBean().actualizar();
		updateValidation();
	}
	
	/**
	 * Acceso a la lista de las partidas, misma que esta sincronizada con las partidas
	 * del bean NotaDeCredito
	 * 
	 * @return
	 */
	public EventList<NotasDeCreditoDet> getPartidasList(){
		return notasDet;
	}
	
	/**
	 * Regresa una NotasDeCreditoDet adecuada para la nota en edicion
	 * Utiliza NotasFactory
	 * @return
	 */
	public NotasDeCreditoDet getPartida(){
		NotasDeCreditoDet det=NotasFactory.getPartidaDeNota(getFormBean());			
		det.setNota(getFormBean());
		return det;
	}
	
	/**
	 * Agrega una partidad a la lista de partidas, este metodo puede ser intercepatdo por AOP
	 * 
	 * @param det
	 */
	public void addPartida(final NotasDeCreditoDet det){						
		notasDet.add(det);
	} 

	@SuppressWarnings("unchecked")
	@Override
	public void commit() {			
		super.commit();
		notasDet.removeListEventListener(sincronizador);
	}



	/**
	 * Informa de cambios en la lista que mantiene las partidas de la nota
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class PartidasHandler implements ListEventListener<NotasDeCreditoDet>{

		public void listChanged(ListEvent<NotasDeCreditoDet> listChanges) {
			while(listChanges.hasNext()){
				listChanges.next();
				updatePartidas();
				
			}
		}			
	}
	
	/**
	 * Actualiza el cambio de cliente 
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class ClienteHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			updateCliente();
		}			
	}

}
