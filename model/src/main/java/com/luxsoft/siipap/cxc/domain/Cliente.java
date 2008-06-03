//package com.luxsoft.siipap.ventas.domain;

package com.luxsoft.siipap.cxc.domain;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.utils.domain.PersistentObject;

@Entity
@Table(name="SW_CLIENTES")
@SuppressWarnings("serial")
public class Cliente extends PersistentObject{
	
	
	private ClienteCredito credito;
	
	//Generales
	@NotNull(message="La clave no puede ser nula")
	@Length(min=7,max=7,message="La longitud de la clave debe ser 7")
	private String clave;
	
	private String nombre;
	
	private String rfc;
	
	private Date fecha_alta;
	
	private Date fecha_suspension;

	//Direccion
	private String calle;
	private int numeroExterior;		 
	private int numeroInterior;
	private String cpostal;
	private String colonia;
	private String entidad;
	private String ciudad;
	private String estado;

	private String telefono1;
	private String telefono2;
	private String telefono3;
	private String fax;

	//Contactos
	private String paginaWeb;
	
	@Email
	private String correoelectronico1;
	
	@Email	
	private String correoelectronico2;
	
	@Email
	private String correoelectronico3;
	
	private String gerentegeneral;
	private String gerentedecompras;
	private String cedula;
	private boolean newsLetter;

	//Condiciones de Pago
	private int operador;
	private String cuentacontable;
	private int clasificacion;
	private Date   fecha_clasif;
	private String observaciones;
	private int dia_revision;
	private int dia_pago;
	private int cobrador;
	private int plazo;
	private String tipo_vencimiento="F";
	private String pagare;
	private String status;
	private String forma_pago;
	private int abogado;
	

	//Condiciones de Venta
	private String cotizacion;
	private String pedido;
	private String factura;
	private CantidadMonetaria limite_cred=CantidadMonetaria.pesos(0);
	private String tipo_venta;
	private int vendedor;
	private boolean ordenDeCompra;
	
	private Date fecha_Atencion;
	private String atencion_Cliente;
	private Date fecha_Modif_Mos;
	private String atencion_Mos;
	private String tel_Anterior;
	private String fax_Anterior;

	
	private Date creado;
	
	private int version;
	
	
	
	public Cliente() {
	}

	public Cliente(String clave, String nombre) {		
		this.clave = clave;
		this.nombre = nombre;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getAbogado() {
		return abogado;
	}

	public void setAbogado(int abogado) {
		this.abogado = abogado;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public int getCobrador() {
		return cobrador;
	}

	public void setCobrador(int cobrador) {
		this.cobrador = cobrador;
	}

	public String getColonia() {
		return colonia;
	}

	public void setColonia(String colonia) {
		this.colonia = colonia;
	}

	public String getCotizacion() {
		return cotizacion;
	}

	public void setCotizacion(String cotizacion) {
		this.cotizacion = cotizacion;
	}

	public String getCpostal() {
		return cpostal;
	}

	public void setCpostal(String cpostal) {
		this.cpostal = cpostal;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public String getCuentacontable() {
		return cuentacontable;
	}

	public void setCuentacontable(String cuentacontable) {
		this.cuentacontable = cuentacontable;
	}

	

	public int getDia_pago() {
		return dia_pago;
	}

	public void setDia_pago(int dia_pago) {
		this.dia_pago = dia_pago;
	}

	public int getDia_revision() {
		return dia_revision;
	}

	public void setDia_revision(int dia_revision) {
		this.dia_revision = dia_revision;
	}

	

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Date getFecha_alta() {
		return fecha_alta;
	}

	public void setFecha_alta(Date fecha_alta) {
		this.fecha_alta = fecha_alta;
	}

	public Date getFecha_clasif() {
		return fecha_clasif;
	}

	public void setFecha_clasif(Date fecha_clasif) {
		this.fecha_clasif = fecha_clasif;
	}

	public Date getFecha_suspension() {
		return fecha_suspension;
	}

	public void setFecha_suspension(Date fecha_suspension) {
		this.fecha_suspension = fecha_suspension;
	}

	public String getForma_pago() {
		return forma_pago;
	}

	public void setForma_pago(String forma_pago) {
		this.forma_pago = forma_pago;
	}

	public String getGerentedecompras() {
		return gerentedecompras;
	}

	public void setGerentedecompras(String gerentedecompras) {
		this.gerentedecompras = gerentedecompras;
	}

	public String getGerentegeneral() {
		return gerentegeneral;
	}

	public void setGerentegeneral(String gerentegeneral) {
		this.gerentegeneral = gerentegeneral;
	}

	
	public CantidadMonetaria getLimite_cred() {
		return limite_cred;
	}

	public void setLimite_cred(CantidadMonetaria limite_cred) {
		this.limite_cred = limite_cred;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public int getOperador() {
		return operador;
	}

	public void setOperador(int operador) {
		this.operador = operador;
	}

	public String getPagare() {
		return pagare;
	}

	public void setPagare(String pagare) {
		this.pagare = pagare;
	}

	public String getPedido() {
		return pedido;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

	public int getPlazo() {
		return plazo;
	}

	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	

	public String getTipo_vencimiento() {
		return tipo_vencimiento;
	}

	public void setTipo_vencimiento(String tipo_vencimiento) {
		this.tipo_vencimiento = tipo_vencimiento;
	}

	public String getTipo_venta() {
		return tipo_venta;
	}

	public void setTipo_venta(String tipo_venta) {
		this.tipo_venta = tipo_venta;
	}

	public int getVendedor() {
		return vendedor;
	}

	public void setVendedor(int vendedor) {
		this.vendedor = vendedor;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public int getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(int clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getCorreoelectronico1() {
		return correoelectronico1;
	}

	public void setCorreoelectronico1(String correoelectronico1) {
		Object oldValue=this.correoelectronico1;
		this.correoelectronico1 = correoelectronico1;
		getPropertyChangeSupport().firePropertyChange("correoelectronico1", oldValue, correoelectronico1);
	}

	public String getCorreoelectronico2() {
		return correoelectronico2;
	}

	public void setCorreoelectronico2(String correoelectronico2) {
		this.correoelectronico2 = correoelectronico2;
	}

	public String getCorreoelectronico3() {
		return correoelectronico3;
	}

	public void setCorreoelectronico3(String correoelectronico3) {
		this.correoelectronico3 = correoelectronico3;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public boolean isNewsLetter() {
		return newsLetter;
	}

	public void setNewsLetter(boolean newsLetter) {
		this.newsLetter = newsLetter;
	}

	public int getNumeroExterior() {
		return numeroExterior;
	}

	public void setNumeroExterior(int numeroExterior) {
		this.numeroExterior = numeroExterior;
	}

	public int getNumeroInterior() {
		return numeroInterior;
	}

	public void setNumeroInterior(int numeroInterior) {
		this.numeroInterior = numeroInterior;
	}

	public boolean isOrdenDeCompra() {
		return ordenDeCompra;
	}

	public void setOrdenDeCompra(boolean ordenDeCompra) {
		this.ordenDeCompra = ordenDeCompra;
	}

	public String getPaginaWeb() {
		return paginaWeb;
	}

	public void setPaginaWeb(String paginaWeb) {
		this.paginaWeb = paginaWeb;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}

	public String getTelefono2() {
		return telefono2;
	}

	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}

	public String getTelefono3() {
		return telefono3;
	}

	public void setTelefono3(String telefono3) {
		this.telefono3 = telefono3;
	}

	

	public String getAtencion_Cliente() {
		return atencion_Cliente;
	}

	public void setAtencion_Cliente(String atencion_Cliente) {
		this.atencion_Cliente = atencion_Cliente;
	}

	public String getAtencion_Mos() {
		return atencion_Mos;
	}

	public void setAtencion_Mos(String atencion_Mos) {
		this.atencion_Mos = atencion_Mos;
	}

	public String getFax_Anterior() {
		return fax_Anterior;
	}

	public void setFax_Anterior(String fax_Anterior) {
		this.fax_Anterior = fax_Anterior;
	}

	public Date getFecha_Atencion() {
		return fecha_Atencion;
	}

	public void setFecha_Atencion(Date fecha_Atencion) {
		this.fecha_Atencion = fecha_Atencion;
	}

	public Date getFecha_Modif_Mos() {
		return fecha_Modif_Mos;
	}

	public void setFecha_Modif_Mos(Date fecha_Modif_Mos) {
		this.fecha_Modif_Mos = fecha_Modif_Mos;
	}

	public String getTel_Anterior() {
		return tel_Anterior;
	}

	public void setTel_Anterior(String tel_Anterior) {
		this.tel_Anterior = tel_Anterior;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append(getClave())
		.append(getNombre())
		.append(getRfc())
		.toString();
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((clave == null) ? 0 : clave.hashCode());
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
		final Cliente other = (Cliente) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}

	public ClienteCredito getCredito() {
		return credito;
	}

	public void setCredito(ClienteCredito credito) {
		this.credito = credito;
	}
	
	public ClienteCredito generarCredito(){
		ClienteCredito c=new ClienteCredito(this);
		c.copyFromCliente();
		return c;
	}

	
	
	
}


