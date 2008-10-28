package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Bean para facilitar la busqueda de depositos 
 * 
 * @author RUBEN
 *
 */
public class DepositoRow {
	
	private Long depositoId;
	private String banco;
	private int numero;
	private BigDecimal importe;
	private String clave;
	private String nombre;
	private Long clienteId;
	private String formaDePago;
	private String cuentaDeposito;
	
	
	public Long getDepositoId() {
		return depositoId;
	}
	public void setDepositoId(Long depositoId) {
		this.depositoId = depositoId;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
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
	public Long getClienteId() {
		return clienteId;
	}
	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}
	public String getFormaDePago() {
		return formaDePago;
	}
	public void setFormaDePago(String formaDePago) {
		this.formaDePago = formaDePago;
	}
	
	public String toString(){
		String pattern="Ref: {0} Importe:{1}";
		return MessageFormat.format(pattern, numero,importe);
	}
	
	public FormaDePago getFP(){
		return FormaDePago.valueOf(getFormaDePago());
	}
	public String getCuentaDeposito() {
		return cuentaDeposito;
	}
	public void setCuentaDeposito(String cuentaDeposito) {
		this.cuentaDeposito = cuentaDeposito;
	}
	

}
