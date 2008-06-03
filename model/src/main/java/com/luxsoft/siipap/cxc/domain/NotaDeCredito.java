package com.luxsoft.siipap.cxc.domain;



import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;

import com.luxsoft.siipap.annotations.UIProperty;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * 
 * Se toma como base:
 * 
 * 	- El analisis para un nuevo modelo homogeno de Notas de Credito
 *  - La estructura de la tablas:
 *  
 *  	- MOCOA
 *  	- MOCOMO
 *  	- MOVCRE
 * 
 * @author RUBEN
 *
 */
public class NotaDeCredito extends PersistentObject implements ClienteHolder{
	
	
	/**
	 * Opcional  y se usa solo para notas de credito por devolucion
	 * 
	 */
	private Devolucion devolucion;
	
	/**
	 * Obligatorio
	 */
	private Cliente cliente;
	
	/**
	 * Clave del cliente
	 */
	private String clave;
	
	/**
	 * Fecha de la nota 
	 */
	private Date fecha=new Date();
		 
	/**
	 * Clasificacion de la nota
	 */
	private String serie;
	
	/**
	 * Clasificacion de la nota
	 */
	private String tipo;
	
	/**
	 * Consecutivo Interno
	 */
	private long numero;
	
	/**
	 * Folio 
	 */
	private long numeroFiscal;
	
	/**
	 * Es opcional para las notas de credito por descuento
	 * y es el mismo para todas sus partidas (facturas)
	 */
	@UIProperty (isPorcentage=true)
	private double descuento;
	
	
	
	/**
	 * Importe de la nota de credito (Es la suma de sus partidas)
	 * Este campo debe ser dinamico mediante un sub select a la tabla de detalle
	 * 
	 */
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	
	/**
	 * Importe antes de descuento
	 */
	private CantidadMonetaria importeBruto=CantidadMonetaria.pesos(0);
	
	
	
	/**
	 * Impuesto de la nota de credito (Es la suma de sus partidas)
	 * Debe ser dinamico importe*.15
	 * 
	 */
	private double impuesto;
	
	/**
	 * Total de la nota, debe ser dinamica mediante 
	 * importe+impuesto
	 * 
	 */
	private double total;
		
	/**
	 * Total de pagos aplicados con esta nota de credito
	 *  
	 */
	private double pagos;
	
	/**
	 * Total de pagos abonados a la nota de cargo
	 */
	private double pagosDeCargos;
	
	/**
	 * Por compatibilidad a SIIPAP CLIPPER
	 * Es parte de la llave actual
	 * 
	 */
	private int grupo;
	
	/**
	 * Actualmente no se usa en el maestro pero en la importacion de datos
	 * se utilizara un registro del detalle en donde el renglon sea nulo
	 * 
	 */
	private String comentario;
	
	/**
	 * Verdadero si el documento ha sido impreso por lo menos una vez
	 */
	private Date impreso;
	
	private List<NotasDeCreditoDet> partidas;
	
	/**
	 * Origen de imporatcion CRE/CAM/MOS/CHE/JUR
	 */
	private String origen="";
	
	private int year=Periodo.obtenerYear(new Date());

	private int mes=Periodo.obtenerMes(new Date())+1;
	
	private long numeroDevo;
	
	private int sucursalDevo;
	
	private double saldo;
	
	
	
	private Date creado=currentTime();
	
	private int version;
	
	private String comentario2;
	
	private boolean aplicable=false;
	
	private boolean descuentoAnticipado=false;
	
	
	
	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		Object old=this.cliente;
		this.cliente = cliente;
		getPropertyChangeSupport().firePropertyChange("cliente", old, cliente);
		//setClave(cliente.getClave());
	}

	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		double old=this.descuento;
		this.descuento = descuento;
		getPropertyChangeSupport().firePropertyChange("descuento", old, descuento);
	}

	public Devolucion getDevolucion() {
		return devolucion;
	}

	public void setDevolucion(Devolucion devolucion) {
		Object old=this.devolucion;
		this.devolucion = devolucion;
		getPropertyChangeSupport().firePropertyChange("devolucion",old,devolucion);
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	

	public double getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(double impuesto) {
		double old=this.impuesto;
		this.impuesto = impuesto;
		getPropertyChangeSupport().firePropertyChange("impuesto",old,impuesto);
	}

	public long getNumero() {
		return numero;
	}

	public void setNumero(long numero) {
		long old=this.numero;
		this.numero = numero;
		getPropertyChangeSupport().firePropertyChange("numero", old, numero);
	}

	public long getNumeroFiscal() {
		return numeroFiscal;
	}

	public void setNumeroFiscal(long numeroFiscal) {
		long old=this.numeroFiscal;
		this.numeroFiscal = numeroFiscal;
		getPropertyChangeSupport().firePropertyChange("numeroFiscal", old, numeroFiscal);
	}
/**
	public BigDecimal getSaldo() {
		if(getSerie().equals("U") || getSerie().equals("V") || getSerie().equals("M"))
			return BigDecimal.ZERO;
		return BigDecimal.valueOf(getTotal()+getPagos());
	}	
**/
	
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public double getTotal() {
		return total;
	}
	
	/**
	 * Regresa el total de la nota en moneda
	 * 
	 * @return
	 */
	public CantidadMonetaria getTotalAsMoneda2(){
		return MonedasUtils.calcularTotal(getImporte());
	}
	
	public BigDecimal getTotalAsBigDecimal(){
		return getImporte().multiply(1.15).amount();
	}

	public void setTotal(double total) {
		double old=this.total;
		this.total = total;
		getPropertyChangeSupport().firePropertyChange("total",old,total);
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}	

	public CantidadMonetaria getImporte() {
		return importe;
	}

	public void setImporte(CantidadMonetaria importe) {
		Object old=this.importe;
		this.importe = importe;		
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
		setIva(importe.multiply(.15));
		setTotalAsMoneda(importe.multiply(1.15));
	}

	public CantidadMonetaria getImporteBruto() {
		return importeBruto;
	}

	public void setImporteBruto(CantidadMonetaria importeBruto) {
		this.importeBruto = importeBruto;
	}

	public Date getImpreso() {
		return impreso;
	}

	public void setImpreso(Date impreso) {
		this.impreso = impreso;
	}

	public List<NotasDeCreditoDet> getPartidas() {		
		if(partidas==null)
			partidas=new ArrayList<NotasDeCreditoDet>();
		return partidas;
	}

	public void setPartidas(List<NotasDeCreditoDet> partidas) {
		this.partidas = partidas;
	}
	
	public void agregarPartida(NotasDeCreditoDet det){
		det.setNota(this);
		det.setClave(getClave());
		det.setFecha(getFecha());
		getPartidas().add(det);
	}
	
	

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		//.append(getCliente())
		//.append(getNumero())
		//.append(getSerie())		
		//.append(getTipo())
		.append(getId())
		.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NotaDeCredito other = (NotaDeCredito) obj;
		return new EqualsBuilder()
		.append(getId(), other.getId())
		//.append(getCliente(), other.getCliente())
		//.append(getNumero(),other.getNumero())
		//.append(getSerie(),other.getSerie())
		//.append(getTipo(),other.getTipo())
		.isEquals();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append("Id",getId())
		.append(getClave())
		.append(getNumero())
		.append(getSerie())
		.append(getTipo())
		.append(getOrigen())
		.append(getSaldo())
		.toString();
	}

	public long getNumeroDevo() {
		return numeroDevo;
	}
	public void setNumeroDevo(long numeroDevo) {
		this.numeroDevo = numeroDevo;
	}
	public int getSucursalDevo() {
		return sucursalDevo;
	}
	public void setSucursalDevo(int sucursalDevo) {
		this.sucursalDevo = sucursalDevo;
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
	
	public double getPagos() {
		return pagos;
	}
	public void setPagos(double pagos) {
		this.pagos = pagos;
	}
	
	public CantidadMonetaria getPagosUnificados(){
		if(getSerie().equals("M")){
			final double pagos= getPagosDeCargos();
			return CantidadMonetaria.pesos(pagos);
		}else{
			final double pagos= getPagos();
			return CantidadMonetaria.pesos(pagos);
		}
	}
	
	public double getSaldo() {
		if(isAplicable())
			return saldo;
		else
			return 0;
	}
	
	public CantidadMonetaria getSaldoAsMoneda(){
		double sal=getSaldo();
		if(Math.abs(sal)<0.1)
			return CantidadMonetaria.pesos(0);
		return new CantidadMonetaria(getSaldo(),getImporte().currency());
	}
	
	private double saldoDelCargo=0;
	
	public double getSaldoDelCargo(){
		return saldoDelCargo;
	}
	public void setSaldoDelCargo(double saldoDelCargo){
		this.saldoDelCargo=saldoDelCargo;
	}
	
	public CantidadMonetaria getSaldoDelCargoEnMoneda(){
		//double cargo=getSaldoDelCargo();
		//if(Math.abs(cargo)<=1)
			//return CantidadMonetaria.pesos(0);
		// else
			return new CantidadMonetaria(getSaldoDelCargo(),getImporte().currency());
	}
	
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	
	/**
	 * Actualiza el importe de la nota en funcion de sus partidas
	 * no aplica para oriten=CHE
	 *
	 */
	public void actualizar(){
		if(getOrigen()!=null && getOrigen().equals("CHE"))
			return;
		CantidadMonetaria importe=null;
		for(NotasDeCreditoDet det:getPartidas()){
			det.setFecha(getFecha());
			det.setClave(getClave());
			det.setOrigen(getOrigen());
			det.setTipo(getTipo());
			det.setSerie(getSerie());
			det.setNumero(getNumero());
			if(importe==null){
				importe=det.getImporte();
				continue;
			}
			importe=importe.add(det.getImporte());
			
		}
		if(importe==null) importe=CantidadMonetaria.pesos(0);
		CantidadMonetaria impsv=importe.divide(BigDecimal.valueOf(1.15));
		setImporte(impsv);
		setImpuesto(MonedasUtils.calcularImpuesto(impsv).amount().doubleValue());
		setTotal(MonedasUtils.calcularTotal(impsv).amount().doubleValue());
		actualizarDescuento();		
		
	}
	
	/**
	 * Actualiza el descuento en funcion de las partidas 
	 *
	 */
	private void actualizarDescuento(){
		
	}
	
	@Length(min=0,max=200)
	public String getComentario2() {
		return comentario2;
	}
	
	public void setComentario2(String comentario2) {
		this.comentario2 = comentario2;
	}
	/**
	@Length(min=0,max=55)
	public String getConceptoBonificacion() {
		return conceptoBonificacion;
	}
	public void setConceptoBonificacion(String conceptoBonificacion) {
		final Object old=this.conceptoBonificacion;
		this.conceptoBonificacion = conceptoBonificacion;
		getPropertyChangeSupport().firePropertyChange("conceptoBonificacion",old,conceptoBonificacion);
	}
	
	**/
	
	private ConceptoDeBonificacion bonificacion;

	public ConceptoDeBonificacion getBonificacion() {		
		return bonificacion;
	}
	public void setBonificacion(ConceptoDeBonificacion bonificacion) {
		Object old=this.bonificacion;
		this.bonificacion = bonificacion;
		getPropertyChangeSupport().firePropertyChange("bonificacion", old, bonificacion);
		
	}
	
	public void asignarDevolucion(final Devolucion d){
		setDevolucion(d);
	}
	
	/** Propiedades para GUI no persistentes ***/
	
	private CantidadMonetaria iva;
	private CantidadMonetaria totalAsMoneda;
	

	public CantidadMonetaria getIva() {
		return iva;
	}
	public void setIva(CantidadMonetaria iva) {
		Object old=this.iva;
		this.iva = iva;
		getPropertyChangeSupport().firePropertyChange("iva",old,iva);		
	}
	
	/**
	 * 
	 * @return
	 * @deprecated Usar getTotalAsMoneda2()
	 * 
	 */
	public CantidadMonetaria getTotalAsMoneda() {
		return totalAsMoneda;
	}
	public void setTotalAsMoneda(CantidadMonetaria totalAsMoneda) {
		Object old=this.totalAsMoneda;
		this.totalAsMoneda = totalAsMoneda;
		getPropertyChangeSupport().firePropertyChange("totalAsMoneda",old,totalAsMoneda);
	}
	
	
	public boolean isAplicable() {
		return aplicable;
	}

	public void setAplicable(boolean aplicable) {
		this.aplicable = aplicable;
	}
	public boolean isDescuentoAnticipado() {
		return descuentoAnticipado;
	}
	public void setDescuentoAnticipado(boolean descuentoAnticipado) {
		this.descuentoAnticipado = descuentoAnticipado;
	}
	
		
	public CantidadMonetaria getSaldoUnificado(){
		if(getSerie().equals("M")){
			return getSaldoDelCargoEnMoneda();
		}else{
			if(isAplicable())
				return getSaldoAsMoneda();
			return CantidadMonetaria.pesos(0);
		}
	}
	
	
	private Date fechaRevisionCxC;
	private Date fechaPagoCxC;

	public Date getFechaRevisionCxC() {
		return fechaRevisionCxC;
	}
	public void setFechaRevisionCxC(Date vencimiento) {
		this.fechaRevisionCxC = vencimiento;
	}
	
	public void actualizarVencimiento(){
		
	}
	public double getPagosDeCargos() {
		return pagosDeCargos;
	}
	public void setPagosDeCargos(double pagosDeCargos) {
		this.pagosDeCargos = pagosDeCargos;
	}
	public Date getFechaPagoCxC() {
		return fechaPagoCxC;
	}
	public void setFechaPagoCxC(Date fechaPagoCxC) {
		this.fechaPagoCxC = fechaPagoCxC;
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
	
}
