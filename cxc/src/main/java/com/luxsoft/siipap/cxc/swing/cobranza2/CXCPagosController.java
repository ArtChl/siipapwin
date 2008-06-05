package com.luxsoft.siipap.cxc.swing.cobranza2;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Controloador para la administracion de pagos
 *  Permite
 *  	Eliminar pagos
 *  	Aplicar pagos a facturas
 *  	Aplicar pagos a cliente (Pendiente)
 *  	Mostrar consulta de solo lectura de un pago (Pendiente)
 *  	Aplicar pagos con descuentos
 * 
 * @author Ruben Cancino
 * 
 */
public class CXCPagosController {
	
	private CXCManager manager;
	private VentasManager ventasManager;

	/**
	 * Aplica un pago de forma generica a aun Cliente
	 * 
	 * @param c
	 * @return
	 */
	public Pago aplicarPago(final Cliente c) {
		return null;
	}

	/**
	 * Permite aplicar un pago a una venta, esta debe tenera saldo pendiente
	 * 
	 * @param v
	 * @return
	 */
	public Pago aplicarPago(final Venta v) {
		/**
		if(v.getSaldo().abs().doubleValue()<=0){
			MessageUtils.showMessage(msg, titulo)
		}
		Assert.isTrue(v.getSaldo().abs().doubleValue() > 0,
				"No se permiten aplicar pagos a facturas sin saldo");
				**/
		
		return null;
	}

	/**
	 * Elimina un pago de la base de datos, esto modifica inmediatamente el saldo de la venta
	 * y puede tener implicaciones serias, usar con cuidado
	 * 
	 * @param p
	 * @return
	 */
	public boolean eliminarPago(final Pago p) {

		String msg = MessageFormat.format(resolveDeleteWarning(p.getVenta()), p
				.getId());
		int res = JOptionPane.showConfirmDialog(getParentComponent(), msg,
				"Eliminación de pago", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (res == JOptionPane.OK_OPTION) {
			try {
				getManager().eliminar(p);
				return true;
			} catch (Exception e) {
				MessageUtils.showError("Error eliminando pago: " + p, e);
			}
		}
		return false;
	}
	
	public boolean editarPago(final Pago p){
		MessageUtils.showMessage("Por el momento no se permite la modificación de pagos", "Modificación de Pagos");
		return false;
	}
	

	/**
	 * Resuelve un mensaje apropiado para la eliminacion de pagos
	 * @param v
	 * @return
	 */
	private String resolveDeleteWarning(final Venta v) {
		if (v.getDescuentos() != 0) {
			return "Seguro que desea eliminar el pago {0} , esta factura tiene descuentos "
					+ "\naplicados por lo que esto puede tener implicaciones serias en la "
					+ "\nconsistencia de la información?";
		} else
			return "Seguro que desa eliminar el pago {0}";
	}

	public Component getParentComponent() {
		if (Application.isLoaded()) {
			return Application.instance().getMainFrame();
		} else
			return null;
	}
	
	/** Colaboradores **/

	public CXCManager getManager() {
		return manager;
	}

	public void setManager(CXCManager manager) {
		this.manager = manager;
	}

	public VentasManager getVentasManager() {
		return ventasManager;
	}

	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}
	
	
	

}
