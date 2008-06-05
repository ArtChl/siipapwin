package com.luxsoft.siipap.cxc.nc;

import java.text.SimpleDateFormat;

import javax.swing.JTextField;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

/**
 * Modelo para controlar el estado de una forma de solo lectura
 * para notas de credito/cargo 
 * 
 * @author Ruben Cancino 
 *
 */
public class RONotaDeCreditoFormModel extends DefaultFormModel{
	
	private EventList<NotasDeCreditoDet> det;

	public RONotaDeCreditoFormModel() {
		super(NotaDeCredito.class);
	}
	
	public RONotaDeCreditoFormModel(Object bean) {
		super(bean,true);
	}

	public EventList<NotasDeCreditoDet> getPartidas(){
		if(det==null){
			det=GlazedLists.eventList(getNota().getPartidas());			
		}
		return det;
	}
	
	public TableFormat<NotasDeCreditoDet> getTableFormat(){
		String[] props={"sucDocumento","numDocumento","serieDocumento","tipoDocumento","fecha","importe","comentario","factura.id","nota.id"};
		String[] cols={"Suc","Numero","S","T","Fecha","Importe","Comentario","Venta ID","Nota ID"};
		return GlazedLists.tableFormat(NotasDeCreditoDet.class, props,cols);
	}
	

	protected NotaDeCredito getNota(){
		return (NotaDeCredito)getBaseBean();
		
	}
	
	public String getNombre(){
		if(getNota().getCliente()!=null)
			return getNota().getCliente().getNombre();
		return "NA";
	}
	
	public void installDevolucionData(final JTextField[] fields){
		if(getNota().getDevolucion()!=null){
			fields[0].setText(getNota().getDevolucion().getId().toString());
			fields[1].setText(getNota().getDevolucion().getNumero().toString());
			fields[2].setText(new SimpleDateFormat("dd/MM/yyyy").format(getNota().getDevolucion().getFecha()));
		}else{
			for(JTextField tf:fields){
				tf.setText("NA");
				tf.setToolTipText("No a plica, la nota no es por devolución");
			}
		}
	}

}
