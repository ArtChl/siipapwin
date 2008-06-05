package com.luxsoft.siipap.cxc.pagos;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaACredito;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Diversos procesos relacionados con pagos 
 * 
 * @author Ruben Cancino
 *
 */
public interface IControladorDePagos {
	

	/**
	 * Propociona un dialogo que permite generar un pago y aplicarlo al grupo de facturas
	 * seleccionadas
	 * 
	 * @param ventas
	 */
	public PagoM registrarPagoCreditoProvisionables(final Cliente c,final List<Venta> ventas);
	
	/**
	 * Proporciona un UI para registrar pagos con forma de pago S Otros productos
	 * 
	 * @param c
	 * @param ventas
	 * @return
	 */
	public void registrarPagoConOtros(final Cliente c,final List<Venta> ventas);
	
	/**
	 * Genera un pago con {@link NotaDeCredito} a un grupo de ventas. Regresa una lista 
	 * de las ventas que fueron afectadas en este proceso 
	 * 
	 * @param c
	 * @param ventas
	 */
	public List<Venta> registrarPagoConNota(final Cliente c,final List<Venta> ventas);
	
	/**
	 * Completa el proceso de pago genrando descuentos y realizando pagos automaticos 
	 * cuando estos apliquen. 
	 *  
	 * 
	 * @param ventas
	 */
	public List<Venta> completar(final PagoM pago);

	/**
	 * Controla el proceso de aplicacion de pagos automaticos sobre un grupo de facturas
	 * 
	 * 
	 * @param ventas
	 */
	public void registrarPagoAutomatico(final List<Venta> ventas);
	
	/**
	 * 
	 * @param ventas
	 */
	public void diferenciaCambiaria(final List<Venta> ventas);
	
	/**
	 * Verifica si un pago es de tipo X ó M y procesa de ser
	 * necesario si el usuario acepta la diferencia cambiaria
	 * 
	 * @param pago
	 */
	public boolean diferenciaCambiaria(final PagoM pago);
	
	/**
	 * Muestra los detalles de una venta
	 * 
	 * @param v
	 */
	public void mostrarVenta(final Venta v);
	
	
	
	/**
	 * Actualiza las ventas indicadas en un sub-proceso
	 * 
	 * 
	 */
	public void actualizarVentas(final List<Venta> ventas);
	
	/**
	 * Elimina la {@link VentaACredito} y la {@link Provision}
	 * de las facturas seleccionadas
	 * 
	 * @param venta
	 */
	public void eliminarCreditoYProvision(final List<Venta> ventas);
	
	/**
	 * Actualiza el numero fiscal de una factura
	 * 
	 * @param venta
	 */
	public void actualizarNumeroFiscal(final Venta venta);
	
	/**
	 * Actualiza el comentario de CXC para el catalogo de clientes de credito
	 * @param c
	 */
	public void actualizarComentarioDeCxC(final ClienteCredito c);
	
	/**
	 * Intenta eliminar un pago tipo PagoM aplicado
	 * 
	 * @param pago
	 * @return
	 */
	public boolean eliminarPagoM(final PagoM pago);
	
	/**
	 * Intenta eliminar un pago unitario 
	 * 
	 * @param pago
	 * @return
	 */
	public boolean eliminarPago(final Pago pago);
	
	
	/**
	 * Genera las notas de credito de descuento para las facturas que
	 * califiquen
	 * 
	 * @param c
	 * @param ventas
	 */
	public List<NotaDeCredito> aplicarNotaDeDescuento(final Cliente c,final List<Venta> ventas,final Date fecha);
	
	/**
	 * Genera las notas de credito de descuento para las facturas que
	 * califiquen
	 * 
	 * @param c
	 * @param ventas
	 */
	public List<NotaDeCredito> aplicarNotaDeDescuento(final Cliente c,final List<Venta> ventas,final Date fecha,boolean condonar);

	/**
	 * Acceso al manager de ventas
	 * @return
	 */
	public VentasManager getVentasManager();
	
	
	/**
	 * Verifica si para este pago proceden notas de credito de descuento
	 * 
	 * 
	 * @param pago
	 * @return
	 */
	public boolean procedeDescuento(final PagoM pago);
	
	/**
	 * Registra un anticipo para el cliente especificado
	 * 
	 * @param cliente
	 * @return
	 */
	public PagoM registrarAnticipo(final ClienteCredito cliente);
	
	/**
	 * Traslado de ventas a juridico
	 * 
	 * @param ventas
	 */
	public boolean trsladoAJuridico(final List<Venta> ventas);

}