package com.luxsoft.siipap.cxc.swing.notas2;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;

public class NCDescuentoModel extends PresentationModel{
	
	private EventList<NotasDeCreditoDet> notasDet;
	private EventList<NotaDeCredito> notas;
	private int consecutivo;
	private Cliente cliente;
	
	public NCDescuentoModel(){
		super(null);
		setBean(this);
	}
	
	private void initGlazedList(){
		notasDet=GlazedLists.threadSafeList(new BasicEventList<NotasDeCreditoDet>());
		//notas=new CollectionList(notasDet,new CollectionList.Model<E, S>)
	}
	
	/**
	 * Carga todas las facturas requeridas para el proceso de generacion
	 *
	 */
	public void cargar(){
		
	}
	

	public int getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(int consecutivo) {
		int old=this.consecutivo;
		this.consecutivo = consecutivo;
		firePropertyChange("consecutivo",old,consecutivo);
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		Object old=this.cliente;
		this.cliente = cliente;
		firePropertyChange("cliente",old,cliente);
	}
	
	
	
	

}
