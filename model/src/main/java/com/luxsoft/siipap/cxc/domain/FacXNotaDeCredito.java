package com.luxsoft.siipap.cxc.domain;

import java.sql.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.PersistentObject;

/**
 * TODO ELIMINAR
 * @deprecated
 * @author RUBEN
 *
 */
@SuppressWarnings("serial")
public class FacXNotaDeCredito extends PersistentObject {

	
	private long venta_ID;
	private String version;
	private String operacion;
	private Date fecha;
	private String serie;
	private String tipo;
	private long numero;
	private String clasificacion;
	private String cliente;
	private String nombre;
	private String socio;
	private String vendedor;
	private String cobrador;
	private Date vencimiento;
	private Date dia_Revision;
	private Date dia_Pago;
	private String plazo;
	private long descuentoFacturado;
	private long importe_Bruto;
	private String importe_Bruto_Mon;
	private long importe_Maniobras;
	private String importe_Maniobras_Mon;
	private String cortes;////
	private long precio_Corte;
	private String precio_Corte_Mon;
	private long importe_Cortes;
	private String importe_Cortes_Mon;
	private long subTotal;
	private String sub_Total_Mon;
	private long impuesto;
	private String impuesto_Mon;
	private long total;
	private String total_Mon;
	private long total_Kilos;
	private long devolucion;
	private String grupo;
	private Date creado;
	

	
	
	public String getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getCobrador() {
		return cobrador;
	}

	public void setCobrador(String cobrador) {
		this.cobrador = cobrador;
	}

	public String getCortes() {
		return cortes;
	}

	public void setCortes(String cortes) {
		this.cortes = cortes;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public long getDescuentoFacturado() {
		return descuentoFacturado;
	}

	public void setDescuentoFacturado(long descuentoFacturado) {
		this.descuentoFacturado = descuentoFacturado;
	}

	public long getDevolucion() {
		return devolucion;
	}

	public void setDevolucion(long devolucion) {
		this.devolucion = devolucion;
	}

	public Date getDia_Pago() {
		return dia_Pago;
	}

	public void setDia_Pago(Date dia_Pago) {
		this.dia_Pago = dia_Pago;
	}

	public Date getDia_Revision() {
		return dia_Revision;
	}

	public void setDia_Revision(Date dia_Revision) {
		this.dia_Revision = dia_Revision;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public long getImporte_Bruto() {
		return importe_Bruto;
	}

	public void setImporte_Bruto(long importe_Bruto) {
		this.importe_Bruto = importe_Bruto;
	}

	public String getImporte_Bruto_Mon() {
		return importe_Bruto_Mon;
	}

	public void setImporte_Bruto_Mon(String importe_Bruto_Mon) {
		this.importe_Bruto_Mon = importe_Bruto_Mon;
	}

	public long getImporte_Cortes() {
		return importe_Cortes;
	}

	public void setImporte_Cortes(long importe_Cortes) {
		this.importe_Cortes = importe_Cortes;
	}

	public String getImporte_Cortes_Mon() {
		return importe_Cortes_Mon;
	}

	public void setImporte_Cortes_Mon(String importe_Cortes_Mon) {
		this.importe_Cortes_Mon = importe_Cortes_Mon;
	}

	public long getImporte_Maniobras() {
		return importe_Maniobras;
	}

	public void setImporte_Maniobras(long importe_Maniobras) {
		this.importe_Maniobras = importe_Maniobras;
	}

	public String getImporte_Maniobras_Mon() {
		return importe_Maniobras_Mon;
	}

	public void setImporte_Maniobras_Mon(String importe_Maniobras_Mon) {
		this.importe_Maniobras_Mon = importe_Maniobras_Mon;
	}

	public long getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(long impuesto) {
		this.impuesto = impuesto;
	}

	public String getImpuesto_Mon() {
		return impuesto_Mon;
	}

	public void setImpuesto_Mon(String impuesto_Mon) {
		this.impuesto_Mon = impuesto_Mon;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getNumero() {
		return numero;
	}

	public void setNumero(long numero) {
		this.numero = numero;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public String getPlazo() {
		return plazo;
	}

	public void setPlazo(String plazo) {
		this.plazo = plazo;
	}

	public long getPrecio_Corte() {
		return precio_Corte;
	}

	public void setPrecio_Corte(long precio_Corte) {
		this.precio_Corte = precio_Corte;
	}

	public String getPrecio_Corte_Mon() {
		return precio_Corte_Mon;
	}

	public void setPrecio_Corte_Mon(String precio_Corte_Mon) {
		this.precio_Corte_Mon = precio_Corte_Mon;
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

	public String getSub_Total_Mon() {
		return sub_Total_Mon;
	}

	public void setSub_Total_Mon(String sub_Total_Mon) {
		this.sub_Total_Mon = sub_Total_Mon;
	}

	public long getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(long subTotal) {
		this.subTotal = subTotal;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getTotal_Kilos() {
		return total_Kilos;
	}

	public void setTotal_Kilos(long total_Kilos) {
		this.total_Kilos = total_Kilos;
	}

	public String getTotal_Mon() {
		return total_Mon;
	}

	public void setTotal_Mon(String total_Mon) {
		this.total_Mon = total_Mon;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getVendedor() {
		return vendedor;
	}

	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	public long getVenta_ID() {
		return venta_ID;
	}

	public void setVenta_ID(long venta_ID) {
		this.venta_ID = venta_ID;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!obj.getClass().isAssignableFrom(getClass())) return false;
		FacXNotaDeCredito other=(FacXNotaDeCredito)obj;
		return new EqualsBuilder()
			.append(venta_ID,other.getVenta_ID())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(venta_ID)
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append(getVenta_ID())
			.append(getNombre())			
			.toString();
		
	}
	
	

}
