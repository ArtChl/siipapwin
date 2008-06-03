package com.luxsoft.siipap.inventarios.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxp.dao.AnalisisDeEntradaDao;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.dao.CostoPromedioDao;
import com.luxsoft.siipap.inventarios.dao.MovimientoDao;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.InventarioMensual;
import com.luxsoft.siipap.inventarios.domain.Salida;
import com.luxsoft.siipap.inventarios.domain.Transformacion;
import com.luxsoft.siipap.maquila.domain.Bobina;
import com.luxsoft.siipap.services.ServiceLocator;

@SuppressWarnings("unchecked")
public class InventariosManagerQro extends HibernateDaoSupport implements InventariosManager{
	
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
		Assert.isTrue(year>=2007,"Este proceso solo es valido para el 2007 en adelante");
		
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
		final BigDecimal inicial=buscarSaldo(clave,year,mes);
		final BigDecimal saldo=buscarSaldoFinal(clave,year,mes);
		final BigDecimal movs=calcularMovimientos(clave, year, mes);
		im.setMovimientos(movs);
		im.setSaldo(saldo);
		im.setInicial(inicial);
		//final CantidadMonetaria ccostoIni=buscarCostoInicial(clave,year,mes);
		calcularCostoInicial(im);
		//im.setCostoInicialConCUnitario(ccostoIni);		
		if(im.getArticulo() instanceof Bobina){
			calcularCostosMaquilaBobina(im);
			if(im.getCostoMaq().amount().doubleValue()==0){
				calcularCostosMaquilaHojeado(im);
			}
		}else
			calcularCostosMaquilaHojeado(im);
		calcularCostosCxp(im);
		im.actualizar();
		actualizarVentasNetas(im);
		calcularComsSA(im);
		if(im.getCostoPromedio().abs().amount().doubleValue()==0){
			calcularCostosTrs(im);
			im.actualizarCostos();
		}
		return im;
	}
	/**
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
	*/
	
	/**
	 * Regresa los movimientos internos (no COM/FAC/RMD/XRM) en unidades para el periodo
	 * 
	 * @param clave
	 * @param year
	 * @param mes
	 * @return
	 */
	private BigDecimal calcularMovimientos(final String clave,int year,int mes){
		Periodo p=Periodo.getPeriodoEnUnMes(mes-1, year);		
		final String sql="select sum(ALMCANTI/ALMUNIXUNI) as CANTIDAD from sw_almacen2 where ALMARTIC=? and ALMFECHA between ? and ? and ALMTIPO not in(\'COM\',\'FAC\',\'RMD\',\'XRM\')";
		List<Map<String, BigDecimal >> rows=getJdbcTemplate().queryForList(sql
				, new Object[]{clave,p.getFechaInicial(),p.getFechaFinal()}
				, new int[]{Types.VARCHAR,Types.DATE,Types.DATE}		
		);
		BigDecimal saldo=rows.get(0).get("CANTIDAD");
		return saldo!=null?saldo:BigDecimal.ZERO;
	}
	
	/**
	 * Busca el saldo inicial de un articulo para el periodo indicado
	 * 
	 * @param clave
	 * @param year
	 * @param mes
	 * @return
	 */
	private BigDecimal buscarSaldo(final String clave,int year,int mes){
		
		// Inventario inicial del ejercicio
		
		Object[] params={year-1,clave};
		int[] types={Types.INTEGER,Types.VARCHAR};
		String sql="SELECT * FROM SW_INVENTARIO_ANUAL A WHERE A.YEAR=? AND A.CLAVE=?";
		List<Map<String, Object>> rows=getJdbcTemplate().queryForList(sql, params, types);
		BigDecimal inicial=BigDecimal.ZERO;
		if(!rows.isEmpty())
			//return BigDecimal.ZERO;
			inicial=(BigDecimal)rows.get(0).get("SALDO");
		
		if(mes==1){			
			return inicial;	
		}else{
			
			Calendar c=Calendar.getInstance();
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, c.getActualMinimum(Calendar.DATE));
			final Date fechaInicial=c.getTime();
			
			c.set(Calendar.MONTH, mes-2);
			c.getTime();
			int max=c.getActualMaximum(Calendar.DATE);
			c.set(Calendar.DATE, max);
			
			final Date fechaFinal=c.getTime();
			
			String sql2="select sum(ALMCANTI/ALMUNIXUNI) as CANTIDAD from sw_almacen2 where ALMARTIC=? and ALMFECHA between ? and ?";
			System.out.println("PARAMETROS: "+fechaInicial+"  "+fechaFinal);
			List<Map<String, BigDecimal >> rows2=getJdbcTemplate().queryForList(sql2
					, new Object[]{clave,fechaInicial,fechaFinal}
					, new int[]{Types.VARCHAR,Types.DATE,Types.DATE});
			
			BigDecimal saldo=rows2.get(0).get("CANTIDAD");
			if(saldo==null)
				saldo=BigDecimal.ZERO;
			return inicial.add(saldo);
		}
	}
	
	private BigDecimal buscarSaldoFinal(final String clave,int year,int mes){
		
		// Inventario inicial del ejercicio
		
		Object[] params={year-1,clave};
		int[] types={Types.INTEGER,Types.VARCHAR};
		String sql="SELECT * FROM SW_INVENTARIO_ANUAL A WHERE A.YEAR=? AND A.CLAVE=?";
		List<Map<String, Object>> rows=getJdbcTemplate().queryForList(sql, params, types);
		
		BigDecimal inicial=BigDecimal.ZERO;
		if(!rows.isEmpty())			
			inicial=(BigDecimal)rows.get(0).get("SALDO");
			
		Calendar c=Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DATE, c.getActualMinimum(Calendar.DATE));
		final Date fechaInicial=c.getTime();
			
		c.set(Calendar.MONTH, mes-1);
		int max=c.getActualMaximum(Calendar.DATE);
		c.set(Calendar.DATE, max);
		
		final Date fechaFinal=c.getTime();
		
		String sql2="select sum(ALMCANTI/ALMUNIXUNI) as CANTIDAD from sw_almacen2 where ALMARTIC=? and ALMFECHA between ? and ?";
		System.out.println("PARAMETROS: "+fechaInicial+"  "+fechaFinal);
		List<Map<String, BigDecimal >> rows2=getJdbcTemplate().queryForList(sql2
				, new Object[]{clave,fechaInicial,fechaFinal}
				, new int[]{Types.VARCHAR,Types.DATE,Types.DATE});
		
		BigDecimal saldo=rows2.get(0).get("CANTIDAD");
		if(saldo==null)
			saldo=BigDecimal.ZERO;
		return inicial.add(saldo);
		
	}
	
	
	private void calcularCostosMaquilaHojeado(final InventarioMensual im){
		final Periodo p=Periodo.getPeriodoEnUnMes(im.getMes()-1, im.getYear());
		final String sql="select SUM(H.CANTIDAD) AS CANTIDAD" +
				" ,SUM((H.CANTIDAD*G.COSTO)) AS COSTO " +
				" ,SUM((H.PRECIOPORKILOFLETE*US.KILOS*H.CANTIDAD)) AS GASTOS " +
				" ,SUM((US.KILOS*H.CANTIDAD)) AS KILOS " +
				"from SW_MOVI_MAQUILA a LEFT JOIN SW_MOVI_MAQUILA b on(b.entrada=a.movimiento_id) " +
				"JOIN SW_HOJEADO G ON(G.CORTE_ID=B.MOVIMIENTO_ID) " +
				"JOIN SW_HOJEADO H ON(G.HOJEADO_ID=H.ORIGEN_ID) " +
				"JOIN SW_ARTICULOS US ON(US.ID=G.ARTICULO_ID) " +
				"JOIN SW_COMS2 I ON(I.COM_ID=H.COM_ID) " +
				"WHERE I.CLAVE=?  " +
				"and FENT BETWEEN ? AND ? ";
		List<Map<String, Object>> rows=getJdbcTemplate().queryForList(
				sql
				, new Object[]{im.getClave(),p.getFechaInicial(),p.getFechaFinal()}
				, new int[]{Types.VARCHAR,Types.DATE,Types.DATE});
		if(rows.isEmpty())
			return;
		
		final BigDecimal cantidad=(BigDecimal)rows.get(0).get("CANTIDAD");
		final BigDecimal costo=(BigDecimal)rows.get(0).get("COSTO");
		final CantidadMonetaria costom=costo!=null?CantidadMonetaria.pesos(costo.doubleValue()):CantidadMonetaria.pesos(0);
		final BigDecimal kilos=rows.get(0).get("KILOS")!=null?(BigDecimal)rows.get(0).get("KILOS"):BigDecimal.ZERO;
		final BigDecimal gastos=rows.get(0).get("GASTOS")!=null?(BigDecimal)rows.get(0).get("GASTOS"):BigDecimal.ZERO;
		
		im.setMaq(cantidad!=null?cantidad:BigDecimal.ZERO);
		im.setCostoMaq(costom);
		im.setKilosMaq(kilos);
		im.setGastosMaq(new CantidadMonetaria(gastos));
		
	}
	
	
	private void calcularCostosMaquilaBobina(final InventarioMensual im){
		final Periodo p=Periodo.getPeriodoEnUnMes(im.getMes()-1, im.getYear());
		final String sql="select SUM(-B.KILOS) AS CANTIDAD,SUM(-B.KILOS*A.PKILO) AS COSTO " +
				" from SW_MOVI_MAQUILA a " +
				" LEFT JOIN SW_MOVI_MAQUILA b on(b.entrada=a.movimiento_id AND B.TIPO='B') " +
				" JOIN SW_ARTICULOS US ON(US.ID=B.BOBINA_ID) " +
				" JOIN SW_COMS2 I ON(I.COM_ID=B.COM_ID) " +
				" WHERE I.CLAVE=? " +
				" and FENT BETWEEN ? AND ?";
		List<Map<String, Object>> rows=getJdbcTemplate().queryForList(
				sql
				, new Object[]{im.getClave(),p.getFechaInicial(),p.getFechaFinal()}
				, new int[]{Types.VARCHAR,Types.DATE,Types.DATE});
		if(rows.isEmpty())
			return;
		
		final BigDecimal cantidad=(BigDecimal)rows.get(0).get("CANTIDAD");
		final BigDecimal costo=(BigDecimal)rows.get(0).get("COSTO");
		final CantidadMonetaria costom=costo!=null?CantidadMonetaria.pesos(costo.doubleValue()):CantidadMonetaria.pesos(0);		
		im.setMaq(cantidad!=null?cantidad:BigDecimal.ZERO);
		im.setCostoMaq(costom);
		
	}
	
	
	private void calcularCostosCxp(final InventarioMensual im){
		final Periodo p=Periodo.getPeriodoEnUnMes(im.getMes()-1, im.getYear());
		final String sql="SELECT SUM(B.CANTIDAD) AS CANTIDAD ,SUM( ROUND((B.CANTIDAD*(B.NETO*C.TC)),2) ) AS COSTO " +
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
	
	//@Transactional (propagation=Propagation.SUPPORTS)
	private void calcularCostoInicial(final InventarioMensual im){
		final int year=im.getYear();
		final int mes=im.getMes();
		final String clave=im.getClave();
		if(mes==1){
			String sql="SELECT * FROM SW_INVENTARIO_ANUAL A WHERE A.YEAR=? AND A.CLAVE=?";
			List<Map<String, Object>> rows=getJdbcTemplate().queryForList(sql, new Object[]{im.getYear()-1,im.getClave()}, new int[]{Types.INTEGER,Types.VARCHAR});
			if(rows.isEmpty()){
				im.setInicial(BigDecimal.ZERO);
				im.setCostoInicial(CantidadMonetaria.pesos(0));
				return;
			}else{
				Map<String, Object> row=rows.get(0);
				Number ccp=(Number)row.get("COSTO");
				if(ccp!=null && ccp.doubleValue()!=0){
					CantidadMonetaria cto=CantidadMonetaria.pesos(ccp.doubleValue());
					im.setCostoInicial(cto);
				}else{
					im.setInicial(BigDecimal.ZERO);
					im.setCostoInicial(CantidadMonetaria.pesos(0));
				}
			}
			return;
		}
		/*
		if(year==2007 && mes==1){			
			CostoPromedio cp=getCostoPromedioDao().buscarCostoPromedio(clave, "12/2006");
			if(cp!=null){
				if((cp.getCosto()!=null) && (im.getInicial()!=null)){
					final CantidadMonetaria cto=cp.getCosto().multiply(im.getInicial());
					im.setCostoInicial(cto);
				}else{
					im.setInicial(BigDecimal.ZERO);
					im.setCostoInicial(CantidadMonetaria.pesos(0));
				}
			}			
			return;
		}
		*/
		final InventarioMensual im2;
		if(mes==1){
			im2=buscarInventario(year-1, 12, clave);
		}else{
			im2=buscarInventario(year, mes-1, clave);
		}
		im.setCostoInicial(im2!=null?im2.getCosto():CantidadMonetaria.pesos(0));
	}
	
	/**
	 * Regresa una lista de todos los articulos que requieren registro en inventario mensual o anual
	 * 
	 * @return
	 */
	public Collection<String> buscarArticulos(){
		List<Map<String, String>> rows=getJdbcTemplate().queryForList("SELECT DISTINCT ALMARTIC AS CLAVE FROM SW_ALMACEN2 order by almartic desc	");
		final List<String> claves=new ArrayList<String>();
		for(Map<String,String> row:rows){
			claves.add(row.get("CLAVE"));
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
	
	//@Transactional (propagation=Propagation.SUPPORTS, readOnly=false)
	public void actualizarVentasNetas(InventarioMensual im){
		getSession().update(im);
		final Periodo p=Periodo.getPeriodoEnUnMes(im.getMes()-1, im.getYear());
		BigDecimal ventas=ventasNetasUnidades(im.getClave(), p);
		//System.out.println("IM: "+im.getClave()+" VN: "+ventas);
		im.setVentas(ventas);
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
	public void actualizarInventariosSospechosos(final int year,final int mes){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults sr=session.createQuery("from InventarioMensual m " +
						" where  m.year=:year" +
						" and m.mes=:mes" +
						" and m.saldo<>0 " +
						" and m.costoPromedio.amount=0 ")
						.setInteger("year", year)
						.setInteger("mes", mes)						
						.scroll();					
				while(sr.next()){					
					InventarioMensual im=(InventarioMensual)sr.get()[0];					
					calcularCostosTrs(im);					
				}
				return null;
			}
		});		
	}
	
	/**
	 * Trata de asignar costos al inventario mensual buscando datos de transformaciones
	 *  
	 * @param im
	 */
	private void calcularCostosTrs(final InventarioMensual im){
		if(im.getCostoPromedio()==null || im.getCostoPromedio().amount().doubleValue()==0){
			//Buscar el TRS
			Transformacion t =(Transformacion)getSession()
			.createQuery("from Transformacion t where t.claveDestino=:clave and t.year=:year and t.mes=:mes")
			.setString("clave", im.getClave())
			.setInteger("year", im.getYear())
			.setInteger("mes",im.getMes())
			.setMaxResults(1)
			.uniqueResult();
			if(t!=null){
				final Salida s=t.getOrigen();
				
				InventarioMensual source=buscarInventario(Periodo.obtenerYear(s.getALMFECHA()), Periodo.obtenerMes(s.getALMFECHA())+1, s.getALMARTIC());
				if(source!=null){
					final Entrada e=t.getDestino();
					CantidadMonetaria costo=source.getCostoPromedio().multiply(s.getCantidad()).abs();
					CantidadMonetaria cp=costo.divide(e.getCantidad());
					im.setCostoPromedio(cp);
					im.setCosto(im.getCostoPromedio().multiply(im.getSaldo()));
					if(im.getInicial().doubleValue()!=0 ){
						if(im.getCostoInicial().amount().doubleValue()==0){
							CantidadMonetaria costoIni=cp.multiply(im.getInicial());
							im.setCostoInicial(costoIni);
							if(im.getMes()>1){
								//Buscar en el periodo anterior
								final InventarioMensual anterior=buscarInventario(im.getYear(), im.getMes()-1,im.getClave());
								if(anterior!=null){
									anterior.setCostoPromedio(cp);
									anterior.setCosto(cp.multiply(anterior.getSaldo()));
								}
							}
						}
					}
				}
			}
		}
	}
	

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
		/*
		Collection<String> claves=ServiceLocator.getInventariosManager().buscarArticulos();
		
		for(String clave:claves){
			System.out.println("Procesando: "+clave);
			
			for(int i=1;i<=12;i++){				
				InventarioMensual im=ServiceLocator.getInventariosManager().actualizarInventario(2007,i, clave);
				System.out.println("\t"+im);
			}
		}		
		*/
		
		//ServiceLocator.getInventariosManager().actualizarInventario();
		/**
		InventariosManager manager=ServiceLocator.getInventariosManager();
		List<InventarioMensual> ivs=manager.inventarioCosteado();
		
		for(InventarioMensual m:ivs){
			manager.actualizarVentasNetas(m);
		}
		**/
		
		for(int i=1;i<=4;i++){
			InventarioMensual im=ServiceLocator.getInventariosManagerQro().actualizarInventario(2008,i, "CARV");
			System.out.println("\t"+im);
		
		}
		
		//ServiceLocator.getInventariosManager().actualizarInventariosSospechosos(2007, 4);
		
	}

	
	
}
