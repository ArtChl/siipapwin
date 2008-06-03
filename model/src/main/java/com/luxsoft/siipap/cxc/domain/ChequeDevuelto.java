package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.Sucursales;
import com.luxsoft.utils.domain.MutableObject;

@Entity
@Table (name="CHEQUES_DEVUELTOS")
public class ChequeDevuelto extends MutableObject{
	
	private Long id;
	
	@NotNull (message="Debe seleccionar un cliente para continuar")
	private Cliente cliente;	
	
	private PagoM origen;
	
	@NotNull
	private Date fecha=new Date();
	
	
	private int numero=0;
	
	@Length (max=50) @NotNull
	@NotEmpty (message="No se permite nulo")
	private String banco="";
	
	private Long sucursalId;
	
	private Sucursales sucursal;
	
	@NotNull
	private BigDecimal importe=BigDecimal.ZERO;
	
	@Length (max=150)	
	private String comentario;
	
	private BigDecimal saldo=BigDecimal.ZERO;
	
	private BigDecimal cargosAplicados=BigDecimal.ZERO;
	
	private Set<Pago> pagos=new HashSet<Pago>();
	
	private Set<NotasDeCreditoDet> cargos=new HashSet<NotasDeCreditoDet>();
	
	private boolean juridico=false;
	
	@NotNull
	private Date creado=new Date();
	
	@NotNull
	private int year;
	
	@NotNull
	private int mes;	
	
	private BigDecimal pagosAplicados=BigDecimal.ZERO;
	

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
		Object oldValue=this.cliente;
		this.cliente = cliente;
		getPropertyChangeSupport().firePropertyChange("cliente", oldValue, cliente);
	}

	public PagoM getOrigen() {
		return origen;
	}

	public void setOrigen(PagoM origen) {
		Object oldValue=this.origen;
		this.origen = origen;
		getPropertyChangeSupport().firePropertyChange("origen", oldValue, origen);
	}	

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}	

	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		Object oldValue=this.banco;
		this.banco = banco;
		getPropertyChangeSupport().firePropertyChange("banco", oldValue, banco);
	}
	
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		Object oldValue=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe", oldValue, importe);
	}
	
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		Object oldValue=this.numero;
		this.numero = numero;
		getPropertyChangeSupport().firePropertyChange("numero", oldValue, numero);
	}
	
	public Sucursales getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursales sucursal) {
		Object oldValue=this.sucursal;
		this.sucursal = sucursal;
		getPropertyChangeSupport().firePropertyChange("sucursal", oldValue, sucursal);
		setSucursalId((long)sucursal.getNumero());
	}
	
	public Long getSucursalId() {
		return sucursalId;
	}
	public void setSucursalId(Long sucursalId) {
		this.sucursalId = sucursalId;
	}
	
	public BigDecimal getSaldo(){
		return saldo;
	}
	
	public BigDecimal getCargosAplicados() {
		return cargosAplicados;
	}
	public void setCargosAplicados(BigDecimal cargosAplicados) {
		this.cargosAplicados = cargosAplicados;
	}
	
	public double getPorcentajeAplicado(){
		return getCargosAplicados().doubleValue()/getImporte().doubleValue()*100;
	}

	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public Set<NotasDeCreditoDet> getCargos() {
		return Collections.unmodifiableSet(cargos);
	}
	public Set<Pago> getPagos() {
		return Collections.unmodifiableSet(pagos);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		ChequeDevuelto otro=(ChequeDevuelto) obj;
		return new EqualsBuilder()
		.append(this.getOrigen(), otro.getOrigen())		
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getOrigen())		
		.toHashCode();
	}
	
	public boolean isJuridico() {
		return juridico;
	}
	public void setJuridico(boolean juridico) {
		this.juridico = juridico;
	}	
	
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getCliente().getClave())
		.append(getNumero())
		.append(getBanco())
		.append(getImporte())
		.toString();
	}
	public BigDecimal getPagosAplicados() {
		return pagosAplicados;
	}
	
	

}
