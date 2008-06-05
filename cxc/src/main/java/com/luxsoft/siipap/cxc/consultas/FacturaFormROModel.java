package com.luxsoft.siipap.cxc.consultas;

import java.util.List;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * PresentationModel patter para una venta en modo de solo
 * lectura
 * 
 * @author Ruben Cancino
 *
 */
public class FacturaFormROModel extends Model{
	
	private final PresentationModel facturaModel;
	
	public FacturaFormROModel(final Venta v){
		ServiceLocator.getVentasManager().getVentasDao().inicializarPartidas(v);
		facturaModel=new PresentationModel(v);
	}	
	
	public Venta getFactura(){
		return (Venta)getFacturaModel().getBean();
	}

	public PresentationModel getFacturaModel() {
		return facturaModel;
	}
	
	public List<Pago> getPagos(){
		return getManager().buscarPagos(getFactura());		
	}
	public List<NotasDeCreditoDet> getNotas(){
		return getManager().buscarNotas(getFactura());
	}
	
	public CXCManager getManager(){
		return ServiceLocator.getCXCManager();
	}
	
	
	

}
