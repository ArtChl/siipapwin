package com.luxsoft.siipap.cxc.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;

public interface NotaDeCreditoDao {
	
	public void salvar(NotaDeCredito nota);
	
	public void actualizar(final NotaDeCredito nota);
	
	public NotaDeCredito buscar(Long id);
	
	public NotaDeCredito buscarDevolucion(Long id);
	
	public void eliminar(NotaDeCredito nota);
	
	public void salvar(Collection<NotaDeCredito> notas);
	
	/**
	 * 
	 * @param p
	 * @return
	 * @deprecated replace with buscarNotas
	 */
	public List<NotaDeCredito> buscar(Periodo p);
	
	/**
	 * Busca todas las notas de credito
	 * 
	 * @param p
	 * @return
	 */
	public List<NotaDeCredito> buscarNotas(Periodo p);
	
	public List<NotaDeCredito> buscarNotasCre(Periodo p);
	
	public void salvarDetalles(Collection<NotasDeCreditoDet> notas);
	
	public NotaDeCredito buscarNota(long numero,String tipo,String serie);
	
	public NotaDeCredito buscarNotaSinNumero(int grupo,String tipo,String serie);
	
	public NotaDeCredito buscarNotaConDetalle(long numero,String tipo,String serie);
	
	public NotaDeCredito buscarNota(long numero,String tipo);
	
	public List<NotasDeCreditoDet> buscarNotasDet(Venta v);
	
	public void salvar(NotasDeCreditoDet det);
	
	/**
	 * Busca notas de credito con saldo, es decir que se pueden ocupar para
	 * realizar pagos, para un cliente
	 * 
	 * @param clave
	 * @return
	 */
	public List<NotaDeCredito> buscarNotasConSaldo(String clave);
	
	/**
	 * Busca una nota de credito en particular que tenga saldo y que se pueda ocupar
	 * para pagar facturas
	 * 
	 * @param numero
	 * @param clave
	 * @return
	 */
	public NotaDeCredito buscarNotaConSaldo(long numero,String clave);
	
	//public void reatach(NotaDeCredito n);
	public List<NotaDeCredito> buscarNotasPorChequeDevueltoYTD(Cliente c);
	
	public List<NotaDeCredito> buscarNotasPorDevolucionYTD(Cliente c);
	
	

}
