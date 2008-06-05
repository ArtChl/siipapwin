package com.luxsoft.siipap.em.replica.devo;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.DefaultMapper;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.ventas.dao.DevolucionDao;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

@SuppressWarnings("unchecked")
public class DevolucionReplicator extends AbstractReplicatorSupport{
	
	
	private DevolucionDetReplicator detReplicator;
	private VentasDao ventasDao;
	private ArticuloDao articuloDao;
	
	/**
	 * Importa los beans correspondientes al periodo indicado
	 * sin persistirlos a la base de datos
	 * 
	 * @param periodo
	 * @return
	 */
	public List importar(Periodo periodo){
		logger.info("Importando Devoluciones para : "+periodo);		
		List<Devolucion> devos=generar(periodo);
		injectYearMonth(periodo,devos);
		cargarDetalles(devos,periodo);
		vincularDependencias(devos);
		for(Devolucion d:devos){
			persistir(d);
		}		
		return devos;
	}
	
	private void cargarDetalles(final List<Devolucion> devos,final Periodo periodo){
		final List<DevolucionDet> detalles=getDetReplicator().generar(periodo);
		CollectionUtils.forAllDo(devos, new Closure(){

			public void execute(Object input) {
				Devolucion d=(Devolucion)input;
				Collection<DevolucionDet> partidas=CollectionUtils.select(detalles, new PartidasFinder(d));
				injectYearMonth(periodo, partidas);
				for(DevolucionDet det:partidas){
					d.addPartida(det);
				}
				//logger.debug("Partidas localizadas para la devolucion: "+d.getNumero()+" :"+d.getPartidas().size());
				//logger.debug("Partidas localizadas para la devolucion: "+d.getNumero()+" :"+partidas.size());
			}
			
		});
	}
	
	private void vincularDependencias(List<Devolucion> devos){
		for(Devolucion d:devos){
			Venta v=getVentasDao().buscarVenta(d.getSucursal(),d.getSerieVenta(), d.getTipoVenta(),d.getNumeroVenta());
			if(v==null){
				String msg="No se localizo la venta para la devolucion  de sucursal:{0} ventaSerie: {1}, ventaTipo: {2}, ventaNumero: {3}";
				logger.warn(MessageFormat.format(msg, d.getSucursal(),d.getSerieVenta(),d.getTipoVenta(),d.getNumeroVenta()));
			}
			d.setVenta(v);
			for(DevolucionDet det:d.getPartidas()){
				Venta venta=det.getDevolucion().getVenta();
				if(venta!=null){
					for(VentaDet vd:v.getPartidas()){
						String claveV=vd.getClave().trim();
						String claveD=det.getClave().trim();
						if(claveV.equals(claveD)){
							double cantidadV=Math.abs(vd.getCantidad());
							double cantidadD=det.getCantidad();
							if(cantidadV>=cantidadD){
								det.setVentaDet(vd);								
								break;
							}
						}
					}
				}
				det.setArticulo(getArticuloDao().buscarPorClave(det.getClave().trim()));
			}
			
		}
	}
	
	/**
	 * Persiste un bean importado a la base de datos
	 * 
	 * @param bean
	 */
	public void persistir(Object bean){
		try {
			Devolucion d=(Devolucion)bean;
			Devolucion d2=getDevolucionDao().buscar(d.getNumero(),d.getSucursal());
			if(d2!=null){
				//logger.debug("La devolucion existe.. borrandola: "+d2.getId());
				logger.debug("La devolucion ya existe: "+d2.getId());
				return;
				/**
				getDevolucionDao().eliminar(d2);
				getDevolucionDao().salvar(d);
				**/
			}else{
				getDevolucionDao().salvar(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al eliminar o actualizar la devolucion, psiblemente existen referencias vivas",e);
		}
	}
	
	
	/**
	 * Exporta un bean a otra fuente de datos
	 * 
	 * TODO: Implementar 
	 * @param bean
	 */
	public void exportar(Object bean){

	}
	
	
	protected List<Devolucion> generar(Periodo mes){
		ReplicationUtils.validarMismoMes(mes);
		String sql=ReplicationUtils.resolveSQL(mes,"MVALMA","MVAFECHA")+" AND MVATIPO=\'RMD\'";
		DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(getBeanClass());
		mapper.setOrigen("MVALMA");
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		List<Devolucion> rows=getFactory().getJdbcTemplate(mes).query(sql,mapper);
		logger.info("Devoluciones obtenidas  : "+rows.size());
		return rows;
	}
	
	
	public List<ReplicaLog> validar(Periodo periodo) {
		Iterator<Date> dias=periodo.getDiasIterator();
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();
		while(dias.hasNext()){
			Periodo pp=new Periodo(dias.next());
			try {
				// Validamos el maestro
				String rowsSQL=ReplicationUtils.resolveSQLCount(pp,"MVALMA","MVAFECHA")+" AND MVATIPO=\'RMD\'";		
				int rows=getFactory().getJdbcTemplate(pp).queryForInt(rowsSQL);
				int beans=contarBeans(pp, new Object[0]);			
				list.add(registrar("Devolucion","MVALMA","RMD",pp,beans,rows));
				
				//Validamos el detalle ALMACE
				rowsSQL=ReplicationUtils.resolveSQLCount(pp,"ALMACE","ALMFECHA")+" AND ALMTIPO=\'RMD\'";		
				rows=getFactory().getJdbcTemplate(pp).queryForInt(rowsSQL);
				beans=contarBeansPartidas(pp);			
				list.add(registrar("DevolucionDet","ALMACE","RMD",pp,beans,rows));
			} catch (Exception e) {
				logger.error(e);
			}
		}
				
		return list;
	}
	
	protected int contarBeansPartidas(Periodo p){
		List<Periodo> meses=Periodo.periodosMensuales(p);
		Assert.notNull(getBeanClass(),"No se ha definido el tipo del bean (La clasea)  del replicador");
		String tabla=ClassUtils.getShortName(DevolucionDet.class);
		
		int beans=0;
		for(Periodo mes:meses){
			String hql="select count(*) from @entity e where e.mes=? and e.year=? ";
			hql=hql.replaceAll("@entity", tabla);
			Object[] vals={getMes(mes),getYear(mes)};
			List<Long> l=getDao().getHibernateTemplate().find(hql, vals);			
			beans+=l.get(0);
		}
		return beans;		
	}
	

	public void bulkImport(Periodo p) {
		validarBulkImport(p);		
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			try {
				logger.info("Importando mes "+mes);
				List<Devolucion> detalle=generar(mes);
				injectYearMonth(mes,detalle);
				salvar(detalle);				
				validar(mes);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}				
		}	
	}

	public void parcheParaActualizarNotasDeCredito(){
			
			//final List<Devolucion> devos=new ArrayList<Devolucion>();
			
			//final List<DevolucionDet> detalles=new ArrayList<DevolucionDet>();
			Periodo p=new Periodo("13/02/2007","27/02/2007");
			
			//for(final Periodo mes:Periodo.getPeriodosDelYear(year)){				
			for(final Periodo mes:Periodo.periodosMensuales(p)){
				System.out.println("Procesando : "+mes);
				//detalles.addAll(getDetReplicator().generar(mes));
				final List<Devolucion> devs=generar(mes);
				injectYearMonth(mes,devs);
				cargarDetalles(devs,mes);
				vincularDependencias(devs);
				//devos.addAll(devs);
				
				System.out.println("Devoluciones localizadas: "+devs.size());
				
				getDao().getHibernateTemplate().execute(new HibernateCallback(){

					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						
						final List<Devolucion> devsP=session.createQuery("from Devolucion d where d.year=:year and d.mes=:mes")
						.setLong("year", getYear(mes))
						.setInteger("mes", getMes(mes))
						.list();
						System.out.println("Devoluciones persistidas: "+devsP.size());
						int count=0;
						for(final Devolucion det:devs){
							long num=det.getNumero();
							int suc=det.getSucursal();
							System.out.println("Localizando devolucion "+num+" "+suc);
							Devolucion dev=(Devolucion)session.createQuery("from Devolucion d where d.numero=:numero and d.sucursal=:sucursal")
							.setLong("numero", num)
							.setInteger("sucursal",suc)
							.uniqueResult();							
							if(dev==null){
								System.out.println("No encontre para Devo: "+det.getNumero());
								continue;
							}
							System.out.println("Actualizando devolucion:"+dev.getId());
							//dev.getPartidas().clear();
							for(DevolucionDet ddd:det.getPartidas()){
								dev.addPartida(ddd);
							}
							
							
							//	Localizamos la nota  && && && &&
							
							for(DevolucionDet dd:dev.getPartidas()){
								NotaDeCredito n=(NotaDeCredito)session.createQuery("from NotaDeCredito n where n.devolucion=:devo and n.numero=:numero")
									.setEntity("devo", dev)
									.setParameter("numero", dd.getCxcnumero())
									.uniqueResult();
								if(n!=null){
									System.out.println("\t Asignando nota: "+n.getId());
									dd.setNota(n);
								}
							}
							
							if(++count%20==0){
								session.flush();
								session.clear();
							}
							
						}
						return null;					
					}
					
				});
				
			}		
		
	}
	
	
	
	public DevolucionDetReplicator getDetReplicator() {
		return detReplicator;
	}

	public void setDetReplicator(DevolucionDetReplicator detReplicator) {
		this.detReplicator = detReplicator;
	}
	public VentasDao getVentasDao() {
		return ventasDao;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}
	public DevolucionDao getDevolucionDao(){
		return (DevolucionDao)getDao();
	}

	
	
	private class PartidasFinder implements Predicate{
		
		private final Devolucion devo;
		
		public PartidasFinder(final Devolucion d){
			this.devo=d;
		}

		public boolean evaluate(Object object) {
			DevolucionDet det=(DevolucionDet)object;
			if(devo.getSucursal()==det.getSucursal()){
				if(devo.getNumero().equals(det.getNumero()))
					return true;
			}
			return false;
		}
		
	}



	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}





	public static void main(String[] args) {
		DevolucionReplicator r=(DevolucionReplicator)ServiceManager.instance().getReplicador(Replicadores.DevolucionesReplicator);
		r.parcheParaActualizarNotasDeCredito();
	}
	

}
