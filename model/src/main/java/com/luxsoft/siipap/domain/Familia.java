/*
 * Created on 9/09/2004
 *
 * TODO Licencia
 */
package com.luxsoft.siipap.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.luxsoft.utils.domain.MutableObject;





/**
 * Familia de Productos, Clasificador de productos
 * 
 * @author Ruben
 */
public class Familia extends MutableObject implements Serializable,Comparable<Familia>{

    private Long id;
    private String clave;
    private String descripcion;
    private Boolean deMovimiento=Boolean.FALSE;
    private Boolean bloqueada=Boolean.FALSE;
    private Familia padre;
    private Date creado;
    private Date modificado;
    
    private Set<Familia> subFamilias=new HashSet<Familia>();
    private Set<Articulo> articulos = new HashSet<Articulo>();

    public Familia() {

    }
    
    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
	private void setId(Long id) {
        final String name="id";
        Object old=this.id;
        this.id = id;
        getPropertyChangeSupport().firePropertyChange(name,old,this.id);
    }
    
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        final String name="clave";
        Object old=this.clave;
        this.clave = clave;
        getPropertyChangeSupport().firePropertyChange(name,old,clave);
    }

   
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        final String name="descripcion";
        Object old=this.descripcion;
        this.descripcion = descripcion;
        getPropertyChangeSupport().firePropertyChange(name,old,descripcion);
    }    
    
   
    
	public Boolean getBloqueada() {
		return bloqueada;
	}
	public void setBloqueada(Boolean bloqueada) {
		this.bloqueada = bloqueada;
	}
	public Boolean getDeMovimiento() {
		return deMovimiento;
	}
	public void setDeMovimiento(Boolean deMovimiento) {
		this.deMovimiento = deMovimiento;
	}
    public Familia getPadre() {
        return padre;
    }
    public void setPadre(final Familia padre) {
        Object old=this.padre;
        this.padre = padre;
        getPropertyChangeSupport().firePropertyChange("padre",old,padre);
    }
    
    public Set<Familia> getSubFamilias() {
        return subFamilias;
    }
    
    @SuppressWarnings("unused")
	private void setSubFamilias(Set<Familia> subFamilias) {
        this.subFamilias = subFamilias;
    }
    
    public void agregarSubFamilia(final Familia fam){        
        fam.setPadre(this);
        getSubFamilias().add(fam);
    }
    
    public Set<Articulo> getArticulos() {
    	if(articulos==null)
    		articulos=new HashSet<Articulo>();
        return articulos;
    }
    @SuppressWarnings("unused")
	private void setArticulos(Set<Articulo> articulos) {
        this.articulos = articulos;
    }
    
    
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
    public boolean equals(Object obj) {
        
        boolean equals=false;
        if(obj!=null && Familia.class.isAssignableFrom(obj.getClass())){
            Familia cve=(Familia)obj;
            equals=new EqualsBuilder()
                     .append(clave,cve.getClave())
                     .isEquals();
        }
        return equals;
    }

    public int hashCode() {        
        return new HashCodeBuilder(3,7)
        	.append(clave)
        	.toHashCode();
    }
    
    public String toString(){
        return getClave()+"  "+getDescripcion();
    }

	public int compareTo(final Familia f2) {
		int[] l1=convertClaveFamilia(getClave());
		int[] l2=convertClaveFamilia(f2.getClave());
		int i=0;
		while(i<l1.length){
			if(l1[i]!=l2[i])
				return l1[i]-l2[i];
			i++;
		}
		return 0;		
	}
	
	private int[] convertClaveFamilia(String clave){
		StringBuffer bs=new StringBuffer();
		int of=0;
		for(char c:clave.toCharArray()){
			bs.append(c);
			if(++of%2==0)
				bs.append(".");
		}		
		String[] n=bs.toString().split("\\.");
		int[] i=new int[n.length];
		for(int y=0;y<i.length;y++){
			i[y]=Integer.valueOf(n[y]);
		}
		return i;
	}

	
    
    
}
