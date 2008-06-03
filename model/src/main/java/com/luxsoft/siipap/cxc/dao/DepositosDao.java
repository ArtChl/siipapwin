package com.luxsoft.siipap.cxc.dao;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.dao2.GenericDao;

public interface DepositosDao extends GenericDao<Deposito, Long>{
	
	public Deposito buscarDeposito(final Long id);
	
	/**
	 * 
	 * @param tipo
	 * @return
	 */
	public List<Deposito> buscarDepositos(String tipo);
	
	/**
	 * Busca todos los deopsitos generados por CXC
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Deposito> buscarDepositosCredito(final Date fecha);
	
	/**
	 * Genera depositos bancarios para todos los pagos generados en CXC que no sean
	 * en cheque o efectivo y que sean de Credito
	 * 
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Deposito> buscarDepositosEnCobranzaCre(final Date fecha);
	
	/**
	 * Busca todos los depositos generados por mostrador
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Deposito> buscarDepositosContado(final Date fecha);
	
	/**
	 * Busca todos los depoisstos generado para camioneta
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Deposito> buscarDepositosCamioneta(final Date fecha);
	
	
	/**
	 * Elimina los depositos importados desde siipap para la fecha indicada
	 * 
	 * @param fecha
	 */
	public void eliminarDepositosImportados(final Date fecha);
	
	
	
	
	
	

}
