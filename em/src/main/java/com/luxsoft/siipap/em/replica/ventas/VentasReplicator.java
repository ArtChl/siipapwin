package com.luxsoft.siipap.em.replica.ventas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

@SuppressWarnings("unchecked")
//@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
public class VentasReplicator extends AbstractReplicatorSupport  {
		
	private VentasDetReplicator detReplicator;
	private ArticuloDao articuloDao;
	private ClienteDao clienteDao;
	private ClientesReplicator clientesReplicator; 
	

	public void exportar(Object bean) {
		// TODO Auto-generated method stub
		
	}

	//@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List importar(Periodo periodo) {
		ReplicationUtils.validarMismoMes(periodo);
		List<Venta> ventas=new ArrayList<Venta>();
		try {
			logger.info("Importando mes "+periodo);				
			//Cargando mocomos
			
			List<Venta> mocomos=cargarMocomos(periodo);				
			//Cargando mococas
			List<Venta> mococas=cargarMococas(periodo);	
			//Cargando movcres
			List<Venta> movcres=cargarMovcres(periodo);
			
			ventas.addAll(mocomos);
			ventas.addAll(mococas);
			ventas.addAll(movcres);
			Collections.sort(ventas, new Comparator<Venta>(){
				public int compare(Venta o1, Venta o2) {
					return o1.getSucursal().compareTo(o2.getSucursal());
				}				
			});
			injectYearMonth(periodo, ventas);
			cargarVentasDet(ventas,periodo);
			vincularDependencias(ventas);
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		for(Venta v:ventas){
			persistir(v);
		}
		return ventas;
	}
	
	//@Transactional(propagation=Propagation.SUPPORTS,readOnly=false)
	protected void cargarVentasDet(final List<Venta> ventas,final Periodo p){
		
		final List<VentaDet> detalles=getDetReplicator().generar(p);		
		CollectionUtils.forAllDo(ventas, new Closure(){

			public void execute(Object input) {
				final Venta venta=(Venta)input;	
				try {
					
					//logger.debug("Localizando partidas para venta: "+venta.toString());					
					
					Collection<VentaDet> partidas=CollectionUtils.select(detalles, new PartidasFinder(venta));
					injectYearMonth(p, partidas);
					
					for(VentaDet partida:partidas){
						venta.agregarDetalle(partida);
					}
					//logger.debug("Partidas localizadas y asignadas: "+venta.getPartidas().size());
					
				} catch (Exception e) {
					logger.error(e);
				}
				
				
			}
			
		});
		logger.info("Partidas de ventas cargadas ...");
	}	
	
	protected void vincularDependencias(final List<Venta> ventas){
		for(Venta v:ventas){
			v.setCliente(getClienteDao().buscarPorClave(v.getClave()));
			for(VentaDet det:v.getPartidas()){
				det.setArticulo(getArticuloDao().buscarPorClave(det.getClave().trim()));
			}
		}
		logger.info("Ventas vinculadas.....");
	}
	
	//@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void persistir(Object bean){
		Venta v=(Venta)bean;
		Venta v2=getVentaDao().buscarVenta(v.getSucursal(),v.getSerie(),v.getTipo(), v.getNumero());
		if(v2!=null){
			if(logger.isDebugEnabled()){
				logger.debug("La venta ya existe, procedemos a eliminarla ");
			}			
			try {
				getVentaDao().eliminar(v2);
				getVentaDao().salvar(v);
			} catch (Exception e) {
				logger.error("Error al eliminar una venta ya existente, es posible que tenga pagos,devoluciones o notas de credito relacionadas",e);
				return;
			}
		}else			
			try {
				if(v.getCliente()==null){
					System.out.println("Err: Venta sin cliente clave:"+v.getClave()+" V: "+v.toString());
					//Cliente c=getClientesReplicator().importar(v.getClave());
					//v.setCliente(c);
				}
				getVentaDao().salvar(v);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}			
	}

	/**
	 * Valida que no existan registros en el periodo seleccionado para poder generar una carga bulk nuevaç
	 * 
	 * @param p
	 * @deprecated
	 */
	public void validarBulkImport(Periodo p){
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			int beans=contarBeans(mes, "");
			Assert.isTrue(0==beans,"Existen registros en el periodo seleccionado deben ser borrados antes de proseguir mes: "+mes);
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.luxsoft.siipap.em.replica.ventas.Importador#bulkImport(com.luxsoft.siipap.domain.Periodo)
	 * @deprecated
	 */
	public void bulkImport(Periodo p){
		validarBulkImport(p);
		
		// Dividimos por meses
		
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			try {
				logger.info("Importando mes "+mes);
				
				//Cargando mocomos
				
				List<Venta> mocomos=cargarMocomos(mes);				
				injectYearMonth(mes, mocomos);				
				salvar(mocomos);
				mocomos.clear();mocomos=null;
				
				//Cargando mococas
				List<Venta> mococas=cargarMococas(mes);				
				injectYearMonth(mes, mococas);				
				salvar(mococas);
				mococas.clear();mococas=null;
				
				
				//Cargando movcres
				List<Venta> movcres=cargarMovcres(mes);
				injectYearMonth(mes, movcres);				
				salvar(movcres);
				movcres.clear();movcres=null;
				
				validar(mes);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
				
		}		
	}
	
	
	@Override
	//@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void salvar(Object... beans) {
		for(Object bean:beans){
			try {
				getDao().getHibernateTemplate().saveOrUpdate(bean);
			} catch (Exception e) {				
				logger.error("No pudo importar bean: "+bean,e);
			}
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.luxsoft.siipap.em.replica.ventas.Importador#validar(com.luxsoft.siipap.domain.Periodo)
	 */
	public List<ReplicaLog> validar(final Periodo periodo){
		
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();
		Iterator<Date> dias=periodo.getDiasIterator();
		while(dias.hasNext()){
			try {
				Periodo pp=new Periodo(dias.next());
				//Validando MOCOMO
				String rowsSQL=ReplicationUtils.resolveSQLCount(pp,"MOCOMO","MCMFECHA")+" AND MCMIDENOPE=1";		
				int rows=getFactory().getJdbcTemplate(pp).queryForInt(rowsSQL);
				int beans=contarBeans(pp, "MOS");			
				list.add(registrar("Venta","MOCOMO","FAC",pp,beans,rows));
				
				//Validando MOCOCA
				rowsSQL=ReplicationUtils.resolveSQLCount(pp,"MOCOCA","MCAFECHA")+" AND MCAIDENOPE=1";		
				rows=getFactory().getJdbcTemplate(pp).queryForInt(rowsSQL);
				beans=contarBeans(pp,"CAM");			
				list.add(registrar("Venta","MOCOCA","FAC",pp,beans,rows));
				
				//Validando MOVCRE
				rowsSQL=ReplicationUtils.resolveSQLCount(pp,"MOVCRE","MCRFECHA")+" AND MCRIDENOPE=1";		
				rows=getFactory().getJdbcTemplate(pp).queryForInt(rowsSQL);
				beans=contarBeans(pp,"CRE");			
				list.add(registrar("Venta","MOVCRE","FAC",pp,beans,rows));
				
				// Validamos ALMACE
				rowsSQL=ReplicationUtils.resolveSQLCount(pp,"ALMACE","ALMFECHA")+" AND ALMTIPO=\'FAC\'";
				
				rows=getFactory().getJdbcTemplate(pp).queryForInt(rowsSQL);
				beans=contarBeansPartidas(pp,"ALMACE");			
				list.add(registrar("VentaDet","ALMACE","FAC",pp,beans,rows));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
		return list;
	}
	
	protected int contarBeans(Periodo p,Object... params){		
		String hql="select count(*) from Venta v where v.fecha between ? and ? and v.origen=? ";
		List<Long> l=getDao().getHibernateTemplate().find(hql, new Object[]{p.getFechaInicial(),p.getFechaFinal(),params[0]});
		return l.get(0).intValue();		
	}
	
	protected int contarBeansPartidas(Periodo p,Object... params){
		String hql="select count(*) from VentaDet v where v.venta.fecha between ? and ? ";
		List<Long> l=getDao().getHibernateTemplate().find(hql, new Object[]{p.getFechaInicial(),p.getFechaFinal()});
		return l.get(0).intValue();	
	}
		
	protected List<Venta> cargarMocomos(Periodo p){		
		String sql=ReplicationUtils.resolveSQL(p,"MOCOMO","MCMFECHA");	
		sql=sql+" AND MCMIDENOPE=1";
		List<Venta> rows=getFactory().getJdbcTemplate(p).query(sql,new MocomoMapper());
		logger.info("Ventas obtenidas  de Mocomo: "+rows.size());
		return rows;
	}
	
	
	@SuppressWarnings("unchecked")
	protected List<Venta> cargarMococas(Periodo periodo){
		String sql=ReplicationUtils.resolveSQL(periodo,"MOCOCA","MCAFECHA");
		sql=sql+" AND MCAIDENOPE=1";
		List<Venta> rows=factory.getJdbcTemplate(periodo).query(sql,new MococaMapper());
		logger.info("Ventas obtenidas  de Mococa: "+rows.size());
		return rows;
	}
	
	@SuppressWarnings("unchecked")
	protected List<Venta> cargarMovcres(Periodo periodo){
		String sql=ReplicationUtils.resolveSQL(periodo,"MOVCRE","MCRFECHA");
		sql=sql+" AND MCRIDENOPE=1";
		List<Venta> rows=factory.getJdbcTemplate(periodo).query(sql,new MovcreMapper());
		logger.info("Ventas obtenidas  de Movcre: "+rows.size());
		return rows;
	}
	
	
	
	protected String getHqlParaContarBeans(){
		String hql="select count(*) from @entity e where e.mes=? and e.year=? and e.origen=?";
		return hql;
	}

	
	public VentasDetReplicator getDetReplicator() {
		return detReplicator;
	}

	public void setDetReplicator(VentasDetReplicator detReplicator) {
		this.detReplicator = detReplicator;
	}
	

	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}

	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	public VentasDao getVentaDao(){
		return (VentasDao)getDao();	
	}
	
	
	
	private class PartidasFinder implements Predicate{
		
		private final Venta venta;
		
		public PartidasFinder(final Venta v){
			venta=v;
		}
		
		public boolean evaluate(Object object) {
			VentaDet partida=(VentaDet)object;
			if(venta.getSucursal().equals(partida.getSucursal())){
				if(venta.getNumero().equals(partida.getNumero())){
					if(venta.getTipo().equals(partida.getTipoFactura())){
						if(venta.getSerie().equals(partida.getSerie())){							
							return true;
						}
					}
				}
			}
			return false;
		}
		
	}
	
	
	public static void main(String[] args) {
		Replicador r=ServiceManager.instance().getReplicador(Replicadores.VentasReplicator);
		//r.validar(Periodo.getPeriodoEnUnMes(0,2007));
		r.importar(new Periodo("01/02/2007","01/02/2007"));
	}

	public ClientesReplicator getClientesReplicator() {
		return clientesReplicator;
	}

	public void setClientesReplicator(ClientesReplicator clientesReplicator) {
		this.clientesReplicator = clientesReplicator;
	}

}
