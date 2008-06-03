package com.luxsoft.siipap.maquila.dao;

import java.util.List;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.MaterialHojeado;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;

public interface HojeadoDao {
	
	public void create(MaterialHojeado mat);
	
	public void delete(MaterialHojeado mat);
	
	public void update(MaterialHojeado mat);
	
	public MaterialHojeado buscarPorId(Long id);
	
	public List<EntradaDeHojas> buscarEntradas();
	
	public List<EntradaDeHojas> buscarDisponibles();
	
	public void recalcularCostoDeCom(SalidaDeHojas salida);
	
	/**
	 * Regresa una lista con todas las salidas de hojas existentes
	 *  
	 * 
	 * @return
	 */
	public List<SalidaDeHojas> buscarSalidas();
	
	public void inicializarSalida(final SalidaDeHojas sal);
	
	public List<AnalisisDeEntrada> buscarComs(final String proveedor);
	
	public List<AnalisisDeEntrada> buscarComs(final String proveedor,final String articulo);

}
