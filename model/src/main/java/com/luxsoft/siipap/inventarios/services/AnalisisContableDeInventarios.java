package com.luxsoft.siipap.inventarios.services;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.InventarioMensual;
import com.luxsoft.siipap.services.ServiceLocator;


public class AnalisisContableDeInventarios{
	
	private InventariosManager manager;
	private JdbcTemplate jdbcTemplate;
	
	@Transactional (propagation=Propagation.SUPPORTS)
	public void analizar(int year,int mes){
		Collection<String> articulos=manager.buscarArticulos();
		for(String articulo:articulos){
			analizar(articulo, year, mes);
			//System.out.println("Analiziando: "+articulo);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional (propagation=Propagation.SUPPORTS)
	public void analizar(final String articulo,final int year,final int mes){
		Periodo per=Periodo.getPeriodoEnUnMes(mes-1, year);		
		final String sql="SELECT SUM(ALMCANTI/ALMUNIXUNI) AS SALDO_FINAL FROM SW_ALMACEN2 WHERE ALMARTIC=? AND ALMFECHA<=?";
		if(articulo.equals("ADHBTE9662")){
			return;
		}
		List<Map<String,Object>> data=getJdbcTemplate().queryForList(
				sql
				,new Object[]{articulo,per.getFechaFinal()}
				,new int[]{Types.VARCHAR,Types.DATE});
		if(data.isEmpty()){
			System.out.println("No existen datos en ALMACEN2 para: "+articulo);
			return;
		}			
		Map<String, Object> almace=data.get(0);
		InventarioMensual im=manager.buscarInventario(year, mes, articulo);
		
		AnalisisContable analisis=new AnalisisContable();
		BigDecimal sfa=(BigDecimal)almace.get("SALDO_FINAL");
		analisis.setSaldoAlmacen(sfa!=null?sfa:BigDecimal.ZERO);
		analisis.setSaldoInventario(im!=null?im.getSaldo():BigDecimal.ZERO);
		analisis.setCostoInventario(im!=null?im.getCosto().amount():BigDecimal.ZERO);
		analisis.setCostoAlmacen(im!=null?im.getCostoPromedio().multiply(analisis.getSaldoAlmacen()).amount():BigDecimal.ZERO);
		
		String pattern="{0} SFI: {1}\tSFA: {2}\t Dif: {3} CI: {4} CA: {5} Dif Cto: {6}";
		
		String res=MessageFormat.format(pattern, articulo
				,analisis.getSaldoInventario()
				,analisis.getSaldoAlmacen()
				,analisis.diferenciaSaldoFinal()
				
				,analisis.getCostoInventario()
				,analisis.getCostoAlmacen()
				,analisis.diferenciaCostoFinal()
				);
		if(
				(analisis.diferenciaCostoFinal().doubleValue()!=0 )
				||(analisis.diferenciaSaldoFinal().doubleValue()!=0)
				)
			System.out.println(res);
	}
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public InventariosManager getManager() {
		return manager;
	}
	public void setManager(InventariosManager manager) {
		this.manager = manager;
	}
	
	
	class AnalisisContable {
		
		private BigDecimal saldoAlmacen=BigDecimal.ZERO;
		private BigDecimal saldoInventario=BigDecimal.ZERO;
		
		private BigDecimal costoAlmacen=BigDecimal.ZERO;
		private BigDecimal costoInventario=BigDecimal.ZERO;
		
		
		public BigDecimal getSaldoAlmacen() {
			return saldoAlmacen;
		}
		public void setSaldoAlmacen(BigDecimal saldoAlmacen) {
			this.saldoAlmacen = saldoAlmacen;
		}
		public BigDecimal getSaldoInventario() {
			return saldoInventario;
		}
		public void setSaldoInventario(BigDecimal saldoInventario) {
			this.saldoInventario = saldoInventario;
		}
		
		
		
		public BigDecimal getCostoAlmacen() {
			return costoAlmacen;
		}
		public void setCostoAlmacen(BigDecimal costoAlmacen) {
			this.costoAlmacen = costoAlmacen;
		}
		public BigDecimal getCostoInventario() {
			return costoInventario;
		}
		public void setCostoInventario(BigDecimal costoInventario) {
			this.costoInventario = costoInventario;
		}
		
		public BigDecimal diferenciaSaldoFinal(){
			return getSaldoAlmacen().subtract(getSaldoInventario());
		}
		
		public BigDecimal diferenciaCostoFinal(){
			return getCostoAlmacen().subtract(getCostoInventario());
		}
		
	}
	
	public static void main(String[] args) {
		AnalisisContableDeInventarios am=(AnalisisContableDeInventarios)ServiceLocator.getDaoContext().getBean("analisisContableInventarios");		
		am.analizar(2007, 1);
	}

	

}
