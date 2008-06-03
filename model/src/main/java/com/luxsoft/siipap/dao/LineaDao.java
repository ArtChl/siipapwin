package com.luxsoft.siipap.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Linea;



public interface LineaDao {
	
	public void salvar(Linea l);
	
	public void eliminar(Linea l);
	
	public void actualizar(Linea l);
	
	public Linea buscar(Long id);
	
	public Linea buscarPorNombre(String nombre);
	
	public List<Linea> buscarTodas();

}
