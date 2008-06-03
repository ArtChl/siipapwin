package com.luxsoft.siipap.maquila.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.luxsoft.siipap.domain.Articulo;

/**
 * Articulo capaz de ser transformado en otro Articulo
 * 
 * @author Ruben Cancino
 *
 */
public class Bobina extends Articulo{
	
	private Set<Conversion> conversiones;

	public Set<Conversion> getConversiones() {
		if(conversiones==null)
			setconversiones(new HashSet<Conversion>());
		return conversiones;
	}

	public void setconversiones(Set<Conversion> conversiones) {
		this.conversiones = conversiones;
	}
	
	public Conversion addConversion(final Articulo articulo){
		Conversion c=new Conversion(articulo,this);
		addConversion(c);
		return c;
	}
	
	public Conversion addConversion(Conversion c){
		getConversiones().add(c);
		return c;
	}
	
	public void removeConversion(final Conversion c){
		getConversiones().remove(c);
		c.setBobina(null);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Conversion> getConversiones(final Articulo a){
		Predicate select=new Predicate(){

			public boolean evaluate(Object object) {
				Conversion c=(Conversion)object;
				if(c.getArticulo().equals(a))
					return true;
				return false;
			}
			
		};
		Collection c=CollectionUtils.select(getConversiones(),select);
		return new HashSet<Conversion>(c);
	}
	

}
