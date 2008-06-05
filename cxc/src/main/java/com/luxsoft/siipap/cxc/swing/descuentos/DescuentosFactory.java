package com.luxsoft.siipap.cxc.swing.descuentos;

import java.util.Date;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.domain.Articulo;

/**
 * Fabrica de Descuentos para centralizar la cacion de beans de este tipo
 * configurando sus propiedades con los defaults adecuados 
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentosFactory {
	
	
	public static DescuentoPorArticulo descuentoPorArticulo(final Articulo articulo,final Cliente c){
		DescuentoPorArticulo d=new DescuentoPorArticulo();
		d.setArticulo(articulo);
		d.setActivo(true);
		d.setAdicional(0);
		d.setClave(c.getClave());
		d.setNombre(c.getNombre());
		d.setCliente(c);
		d.setClaveArticulo(articulo.getClave());
		d.setCreado(new Date());
		d.setFechaAutorizacion(new Date());
		d.setModificado(new Date());
		d.setPrecioNeto(false);
		d.setTipoFac("E");
		return d;
	}

}
