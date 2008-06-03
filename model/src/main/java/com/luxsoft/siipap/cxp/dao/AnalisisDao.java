package com.luxsoft.siipap.cxp.dao;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxp.domain.Analisis;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.cxp.domain.AnalisisDet;
import com.luxsoft.siipap.cxp.domain.CXPFactura;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Proveedor;

public interface AnalisisDao {
	
	
		
	/**
	 * Actualiza un Analisis
	 * 
	 * @param analisis
	 */
	public void salvar(Analisis analisis);
	
	public void eliminar(final Analisis analisis);
	
	public Analisis buscarAnalisis(final Long id);
	
	/**
	 * Busca Partidas analizadas por fecha
	 * 
	 * @param fecha
	 * @return
	 */
	public List<AnalisisDet> buscarAnalisisUnitarios(final Date fecha);
	
	/**
	 * Busca partidas analizadas por periodo
	 * 
	 * @param periodo
	 * @return
	 */
	public List<AnalisisDet> buscarAnalisisUnitarios(final Periodo periodo);
	
	/**
	 * Busca Analisis por periodo
	 * 
	 * @param p
	 * @return
	 */
	public List<Analisis> buscarAnalisisPorPeriodo(final Periodo p);
	
	/**
	 * Busca todos los analisis de un proveedor
	 * 
	 * @param p
	 * @return
	 */
	public List<Analisis> buscarAnalisisPorProveedor(final Proveedor p);
	
	
	/**
	 * Actualiza un analisis para definir que se ha impreso por primera vez
	 * 
	 * @param analisis
	 */
	public void actualizarImpresion(final Analisis analisis);
	
	
	/**
	 * Inicializar las partidas de un analisis
	 *
	 */
	public void inicializarPartidas(final Object analisis);
	
	/**
	 * Inicializa una partida de analisis
	 * 
	 * @param det
	 */
	public void inicializarAnalisisDet(final AnalisisDet det);
 
	public void reload(final Analisis analisls);
	
	
	public void reAsignarCostoDeCom(Long id);
	
	/**
	 * Localiza un analisis a partir de una entrada
	 * @param entrada
	 * @return
	 */
	public AnalisisDet buscarAnalisis(AnalisisDeEntrada entrada);
	
	/**
	 * Valida que el costo del analisis este reflejado en el campo
	 * de COMS. Regresa una lista con los AnalisDet que fueron ajustados
	 * si que estos cambios sean persistidos a la base de datos
	 * 
	 * @return
	 */
	public List<AnalisisDet> validarCostoEnComs();
	
	
	
	/**
	 * TODO BORRAR
	 *
	 */
	public void parcheDeCambioDeProveedor();
	
	
	public List<AnalisisDet> buscarAnalisisUnitariosPorFechaDeEntrada(final Date date,Integer sucursal);
	
	public Analisis buscarAnalisis(final CXPFactura cargo);
	
}
