package com.luxsoft.siipap.cxc.dao;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Anticipo;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;

/**
 * DAO para la persistencia de instancias de PagoConDescuentos 
 * 
 * @author Ruben Cancino
 *
 */
public interface PagoMDao {
	
	public PagoM buscarPorId(final Long id);
	
	public void salvar(final PagoM pd);
	
	public void eliminar(final PagoM pd);
	
	public List<PagoM> buscarDisponibles(final Cliente cliente);
	
	public List<PagoM> buscarDisponibles(final String cliente);
	
	
	
	/**
	 * Busca todos los pagosM de la fecha indicada que terminaran con saldo a favor en la misma fecha
	 * 
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
	 * Busca todos los anticipos del dia
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Anticipo> buscarAnticipos(final Date fecha);
	
	/**
	 * Busca los pagos de menos (Automaticos o manuales) para la fecha indicada
	 * 
	 * @param fecha
	 * @return
	 */
	public List<PagoM> buscarPagosDeMenos(final Date fecha);
	
	
	/**
	 * Busca los pagos en efectivo para la fecha indicada
	 * 
	 * @param fecha
	 * @param origen
	 * @return
	 */
	public List<PagoM> buscarPagosEfectivos(final Date fecha,final String origen);
	
	/**
	 * 
	 * @param cliente
	 * @return
	 */
	public List<PagoM> buscarPagosAplicadosConCheque(final String cliente);
	
	/**
	 * 
	 * @param fecha
	 * @param forma
	 * @param origen
	 * @return
	 */
	public List<PagoM> buscarPagos(final Date fecha,final FormaDePago forma,final String origen);
	
}