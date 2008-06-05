package com.luxsoft.siipap.em.replica.notas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.DefaultMapper;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;



@SuppressWarnings("unchecked")
public class NotasDeCreditoDetReplicator extends AbstractReplicatorSupport{
	
	private Map<String, String> propertyColumnMap;

	

	public void bulkImport(Periodo p) {
		validarBulkImport(p);
		List<Periodo> meses=Periodo.periodosMensuales(p);
		
		for(Periodo mes:meses){
			List<NotaDeCredito> notas=getNotas(mes);
			
			List<NotasDeCreditoDet> detsDCR=generarNotasDeDDOCRE(mes);
			vincularConMaestro(mes, notas, detsDCR);
			for(NotasDeCreditoDet det:detsDCR){
				if(det.getNota()==null){
					logger.debug("NotaDet sin maestro: "+ det.getNumero()+" "+det.getTipo());
				}
			}
			
			//salvar(detsDCR.toArray());
			
			List<NotasDeCreditoDet> detsDCA=generarNotasDeDDOCAM(mes);
			vincularConMaestro(mes, notas, detsDCA);
			//salvar(detsDCA);
			
			List<NotasDeCreditoDet> detsDMO=generarNotasDeDDOMOS(mes);
			vincularConMaestro(mes, notas, detsDMO);
			
			salvar(notas.toArray());
			
			validar(mes);
		}
		
	}
	
	
	
	@Override
	public void salvar(Object... beans) {
		//System.out.println(beans[0].getClass().getName());
		for(Object o:beans){
			NotaDeCredito n=(NotaDeCredito)o;
			try {
				getDao().getHibernateTemplate().saveOrUpdate(n);
			} catch (Exception e) {
				logger.error("Imposible salvar nota: "+n.getNumero()+" Tipo: "+n.getTipo(),e);
			}
			
			
		}
		
	}


	protected List<NotasDeCreditoDet> generarNotasDeDDOCRE(Periodo periodo){
		ReplicationUtils.validarMismoMes(periodo);
		List<NotasDeCreditoDet> dets=cargarDetalle(periodo,"DDOCRE","CRE","DCR","FECHA");
		return dets;
	}
	protected List<NotasDeCreditoDet> generarNotasDeDDOCAM(Periodo periodo){
		List<NotasDeCreditoDet> dets=cargarDetalle(periodo,"DDOCAM","CAM","DCA","FECHA");
		return dets;
	}
	protected List<NotasDeCreditoDet> generarNotasDeDDOMOS(Periodo periodo){
		List<NotasDeCreditoDet> dets=cargarDetalle(periodo,"DDOMOS","MOS","DMO","FECHA");
		return dets;
	}
	
	@Override
	protected int contarBeans(Periodo p, Object... args) {
		final String hql="select count(*) from NotasDeCreditoDet e where e.fecha between ? and ? and e.origen=?";		
		List l=getDao().getHibernateTemplate().find(hql, new Object[]{p.getFechaInicial(),p.getFechaFinal(),args[0]});
		return ((Long)l.get(0)).intValue();
	}

		
	
	protected List<NotasDeCreditoDet> cargarDetalle(final Periodo periodo,final String tabla,final String origen,final String prefix,final String fechaDiscriminator){		
		//Date fecha=periodo.getFechaInicial();
		//String table=ReplicationUtils.resolveTable(tabla, fecha);
		//String sql="select * from "+table;
		
		String sql=ReplicationUtils.resolveSQL(periodo, tabla, prefix+fechaDiscriminator);
		
		DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(NotasDeCreditoDet.class);
		mapper.setOrigen(origen);
		mapper.setPrefix(prefix);
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		
		List<NotasDeCreditoDet> rows=getFactory().getJdbcTemplate(periodo).query(sql,mapper);
		injectYearMonth(periodo, rows);
		logger.debug("NotasDeCreditoDet generadas desde "+tabla+" : "+rows.size());
		return rows;		
	}
	
	public DefaultMapper getMapper(){
		DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(NotasDeCreditoDet.class);		
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		return mapper;
	}
	
	public List<ReplicaLog> validar(Periodo mes) {
		ReplicationUtils.validarMismoMes(mes);
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();		
			
			String tabla=ReplicationUtils.resolveTable("DDOCRE", mes.getFechaFinal());
			String rowsSQL="select count(*) from "+tabla;
			int rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			int beans=contarBeans(mes, "CRE");			
			list.add(registrar("NotaDet","DDOCRE","NCD",mes,beans,rows));
			
			tabla=ReplicationUtils.resolveTable("DDOCAM", mes.getFechaFinal());
			rowsSQL="select count(*) from "+tabla;
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes, "CAM");			
			list.add(registrar("NotaDet","DDOCAM","NCD",mes,beans,rows));
			
			
			tabla=ReplicationUtils.resolveTable("DDOMOS", mes.getFechaFinal());
			rowsSQL="select count(*) from "+tabla;					
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes, "MOS");			
			list.add(registrar("NotaDet","DDOMOS","NCD",mes,beans,rows));
		
		
		return list;
	}

	
	public void validarBulkImport(Periodo p) {
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			String hql="select count(*) from NotasDeCreditoDet d where d.mes=? and d.year=?";
			List<Long> l=getDao().getHibernateTemplate().find(hql, new Object[]{getMes(mes),getYear(mes)});
			Assert.isTrue(l.get(0).intValue()==0,"Existen registros en el periodo seleccionado deben ser borrados antes de proseguir mes: "+mes);
		}
		
	}
	
	protected void vincularConMaestro(final Periodo periodo,final List<NotaDeCredito> notas,final List<NotasDeCreditoDet> detalles){		
			
		
		for(final NotaDeCredito nc:notas){
			
			CollectionUtils.select(detalles,new Predicate(){

				public boolean evaluate(Object arg0) {
					NotasDeCreditoDet det=(NotasDeCreditoDet)arg0;
					long numeroNC=nc.getNumero();					
					long numeroDet=det.getNumero();
					if(numeroNC==0){
						numeroNC=nc.getGrupo();
						numeroDet=det.getGrupo();
					}
					if(numeroDet==numeroNC)
						if(det.getTipo().equals(nc.getTipo()))
							if(det.getSerie().equals(nc.getSerie())){
								nc.agregarPartida(det);
								return true;
							}					
					return false;
				}
				
			});			
						
		}
		//filtrarPadresNulos(detalles);		
	}
	
	
	protected List<NotaDeCredito> getNotas(Periodo mes){
		String hql="select n from NotaDeCredito n left join fetch n.partidas where n.mes=? and n.year=?";
		return getDao().getHibernateTemplate().find(hql, new Object[]{getMes(mes),getYear(mes)});
		
	}

	public Map<String, String> getPropertyColumnMap() {
		return propertyColumnMap;
	}

	public void setPropertyColumnMap(Map<String, String> propertyColumnMap) {
		this.propertyColumnMap = propertyColumnMap;
	}
	
	public static void main(String[] args) {
		Replicador r=ServiceManager.instance().getReplicador(Replicadores.NotasDeCreditoDetReplicator);
		r.validar(new Periodo("01/01/2007","31/01/2007"));
	}

}
