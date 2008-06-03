package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.Juridico;
import com.luxsoft.siipap.ventas.domain.Venta;

public interface JuridicoDao {
	
	public void salvar(Juridico j);
	
	public void eliminar(Juridico j);
	
	public Juridico buscar(Long id);
	
	public List<Juridico> buscar();
	
	public Juridico buscar(Venta v);

}
