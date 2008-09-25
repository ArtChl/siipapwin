package com.luxsoft.siipap.em.replica.notas;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.dao.JuridicoDao;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.domain.Juridico;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;


public class JuridicoReplicator extends AbstractReplicatorSupport{
	
	
	
	private JuridicoDao juridicoDao;
	private VentasDao ventasDao;
	
	private String prefix="MJU";
	
	public void bulkImport(Periodo p) {
		//validarBulkImport(p);
		System.out.println("Procesando periodo: "+p);
		List<Periodo> periodos=Periodo.periodosMensuales(p);
		
		for(final Periodo mes:periodos){
			System.out.println("Procesando periodo: "+mes);
			String table=ReplicationUtils.resolveTable("MOVJUR",mes.getFechaInicial());
			String sql="select * from "+table+ " where MJUIDENOPE in(3,4) ";
			getFactory().getJdbcTemplate(mes).query(sql, new RowCallbackHandler(){

				public void processRow(ResultSet rs) throws SQLException {
					
					
					BigDecimal numeroNota=rs.getBigDecimal(getPrefix()+"NUMDOCT");
					System.out.println("Procesando nota: "+numeroNota);
					Assert.notNull(numeroNota);
					String tipo=rs.getString(getPrefix()+"TIPOFAC");
					Assert.notNull(tipo);
					String serie=rs.getString(getPrefix()+"SERIEFA");
					Assert.notNull(serie);
					
					NotaDeCredito notaCredito=existeLaNota(numeroNota.longValue(), tipo, serie);
					
					if(notaCredito==null){
						System.out.println("Generando nueva nota de credito: "+numeroNota);
						final NotaDeCredito nota=new NotaDeCredito();
						nota.setOrigen("JUR");
						String clave=rs.getString(getPrefix()+"CLAVCLI");						
						nota.setClave(clave);	
						
						if(numeroNota==null){
							numeroNota=BigDecimal.ZERO;
						}			
						nota.setNumero(numeroNota.longValue());
						nota.setSerie(serie);
						nota.setTipo(tipo);
						nota.setFecha(rs.getDate(getPrefix()+"FECHA"));
						
						BigDecimal nf=rs.getBigDecimal(getPrefix()+"NUMFISC");
						if(nf!=null)			
							nota.setNumeroFiscal(nf.intValue());						
						Number importe=(Number)rs.getObject(getPrefix()+"SUBTOT");
						if(importe!=null)
							nota.setImporte(CantidadMonetaria.pesos(importe.doubleValue()));
						nota.setImpreso(rs.getDate(getPrefix()+"IMPRESO"));
						String tablaDet=ReplicationUtils.resolveTable("DDOJUR", mes.getFechaInicial());
						String sqlDet="select * from "+tablaDet+" where DJUSERIE=\'"+serie+"\' and DJUTIPO=\'"+tipo+"\' and DJUNUMERO="+numeroNota.longValue();
						System.out.println("SQL=" +sqlDet);
						System.out.println("Detalles de nota: "+getFactory().getJdbcTemplate(mes).queryForList(sqlDet).size());
						getFactory().getJdbcTemplate(mes).query(sqlDet, new RowCallbackHandler(){

							public void processRow(ResultSet rs) throws SQLException {
								
								final String prefix="DJU";
								NotasDeCreditoDet det=new NotasDeCreditoDet();
								det.setOrigen(nota.getOrigen());
								/** Datos para la migracion **/
								Number numero=(Number)rs.getObject(prefix+"NUMERO");
								if(numero==null)
									numero=BigDecimal.ZERO;
								det.setNumero(numero.longValue());
								det.setSerie(rs.getString(prefix+"SERIE"));
								det.setTipo(rs.getString(prefix+"TIPO"));
								
								Number rengl=(Number)rs.getObject(prefix+"RENGL");
								det.setRenglon(rengl!=null?rengl.intValue():0);
								
								String comentario=(String)rs.getString(prefix+"TEXTO");		
								det.setComentario(comentario);
								
								if(rengl!=null){
									int sucursal=((Number)rs.getObject(prefix+"SUCDOCR")).intValue();
									int factura=((Number)rs.getObject(prefix+"NUMDOCR")).intValue();
									String tip=rs.getString(prefix+"TIPDOCR");
									String ser=rs.getString(prefix+"SERDOCR");
									
									Number bdDesc=(Number)rs.getObject(prefix+"PORDOCR");
									if(bdDesc!=null)
										det.setDescuento(bdDesc.doubleValue());
									
									//Importe
									Number bdImp=(Number)rs.getObject(prefix+"DESDOCR");
									if(bdImp!=null)
										det.setImporte(CantidadMonetaria.pesos(bdImp.doubleValue()));
									
									//Fecha
									det.setFechaDocumento((Date)rs.getDate(prefix+"FECDOCR"));
									//Sucursal
									det.setSucDocumento(sucursal);			
									det.setNumDocumento(factura);
									det.setTipoDocumento(tip);
									det.setSerieDocumento(ser);
									
									Number bdGrp=(Number)rs.getObject(prefix+"GRUPO");
									if(bdGrp!=null)
										det.setGrupo(bdGrp.intValue());
									
								}
								//logger.debug("Detalle de nota generada: "+det);
								System.out.println("Detalle de nota generada: "+det);
								nota.agregarPartida(det);
								injectYearMonth(mes, nota);
							}
							
						});
						notaCredito=nota;
						injectYearMonth(mes, notaCredito);
					}
					//Creamos Juridico
					Assert.notNull(notaCredito);
					Juridico j=new Juridico();
					j.setNota(notaCredito);
					Number claveAbogado=(Number)rs.getObject(getPrefix()+"CLAVABO");
					String nombreAbogado=rs.getString(getPrefix()+"NOMABO");
					if(claveAbogado!=null)
						j.setClaveAbogado(claveAbogado.intValue());
					j.setNombreAbogado(nombreAbogado);
					String comentarios=rs.getString(getPrefix()+"COMENTA");
					j.setComentarios(comentarios);
					Date fechaTraspaso=rs.getDate(getPrefix()+"FECTRAS");
					j.setFechaTraspaso(fechaTraspaso);
					String origen=rs.getString(getPrefix()+"ORIGEN");
					j.setOrigen(origen);
					Number saldo=(Number)rs.getObject(getPrefix()+"SALDO");
					if(saldo!=null)
						j.setSaldoDoc(saldo.doubleValue());
					System.out.println("Juridico creado: "+j);
					injectYearMonth(mes, j);
					try {
						getJuridicoDao().salvar(j);
					} catch (Exception ex) {
						logger.error(ex);
					}					
				}
				
			});
			
		}		
		vincularVentas(p);
	}
	
	private void vincularVentas(final Periodo periodo){
		System.out.println("Vinculando ventas del periodo: "+periodo.toString2());
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotasDeCreditoDet n where   n.origen=\'JUR\' and n.nota.fecha between ? and ?";
				ScrollableResults rs=session.createQuery(hql)
				.setParameter(0, periodo.getFechaInicial(),Hibernate.DATE)
				.setParameter(1, periodo.getFechaFinal(),Hibernate.DATE)
				.scroll();
				int count=0;
				while(rs.next()){
					final NotasDeCreditoDet det=(NotasDeCreditoDet)rs.get()[0];
					Venta v=getVentasDao().buscarVenta(det.getSucDocumento(),det.getSerieDocumento(), det.getTipoDocumento(), det.getNumDocumento());
					if(v==null){
						v=getVentasDao().buscarVenta(det.getSucDocumento(), det.getTipoDocumento(), det.getNumDocumento());
					}
					System.out.println("Venta econtrada: "+v);
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
	
	@SuppressWarnings("unchecked")
	@Override
	protected int contarBeans(Periodo p, Object... args) {
		return 0;
	}

	public List<ReplicaLog> validar(Periodo periodo) {
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();
		return list;
	}
	
	public void validarBulkImport(Periodo p) {
		
	}
	
	@SuppressWarnings("unchecked")
	private void importarFacturas(Periodo periodo){
		List<Periodo> meses=Periodo.periodosMensuales(periodo);		
		for(Periodo mes:meses){
			String table=ReplicationUtils.resolveTable("MOVJUR",mes.getFechaInicial());
			String sql="select * from "+table+ " where MJUIDENOPE=1 ";
			List<Map<String,Object>> rows=getFactory().getJdbcTemplate(mes).queryForList(sql);
			for(Map row:rows){
				logger.debug("Procesando registro:"+row);
				Integer sucursal=((Number)row.get("MJUSUCURSA")).intValue();
				String serie=(String)row.get("MJUSERIEFA");
				String tipo=(String)row.get("MJUTIPOFAC");
				Long numero=((Number)row.get("MJUNUMDOCT")).longValue();
				logger.debug("Localizando venta: "+numero+" Suc:"+sucursal+" Tipo:"+tipo+" Serie: "+serie);
				Venta v=getVentasDao().buscarVenta(sucursal, serie, tipo, numero);
				Assert.notNull(v);
				logger.debug("Localizando Juridico ..");
				Juridico j=existeLaFacturaEnJuridico(v);
				if(j!=null){
					logger.debug("Juridico localizado: "+j.getId()+ " Eliminandolo");
					getJuridicoDao().eliminar(j);
					j=null;
				}
				j=new Juridico();				
				j.setOrigen(v.getOrigen());
				j.setClaveAbogado(((Number)row.get(getPrefix()+"CLAVABO")).intValue());
				j.setNombreAbogado((String)row.get(getPrefix()+"NOMABO"));
				j.setComentarios((String)row.get(getPrefix()+"COMENTA"));
				j.setFechaTraspaso((Date)row.get(getPrefix()+"FECTRAS"));
				Number saldo=(Number)row.get(getPrefix()+"SALDO");
				if(saldo!=null)
					j.setSaldoDoc(saldo.doubleValue());				
				j.setVenta(v);
				injectYearMonth(mes, j);
				v.setOrigen("JUR");
				getVentasDao().salvar(v);
				getJuridicoDao().salvar(j);
				
			}
		}
	}
	
	public Juridico existeLaFacturaEnJuridico(Venta v){
		return getJuridicoDao().buscar(v);
	}
	
	public NotaDeCredito existeLaNota(long numero,String tipo,String serie){
		NotaDeCredito nc=getNotaDeCreditoDao().buscarNota(numero, tipo, serie);
		return nc;
	}	

	public NotaDeCreditoDao getNotaDeCreditoDao() {
		return (NotaDeCreditoDao)getDao();
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public JuridicoDao getJuridicoDao() {
		return juridicoDao;
	}

	public void setJuridicoDao(JuridicoDao juridicoDao) {
		this.juridicoDao = juridicoDao;
	}
	
	
	public VentasDao getVentasDao() {
		return ventasDao;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}

	public static void main(String[] args) {
		JuridicoReplicator r=(JuridicoReplicator)ServiceManager.instance().getReplicador(Replicadores.JuridicoReplicator);
		//r.importarFacturas(new Periodo("01/04/2007","28/06/2007"));
		r.bulkImport(new Periodo("01/01/2008","30/09/2008"));
		
	}

}
