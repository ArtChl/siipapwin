package com.luxsoft.siipap.maquila.dao;

import java.util.List;

import com.luxsoft.siipap.maquila.domain.Almacen;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.RecepcionDeMaterial;

public interface RecepcionDeMaterialDao {
	
	public void create(RecepcionDeMaterial rec);
	
	public RecepcionDeMaterial get(Long id);
	
	public void update(RecepcionDeMaterial oc);
	
	public void delete(RecepcionDeMaterial oc);
	
	
	public List<RecepcionDeMaterial> buscarRecepciones();
	
	public List<EntradaDeMaterial> buscarEntradasDisponibles();
	
	public List<EntradaDeMaterial> buscarEntradasDisponibles(final String articulo);
	
	public List<EntradaDeMaterial> buscarEntradasDisponibles(final Almacen almacen);
	
	public void initEntradas(RecepcionDeMaterial r);
	
	public void eliminarRecepcion(RecepcionDeMaterial r);
	
	public List<EntradaDeMaterial> buscarEntradasPorTraslados(boolean disponible);
	
	public EntradaDeMaterial buscarEntrada(final Long id);
	
	public List<EntradaDeMaterial> buscarEntradas();
	
	
}
