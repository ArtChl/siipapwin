package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.DescuentoEspecial;
import com.luxsoft.siipap.ventas.domain.Venta;



public interface DescuentoEspecialDao {
	
	public void salvar(DescuentoEspecial d);
	
	public void eliminar(DescuentoEspecial d);
	
	public void actualisar(DescuentoEspecial d);
	
	public DescuentoEspecial buscarPorId(Long id);
	
	public DescuentoEspecial buscar(final Venta v);
	
	public List<DescuentoEspecial> buscar();
	

}
