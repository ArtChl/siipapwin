package com.luxsoft.siipap.cxc.services;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.Matcher;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.PagoDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

public class ActualizadorDeClientesImpl extends HibernateDaoSupport implements ActualizadorDeClientes{
	
	private VentasDao ventasDao;
	private PagoDao pagoDao;
	private NotaDeCreditoDao notaDeCreditoDao;
	private ClienteDao clienteDao;
	private JdbcTemplate template;
	
	/**
	 * Tarea para ser ejecutada por proceso programado una vez al dia
	 */
	@SuppressWarnings("unchecked")
	public void actualizarClientesCredito(){
		/*
		List<ClienteCredito> clientes=getHibernateTemplate().find("from ClienteCredito c order by c.cliente.clave ");		
		for(ClienteCredito c:clientes){
			actualizarCliente(c);
		}*/
	}
	
	
	@Transactional (propagation=Propagation.SUPPORTS, readOnly=false)
	public void actualizarCliente(final ClienteCredito credito){
		credito.updateProperties();
		getClienteDao().salvar(credito.getCliente());
		/*
		System.out.println("Actualizando: "+credito.getCliente().getClave());
		actualizarNotasCargoYDevolucion(credito);
		actualizarVentas(credito);
		actualizarNotasCargoYDevolucion(credito);
		getClienteDao().salvar(credito.getCliente());
		System.out.println("Id: "+credito.getVcontadoYtd());
		*/
	}
	
	private void actualizarVentas(final ClienteCredito credito){
		/*
		List<Venta> ventas=getVentasDao().buscarVentasYTD(credito.getCliente());
		
		System.out.println("Ventas : "+ventas.size());
		if(ventas.isEmpty()) 
			return;
		//validarVentas(ventas);
		final EventList<Venta> source=GlazedLists.eventList(ventas);		
		final SortedList<Venta> ytd=new SortedList<Venta>(source,GlazedLists.beanPropertyComparator(Venta.class, "fecha"));
		Matcher<Venta> matcher=new Matcher<Venta>(){
			public boolean matches(Venta item) {
				
				boolean conSaldo=item.getSaldo().doubleValue()>1;				
				if(conSaldo && item.getOrigen().equals("CRE")){
					if(item.getCredito()==null) 
						return false;
					boolean conAtraso=item.getCredito().getAtrasoOperativo()>0;
					return conAtraso;
				}else
					return item.getAtraso()>0;
				//return ((item.getSaldo().doubleValue()>1) );
			}
		};
		final EventList<Venta> vencidas=new FilterList<Venta>(ytd,matcher);
		//Actualizamoes 
		credito.setFacturas(ytd.size());
		credito.setFacturasVencidas(vencidas.size());
		credito.actualizarSaldo(ytd);
		credito.actualizarSaldoVencido(ytd);
		credito.actualizarYtd(ytd);
		credito.actualizarDescuentos(ytd);
		credito.setUltimaVenta(ytd.get(0).getFecha());
		*/
	}
	
	
	
	private void actualizarPagos(final ClienteCredito credito){
		/*
		List<Pago> pagos=getPagoDao().buscarPagosYtd(credito.getCliente());
		if(pagos.isEmpty()) return;		
		credito.actualizarPagos(pagos);
		*/		
	}
	
	private void actualizarNotasCargoYDevolucion(final ClienteCredito credito){
		/*
		final List<NotaDeCredito> devos=getNotaDeCreditoDao().buscarNotasPorDevolucionYTD(credito.getCliente());
		credito.actualizarDevoluciones(devos);
		final List<NotaDeCredito> cheques=getNotaDeCreditoDao().buscarNotasPorChequeDevueltoYTD(credito.getCliente());
		credito.setChequesDevueltos(cheques.size());
		*/
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void generarClientesCredito() {
		String sql="select distinct clave as CLAVE from SW_VENTAS where YEAR>2005 and ORIGEN=\'CRE\'";
		List<Map<String, String>> claves=ServiceLocator.getJdbcTemplate().queryForList(sql);
		for(Map<String,String> clave:claves){
			String c=clave.get("CLAVE");
			Cliente cliente=getClienteDao().buscarPorClave(c);
			if(cliente.getCredito()==null){
				ClienteCredito credito=new ClienteCredito(cliente);
				credito.copyFromCliente();
				getClienteDao().salvar(cliente);
				if(logger.isDebugEnabled()){
					logger.debug("Cliente credito generado: "+cliente.getClave());
				}
			}
		}		
	}

	public VentasDao getVentasDao() {
		return ventasDao;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}

	public PagoDao getPagoDao() {
		return pagoDao;
	}

	public void setPagoDao(PagoDao pagoDao) {
		this.pagoDao = pagoDao;
	}

	public NotaDeCreditoDao getNotaDeCreditoDao() {
		return notaDeCreditoDao;
	}

	public void setNotaDeCreditoDao(NotaDeCreditoDao notaDeCreditoDao) {
		this.notaDeCreditoDao = notaDeCreditoDao;
	}
	
	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	public JdbcTemplate getTemplate() {
		return template;
	}
	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	

	public static void main(String[] args) {
		//ServiceLocator.getActualizadorDeClientes().actualizarCliente("A100063");
		//ServiceLocator.getActualizadorDeClientes().actualizarClientesCredito();
		ServiceLocator.getActualizadorDeClientes().generarClientesCredito();
		
		
	}
	

}
