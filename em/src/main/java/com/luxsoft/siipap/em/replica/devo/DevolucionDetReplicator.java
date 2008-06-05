package com.luxsoft.siipap.em.replica.devo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.DefaultMapper;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;

@SuppressWarnings("unchecked")
public class DevolucionDetReplicator extends AbstractReplicatorSupport{
	
	private Map<String,String> propertyColumnMap;
	
	
	public List<DevolucionDet> generar(Periodo mes){
		ReplicationUtils.validarMismoMes(mes);
		String sql=ReplicationUtils.resolveSQL(mes,"ALMACE","ALMFECHA")+" AND ALMTIPO=\'RMD\' order by ALMSUCUR,ALMNUMER";
		DefaultMapper mapper=new DefaultMapper(){

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				DevolucionDet det=(DevolucionDet)super.mapRow(rs, rowNum);
				det.setCantidad(det.getCantidad()/det.getFactorDeConversionUnitaria());
				return det;
			}
			
		};
		mapper.setBeanClass(getBeanClass());
		mapper.setOrigen("ALMACE");
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		List<DevolucionDet> rows=getFactory().getJdbcTemplate(mes).query(sql,mapper);
		logger.info("DevolucionesDet obtenidas  : "+rows.size());
		return rows;
	}
	
	
	public List<ReplicaLog> validar(Periodo periodo) {
		List<Periodo> meses=Periodo.periodosMensuales(periodo);
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();
		for(Periodo mes:meses){
			try {
				String rowsSQL=ReplicationUtils.resolveSQLCount(mes,"ALMACE","ALMFECHA")+" AND ALMTIPO=\'RMD\'";		
				int rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
				int beans=contarBeans(mes, new Object[0]);			
				list.add(registrar("DevolucionDet","ALMACE","RMD",mes,beans,rows));
				
			} catch (Exception e) {
				logger.error(e);
			}						
		}		
		return list;
	}

	public void bulkImport(Periodo p) {
		validarBulkImport(p);		
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			try {
				logger.info("Importando mes "+mes);
				List<DevolucionDet> detalle=generar(mes);
				injectYearMonth(mes,detalle);
				salvar(detalle);				
				validar(mes);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}				
		}	
	}

	

	public Map<String, String> getPropertyColumnMap() {
		return propertyColumnMap;
	}

	public void setPropertyColumnMap(Map<String, String> propertyColumnMap) {
		this.propertyColumnMap = propertyColumnMap;
	}	
	
	public static void main(String[] args) {
		Replicador r=ServiceManager.instance().getReplicador(Replicadores.DevolucionesDetReplicator);
		r.validar(new Periodo("01/01/2007","31/01/2007"));
	}
	

}
