package com.luxsoft.siipap.ventas.domain;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;


import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteHolder;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.utils.domain.PersistentObject;


public class Venta extends PersistentObject implements ClienteHolder{
	
	private Cliente cliente;
	
	private Integer sucursal;
	
	private Date fecha=currentTime();
	
	private String origen;
	
	/**
	 * Tipo de venta (E=Credito, C=Camioneda A=Mostrador)
	 */
	private String serie;
	
	/**
	 * Clasificacion de la venta segun el proceso seguido por la venta
	 * Tabla: TIPOFACT.D00
	 */
	private String tipo;
	
	/**
	 * Folio de la venta
	 */
	private long numero;
	
	private Integer clasificacion;
	
	private String clave;
	
	private String nombre;
	
	private String socio;
	
	private String nombreSocio;
	
	/**
	 * Suma de el importe de cada una de las partidas de la venta
	 */
	private CantidadMonetaria importeBruto=CantidadMonetaria.pesos(0);
	
	/**
	 * Descuento contemplado antes de facturar. 
	 * Es el descuento definido  por el sistema al momento de la venta 
	 */
	private double descuentoReal;
	
	/**
	 * Descuento definitivo del sistema en el mejor de los casos es el descuentoReal pero por operaciones
	 * de ventas puede ser modificado por algun tipo de autorización. Si este descuento es diferente
	 * al descuentoReal quiere decir que fue explilcitamente asignado por el usuario
	 *  
	 */
	private double descuentoFacturado;
	
	/**
	 * Cargo adicional (Opcional)
	 */
	private CantidadMonetaria importeManiobras=CantidadMonetaria.pesos(0);
	
	
	private int cortes;
	
	private CantidadMonetaria precioCorte=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria importeCortes=CantidadMonetaria.pesos(0);
	
	/**
	 * Importe de la factua con descuentos aplicados
	 */
	private CantidadMonetaria subTotal=CantidadMonetaria.pesos(0);;
	
	/**
	 * 
	 */
	private CantidadMonetaria impuesto=CantidadMonetaria.pesos(0);
	
	/**
	 * Total Facturado con IVA. Este es el importe en la cuenta por cobrar
	 * 
	 */
	private CantidadMonetaria total=CantidadMonetaria.pesos(0);
	
	private double kilos=0;
	
	private double cantidad=0;
	
	/**
	 * Numero del pedido, puede ser cero en algunos casos
	 */
	private int pedido;
	
	/**
	 * Quien levanto el pedido
	 */
	private String pedidoUsuario;
	
	/**
	 * Folio de la factura
	 */
	private int numeroFiscal;
	
	/**
	 * length=1
	 */
	private String tipoDePago;
	
	/**
	 * length=15
	 */
	private String facturista;
	
	
	private String surtidor;
	
	private String comentario;
	
	private Date fechaCancelacion;
	
	/**
	 * quien cancelo
	 */
	private String cancelo;
	
	private String motivoCancelacion;
	
	
	private double bonificaciones;
	
	private double pagos;
	
	private double descuentos;
	
	private double descuento1;
	
	private double descuento2;
	
	private long descuento1Numero;
	
	private double descuentoT;
	
	private int vendedor;
	
	private int cobrador;
	
	private int chofer;
	
	
	private Date vencimiento;
	
	private int diaRevision;
	
	private int diaPago;

	
	private Set<VentaDet> partidas=new HashSet<VentaDet>();
	
	private boolean eliminado=false;
	
	private double devolucionesCred;
	
	private int year;
	
	private int mes;
	
	private Date fechaReal;
	
	private Date creado=currentTime();
	
	private int version;
	
	private BigDecimal saldo;
	
	private Provision provision;
	
	private VentaACredito credito;
	
	private List<Pago> pagosAplicados;
	
	private List<NotaDeCredito> notasAplicadas;
	
	private BigDecimal pagoComisionable=BigDecimal.ZERO;
	
	private double comisionVenta;
	
	private double comisionCobrador;
	
	private BigDecimal impComisionVent=BigDecimal.ZERO;
	
	private BigDecimal impComisionCob=BigDecimal.ZERO;
	
	private Date pagoComisionVendedor;
	
	private Date pagoComisionCobrador;
	
	@Length (max=55)
	private String cancelComiVent;
	
	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	
	public Integer getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(Integer clasificacion) {
		this.clasificacion = clasificacion;
	}
		
	public int getNumeroFiscal() {
		return numeroFiscal;
	}
	public void setNumeroFiscal(int numeroFiscal) {
		this.numeroFiscal = numeroFiscal;
	}
	
	
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public int getCortes() {
		return cortes;
	}
	public void setCortes(int cortes) {
		this.cortes = cortes;
	}
	
	
	
	public double getDescuentoFacturado() {
		return descuentoFacturado;
	}
	public void setDescuentoFacturado(double descuentoFacturado) {
		this.descuentoFacturado = descuentoFacturado;
	}
	public double getDescuentoReal() {
		return descuentoReal;
	}
	public void setDescuentoReal(double descuentoReal) {
		this.descuentoReal = descuentoReal;
	}
	public int getPedido() {
		return pedido;
	}
	public void setPedido(int pedido) {
		this.pedido = pedido;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	
	
	public CantidadMonetaria getImporteBruto() {
		return importeBruto;
	}
	public void setImporteBruto(CantidadMonetaria importeBruto) {
		this.importeBruto = importeBruto;
	}
	
	public CantidadMonetaria getImporteCortes() {
		return importeCortes;
	}
	public void setImporteCortes(CantidadMonetaria importeCortes) {
		this.importeCortes = importeCortes;
	}
	
	public CantidadMonetaria getImporteManiobras() {
		return importeManiobras;
	}
	public void setImporteManiobras(CantidadMonetaria importeManiobras) {
		this.importeManiobras = importeManiobras;
	}
	
	public CantidadMonetaria getImpuesto() {
		return impuesto;
	}
	public void setImpuesto(CantidadMonetaria impuesto) {
		this.impuesto = impuesto;
	}
	
	public double getKilos() {
		return kilos;
	}
	public void setKilos(double kilos) {
		this.kilos = kilos;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getNombreSocio() {
		return nombreSocio;
	}
	public void setNombreSocio(String nombreSocio) {
		this.nombreSocio = nombreSocio;
	}
	
	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	
	public CantidadMonetaria getPrecioCorte() {
		return precioCorte;
	}
	public void setPrecioCorte(CantidadMonetaria precioCorte) {
		this.precioCorte = precioCorte;
	}
	
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	
	public String getSocio() {
		return socio;
	}
	public void setSocio(String socio) {
		this.socio = socio;
	}
	
	public CantidadMonetaria getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(CantidadMonetaria subTotal) {
		this.subTotal = subTotal;
	}
	
	public Integer getSucursal() {
		return sucursal;
	}
	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}
	
	
	
	public void setNumero(long numero) {
		this.numero = numero;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public CantidadMonetaria getTotal() {
		return total;
	}
	public void setTotal(CantidadMonetaria total) {
		this.total = total;
	}	
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public void calcularImporteDeCorte(){
		if(getPrecioCorte().getAmount().doubleValue()>0){
			setImporteCortes(getPrecioCorte().multiply(getCortes()));
		}else
			setImporteCortes(new CantidadMonetaria(0,getImporteCortes().getCurrency()));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		Venta v=(Venta)obj;
		return new EqualsBuilder()
		.append(getSucursal(),v.getSucursal())
		.append(getSerie(),v.getSerie())
		.append(getTipo(),v.getTipo())
		.append(getNumero(),v.getNumero())
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getSucursal())
		.append(getSerie())
		.append(getTipo())
		.append(getNumero())
		.toHashCode();
	}
	
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Docto:",getNumero())
		.append("Suc: ",getSucursal())
		.append("Serie: ",getSerie())
		.append("Tipo: ",getTipo())
		.append("Fecha",getFecha())
		.append("Total:",getTotal())
		.append("Saldo: ",getSaldo())
		.append("Descuentos NC",getDescuentos())
		.append("Descueneto T:",getDescuentoT())
		.append("Pagos: "+getPagos())
		.append("Origen",getOrigen())
		.toString();
		
	}
	
	public Set<VentaDet> getPartidas() {
		return partidas;
	}
	public void setPartidas(Set<VentaDet> partidas) {
		this.partidas = partidas;
	}
	
	public void agregarDetalle(final VentaDet det){
		det.setVenta(this);
		getPartidas().add(det);
	}
	public String getCancelo() {
		return cancelo;
	}
	public void setCancelo(String cancelo) {
		this.cancelo = cancelo;
	}
	public int getChofer() {
		return chofer;
	}
	public void setChofer(int chofer) {
		this.chofer = chofer;
	}
	public int getCobrador() {
		return cobrador;
	}
	public void setCobrador(int cobrador) {
		this.cobrador = cobrador;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public int getDiaPago() {
		return diaPago;
	}
	public void setDiaPago(int diaPago) {
		this.diaPago = diaPago;
	}
	
	
	
	public int getDiaRevision() {
		return diaRevision;
	}
	public void setDiaRevision(int diaRevision) {
		this.diaRevision = diaRevision;
	}
	public String getFacturista() {
		return facturista;
	}
	public void setFacturista(String facturista) {
		this.facturista = facturista;
	}
	public Date getFechaCancelacion() {
		return fechaCancelacion;
	}
	public void setFechaCancelacion(Date fechaCancelacion) {
		this.fechaCancelacion = fechaCancelacion;
	}
	public String getMotivoCancelacion() {
		return motivoCancelacion;
	}
	public void setMotivoCancelacion(String motivoCancelacion) {
		this.motivoCancelacion = motivoCancelacion;
	}
	public String getPedidoUsuario() {
		return pedidoUsuario;
	}
	public void setPedidoUsuario(String pedidoUsuario) {
		this.pedidoUsuario = pedidoUsuario;
	}
	
	
	
	public String getSurtidor() {
		return surtidor;
	}
	public void setSurtidor(String surtidor) {
		this.surtidor = surtidor;
	}
	public String getTipoDePago() {
		return tipoDePago;
	}
	public void setTipoDePago(String tipoDePago) {
		this.tipoDePago = tipoDePago;
	}
	
	public Date getVencimiento() {
		if(getCredito()!=null){
			int plazo=getCredito().getPlazo();
			return org.apache.commons.lang.time.DateUtils.addDays(getFecha(), plazo);
		}
		return vencimiento;
	}
	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}
	public int getVendedor() {
		return vendedor;
	}
	public void setVendedor(int vendedor) {
		this.vendedor = vendedor;
	}
	public boolean isEliminado() {
		return eliminado;
	}
	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}
	
	public double getDescuentos() {
		return descuentos;
	}
	public void setDescuentos(double descuentos) {
		this.descuentos = descuentos;
	}
	public double getPagos() {
		return pagos;
	}
	public void setPagos(double pagos) {
		this.pagos = pagos;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Date getFechaReal() {
		return fechaReal;
	}
	public void setFechaReal(Date fechaReal) {
		this.fechaReal = fechaReal;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public double getDescuentoT() {
		return descuentoT;
	}
	public void setDescuentoT(double descuentoT) {
		this.descuentoT = descuentoT;
	}
	
	public CantidadMonetaria getCosto(){
		CantidadMonetaria costo=new CantidadMonetaria(0,getTotal().getCurrency());
		for(VentaDet det:getPartidas()){
			costo=costo.add(det.getCostoHistorico().getCosto());
		}		
		return costo;
	}
	
	/**
	 *  Acceso dinamico a las notas por bonificacion (L)
	 *  
	 * @return
	 */
	public double getBonificaciones(){
		return bonificaciones;
	}
	
	
	public double getDescuento1() {		
		return descuento1;
	}
	public void setDescuento1(double descuento1) {
		this.descuento1 = descuento1;
	}
	public double getDevolucionesCred() {
		return devolucionesCred;
	}
	public void setDevolucionesCred(double devolucionesCred) {
		this.devolucionesCred = devolucionesCred;
	}
	public BigDecimal getSaldo() {
		//if(getCliente().getCredito().isNotaAnticipada())
			//return saldo.add(BigDecimal.valueOf(getBonificaciones()));
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		Object old=this.saldo;
		this.saldo = saldo;
		getPropertyChangeSupport().firePropertyChange("saldo",old,saldo);
	}
	
	public CantidadMonetaria getSaldoEnMoneda(){
		return new CantidadMonetaria(getSaldo().abs().doubleValue(),getTotal().getCurrency());
	}
	
	public CantidadMonetaria getSaldoEnMonedaSinImportaroSigno(){
		return new CantidadMonetaria(getSaldo().doubleValue(),getTotal().getCurrency());
	}
	
	/**
	 * Regresa el total de la factura menos devoluciones sin iva
	 * 
	 * @return
	 */
	public CantidadMonetaria getTotalSinDevoluciones(){
		return getTotal().add(getDevoluciones().divide(1.15));
	}
	
	public double getTotalSinDevolucionesAsDouble(){
		return getTotal().add(getDevoluciones().divide(1.15)).amount().doubleValue();
	}
	
	public int getAtraso(){
		if(getSaldo().doubleValue()<=0) return 0;
		Date today=new Date();		
		if(getCredito()!=null){
			long res=today.getTime()-getCredito().getVencimiento().getTime();
			if(res>0){				
				long dias=(res/(86400*1000));			
				return (int)dias;
			}else
				return 0;
		}else{
			
			Date actual=new Date();
			Date fecha=getVencimiento()!=null?getVencimiento():actual;
			long res2=actual.getTime()-fecha.getTime();
			long dias=0;
			if(res2>0){
				dias=(res2/(86400*1000));
				int atraso=(int)dias;
				return atraso;
			}else
				return 0;
			
		}
		
	}
	
	public int getAtrasoReal(){
		if(getSaldo().doubleValue()<=0)
			return 0;
		Date today=new Date();
		Date pivot=getFecha();
		/*if(getCredito()!=null){
			pivot=getCredito().getFechaRevision();
		}*/
		long res=today.getTime()-pivot.getTime();
		if(res>0){			
			long dias=(res/(86400*1000));			
			int atraso= ((int)dias-getPlazo());
			if(atraso<0) atraso=0;
			return atraso;
		}else{
			return 0;
		}
	}
	
	public Currency getMoneda(){
		return getTotal().currency();
	}
	
	public Provision getProvision() {
		return provision;
	}
	public void setProvision(Provision provision) {
		this.provision = provision;
	}
	public VentaACredito getCredito() {
		return credito;
	}
	public void setCredito(VentaACredito credito) {
		this.credito = credito;
	}
	
	public void actualizarDatosDeCredito(){
		if("CRE".equals(getOrigen()) && (getCredito()==null)){
			@SuppressWarnings("unused")
			VentaACredito c=new VentaACredito(this);			
			
		}else if("CRE".equals(getOrigen()) && (getCredito()!=null)){
			getCredito().actualizar();
			getCredito().actualizarDescuentoPrecioNetoProchemex();
		}
	}
	
	public int getPlazo(){
		//long time=getVencimiento().getTime()-getFecha().getTime();
		//return (int)(time/(86400*1000));
		if(getCredito()!=null)
			return getCredito().getPlazo();
		return 1;
	}
	
	public List<NotaDeCredito> getNotasAplicadas() {
		if(notasAplicadas==null)
			notasAplicadas=new ArrayList<NotaDeCredito>();
		return notasAplicadas;
	}
	public void setNotasAplicadas(List<NotaDeCredito> notasAplicadas) {
		this.notasAplicadas = notasAplicadas;
	}
	
	public List<Pago> getPagosAplicados() {
		if(pagosAplicados==null){
			pagosAplicados=new ArrayList<Pago>();
		}
		return pagosAplicados;
	}
	public void setPagosAplicados(List<Pago> pagosAplicados) {
		this.pagosAplicados = pagosAplicados;
	}
	
	/**
	 * Agrega un pago a la venta
	 * 
	 * @param p
	 */
	public void agregarPago(final Pago p){
		getPagosAplicados().add(p);
	}
	
	/**
	 * Recalcula el saldo de la factura
	 *
	 */
	public void recalcularSaldo(){
		
	}
	
	public NotaDeCredito getNotaPorDescuento() {
		for(NotaDeCredito d:getNotasAplicadas()){
			if(d.getTipo().equals("U"))
				return d;
		}
		return null;
	}
	
	/*** Campos solo para facilitar la GUI ***/
	
	
	private CantidadMonetaria pago;

	/**
	 * Sirve para almacenar un pago a una factura de manera temporal
	 * este no se persiste como parte de la factura
	 * 
	 */
	public CantidadMonetaria getPago() {
		return pago;
	}
	public void setPago(CantidadMonetaria pago) {
		Object old=this.pago;
		this.pago = pago;
		getPropertyChangeSupport().firePropertyChange("pago", old, pago);
	}
	public long getDescuento1Numero() {		
		return descuento1Numero;
	}
	public void setDescuento1Numero(long descuento1Numero) {
		this.descuento1Numero = descuento1Numero;
	}
	
	public double getSaldoEstimadoEnDouble(){
		if(getPagos()>0)
			return getSaldo().doubleValue();
		return getSaldoEstimado().amount().doubleValue();
	}
	
	/**
	 * Saldo real de la factura menos el descuento estimado (Provision)
	 * 
	 * @return
	 */
	public CantidadMonetaria getSaldoEstimado(){
				
		//if(getDescuento()>0 ){  ///EN REVISION POR RCR Y CP
		if(getDescuento()>0 && getPagos()==0){
			CantidadMonetaria devoluciones=CantidadMonetaria.pesos(getDevolucionesCred());
			CantidadMonetaria ventaSinDevolucion=getTotal().add(devoluciones).multiply((getDescuento()-getCargo())/100);				
			return getSaldoEnMoneda().subtract(ventaSinDevolucion);
		}
		return getSaldoEnMoneda();
	}
	
	/**
	 * Saldo estimando menos el descuento sin contemplar el cargo
	 * @return
	 */
	public CantidadMonetaria getSaldoEstimadoSinCargo(){
		
		//if(getDescuentoPactado()>0 && getDescuentos()==0){   ///EN REVISION POR RCR Y CP
		if(getDescuentoPactado()>0 && getDescuento1()==0 && getPagos()==0){  ///			
			CantidadMonetaria devoluciones=CantidadMonetaria.pesos(getDevolucionesCred());
			CantidadMonetaria provision=getTotal().add(devoluciones).multiply(getDescuentoPactado()/100);				
			return getSaldoEnMoneda().subtract(provision);
		}
		return getSaldoEnMoneda();
	}
	
	/** Intento por homegenizar el comportamiento de las ventas ***/
	
	/**
	 * Descuento calculado por la provision 
	 */
	public double getDescuento(){
		if(isProvisionable() && getDescuento1()==0){  ////
			if(getProvision()!=null)/*RCR*/
				return getProvision().getDescuento();
			return 0;
		}
		return 0;
	}	
	
	/**
	 * Decuento pactado sin cargos
	 * 
	 * @return
	 */
	public double getDescuentoPactado(){
		if(isProvisionable() && getCredito()!=null){
			return getCredito().getDescuento();
		}else
			return 0;
	}
	
	public boolean isProvisionable(){
		return ("E".equals(getTipo()) || "S".equals(getTipo()));
	}
	
	public double getCargo(){
		if(isProvisionable() && getProvision()!=null){			
			return getProvision().getCargoCalculado();
		}
		return 0;
	}
	
	public CantidadMonetaria getDescuentoImporte(){
		if(isProvisionable()){
			return getTotal().multiply(BigDecimal.valueOf(getDescuento()/100));
		}
		return CantidadMonetaria.pesos(0);
	}	
	
	/**
	 * Regresa las devoluciones de esta factura
	 * 
	 * @return
	 */
	public CantidadMonetaria getDevoluciones(){
		return CantidadMonetaria.pesos(getDevolucionesCred());
	}
	
	/** Fin:  Intento por homegenizar el comportamiento de las ventas ***/
	
	public Date getVencimientoFinanciero(){
		if(getCredito()==null)
			return null;
		final int plazo=getCredito().getPlazo();
		final Date ffactura=getFecha();		
		Calendar c=Calendar.getInstance();
		c.setTime(ffactura);
		c.getTime();
		c.add(Calendar.DATE, plazo);		
		final Date vto=DateUtils.truncate(c.getTime(),Calendar.DATE);
		return vto;
	}
	
	/**
	 * In-Line para ayudar en UI
	 */
	public double descuentoTemporal=0;




	public double getDescuentoTemporal() {
		return descuentoTemporal;
	}
	public void setDescuentoTemporal(double descuentoTemporal) {
		Object oldValue=this.descuentoTemporal; 
		this.descuentoTemporal = descuentoTemporal;
		getPropertyChangeSupport().firePropertyChange("descuentoTemporal", oldValue, descuentoTemporal);
	}
	
	
	public double getDescuento2() {
		return descuento2;
	}
	public void setDescuento2(double descuento2) {
		this.descuento2 = descuento2;
	}
	
	public double getComisionCobrador() {
		return comisionCobrador;
	}
	public void setComisionCobrador(double comisionCobrador) {
		this.comisionCobrador = comisionCobrador;
	}
	public double getComisionVenta() {
		return comisionVenta;
	}
	public void setComisionVenta(double comisionVenta) {
		this.comisionVenta = comisionVenta;
	}
	public Date getPagoComisionCobrador() {
		return pagoComisionCobrador;
	}
	public void setPagoComisionCobrador(Date pagoComisionCobrador) {
		this.pagoComisionCobrador = pagoComisionCobrador;
	}
	public Date getPagoComisionVendedor() {
		return pagoComisionVendedor;
	}
	public void setPagoComisionVendedor(Date pagoComisionVendedor) {
		this.pagoComisionVendedor = pagoComisionVendedor;
	}
	

	public BigDecimal getImpComisionCob() {
		return impComisionCob;
	}
	public void setImpComisionCob(BigDecimal impComisionCob) {
		this.impComisionCob = impComisionCob;
	}
	public BigDecimal getImpComisionVent() {
		return impComisionVent;
	}
	public void setImpComisionVent(BigDecimal impComisionVent) {
		this.impComisionVent = impComisionVent;
	}
	public String getCancelComiVent() {
		return cancelComiVent;
	}
	public void setCancelComiVent(String cancelComiVent) {
		this.cancelComiVent = cancelComiVent;
	}
	public BigDecimal getPagoComisionable() {
		return pagoComisionable;
	}
	public void setPagoComisionable(BigDecimal pagoComisionable) {
		this.pagoComisionable = pagoComisionable;
	}
	
	public double getCantidadEnKilos(){
		return getKilos()*getCantidad();
	}
	
	public double getTotalAsDouble(){
		return getTotal().amount().doubleValue();
	}
	
	public String getCuenta(){
		if(getCliente().getCredito()!=null)
			if(getCliente().getCredito().getCuenta()!=null){
				String cta= getCliente().getCredito().getCuenta();
				if(StringUtils.isBlank(cta)){
					throw new IllegalStateException(
							MessageFormat.format("El cliente no {0}  tiene cuenta contable registrada",getClave()));
				}
				return cta;
			}
		return "SIN CUENTA";
	}
	
	
	public Boolean getMandarARevision(){
		Boolean tipo=Boolean.FALSE;
		if(getCredito()!=null){
			tipo=getCredito().isRevision();
		}
		return tipo;
	}
}
