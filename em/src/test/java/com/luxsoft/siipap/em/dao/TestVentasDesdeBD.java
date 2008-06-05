package com.luxsoft.siipap.em.dao;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Pruebas de integracion con Spring+Hibernate para validar
 * la funcionalidad del bean de Ventas una vez leido desde
 * la base de datos
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class TestVentasDesdeBD extends AbstractTransactionalDataSourceSpringContextTests{
	
	private VentasDao ventaDao;

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"em-dao-context.xml"};
							 
	}
	
	/**
	 * Prueba el monto de los pagos aplicados a una o varias ventas
	 *
	 */
	public void testPagosAplicados(){
		String cliente="U050008";
		int year=2007;
		int mes=1;
		List<Venta> ventas=ventaDao.buscarVentasPorCliente(cliente, year, mes);
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_VENTAS a where a.YEAR=? and a.MES=? and a.CLAVE=?",new Object[]{year,mes,cliente});
		//Contamos que el numero de ventas sea el correcto
		assertEquals(expected, ventas.size());
		
		for(Venta v:ventas){
			
			String sql="select PAGOS from V_VENTAS a where a.VENTA_ID=?";
			List<Map<String, Object>> rows=jdbcTemplate.queryForList(sql,new Object[]{v.getId()});
			assertTrue(rows.size()==1);
			double real=((Number)rows.get(0).get("PAGOS")).doubleValue();
			
			double found=Math.abs(v.getPagos());
			assertEquals(real,found);
			String msg=MessageFormat.format("Venta OK los pagos en Venta_id {0} son {1} y en el Bean: {2}", v.getId(),real,found);
			logger.info(msg);
			//System.out.println(msg);
		}
	}
	
	/**
	 * Prueba el monto de los descuentos aplicados a una o varias ventas
	 *
	 */
	public void testDescuentos(){
		String cliente="U050008";
		int year=2007;
		int mes=1;
		List<Venta> ventas=ventaDao.buscarVentasPorCliente(cliente, year, mes);
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_VENTAS a where a.YEAR=? and a.MES=? and a.CLAVE=?",new Object[]{year,mes,cliente});
		//Contamos que el numero de ventas sea el correcto
		assertEquals(expected, ventas.size());
		
		for(Venta v:ventas){
			
			String sql="select DESCUENTOS from V_VENTAS a where a.VENTA_ID=?";
			List<Map<String, Object>> rows=jdbcTemplate.queryForList(sql,new Object[]{v.getId()});
			assertTrue(rows.size()==1);
			double real=((Number)rows.get(0).get("DESCUENTOS")).doubleValue();
			
			double found=Math.abs(v.getDescuentos());
			assertEquals(real,found);
			String msg=MessageFormat.format("Venta OK los descuentos en Venta_id {0} son {1} y en el Bean: {2}", v.getId(),real,found);
			logger.info(msg);
			//System.out.println(msg);
		}
	}
	
	/**
	 * Prueba el monto del descuento T aplicados a una o varias ventas
	 *
	 */
	public void testDescuentoT(){
		String cliente="U050008";
		int year=2006;
		int mes=11;
		List<Venta> ventas=ventaDao.buscarVentasPorCliente(cliente, year, mes);
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_VENTAS a where a.YEAR=? and a.MES=? and a.CLAVE=?",new Object[]{year,mes,cliente});
		//Contamos que el numero de ventas sea el correcto
		assertEquals(expected, ventas.size());
		
		for(Venta v:ventas){
			
			String sql="select DESCUENTO_T from V_VENTAS a where a.VENTA_ID=?";
			List<Map<String, Object>> rows=jdbcTemplate.queryForList(sql,new Object[]{v.getId()});
			assertTrue(rows.size()==1);
			double real=((Number)rows.get(0).get("DESCUENTO_T")).doubleValue();
			
			double found=Math.abs(v.getDescuentoT());
			assertEquals(real,found);
			String msg=MessageFormat.format("Venta OK el Descuento T en Venta_id {0} son {1} y en el Bean: {2}", v.getId(),real,found);
			logger.info(msg);
			//System.out.println(msg);
		}
	}


	public void setVentaDao(VentasDao ventaDao) {
		this.ventaDao = ventaDao;
	}
	
	

}
