package com.luxsoft.siipap.inventarios2.dao;

import java.util.List;

import com.luxsoft.siipap.inventarios2.domain.Concepto;

/**
 * DAO para Concepto
 * 
 * @author Ruben Cancino
 *
 */
public interface ConceptoDao {
	
	public void salvar(Concepto c);
	
	public void delete(Concepto c);
	
	public Concepto buscarById(Long id);
	
	public List<Concepto> buscarManuales();

}
