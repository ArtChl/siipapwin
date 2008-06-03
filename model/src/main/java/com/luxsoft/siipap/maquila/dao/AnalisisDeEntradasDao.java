package com.luxsoft.siipap.maquila.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.maquila.domain.AnalisisDeEntradas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;

public interface AnalisisDeEntradasDao {
	
	public void salvar(AnalisisDeEntradas analisis);
	
	public void eliminar(AnalisisDeEntradas analisis);
	
	public AnalisisDeEntradas getFromId(Long id);
	
	public void actualizar(AnalisisDeEntradas analisis);
	
	public AnalisisDeEntradas buscarPorFactura(final String factura,final Proveedor p);
	
	public List<AnalisisDeEntradas> buscarTodos();
	
	public List<EntradaDeMaterial> buscarEntradasPorFactura(String factura, Proveedor p);
	
	public List<EntradaDeMaterial> buscarEntradas();
	
	/**
	 * Busca entradas pendientes de analisis
	 * @return
	 */
	public List<EntradaDeMaterial> buscarEntradasPendientes();
	
	/**
	 * Inicializa un analisis particularmente inicializa su coleccion
	 * de EntradaDeMaterial
	 * @param a
	 */
	public void inicializarAnalisis(AnalisisDeEntradas a);

}
