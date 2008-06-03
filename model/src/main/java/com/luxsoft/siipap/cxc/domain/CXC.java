package com.luxsoft.siipap.cxc.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Bean para unificar lo que son cargos y abonos de un cliente
 * 
 * @author Ruben Cancino
 * @Deprecated usar CXC2
 *
 */
@Deprecated
public class CXC implements Serializable{
	
	private Long id;
	
	private int version;
	
	/**
	 * Clave del cliente
	 * 
	 */
	@Length(max=7 , message="La clave debe ser de maximo 7 caracteres")
	private String clave;
	
	/**
	 * Nombre del cliente
	 */
	@Length(max=55)
	private String nombre;
	
	/**
	 * Documento del movimiento puede ser una factura/Nota de Credito o Nota de Cargo
	 * 
	 */	
	private long documento;
	
	/**
	 * Documento afectado por el movimiento
	 */	
	private long referencia;
	
	private int sucursal;
	
	/**
	 * Tipo del movimiento
	 * 
	 */
	@Length(max=3)
	private String tipo;
	
	/**
	 * Fecha del movimiento
	 */
	private Date fecha;
	
	/**
	 * Importe del movimiento
	 */
	private CantidadMonetaria importe;
	
	/**
	 * Origen del movimiento. Debe apuntar a un Id valido
	 * 
	 */
	private Long origen;
	
	private CantidadMonetaria saldo;
	
	private Long factura_id;
	
	private Long cargo_id;
	
	@Length(max=1 )
	private String tipDocto;
	
	@Length(max=1 )
	private String serieDocto;
	
	
	/**
	 * Es la forma de pago es la clave (De un catalogo)
	 */
	@Length(max=1 )
	private String formaDePago;
	
	/**
	 * Descripcion de la forma de pago
	 */
	@Length(max=30 )
	private String descFormaDePago;
	
	/**
	 * numero del Cheque ,transferencia o nota con la que se esta pagando
	 * 
	 */
	@Length(max=20 )
	private String referenciaPago;
	
	/**
	 * 
	 */
	@Length(max=20 )
	private String bancoOrigen;
	
	
	
	
	private Date creado;
	
	private int year;
	
	private int mes;


	public String getClave() {
		return clave;
	}


	public void setClave(String clave) {
		this.clave = clave;
	}


	public Date getCreado() {
		return creado;
	}


	public void setCreado(Date creado) {
		this.creado = creado;
	}


	public long getDocumento() {
		return documento;
	}


	public void setDocumento(long documento) {
		this.documento = documento;
	}


	public Date getFecha() {
		return fecha;
	}


	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public CantidadMonetaria getImporte() {
		return importe;
	}


	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public Long getOrigen() {
		return origen;
	}


	public void setOrigen(Long origen) {
		this.origen = origen;
	}


	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public int getVersion() {
		return version;
	}


	public void setVersion(int version) {
		this.version = version;
	}


	
	
	
	public String toString(){
		return  new ToStringBuilder(this,ToStringStyle.DEFAULT_STYLE)
		.append(getId())
		.append(getClave())
		.append(getDocumento())
		.append(getTipo())
		.append(getFecha())
		.append(getImporte())
		.toString();
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


	public int getSucursal() {
		return sucursal;
	}


	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	
	public CantidadMonetaria getSaldo() {
		return saldo;
	}
	public void setSaldo(CantidadMonetaria saldo) {
		this.saldo = saldo;
	}
	


	public long getReferencia() {
		return referencia;
	}


	public void setReferencia(long referencia) {
		this.referencia = referencia;
	}


	public static CXC registrar(final Venta v){
		CXC cxc=new CXC();
		cxc.setClave(v.getClave());
		cxc.setNombre(v.getNombre());
		cxc.setFactura_id(v.getId());
		cxc.setDocumento(v.getNumero());
		cxc.setReferencia(v.getNumero());
		cxc.setSerieDocto(v.getSerie());
		cxc.setTipDocto(v.getTipo());
		cxc.setFecha(v.getFecha());
		cxc.setImporte(v.getTotal());
		cxc.setSaldo(v.getSaldoEnMoneda());
		cxc.setOrigen(v.getId());
		cxc.setSucursal(v.getSucursal());
		cxc.setTipo("FAC");
		cxc.setYear(v.getYear());
		cxc.setMes(v.getMes());
		cxc.setCreado(new Date(System.currentTimeMillis()+15));
		return cxc;
	}
	
	public static CXC registrarCredito(final NotasDeCreditoDet credito){
		CXC cxc=new CXC();
		cxc.setClave(credito.getFactura().getClave());
		cxc.setNombre(credito.getFactura().getNombre());
		cxc.setFactura_id(credito.getFactura().getId());
		cxc.setDocumento(credito.getNota().getNumero());
		cxc.setReferencia(credito.getFactura().getNumero());
		cxc.setFecha(credito.getNota().getFecha());
		cxc.setImporte(credito.getImporte());
		cxc.setSaldo(CantidadMonetaria.pesos(0));
		cxc.setOrigen(credito.getId());
		cxc.setSucursal(credito.getFactura().getSucursal());
		cxc.setTipDocto(credito.getTipoDocumento());
		cxc.setSerieDocto(credito.getSerieDocumento());
		cxc.setTipo("CRE");
		cxc.setYear(credito.getYear());
		cxc.setMes(credito.getMes());
		cxc.setCreado(new Date(System.currentTimeMillis()+15));
		return cxc;
	}
	
	public static CXC registrarCargo(final NotaDeCredito cargo){
		CXC cxc=new CXC();
		cxc.setClave(cargo.getClave());
		cxc.setNombre(cargo.getCliente().getNombre());
		cxc.setCargo_id(cargo.getId());
		cxc.setDocumento(cargo.getNumero());
		cxc.setReferencia(cargo.getNumero());
		cxc.setFecha(cargo.getFecha());
		cxc.setImporte(cargo.getImporte());	
		cxc.setSaldo(cargo.getSaldoDelCargoEnMoneda());
		cxc.setOrigen(cargo.getId());
		cxc.setTipDocto(cargo.getTipo());
		cxc.setSerieDocto(cargo.getSerie());
		cxc.setSucursal(1);
		cxc.setTipo("CAR");
		cxc.setYear(cargo.getYear());
		cxc.setMes(cargo.getMes());
		cxc.setCreado(new Date(System.currentTimeMillis()+15));
		return cxc;
	}
	
	public static CXC registrarPago(final Pago pago){
		CXC cxc=new CXC();		
		if(pago.getVenta()!=null){
			final Venta v=pago.getVenta();			
			cxc.setDocumento(v.getNumero());
			cxc.setReferencia(v.getNumero());
			cxc.setFactura_id(v.getId());
			cxc.setSerieDocto(v.getSerie());
			cxc.setTipDocto(pago.getTipoDocto());
			
		}else if(pago.getNota()!=null){
			final NotaDeCredito credito=pago.getNota();
			cxc.setDocumento(credito.getNumero());
			cxc.setReferencia(credito.getNumero());
			cxc.setCargo_id(credito.getId());
			cxc.setSerieDocto(credito.getSerie());
			cxc.setTipDocto(credito.getTipo());
		}
		
		cxc.setSaldo(CantidadMonetaria.pesos(0));
		cxc.setClave(pago.getClave());
		cxc.setNombre(pago.getCliente().getNombre());
		cxc.setImporte(pago.getImporte());
		cxc.setOrigen(pago.getId());
		cxc.setSucursal(pago.getSucursal());
		cxc.setFecha(pago.getFecha());
		cxc.setTipo("PAG");
		cxc.setYear(pago.getYear());
		cxc.setMes(pago.getMes());
		cxc.setCreado(new Date(System.currentTimeMillis()+15));
		return cxc;
	}


	public Long getCargo_id() {
		return cargo_id;
	}
	public void setCargo_id(Long cargo_id) {
		this.cargo_id = cargo_id;
	}

	public Long getFactura_id() {
		return factura_id;
	}
	public void setFactura_id(Long factura_id) {
		this.factura_id = factura_id;
	}


	public String getSerieDocto() {
		return serieDocto;
	}


	public void setSerieDocto(String serieDocto) {
		this.serieDocto = serieDocto;
	}


	public String getTipDocto() {
		return tipDocto;
	}


	public void setTipDocto(String tipDocto) {
		this.tipDocto = tipDocto;
	}
	
	

}
