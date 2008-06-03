package com.luxsoft.siipap.cxp.dao;

import java.util.Currency;
import java.util.List;

import com.luxsoft.siipap.cxp.domain.Analisis;
import com.luxsoft.siipap.cxp.domain.CXP;
import com.luxsoft.siipap.cxp.domain.CXPFactura;
import com.luxsoft.siipap.cxp.domain.CXPNCargo;
import com.luxsoft.siipap.cxp.domain.CXPNCredito;
import com.luxsoft.siipap.cxp.domain.CXPPago;
import com.luxsoft.siipap.cxp.domain.RequisicionDetalle;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;

public interface CXPDao {
	
	
	
	public void salvarCXP(CXP cxp);
	
	public void eliminarCXP(CXP cxp);
	
	public void actualizarCXP(CXP cxp);
	
	public CXP buscarCXP(Long id);
	
	public List<CXP> buscarMovimientos(final String proveedor,final String referencia);
	
	public List<CXPFactura> buscarFacturas(final Proveedor proveedor);
	
	public List<CXPFactura> buscarFacturasSinRequisitar(final Proveedor proveedor);
	
	public List<CXPFactura> buscarFacturasSinRequisitar(final Proveedor proveedor,Currency moneda);
	
	/**
	 * Busca todas las facturas con saldo pendiente
	 * @return
	 */
	public List<CXPFactura> buscarFacturasPendietnes();
	
	public List<CXPFactura> buscarFacturasPendietnes(Proveedor p);
	
	public List<CXPPago> buscarPagos();
	
	public List<CXPPago> buscarPagos(Proveedor proveedor);
	
	
	public List<CXPNCargo> buscarCargos(Proveedor proveedor);
	
	public List<CXPNCredito> buscarAbonos(Proveedor proveedor);
	
	/**
	 * Valida que una nota de credito para un proveedor no sea aplicada 
	 * mas de una vez a la misma factura
	 * 
	 * @param proveedor
	 * @param nota
	 */
	public void validarNotaDeCredito(String factura,String proveedor,String folio);
	
	/**
	 * Recalcula el saldo de las facturas para un proveedor en especial
	 * @param proveedor
	 */
	//public void recalcularSaldos(String proveedor);
	
	//public List buscarNotasDeCreditoAplicadasPorRequisicion();
	
	//public List buscarRequisicionesConNCAplicada();
	
	public CXPNCredito buscarNotaDeCreditoPorRequisicion(final RequisicionDetalle r);
	
	/**
	 * Busca el monto de los posibles abonos aplicados a una Factura
	 * @param fac
	 * @return
	 */
	public CantidadMonetaria buscarMontoDeAbonos(CXPFactura fac);
	
	public Analisis buscarAnalisis(final CXPFactura cargo);

}
