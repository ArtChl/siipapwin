package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class ComisionDeVentas {
	
	private Long venta;
	private Date fechaVenta;
	private String Origen;
	private Date fechaPago;
	private int cobrador;
	private int vendedor;
	private int sucursal;
	private String clave;
	private String nombre;
	private long numero;
	private String serie;
	private String tipo;
	private BigDecimal importe;
	private BigDecimal total;
	private BigDecimal notasAplicadas;
	private BigDecimal ventaNeta;
	private BigDecimal pagos;
	private BigDecimal pagosComisionables;	
	private BigDecimal saldo;
	private Date vencimiento;
	private double descuento;
	private int atraso;
	private double comision;
	private boolean aplicado=false;
	private String cancelComentario;
	
	public int getAtraso() {
		return atraso;
	}
	public void setAtraso(int atraso) {
		this.atraso = atraso;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		this.comision = comision;
	}
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	public Date getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	public BigDecimal getNotasAplicadas() {
		return notasAplicadas;
	}
	public void setNotasAplicadas(BigDecimal notasAplicadas) {
		this.notasAplicadas = notasAplicadas;
	}
	public long getNumero() {
		return numero;
	}
	public void setNumero(long numero) {
		this.numero = numero;
	}
	public String getOrigen() {
		return Origen;
	}
	public void setOrigen(String origen) {
		Origen = origen;
	}
	public BigDecimal getPagos() {
		return pagos;
	}
	public void setPagos(BigDecimal pagos) {
		this.pagos = pagos;
	}
	public BigDecimal getPagosComisionables() {
		return pagosComisionables;
	}
	public void setPagosComisionables(BigDecimal pagosComisionables) {
		this.pagosComisionables = pagosComisionables;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public int getSucursal() {
		return sucursal;
	}
	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Date getVencimiento() {
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
	
	public Long getVenta() {
		return venta;
	}
	public void setVenta(Long venta) {
		this.venta = venta;
	}
	public BigDecimal getVentaNeta() {
		return ventaNeta;
	}
	public void setVentaNeta(BigDecimal ventaNeta) {
		this.ventaNeta = ventaNeta;
	}
	public boolean isAplicado() {
		return aplicado;
	}
	public void setAplicado(boolean aplicado) {
		this.aplicado = aplicado;
	}
	
	public BigDecimal getImporte(){
		if(isAplicado())
			return importe;
		final BigDecimal com;
		if(getVendedor()!=0)
			com=getAtraso()<=30?BigDecimal.valueOf(getComision()/100):BigDecimal.ZERO;
		else{
			com=BigDecimal.valueOf(getComision()/100);	
			}
				
		BigDecimal res=com;//.divide(BigDecimal.valueOf(1.15), 2, RoundingMode.HALF_EVEN);
		return getPagosComisionables().multiply(res);
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	
	public int getCobrador() {
		return cobrador;
	}
	public void setCobrador(int cobrador) {
		this.cobrador = cobrador;
	}
	public String getCancelComentario() {
		return cancelComentario;
	}
	public void setCancelComentario(String cancelComentario) {
		this.cancelComentario = cancelComentario;
	}
	
	
	

	

}
