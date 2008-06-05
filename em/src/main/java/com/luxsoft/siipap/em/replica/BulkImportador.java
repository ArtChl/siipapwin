package com.luxsoft.siipap.em.replica;

import com.luxsoft.siipap.domain.Periodo;

public interface BulkImportador {

	/**
	 * Importa los registros del periodo solicitado en grandes cantidades
	 * este metodo es para se usado simpre que se requiera una carga fresca
	 * 
	 * @param p
	 */
	public void bulkImport(Periodo p);
	
	/**
	 * Valida que no existan registros en el periodo seleccionado para poder generar una carga bulk nuevaç
	 * 
	 * @param p
	 */
	public void validarBulkImport(Periodo p);

	

}