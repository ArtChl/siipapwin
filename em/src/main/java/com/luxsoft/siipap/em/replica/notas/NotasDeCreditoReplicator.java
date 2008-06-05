package com.luxsoft.siipap.em.replica.notas;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

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
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.dao.DevolucionDao;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class NotasDeCreditoReplicator extends AbstractReplicatorSupport{

	
	private NotasDeCreditoDetReplicator detReplicator;
	private DevolucionDao devolucionDao;
	private ClienteDao clienteDao;
	private VentasDao ventasDao;
	
	@Override
	public List importar(Periodo periodo) {
		List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
		//notas.addAll(generarNotasMovcre(periodo));
		notas.addAll(generarNotasMocomo(periodo));
		notas.addAll(generarNotasMococa(periodo));
		injectYearMonth(periodo, notas);
		cargarDetalles(notas,periodo);
		for(NotaDeCredito nota:notas){			
			persistir(nota);
		}
		vincularDependencias(notas);
		
		actualizarDevoluciones();
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
				logger.info("Nota existente eliminandola....");
				getNotaDeCreditoDao().eliminar(n2);				
				getNotaDeCreditoDao().salvar(nota);
			}else
				getNotaDeCreditoDao().salvar(nota);
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
	
	public void vincularDevoluciones(){
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotaDeCredito n where n.tipo in(?,?,?) and devolucion is null and year=2007";
				ScrollableResults rs=session.createQuery(hql)
				.setString(0, "H")
				.setString(1, "I")
				.setString(2, "J")
				.scroll();
				int count=0;
				while(rs.next()){
					final NotaDeCredito nota=(NotaDeCredito)rs.get()[0];
					Devolucion d=getDevolucionDao().buscar(nota.getNumeroDevo(), nota.getSucursalDevo());
					if(d!=null){
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
					if(count++%20==0){
						session.flush();
						session.clear();
					}
					
				}
				return null;
			}
			
		});
	}
	
	public void vincularVentas(){
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotasDeCreditoDet n where  n.year=2007 and n.mes>2 and n.factura is null";
				ScrollableResults rs=session.createQuery(hql)				
				.scroll();
				int count=0;
				while(rs.next()){
					final NotasDeCreditoDet det=(NotasDeCreditoDet)rs.get()[0];
					Venta v=getVentasDao().buscarVenta(det.getSucDocumento(),det.getSerieDocumento(), det.getTipoDocumento(), det.getNumDocumento());
					if(v==null){
						v=getVentasDao().buscarVenta(det.getSucDocumento(), det.getTipoDocumento(), det.getNumDocumento());
					}
					det.setFactura(v);
					System.out.println("Actualizando venta de notadet: "+det);
					if(count++%20==0){
						session.flush();
						session.clear();
					}					
				}
				return null;
			}
			
		});
	}
	
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
		/*
		ReplicationUtils.validarMismoMes(periodo);
		
		Iterator<Date> dias=periodo.getDiasIterator();
		while(dias.hasNext()){
			//			Validando MOCOMO
			Periodo mes=new Periodo(dias.next());
			String rowsSQL=ReplicationUtils.resolveSQLCount(mes,"MOCOMO","MCMFECHA")+" AND MCMIDENOPE in (3,4)";		
			int rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			int beans=contarBeans(mes, "MOS");			
			list.add(registrar("Nota","MOCOMO","NCR",mes,beans,rows));
			
			//Validando MOCOCA
			rowsSQL=ReplicationUtils.resolveSQLCount(mes,"MOCOCA","MCAFECHA")+" AND MCAIDENOPE in (3,4)";		
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes,"CAM");			
			list.add(registrar("Nota","MOCOCA","NCR",mes,beans,rows));
			
			//Validando MOVCRE
			rowsSQL=ReplicationUtils.resolveSQLCount(mes,"MOVCRE","MCRFECHA")+" AND MCRIDENOPE in (3,4)";		
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes,"CRE");			
			list.add(registrar("Nota","MOVCRE","NCR",mes,beans,rows));
			
			
		}
		list.addAll(getDetReplicator().validar(periodo));
		*/
		return list;
	}
	
	@Override
	protected int contarBeans(Periodo p, Object... args) {		
		final String hql="select count(*) from NotaDeCredito e where e.fecha between ? and ? and e.origen=?";		
		List l=getDao().getHibernateTemplate().find(hql, new Object[]{p.getFechaInicial(),p.getFechaFinal(),args[0]});
		return ((Long)l.get(0)).intValue();
	}	
	
	private void actualizarDevoluciones(){
		String  sql="UPDATE SW_DEVODET A SET A.NOTA_ID=(SELECT B.NOTA_ID FROM SW_NOTASDET B WHERE A.CXCNUMERO=B.NUMERO AND A.TIPOCXC=B.TIPO) WHERE NOTA_ID IS NULL";
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
		replicator.importar(new Periodo(DateUtils.obtenerFecha("28/01/2008")));
		//replicator.vincularDevoluciones();
		//replicator.vincularVentas();
	}

	

}
