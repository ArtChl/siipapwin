package com.luxsoft.siipap.ventas.servicios;

import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class PolizaDeIngresoModel extends HibernateDaoSupport{
	
	protected final Date fecha;
	private final EventList<Pago> pagos;	
	private final EventList<Venta> ventas;	
	private final EventList<Deposito> depositos;
	private Map<Comparable<Integer>, List<Venta>> ventasPorSucursal;
	private Map<Comparable<Integer>, List<Pago>> pagosPorSucursal;
	private Map<Comparable<Integer>, List<Deposito>> depositosPorSucursal;
	
	
	public PolizaDeIngresoModel(final Date fecha){
		this.fecha=fecha;
		ventas=GlazedLists.threadSafeList(new BasicEventList<Venta>());
		pagos=GlazedLists.threadSafeList(new BasicEventList<Pago>());	
		depositos=GlazedLists.threadSafeList(new BasicEventList<Deposito>());
	}
	
	@SuppressWarnings("unchecked")
	public void load(){
		ventas.clear();
		ventas.addAll(getHibernateTemplate().find("from Venta v left join fetch v.cliente " +
				"c left join fetch c.credito where v.fecha=? " +
				"order by v.origen,v.fecha,v.id", fecha));
		pagos.addAll(getHibernateTemplate().find("" +
				" from Pago  p " +
				" left join fetch p.cliente c " +
				" left join fetch c.credito " +
				" left join fetch p.venta ve" +
				" left join fetch p.nota nt " +
				" where p.fecha=? " +
				" order by p.origen,p.fecha,p.id", fecha));	
		
		depositos.addAll(((DepositosDao)ServiceLocator.getDaoContext().getBean("depositosDao")).buscarDepositosContado(fecha));
		
		
	}
	
	public Date getFecha(){
		return fecha;
	}		
	public EventList<Venta> getVentas(){
		return ventas;
	}	
	public EventList<Pago> getPagos(){
		return pagos;
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
	private List<Venta> getVentasPorSucursal(final Sucursales s){
		if(ventasPorSucursal==null){
			ventasPorSucursal=GlazedLists.syncEventListToMultiMap(ventas,new VentasPorSucursalFuntion() );
		}
		return ventasPorSucursal.get(s.getNumero());
	}	
		
	public EventList<Venta> getVentas(Sucursales s,String tipo){
		final List<Venta> vs=getVentasPorSucursal(s);		
		final FilterList<Venta> res=new FilterList<Venta>(GlazedLists.eventList(vs),Matchers.beanPropertyMatcher(Venta.class, "origen", tipo));
		return res;
	}
	
	/**
	 * Regresa del cache de pagos del dia las que correspondan a la sucursal indicada
	 * 
	 * @param s
	 * @return
	 */
	private List<Pago> getPagosPorSucursal(final Sucursales s){
		if(pagosPorSucursal==null){
			pagosPorSucursal=GlazedLists.syncEventListToMultiMap(pagos,new PagosPorSucursalFuntion() );
		}
		return pagosPorSucursal.get(s.getNumero());
	}
	
	public EventList<Pago> getPagos(Sucursales s,String tipo){
		final List<Pago> ps=getPagosPorSucursal(s);		
		final FilterList<Pago> res=new FilterList<Pago>(GlazedLists.eventList(ps),Matchers.beanPropertyMatcher(Pago.class, "origen", tipo));
		return res;
	}
	
	public double total(final List<Venta> ventas){
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(Venta v:ventas){
			total=total.add(v.getTotal());
		}
		return total.amount().doubleValue();
	}
	public double totalSinIva(final List<Venta> ventas){
		CantidadMonetaria monto=CantidadMonetaria.pesos(total(ventas));
		return monto.divide(1.15).amount().doubleValue();
	}
	public double ivaVentas(final List<Venta> ventas){
		CantidadMonetaria monto=CantidadMonetaria.pesos(totalSinIva(ventas));
		return monto.multiply(.15).amount().doubleValue();
	}
	
	public double totalPagos(final List<Pago> pagos){
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(Pago p:pagos){
			total=total.add(p.getImporte());
		}
		return total.amount().doubleValue();
	}
	public double totalPagosSinIva(final List<Pago> pagos){
		CantidadMonetaria monto=CantidadMonetaria.pesos(totalPagos(pagos));
		return monto.divide(1.15).amount().doubleValue();
	}
	
	public double ivaPagos(final List<Pago> pagos){
		CantidadMonetaria monto=CantidadMonetaria.pesos(totalPagosSinIva(pagos));
		return monto.multiply(.15).amount().doubleValue();
	} 
	
	public double diferenciaEnPagoContraFactura(final List<Pago> pagos){
		CantidadMonetaria monto=CantidadMonetaria.pesos(0);
		for(Pago p:pagos){
			if(p.getFormaDePago().equals("")){
				CantidadMonetaria total=null;
				if(p.getVenta()!=null){
					total=p.getVenta().getTotal();
				}else if(p.getNota()!=null){
					total=p.getNota().getTotalAsMoneda2();
				}else
					continue;
				CantidadMonetaria pago=p.getImporte();
				CantidadMonetaria res=total.subtract(pago);
				monto.add(res);
			}
		}
		return monto.amount().doubleValue();
	}

	
	public EventList<Pago> buscarPagosConTarjeta(final EventList<Pago> pagos){
		return new FilterList<Pago>(pagos,Matchers.beanPropertyMatcher(Pago.class,"formaDePago", "C"));
	}
	
	public double calcularIetu(final List<Pago> pagos){
		//CantidadMonetaria res=0;
		CantidadMonetaria res=CantidadMonetaria.pesos(0);
		for(Pago p:pagos){
			if(p.getVenta()!=null && p.getVenta().getYear()>=2008){
				//res+=p.getImporteAsDouble()/1.15;
				res=res.add(p.getImporte().divide(1.15));
			}else{
				if(p.getNota()!=null && p.getNota().getYear()>=2008){
					//res+=p.getImporteAsDouble()/1.15;
					res=res.add(p.getImporte().divide(1.15));
				}
			}
		}
		return res.amount().doubleValue();
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
	
	
	
	private class VentasPorSucursalFuntion implements Function<Venta, Integer>{
		public Integer evaluate(Venta source) {
			return source.getSucursal();
		}
	}
	
	private class PagosPorSucursalFuntion implements Function<Pago, Integer>{
		public Integer evaluate(Pago source) {
			return source.getSucursal();
		}
	}
	
	private class DepositosPorSucursalFuntion implements Function<Deposito, Integer>{
		public Integer evaluate(Deposito source) {
			return source.getSucursalId();
		}
	}
	
	public CantidadMonetaria anticipos(Sucursales s){		
		final String sql="SELECT SUM(IMPORTE/1.15) AS ANTICIPO" +
				" FROM SW_PAGOS " +
				" WHERE YEAR=?" +
				"  AND FECHA=?" +
				"  AND ORIGEN=\'CAM\' " +
				"  AND FORMADEPAGO IN(\'K\') " + 
				"  AND SUCURSAL=?";
		Object[] vals={Periodo.obtenerYear(fecha),fecha,s.getNumero()};
		Map<String,Number> row=ServiceLocator.getJdbcTemplate().queryForMap(sql, vals, new int[]{Types.INTEGER,Types.DATE,Types.INTEGER});
		Number val=(Number)row.get("ANTICIPO");
		if(val==null)
			return CantidadMonetaria.pesos(0);
		return CantidadMonetaria.pesos(row.get("ANTICIPO").doubleValue());
	}
	 
	
	public static void main(String[] args) {
		PolizaDeIngresoModel model=new PolizaDeIngresoModel(DateUtils.obtenerFecha("30/01/2008"));
		model.setSessionFactory(ServiceLocator.getSessionFactory());
		model.load();
		final List<Venta> ventasCre=model.getVentas(Sucursales.ANDRADE, "CRE");
		final List<Venta> ventasCam=model.getVentas(Sucursales.ANDRADE, "CAM");
		final List<Venta> ventasMos=model.getVentas(Sucursales.ANDRADE, "MOS");
		
		System.out.println("CAM: "+model.total(ventasCam));
		System.out.println("CRE: "+model.total(ventasCre));		
		System.out.println("MOS: "+model.total(ventasMos));
		
		
		final EventList<Pago> pagosCre=model.getPagos(Sucursales.ANDRADE, "CRE");
		final EventList<Pago> pagosCam=model.getPagos(Sucursales.ANDRADE, "CAM");
		final List<Pago> pagosMos=model.getPagos(Sucursales.ANDRADE, "MOS");
		
		System.out.println("Pagos CAM: "+model.totalPagos(pagosCam));
		System.out.println("Pagos CRE: "+model.totalPagos(pagosCre));		
		System.out.println("Pagos MOS: "+model.totalPagos(pagosMos));
		
		final List<Pago> pagosConTarjeta=model.buscarPagosConTarjeta(pagosCam);
		
		for(Pago p:pagosConTarjeta){
			System.out.println(p);
		}
		
		final List<Pago> pagosConTarjetaCre=model.buscarPagosConTarjeta(pagosCre);
		
		for(Pago p:pagosConTarjetaCre){
			System.out.println(p);
		}
		
		//final List<Deposito> depositos=model.getDepositos(Su, origen)
	}

}
