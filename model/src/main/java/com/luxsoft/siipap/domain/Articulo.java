/*
 * Created on 9/09/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.hibernate.validator.Length;
import org.hibernate.validator.Max;

import com.luxsoft.utils.domain.MutableObject;






/**
 * @author Ruben
 * @hibernate.class table="ARTICULO"
 */

public class Articulo extends MutableObject implements Serializable,Comparable{

	private Long id;
	
	/** Datos Generales del Producto **/
	@Length(max=10,message="La clave solo puede tener 10 caracteres")	
    private String clave="";
    
	private String descripcion1="";
	
    private String descripcion2="";    
    
	private BigDecimal kilos=BigDecimal.valueOf(0);
	private int gramos;
	private String observacion;
	private Unidad unidad;
	private Unidad unidadDeVenta;
	private Familia familia;
	
	private Estado estado=Estado.getEstado("A");
	private String comentarioDeSuspencion;
	private String codigoOrigen;
	private Date fechaDeCodigoOrigen;
	
	/** Permiso de uso en otras operaciones **/
	private boolean suspendidoEnVentas=false;
	private String comentarioDeSuspencionEnVenta;
	private boolean suspendidoEnInventarios=false;
	private String comentarioDeSuspencionEnInventario;
	private boolean suspendidoEnCompras=false;	
	private String comentarioDeSuspencionEnCompra;
	private boolean visibleEnComentariosDeAyuda=true;
	private boolean afectaInventario=true; //TODO Pregruntar a Andres
	//private CantidadMonetaria percioContado;
	//private CantidadMonetaria percioCredito;
	//private CantidadMonetaria costoPromedio;
	private boolean modoDeVentaBruto=true; //TODO Pregruntar a Andres
	private String descripcionListaDePrecios;
	private int paginaListaDePrecios;
	private int subPagina;	
	
	/* Precios y Costos */
	private CantidadMonetaria precioContado;
	private CantidadMonetaria precioCredito;
	private CantidadMonetaria costo;
	//TODO Propiedades no muy claras
	private CantidadMonetaria artprenu;  //ARTPRENU   FLOAT(126)
	private CantidadMonetaria artprenu2; //ARTPRENU2
	
	private BigDecimal costoP=BigDecimal.ZERO;
	
	// TODO Implementar en otra tabla
	private CantidadMonetaria precioDeListaContadoB;
	private CantidadMonetaria precioDeListaCreditoB;
	private CantidadMonetaria precioDeListaContadoC;
	private CantidadMonetaria precioDeListaCreditoC;
	private CantidadMonetaria precioDeListaContadoD;
	private CantidadMonetaria precioDeListaCreditoD;
	private CantidadMonetaria actualizaPrecioContado;
	private CantidadMonetaria actualizaPrecioCredito;
	
	private Date creado;
	private Date modificado;
	private Date fechaDeSuspencion;
	private Date artfecal;  //TODO:  Investigar su uso
	private Date artfebaj; // TODO:  Investigar su uso ARTFEBAJ
	private boolean visible=true;
	
	
	/** Asociaciones one-to-many (Colecciontes) **/
	//private UnidadesxArticulo unidad;
	private Set proveedores=new HashSet();
	private Set unidades;
	
	/** Propiedades Nuevas **/
	
	private BigDecimal largo=BigDecimal.valueOf(0);
	private BigDecimal ancho=BigDecimal.valueOf(0);
	private int calibre;
	
	private double area;
	
	private boolean precioNeto=false;
	private String clasificacion="L";
	private int caras=1;
	private String acabado="";
	private String color;
	private boolean nacional=true;
	
	/** Nuevos catalogos **/
	private Linea linea;
	private String lineaClave="SIN ASIGNAR";
	private Marca marca;
	private String marcaClave="SIN ASIGNAR";
	private Clase clase;
	private String claseClave="SIN ASIGNAR";
	private String presentacion;
	
	/** Propiedades no persisteibles solo utiles para la migracion **/
	private String familiaSiipap;
	private String unidadSiipap="MIL";
	
	public Articulo() {
		
	    addPropertyChangeListener(this.businessRules);
	}
	
	/**
	 * 
	 * @return
	 * @hibernate.id
	 * column="ARTICULO_ID"
	 * generator-class="native"
	 */
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
        final String name="id";
        Object old=this.id;
        this.id = id;
        getPropertyChangeSupport().firePropertyChange(name,old,this.id);
	}
	
    public Estado getEstado() {
        return estado;
    }
    public void setActivo(final Estado estado) {
        this.estado = estado;
    }
    public boolean isAfectaInventario() {
        return afectaInventario;
    }
    public void setAfectaInventario(boolean afectaInventario) {
        this.afectaInventario = afectaInventario;
    }
    
    public String getClave() {
        return clave;
    }
    /**
     * Asignar clave del producto
     * @param clave void
     */
    public void setClave(String clave) {
        Object old=this.clave;
        this.clave = clave;        
        getPropertyChangeSupport()
        .firePropertyChange("clave",old,clave);
    }
    
    public String getCodigoOrigen() {
        return codigoOrigen;
    }
    public void setCodigoOrigen(String codigoOrigen) {
        this.codigoOrigen = codigoOrigen;
    }
    
    
    
	public boolean isVisibleEnComentariosDeAyuda() {
		return visibleEnComentariosDeAyuda;
	}
	public void setVisibleEnComentariosDeAyuda(
			boolean visibleEnComentariosDeAyuda) {
		this.visibleEnComentariosDeAyuda = visibleEnComentariosDeAyuda;
	}
    public boolean isSuspendidoEnCompras() {
        return suspendidoEnCompras;
    }
    public void setSuspendidoEnCompras(boolean suspendidoEnCompras) {
        this.suspendidoEnCompras = suspendidoEnCompras;
    }
    public boolean isSuspendidoEnInventarios() {
        return suspendidoEnInventarios;
    }
    public void setSuspendidoEnInventarios(boolean suspendidoEnInventarios) {
        this.suspendidoEnInventarios = suspendidoEnInventarios;
    }
    public boolean isSuspendidoEnVentas() {
        return suspendidoEnVentas;
    }
    public void setSuspendidoEnVentas(boolean suspendidoEnVentas) {
        this.suspendidoEnVentas = suspendidoEnVentas;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    public String getComentarioDeSuspencion() {
        return comentarioDeSuspencion;
    }
    public void setComentarioDeSuspencion(String comentarioDeSuspencion) {
        this.comentarioDeSuspencion = comentarioDeSuspencion;
    }
    public String getComentarioDeSuspencionEnCompra() {
        return comentarioDeSuspencionEnCompra;
    }
    public void setComentarioDeSuspencionEnCompra(
            String comentarioDeSuspencionEnCompra) {
        this.comentarioDeSuspencionEnCompra = comentarioDeSuspencionEnCompra;
    }
    public String getComentarioDeSuspencionEnInventario() {
        return comentarioDeSuspencionEnInventario;
    }
    public void setComentarioDeSuspencionEnInventario(
            String comentarioDeSuspencionEnInventario) {
        this.comentarioDeSuspencionEnInventario = comentarioDeSuspencionEnInventario;
    }
    public String getComentarioDeSuspencionEnVenta() {
        return comentarioDeSuspencionEnVenta;
    }
    public void setComentarioDeSuspencionEnVenta(
            String comentarioDeSuspencionEnVenta) {
        this.comentarioDeSuspencionEnVenta = comentarioDeSuspencionEnVenta;
    }
    
    public Date getCreado() {
        return creado;
    }
    public void setCreado(Date creado) {
        this.creado = creado;
    }
    public String getDescripcion1() {
        return descripcion1;
    }
    public void setDescripcion1(String descripcion1) {
        this.descripcion1 = descripcion1;
    }
    public String getDescripcion2() {
        return descripcion2;
    }
    public void setDescripcion2(String descripcion2) {
        this.descripcion2 = descripcion2;
    }
	
	
    public String getDescripcionListaDePrecios() {
        return descripcionListaDePrecios;
    }
    public void setDescripcionListaDePrecios(String descripcionListaDePrecios) {
        this.descripcionListaDePrecios = descripcionListaDePrecios;
    }
    public Date getFechaDeSuspencion() {
        return fechaDeSuspencion;
    }
    public void setFechaDeSuspencion(Date fechaDeSuspencion) {
        this.fechaDeSuspencion = fechaDeSuspencion;
    }
    
	public Date getArtfebaj() {
		return artfebaj;
	}
	public void setArtfebaj(Date artfebaj) {
		this.artfebaj = artfebaj;
	}
	public Date getArtfecal() {
		return artfecal;
	}
	public void setArtfecal(Date artfecal) {
		this.artfecal = artfecal;
	}
	
    public int getGramos() {
        return gramos;
    }
    public void setGramos(int gramos) {
    	int oldValue=this.gramos;
        this.gramos = gramos;
        getPropertyChangeSupport().firePropertyChange("gramos", oldValue, gramos);
    }
   
    public BigDecimal getKilos() {
        return kilos;
    }
    public void setKilos(BigDecimal kilos) {
    	Object old=this.kilos;    	
        this.kilos = kilos;
        getPropertyChangeSupport().firePropertyChange("kilos",old,kilos);
    }
    
    public boolean isModoDeVentaBruto() {
        return modoDeVentaBruto;
    }
    public void setModoDeVentaBruto(boolean modoDeVentaNeto) {
        this.modoDeVentaBruto = modoDeVentaNeto;
    }
    public String getObservacion() {
        return observacion;
    }
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    public int getPaginaListaDePrecios() {
        return paginaListaDePrecios;
    }
    public void setPaginaListaDePrecios(int paginaListaDePrecios) {
        this.paginaListaDePrecios = paginaListaDePrecios;
    }
    public int getSubPagina() {
        return subPagina;
    }
    public void setSubPagina(int subPagina) {
        this.subPagina = subPagina;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
	
	
	public Familia getFamilia() {
		return familia;
	}
	public void setFamilia(Familia familia) {
		this.familia = familia;
	}
	public Unidad getUnidad() {
		return unidad;
	}
	public void setUnidad(Unidad unidad) {
		this.unidad = unidad;
	}
	public Unidad getUnidadDeVenta() {
		return unidadDeVenta;
	}
	public void setUnidadDeVenta(Unidad unidadDeVenta) {
		this.unidadDeVenta = unidadDeVenta;
	}
    
    
    
    
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
    
    
    /** Asosiaciones **/
    
    public Set getProveedores() {
        return proveedores;
    }
    public void setProveedores(Set proveedores) {
        this.proveedores = proveedores;
    }
    
    @SuppressWarnings("unchecked")
	public ArticuloPorProveedor addProveedor(final ArticuloPorProveedor ap){
        Validate.notNull(ap,"No se permite agregar un ArticuloPorProveedor nulo");
        ap.setArticulo(this);
        getProveedores().add(ap);
        return ap;
    }
	
	public int getCalibre() {
		return calibre;
	}
	public void setCalibre(int calibre) {
		int oldValue=this.calibre;
		this.calibre = calibre;
		getPropertyChangeSupport().firePropertyChange("calibre", oldValue, calibre);
	}
	
	public BigDecimal getLargo() {
		return largo;
	}
	public void setLargo(BigDecimal largo) {
		Object oldValue=this.largo;
		this.largo = largo;
		getPropertyChangeSupport().firePropertyChange("largo", oldValue, largo);
	}
	
	public BigDecimal getAncho() {
		return ancho;
	}
	public void setAncho(BigDecimal ancho) {
		Object oldValue=this.ancho;
		this.ancho = ancho;
		getPropertyChangeSupport().firePropertyChange("ancho", oldValue, ancho);
	}
	
	public Set getUnidades() {
		if(unidades==null)unidades=new HashSet();
		return unidades;
	}
	
	@SuppressWarnings("unused")
	public void setUnidades(Set unidades) {
		this.unidades = unidades;
	}
	
	public CantidadMonetaria getCosto() {
		return costo;
	}
	public void setCosto(CantidadMonetaria costo) {
		this.costo = costo;
	}
	public CantidadMonetaria getPrecioContado() {
		return precioContado;
	}
	public void setPrecioContado(CantidadMonetaria precioContado) {
		this.precioContado = precioContado;
	}
	public CantidadMonetaria getPrecioCredito() {
		return precioCredito;
	}
	public void setPrecioCredito(CantidadMonetaria precioCredito) {
		this.precioCredito = precioCredito;
	}
	
	public CantidadMonetaria getArtprenu() {
		return artprenu;
	}
	public void setArtprenu(CantidadMonetaria artprenu) {
		this.artprenu = artprenu;
	}
	public CantidadMonetaria getArtprenu2() {
		return artprenu2;
	}
	public void setArtprenu2(CantidadMonetaria artprenu2) {
		this.artprenu2 = artprenu2;
	}
	
	public UnidadesPorArticulo getUnidadDisponible(final String s){
		Predicate select=new Predicate(){

			public boolean evaluate(Object object) {
				UnidadesPorArticulo ua=(UnidadesPorArticulo)object;
				if(ua.getUnidad().getClave().equals(s))
					return true;
				return false;
			}
			
		};
		Object o=CollectionUtils.find(getUnidades(),select);
		if(o==null)
			throw new RuntimeException("Unidad no definida: "+this.clave);
		return (UnidadesPorArticulo)o;
	}
	
	@SuppressWarnings("unchecked")
	public List unidades(){
		List unidades=new ArrayList();
		Iterator iter=getUnidades().iterator();
		while(iter.hasNext()){
			Unidad u=((UnidadesPorArticulo)iter.next()).getUnidad();
			unidades.add(u);
		}
		return unidades;
	}
	
	@SuppressWarnings("unchecked")
	public UnidadesPorArticulo addUnidad(Unidad u){
		UnidadesPorArticulo ua=
			new UnidadesPorArticulo(this,u);
		getUnidades().add(ua);
		return ua;
	}
	
	
	public Date getFechaDeCodigoOrigen() {
		return fechaDeCodigoOrigen;
	}
	public void setFechaDeCodigoOrigen(Date fechaDeCodigoOrigen) {
		this.fechaDeCodigoOrigen = fechaDeCodigoOrigen;
	}
	
	
	
	
		public CantidadMonetaria getActualizaPrecioContado() {
		return actualizaPrecioContado;
	}

	public void setActualizaPrecioContado(CantidadMonetaria actualizaPrecioContado) {
		this.actualizaPrecioContado = actualizaPrecioContado;
	}

	public CantidadMonetaria getActualizaPrecioCredito() {
		return actualizaPrecioCredito;
	}

	public void setActualizaPrecioCredito(CantidadMonetaria actualizaPrecioCredito) {
		this.actualizaPrecioCredito = actualizaPrecioCredito;
	}

	public CantidadMonetaria getPrecioDeListaContadoB() {
		return precioDeListaContadoB;
	}

	public void setPrecioDeListaContadoB(CantidadMonetaria precioDeListaContadoB) {
		this.precioDeListaContadoB = precioDeListaContadoB;
	}

	public CantidadMonetaria getPrecioDeListaContadoC() {
		return precioDeListaContadoC;
	}

	public void setPrecioDeListaContadoC(CantidadMonetaria precioDeListaContadoC) {
		this.precioDeListaContadoC = precioDeListaContadoC;
	}

	public CantidadMonetaria getPrecioDeListaContadoD() {
		return precioDeListaContadoD;
	}

	public void setPrecioDeListaContadoD(CantidadMonetaria precioDeListaContadoD) {
		this.precioDeListaContadoD = precioDeListaContadoD;
	}

	public CantidadMonetaria getPrecioDeListaCreditoB() {
		return precioDeListaCreditoB;
	}

	public void setPrecioDeListaCreditoB(CantidadMonetaria precioDeListaCreditoB) {
		this.precioDeListaCreditoB = precioDeListaCreditoB;
	}

	public CantidadMonetaria getPrecioDeListaCreditoC() {
		return precioDeListaCreditoC;
	}

	public void setPrecioDeListaCreditoC(CantidadMonetaria precioDeListaCreditoC) {
		this.precioDeListaCreditoC = precioDeListaCreditoC;
	}

	public CantidadMonetaria getPrecioDeListaCreditoD() {
		return precioDeListaCreditoD;
	}

	public void setPrecioDeListaCreditoD(CantidadMonetaria precioDeListaCreditoD) {
		this.precioDeListaCreditoD = precioDeListaCreditoD;
	}

	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		Object oldValue=this.clasificacion;
		this.clasificacion = clasificacion;
		getPropertyChangeSupport().firePropertyChange("clasificacion", oldValue, clasificacion);
	}

	public boolean isPrecioNeto() {
		return precioNeto;
	}
	public void setPrecioNeto(boolean precioNeto) {
		boolean oldValue=this.precioNeto;
		this.precioNeto = precioNeto;
		getPropertyChangeSupport().firePropertyChange("precioNeto", oldValue, precioNeto);
	
	}
	

	

	public String getAcabado() {
		return acabado;
	}

	public void setAcabado(String acabado) {
		this.acabado = acabado;
	}

	public int getCaras() {
		return caras;
	}
	public void setCaras(int caras) {
		int oldValue=this.caras;		
		this.caras = caras;
		getPropertyChangeSupport().firePropertyChange("caras", oldValue, caras);
	}

	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		Object oldValue=this.color;
		this.color = color;
		getPropertyChangeSupport().firePropertyChange("color", oldValue, color);
	}

	public boolean isNacional() {
		return nacional;
	}

	public void setNacional(boolean nacional) {
		this.nacional = nacional;
	}

		/***************** MUTABLE OBJECT **********************/
	
    public String toString(){
        return clave;
    }
    
    
    public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public boolean equals(Object obj) {
    	if(obj==this) return true;
    	if(obj==null) return false;
    	if(!(obj instanceof Articulo)) return false;
    	Articulo name2=(Articulo)obj;
        return new EqualsBuilder()
                     .append(clave,name2.getClave())
                     .isEquals();
    }

    public int hashCode() {        
        return new HashCodeBuilder(17,37)
        	.append(clave)
        	.toHashCode();
    }
    
    
	
	public int compareTo(Object o) {
		if(o==null)return -1;
		Articulo a2=(Articulo)o;		
		return getClave().compareTo(a2.getClave());
	}
	
    /** Reglas de negocio **/
    PropertyChangeListener businessRules=new PropertyChangeListener(){

        public void propertyChange(PropertyChangeEvent evt) {
            /**
             * Cuando se da de alta un articulo la clave tambien se asigna
             * al codigo origen
             */
            if("clave".equals(evt.getPropertyName())){
                if(getCodigoOrigen()==null)
                    setCodigoOrigen((String)evt.getNewValue());
            }
        }
        
    };
    
    
    @SuppressWarnings("unchecked")
    public static class Estado{
        
        public static final Map ESTADOS;
        
		static{
            ESTADOS=new HashMap();
            Estado a=new Estado("A","ACTIVO");
            ESTADOS.put("A",a);
            Estado s=new Estado("S","SUSPENDIDO");
            ESTADOS.put("S",s);
            Estado r=new Estado("R","RECLASIFICADO");
            ESTADOS.put("R",r);
            Estado d=new Estado("D","DESCONTINUADO");
            ESTADOS.put("D",d);
        }
        
        private String estado;
        private String descripcion;
        
        private Estado(String estado,String descripcion){
            this.estado=estado;
            this.descripcion=descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        @SuppressWarnings("unused")
		private void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getEstado() {
            return estado;
        }
        @SuppressWarnings("unused")
		private void setEstado(String estado) {
            this.estado = estado;
        }
        
        
        
        
        public boolean equals(final Object obj){
            boolean equal=false;
            if(obj!=null && Articulo.Estado.class.isAssignableFrom(obj.getClass())){
                Articulo.Estado e=(Articulo.Estado)obj;
                equal=new EqualsBuilder()
                	.append(getEstado(),e.getEstado())
                	.isEquals();
            }
            return equal; 
        }
        
        public int hashCode(){
            return new HashCodeBuilder(3,7)
            .append(getEstado())
            .toHashCode();
        }
        
        public String toString(){
            return getDescripcion();
        }
        
        public static Estado getEstado(final String estado){
        	
            if(ESTADOS.containsKey(estado)){
                return (Estado)ESTADOS.get(estado);
            }
			throw new NullPointerException("No esta definido el estado: "+estado);
        }
    }


	public String getFamiliaSiipap() {
		return familiaSiipap;
	}

	public void setFamiliaSiipap(String familiaSiipap) {
		this.familiaSiipap = familiaSiipap;
	}

	public String getUnidadSiipap() {
		return unidadSiipap;
	}

	public void setUnidadSiipap(String unidadSiipap) {
		this.unidadSiipap = unidadSiipap;
	}

	public Clase getClase() {
		return clase;
	}
	public void setClase(Clase clase) {
		Object oldValue=this.clase;
		this.clase = clase;
		getPropertyChangeSupport().firePropertyChange("clase", oldValue, clase);
		if(clase!=null)
			setClaseClave(clase.getNombre());
	}

	public Linea getLinea() {
		return linea;
	}
	public void setLinea(Linea linea) {
		Object old=this.linea;
		this.linea = linea;
		getPropertyChangeSupport().firePropertyChange("linea", old, linea);
		if(linea!=null)
			setLineaClave(linea.getNombre());
	}

	public Marca getMarca() {
		return marca;
	}
	public void setMarca(Marca marca) {
		Object oldValue=this.marca;
		this.marca = marca;
		getPropertyChangeSupport().firePropertyChange("marca", oldValue, marca);
		if(marca!=null)
			setMarcaClave(marca.getNombre());
	}

	public String getClaseClave() {
		return claseClave;
	}

	public void setClaseClave(String claseClave) {		
		this.claseClave = claseClave;
	}

	public String getLineaClave() {
		return lineaClave;
	}

	public void setLineaClave(String lineaClave) {
		this.lineaClave = lineaClave;
	}

	public String getMarcaClave() {
		return marcaClave;
	}

	public void setMarcaClave(String marcaClave) {
		this.marcaClave = marcaClave;
	}

	public String getPresentacion() {
		return presentacion;
	}
	public void setPresentacion(String presentacion) {
		this.presentacion = presentacion;
	}
    
	public CantidadMonetaria getPrecioKiloCred(){
		if(getPrecioCredito()!=null)
			return getPrecioCredito().divide(getKilos());
		return CantidadMonetaria.pesos(0);
	}

	public BigDecimal getCostoP() {
		return costoP;
	}
	public void setCostoP(BigDecimal costoP) {
		Object oldValue=this.costoP;
		this.costoP = costoP;
		getPropertyChangeSupport().firePropertyChange("costoP", oldValue, costoP);
	}
	
	
    
}