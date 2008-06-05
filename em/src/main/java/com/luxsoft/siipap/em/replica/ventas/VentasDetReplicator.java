package com.luxsoft.siipap.em.replica.ventas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.ventas.domain.VentaDet;

@SuppressWarnings("unchecked")
public class VentasDetReplicator extends AbstractReplicatorSupport  {
		
	private Map<String,String> propertyColumnMap;
	
	
	
	public void exportar(Object bean) {
		// TODO Auto-generated method stub
		
	}

	public List importar(Periodo periodo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Valida que no existan registros en el periodo seleccionado para poder generar una carga bulk nuevaç
	 * 
	 * @param p
	 */
	public void validarBulkImport(Periodo p){
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			int beans=contarBeans(mes, new Object[0]);
			Assert.isTrue(0==beans,"Existen registros en el periodo seleccionado deben ser borrados antes de proseguir mes: "+mes);
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.luxsoft.siipap.em.replica.ventas.Importador#bulkImport(com.luxsoft.siipap.domain.Periodo)
	 */
	public void bulkImport(Periodo p){
		validarBulkImport(p);
		
		// Dividimos por meses
		
		List<Periodo> meses=Periodo.periodosMensuales(p);
		for(Periodo mes:meses){
			try {
				logger.info("Importando mes "+mes);
				List<VentaDet> detalle=generar(mes);
				injectYearMonth(mes,detalle);
				salvar(detalle);
				/*
				for(Date dia:mes.getListaDeDias()){
					logger.info("Importando: "+dia);
					List<VentaDet> detalle=generar(dia);
					injectYearMonth(mes,detalle);
					salvar(detalle);
				}*/
				validar(mes);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
				
		}		
	}
	
	

	/* (non-Javadoc)
	 * @see com.luxsoft.siipap.em.replica.ventas.Importador#validar(com.luxsoft.siipap.domain.Periodo)
	 */
	public List<ReplicaLog> validar(final Periodo periodo){
		List<Periodo> meses=Periodo.periodosMensuales(periodo);
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();
		for(Periodo mes:meses){
			try {
//				Validando MOCOMO
				String rowsSQL=ReplicationUtils.resolveSQLCount(mes,"ALMACE","ALMFECHA")+" AND ALMTIPO=\'FAC\'";		
				int rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
				int beans=contarBeans(mes, new Object[0]);			
				list.add(registrar("VentaDet","ALMACE","FAC",mes,beans,rows));
				
			} catch (Exception e) {
				logger.error(e);
			}						
		}		
		return list;
	}	
	
	
	
	public List<VentaDet> generar(Periodo mes){		
		//String sql=ReplicationUtils.resolveSQL(new Periodo(dia,dia),"ALMACE","ALMFECHA")+" AND ALMTIPO=\'FAC\'";
		String sql=ReplicationUtils.resolveSQL(mes,"ALMACE","ALMFECHA")+" AND ALMTIPO=\'FAC\' ORDER BY ALMSUCUR,ALMNUMER,ALMSERIE";
		VentasDetMapper mapper=new VentasDetMapper();
		mapper.setBeanClass(getBeanClass());
		mapper.setOrigen("ALMACE");
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		List<VentaDet> rows=getFactory().getJdbcTemplate(mes).query(sql,mapper);
		logger.info("VentaDet obtenidas  : "+rows.size());
		return rows;
	}
	
	
	
	
	public Map<String, String> getPropertyColumnMap() {
		return propertyColumnMap;
	}

	public void setPropertyColumnMap(Map<String, String> propertyColumnMap) {
		this.propertyColumnMap = propertyColumnMap;
	}

	public static void main(String[] args) {
		Replicador r=ServiceManager.instance().getReplicador(Replicadores.VentasDetReplicator);
		r.validar(new Periodo("01/01/2007","31/01/2007"));
	}

}
