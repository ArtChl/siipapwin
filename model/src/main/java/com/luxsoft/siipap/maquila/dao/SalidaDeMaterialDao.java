package com.luxsoft.siipap.maquila.dao;

import java.util.List;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;


public interface SalidaDeMaterialDao {
	
	public void salvar(SalidaDeMaterial s);
	
	public void eliminar(SalidaDeMaterial s);
	
	public void actualizar(SalidaDeMaterial s);
	
	public SalidaDeMaterial get(Long id);
	
	public List<AnalisisDeEntrada> buscarEntradasPorAsignar();
	
	public List<AnalisisDeEntrada> buscarEntradasPorAsignar(String articulo);
	
	public List<SalidaDeBobinas> buscarSalidasDeBobinas();
	
	
	
	public List<AnalisisDeEntrada> buscarDisponiblesKilos(final String proveedor);
	
	public List<AnalisisDeEntrada> buscarDisponiblesKilos(final String proveedor,final String articulo);
	
	/**
	 * Regresa todas las salidas a corte existentes
	 * 
	 * @return
	 */
	public List<SalidaACorte> buscarSalidas();
	
	
	/**
	 * Busca todas las salidas de material existentes en la base de datos
	 * 
	 * @return
	 */
	public List<SalidaDeMaterial> buscarSalidasDeMaterial();
	

}
