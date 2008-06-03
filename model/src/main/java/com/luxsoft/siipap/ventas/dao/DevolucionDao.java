package com.luxsoft.siipap.ventas.dao;

import java.util.Collection;
import java.util.List;

import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public interface DevolucionDao {
	
	public void salvar(Devolucion dev);
	
	public void eliminar(Devolucion dev);
	 
	
	public Devolucion buscarPorId(Long id);
	
	public void salvarDevoluciones(final Collection<Devolucion> devs);
	
	
	public Devolucion buscar(long numero,int sucursal);
	
	/**
	 * Obtiene una lista de detalle de devoluciones correctamente inicializadas
	 * y que no esten aplicadas, para un cliente en especifico
	 *  
	 * @param cliente
	 * @return
	 */
	public List<DevolucionDet> buscarDevolucionesSinAplicar(String cliente);
	
	/**
	 * 
	 * @param v
	 * @return
	 */
	public List<DevolucionDet> buscarDevoluciones(final Venta v);

}
