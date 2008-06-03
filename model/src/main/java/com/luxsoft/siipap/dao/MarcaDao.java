package com.luxsoft.siipap.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Marca;



public interface MarcaDao {
	
	public void salvar(Marca n);
	
	public void eliminar(Marca m);
	
	public void actualizar(Marca m);
	
	public Marca buscar(Long id);
	
	public Marca buscarPorNombre(String nombre);
	
	public List<Marca> buscarTodas();

}
