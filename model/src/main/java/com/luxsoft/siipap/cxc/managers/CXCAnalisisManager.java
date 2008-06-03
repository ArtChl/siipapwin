package com.luxsoft.siipap.cxc.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.cxc.domain.CXC;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;



/**
 * Manager especializado en consulta y extraccion de informacion
 * no en presistencia de datos
 * 
 * @author Ruben Cancino
 *
 */
public class CXCAnalisisManager {
	
	
	
	@SuppressWarnings("unchecked")
	public static List<CXC> leerCargosAbonos(final String cliente, final Date fecha){
		final String sql="SELECT A.*," +
				"(SELECT NVL(SUM(X.TOTAL),0) FROM SW_CXC2 X WHERE A.REFERENCIA=X.REFERENCIA AND X.TIPO<>'FAC' AND X.APLICABLE<>1 AND X.FECHA<=?) AS ABONOS " +
				" FROM SW_CXC2 A WHERE A.FECHA<=? AND A.TIPO='FAC' AND A.CLIENTE=? ORDER BY A.CLIENTE";
		final Object[] args={fecha,fecha,cliente};
		return getTemplate().query(sql, args, new CXCMapper());
	}
	
	public static JdbcTemplate getTemplate(){
		return ServiceLocator.getJdbcTemplate();
	}
	
	static class CXCMapper implements RowMapper{

		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			CXC cxc=new CXC();
			cxc.setReferencia(rs.getLong("REFERENCIA"));
			return cxc;
		}
		
	};
	
	public static void main(String[] args) {
		List<CXC> cxc=leerCargosAbonos("I059999", DateUtils.obtenerFecha("15/01/2008"));
		System.out.println("CargosAbonos: "+cxc.size());
	}
	

}
