package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Juridico;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Clase estatica para el manejo de asuntos juridicos
 * 
 * @author Ruben Cancino
 *
 */
public interface JuridicoManager {
	
	
	public  void transferirJuridico(final Venta v,final Date fecha);
	
	public void transferirJuridico(final NotaDeCredito cargo,final Date fecha);
	
	public void transferirJuridico(final ChequeDevuelto cheque,final Date fecha);
	
	public List<Juridico> buscarChequesDeJuridico();

}
