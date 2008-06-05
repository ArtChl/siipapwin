package com.luxsoft.siipap.cxc.swing.descuentos;

import java.util.Comparator;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.UniqueList;

import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * Bean para el mantenimiento en grupo de descuentos
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentosPorArticulos extends Model{
	
	
	private Cliente cliente;
	private double descuento=0;
	private int maximo;
	private int minimo;
	private boolean activo=true;
	private CantidadMonetaria precioKilo;
	private EventList<DescuentoPorArticulo> descuentos;
	private boolean porPrecioKilo=false;
	
	
	
	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(final Cliente cliente) {
		Object old=this.cliente;
		this.cliente = cliente;
		firePropertyChange("cliente", old, cliente);
	}
	
	
	
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		double old=this.descuento;
		this.descuento = descuento;
		firePropertyChange("descuento", old, descuento);
	}
	
	public int getMaximo() {
		return maximo;
	}
	public void setMaximo(int maximo) {
		this.maximo = maximo;
	}
	
	public int getMinimo() {
		return minimo;
	}
	public void setMinimo(int minimo) {
		this.minimo = minimo;
	}
	
	public EventList<DescuentoPorArticulo> getDescuentos() {
		if(descuentos==null){						
			descuentos=createUniqueList();
		}
		return descuentos;
	}
	
	
	public void setDescuentos(EventList<DescuentoPorArticulo> descuentos) {
		this.descuentos = descuentos;
	}
	private EventList<DescuentoPorArticulo> createUniqueList(){
		final Comparator<DescuentoPorArticulo> comparator=GlazedLists.beanPropertyComparator(DescuentoPorArticulo.class, "articulo");
		final EventList<DescuentoPorArticulo> source=new BasicEventList<DescuentoPorArticulo>();
		final UniqueList<DescuentoPorArticulo> uniqueList=new UniqueList<DescuentoPorArticulo>(source,comparator);
		return uniqueList;
	}
	
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		boolean old=this.activo;
		this.activo = activo;
		firePropertyChange("activo", old, activo);
	}
	public CantidadMonetaria getPrecioKilo() {
		return precioKilo;
	}
	public void setPrecioKilo(CantidadMonetaria precioKilo) {
		Object old=this.precioKilo;
		this.precioKilo = precioKilo;
		firePropertyChange("precioKilo", old, precioKilo);
	}
	public boolean isPorPrecioKilo() {
		return porPrecioKilo;
	}
	
	public void setPorPrecioKilo(boolean porPrecioKilo) {
		boolean old=this.porPrecioKilo;
		this.porPrecioKilo = porPrecioKilo;
		firePropertyChange("porPrecioKilo", old, porPrecioKilo);
	}

}
