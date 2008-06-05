package com.luxsoft.siipap.em.importar;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

public class VentasSupportTest extends AbstractImportadoTest{
	
	protected VentasSupport support;
	
	public void testBuscarVentasSiipap(){
		final Date date=DateUtils.obtenerFecha("03/05/2007");
		
		//
		final String sql1="SELECT COUNT(*) FROM MOVCRE05 WHERE MCRIDENOPE=1 AND MCRFECHA=#2007/05/03#";
		final String sql2="SELECT COUNT(*) FROM MOCOCA05 WHERE MCAIDENOPE=1 AND MCAFECHA=#2007/05/03#";
		final String sql3="SELECT COUNT(*) FROM MOCOMO05 WHERE MCMIDENOPE=1 AND MCMFECHA=#2007/05/03#";
		int rows=factory.getJdbcTemplate().queryForInt(sql1);
		rows+=factory.getJdbcTemplate().queryForInt(sql2);
		rows+=factory.getJdbcTemplate().queryForInt(sql3);
		System.out.println("Registros: "+rows);
		//int expected real=738
		
		List<Venta> ventas=support.buscarVentasEnSiipap(date);
		int found=ventas.size();
		assertEquals(rows, found);
		
		int year=2007;
		int mes=5;
		
		for(Venta v:ventas){
			assertEquals(year,v.getYear());
			assertEquals(mes,v.getMes());
		}
		
	}

	public void setSupport(VentasSupport support) {
		this.support = support;
	}
	
	
	
	
	

}
