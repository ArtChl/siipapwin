package com.luxsoft.siipap.em.replica;

import java.util.List;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;

public interface Replicador extends BulkImportador{
	
	public boolean isFechado();
	
	/**
	 * Valida la carga de un periodo en especifico, generando un registro log del mismo
	 * 
	 * 
	 * @param periodo
	 */
	public List<ReplicaLog> validar(final Periodo periodo);
	
	
	
	/**
	 * Importa los beans correspondientes al periodo indicado
	 * sin persistirlos a la base de datos
	 * 
	 * @param periodo
	 * @return
	 */
	public List importar(Periodo periodo);
	
	/**
	 * Persiste un bean importado a la base de datos
	 * 
	 * @param bean
	 */
	public void persistir(Object bean);
	
	
	/**
	 * Exporta un bean a otra fuente de datos
	 * 
	 * @param bean
	 */
	public void exportar(Object bean);
	
	
	
	

}
