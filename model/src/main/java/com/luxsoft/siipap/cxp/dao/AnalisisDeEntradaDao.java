package com.luxsoft.siipap.cxp.dao;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;

public interface AnalisisDeEntradaDao {
	
	public void salvar(final AnalisisDeEntrada com);
	
	public AnalisisDeEntrada buscarAnalisisDeEntrada(final Long id);
	
	public List<AnalisisDeEntrada> buscarEntradasPorCompra(final Periodo p);
	
	public List<AnalisisDeEntrada> buscarEntradasPorCompra(final Periodo p,final String clave);
	
	public List<AnalisisDeEntrada> buscarEntradasPorProveedor(final String clave,final Periodo p);
	
	public List<AnalisisDeEntrada> buscarDisponibles(final String proveedor);
	
	public List<AnalisisDeEntrada> buscarDisponibles(final String proveedor,final String articulo);
	
	public AnalisisDeEntrada buscarAnalisisPorEntrada(final Entrada e);
	
	public AnalisisDeEntrada localizarEntrada(final Integer sucursal,final Long numero,final String clave,final Integer renglon);
	
	public void salvarAnalisis(final List<AnalisisDeEntrada> analisis);
	
	public List<AnalisisDeEntrada> buscarEntradasPorAnalizar(String articulo,Date  hasta); 
	

}
