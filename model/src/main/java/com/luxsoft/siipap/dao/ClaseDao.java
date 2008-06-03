package com.luxsoft.siipap.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Clase;

public interface ClaseDao {
	
	public void salvar(Clase c);
	
	public void eliminar(Clase c);
	
	public void actualizar(Clase c);
	
	public Clase buscarPorNombre(String nombre);
	
	public Clase buscar(Long id);
	
	public List<Clase> buscarTodas();

}
