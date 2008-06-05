package com.luxsoft.siipap.cxc.swing.cobranza2;

import java.util.List;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * PresentationModel para encapsular el comportamiento y estado 
 * del manenimiento de Facturas, por el momento es una interfaz muy ligera
 * que delega todo a otros objetos y por lo tanto es facil que sea administrada
 * por spring
 * 	
 * 	Responsablidades
 * 		1 Cargar sus pagos
 * 		2 Cargar sus notas
 *  
 * @author Ruben Cancino
 *
 */
public class CXCFacturaFormModel extends Model{
	
	private final PresentationModel facturaModel;
	private final ValidationResultModel validationModel;
	private VentasManager ventasManager;
	private CXCManager cxcManager;

	public CXCFacturaFormModel(final Venta venta) {
		facturaModel=new PresentationModel(venta);	
		validationModel=new DefaultValidationResultModel();
	}	
	
	
	
	public Venta getFactura(){
		return (Venta)facturaModel.getBean();
	}
	
	public void refreshVenta(){		
		getVentasManager().refresh(getFactura());
		System.out.println("Saldo: "+getFactura().getSaldo());
	}
	
	public List<Pago> getPagos(){
		return getCxcManager().buscarPagos(getFactura());		
	}
	public List<NotasDeCreditoDet> getNotas(){
		return getCxcManager().buscarNotas(getFactura());
	}

	public PresentationModel getFacturaModel(){
		return facturaModel;
	}

	public ValidationResultModel getValidationModel() {
		return validationModel;
	}

	public CXCManager getCxcManager() {
		return cxcManager;
	}
	public void setCxcManager(CXCManager cxcManager) {
		this.cxcManager = cxcManager;
	}
	public VentasManager getVentasManager() {
		return ventasManager;
	}

	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}
	

}
