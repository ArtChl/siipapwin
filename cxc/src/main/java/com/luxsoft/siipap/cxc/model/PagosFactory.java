package com.luxsoft.siipap.cxc.model;

import java.math.BigDecimal;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.ventas.domain.Venta;

public interface PagosFactory {
	
	public static final BigDecimal TOLERANCIA_AUTOMATICA=BigDecimal.valueOf(100);
	
	
	
	/**
	 * Genera un PagoM para ventas con saldo menor o igual a 100 pesos
	 * 
	 * @param ventas
	 * @return
	 */
	public PagoM crearPagoAutomatico(final List<Venta> ventas);
	
	/**
	 * Genera un PagoM para cargos con saldo menor o igual a 5 pesos
	 * @param cargos
	 * @return
	 */
	public PagoM crearPagoAutomaticoParaNotaDeCargo(final List<NotaDeCredito> cargos);
	
	/**
	 * Pago para diferiencia cambiaria
	 * 
	 * @param ventas
	 * @return
	 */
	public PagoM crearPagoParaDiferienciaCambiaria(final List<Venta> ventas);
	
	/**
	 * Pago para diferencia cambiaria
	 * 
	 * @param pago
	 * @return
	 */
	public PagoConOtros crearPagoParaDiferienciaCambiaria(final PagoM pago);
	
	/**
	 * Genera un {@link PagoConOtros} para un grupo de ventas
	 * 
	 * @param origen
	 * @param ventas
	 * @return
	 */
	public PagoConOtros crearPago(final PagoM origen,final List<Venta> ventas);
	
	/**
	 * 
	 * @param origen
	 * @param ventas
	 * @return
	 */
	public PagoConOtros crearPagoDeCargo(final PagoM origen,final List<NotaDeCredito> cargos);
	
	/**
	 * Genera un {@link PagoConNota} para un grupo de ventas
	 * 
	 * @param nota
	 * @param ventas
	 * @return
	 */
	public PagoConNota crearPagoConNota(final NotaDeCredito nota,final List<Venta> ventas );
	
	public PagoConNota crearPagoDeCargoConNota(NotaDeCredito disponible, List<NotaDeCredito> cargos);
	
	
	/**
	 * Genera un pago para el grupo de cargos indicado
	 * 
	 * @param c
	 * @param cargos
	 * @return
	 */
	public PagoM crearPago(final Cliente c,final List<NotaDeCredito> cargos);

}
