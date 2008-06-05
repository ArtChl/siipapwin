package com.luxsoft.siipap.cxc.desc;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.cxc.domain.DescuentoEspecial;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.dialog.AbstractCatalogDialog;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

/**
 * Ventana para el mantenimiento de Descuentos por factura
 * antes tambien conocidos como descuentos especiales
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentosPorFacturaDialog extends AbstractCatalogDialog<DescuentoEspecial>{
	
	

	public DescuentosPorFacturaDialog() {
		super(new BasicEventList<DescuentoEspecial>(), "Descuentos especiales");		
	}
	
	@Override
	protected JComponent buildContent() {				
		JComponent c=super.buildContent();
		c.setPreferredSize(new Dimension(700,400));
		grid.packAll();
		return c;
	}

	@Override
	protected List<DescuentoEspecial> getData() {		
		return ServiceLocator.getDescuentosManager().buscarDescuentosPorVenta();
	}

	@Override
	protected TableFormat<DescuentoEspecial> getTableFormat() {		
		return GlazedLists.tableFormat(DescuentoEspecial.class
			, new String[]{"id","venta.id","venta.numero"
			,"venta.clave","venta.nombre","venta.fecha","venta.total","venta.saldo","descuento","autorizo","fechaAutorizacion","comentario"
			}
			, new String[]{"Id","VentaId","Factura"
			,"Cliente","Nombre","Fecha","Total","Saldo","Descuento","Autorizo","F.Autorizacion","Comentario"
			}
		);
	}
	
	public void insert(){
		if(logger.isDebugEnabled()){
			logger.debug("Generando descuento especial");
		}
		DefaultFormModel model=new DefaultFormModel(new DescuentoEspecial());
		DescuentoEspecialForm form=new DescuentoEspecialForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			final DescuentoEspecial d=(DescuentoEspecial)model.getBaseBean();
			ServiceLocator.getDescuentosManager().salvar(d);			
			this.source.add((DescuentoEspecial)model.getBaseBean());
			grid.packAll();			
		}
	}
	
	
	
	protected boolean doDelete(){
		try {
			ServiceLocator.getDescuentosManager().eliminarDescuento(getSelected());
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		
	}

	public static void main(String[] args) {
		DescuentosPorFacturaDialog dialog=new DescuentosPorFacturaDialog();
		dialog.open();
	}

}
