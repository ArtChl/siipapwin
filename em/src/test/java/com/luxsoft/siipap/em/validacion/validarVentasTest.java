package com.luxsoft.siipap.em.validacion;

import junit.framework.TestCase;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.managers.EMServiceLocator;

public class validarVentasTest extends TestCase{
	
	private Periodo periodo;
	private Validador ventasValidator;
	
	public void setUp(){
		//Septiembre
		periodo=Periodo.getPeriodoEnUnMes(8);
		ventasValidator=new VentasValidator();
	}
	
	
	
	public void testVentasCreditoEnSiipap(){
		
		String sql="SELECT COUNT(*) FROM MOVCRE09 WHERE MCRIDENOPE=1";
		int registros=EMServiceLocator.instance()
			.getSiipapTemplateFactory().getJdbcTemplate().queryForInt(sql);
		
		int found=ventasValidator.contarRegistrosEnSiipap(periodo);
		assertEquals("Los registros no checan",registros, found);
	}
	
	public void testVentasCreditoEnSiipapWin(){
		final String sql="SELECT COUNT(*) FROM SW_VENTAS WHERE YEAR=2007 AND MES=9 AND ORIGEN=\'CRE\'";
		int expected=EMServiceLocator.instance().getDefaultJdbcTemplate().queryForInt(sql);
		System.out.println(sql);
		int actual=ventasValidator.contarRegistrosEnSiipapWin(periodo);
		assertEquals("Los registros en siipapwin no chean",expected, actual);
	}
	
}
