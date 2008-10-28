package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.utils.SystemUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.utils.domain.MutableObject;

/**
 * Maestro de pagos con descuento, mantiene información
 * relacionada con un grupo de beans PagoDesc
 * 
 * TODO Agregar referencia a la cuenta destino en la que se piensa depositar el pago
 * 
 * @author Ruben Cancino
 *
 */
@Entity
@Table(name="SW_PAGOM")
public class PagoM extends MutableObject implements ClienteHolder{
	
	private Long id;
	
	private Cliente cliente;
	
	@Length (max=7)
	private String clave;
	
	private Date fecha=new Date();
	
	private FormaDePago formaDePago=FormaDePago.H;
	
	@Length (max=20, message="El tamaño máximo para la referencia es de 30 caracteres") 
	private String referencia;
	
	@Length (max=30)
	private String banco;	
	
	@Length (max=50,message="El tamaño máximo para el comantario es de 50 caracteres")
	private String comentario;
	
	//@MinCantidad ( min=0,message="No se permiten pagos con importes <=0.00")
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	
	private Set<Pago> pagos=new HashSet<Pago>();
	
	private double tipoDeCambio=1;	
	
	
	//private CantidadMonetaria disponible;
	private BigDecimal disponible=BigDecimal.ZERO;
	private BigDecimal saldo;
	
	private BigDecimal aplicado;
	
	private ClasificacionDeDiferencias diferenciaTipo=ClasificacionDeDiferencias.Saldo;
	
	private String tipoDeDocumento;
	
	private boolean condonar=false;
	
	@Length(max=30)
	private String cuentaDeposito;
	
	private Date creado;
	
	private Date modificado;
	
	private boolean cambiaria=false;
	
	private List<PagoM> children;
	
	
	
	private int year=Periodo.obtenerYear(new Date());

	private int mes=Periodo.obtenerMes(new Date())+1;
	
	private int version;
	
	
	

	public PagoM() {
		SystemUtils.sleep(55);
		this.creado=new Date();
	}
	
	public PagoM(final Cliente c,final String tipo){
		this();
		setCliente(c);
		setClave(c.getClave());
		setTipoDeDocumento(tipo);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	

	public Cliente getCliente() {		
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		Object old=this.cliente;
		this.cliente = cliente;
		getPropertyChangeSupport().firePropertyChange("cliente", old, cliente);
	}
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		String old=this.clave;
		this.clave = clave;
		getPropertyChangeSupport().firePropertyChange("clave", old, clave);
	}
	

	/**
	 * Banco origen del cheque con el que se paga
	 * 
	 * @return
	 */
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		Object old=this.banco;
		this.banco = banco;
		getPropertyChangeSupport().firePropertyChange("banco", old, banco);
	}

	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		Object old=this.comentario;
		this.comentario = comentario;
		getPropertyChangeSupport().firePropertyChange("comentario", old, comentario);
	}

	
	public Date getFecha() {
		return fecha;
	}
	
	public void setFecha(Date fecha) {
		Object old=this.fecha;
		this.fecha = fecha;				
		getPropertyChangeSupport().firePropertyChange("fecha", old, fecha);
		if(fecha!=null){
			setYear(Periodo.obtenerYear(fecha));
			setMes(Periodo.obtenerMes(fecha)+1);
		}
	}

	public FormaDePago getFormaDePago() {
		return formaDePago;
	}
	public void setFormaDePago(FormaDePago formaDePago) {
		Object old=this.formaDePago;
		this.formaDePago = formaDePago;
		getPropertyChangeSupport().firePropertyChange("formaDePago", old, formaDePago);
	}
	
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		Object old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe", old, importe);
	}	
	
	public Set<Pago> getPagos() {	
		return pagos;
	}
	@SuppressWarnings("unused")
	private void setPagos(Set<Pago> pagos) {
		this.pagos = pagos;
	}

	/**
	 * Agrega un pago  a partir de una venta
	 * @param v
	 * @return
	 */
	public Pago aplicarPago(final Venta v,final CantidadMonetaria impPago){
		final String pattern="La venta {0} no es de tipo {1}, es de tipo {2}";
		Assert.isTrue(getTipoDeDocumento().equalsIgnoreCase(v.getTipo()),MessageFormat.format(pattern, v.getId(),getTipoDeDocumento(),v.getTipo()));
		
		Pago p=crearPagoParaVenta(v);
		p.setFormaDePago2(getFormaDePago());		
		p.setFecha(getFecha());
		p.setYear(getYear());
		p.setMes(getMes());
		Assert.isTrue(getCliente().getClave().equals(v.getCliente().getClave()),
				"Los pagos deben ser del mismo cliente Cliente a:"+getCliente()+ "B: "+v.getCliente());
		
		p.setPagoM(this);
		getPagos().add(p);
		p.setImporte(impPago);
		return p;
	}
	
	/**
	 * Agrega un pago  a partir de una venta
	 * @param v
	 * @return
	 */
	public Pago aplicarPago(final NotaDeCredito cargo,final CantidadMonetaria impPago){
		
		Pago p=crearPagoParaCargo(cargo);
		p.setFormaDePago2(getFormaDePago());		
		p.setFecha(getFecha());
		p.setYear(getYear());
		p.setMes(getMes());
		Assert.isTrue(getCliente().getClave().equals(cargo.getCliente().getClave()),
				"Los pagos deben ser del mismo cliente Cliente a:"+getCliente()+ "B: "+cargo.getCliente());
		
		p.setPagoM(this);
		getPagos().add(p);
		p.setImporte(impPago);
		return p;
	}
	
	public void agregarPago(final Pago p){
		p.setPagoM(this);
		getPagos().add(p);
	}
	
	public boolean eliminarPago(final Pago pago){
		boolean res=getPagos().remove(pago);
		pago.setPagoM(null);
		return res;
	}

	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		Object old=this.referencia;
		this.referencia = referencia;
		getPropertyChangeSupport().firePropertyChange("referencia", old, referencia);
	}
	
	public BigDecimal getDisponible() {		
		return disponible;
	}
	public CantidadMonetaria getDisponibleAsMoneda(){
		if(getImporte()!=null)
			return new CantidadMonetaria(getDisponible().doubleValue(),getImporte().currency());
		return CantidadMonetaria.pesos(getDisponible().doubleValue());
	}
	
	public void setDisponible(BigDecimal disponible) {
		BigDecimal old=this.disponible;
		this.disponible=disponible;
		getPropertyChangeSupport().firePropertyChange("disponible",old,disponible);
	}
	
	public ClasificacionDeDiferencias getDiferenciaTipo() {		
		return diferenciaTipo;
	}
	public void setDiferenciaTipo(ClasificacionDeDiferencias diferenciaTipo) {
		Object old=this.diferenciaTipo;
		this.diferenciaTipo = diferenciaTipo;
		getPropertyChangeSupport().firePropertyChange("diferenciaTipo", old,diferenciaTipo );
	}
	
	public double getTipoDeCambio() {
		return tipoDeCambio;
	}
	public void setTipoDeCambio(double tipoDeCambio) {
		this.tipoDeCambio = tipoDeCambio;
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
	
	public Date getCreado() {
		return creado;
	}
	
	public Date getModificado() {
		return modificado;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	@SuppressWarnings("unused")
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}	
	
	/**
	 * Tipo de documentos (Facturas) que puede agregar
	 * 
	 * @return
	 */
	public String getTipoDeDocumento() {
		return tipoDeDocumento;
	}
	public void setTipoDeDocumento(String tipoDeDocumento) {
		this.tipoDeDocumento = tipoDeDocumento;
	}

	public boolean isCondonar() {
		return condonar;
	}
	public void setCondonar(boolean condonar) {
		boolean old=this.condonar;
		this.condonar = condonar;
		getPropertyChangeSupport().firePropertyChange("condonar", old, condonar);
	}

	/**
	 * Elimina todos los pagos con importe cero
	 */
	public void depurar(){
		final Iterator<Pago> iter=getPagos().iterator();
		while(iter.hasNext()){
			Pago pp=iter.next();
			if(pp.getImporte().abs().amount().doubleValue()==0){
				iter.remove();
			}

		}
	}
	
	/**
	 * Calcula el saldo disponible del pago para ser aplicado a facturas
	 * 
	 * @return
	 */
	/**
	public CantidadMonetaria calcularDisponible(){
		CantidadMonetaria disponible=getImporte();
		for(Pago p:getPagos()){			
			disponible=disponible.subtract(p.getImporte());
		}
		setDisponible(disponible.abs());
		return disponible;
	}
	**/
	/**
	 * Acceso a las ventas represnetadas por el pago
	 * @return
	 */
	public List<Venta> getVentasAplicadas(){
		final List<Venta> ventas=new ArrayList<Venta>();
		for(Pago p:getPagos()){
			if(p.getVenta()!=null)
				ventas.add(p.getVenta());
		}
		return ventas;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		PagoM g=(PagoM)obj;
		return new EqualsBuilder()
		.append(getId(), g.getId())
		.append(getCreado(), g.getCreado())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(15,35)
		.append(getId())
		.append(getCreado())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Cliente: ",getClave())
		.append("Fecha",getFecha())
		.append("Imorte: "+getImporte())
		.append("Aplicado: "+getAplicado())
		.append("Disponible: ",getDisponible())
		.append("Saldo: ",getSaldo())
		.toString();
	}
	
	/**
	 * Crea un pago a partir de una venta, el importe del pago
	 * esta en funcion de la provision
	 * 
	 * @param v
	 * @return
	 */
	public static Pago crearPagoParaVenta(final Venta v){
		Pago p=new Pago();
		p.setVenta(v);
		p.setOrigen(v.getOrigen());
		p.setTipoDocto(v.getTipo());
		p.setSucursal(v.getSucursal());		
		p.setCliente(v.getCliente());
		p.setClave(v.getClave());
		p.setNumero(v.getNumero());
		return p;
	}
	
	/** Crea un pago a partir de una Nota de cargo, el importe del pago
	 * esta en funcion del saldo
	 * 
	 * @param v
	 * @return
	 */
	public static Pago crearPagoParaCargo(final NotaDeCredito cargo){
		Pago p=new Pago();
		p.setNota(cargo);
		p.setOrigen(cargo.getOrigen());
		p.setTipoDocto(cargo.getTipo());
		p.setSucursal(1);		
		p.setCliente(cargo.getCliente());
		p.setClave(cargo.getClave());
		p.setNumero(cargo.getNumero());
		return p;
	}

	public String getCuentaDeposito() {
		return cuentaDeposito;
	}
	public void setCuentaDeposito(String cuentaDeposito) {
		Object old=this.cuentaDeposito;
		this.cuentaDeposito = cuentaDeposito;
		getPropertyChangeSupport().firePropertyChange("cuentaDeposito", old, cuentaDeposito);
	}

	public BigDecimal getAplicado() {
		return aplicado;
	}

	public void setAplicado(BigDecimal aplicado) {
		this.aplicado = aplicado;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public String getLabel(){
		return "Pago";
	}
	
	/**
	 * 
	 * @param fecha
	 * @return
	 * 
	 */
	public CantidadMonetaria getSaldoAl(final Date fecha){		
		if(generaDisponible()){
			CantidadMonetaria total=getImporte();
			for(Pago p:getPagos()){
				if(p.getFecha().compareTo(fecha)<=0)
					total=total.subtract(p.getImporte());
			}
			if(getChildren()!=null){
				for(PagoM child:getChildren()){
					if(child.getFecha().compareTo(fecha)<0){
						//total=total.subtract(child.getImporte());
					}
				}
			}
			return total;
			
		}
		return CantidadMonetaria.pesos(0);
	}
	public double getSaldoAsDouble(){
		return getSaldo().doubleValue();
	}
	
	public boolean esPagoNormal(){
		return (getClass().equals(PagoM.class)  && (!getFormaDePago().equals(FormaDePago.D)));
	}
	public boolean generaDisponible(){
		return (esPagoNormal() || getClass().equals(Anticipo.class));
	}
	
	public String getCuenta(){
		if(getCliente().getCredito()!=null)
			return getCliente().getCredito().getCuenta();
		return "";
	}
	
	public double getImporteAsDouble(){
		return getImporte().amount().doubleValue();
	}
	public String getFormaDePagoAsString(){
		return getFormaDePago().name();
	}

	public List<PagoM> getChildren() {
		return children;
	}

	public void setChildren(List<PagoM> children) {
		this.children = children;
	}

	public boolean isCambiaria() {
		return cambiaria;
	}

	public void setCambiaria(boolean cambiaria) {
		this.cambiaria = cambiaria;
	}
	
	/**
	 * Util para importar datos de deposito
	 * 
	 */
	private DepositoRow depositoRow;


	public DepositoRow getDepositoRow() {
		return depositoRow;
	}

	public void setDepositoRow(DepositoRow depositoRow) {
		this.depositoRow = depositoRow;
		setReferencia(String.valueOf(depositoRow.getNumero()));
		setFormaDePago(depositoRow.getFP());
		setBanco(depositoRow.getBanco());
		CantidadMonetaria imp=CantidadMonetaria.pesos(depositoRow.getImporte().doubleValue());
		setImporte(imp);
		System.out.println("Cuenta destino: "+depositoRow.getCuentaDeposito());
		setCuentaDeposito(depositoRow.getCuentaDeposito());
		
	}
	
	
	
	
}
