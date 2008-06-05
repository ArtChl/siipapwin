package com.luxsoft.siipap.em.replica;

import org.springframework.util.StringUtils;

public enum Replicadores {
	
	/**
	 * Replicador que replica tanto Ventas como VentasDet 
	 */
	ArticulosReplicator,
	ClientesReplicator,
	VentasReplicator,	
	VentasDetReplicator,
	DevolucionesReplicator,
	DevolucionesDetReplicator,
	NotasDeCreditoReplicator,
	NotasDeCreditoDetReplicator,
	ChequesDevueltosReplicator,
	JuridicoReplicator,
	PagosReplicator,
	DescuentosPorVolReplicator,
	DescuentosPorCliente;
	
	public String toString(){
		return StringUtils.uncapitalize(name());
	}


}
