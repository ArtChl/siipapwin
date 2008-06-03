package com.luxsoft.siipap.inventarios.services;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.ScrollableResults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxp.dao.AnalisisDeEntradaDao;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.dao.CostoPromedioDao;
import com.luxsoft.siipap.inventarios.dao.MovimientoDao;

import com.luxsoft.siipap.inventarios.domain.InventarioMensual;
import com.luxsoft.siipap.services.ServiceLocator;

@SuppressWarnings("unchecked")
public class InventariosManagerQro_old extends HibernateDaoSupport implements InventariosManager{
	
	private AnalisisDeEntradaDao analisisDeEntradaDao;
	private ArticuloDao articuloDao;
	private MovimientoDao movimientoDao;
	private CostoPromedioDao costoPromedioDao;
	private JdbcTemplate jdbcTemplate;

	public List<AnalisisDeEntrada> buscarEntradasCom(final Periodo p,final String clave) {		
		return getAnalisisDeEntradaDao().buscarEntradasPorCompra(p,clave);
	}
	
	/**
	 * Localiza el inventario mensual para el articulo indicado
	 * 
	 * @param year
	 * @param mes
	 * @param clave
	 * @return
	 */
	//@Transactional (propagation=Propagation.SUPPORTS)
	public InventarioMensual buscarInventario(final int year,final int mes, final String clave){
		List list=getSession().createQuery(
				"from InventarioMensual i  left join fetch i.articulo a " +
				"where i.clave=:clave and i.year=:year and i.mes=:mes")
				.setString("clave", clave)
				.setInteger("year", year)
				.setInteger("mes", mes)
				.list();
		return list.isEmpty()?null:(InventarioMensual)list.get(0);
	}
	
	
	/**
	 * Actualiza el inventario costeado mensual
	 * Toma en consideracion que el inicio del periodo es enero y que el primer año de
	 * operacion es el 2007. Regresa nulo si el articulo no es inventariable
	 * 
	 * 
	 * @param year
	 * @param mes
	 * @param clave
	 * @return
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public InventarioMensual actualizarInventario(final int year,final int mes,final String clave){		
		System.out.println("Procesando articulo: "+clave+" Year: "+year+" Mes: "+mes);
		InventarioMensual im=buscarInventario(year, mes, clave);
		if(im==null){
			Articulo a=getArticuloDao().buscarPorClaveConUnidades(clave);
			im=new InventarioMensual(year,mes,a);
			im.setYear(year);
			im.setMes(mes);
			im.setFactor(a.getUnidad().getCantidad());
		}
		if(!im.getArticulo().isAfectaInventario()){
			return null;
		}
		getSession().saveOrUpdate(im);
		final BigDecimal inicial=buscarSaldo(clave,year,mes-1);
		final BigDecimal saldo=buscarSaldo(clave,year,mes);
		im.setSaldo(saldo);
		im.setInicial(inicial);
		calcularCostoInicial(im);
		calcularCostosCxp(im);
		im.actualizar();
		actualizarVentasNetas(im);
		calcularComsSA(im);	
		return im;
	}
	
	private void calcularCostoInicial(final InventarioMensual im){
		final int year=im.getYear();
		final int mes=im.getMes();
		final String clave=im.getClave();
		InventarioMensual im2=buscarInventario(year, mes-1, clave);
		im.setCostoInicial(im2!=null?im2.getCosto():CantidadMonetaria.pesos(0));
	}
	
	
	private BigDecimal buscarSaldo(final String clave,int year,int mes){
		Calendar c=Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, mes-1);
		int max=c.getActualMaximum(Calendar.DATE);
		c.set(Calendar.DATE, max);		
		final String sql="select sum(ALMCANTI/ALMUNIXUNI) as CANTIDAD from sw_almacen2 where ALMARTIC=? and ALMFECHA<=?";
		List<Map<String, BigDecimal >> rows=getJdbcTemplate().queryForList(sql, new Object[]{clave,c.getTime()}, new int[]{Types.VARCHAR,Types.DATE});
		BigDecimal saldo=rows.get(0).get("CANTIDAD");
		return saldo!=null?saldo:BigDecimal.ZERO;
	}
	
		
	private void calcularCostosCxp(final InventarioMensual im){
		final Periodo p=Periodo.getPeriodoEnUnMes(im.getMes()-1, im.getYear());
		final String sql="SELECT SUM(B.CANTIDAD) AS CANTIDAD ,SUM(B.CANTIDAD*(B.NETO*C.TC)) AS COSTO " +
				" FROM SW_COMS2 I JOIN SW_ANALISISDET B ON(I.COM_ID=B.COM_ID) " +
				"JOIN SW_ANALISIS C ON(B.ANALISIS_ID=C.ANALISIS_ID) " +
				"WHERE  I.CLAVE=? " +
				"AND FENT BETWEEN ? AND ?";
		List<Map<String, Object>> rows=getJdbcTemplate().queryForList(
				sql
				, new Object[]{im.getClave(),p.getFechaInicial(),p.getFechaFinal()}
				, new int[]{Types.VARCHAR,Types.DATE,Types.DATE});
		if(rows.isEmpty())
			return;
		
		final BigDecimal cantidad=(BigDecimal)rows.get(0).get("CANTIDAD");
		final BigDecimal costo=(BigDecimal)rows.get(0).get("COSTO");
		final CantidadMonetaria costom=costo!=null?CantidadMonetaria.pesos(costo.doubleValue()):CantidadMonetaria.pesos(0);
		
		im.setCxp(cantidad!=null?cantidad:BigDecimal.ZERO);
		im.setCostoCxp(costom);
	}
	
	/**
	 * 
	 * @return
	 */
	public Collection<String> buscarArticulos(){
		final String sql="SELECT DISTINCT ALMARTIC FROM SW_ALMACEN2 WHERE ALMFECHA>='01/01/06'  order by ALMARTIC desc";
		List<Map<String, String>> rows=getJdbcTemplate().queryForList(sql);
		final Set<String> claves=new TreeSet<String>();		
		for(Map<String, String> row:rows){
			claves.add(row.get("ALMARTIC"));
		}
		return claves;
	}
	
	
	/**
	 * Carga todos los registros del inventario inicial
	 * 
	 * @return
	 */	
	public List<InventarioMensual> inventarioCosteado(){
		final List<InventarioMensual> list=new ArrayList<InventarioMensual>();
		ScrollableResults sc=getSession().createQuery("from InventarioMensual m left join fetch m.articulo").scroll();
		int count=0;
		while(sc.next()){			
			InventarioMensual im=(InventarioMensual)sc.get()[0];
			list.add(im);
			while(count++%20==0){
				getSession().flush();
				getSession().clear();
			}
		}
		return list;
	}
	

	public void actualizarVentasNetas(InventarioMensual im){
		getSession().update(im);
		final Periodo p=Periodo.getPeriodoEnUnMes(im.getMes()-1, im.getYear());
		BigDecimal ventas=ventasNetasUnidades(im.getClave(), p);
		im.setVentas(ventas);
	}
	
	/**
	 * FAC-RMD+XRM
	 * 
	 * @param clave
	 * @param p
	 * @return
	 */
	public BigDecimal ventasNetasUnidades(final String clave,final Periodo p){
		String sql="select sum(ALMCANTI/ALMUNIXUNI) as cantidad " +
				"from sw_almacen2 where almartic=? and almfecha between ? and ?  and ALMTIPO in(?,?,?)";
		final BigDecimal facs=(BigDecimal)getJdbcTemplate().queryForObject(
				sql
				,new Object[]{clave,p.getFechaInicial(),p.getFechaFinal(),"FAC","RMD","XRM"}
				,new int[]{Types.VARCHAR,Types.DATE,Types.DATE,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR},BigDecimal.class);
		return facs!=null?facs:BigDecimal.ZERO;		
	}
	
	
	
	
	private void calcularComsSA(final InventarioMensual im){
		final Periodo p=Periodo.getPeriodoEnUnMes(im.getMes()-1, im.getYear());
		final String sql="select SUM(a.ingresada-nvl((select sum(cantidad) as CANTIDAD " +
				" from sw_analisisdet b where b.com_id=a.com_id),0) " +
				"+ nvl((select sum(kilos) from SW_MOVI_MAQUILA c where c.com_id=a.com_id),0) " +
				"- nvl((select sum(cantidad) from sw_hojeado d where d.com_id=a.com_id),0)) AS CANTIDAD " +
				"from sw_coms2 a JOIN SW_MVCOMP ON(SUCURSAL=MVCSUCUR AND MVCNUMER=COM AND MVCFACREM=FACREM) " +
				"where  A.CLAVE=? AND A.FENT BETWEEN ? AND ?" +
				" AND a.ingresada-nvl((select sum(cantidad) from sw_analisisdet b where b.com_id=a.com_id),0) + nvl((select sum(kilos) from SW_MOVI_MAQUILA c where c.com_id=a.com_id),0) - nvl((select sum(cantidad) from sw_hojeado d where d.com_id=a.com_id),0)>0";
		List<Map<String, Object>> rows=getJdbcTemplate().queryForList(
				sql
				, new Object[]{im.getClave(),p.getFechaInicial(),p.getFechaFinal()}
				, new int[]{Types.VARCHAR,Types.DATE,Types.DATE});
		if(rows.isEmpty())
			return ;
		BigDecimal cantidad=(BigDecimal)rows.get(0).get("CANTIDAD");
		im.setComsSinAnalizar(cantidad!=null?cantidad:BigDecimal.ZERO);
	}
	
	/**
	 * Busca posibles registros de InventarioMensual que tengan saldo <>0 y costo promedio=0
	 * para tratar de asignarles un costo
	 * 
	 * @param year
	 * @param mes
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void actualizarInventariosSospechosos(final int year,final int mes){}
	

	public AnalisisDeEntradaDao getAnalisisDeEntradaDao() {
		return analisisDeEntradaDao;
	}

	public void setAnalisisDeEntradaDao(AnalisisDeEntradaDao analisisDeEntradaDao) {
		this.analisisDeEntradaDao = analisisDeEntradaDao;
	}

	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}

	public MovimientoDao getMovimientoDao() {
		return movimientoDao;
	}

	public void setMovimientoDao(MovimientoDao movimientoDao) {
		this.movimientoDao = movimientoDao;
	}
	
	public CostoPromedioDao getCostoPromedioDao() {
		return costoPromedioDao;
	}

	public void setCostoPromedioDao(CostoPromedioDao costoPromedioDao) {
		this.costoPromedioDao = costoPromedioDao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public static void main(String[] args) throws Exception{
		
		
		final InventariosManager manager=ServiceLocator.getInventariosManagerQro();
		
		Collection<String> claves=manager.buscarArticulos();
		System.out.println("Procesando claves: "+claves.size());
		for(String clave:claves){
			System.out.println("Procesando: "+clave);
			for(int i=11;i<=12;i++){				
				InventarioMensual im=manager.actualizarInventario(2006,i, clave);
				System.out.println("\t"+im);
			}
		}		
		
		/**
		for(int i=1;i<=9;i++){
			InventarioMensual im=manager.actualizarInventario(2007,i, "ADH2C");
		}
		**/
		
	}

	
	
}
