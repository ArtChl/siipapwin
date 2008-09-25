package com.luxsoft.siipap.cxc.domain;



import java.util.Date;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * 
 * @author RUBEN
 *
 */
@SuppressWarnings("serial")
@Entity
public class NotasDeCreditoDet extends PersistentObject{
	
	/**
	 * Clave del cliente de la nota de credito
	 */
	private String clave;
	
	/**
	 * Nota de Credito (Maestro)
	 */
	private NotaDeCredito nota;

	/**
	 * La facura (venta) origen. Es opcional
	 * 
	 */
	private Venta factura;
	
			
	/**
	 * Por mantener algo de compatibilidad con 
	 *  
	 * en SIIPAP CLIPPER
	 */
	private int renglon;
	
	
	/**
	 * Descuento a aplicar en las notas por descuento , normalmente es del del mestro pero por compatibilidad
	 * se deja como una propiedad de esta entidad.
	 * Este campo es estatico pero posteriormente puede ser que se migre a uno dinamico
	 * 
	 */
	private double descuento;
	
	/**
	 * Importe de la partida, normalmente es el porcentaje de la factura (venta) 
	 * Este campo es estatico pero posteriormente puede ser que se migre a uno dinamico
	 */
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
				
	/**
	 * Fecha del documento origen Normalmente una venta
	 * 
	 */
	private Date fechaDocumento;
	
	/**
	 * Sucursal del documento origen
	 * Deberia ser igual al de la venta o bien ser nulo
	 */
	private int sucDocumento;
	
	/**
	 * Numero del documento origen 
	 * Deberia ser igual al de la venta o bien ser nulo
	 */
	private long numDocumento;
	
	/**
	 * Serie del documento origen 
	 * Deberia ser igual al de la venta o bien ser nulo
	 */
	private String serieDocumento;
	
	/**
	 * Tipo del documento origen
	 * Deberia ser igual al de la venta o bien ser nulo
	 */
	private String tipoDocumento;
	
	/**
	 * Por compatibilidad con SIIPAP
	 */
	private int grupo;
	
	/**
	 * Comentario de la partida
	 */
	@Length(min=0,max=70,message="El maximo de longitud para el comentario es de 70 caracteres")
	private String comentario;
	
	private CantidadMonetaria saldoFactura;
	
	private Date fecha=new Date();
	
	
	private int year=Periodo.obtenerYear(new Date());
	
	private int mes=Periodo.obtenerMes(new Date())+1;
	
	private String origen;
	
	private ChequeDevuelto cheque;
	
	private Date creado=new Date();
	
	
	private int version;

	public NotasDeCreditoDet(){
		
	}

	public String getComentario() {
		return comentario;
	}


	public void setComentario(String comentario) {
		Object old=this.comentario;
		this.comentario = comentario;
		getPropertyChangeSupport().firePropertyChange("comentario",old,comentario);
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
		getPropertyChangeSupport().firePropertyChange("descuento",old,descuento);
	}
	
	public void setDescuentoSilently(double descuento){
		this.descuento=descuento;
	}


	public Venta getFactura() {
		return factura;
	}


	public void setFactura(Venta factura) {
		Object old=this.factura;
		this.factura = factura;
		getPropertyChangeSupport().firePropertyChange("factura",old,factura);
	}


	public Date getFechaDocumento() {
		return fechaDocumento;
	}


	public void setFechaDocumento(Date fechaDocumento) {
		this.fechaDocumento = fechaDocumento;
	}


	public int getGrupo() {
		return grupo;
	}


	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}


	public CantidadMonetaria getImporte() {
		return importe;
	}


	public void setImporte(final CantidadMonetaria importe) {
		Object old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
	}


	public NotaDeCredito getNota() {
		return nota;
	}


	public void setNota(NotaDeCredito nota) {
		this.nota = nota;
	}


	public long getNumDocumento() {
		return numDocumento;
	}


	public void setNumDocumento(long numDocumento) {
		this.numDocumento = numDocumento;
	}


	public int getRenglon() {
		return renglon;
	}


	public void setRenglon(int renglon) {
		this.renglon = renglon;
	}


	public String getSerieDocumento() {
		return serieDocumento;
	}


	public void setSerieDocumento(String serieDocumento) {
		this.serieDocumento = serieDocumento;
	}


	public int getSucDocumento() {
		return sucDocumento;
	}


	public void setSucDocumento(int sucDocumento) {
		this.sucDocumento = sucDocumento;
	}


	public String getTipoDocumento() {
		return tipoDocumento;
	}


	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}


	public int getVersion() {
		return version;
	}


	public void setVersion(int version) {
		this.version = version;
	}


	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof NotasDeCreditoDet)) {
			return false;
		}
		NotasDeCreditoDet rhs = (NotasDeCreditoDet) object;
		return new EqualsBuilder().append(
				this.descuento, rhs.descuento)
				.append(this.renglon, rhs.renglon).append(this.numDocumento,
						rhs.numDocumento)
				//.append(this.factura, rhs.factura)
				.append(this.version, rhs.version).append(this.creado,
						rhs.creado).append(this.fechaDocumento,
						rhs.fechaDocumento).append(this.serieDocumento,
						rhs.serieDocumento).append(this.tipoDocumento,
						rhs.tipoDocumento).append(this.nota, rhs.nota).append(
						this.grupo, rhs.grupo).append(this.sucDocumento,
						rhs.sucDocumento).append(this.comentario,
						rhs.comentario).append(this.importe, rhs.importe)
				.isEquals();
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-1129407489, -676101009)
		.append(this.descuento)
		.append(this.renglon)
		.append(this.numDocumento)
		//.append(this.factura)
		.append(this.version)
		.append(this.creado)
		.append(this.fechaDocumento)
		.append(this.serieDocumento)
		.append(this.tipoDocumento)
		.append(this.nota)
		.append(this.grupo)
		.append(this.sucDocumento)
		.append(this.comentario)
		.append(this.importe)
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append(this.getId())
		.append(numero)
		.append(serie)
		.append(tipo)
		.append(clave)
		.append(fecha)
		.append(numDocumento)
		.append(sucDocumento)
		.append(serieDocumento)
		.append(tipoDocumento)
		.append(fechaDocumento)
		.toString();
		
		
	}
	
	/** Campos solo utiles para la migracion ***/
	
	private long numero;
	private String tipo;
	private String serie;


	public long getNumero() {
		return numero;
	}
	public void setNumero(long numero) {
		this.numero = numero;
	}


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


	public Date getFecha() {
		return fecha;
	}


	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public CantidadMonetaria getTotal(){
		return getImporte().multiply(1.15);
	}
	
	/**
	 * Actualiza el importe en funcion del descuento
	 *
	 */
	public void actualizarImporte(){
		//CantidadMonetaria total=getFactura().getTotal();
		CantidadMonetaria total=getFactura().getSaldoEnMoneda();
		double desc=(getDescuento()/100)*-1.0;
		setImporte(total.multiply(desc));
	}


	public CantidadMonetaria getSaldoFactura() {
		return saldoFactura;
	}


	public void setSaldoFactura(CantidadMonetaria saldoFactura) {
		this.saldoFactura = saldoFactura;
	}

	

/*
	@Override
	public boolean equals(Object obj) {
		
		if(obj==null) return false;
		if(obj==this) return true;
		//return false;
		
		NotasDeCreditoDet other=(NotasDeCreditoDet)obj;
		return new EqualsBuilder()
		.append(getNota(),other.getNota())
		.append(getCreado(),other.getCreado())
		.isEquals();
		
		
		
	}
*/
/*
	@Override
	public int hashCode() {
		
		return new HashCodeBuilder(17,35)
		.append(getNota())
		.append(getCreado())
		.toHashCode();
	
	}
*/	
	
	public String getCuenta(){
		String cuenta="";
		if(getNota().getOrigen().equals("CRE")){
			if(getNota().getCliente().getCredito()==null)
				cuenta=getNota().getCliente().getCuentacontable();
			else
			cuenta=getNota()
			.getCliente()
			.getCredito()
			.getCuenta();
		}
		if(StringUtils.isBlank(cuenta))
			throw new RuntimeException("El cliente: "+getClave()+" No tiene asignada una cuenta contable");
		return cuenta; 
	}
	
	public double getImporteSinIva(){
		return getImporte().amount().abs().doubleValue();
	}

	public ChequeDevuelto getCheque() {
		return cheque;
	}

	public void setCheque(ChequeDevuelto cheque) {
		this.cheque = cheque;
	}
	
	
	
}
