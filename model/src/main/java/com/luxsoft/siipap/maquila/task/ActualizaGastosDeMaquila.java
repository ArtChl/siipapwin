package com.luxsoft.siipap.maquila.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Actualiza los gastos de maquila prorrateando un valor entre los kilos totales del periodo
 * y colocando el dato en la columna de PRECIOPORKILOFLETE de SW_HOJEADO
 * 
 * @author Ruben Cancino
 *
 */
public class ActualizaGastosDeMaquila extends HibernateDaoSupport{
	
	
	@SuppressWarnings("unchecked")
	public void execute(final Periodo p,final BigDecimal importe){
		int[] types={Types.DATE,Types.DATE};
		Object[] values={p.getFechaInicial(),p.getFechaFinal()};
		
		//Obtener el importe de kilos
		String sql="SELECT SUM(B.KILOS*CANTIDAD) as KILOS " +
				"FROM SW_HOJEADO A JOIN SW_ARTICULOS B ON(A.ARTICULO_ID=B.ID) WHERE A.TIPO='S' AND FECHA BETWEEN ? AND ?";
		List<Map<String,BigDecimal>> row=ServiceLocator.getJdbcTemplate().queryForList(sql,values,types);
		BigDecimal kilos=row.get(0).values().iterator().next();
		
		System.out.println("Kilos del periodo: "+kilos);
		BigDecimal costo=importe.divide(kilos,4,RoundingMode.HALF_EVEN);			
		System.out.println("Costo por flete: "+costo);
		
		
		String sql2="UPDATE SW_HOJEADO SET PRECIOPORKILOFLETE=@COSTO WHERE TIPO=\'S\' AND FECHA BETWEEN ? AND ?";
		sql2=sql2.replaceAll("@COSTO", costo.toString());
		System.out.println(" Ejecutando update: "+sql2);
		ServiceLocator.getJdbcTemplate().update(sql2,values,types);
	}
	
	public static void main(String[] args) {
		ActualizaGastosDeMaquila m=new ActualizaGastosDeMaquila();
		m.setSessionFactory(ServiceLocator.getSessionFactory());
		
		/**
		m.execute(Periodo.getPeriodoEnUnMes(0,2008), new BigDecimal(415954.12));
		m.execute(Periodo.getPeriodoEnUnMes(1,2008), new BigDecimal(440308.96));
		m.execute(Periodo.getPeriodoEnUnMes(2,2008), new BigDecimal(276220.44));
		**/
		
		m.execute(Periodo.getPeriodoEnUnMes(0,2008), new BigDecimal(340598.07));
		m.execute(Periodo.getPeriodoEnUnMes(1,2008), new BigDecimal(299800.72));
		m.execute(Periodo.getPeriodoEnUnMes(2,2008), new BigDecimal(219017.28));
		m.execute(Periodo.getPeriodoEnUnMes(3,2008), new BigDecimal(232275.37));
		m.execute(Periodo.getPeriodoEnUnMes(4,2008), new BigDecimal(229420.18));
		m.execute(Periodo.getPeriodoEnUnMes(5,2008), new BigDecimal(158250.10));
		m.execute(Periodo.getPeriodoEnUnMes(6,2008), new BigDecimal(415430.24));
		m.execute(Periodo.getPeriodoEnUnMes(7,2008), new BigDecimal(221960.85));
		
		
			
		
	}

}
