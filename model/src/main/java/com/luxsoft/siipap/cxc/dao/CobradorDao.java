package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cobrador;

public interface CobradorDao {
	
	public void salvar(final Cobrador c);
	
	public void eliminar(final Cobrador c);
	
	public List<Cobrador> buscar();
	
	public void actualizar(final Cobrador c);
	
	public Cobrador buscar(final Long id);

}
