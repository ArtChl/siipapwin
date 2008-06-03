package com.luxsoft.siipap.cxc.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.PagoDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Facade para el acceso centralizado a diversas operaciones y procesos de uso comun
 * en el modulo de CXC
 * 
 * TODO Esta clase requiere de una depuracion y refactorizacion ya que por un lado tiene metodos
 * que ya no son necesarios y por otro lado si le agregamos algunos mas se puede usar como facade
 * en varias opciones que actualmente hacen referencia a varios beans
 * -----URGENTE-----
 * 
 * @author Ruben Cancino
 *
 */
public class CXCManager {
	
	private ClienteDao clienteDao;
	private VentasDao ventasDao;
	private NotaDeCreditoDao notaDeCreditoDao;
	private PagoDao pagoDao;
	
	private VentasManager ventasManager;
	private NotasManager notasManager;
	
	//******************** METODOS REVISADOS EN LA REFACTORIZACION  *********************************//
	
	
	/**
	 * Busca las ventas de un cliente
	 * @param c
	 * @return
	 */
	public List<Venta> buscarVentasCredito(Cliente c){
		return getVentasDao().buscarVentasCreditoPorCliente(c.getClave());
	}
	
	/**
	 * Busca las ventas de un cliente para el periodo indicado
	 * 
	 * @param c
	 * @return
	 */
	public List<Venta> buscarVentasCredito(final Cliente c,final Periodo p){
		return getVentasDao().buscarVentasCreditoPorCliente(c.getClave(),p);
	}
	
	/**
	 * Refresca el estado de la venta accediendo a la base de datos
	 *  
	 * @param v
	 * @return
	 */
	public Venta refrescarVenta(final Venta v){
		getVentasManager().refresh(v);
		return v;
	}
	
	/**
	 * Busca las notas de credito de descuento pendientes de impresion. 
	 * En conbranza o anticipadas
	 * 
	 * @param p
	 * @return
	 */
	public List<NotaDeCredito> buscarNotasDeDescuentoPorImprimir(final Periodo p,boolean anticipadas){
		return getNotasManager().buscarNotasParaImpresionPorDescuentos(p, anticipadas);
	}
	
	//******************** FIN DE METODOS REVISADOS EN LA REFACTORIZACION  *********************************//
	
	public void actualizarCliente(final ClienteCredito c){
		c.setModificado(new Date());
		getClienteDao().salvar(c.getCliente());
	}
	
	public List<Venta> buscarVentasConSaldo(Cliente c){
		return getVentasDao().buscarVentasConSaldo(c.getClave());
	}	
	
	
	public List<Cliente> buscarClientes(String clave){
		return getClienteDao().buscarClientesPorClave(clave);
	}
	
	public List<Cliente> buscarClientesPorNombre(String nombre){
		return getClienteDao().buscarPorNombre(nombre);
	}
	
	public Cliente buscarCliente(String clave){
		return getClienteDao().buscarPorClave(clave);
	}
	
	public List<ClienteCredito> buscarClientesACredito(){
		return getClienteDao().buscarClientesDeCredito();
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void aplicarPagoPorVenta(Pago... pagos){		
		List<Pago> pp=new ArrayList<Pago>();
		for(Pago p:pagos){
			pp.add(p);			
		}
		aplicarPagoPorVenta(pp);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void aplicarPagoPorVenta(List<Pago> pagos){
		// Salvar cada uno de lo
		for(Pago p:pagos){
			if(p.getImporte()==null ||p.getImporte().abs().amount().doubleValue()==0){
				continue;
			}			
			Venta v=p.getVenta();
			p.setClave(v.getClave());
			p.setCliente(v.getCliente());
			p.setSucursal(v.getSucursal());
			p.setTipoDocto(v.getTipo());
			p.setNumero(v.getNumero());
			p.setOrigen(v.getOrigen());
			p.setFormaDePago(p.getFormaDePago2().getId());
			p.setDescFormaDePago(p.getFormaDePago2().getDesc());
			p.setImporte(p.getImporte().abs());
			v.agregarPago(p);
			v.recalcularSaldo();
			System.out.println("Pago por salvar: "+p);
			getPagoDao().salvar(p);
			System.out.println("Id "+p.getId());
			
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void aplicarPagosConDescuentos(final List<Pago> pagos,final List<NotaDeCredito> notas){
		aplicarPagoPorVenta(pagos);
		getNotasManager().salvarNotasCre(notas);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Pago> buscarPagos(final Venta v){
		return getPagoDao().buscarPagos(v);
	}
	
	
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<NotasDeCreditoDet> buscarNotas(final Venta v){
		return getNotaDeCreditoDao().buscarNotasDet(v);
	}
	
	public List<NotasDeCreditoDet> buscarNotas(final Cliente c,final Periodo p){
		return getNotasManager().buscarNotasDet(c, p);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void eliminar(final Pago p){
		getPagoDao().eliminar(p);
	}
	
	
	
	/** COLABORADORES TODO REUIERE REVISION  *************/

	public ClienteDao getClienteDao() {
		return clienteDao;
	}
	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	public VentasDao getVentasDao() {
		return ventasDao;
	}
	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}


	public NotaDeCreditoDao getNotaDeCreditoDao() {
		return notaDeCreditoDao;
	}
	public void setNotaDeCreditoDao(NotaDeCreditoDao notaDeCreditoDao) {
		this.notaDeCreditoDao = notaDeCreditoDao;
	}


	public PagoDao getPagoDao() {
		return pagoDao;
	}
	public void setPagoDao(PagoDao pagoDao) {
		this.pagoDao = pagoDao;
	}

	public NotasManager getNotasManager() {
		return notasManager;
	}
	public void setNotasManager(NotasManager notasManager) {
		this.notasManager = notasManager;
	}

	public VentasManager getVentasManager() {
		return ventasManager;
	}
	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}
	
	

}
