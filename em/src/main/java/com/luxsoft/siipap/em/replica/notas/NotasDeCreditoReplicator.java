package com.luxsoft.siipap.em.replica.notas;

import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
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

@SuppressWarnings("unchecked")
public class NotasDeCreditoReplicator extends AbstractReplicatorSupport{

	
	private NotasDeCreditoDetReplicator detReplicator;
	private DevolucionDao devolucionDao;
	private ClienteDao clienteDao;
	private VentasDao ventasDao;
	 
	
	@Override
	public List importar(Periodo periodo) {
		System.out.println("Cargando Notas: "+periodo);
		List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
		//notas.addAll(generarNotasMovcre(periodo));
		List<NotaDeCredito> mocomos=generarNotasMocomo(periodo);
		System.out.println("NC MOS: "+mocomos.size());
		notas.addAll(mocomos);
		List<NotaDeCredito> mococas=generarNotasMococa(periodo);
		System.out.println("NC CAM: "+mococas.size());
		notas.addAll(mococas);
		Collections.sort(notas, new Comparator<NotaDeCredito>(){

			public int compare(NotaDeCredito o1, NotaDeCredito o2) {
				return o1.getFecha().compareTo(o2.getFecha());
			}
			
		});
		injectYearMonth(periodo, notas);
		cargarDetalles(notas,periodo);
		
		vincularDependencias(notas);		
		
		
		for(NotaDeCredito nota:notas){
			persistir(nota);
		}
		//vincularVentas(periodo);
		actualizarClientes(periodo);
		actualizarDevoluciones();
		vincularDevoluciones(periodo);
		fixDevolucionesGrandes(periodo);
		return notas;
	}
	
	public void persistir(Object bean){
		NotaDeCredito nota=(NotaDeCredito)bean;
		try {
			NotaDeCredito n2=null;
			if(nota.getNumero()!=0){
				n2=getNotaDeCreditoDao().buscarNotaConDetalle(nota.getNumero(),nota.getTipo(), nota.getSerie());
			}else
				n2=getNotaDeCreditoDao().buscarNotaSinNumero(nota.getGrupo(),nota.getTipo(), nota.getSerie());
			if(n2!=null){
				//System.out.println("Nota existente: "+n2+" partidas: "+nota.getPartidas().size()+"; "+nota.getFecha());
				logger.info("Nota existente actualizandola....");
				if(n2.getPartidas().size()==0){
					logger.info("Nota sin partidas actualizandola....");
					getNotaDeCreditoDao().eliminarNotaConAplicaciones(n2.getId());
					getNotaDeCreditoDao().salvar(nota);
				}
				//Validando consistencia de la existente
				
				/*logger.info("Nota existente eliminandola....");
				getNotaDeCreditoDao().eliminar(n2);				
				getNotaDeCreditoDao().salvar(nota);
				*/
			}else{
				System.out.println("Nota no existente: "+nota+" partidas: "+nota.getPartidas().size());
				getNotaDeCreditoDao().salvar(nota);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}		
	}
	
	private void cargarDetalles(final List<NotaDeCredito> notas,final Periodo p){
		
		for(NotaDeCredito n:notas){
			List<NotasDeCreditoDet> partidas=buscarPartidas(n);
			injectYearMonth(p, partidas);
			for(NotasDeCreditoDet det:partidas){
				n.agregarPartida(det);
			}
			
		}
	}
	
	
	public List<NotasDeCreditoDet> buscarPartidas(NotaDeCredito nota){
			
		
		long numero=nota.getNumero();
		String serie=nota.getSerie();
		int grupo=nota.getGrupo();
		String tipo=nota.getTipo();
		
		
		String prefix="";
		String tabla="";
		String num="NUMERO";
		if(numero==0){
			num="GRUPO";
			numero=grupo;
		}
		String sql="select * from @TABLA WHERE @NUMERO="+numero+" and @SERIE=\'"+serie+"\' and @TIPO=\'"+tipo+"\'";
		
		if(nota.getOrigen().equals("CRE")){
			prefix="DCR";
			tabla=ReplicationUtils.resolveTable("DDOCRE", nota.getFecha());			
		}else if(nota.getOrigen().equals("CAM")){
			prefix="DCA";
			tabla=ReplicationUtils.resolveTable("DDOCAM", nota.getFecha());
		}else if(nota.getOrigen().equals("MOS")){
			prefix="DMO";
			tabla=ReplicationUtils.resolveTable("DDOMOS", nota.getFecha());
		}		
		sql=sql.replaceAll("@TABLA",tabla);
		sql=sql.replaceAll("@NUMERO", prefix+num);
		sql=sql.replaceAll("@SERIE",prefix+"SERIE");
		sql=sql.replaceAll("@TIPO",prefix+"TIPO");
		
		DefaultMapper mapper=getDetReplicator().getMapper();
		mapper.setOrigen(nota.getOrigen());
		mapper.setPrefix(prefix);
		
		List<NotasDeCreditoDet> partidas=getFactory().getJdbcTemplate(nota.getFecha()).query(sql, mapper);
		String msg="Partidas encontradas para nota {0} :{1}";
		
		logger.debug(MessageFormat.format(msg, nota.getNumero(),partidas.size()));
		return partidas;
		
	}
	
	private void vincularDevoluciones(final Periodo periodo){
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotaDeCredito n where n.tipo in(?,?) and n.fecha between ? and ?";
				ScrollableResults rs=session.createQuery(hql)
				.setString(0, "H")
				.setString(1, "I")
				//.setString(2, "J")
				.setParameter(2, periodo.getFechaInicial(),Hibernate.DATE)
				.setParameter(3, periodo.getFechaFinal(),Hibernate.DATE)
				.scroll();
				int count=0;
				while(rs.next()){
					final NotaDeCredito nota=(NotaDeCredito)rs.get()[0];
					Devolucion d=nota.getDevolucion();
					
					if(d==null){
						d=getDevolucionDao().buscar(nota.getNumeroDevo(), nota.getSucursalDevo());						
						if(d!=null){
							d=(Devolucion)session.get(Devolucion.class, d.getId());
							nota.setDevolucion(d);
							for(NotasDeCreditoDet det:nota.getPartidas()){
								Venta v=d.getVenta();
								if(nota.getCliente()==null)
									nota.setCliente(v.getCliente());
								det.setFactura(v);
								det.setSucDocumento(v.getSucursal());
								det.setTipoDocumento(v.getTipo());
								det.setSerieDocumento(v.getSerie());
								det.setNumDocumento(v.getNumero());
								det.setFechaDocumento(v.getFecha());
								det.setImporte(nota.getImporte().multiply(1.15));
							}
						}
					}else{						
						d=(Devolucion)session.get(Devolucion.class, d.getId());
						System.out.println("Devolucion existente: "+d);
					}
					if(d!=null){
						for(DevolucionDet dddet:d.getPartidas()){
							dddet.setNota(nota);
							dddet.setCxcnumero(nota.getNumero());
							dddet.setTipocxc(nota.getTipo());
						}
					}
					if(d==null)
						System.out.println("Nota de devolucion sin devoucion: "+nota);
					
					if(count++%20==0){
						session.flush();
						session.clear();
					}
					
				}
				return null;
			}
			
		});
	}
	
	/**
	 * Arregla posibles errores entre Notas y DevoDet para
	 * devoluciones mayores a 9 partidas
	 * 
	 * @param periodo
	 */
	private void fixDevolucionesGrandes(final Periodo periodo){
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Devolucion d where d.fecha between ? and ? and d.venta.origen in(\'CAM\',\'MOS\')";
				ScrollableResults rs=session.createQuery(hql)
				.setParameter(0, periodo.getFechaInicial(),Hibernate.DATE)
				.setParameter(1, periodo.getFechaFinal(),Hibernate.DATE)
				.scroll();
				while(rs.next()){
					final Devolucion devo=(Devolucion)rs.get()[0];
					if(devo.getPartidas().size()>9){
						//Localizamos las notas de credito deben existir 2
						List<NotaDeCredito> notas=session.createQuery("from NotaDeCredito n where n.devolucion=? order by n.id asc")
						.setEntity(0, devo)
						.list();
						if(notas.size()==0)
							continue;
						Assert.isTrue(notas.size()==2,"Notas incorrectas para: "+devo);						
						for(DevolucionDet det:devo.getPartidas()){
							if(det.getRenglon()<10)
								det.setNota(notas.get(0));
							else
								det.setNota(notas.get(1));
							det.setCxcnumero(det.getNota().getNumero());
							det.setTipocxc(det.getNota().getTipo());
						}
					}	
				}
				return null;
			}
			
		});
	}
	
	/*
	private void vincularVentas(final Periodo periodo){
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotasDeCreditoDet n where  n.fecha between ? and ? and n.factura is null";
				ScrollableResults rs=session.createQuery(hql)
				.setParameter(0, periodo.getFechaInicial(),Hibernate.DATE)				
				.setParameter(1, periodo.getFechaFinal(),Hibernate.DATE)
				.scroll();
				int count=0;
				while(rs.next()){
					final NotasDeCreditoDet det=(NotasDeCreditoDet)rs.get()[0];
					if(det.getNota().getDevolucion()!=null){
						logger.info("Usando devolucion");
						Devolucion devo=det.getNota().getDevolucion();
						Venta venta=devo.getVenta();
						det.setFactura(venta);
						det.setNumDocumento(venta.getNumero());
						det.setSucDocumento(venta.getSucursal());
						det.setSerieDocumento(venta.getSerie());
						det.setTipoDocumento(venta.getTipo());
						det.setFechaDocumento(venta.getFecha());
						det.setImporte(det.getNota().getTotalAsMoneda2());
						
					}
				}
				session.flush();
				return null;
			}
			
		});
	}
	*/
	private void vincularDependencias(List<NotaDeCredito> notas){
		for(NotaDeCredito nota:notas){
			
			// Vincula venta y cliente
			for(NotasDeCreditoDet det:nota.getPartidas()){
				Venta v=getVentasDao().buscarVenta(det.getSucDocumento(),det.getSerieDocumento(), det.getTipoDocumento(), det.getNumDocumento());
				if(v==null){
					v=getVentasDao().buscarVenta(det.getSucDocumento(), det.getTipoDocumento(), det.getNumDocumento());
				}
				if(v!=null && nota.getCliente()!=null)
					nota.setCliente(v.getCliente());
				det.setFactura(v);
			}
			
			// Vincular devoluciones
			if(nota.getTipo().equals("J") || nota.getTipo().equals("H") || nota.getTipo().equals("I")){
				
				//Localizamos la devolucion
				Devolucion d=getDevolucionDao().buscar(nota.getNumeroDevo(), nota.getSucursalDevo());
				if(d!=null){
					nota.setDevolucion(d);
					for(NotasDeCreditoDet det:nota.getPartidas()){
						Venta v=d.getVenta();
						det.setFactura(v);
						det.setSucDocumento(v.getSucursal());
						det.setTipoDocumento(v.getTipo());
						det.setSerieDocumento(v.getSerie());
						det.setNumDocumento(v.getNumero());
						det.setFechaDocumento(v.getFecha());
						det.setImporte(nota.getImporte().multiply(1.15));
					}
				}else{
					String msg="No se localizo la devolucion para vincular con NotasDet para la nota numeroDevo: {0} sucursalDevo: {1} tipo: {2}";
					logger.warn(MessageFormat.format(msg,nota.getNumeroDevo(),nota.getSucursalDevo(),nota.getTipo() ));
				}
			}
		}
	}

	public void bulkImport(Periodo periodo) {
		
		
	}
	/*
	protected List<NotaDeCredito> generarNotasMovcre(Periodo mes){
		ReplicationUtils.validarMismoMes(mes);
		List<NotaDeCredito> notas=cargarMaestro(mes, "MOVCRE", "MCR", "FECHA", "CRE");		
		return notas;
	}
	*/
	protected List<NotaDeCredito> generarNotasMococa(Periodo periodo){
		List<NotaDeCredito> notasMococa=cargarMaestro(periodo, "MOCOCA", "MCA", "FECHA","CAM");
		return notasMococa;
	}
	protected List<NotaDeCredito> generarNotasMocomo(Periodo periodo){
		List<NotaDeCredito> notas=cargarMaestro(periodo, "MOCOMO", "MCM", "FECHA", "MOS");
		return notas;
	}
	
	
	public List<NotaDeCredito> cargarMaestro(final Periodo mes,final String tabla,final String prefix,
			final String filtroFecha,final String origen){
		final String filtroOper="IDENOPE"; 
		String sql=ReplicationUtils.resolveSQL(mes,tabla,prefix+filtroFecha);
		sql=sql+" AND "+prefix+filtroOper+" in (3,4)";
		//logger.debug("SQL Final: "+sql);		
		
		DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(getBeanClass());
		mapper.setOrigen(origen);
		mapper.setPrefix(prefix);
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		List<NotaDeCredito> rows=getFactory().getJdbcTemplate(mes).query(sql,mapper);
		
		injectYearMonth(mes, rows);		
		return rows;
	}
	
	

	public List<ReplicaLog> validar(Periodo periodo) {
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();		
		return list;
	}
	
	@Override
	protected int contarBeans(Periodo p, Object... args) {		
		final String hql="select count(*) from NotaDeCredito e where e.fecha between ? and ? and e.origen=?";		
		List l=getDao().getHibernateTemplate().find(hql, new Object[]{p.getFechaInicial(),p.getFechaFinal(),args[0]});
		return ((Long)l.get(0)).intValue();
	}	
	
	private void actualizarClientes(final Periodo periodo){
		String sql="UPDATE SW_NOTAS A SET A.CLIENTE_ID=(SELECT X.CLIENTE_ID FROM SW_CLIENTES X WHERE A.CLAVE=X.CLAVE) WHERE FECHA BETWEEN ? AND ? AND ORIGEN IN(\'CAM\', \'MOS\')";
		SqlParameterValue p1=new SqlParameterValue(Types.DATE,periodo.getFechaInicial());
		SqlParameterValue p2=new SqlParameterValue(Types.DATE,periodo.getFechaFinal());
		getTargetJdbcTemplate().update(sql, new SqlParameterValue[]{p1,p2});
	}
	
	/**
	 * Actualiza la tabla de devoluciones
	 * 
	 */
	private void actualizarDevoluciones(){
		String  sql="UPDATE SW_DEVODET A SET A.NOTA_ID=(SELECT B.NOTA_ID FROM SW_NOTASDET B WHERE A.CXCNUMERO=B.NUMERO AND A.TIPOCXC=B.TIPO)" +
				" WHERE NOTA_ID IS NULL";
		getTargetJdbcTemplate().execute(sql);
	}

	public void validarBulkImport(Periodo p) {
			
	}
	public NotasDeCreditoDetReplicator getDetReplicator() {
		return detReplicator;
	}
	public void setDetReplicator(NotasDeCreditoDetReplicator detReplicator) {
		this.detReplicator = detReplicator;
	}
	
	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	public DevolucionDao getDevolucionDao() {
		return devolucionDao;
	}

	public void setDevolucionDao(DevolucionDao devolucionDao) {
		this.devolucionDao = devolucionDao;
	}
	

	public VentasDao getVentasDao() {
		return ventasDao;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}

	public NotaDeCreditoDao getNotaDeCreditoDao(){
		return (NotaDeCreditoDao)getDao();
	}
			
	public static void main(String[] args) {
		NotasDeCreditoReplicator replicator=(NotasDeCreditoReplicator)ServiceManager.instance()
		.getReplicador(Replicadores.NotasDeCreditoReplicator);
		replicator.importar(new Periodo("01/09/2008","30/09/2008"));
		//replicator.vincularDevoluciones();
		//replicator.vincularVentas();
	}

	

}
