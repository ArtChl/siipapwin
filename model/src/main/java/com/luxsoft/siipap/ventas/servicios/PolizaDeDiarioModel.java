package com.luxsoft.siipap.ventas.servicios;

import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.FunctionList.Function;
import ca.odell.glazedlists.matchers.Matchers;

import com.luxsoft.siipap.cxc.dao.DepositosDao;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Sucursales;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * 
 * Model de datos para la exportacion de poliza de ingreso a contabilidad
 * 
 * @author Ruben Cancino 
 *
 */
@SuppressWarnings("unchecked")
public class PolizaDeDiarioModel extends HibernateDaoSupport{
	
	private JdbcTemplate jdbcTemplate;
	protected final Date fecha;
	//private final EventList<Pago> pagos;
	private final EventList<Deposito> depositos;	
	private Map<Comparable<Integer>, List<Pago>> pagosPorSucursal;
	private Map<Comparable<Integer>, List<Deposito>> depositosPorSucursal;
	
	
	public PolizaDeDiarioModel(final Date fecha){
		this.fecha=fecha;		
		//pagos=GlazedLists.threadSafeList(new BasicEventList<Pago>());	
		depositos=GlazedLists.threadSafeList(new BasicEventList<Deposito>());
	}
	
	@SuppressWarnings("unchecked")
	public void load(){
		depositos.addAll(((DepositosDao)ServiceLocator.getDaoContext().getBean("depositosDao")).buscarDepositosCamioneta(fecha));
	}
	
	public Date getFecha(){
		return fecha;
	}		
	
	public EventList<Deposito> getDepositos(){
		return depositos;
	}
	
	/**
	 * Regresa del cache de ventas del dia las que correspondan a la sucursal indicada
	 * 
	 * @param s
	 * @return
	 */
	private List<Deposito> getDepositosPorSucursal(final Sucursales s){
		if(depositosPorSucursal==null){
			depositosPorSucursal=GlazedLists.syncEventListToMultiMap(depositos,new DepositosPorSucursalFuntion() );
		}
		return depositosPorSucursal.get(s.getNumero());
	}	
	
	public List<Deposito> getDepositos(final Sucursales s, String origen){
		final List<Deposito> ds=getDepositosPorSucursal(s);	
		return new FilterList<Deposito>(GlazedLists.eventList(ds),Matchers.beanPropertyMatcher(Deposito.class,"origen", origen));
	}
	
		
	public CantidadMonetaria calcularIvaEnVentas(Sucursales s){		
		final String sql="SELECT SUM((IMPORTE/1.15)*.15)AS IVA" +
				" FROM SW_PAGOS " +
				" WHERE YEAR=?" +
				"  AND FECHA=?" +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO IN(\'E\',\'H\',\'O\',\'N\') " +
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number res=(Number)row.get("IVA");
		return res!=null?CantidadMonetaria.pesos(res.doubleValue()):CantidadMonetaria.pesos(0); 
		//row.isEmpty()?CantidadMonetaria.pesos(0):CantidadMonetaria.pesos(row.get("IVA").doubleValue());
	}
	 /*
	public CantidadMonetaria calcularIvaPagoConAnticipo(Sucursales s){		
		final String sql="SELECT SUM((IMPORTE/1.15)*.15) AS IVA" +
				" FROM SW_PAGOS " +
				" WHERE YEAR=?" +
				"  AND FECHA=?" +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO IN(\'A\') " +
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("IVA");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("IVA").doubleValue());
	}
	*/
	public CantidadMonetaria ietu(Sucursales s){		
		final String sql="SELECT SUM(a.IMPORTE/1.15) AS IETU FROM SW_PAGOS a  " +
				"WHERE a.YEAR=?  AND a.FECHA=?  AND a.ORIGEN=\'CAM\'  " +
				"  AND a.FORMADEPAGO IN(\'H\',\'N\',\'E\',\'O\') " +
				"  AND a.SUCURSAL=? " +
				"  and a.venta_id in(select x.venta_id from sw_ventas x where a.venta_id=x.venta_id  and x.year>=2008)";		
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("IETU");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("IETU").doubleValue());
	}
	
	public CantidadMonetaria anticipos(Sucursales s){		
		final String sql="SELECT SUM(IMPORTE/1.15) AS ANTICIPO" +
				" FROM SW_PAGOS " +
				" WHERE YEAR=?" +
				"  AND FECHA=?" +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO IN(\'A\') " + 
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("ANTICIPO");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("ANTICIPO").doubleValue());
	}
	
	
	public CantidadMonetaria calcularIvaAnticipos(Sucursales s){		
		final String sql="SELECT SUM((IMPORTE/1.15)*.15) AS IVA" +
				" FROM SW_PAGOS " +
				" WHERE YEAR=?" +
				"  AND FECHA=?" +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO IN(\'A\') " +
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("IVA");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("IVA").doubleValue());
	}
	
	public CantidadMonetaria calcularOtrosProductos(Sucursales s){		
		final String sql="SELECT SUM(IMPORTE) AS TOT" +
				" FROM SW_PAGOS " +
				" WHERE YEAR=?" +
				"  AND FECHA=?" +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO  IN (\'U\')" +
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("TOT");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("TOT").doubleValue());
	}
	
	
	
	public CantidadMonetaria calcularPagosCamioneta(Sucursales s){		
		final String sql="SELECT sum(IMPORTE) AS TOT " +
				"FROM SW_PAGOS " +
				" WHERE YEAR=? " +
				"  AND FECHA=? " +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO NOT IN (\'U\',\'C\') " +
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("TOT");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("TOT").doubleValue());
	}
	
	public CantidadMonetaria calcularDevolucionesCam(Sucursales s){		
		final String sql="SELECT SUM(IMPORTE) as TOT FROM SW_NOTAS WHERE YEAR=?  AND ORIGEN=\'CAM\' AND TIPO=\'I\' " +
				"AND FECHA=? AND SUCURSALDEVO=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("TOT");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("TOT").doubleValue()).abs();
	}
	
	public CantidadMonetaria pagoConOtros(Sucursales s){		
		final String sql="SELECT sum(IMPORTE) AS TOT " +
				"FROM SW_PAGOS " +
				" WHERE YEAR=? " +
				"  AND FECHA=? " +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO IN (\'S\') " +
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("TOT");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("TOT").doubleValue());
	}
	
	public CantidadMonetaria calcularPagosConTarjetaCamioneta(Sucursales s){
		final String sql="SELECT sum(IMPORTE) AS TOT " +
		"FROM SW_PAGOS " +
		" WHERE YEAR=? " +
		"  AND FECHA=? " +
		"  AND ORIGEN=\'CAM\' " +
		"  AND FORMADEPAGO IN (\'C\') " +
		"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("TOT");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("TOT").doubleValue());
	}
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	private class DepositosPorSucursalFuntion implements Function<Deposito, Integer>{
		public Integer evaluate(Deposito source) {
			return source.getSucursalId();
		}
	}

	public static void main(String[] args) {
		final PolizaDeDiarioModel model=new PolizaDeDiarioModel(DateUtils.obtenerFecha("07/04/2008"));
		model.setSessionFactory(ServiceLocator.getSessionFactory());
		model.setJdbcTemplate(ServiceLocator.getJdbcTemplate());
		model.load();
		System.out.println("Iva en Ventas: "+model.calcularIvaEnVentas(Sucursales.ANDRADE));
		//System.out.println("Iva en Pago con Anticipos: "+model.calcularIvaPagoConAnticipo(Sucursales.ANDRADE));
		System.out.println("Ietu: "+model.ietu(Sucursales.ANDRADE));
		System.out.println("Anticipo: "+model.anticipos(Sucursales.ANDRADE));
		System.out.println("Iva en Anticipos: "+model.calcularIvaAnticipos(Sucursales.ANDRADE));
		CantidadMonetaria devos=model.calcularDevolucionesCam(Sucursales.ANDRADE);
		System.out.println("Total Devoluciones : "+devos);
		System.out.println("Total Devoluciones : "+devos.multiply(.15));
		System.out.println("Total Pagos camioneta (Clientes): "+model.calcularPagosCamioneta(Sucursales.ANDRADE));
		double otrosProductos=model.calcularOtrosProductos(Sucursales.ANDRADE).amount().doubleValue();
		double pagoConOtros=model.pagoConOtros(Sucursales.ANDRADE).amount().doubleValue();
		double otrosIngresos=(otrosProductos-pagoConOtros);
		System.out.println("Total OtrosProductos : "+otrosProductos);
		System.out.println("Total PagoConOtros:    "+pagoConOtros);
		System.out.println("OtrosIngresos: "+otrosIngresos);
		System.out.println("Iva de Otros ingresos"+(otrosIngresos/1.15)*.15);
	}

}
