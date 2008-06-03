package com.luxsoft.siipap.cxc.managers;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Pago;

public interface ChequesDevManager {
	
	public ChequeDevuelto salvar(final ChequeDevuelto ch);
	
	public void cancelar(final ChequeDevuelto ch);
	
	public ChequeDevuelto refresh(final ChequeDevuelto ch);
	
	public List<Pago> buscarPagosDeCheques();

}
