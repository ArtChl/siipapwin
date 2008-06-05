package com.luxsoft.siipap.em.replica.notas;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.util.Assert;


import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;

import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;


public class ChequesDevueltosReplicator extends AbstractReplicatorSupport{
	
	//private ChequeDao chequeDao;		
	private String prefix="MCH";
	
	@Override
	@SuppressWarnings("unchecked")
	protected int contarBeans(Periodo p, Object... args) {
		String hql="select count(*) from Cheque c where c.mes=? and c.year=?";
		List<Long> l=getDao().getHibernateTemplate().find(hql,new Object[]{getMes(p),getYear(p)});
		return l.get(0).intValue();
	}

	public List<ReplicaLog> validar(Periodo periodo) {
		List<Periodo> meses=Periodo.periodosMensuales(periodo);
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();
		for(Periodo mes:meses){
			
			//Validando DDOCRE
			String table=ReplicationUtils.resolveTable("MOVCHE",mes.getFechaInicial());
			String sql="select count(*) from "+table+ " where MCHIDENOPE in(3,4) ";					
			int rows=getFactory().getJdbcTemplate(mes).queryForInt(sql);
			int beans=contarBeans(mes, "CHE");			
			list.add(registrar("Cheque","MOVCHE","CHE",mes,beans,rows));
			
		}
		return list;
	}


	
	public void validarBulkImport(Periodo p) {
		List<Periodo> periodos=Periodo.periodosMensuales(p);
		for(final Periodo mes:periodos){
			int beans=contarBeans(mes,"");
			Assert.isTrue(0==beans,"Existen registros en Cheque para el periodo :"+p);
		}
		
	}

	
	public void bulkImport(Periodo p) {
		validarBulkImport(p);
		List<Periodo> periodos=Periodo.periodosMensuales(p);
		
		for(final Periodo mes:periodos){
			String table=ReplicationUtils.resolveTable("MOVCHE",mes.getFechaInicial());
			String sql="select * from "+table+ " where MCHIDENOPE in(3,4) ";			
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
						nota.setOrigen("CHE");
						String clave=rs.getString(getPrefix()+"CLAVCLI");
						nota.setClave(clave);	
						
						if(numeroNota==null){
							numeroNota=BigDecimal.ZERO;
						}			
						nota.setNumero(numeroNota.longValue());
						nota.setSerie(serie);
						nota.setTipo(tipo);
						nota.setFecha(rs.getDate(getPrefix()+"FECHA"));						
						Number n=(Number)rs.getObject(getPrefix()+"SUBTOT");
						if(n!=null)
							nota.setImporte(CantidadMonetaria.pesos(n.doubleValue()));
						nota.setImpreso(rs.getDate(getPrefix()+"IMPRESO"));
						String tablaDet=ReplicationUtils.resolveTable("DDOCHE", mes.getFechaInicial());
						String sqlDet="select * from "+tablaDet+" where DCHSERIE=\'"+serie+"\' and DCHTIPO=\'"+tipo+"\' and DCHNUMERO="+numeroNota.longValue();
						System.out.println("SQL=" +sqlDet);
						System.out.println("Detalles de nota: "+getFactory().getJdbcTemplate(mes).queryForList(sqlDet).size());
						getFactory().getJdbcTemplate(mes).query(sqlDet, new RowCallbackHandler(){

							public void processRow(ResultSet rs) throws SQLException {
								
								final String prefix="DCH";
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
								injectYearMonth(mes, det);								
								nota.agregarPartida(det);
							}
							
						});
						notaCredito=nota;
						injectYearMonth(mes, nota);
					}
					//Creamos Juridico
					Assert.notNull(notaCredito);
					ChequeDevuelto c=new ChequeDevuelto();
					/*
					c.setNota(notaCredito);
					
					String concepto=rs.getString(getPrefix()+"CONCEPT");
					c.setConcepto(concepto);					
					
					Number origen=(Number)rs.getObject(getPrefix()+"ORIGEN");
					if(origen!=null)
						c.setOrigen(origen.intValue());
					
					Number banco=(Number)rs.getObject(getPrefix()+"BANCO");
					if(banco!=null)
						c.setBanco(banco.intValue());					
					
					Number porCarg=(Number)rs.getObject(getPrefix()+"PORCARG");
					if(porCarg!=null)
						c.setPorCarg(porCarg.doubleValue());					
					injectYearMonth(mes, c);
					try{
						getChequeDao().salvar(c);
					}catch(Exception ex){
						logger.error("No se pudo persistir el Cheque, seguramente ya exsite en la base de datos",ex);
					}
					*/
					
				}
				
			});
			validar(mes);
		}
		
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

/**
	public ChequeDao getChequeDao() {
		return chequeDao;
	}


	public void setChequeDao(ChequeDao chequeDao) {
		this.chequeDao = chequeDao;
	}

**/
	
	public static void main(String[] args) {
		ChequesDevueltosReplicator replicator=(ChequesDevueltosReplicator)ServiceManager.instance().getReplicador(Replicadores.ChequesDevueltosReplicator);
		replicator.bulkImport(new Periodo("01/01/2007","28/06/2007"));
	}	
	
	
	

}
