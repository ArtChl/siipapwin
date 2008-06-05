package com.luxsoft.siipap.cxc.model2;

import java.util.List;

import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.swing.form2.IFormModel;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;


public interface NotaDeDevolucionFormModel extends IFormModel{
	
	
	public Devolucion getDevolucion();
	
	public ValueModel getComentarioModel();
	
	public ValueModel getFechaModel();

	public EventList<DevolucionDet> getPartidas();
	
	public List<NotaDeCredito> procesar() ;
	
	public void actualizarImporte();

	

}
