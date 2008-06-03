package com.luxsoft.siipap.cxc.dao;


import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.domain.Articulo;


public interface DescuentoPorArticuloDao {
	
public void salvar(DescuentoPorArticulo d);
	
	public void eliminar(DescuentoPorArticulo d);
	
	public void actualisar(DescuentoPorArticulo d);
	
	public DescuentoPorArticulo buscarPorId(Long id);
	
	public DescuentoPorArticulo buscar(final Cliente c,final  Articulo articulo);
	
	public List<DescuentoPorArticulo> buscar(final String claveCliente);

}
