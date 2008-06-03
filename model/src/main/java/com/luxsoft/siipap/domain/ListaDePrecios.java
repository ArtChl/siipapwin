/*
 * Created on 30/03/2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.PersistentObject;





/**
 * Lista de precios de articulos comercializables 
 * 
 * @author Ruben Cancino 
 */
public class ListaDePrecios extends PersistentObject {
    
    
    private long numero;
    private String descripcion;
    private Periodo periodo=Periodo.getPeriodoDelMesActual();
    private Proveedor proveedor;
    private Date creado=currentTime();
    private Date modificado;
    
    /** Descripciones de Descuentos  NO PARECE SER ESTE EL LUGAR PARA COLOCAR 
     *  DESCRIPCION DE LOS DESCUENTOS**/
    
    private String descDesc1;
    private String descDesc2;
    private String descDesc3;
    private String descDesc4;
    private String descDesc5;
    private String descDesc6;
    private String descDesc7;
    private String descDesc8;
    
    private Set<Precio> precios=new HashSet<Precio>();
    
    
    
	public long getNumero() {
		return numero;
	}
	public void setNumero(long numero) {
		this.numero = numero;
	}
	
    public ListaDePrecios() {    
    }
    
    @SuppressWarnings("unused")
	private ListaDePrecios(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Periodo getPeriodo() {
        return periodo;
    }
    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }
    
    public Proveedor getProveedor() {
        return proveedor;
    }
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
    
    public String getDescDesc1() {
        return descDesc1;
    }
    public void setDescDesc1(String descDesc1) {
        this.descDesc1 = descDesc1;
    }
    public String getDescDesc2() {
        return descDesc2;
    }
    public void setDescDesc2(String descDesc2) {
        this.descDesc2 = descDesc2;
    }
    public String getDescDesc3() {
        return descDesc3;
    }
    public void setDescDesc3(String descDesc3) {
        this.descDesc3 = descDesc3;
    }
    public String getDescDesc4() {
        return descDesc4;
    }
    public void setDescDesc4(String descDesc4) {
        this.descDesc4 = descDesc4;
    }
    public String getDescDesc5() {
        return descDesc5;
    }
    public void setDescDesc5(String descDesc5) {
        this.descDesc5 = descDesc5;
    }
    public String getDescDesc6() {
        return descDesc6;
    }
    public void setDescDesc6(String descDesc6) {
        this.descDesc6 = descDesc6;
    }
    public String getDescDesc7() {
        return descDesc7;
    }
    public void setDescDesc7(String descDesc7) {
        this.descDesc7 = descDesc7;
    }
    public String getDescDesc8() {
        return descDesc8;
    }
    public void setDescDesc8(String descDesc8) {
        this.descDesc8 = descDesc8;
    }
    
    public Set<Precio> getPrecios() {
        return precios;
    }
    
    @SuppressWarnings("unused")
	private void setPrecios(Set<Precio> precios) {
        this.precios = precios;
    }
    
    public void agregarPrecio(Precio p){
        p.setLista(this);
        getPrecios().add(p);
    }
    
    public void agregarPrecios(final List<Precio> precios){
    	for(Precio p:precios){
    		p.setLista(this);
    	}
    	getPrecios().addAll(precios);
    }
    
    public void eliminarPrecio(Precio p){
        if(getPrecios().contains(p)){
            getPrecios().remove(p);
        }
    }
    
    public Precio agregarArticulo(final ArticuloPorProveedor ap){
    	Precio p=new Precio();
    	p.setArticuloProveedor(ap);
    	getPrecios().add(p);
    	p.setLista(this);
    	return p;
    }
    
    public boolean equals(Object obj) {
        if(obj==null)return false;
        if(obj==this)return true;
        ListaDePrecios lp=(ListaDePrecios)obj;
        return new EqualsBuilder()
        //.append(getProveedor(),lp.getProveedor())
        //.append(getPeriodo(),lp.getPeriodo())
        //.append(getCreado(),lp.getCreado())
        .append(getId(),lp.getId())
        .isEquals();
    }

    public int hashCode() {        
        return new HashCodeBuilder(1,7)
        	//.append(getProveedor())
        	//.append(getPeriodo())
        	//.append(getCreado())
        	.append(getId())
        	.toHashCode();
    }
    
    public String toString(){
        return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append(getId())
        	.append(" ",descripcion)        	
        	.append(" ",periodo)
        	.toString();
    }
    
    public static String descripcionStandar(ListaDePrecios lp){
    	SimpleDateFormat df=new SimpleDateFormat("MMMM/yyyy");
    	String m="Lista de Precios para: "+df.format(lp.getPeriodo().getFechaInicial());
    	return m;
    }

}
