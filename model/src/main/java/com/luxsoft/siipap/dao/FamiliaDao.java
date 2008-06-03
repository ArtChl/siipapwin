/*
 * Created on 21-abr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Familia;

/**
 * 
 * @author Ruben Cancino 
 */
public interface FamiliaDao {
	
	public Familia crearFamilia(Familia familia);
	public Familia actualizarFamilia(Familia familia);
	public Familia buscarFamilia(Long id);
	public void eliminarFamilia(Familia familia);
	public Familia buscarFamilia(String clave);
	public List buscarTodasFamilias();
	
	public int contarFamilias();
	public void crearFamilias(List familias);
	
	public List<Familia> buscarRangoDeFamilias(Familia f1,Familia f2);
	

}
