package com.luxsoft.siipap.cxc.managers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.FunctionList.Function;

import com.luxsoft.siipap.cxc.CXCPermiso;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.PagoDao;
import com.luxsoft.siipap.cxc.dao.ProvisionDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.NotasFactory;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.dao.DevolucionDao;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
public class NotasManagerImpl extends HibernateDaoSupport implements NotasManager{
	
	private NotaDeCreditoDao notaDeCreditoDao;
	private DevolucionDao devolucionDao;
	private PagoDao pagoDao;
	private ProvisionDao provisionDao;
	private VentasDao ventasDao;
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void eliminar(final NotaDeCredito nota){
		if(tienePermiso(CXCPermiso.EliminarNotasUsadasEnPagos)){
			List<Pago> pagos=buscarPagosAplicados(nota);
			if(!pagos.isEmpty()){
				eliminarPagosAplicados(pagos);
			}
		}else
			validarNotaSinPagos(nota);
		
		if(nota.getDevolucion()!=null){
			getSession().update(nota.getDevolucion()); //Reatach make persistent
			Devolucion d=nota.getDevolucion();
			d.setCortes(0);
			for(DevolucionDet det:d.getPartidas()){
				det.setNota(null);
				det.setCxcnumero(0);
				det.setTipocxc(null);
				
			}
			d.actualizarImporte();
			getDevolucionDao().salvar(d);
			nota.setDevolucion(null);
		}
		getNotaDeCreditoDao().eliminar(nota);
		
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public void validarNotaSinPagos(final NotaDeCredito nota){
		List<Pago> pagos=buscarPagosAplicados(nota);
		if(!pagos.isEmpty()){
			String msg=MessageFormat.format("La nota {0} tiene {1} pago(s)  aplicado(s) no se puede eliminar", nota.getId(),pagos.size());
			throw new RuntimeException(msg);
		}
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Pago> buscarPagosAplicados(final NotaDeCredito nota) {
		List<Pago> pagos=getPagoDao().buscarPagosConNota(nota);
		return pagos;
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<NotaDeCredito> buscarNotasCre(Periodo p){
		return getNotaDeCreditoDao().buscarNotasCre(p);
	}
	
	
	
	public NotaDeCredito buscarNota(Long id) {
		return getNotaDeCreditoDao().buscar(id);
	}

	public NotaDeCredito buscarNotaDevolucion(Long id) {
		return getNotaDeCreditoDao().buscarDevolucion(id);
	}

	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Provision> buscarProvisiones(){
		return getProvisionDao().buscarProvisiones();
	}
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void eliminarPagosAplicados(List<Pago> pagos){
		for(Pago p:pagos){
			getPagoDao().eliminar(p);
		}
	}
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void salvarNotasCre(final List<NotaDeCredito> notas){
		for(NotaDeCredito n:notas){
			salvarNotaCre(n);
		}
	}
	
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void salvarNotaCre(final NotaDeCredito nota) {
		
		if(nota.getTipo().equals("J"))
			throw new IllegalArgumentException("Este metodo no es para salvar N.C. por devolucion");
		//Salvar una nota de credito tiene algunas implicaciones este es el lugar de asegura la regla de negocio
		
		//Validar que el numero de partidas no exceda 9
		Assert.isTrue(nota.getPartidas().size()<=11,"El número máximo de partidas en una nota de credito es 9");
		nota.actualizar();
		getSession().update(nota.getCliente());
		nota.setClave(nota.getCliente().getClave());
		if(nota.getSerie().equals("M")){
			nota.setImporte(nota.getImporte().abs());
		}else{
			nota.setImporte(nota.getImporte().multiply(-1));
		}		
		//Garantizar que el mes y el año esten coordinados con la fecha de la nota
		
		int mes=Periodo.obtenerMes(nota.getFecha())+1;
		int year=Periodo.obtenerYear(nota.getFecha());
		nota.setYear(year);
		nota.setMes(mes);
		
		for(NotasDeCreditoDet det:nota.getPartidas()){
			det.setClave(nota.getClave());
			det.setOrigen(nota.getOrigen());
			det.setYear(nota.getYear());
			det.setMes(nota.getMes());
			det.setSerie(nota.getSerie());
			det.setTipo(nota.getTipo());
			det.setFecha(nota.getFecha());
			det.setNumero(nota.getNumero());			
			if(det.getFactura()!=null){
				Venta v=det.getFactura();
				det.setClave(nota.getClave());
				det.setNumDocumento(v.getNumero());
				det.setTipoDocumento(v.getTipo());
				det.setFechaDocumento(v.getFecha());
				det.setSerieDocumento(v.getSerie());
				det.setSucDocumento(v.getSucursal());
				det.setSaldoFactura(v.getSaldoEnMoneda());
				if(det.getSerie().equals("M")){
					det.setImporte(det.getImporte().abs());
					det.setSaldoFactura(det.getTotal());
				}else
					det.setImporte(det.getImporte().abs().multiply(-1));
			}
			det.setMes(mes);
			det.setYear(year);
		}
		nota.actualizar();	
		if(nota.getTipo().equals("U")){
			validarNotaPorDescuento(nota);
		}
		if(NotasFactory.isBonificacion(nota)){
			for(NotasDeCreditoDet det:nota.getPartidas()){
				if(det.getFactura().getSaldo().abs().doubleValue()<=1)
					nota.setAplicable(true);
				if(det.getDescuento()==100){					
					//BigDecimal saldo=det.getSaldoFactura().amount().abs();
					//BigDecimal saldo=det.getSaldoFactura()
					if(det.getFactura()!=null){
						BigDecimal total=BigDecimal.valueOf(det.getFactura().getTotalSinDevolucionesAsDouble());
						BigDecimal importe=det.getImporte().amount().abs();
						BigDecimal desc=importe.divide(total, 4, RoundingMode.HALF_EVEN);
						//BigDecimal res=BigDecimal.valueOf(desc).mul
						det.setDescuentoSilently(desc.doubleValue()*100);
						//det.setDescuento(0);
					}
					
				}
			}
		}
		getNotaDeCreditoDao().salvar(nota);
		
		/**
		for(NotasDeCreditoDet det:nota.getPartidas()){			
			Provision p=det.getFactura().getProvision();
			if(p!=null){
				p.setAplicado(true);
				getProvisionDao().salvar(p);
			}
		}
		**/
	}
	
	
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void salvarNCDevoCRE(final List<NotaDeCredito> notas){
		Assert.isTrue(notas.size()<=2,"Solo puede haber hasta 2 notas por devolucion");
		for(NotaDeCredito nota:notas){
			
			//Garantizar que el mes y el año esten coordinados con la fecha de la nota
			int mes=Periodo.obtenerMes(nota.getFecha())+1;
			int year=Periodo.obtenerYear(nota.getFecha());
			nota.setYear(year);
			nota.setMes(mes);			
			
			for(NotasDeCreditoDet det:nota.getPartidas()){
				if(det.getImporte().amount().doubleValue()>0)
					det.setImporte(det.getImporte().multiply(-1));
				det.setClave(nota.getClave());
				det.setOrigen(nota.getOrigen());
				det.setYear(nota.getYear());
				det.setMes(nota.getMes());
				det.setSerie(nota.getSerie());
				det.setTipo(nota.getTipo());
				det.setFecha(nota.getFecha());
				det.setNumero(nota.getNumero());			
				if(det.getFactura()!=null){
					Venta v=det.getFactura();
					det.setClave(nota.getClave());
					det.setNumDocumento(v.getNumero());
					det.setTipoDocumento(v.getTipo());
					det.setFechaDocumento(v.getFecha());
					det.setSerieDocumento(v.getSerie());
					det.setSucDocumento(v.getSucursal());
					det.setSaldoFactura(v.getSaldoEnMoneda());					
				}
				if(det.getFactura().getSaldo().abs().doubleValue()==0)
					nota.setAplicable(true);
			}
			nota.actualizar();			
			getSession().saveOrUpdate(nota);
			
		}
		
		getSession().update(notas.get(0).getDevolucion());
		
	}
	
	private void validarNotaPorDescuento(final NotaDeCredito nota){
		for(NotasDeCreditoDet det:nota.getPartidas()){
			double val=det.getFactura().getDescuento1();
			Assert.isTrue(val==0,"Ya existe un descuento  1 para la factura con id: "+det.getFactura().getId());
		}
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Venta> buscarVentasParaNota(Cliente c) {
		return getVentasDao().buscarVentasConSaldo(c.getClave());
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Venta> buscarVentasParaNota(String clave) {
		return getVentasDao().buscarVentasConSaldo(clave);
	}

	public boolean tienePermiso(CXCPermiso per){
		return true;
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<DevolucionDet> buscarDevoluciones(String clave) {		
		return getDevolucionDao().buscarDevolucionesSinAplicar(clave);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Devolucion> buscarDevolucionesDisponibles(Cliente c) {
		/**
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("from Devolucion d" +
						" left join fetch d.partidas" +
						" where d.")
				return null;
			}			
		});
		**/
		
		List<DevolucionDet> dets=getDevolucionDao().buscarDevolucionesSinAplicar(c.getClave());
		final EventList<DevolucionDet> source=GlazedLists.eventList(dets);
		final Function<DevolucionDet, Devolucion> function=new Function<DevolucionDet, Devolucion>(){
			public Devolucion evaluate(DevolucionDet sourceValue) {				
				return sourceValue.getDevolucion();
			}			
		};
		final FunctionList<DevolucionDet, Devolucion> fl=new FunctionList<DevolucionDet, Devolucion>(source,function);
		final UniqueList<Devolucion> l=new UniqueList<Devolucion>(fl,GlazedLists.beanPropertyComparator(Devolucion.class, "id"));
		for(Devolucion dd:l){
			Hibernate.initialize(dd.getPartidas());
		}
		return l;
		
	}

	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void actualizarDevolucion(final Devolucion d){
		getSession().update(d);
		for(DevolucionDet det:d.getPartidas()){			
			if(d.getVenta().getDescuentos()!=0){
				if(d.getVenta().getTipo().equals("E")){
					double descuentos=d.getVenta().getDescuentos();
					double desc=(descuentos)/d.getVenta().getTotal().amount().doubleValue();
					det.setDescuento(Math.abs(desc));
					//d.setDescuento(desc);
					//d.setDescuento(det.getDescuento());					
					//Sy stem.out.println("Descuento calculado: "+det.getDescuento());
					//System.out.println("Descuento calculado: "+det.getImporte());
				}
			}else
				det.setDescuento(1);			
			det.getArticulo().getClave(); 			//Para inicializar y evitar LaxyInitializationException
			det.getVentaDet().getPrecioFacturado();//Para inicializar y evitar LaxyInitializationException
		}
		d.actualizarImporte();		
	}
	
	
	
	@Transactional (propagation=Propagation.SUPPORTS, readOnly=false)
	public void actualizarImpresion(final NotaDeCredito nc){
		nc.setImpreso(new Date());
		getNotaDeCreditoDao().actualizar(nc);
	}
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void imprimirNotaDeDescuento(final NotaDeCredito nota) {
		if(nota.getImpreso()!=null) return;
		getSession().update(nota);
		Hibernate.initialize(nota.getCliente());
		Hibernate.initialize(nota.getPartidas());
		try {
			ImpresorDeNotas.imprimir(nota);			
			nota.setImpreso(new Date());			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("No se pudo generar el archivo de impresion para la nota: "+nota.getId());
		}
		
		
	}
	
	/**
	 * Manda a imprimir una nota de devolución
	 * @param nota
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void imprimirNotaDevolucion(final NotaDeCredito nota){
		if(nota.getImpreso()!=null) return;
		NotaDeCredito devo=nota;
			//buscarNotaDevolucion(nota.getId());		
		getSession().update(devo);
		try {
			ImpresorDeNotas.imprimirNotaDeDevo(devo);  //TEMPORAL			
			devo.setImpreso(new Date());			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("No se pudo generar el archivo de impresion para la nota: "+nota.getId());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<NotaDeCredito> buscarNotasParaImpresionPorDescuentos(final Periodo p,final boolean anticipadas){
		final String hql="from NotaDeCredito nc " +
		" left join fetch nc.cliente" +
		" left join fetch nc.partidas" +
		" where nc.fecha between :f1 and :f2" +
		" and nc.tipo in (:u,:v)" +
		" and nc.impreso is  null " +
		" and nc.descuentoAnticipado=:anticipado" +
		" order by nc.clave, nc.fecha";
		List<NotaDeCredito> notas=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {				
				return session.createQuery(hql)
				.setParameter("f1", p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2", p.getFechaFinal(),Hibernate.DATE)
				.setString("u", "U")
				.setString("v", "V")
				.setBoolean("anticipado", anticipadas)
				.list();
			}			
		});
		final EventList<NotaDeCredito> source=GlazedLists.eventList(notas);
		final UniqueList<NotaDeCredito> unique=new UniqueList<NotaDeCredito>(source,GlazedLists.beanPropertyComparator(NotaDeCredito.class, "id"));
		return unique;
	}
	
	/**
	 * Refresca los datos de la nota a partir de la base de datos
	 * 
	 * @param nota
	 */
	@Transactional (propagation=Propagation.SUPPORTS, readOnly=true)
	public void refresh(final NotaDeCredito nota){
		getSession().update(nota);
		getSession().refresh(nota);		
		Hibernate.initialize(nota.getCliente());
		Hibernate.initialize(nota.getPartidas());		
		Hibernate.initialize(nota.getDevolucion());
		for(NotasDeCreditoDet det:nota.getPartidas()){
			Hibernate.initialize(det.getFactura());
			
		}
	}
	
	/**
	 * 
	 */	
	public List<NotaDeCredito> buscarNotasDeCreditoDisponibles(final Cliente c) {
		return buscarNotasDeCreditoDisponibles(c.getClave());
	}
	
	/**
	 * Regresa una lista de {@link NotaDeCredito} disponibles para ser usadas en
	 * pagos
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NotaDeCredito> buscarNotasDeCreditoDisponibles(final String clave){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {				
				return session.createQuery("from NotaDeCredito n " +
						" left join fetch n.cliente " +
						" where n.year>=2006 " +
						"   and n.aplicable=1 " +
						"   and n.clave=:c " +
						"   and n.saldo<-0.1" 
						)
						//.setBoolean("app", true)
						.setString("c", clave)
						.list();
			}			
		});
	}
	
	/**
	 * Obtiene el utlimo consecutivo 
	 * 
	 * @return
	 */
	public long nextNumero(final String tipo){
		
		final Long last=(Long)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String tipo1="";
				String tipo2="";
				if(tipo.equals("M") || tipo.equals("O")){
					tipo1=tipo2="M";
				}else if(tipo.equals("U") || tipo.equals("V")){
					tipo1="U";
					tipo2="V";
				}else if(tipo.equals("J") || tipo.equals("L")){
					tipo1="J";
					tipo2="L";
				}else{
					System.out.println("Tipo sin consecutivo");
					return 0L;
				}
					
				
				final String hql="select max(n.numero) from NotaDeCredito n" +
						" where n.tipo in(?,?)";
				return (Long)session.createQuery(hql)
				.setString(0, tipo1)
				.setString(1, tipo2)
				.uniqueResult();				
			}
		});
		return last+1;
	}
	
	/**
	 * Regresa una lista de beans {@link NotasDeCreditoDet} para el cliente y periodo indicados
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<NotasDeCreditoDet> buscarNotasDet(final Cliente c, final Periodo p) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final String hql="from NotasDeCreditoDet d " +
						" left join fetch d.nota n" +
						//" left join fetch d.partidad dp " +
						" left join fetch d.nota.cliente c" +
						" left join fetch d.factura f" +
						" where n.cliente=:c" +
						"   and n.fecha between :f1 and :f2";
				return session.createQuery(hql)
				.setEntity("c", c)
				.setParameter("f1", p.getFechaInicial())
				.setParameter("f2", p.getFechaFinal())
				.list();
			}			
		});
	}
	
	/**
	 * Actualiza el vencimiento de las notas de cargo
	 * pendientes de pago
	 *
	 */
	@Transactional (propagation=Propagation.SUPPORTS, readOnly=false)
	public void actualizarRevision(){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
				ScrollableResults rs=session.createQuery("from NotaDeCredito n " +
						"where  n.year>2005 and n.serie=:s" +
						"  and n.saldoDelCargo>1").setString("s", "M")
						.scroll();
				while(rs.next()){
					NotaDeCredito cargo=(NotaDeCredito)rs.get()[0];
					final String tipo=System.getProperty("recalcularCxc.origen");
					final Date fecha=tipo!=null?cargo.getFecha():new Date();
					if(cargo.getCliente()==null){
						System.out.println("Cargo sin cliente_id #:"+cargo.getId());
						Cliente cc=(Cliente)session.createQuery("from Cliente c where c.clave=:c").setString("c", cargo.getClave()).setMaxResults(1).uniqueResult();
						if(cc!=null)
							cargo.setCliente(cc);
						
						else
							continue;
					}
					if(cargo.getCliente().getCredito()!=null){
						int diaRevision=cargo.getCliente().getCredito().getDia_revision();
						final Date proxima=DateUtils.calcularFechaMasProxima(fecha,diaRevision, false);
						cargo.setFechaRevisionCxC(proxima);
						System.out.println("Fecha: "+df.format(fecha)+" Revision: "+df.format(cargo.getFechaRevisionCxC()));
						System.out.println("Cargo actualizado: "+cargo);
						
						int diaPago=cargo.getCliente().getCredito().getDia_pago();
						int plazo=cargo.getCliente().getCredito().getPlazo();
						final Date fechaCargo=cargo.getFecha();
						Calendar c=Calendar.getInstance();
						c.setTime(fechaCargo);
						c.add(Calendar.DATE, plazo);
						final Date primerPagoNatural=c.getTime();
						final Date primerPagoReal=DateUtils.calcularFechaMasProxima(primerPagoNatural, diaPago, true);
						final Date proximoPago;
						final Date hoy=DateUtils.truncate(new Date(),Calendar.DATE);						
						if(hoy.after(primerPagoReal)){
							proximoPago=DateUtils.calcularFechaMasProxima(hoy, diaPago, true);
						}else
							proximoPago=primerPagoReal;
						cargo.setFechaPagoCxC(proximoPago);
						
					}
					session.update(cargo);
						
					
				}
				return null;
			}			
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<NotasDeCreditoDet> buscarNotasNoAplicables(final Date fecha,final String origen){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final int year=Periodo.obtenerYear(fecha);
				final int mes=Periodo.obtenerMes(fecha)+1;
				System.out.println("Buscando notas para: "+fecha);
				List<NotasDeCreditoDet> dets=session.createQuery(
						"from NotasDeCreditoDet d " +
						"where d.nota.year=:year " +
						"  and d.nota.mes=:mes " 
						+"  and d.fecha=:fecha "
						//+"  and d.nota.aplicable=:aplica" 
						+"  and d.nota.origen=:origen"						
						+" order by d.fecha desc"
						)
						.setInteger("year", year)
						.setInteger("mes", mes)
						.setParameter("fecha", fecha,Hibernate.DATE)
						.setString("origen", origen)
						//.setBoolean("aplica", false)						
						.list();
				for(NotasDeCreditoDet d:dets){
					d.getCuenta();					
				}
				return dets;
			}
			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.managers.NotasManager#generarNotaDeCancelacion(com.luxsoft.siipap.cxc.domain.NotaDeCredito)
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public NotaDeCredito generarNotaDeCancelacion(final NotaDeCredito cargo) {
		getSession().update(cargo);
		final NotaDeCredito cred=NotasFactory.crearNotaDeCancelacion(cargo);
		cred.actualizar();
		getNotaDeCreditoDao().salvar(cred);
		return cred;
	}

	/****** Colaboradores **********/

	public NotaDeCreditoDao getNotaDeCreditoDao() {
		return notaDeCreditoDao;
	}
	public void setNotaDeCreditoDao(NotaDeCreditoDao notaDeCreditoDao) {
		this.notaDeCreditoDao = notaDeCreditoDao;
	}

	public ProvisionDao getProvisionDao() {
		return provisionDao;
	}
	public void setProvisionDao(ProvisionDao provisionDao) {
		this.provisionDao = provisionDao;
	}

	public DevolucionDao getDevolucionDao() {
		return devolucionDao;
	}
	public void setDevolucionDao(DevolucionDao devolucionDao) {
		this.devolucionDao = devolucionDao;
	}

	public PagoDao getPagoDao() {
		return pagoDao;
	}
	public void setPagoDao(PagoDao pagoDao) {
		this.pagoDao = pagoDao;
	}

	public VentasDao getVentasDao() {
		return ventasDao;
	}
	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<NotaDeCredito> buscarCargosCheque() {
		return getHibernateTemplate().find("from NotaDeCredito n " +
				" left join fetch n.cliente c" +				
			" where n.origen=\'CHE\' " +
				//" and n.tipo=\'M\'" +
			" and n.year>=2008");
	}

	/****** END Colaboradores **********/

	
	public static void main(String[] args) {
		/*
		List<NotasDeCreditoDet> dets=ServiceLocator.getNotasManager().buscarNotasNoAplicables(DateUtils.obtenerFecha("01/10/2007"), "CRE");
		System.out.println("Dets: "+dets.size());
		*/
		
		final NotaDeCredito cargo=ServiceLocator.getNotasManager().buscarNota(108844L);
		ServiceLocator.getNotasManager().generarNotaDeCancelacion(cargo);
		
		
	}

	
		
	

}
