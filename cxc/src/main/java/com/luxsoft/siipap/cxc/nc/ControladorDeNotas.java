package com.luxsoft.siipap.cxc.nc;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Interfaz para centralizar las operaciones en la vista de notas de credito
 * Actua como un Facade para las todas las operaciones, delegando a otros objetos
 * la tarea. Al ser una interfaz esta puede ser controlada por spring's AOP 
 * 
 * @author Ruben Cancino
 *
 */
public interface ControladorDeNotas {
	
	/**
	 * Regresa una lista con los clientes de credito
	 * 
	 * @return
	 */
	public List<ClienteCredito> buscarClientesACredito();
	
	
	/**
	 * Muestra los datos relevantes a una nota de credito
	 * en una forma
	 * 
	 * @param nota
	 */
	public void mostrarNota(final NotaDeCredito nota);
	
	/**
	 * Elimina una nota de credito/cargo
	 * @param nota
	 */
	public void eliminarNota(final NotaDeCredito nota);
	
	/**
	 * Cancela una nota de carto generando una nota de credito y generando el pago correspondiente
	 *  
	 * @param cargo
	 */
	public void cancelarNotaDeCargo(final NotaDeCredito cargo);
	
	/**
	 * Controla el proceso de aplicacion de pagos automaticos sobre un grupo de facturas
	 * 
	 * 
	 * @param ventas
	 */
	public boolean registrarPagoAutomatico(final List<NotaDeCredito> cargos);
	
	
	/**
	 * Muestra los detalles de una venta
	 * 
	 * @param v
	 */
	public void mostrarVenta(final Venta v);
	
	/**
	 * Refrescar las propiedades de una venta desde la base de datos
	 * 
	 * @param v
	 * @return
	 */
	public Venta refrescar(final Venta v);
	
	/**
	 * Refresca las propiedades de la nota desde la base de datos
	 * @param nota
	 */
	public void refrescar(final NotaDeCredito nota);
	
		
	
	/**
	 * Genera las notas de credito por descuento anticipado 
	 * 
	 * 
	 * @param c
	 * @param ventas
	 */
	public List<NotaDeCredito> generarNotasDeDescuentoPorAnticipado(final Cliente c,final List<Venta> ventas,final Date fecha);
	
	/**
	 * Busca la lista de devoluciones del cliente sin nota de credito
	 * @param c
	 * @return
	 */
	public List<Devolucion> buscarDevolucionesDisponibles(final Cliente c);
	
	/**
	 * Genera una o mas notas de credito por devolucion
	 * 
	 * @param c
	 * @return
	 */
	public List<NotaDeCredito> aplicarNotaPorDevolucion(final Cliente c);
	
	/**
	 * Generar una o mas notas de credito por bonificacion
	 * 
	 * @param c
	 * @param ventas
	 * @return
	 */
	public List<NotaDeCredito> aplicarNotaPorBonificacion(final Cliente c,final List<Venta> ventas);
	
	/**
	 * Genera una o mas nota de credito por descuento financiero
	 * 
	 * @param c
	 * @param ventas
	 * @return
	 */
	public List<NotaDeCredito> aplicarNotaPorDescuentoFinanciero(final Cliente c,final List<Venta> ventas);
	
	/**
	 * Genera una lista de notas de cargo para la el cliente y ventas seleccionadas
	 * 
	 * @param c
	 * @param ventas
	 * @return
	 */
	public List<NotaDeCredito> generarNotasDeCargo(final Cliente cliente,final List<Venta> ventas);
	
	
	public PagoConNota aplicarPagoConNota(final Cliente c,final List<NotaDeCredito> cargos);
	
	/**
	 * Aplica pago con otros a nota de cargo
	 * 
	 * @param c
	 * @param cargos
	 * @return
	 */
	public PagoConOtros aplicarPagoConOtros(final Cliente c,final List<NotaDeCredito> cargos,final List<PagoM> dispponibles);
	
	public List<PagoM> buscarDisponibles(final Cliente c);
	
	
	/**
	 * Manda imprimir una serie de notas de credito
	 * 
	 * @param notas
	 */
	public void imprimirNotasDeCredito(final List<NotaDeCredito> notas);

}
