package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;



public interface DescuentoPorClienteDao {
	
	public void salvar(DescuentoPorCliente d);
	
	public void eliminar(DescuentoPorCliente d);
	
	public void actualisar(DescuentoPorCliente d);
	
	public DescuentoPorCliente buscarPorId(Long id);
	
	public void eliminarDescuentosOrigenSiipap();
	
	public DescuentoPorCliente buscar(final Cliente c);
	
	public List<DescuentoPorCliente> buscarDescuentos();

}
