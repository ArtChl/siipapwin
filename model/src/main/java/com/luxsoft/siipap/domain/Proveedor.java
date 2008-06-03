/*
 * Created on 9/09/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.domain;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.luxsoft.utils.domain.PersistentObject;

/**
 * @author Ruben Cancino
 * 
 */

public class Proveedor extends PersistentObject implements Serializable,Comparable{

    public static final int FECHA_DE_FACTURA=1;
    public static final int FECHA_DE_REVISION=2;
    
	private Long id;
	private String clave;	
	private String nombre;
	private Direccion direccion;
	private Telefono telefono1;
	private Telefono telefono2; 
	private Telefono fax;
	private String rfc="";
	private int diasDeCredito; //Dias de plazo para pago,Dias de credito que ofrece el proveedor
	private int vencimientoEstipulado; // A partid de la fecha de revision(1) o a partir de la fecha de factura (2)
	private String observaciones;
	private String representante;
	private String cuentaContable;
	private boolean activo=true;
	private String comentarioDeSuspencion;
	private Date fechaDeSuspencion;
	private Date fechaDeListaDePreciosVigente;
	private boolean recibeDocumentos=true; //??No se para que sirve
	private Currency monedaDefault=CantidadMonetaria.PESOS;
	private Date creado=currentTime();
	private Date modificado;
	private String comentarioParaPedido;
	private CantidadMonetaria saldo=CantidadMonetaria.pesos(0);
	
	private Set<ArticuloPorProveedor> articulosAsignados;
	
	private Date prosusent;
	private Date prosusrev;
	private Predicate articuloPredicate;
	
	private Map<String,ArticuloPorProveedor> articulosPorClave;
	
	private BigDecimal descuentoFinanciero=BigDecimal.ZERO;
	private int diasDF=0;
	
	//PROSUSREV
	/* Propiedades pendientes de refactorizar
	PROCOSTO
	PRDIAPAG
	PRRANPAG
	PROFECBAJ
	*/
	
	
	public Proveedor(){
		
		
	}
	
	/**
	 * 
	 * @return
	 * 
	 */
	
	public Long getId() {
		return id;
	}
	@SuppressWarnings("unused")
	public void setId(Long id) {
        final String name="id";
        Object old=this.id;
        this.id = id;
        getPropertyChangeSupport().firePropertyChange(name,old,this.id);
	}
		
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }
    public String getComentarioDeSuspencion() {
        return comentarioDeSuspencion;
    }
    public void setComentarioDeSuspencion(String comentarioDeSuspencion) {
        this.comentarioDeSuspencion = comentarioDeSuspencion;
    }
    public Date getCreado() {
        return creado;
    }
    public void setCreado(Date creado) {
        this.creado = creado;
    }
    public String getCuentaContable() {
        return cuentaContable;
    }
    public void setCuentaContable(String cuentaContable) {
        this.cuentaContable = cuentaContable;
    }
    public int getDiasDeCredito() {
        return diasDeCredito;
    }
    public void setDiasDeCredito(int diasDeCredito) {
        this.diasDeCredito = diasDeCredito;
    }
    public Direccion getDireccion() {
        return direccion;
    }
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
    public Telefono getFax() {
        return fax;
    }
    public void setFax(Telefono fax) {
        this.fax = fax;
    }
    public Date getFechaDeListaDePreciosVigente() {
        return fechaDeListaDePreciosVigente;
    }
    public void setFechaDeListaDePreciosVigente(
            Date fechaDeListaDePreciosVigente) {
        this.fechaDeListaDePreciosVigente = fechaDeListaDePreciosVigente;
    }
    public Date getFechaDeSuspencion() {
        return fechaDeSuspencion;
    }
    public void setFechaDeSuspencion(Date fechaDeSuspencion) {
        this.fechaDeSuspencion = fechaDeSuspencion;
    }
    public Currency getMonedaDefault() {
        return monedaDefault;
    }
    public void setMonedaDefault(Currency monedaDefault) {
        this.monedaDefault = monedaDefault;
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
    public boolean isRecibeDocumentos() {
        return recibeDocumentos;
    }
    public void setRecibeDocumentos(boolean recibeDocumentos) {
        this.recibeDocumentos = recibeDocumentos;
    }
    public String getRepresentante() {
        return representante;
    }
    public void setRepresentante(String representante) {
        this.representante = representante;
    }
    public String getRfc() {
        return rfc;
    }
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }
    public Telefono getTelefono1() {
        return telefono1;
    }
    public void setTelefono1(Telefono telefono1) {
        this.telefono1 = telefono1;
    }
    public Telefono getTelefono2() {
        return telefono2;
    }
    public void setTelefono2(Telefono telefono2) {
        this.telefono2 = telefono2;
    }
    public int getVencimientoEstipulado() {
        return vencimientoEstipulado;
    }
    public void setVencimientoEstipulado(int vencimientoEstipulado) {
        this.vencimientoEstipulado = vencimientoEstipulado;
    }
    
    
    
    public Set<ArticuloPorProveedor> getArticulosAsignados() {
    	if(articulosAsignados==null)articulosAsignados=new HashSet<ArticuloPorProveedor>();
        return articulosAsignados;
    }
    
    public void setArticulosAsignados(Set<ArticuloPorProveedor> articulosAsignados) {
        this.articulosAsignados = articulosAsignados;
    }
    
    public void agregarArticuloPorProducto(final ArticuloPorProveedor ap){
        Validate.notNull(ap,"No se puede agregar un ArticuloPorProveedor nulo");
        ap.setProveedor(this);
        getArticulosAsignados().add(ap);
    }
    
    public void removeArticuloPorProveedor(final ArticuloPorProveedor ap){
        if(getArticulosAsignados().contains(ap))
            getArticulosAsignados().remove(ap);        
    }
    
    public ArticuloPorProveedor agregarArticulo(Articulo a){
        ArticuloPorProveedor ap=new ArticuloPorProveedor();
        ap.setArticulo(a);
        ap.setProveedor(this);
        ap.setCodigoDelProveedor(a.getClave());
        ap.setDescripcionDelProveedor(a.getDescripcion1());
        ap.setCreado(new java.util.Date());
        agregarArticuloPorProducto(ap);
        return ap;
    }
    
    public void agregarArticulos(List arts){
        Iterator iter=arts.iterator();
        while(iter.hasNext()){
            agregarArticulo((Articulo)iter.next());
        }
    }
    
    public Map getArticulosPorClave(){
    	if(articulosPorClave==null){
    		articulosPorClave=new HashMap<String,ArticuloPorProveedor>();
    		for(ArticuloPorProveedor ap:getArticulosAsignados()){
    			articulosPorClave.put(ap.getArticulo().getClave().trim(),ap);
    		}
    	}
    	return this.articulosPorClave;
    }
    
    public ArticuloPorProveedor getArticuloAsignado(final String clave){
    	//if(articuloPredicate==null){
    		articuloPredicate=new Predicate(){
    			public boolean evaluate(Object object) {
    				ArticuloPorProveedor ap=(ArticuloPorProveedor)object;
    				//System.out.println("Clave: "+ap.getArticulo().getClave());
    				/*
    				boolean is= ap.getArticulo().getClave()
						.trim().equals(clave.trim());
    				if(is){
    					System.out.println("Encontro:" +clave);
    					return is;
    				}
    				return false;
    				*/
    				return ap.getArticulo().getClave().equals(clave);
    			}
        		
        	};
    	//}
    	Object o=CollectionUtils.find(getArticulosAsignados(),articuloPredicate);
    	ArticuloPorProveedor aap=null;
    	if(o!=null)
    		aap=(ArticuloPorProveedor)o;
    	return aap;
    }
	
	public String getComentarioParaPedido() {
		return comentarioParaPedido;
	}
	public void setComentarioParaPedido(String comentarioParaPedido) {
		this.comentarioParaPedido = comentarioParaPedido;
	}
	
	
    public boolean equals(Object obj) {
        boolean equals=false;
        if(obj!=null && Proveedor.class.isAssignableFrom(obj.getClass())){
            Proveedor cve=(Proveedor)obj;
            equals=new EqualsBuilder()
                     .append(this.clave,cve.getClave())
                     .isEquals();
        }
        return equals;
    }

    public int hashCode() {        
        return new HashCodeBuilder(1,7)
        	.append(clave)
        	.toHashCode();
    }
    
    public String toString(){
        //return "("+clave+") "+nombre;
    	return clave+"  "+nombre;
    }
    
    
    
    
    

	public Date getModificado() {
		return modificado;
	}

	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
//	 Propiedades raras

	public Date getProsusent() {
		return prosusent;
	}
	public void setProsusent(Date prosusent) {
		this.prosusent = prosusent;
	}
	
	
	public Date getProsusrev() {
		return prosusrev;
	}
	public void setProsusrev(Date prosusrev) {
		this.prosusrev = prosusrev;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		Proveedor p=(Proveedor)arg0;
		return getClave().compareTo(p.getClave());
	}

	public BigDecimal getDescuentoFinanciero() {
		return descuentoFinanciero;
	}

	public void setDescuentoFinanciero(BigDecimal descuentoFinanciero) {
		this.descuentoFinanciero = descuentoFinanciero;
	}

	public int getDiasDF() {
		return diasDF;
	}

	public void setDiasDF(int diasDF) {
		this.diasDF = diasDF;
	}

	public CantidadMonetaria getSaldo() {
		return saldo;
	}

	public void setSaldo(CantidadMonetaria saldo) {
		this.saldo = saldo;
	}
	
	
	
	
}