package com.luxsoft.siipap.inventarios.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.dao.GenericDao;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.Movimiento;
import com.luxsoft.siipap.inventarios.domain.Salida;




public interface MovimientoDao extends GenericDao<Movimiento,Long>{
	
	public List<Movimiento> buscarPorDia(final Date dia);
	
	/**
	 * Busca las partidas de un documento en especifico
	 * 
	 * @param sucursal
	 * @param tipo
	 * @param numero
	 * @return
	 */
	public List<Movimiento> buscarDocumento(final Integer sucursal,final String tipo,final Long numero);
	
	/**
	 * Localiza la entrada de la remision determinada
	 */
	public Entrada buscarEntradaPorRemision(final Integer sucursal,final Long facrem,String articulo);
	
	/**
	 * Busca las entradas de un articulo por periodo y tipo
	 * @param articulo
	 * @param tipo
	 * @param periodo
	 * @return
	 */
	public List<Entrada> buscarEntradas(final String articulo,final String tipo,final Periodo periodo);
	
	
	/**
	 * Busca las entradas del articulo para el periodo
	 * 
	 * @param art
	 * @param p
	 * @return
	 */
	public List<Entrada> buscarEntradas(String art,Periodo p);
	
	/**
	 * Busca las entradas existentes hasta la fecha indicada
	 * Util en el caluclo de costo promedio
	 * 
	 * @param articulo
	 * @param tipo
	 * @param hasta
	 * @return
	 */
	public List<Entrada> buscarEntradas(final String articulo,final String tipo,final Date hasta);
	
	/**
	 * Buscar las salidas en el periodo
	 * 
	 * @param articulo
	 * @param p
	 * @return
	 */
	public List<Salida> buscarSalidas(final String articulo,final Periodo p);
	
	/**
	 * Suma las entradas del articulo en el periodo  y sucursal
	 * @param a
	 * @param p
	 * @param sucursal
	 * @return
	 */
	public Long entradas(final String a,final Periodo p,final Integer sucursal);
	
	
	
	/**
	 * Suma las salidas del articulo en el periodo y sucursal
	 * @param a
	 * @param p
	 * @param sucursal
	 * @return
	 */
	public Long salidas(final String a,final Periodo p,final Integer sucursal);
	
	
	
	
	/**
	 * Calcula la existencia del articulo a la fecha en la sucursal
	 * incluyendo la fecha indicada
	 * 
	 * TODO: Investigar si este metodo debe existir como tal, es posible que el incluir la fecha
	 * provoque algun bug
	 * 
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public Long existencia(final String articulo, final Date fecha,	final  Integer sucursal);
	
	/**
	 * Calcula la existencia del articulo a la fecha en la sucursal
	 * sin incluir la fecha indicada es decir antes de la fecha
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public Long saldo(final String articulo, final Date antesDe,	final  Integer sucursal);
	
	/**
	 * Calcula la existencia en millares del articulo a la fecha en la sucursal 
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public BigDecimal existenciaEnUnidad(final String articulo, final Date fecha,	final  Integer sucursal);
	
	/**
	 * Calcula la existencia del articulo a la fecha 
	 * Se ocupa en los acumulados
	 * 
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public Long existencia(final String articulo, final Date fecha);
	
	
	
	/**
	 * Busca las entradas sin costo
	 * 
	 * @param dia
	 * @return
	 */
	public List<Entrada> buscarEntradasSinCosto(final Date dia);
	
	/**
	 * Busca las entradas de un periodo (mes/año)
	 * 
	 * @param p
	 * @return
	 */
	public List<Entrada> buscarEntradas(final String periodo);
	
	/**
	 * Busca las entradas de un periodo 
	 * 
	 * @param p
	 * @return
	 */
	public List<Entrada> buscarEntradas(final Periodo p);
	
	/**
	 * Busca las entradas de un periodo para un tipo determinado
	 * 
	 * @param p
	 * @param tipo
	 * @return
	 */
	public List<Entrada> buscarEntradas(final Periodo p,final String tipo);
	
	/**
	 * Busca las entradas por folio
	 * @param numero
	 * @return
	 */
	public List<Entrada> buscarEntradasPorFolio(Long numero);
	
	public int contarRegistros(final Date fecha);
	
	public List<Salida> buscarSalidas(final Periodo p,final String... tipos);
	
	
	
	public List<Entrada> buscarEntradas(final String tipo,final String articulo,final Integer sucursal,final Periodo p);
	
			
}
