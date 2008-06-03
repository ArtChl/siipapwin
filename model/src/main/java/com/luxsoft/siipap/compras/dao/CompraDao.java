package com.luxsoft.siipap.compras.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.luxsoft.siipap.compras.domain.Compra;
import com.luxsoft.siipap.compras.domain.CompraUnitaria;
import com.luxsoft.siipap.domain.Articulo;

public interface CompraDao {
	
	public void salvar(Compra c);
	
	public void eliminar(Compra c);
	
	public void actualizar(Compra c);
	
	public Compra getObject(Long id);
	
	public List<Compra> buscarPorFecha(final Date fecha);
	
	public Compra buscarCompra(final Integer sucursal,final Integer numero);
	
	public Set<CompraUnitaria> buscarPendientes(final Integer sucursal,final Articulo articulo);

}
