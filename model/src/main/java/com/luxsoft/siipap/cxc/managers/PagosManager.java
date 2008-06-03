package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Encapsula todas las reglas de negicios vigentes para la persistencia
 * de PagoM y Pagos
 * 
 * @author Ruben Cancino
 *
 */
public interface PagosManager {
	
	/**
	 * Salva un grupo de pagos aplicando las reglas de negocios vigentes
	 * para este proceso
	 * 
	 * @param grupo
	 */
	public void salvarGrupoDePagos(final PagoM grupo);
	
	/**
	 * Actualiza un pagoM desde la base de datos
	 * 
	 * @param pago
	 */
	public void refresh(final PagoM pago);
	
	//public PagoM refresh2(final PagoM pago);
	
	
	/**
	 * Busca las posibles ventas suceptibles de pagos automaticos
	 * 
	 * @param pago
	 * @return
	 */
	public List<Venta> buscarPosiblesPagosAutomaticos(final PagoM pago);
	
	
	/**
	 * Busca todos los pagos aplicados para el cliente en el periodo determinado
	 * @param c
	 * @param p
	 * @return
	 */
	public List<Pago> buscarPagosAplicados(final Cliente c,final Periodo p);
	
	/**
	 * Busca todos los pagos aplicados para el cliente en el periodo determinado
	 * @param c
	 * @param p
	 * @return
	 */
	public List<PagoM> buscarSaldosAFavor(final Cliente c);
	
	/**
	 * Busca todos los pagos de la fecha indicada que terminaran con saldo a favor en la misma fecha
	 * @param fecha
	 * @return
	 */
	public List<PagoM> buscarSaldosAFavor(final Date fecha);
	
	/**
	 * Busca todos los pagos con otros de la fecha indicada
	 * 
	 * @param fecha
	 * @return
	 */
	public List<PagoConOtros> buscarPagosConOtros(final Date fecha);
	
	/**
	 * Busca los pagos de menos de la fecha indicada
	 * 
	 * @param fecha
	 * @return
	 */
	public List<PagoM> buscarPagosDeMenos(final Date fecha);
	
	
	
	/**
	 * Muestra todos los saldos a favor existentes
	 * 
	 * @return
	 */
	public List<Map<String, Object>> buscarSaldosAFavor();
	
	/**
	 * Busca todos los pagos aplicados para el periodo determinado
	 * @param c
	 * @param p
	 * @return
	 */
	public List<Pago> buscarPagosAplicados(final Periodo p);
	
	
	/**
	 * Busca todos los pagos aplicados para el periodo determinado incluidos los del sistema anterior
	 * @param c
	 * @param p
	 * @return
	 */
	public List<Pago> buscarPagosAplicadosOld(final Periodo p);
	

	/**
	 * Intenta eliminar un pago aplicado
	 *  
	 * @param pago
	 * 
	 */
	public void eliminarPagoM(final PagoM pago);
	
	public void eliminarPagoM(final Long id);
	
	/**
	 * Intenta eliminar de la base de datos un pago unitario aplicado
	 * 
	 * @param pago
	 * 
	 */
	public void eliminarPago(final Pago pago);
	
	/**
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Pago> buscarPagos(final Date fecha,String origen);
	
	/**
	 * 
	 * @param fecha
	 * @param origen
	 * @return
	 */
	public List<PagoM> buscarPagosM(final Date fecha,final String origen);
	
	/**
	 * Pagos efectivamente cobrados para la fecha 
	 * 
	 * @param fecha	 * 
	 * @return
	 */
	public List<PagoM> buscarPagosEfectivamenteCobrados(final Date fecha);
	
	/**
	 * 
	 * @param fecha
	 * @return
	 */
	public List<PagoM> buscarPagosConTarjeta(final Date fecha);
	
	/**
	 * 
	 * @param cliente
	 * @return
	 */
	public List<PagoM> buscarPagosAplicadosConCheque(final String cliente);
	
	
	//public List<PagoM> buscarPagosM(final Date fecha,String origen,final int sucursal);  

}
