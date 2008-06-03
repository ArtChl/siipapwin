package com.luxsoft.siipap.maquila.manager;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.InventarioMaq;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;

/**
 * Manager central de manejo de informacion relacionada con maquila
 * 
 * @author Ruben Cancino
 *
 */
public interface MaquilaManager {
	
	/**
	 * 
	 * Genera el inventario de maquila para la entrada id (MOVIMIENTO_ID) especificadao
	 * 
	 * @param entradaId
	 * @param fecha
	 * @return
	 */
	public InventarioMaq cargarInventario(final Long entradaId,final Date fecha);
	
	/**
	 * Genera el inventario de maquila para la entrada especificada
	 * 
	 * @param entrada
	 * @param fecha
	 * @return
	 */
	public InventarioMaq cargarInventario(final EntradaDeMaterial entrada,final Date fecha);
	
	
	/**
	 * Regresa todas las entradas de material
	 * 
	 * @return
	 */
	public List<EntradaDeMaterial> buscarEntradasDeMaterial();
	
	/**
	 * Busca todas las salidas a corte existentes
	 * 
	 * @return
	 */
	public List<SalidaACorte> buscarSalidasACorte();
	
	/**
	 * Busca todas las salidas de bobina existentes
	 * 
	 * @return
	 */
	public List<SalidaDeBobinas> buscarSalidasDeBobina();
	
	/**
	 * Busca todas las salidas de material
	 * 
	 * @return
	 */
	public List<SalidaDeMaterial> buscarSalidasDeMaterial();
	
	
	public List<EntradaDeHojas> buscarEntradasDeHojas();
	
	/**
	 * Regresa una lista unica de todas las SalidaDeHojas existentes
	 * 
	 * @return
	 */
	public List<SalidaDeHojas> buscarSalidaDeHojas();
	
	
	

}
