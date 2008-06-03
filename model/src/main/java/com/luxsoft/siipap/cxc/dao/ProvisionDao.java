package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.ventas.domain.Venta;

public interface ProvisionDao {
	
	public void salvar(Provision p);
	
	public void actualizar(Provision p);
	
	public void eliminar(Provision p);	
	
	public Provision buscarPorId(Long id);
	
	public Provision buscar(Venta v);
	
	public List<Provision> buscarProvisiones();

}
