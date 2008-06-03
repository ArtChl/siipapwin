package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;

public interface DescuentoPorVolumenDao {
	
	public void salvar(DescuentoPorVolumen d);
	
	public void eliminar(DescuentoPorVolumen d);
	
	public void actualisar(DescuentoPorVolumen d);
	
	public DescuentoPorVolumen buscarPorId(Long id);
	
	public void eliminarDescuentos(int year, int mes);
	
	/**
	 * Descuento por volumen  
	 * 
	 * @param importe
	 * @return
	 */
	public DescuentoPorVolumen buscar(double importe);
	
	/**
	 * Calcula el descuento para un cliente de cheque post fechado 
	 * para el importe determinado
	 * 
	 * @param ventaId
	 * @return
	 */
	public double calcularDescuentoChequeP(final Long ventaId);
	
	
	/**
	 * Localiza un descuento por volumen especifico para el cliente
	 * indicado. Normalmente es un descuento adicional
	 * 
	 * @param c
	 * @return
	 */
	public DescuentoPorVolumen buscar(Cliente c);
	
	/**
	 * Busca todos los descuentos por volumen
	 * @return
	 */
	public List<DescuentoPorVolumen> buscar();
	
	
	

}
