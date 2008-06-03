package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.DepositoUnitario;

public interface DepositosManager {
	
	
	public List<Deposito> generaDepositosCheque(final List<DepositoUnitario> depositos);
	
	public void salvarDepositos(final List<Deposito> depositos,final Date fecha);
	
	

}
