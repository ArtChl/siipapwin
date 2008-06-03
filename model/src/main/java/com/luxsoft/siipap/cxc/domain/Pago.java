package com.luxsoft.siipap.cxc.domain;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.utils.SystemUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * 
 * Pagos a facturas y Cargo a Notas de credito
 * 
 * TODO Vincular con Bancos
 * 
 * @author Ruben Cancino
 *
 */
public class Pago extends PersistentObject {
	
	/**
	 * Cliente
	 */
	private Cliente cliente;
	
	/**
	 * Venta a la que se le aplica el pago  
	 */
	private Venta venta;
	
	/**
	 * Nota de credito o cargo a la que se le aplica el pago
	 */
	private NotaDeCredito nota;
	
	/**
	 * Clave del cliente
	 */
	private String clave;
	
	/**
	 * Fecha del pago
	 */
	private Date fecha=new Date();
	
	/**
	 * Sucursal del origen del documento (Venta o Nota )
	 */
	private int sucursal;
	
	/**
	 * Archivo del que se importa el movimiento (CAM,CHE,CRE,JUR,MOS)
	 */
	private String origen;
	
	/**
	 * numero de factura o nota de credito segun sea el caso
	 */
	private long numero;
	
	/**
	 * Solo aplica para  ventas de mostrador y es el catalgo de los bancos 
	 * 
	 */
	private int institucion;
	
	/**
	 * Es la forma de pago es la clave (De un catalogo)
	 */
	private String formaDePago;
	
	/**
	 * Descripcion de la forma de pago
	 */
	private String descFormaDePago;
	
	/**
	 * numero del Cheque ,transferencia o nota con la que se esta pagando
	 * 
	 */
	private String referencia;
	
	/**
	 * Descripcion de la referencia (Comentario)
	 */
	private String descReferencia;	
	
	/**
	 * Importe del pago
	 */
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);	
	
	/**
	 * 
	 */
	private String comentario;
	
	/**
	 * Fecha en la que se genero un corte de caja por dia, agrupa los pagos de esa fecha
	 * 
	 */
	private Date corte;
	
	/**
	 * PENDIENTE
	 */
	private String estado;
	
	/**
	 * Fecha en la que se genero el pago
	 */
	private Date fechaReal;
	
	/**
	 * Nota de credito con la que se esta PAGANDO el importe del documento (Pago)
	 */
	private NotaDeCredito notaDelPago;
	
	
	private String tipoDocto;
	
	private int year=Periodo.obtenerYear(new Date());
	
	private int mes=Periodo.obtenerMes(new Date())+1;
	
	private FormaDePago formaDePago2=FormaDePago.H;
	
	private Date creado;
	
	private PagoM pagoM;	
	
	private int version;
	
	private double condonacion=0;
	
	private String tarjetaTip;
	
	/*Almacenamiento temporal para facilitar la Interfaz grafica*/
	
	private CantidadMonetaria descuento=CantidadMonetaria.pesos(0);
	
	private ChequeDevuelto cheque;
	
	public Pago(){	
		SystemUtils.sleep(55);		
		setCreado(new Date());
	}
	
	
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
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
	public Date getCreado() {
		return creado;
	}
	
	private void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(final CantidadMonetaria importe) {
		CantidadMonetaria old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe", old, importe);		
	}
	
	public long getNumero() {
		return numero;
	}
	public void setNumero(long numero) {
		this.numero = numero;
	}
	
	public int getSucursal() {
		return sucursal;
	}
	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	
		
	
	public NotaDeCredito getNota() {
		return nota;
	}
	public void setNota(NotaDeCredito nota) {
		this.nota = nota;
	}
	
	public Venta getVenta() {
		return venta;
	}
	public void setVenta(Venta venta) {
		this.venta = venta;
	}
	
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		Object old=this.comentario;
		this.comentario = comentario;
		getPropertyChangeSupport().firePropertyChange("comentario", old, comentario);
	}

	public String getDescFormaDePago() {
		return descFormaDePago;
	}

	public void setDescFormaDePago(String descFormaDePago) {
		Object old=this.descFormaDePago;
		this.descFormaDePago = descFormaDePago;
		getPropertyChangeSupport().firePropertyChange("descFormaDePago", old, descFormaDePago);
	}

	public String getDescReferencia() {
		return descReferencia;
	}

	public void setDescReferencia(String descReferencia) {		
		this.descReferencia = descReferencia;
	}

	public String getFormaDePago() {
		return formaDePago;
	}

	public void setFormaDePago(String formaDePago) {
		Object old=this.formaDePago;
		this.formaDePago = formaDePago;
		getPropertyChangeSupport().firePropertyChange("formaDePago",old,formaDePago);
	}

	public String getReferencia() {
		return referencia;
	}
	
	public Date getFechaReal() {
		return fechaReal;
	}
	public void setFechaReal(Date fechaReal) {
		this.fechaReal = fechaReal;
	}

	public void setReferencia(String referencia) {
		Object old=this.referencia;
		this.referencia = referencia;
		getPropertyChangeSupport().firePropertyChange("referencia",old,referencia);
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public Date getCorte() {
		return corte;
	}
	public void setCorte(Date corte) {
		this.corte = corte;
	}

	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}


	public int getInstitucion() {
		return institucion;
	}
	public void setInstitucion(int institucion) {
		this.institucion = institucion;
	}
	
	


	public NotaDeCredito getNotaDelPago() {
		return notaDelPago;
	}
	public void setNotaDelPago(NotaDeCredito notaDelPago) {		
		Object old=this.notaDelPago;
		this.notaDelPago = notaDelPago;
		getPropertyChangeSupport().firePropertyChange("notaDelPago",old,notaDelPago);
	}
	
	




	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof Pago)) {
			return false;
		}
		Pago rhs = (Pago) object;
		return new EqualsBuilder()
		.append(getId(), rhs.getId())
		.append(getCreado(), rhs.getCreado())		
		.isEquals();
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(8256909, -178324741)		
		.append(getId())
		.append(getCreado())		
		.toHashCode();
	}


	public String toString(){				
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Venta",getNumero())
		.append("Sucursal",getSucursal())
		.append("Cliente",getClave())
		.append("FP.",getDescFormaDePago())
		.append("Desc FP",getDescFormaDePago())
		.append("Referencia",getReferencia())
		.append("Desc Ref",getDescReferencia())
		.append("Importe",getImporte())
		.append(getEstado())
		.toString();
		
	}	


	public String getTipoDocto() {
		return tipoDocto;
	}
	public void setTipoDocto(String tipoDocto) {
		this.tipoDocto = tipoDocto;
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


	public FormaDePago getFormaDePago2() {
		return formaDePago2;
	}
	public void setFormaDePago2(FormaDePago formaDePago2) {
		Object old=this.formaDePago2;
		this.formaDePago2 = formaDePago2;
		getPropertyChangeSupport().firePropertyChange("formaDePago2", old, formaDePago2);
		setFormaDePago(formaDePago2.getId());
	}

	public double getCondonacion() {
		return condonacion;
	}
	public void setCondonacion(double condonacion) {
		this.condonacion = condonacion;
	}


	public PagoM getPagoM() {
		return pagoM;
	}


	public void setPagoM(PagoM grupo) {
		this.pagoM = grupo;
	}
	public CantidadMonetaria getDescuento() {
		return descuento;
	}


	public void setDescuento(CantidadMonetaria descuento) {
		this.descuento = descuento;
	}
	
	/**
	 * Utility para estimar el pago adecuado
	 * solo se usa como referencia en las formas
	 * 
	 * @return
	 */
	public CantidadMonetaria getPorPagar(){
		if(getVenta()!=null){			
			if(getPagoM()!=null){				
				if(getPagoM().isCondonar() ){
					//final CantidadMonetaria saldoBruto=getVenta().getSaldoEnMoneda();
					//final double descuento=getVenta().getDescuentoPactado();
					//final CantidadMonetaria saldoNeto=saldoBruto.multiply(descuento/100);
					//return getVenta().getSaldoEnMoneda().subtract(getVenta().getSaldoEstimadoSinCargo());
					
					final CantidadMonetaria porPagar=getVenta().getSaldoEstimadoSinCargo();					
					return porPagar;
				}	
				else {/*
					final CantidadMonetaria saldo=getVenta().getSaldoEnMoneda();
					final double descuento=getVenta().getDescuento();
					return saldo.multiply(descuento/100);*/
					//return getVenta().getSaldoEnMoneda().subtract(getVenta().getSaldoEstimado());
					return getVenta().getSaldoEstimado();
				}
					
			}else
				return CantidadMonetaria.pesos(0);
		}else
			return getNota().getSaldoAsMoneda();
	}
	
	
	public long getDocumento(){
		if(getVenta()!=null)
			return getVenta().getNumero();
		else if(getNota()!=null){
			return getNota().getNumero();
		}return 0;
	}
	
	public long getNumeroFiscal(){
		if(getVenta()!=null)
			return getVenta().getNumeroFiscal();
		else if(getNota()!=null){
			return getNota().getNumero();
		}return 0;
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
	
	public double getImporteAsDouble(){
		return getImporte().amount().doubleValue();
	}
	
	public String getCuentaDestino(){
		if(getPagoM()!=null){
			return getPagoM().getCuentaDeposito();
		}else
			return "";
		
	}
	
	public double getImporteSinIva(){
		return getImporteAsDouble()/1.15;
	}
	
	public boolean esPagoEnEfectivo(){
		switch (getFormaDePago2()) {
		case H:
		case N:
		case Y:
		case E:
		case C:
		case O:
		case B:
		case Q:
			return true;

		default:
			return false;
		}
	}
	
	/*
	 * H N Y E O
	 */
	public boolean esPagoEnEfectivoSinTarjetas(){
		switch (getFormaDePago2()) {
		case H:
		case N:
		case Y:
		case E:		
		case O:		
			return true;

		default:
			return false;
		}
	}

	public String getTarjetaTip() {
		return tarjetaTip;
	}
	public void setTarjetaTip(String tarjetaTip) {
		this.tarjetaTip = tarjetaTip;
	}


	public ChequeDevuelto getCheque() {
		return cheque;
	}
	public void setCheque(ChequeDevuelto cheque) {
		this.cheque = cheque;
	}
	
	

}
