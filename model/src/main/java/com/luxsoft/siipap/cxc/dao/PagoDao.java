package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;

public interface PagoDao {
	
	public void salvar(Pago p);
	
	public void eliminar(Pago p);
	
	public Pago buscar(Long id);
	
	public void eliminarPagos(Periodo p,String origen);
	
	public List<Pago> buscarPagos(final Venta v);
	
	public List<Pago> buscarPagosConNota(final NotaDeCredito nota);
	
	/**
	 * Busca los pagos acumulados del  año para el cliente
	 * @param c
	 * @return
	 */
	public List<Pago> buscarPagosYtd(final Cliente c);

}
