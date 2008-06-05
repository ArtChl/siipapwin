package com.luxsoft.siipap.em.validacion;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.managers.EMServiceLocator;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;

public class VentasValidator implements Validador{
	
	private final String tabla="MOVCRE";

	public int contarRegistrosEnSiipap(final Periodo periodo) {
		int registros=0;
		for (Periodo mes:Periodo.periodosMensuales(periodo)){
			String sql=getSqlParaSiipap(mes);
			System.out.println(sql);
			registros+=getSiipapTemplate(mes).queryForInt(sql);
		}
		return registros;
	}
	
	public int contarRegistrosEnSiipapWin(final Periodo p){
		final String sql="SELECT COUNT(*) FROM SW_VENTAS WHERE " +
				" YEAR=? AND MES=? AND FECHA BETWEEN ? AND ? ";
		Object[] params={
				Periodo.obtenerYear(p.getFechaFinal())
				,Periodo.obtenerMes(p.getFechaFinal())
				,p.getFechaInicial()
				,p.getFechaFinal()
				};
		int[] tipos={Types.INTEGER,Types.INTEGER,Types.DATE,Types.DATE};
		return getSiipapWinTemplate().queryForInt(sql,params,tipos);
	}
	
	public String getSqlParaSiipap(Periodo mes) {
		return ReplicationUtils.resolveSQLCount(mes, tabla, "MCRFECHA")+" AND MCRIDENOPE=1";
	}
	
	




	public List<ReplicaLog> validarDia(Date dia) {
		int registros=contarRegistrosEnSiipap(new Periodo(dia));
		int registrosWin=contarRegistrosEnSiipapWin(new Periodo(dia));
		
		ReplicaLog log=new ReplicaLog();
		log.setBeans(registrosWin);
		log.setRegistros(registros);
		log.setDia(dia);
		log.setTabla("MOVCRE");
		log.setEntity("Venta");
		log.setMonth(Periodo.obtenerMes(dia));
		log.setYear(Periodo.obtenerYear(dia));
		List<ReplicaLog> logs=new ArrayList<ReplicaLog>();
		logs.add(log);
		return logs;
	}

	protected JdbcTemplate getSiipapTemplate(final Periodo p){
		return EMServiceLocator.instance().getSiipapTemplateFactory().getJdbcTemplate(p);
	}
	
	protected JdbcTemplate getSiipapWinTemplate(){
		return EMServiceLocator.instance().getDefaultJdbcTemplate();
	}

}
