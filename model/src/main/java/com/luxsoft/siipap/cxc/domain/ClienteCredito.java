package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.utils.domain.MutableObject;


/**
 * Anexo al catalogo de clientes para controlar lo
 * referente al credito e historial crediticio
 * del cliente
 * 
 * @author Ruben Cancino
 *
 */
@Entity
@Table(name="SW_CLIENTES_CREDITO")
public class ClienteCredito extends MutableObject{
	
	private Long id;
	
	private Cliente cliente;
	
	/***NUEVOS**/
	@Length (min=1, max=7)
	private String clave;
	
	@Length (min=1,max=55)
	private String nombre;
	
	private int dia_revision;
	
	private int dia_pago;
	
	private int cobrador;
	
	private int operador;
	
	private int plazo;
	
	private boolean tipo_vencimiento;
	
	private boolean pagare=false;

	private CantidadMonetaria limite;

	private boolean ordenDeCompra=false;
	
	private boolean notaAnticipada=false;	
	
	@Length (max=30)
	private String corporativo;
	
	private BigDecimal saldoInicial=BigDecimal.ZERO;
	
	private BigDecimal respaldo=BigDecimal.ZERO;
	
	@Length (max=255)
	private String comentarioCxc;
	
	private String comentariosVentas;
	
	private boolean chequep=false;
	
	private boolean suspenderDescuento=false;
	
	
	
	private String cuenta;
	 
	
	private Date creado=new Date();
	
	private Date modificado;
	
	
	
	
	private BigDecimal saldoVencido=BigDecimal.ZERO;
	
	private BigDecimal saldo=BigDecimal.ZERO;
	
	private BigDecimal pagos=BigDecimal.ZERO;
	
	private Date ultimoPago;
	
	private int atrasoMaximo;
	
	private int facturasVencidas;
	
	private Date ultimaVenta;
	
	private CantidadMonetaria ventaNeta;
	
	private double comisionCobrador;
	
	private double comisionVendedor;
	
	private int vendedor;
	
	private boolean calendario=false;
	
	private Set<Date> fechasRevision=new HashSet<Date>();
	
	private Set<Date> fechasPago=new HashSet<Date>();
	
	private String clasificacionCxc;
	


	public String getClasificacionCxc() {
		return clasificacionCxc;
	}

	public void setClasificacionCxc(String clasificacionCxc) {
		Object old=this.clasificacionCxc;
		this.clasificacionCxc = clasificacionCxc;
		getPropertyChangeSupport().firePropertyChange("clasificacionCxc", old, clasificacionCxc);
	}

	public ClienteCredito() {
	}
	
	public ClienteCredito(Cliente cliente) {		
		this.cliente = cliente;
		//setCliente(cliente);
		this.cliente.setCredito(this);
		//updateProperties();
	}
	
	public void updateProperties(){
		if(getCliente().getId()==null){
			setClave(cliente.getClave());
			setNombre(cliente.getNombre());			
			setCobrador(cliente.getCobrador());
			setVendedor(cliente.getVendedor());
			setDia_pago(cliente.getDia_pago());
			setDia_revision(cliente.getDia_revision());
			setLimite(cliente.getLimite_cred());
			setOperador(cliente.getOperador());
			setPlazo(cliente.getPlazo());
			setTipo_vencimiento(cliente.getTipo_vencimiento().equalsIgnoreCase("F"));
			
		}
	}
	
	public void copyFromCliente(){
		setClave(cliente.getClave());
		setNombre(cliente.getNombre());			
		setCobrador(cliente.getCobrador());
		setVendedor(cliente.getVendedor());
		setDia_pago(cliente.getDia_pago());
		setDia_revision(cliente.getDia_revision());
		setLimite(cliente.getLimite_cred());
		setOperador(cliente.getOperador());
		setPlazo(cliente.getPlazo());
		setTipo_vencimiento(cliente.getTipo_vencimiento().equalsIgnoreCase("F"));
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public int getCobrador() {
		return cobrador;
	}

	public void setCobrador(int cobrador) {
		int oldValue=this.cobrador;
		this.cobrador = cobrador;
		getPropertyChangeSupport().firePropertyChange("cobrador", oldValue, cobrador);
	}

	public String getComentarioCxc() {
		return comentarioCxc;
	}

	public void setComentarioCxc(String comentarioCxc) {
		Object old=this.comentarioCxc;
		this.comentarioCxc = comentarioCxc;
		getPropertyChangeSupport().firePropertyChange("comentarioCxc", old, comentarioCxc);
	}

	public String getComentariosVentas() {
		return comentariosVentas;
	}

	public void setComentariosVentas(String comentariosVentas) {
		this.comentariosVentas = comentariosVentas;
	}

	public String getCorporativo() {
		return corporativo;
	}

	public void setCorporativo(String corporativo) {
		String oldValue=this.corporativo;
		this.corporativo = corporativo;
		getPropertyChangeSupport().firePropertyChange("corporativo", oldValue, corporativo);
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public int getDia_pago() {
		return dia_pago;
	}

	public void setDia_pago(int dia_pago) {
		int oldValue=this.dia_pago;
		this.dia_pago = dia_pago;
		getPropertyChangeSupport().firePropertyChange("dia_pago", oldValue, dia_pago);
	}

	public int getDia_revision() {
		return dia_revision;
	}

	public void setDia_revision(int dia_revision) {
		int oldValue=this.dia_revision;
		this.dia_revision = dia_revision;
		getPropertyChangeSupport().firePropertyChange("dia_revision", oldValue, dia_revision);
	}

	public CantidadMonetaria getLimite() {
		return limite;
	}

	public void setLimite(CantidadMonetaria limite) {
		this.limite = limite;
	}

	public Date getModificado() {
		return modificado;
	}

	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isNotaAnticipada() {
		return notaAnticipada;
	}

	public void setNotaAnticipada(boolean notaAnticipada) {
		boolean oldValue=this.notaAnticipada;
		this.notaAnticipada = notaAnticipada;
		getPropertyChangeSupport().firePropertyChange("notaAnticipada", oldValue, notaAnticipada);
	}

	public int getOperador() {
		return operador;
	}

	public void setOperador(int operador) {
		int oldValue=this.operador;
		this.operador = operador;
		getPropertyChangeSupport().firePropertyChange("operador", oldValue, operador);
	}

	
	public boolean isOrdenDeCompra() {
		return ordenDeCompra;
	}

	public void setOrdenDeCompra(boolean ordenDeCompra) {
		boolean oldValue=this.ordenDeCompra;
		this.ordenDeCompra = ordenDeCompra;
		getPropertyChangeSupport().firePropertyChange("ordenDeCompra", oldValue, ordenDeCompra);
	}

	public boolean isPagare() {
		return pagare;
	}

	public void setPagare(boolean pagare) {
		this.pagare = pagare;
	}

	public int getPlazo() {
		return plazo;
	}

	public void setPlazo(int plazo) {
		int oldValue=this.plazo;
		this.plazo = plazo;
		getPropertyChangeSupport().firePropertyChange("plazo",oldValue,plazo);
	}

	public BigDecimal getRespaldo() {
		return respaldo;
	}

	public void setRespaldo(BigDecimal respaldo) {
		this.respaldo = respaldo;
	}

	public BigDecimal getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(BigDecimal saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public boolean isTipo_vencimiento() {
		return tipo_vencimiento;
	}

	public void setTipo_vencimiento(boolean tipo_vencimiento) {
		boolean oldValue=this.tipo_vencimiento;
		this.tipo_vencimiento = tipo_vencimiento;
		getPropertyChangeSupport().firePropertyChange("tipo_vencimiento", oldValue, tipo_vencimiento);
	}

	/**
	 * Bandera para clientes que trabajan con cheque post fechado
	 * 
	 * @return
	 */
	public boolean isChequep() {
		return chequep;
	}
	public void setChequep(boolean chequep) {
		boolean old=this.chequep;
		this.chequep = chequep;
		getPropertyChangeSupport().firePropertyChange("chequep", old, chequep);
	}

/**
	public int getChequesDevueltos() {
		return chequesDevueltos;
	}
	public void setChequesDevueltos(int chequesDevueltos) {
		this.chequesDevueltos = chequesDevueltos;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public String getComentarioCxc() {
		return comentarioCxc;
	}
	public void setComentarioCxc(String comentarioCxc) {
		this.comentarioCxc = comentarioCxc;
	}
	public String getComentariosVentas() {
		return comentariosVentas;
	}
	public void setComentariosVentas(String comentariosVentas) {
		this.comentariosVentas = comentariosVentas;
	}
	public BigDecimal getCredito() {
		return credito;
	}
	public void setCredito(BigDecimal credito) {
		this.credito = credito;
	}
	public BigDecimal getDescuentosYtd() {
		return descuentosYtd;
	}
	public void setDescuentosYtd(BigDecimal descuentosYtd) {
		this.descuentosYtd = descuentosYtd;
	}
	public BigDecimal getDevolucionesYtd() {
		return devolucionesYtd;
	}
	public void setDevolucionesYtd(BigDecimal devolucionesYtd) {
		this.devolucionesYtd = devolucionesYtd;
	}
	public int getFacturas() {
		return facturas;
	}
	public void setFacturas(int facturas) {
		this.facturas = facturas;
	}
	public int getFacturasVencidas() {
		return facturasVencidas;
	}
	public void setFacturasVencidas(int facturasVencidas) {
		this.facturasVencidas = facturasVencidas;
	}
	public String getNotaCredito() {
		return notaCredito;
	}
	public void setNotaCredito(String notaCredito) {
		this.notaCredito = notaCredito;
	}
	public String getNotaVentas() {
		return notaVentas;
	}
	public void setNotaVentas(String notaVentas) {
		this.notaVentas = notaVentas;
	}
	public BigDecimal getPagosYtd() {
		return pagosYtd;
	}
	public void setPagosYtd(BigDecimal pagosYtd) {
		this.pagosYtd = pagosYtd;
	}
	public BigDecimal getRespaldo() {
		return respaldo;
	}
	public void setRespaldo(BigDecimal respaldo) {
		this.respaldo = respaldo;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		Object old=this.saldo;
		this.saldo = saldo;
		getPropertyChangeSupport().firePropertyChange("saldo",old,saldo);
	}
	
	public BigDecimal getSaldoInicial() {
		return saldoInicial;
	}
	public void setSaldoInicial(BigDecimal saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	public BigDecimal getSaldoVencido() {
		return saldoVencido;
	}
	
	public void setSaldoVencido(BigDecimal saldoVencido) {
		Object old=this.saldoVencido;
		this.saldoVencido = saldoVencido;
		getPropertyChangeSupport().firePropertyChange("saldoVencido",old,saldoVencido);
	}
	
	public Date getUltimaVenta() {
		return ultimaVenta;
	}
	public void setUltimaVenta(Date ultimaVenta) {
		this.ultimaVenta = ultimaVenta;
	}
	public Date getUltimoPago() {
		return ultimoPago;
	}
	public void setUltimoPago(Date ultimoPago) {
		this.ultimoPago = ultimoPago;
	}
	
	

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public Date getModificado() {
		return modificado;
	}

	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}

	public BigDecimal getVcontadoYtd() {
		return vcontadoYtd;
	}

	public void setVcontadoYtd(BigDecimal vcontadoYtd) {
		this.vcontadoYtd = vcontadoYtd;
	}

	public BigDecimal getVcreditoYtd() {
		return vcreditoYtd;
	}

	public void setVcreditoYtd(BigDecimal vcreditoYtd) {
		this.vcreditoYtd = vcreditoYtd;
	}
	
	

	public int getAtrasoMaximo() {
		return atrasoMaximo;
	}

	public void setAtrasoMaximo(int atrasoMaximo) {
		this.atrasoMaximo = atrasoMaximo;
	}
	
	public void actualizarSaldo(final List<Venta> ventas){
		setSaldoVencido(getSaldoInicial());
		CollectionUtils.forAllDo(ventas, new Closure(){
			public void execute(Object input) {
				final Venta v=(Venta)input;
				final BigDecimal saldo=v.getSaldo()!=null?v.getSaldo():BigDecimal.ZERO;
				setSaldo(getSaldo().add(saldo));
			}			
		});
	}
	**/

	

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public BigDecimal getSaldoVencido() {
		return saldoVencido;
	}

	public void setSaldoVencido(BigDecimal saldoVencido) {
		this.saldoVencido = saldoVencido;
	}

	public Date getUltimoPago() {
		return ultimoPago;
	}

	public void setUltimoPago(Date ultimoPago) {
		this.ultimoPago = ultimoPago;
	}
	
	/**
	 * Actualiza el saldo vencido y el atraso maxio de las vetas 
	 * 
	 * @param vencidas
	 */
	/**
	public void actualizarSaldoVencido(final List<Venta> ventas){
		setSaldoVencido(BigDecimal.ZERO);
		CollectionUtils.forAllDo(ventas, new Closure(){
			public void execute(Object input) {
				final Venta v=(Venta)input;
				final BigDecimal saldo=v.getSaldo()!=null?v.getSaldo():BigDecimal.ZERO;
				setSaldoVencido(getSaldoVencido().add(saldo));
				
				if("CRE".equals(v.getOrigen()) && v.getCredito()!=null){					
					if(v.getSaldo().doubleValue()>1 && v.getCredito().getAtrasoOperativo()>0){
						int atraso=v.getCredito().getAtrasoOperativo();
						if(atraso>getAtrasoMaximo())
							setAtrasoMaximo(atraso);
					}
				}else{
					if(v.getSaldo().doubleValue()>1){
						
						Date actual=new Date();
						Date fecha=v.getVencimiento()!=null?v.getVencimiento():actual;
						long res=actual.getTime()-fecha.getTime();
						long dias=0;
						if(res>0)
							dias=(res/(86400*1000));
						int atraso=(int)dias;
						if(atraso>getAtrasoMaximo())
							setAtrasoMaximo(atraso);
					}
				}				
				
			}			
		});
	}
	
	public void actualizarYtd(final List<Venta> ventasYtd){
		setVcontadoYtd(BigDecimal.ZERO);
		setVcreditoYtd(BigDecimal.ZERO);
		setDevolucionesYtd(BigDecimal.ZERO);
		CollectionUtils.forAllDo(ventasYtd, new Closure(){
			public void execute(Object input) {
				Venta v=(Venta)input;
				if("CRE".equals(v.getOrigen())){
					setVcreditoYtd(getVcreditoYtd().add(v.getTotal().abs().amount()));
				}else if("CAM".equals(v.getOrigen()) || "MOS".equals(v.getOrigen())){
					setVcontadoYtd(getVcreditoYtd().add(v.getTotal().abs().amount()));
				}
				
			}			
		});
	}
	
	public void actualizarDescuentos(final List<Venta> ventasYtd){
		setDescuentosYtd(BigDecimal.ZERO);		
		CollectionUtils.forAllDo(ventasYtd, new Closure(){
			public void execute(Object input) {
				Venta v=(Venta)input;
				final BigDecimal desc=BigDecimal.valueOf(v.getDescuentos());//!=null?v.getSaldo():BigDecimal.ZERO;
				setDescuentosYtd(getDescuentosYtd().abs().add(desc));
			}			
		});
	}
	
	public void actualizarPagos(final List<Pago> pagosYtd){
		setPagosYtd(BigDecimal.ZERO);
		final int year=Periodo.obtenerYear(new Date());
		CollectionUtils.forAllDo(pagosYtd, new Closure(){
			public void execute(Object input) {
				Pago p=(Pago)input;
				if(p.getYear()==year){
					setPagosYtd(getPagosYtd().add(p.getImporte().amount()));
					if( (getUltimoPago()==null) || (p.getFecha().after(getUltimoPago())) )
						setUltimoPago(p.getFecha());
				}
			}			
		});
	}
	
	public void actualizarDevoluciones(final List<NotaDeCredito> devoluciones){
		setDevolucionesYtd(BigDecimal.ZERO);
		final int year=Periodo.obtenerYear(new Date());
		CollectionUtils.forAllDo(devoluciones, new Closure(){
			public void execute(Object input) {
				NotaDeCredito n=(NotaDeCredito)input;
				if(year==n.getYear()){
					if(n.getDevolucion()!=null){
						BigDecimal dev=n.getTotalAsBigDecimal()!=null?n.getTotalAsBigDecimal().abs():BigDecimal.ZERO;
						setDevolucionesYtd(getDevolucionesYtd().add(dev));
					}
				}
			}			
		});
	}

	public boolean isOrdenDeCompra() {
		return ordenDeCompra;
	}

	public void setOrdenDeCompra(boolean ordenDeCompra) {
		this.ordenDeCompra = ordenDeCompra;
	}

	public boolean isPagare() {
		return pagare;
	}

	public void setPagare(boolean pagare) {
		this.pagare = pagare;
	}

	public boolean isNotaAnticipada() {
		return notaAnticipada;
	}

	public void setNotaAnticipada(boolean notaAnticipada) {
		this.notaAnticipada = notaAnticipada;
	}

	public String getCorporativo() {
		return corporativo;
	}

	public void setCorporativo(String corporativo) {
		this.corporativo = corporativo;
	}
	
	**/
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((cliente == null) ? 0 : cliente.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ClienteCredito other = (ClienteCredito) obj;
		if (cliente == null) {
			if (other.cliente != null)
				return false;
		} else if (!cliente.equals(other.cliente))
			return false;
		return true;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
		.append(getClave())
		.append(getNombre())
		.append("Limite",getLimite())
		.append("Plazo",getPlazo())
		.append("Cobrador",getCobrador())
		.append("Operador",getOperador())
		.append("CheuqeP",isChequep())
		.append("Nota A",isNotaAnticipada())
		.append("Dia Rev:",getDia_revision())
		.append("Dia Pag:",getDia_pago())
		.toString();
	}

	public boolean isSuspenderDescuento() {
		return suspenderDescuento;
	}

	public void setSuspenderDescuento(boolean suspenderDescuento) {
		Object old=this.suspenderDescuento; 
		this.suspenderDescuento = suspenderDescuento;
		getPropertyChangeSupport().firePropertyChange("suspenderDescuento", old, suspenderDescuento);
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public int getAtrasoMaximo() {
		return atrasoMaximo;
	}

	public void setAtrasoMaximo(int atrasoMaximo) {
		this.atrasoMaximo = atrasoMaximo;
	}

	public Date getUltimaVenta() {
		return ultimaVenta;
	}

	public void setUltimaVenta(Date ultimaVenta) {
		this.ultimaVenta = ultimaVenta;
	}

	public CantidadMonetaria getVentaNeta() {
		return ventaNeta;
	}

	public void setVentaNeta(CantidadMonetaria ventaNeta) {
		this.ventaNeta = ventaNeta;
	}

	public BigDecimal getPagos() {
		return pagos;
	}

	public void setPagos(BigDecimal pagos) {
		this.pagos = pagos;
	}

	public int getFacturasVencidas() {
		return facturasVencidas;
	}

	public void setFacturasVencidas(int facturasVencidas) {
		this.facturasVencidas = facturasVencidas;
	}

	public int getVendedor() {
		return vendedor;
	}

	public void setVendedor(int vendedor) {
		this.vendedor = vendedor;
	}
	
	public String getEmail(){
		return getCliente().getCorreoelectronico1();
	}

	public double getComisionCobrador() {
		return comisionCobrador;
	}

	public void setComisionCobrador(double comisionCobrador) {
		double oldValue=this.comisionCobrador;
		this.comisionCobrador = comisionCobrador;
		getPropertyChangeSupport().firePropertyChange("comisionCobrador", oldValue, comisionCobrador);
	}

	public double getComisionVendedor() {
		return comisionVendedor;
	}

	public void setComisionVendedor(double comisionVendedor) {
		double oldValue=this.comisionVendedor;
		this.comisionVendedor = comisionVendedor;
		getPropertyChangeSupport().firePropertyChange("comisionVendedor", oldValue, comisionVendedor);
	}
	
	public boolean agregarFechaRevision(final Date fecha){
		if(calendario)
			return fechasRevision.add(fecha);
		return false;
	}
	
	public boolean agregarFechaPago(final Date fecha){
		if(calendario)
			return fechasPago.add(fecha);
		return false;
	}
	
	public boolean removerFechaRevision(final Date fecha){
		if(calendario)
			return fechasRevision.remove(fecha);
		return false;
	}
	
	public boolean removerFechaPago(final Date fecha){
		if(calendario)
			return fechasPago.remove(fecha);
		return false;
	}

	public boolean isCalendario() {
		return calendario;
	}

	public void setCalendario(boolean calendario) {
		boolean oldValue=this.calendario;		
		this.calendario = calendario;
		getPropertyChangeSupport().firePropertyChange("calendario", oldValue, calendario);
	}

	public Set<Date> getFechasPago() {
		return fechasPago;
	}

	public Set<Date> getFechasRevision() {
		return fechasRevision;
	}
	
	

}

