package com.luxsoft.siipap.cxp.dao;

import java.util.List;

import com.luxsoft.siipap.cxp.domain.Crecibos;
import com.luxsoft.siipap.cxp.domain.Crecibosde;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Proveedor;

public interface ContraRecibosDao {
	
	/**
	 * Salva un contra recibo aplicando todas las reglas  de negocios vigentes
	 * 
	 * @param recibo
	 */
	public void salvarContraRecibo(final Crecibos recibo);
	
	/**
	 * Actualiza un contrarecibo aplicando todas las reglas de negocios vigentes
	 * @param recibo
	 */
	public void actualizarContraRecibo(final Crecibos recibo);
	
	/**
	 * Elimina un contrarecibo aplicando todas las reglas de negocios vigentes
	 * 
	 * @param recibo
	 */
	public void eliminarContraRecibo(final Crecibos recibo);
	
	/**
	 * Busca el contrarecibo indicado
	 * @param id
	 */
	public Crecibos buscarContraRecibo(final Long id);
	
	/**
	 * Busca todas las partidas del los recibos del periodo seleccionado
	 * @param p
	 * @return
	 */
	public List<Crecibosde> buscarPartidasDeRecibos(final Periodo p);
	
	/**
	 * Localiza un detalle de contrarecibo 
	 * 
	 * @param p
	 * @param factura
	 * @return
	 */
	public Crecibosde buscarPartida(final Proveedor p,final String factura);
	
	/**
	 * Re inicializa un contrarecibo
	 * 
	 * @param recibo
	 */
	public void refresh(final Crecibos recibo);

}
