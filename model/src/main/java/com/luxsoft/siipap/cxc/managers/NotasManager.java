package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.CXCPermiso;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

public interface NotasManager {
	
	
	public NotaDeCredito buscarNota(final Long id);
	
	public void eliminar(final NotaDeCredito nota);
	
	public List<Pago> buscarPagosAplicados(final NotaDeCredito nota);
	
	public List<NotaDeCredito> buscarNotasCre(Periodo p);
	
	public List<NotasDeCreditoDet> buscarNotasDet(final Cliente c,final Periodo p);
	
	public NotaDeCredito buscarNotaDevolucion(final Long id);
	
	public List<Provision> buscarProvisiones();
	
	
	public void eliminarPagosAplicados(List<Pago> pagos);
	
	public void salvarNotasCre(final List<NotaDeCredito> notas);
	
	public void salvarNotaCre(final NotaDeCredito nota);
	
	public boolean tienePermiso(CXCPermiso per);
	
	public List<Venta> buscarVentasParaNota(Cliente c);
	
	/**
	 * 
	 * @param clave
	 * @return
	 */
	public List<Venta> buscarVentasParaNota(String clave);
	
	/**
	 * Busca las devoluciones existentes para un cliente
	 * mismas que no hayan sido aplicadas, es decir nota_id is null
	 * 
	 * @param clave
	 * @return
	 */
	public List<DevolucionDet> buscarDevoluciones(String clave);
	
	public List<Devolucion> buscarDevolucionesDisponibles(final Cliente c);
	
	
	public void actualizarDevolucion(final Devolucion d);
	
	/**
	 * Salva las notas de descuento correspondiente
	 * 
	 * @param nc
	 */
	public void salvarNCDevoCRE(final List<NotaDeCredito> notas);
	
	/**
	 * Actualiza la fecha de impresion de una nota de credito 
	 * 
	 * @param nc
	 */
	public void actualizarImpresion(final NotaDeCredito nc);
	
	
	/**
	 * Manda imprimir una nota de descuento actualizando el campo de impreso
	 * 
	 * @param nota
	 */
	public void imprimirNotaDeDescuento(final NotaDeCredito nota);
	
	
	/**
	 * Manda a imprimir una nota de devolución
	 * @param nota
	 */
	public void imprimirNotaDevolucion(final NotaDeCredito nota);
	
	
	/**
	 * Refresca los datos de la nota a partir de la base de datos
	 * 
	 * @param nota
	 */
	public void refresh(final NotaDeCredito nota);
	
	/**
	 * Regresa una lista de todas las Notas de descuento generadas en la cobranza
	 * o por anticipado
	 *  
	 * @param p
	 * @return
	 */
	public List<NotaDeCredito> buscarNotasParaImpresionPorDescuentos(final Periodo p,final boolean anticipadas);
	
	/**
	 * Regresa una lista de {@link NotaDeCredito} disponibles para ser usadas en
	 * pagos
	 * 
	 * @param c
	 * @return
	 */
	public List<NotaDeCredito> buscarNotasDeCreditoDisponibles(final Cliente c);
	
	/**
	 * Regresa una lista de {@link NotaDeCredito} disponibles para ser usadas en
	 * pagos
	 * 
	 * @param c
	 * @return
	 */
	public List<NotaDeCredito> buscarNotasDeCreditoDisponibles(final String clave);
	
	/**
	 * Obtiene el utlimo consecutivo 
	 * 
	 * @return
	 */
	public long nextNumero(final String tipo);
	
	/**
	 * Actualiza el vencimiento de las notas de cargo
	 * pendientes de pago
	 *
	 */
	public void actualizarRevision();
	
	public List<NotasDeCreditoDet> buscarNotasNoAplicables(final Date fecha,final String origen);
	
	/**
	 * Genera una nota de crredito para cancelación de una nota de cargo
	 * 
	 * @param cargo
	 * @return
	 */
	public NotaDeCredito generarNotaDeCancelacion(final NotaDeCredito cargo);
	
	/**
	 * 
	 * @return
	 */
	public List<NotaDeCredito> buscarCargosCheque();

}
