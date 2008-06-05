package com.luxsoft.siipap.em.validacion;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;

public interface Validador {

	
	
	public int contarRegistrosEnSiipap(final Periodo p);
	
	public int contarRegistrosEnSiipapWin(final Periodo p);
	
	public List<ReplicaLog> validarDia(Date dia);
	
	
}
