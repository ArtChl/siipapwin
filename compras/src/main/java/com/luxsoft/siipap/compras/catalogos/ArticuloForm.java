package com.luxsoft.siipap.compras.catalogos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.form2.IFormModel;
import com.luxsoft.siipap.swing.utils.FormatUtils;

public class ArticuloForm extends AbstractForm{
	
	
	public ArticuloForm(IFormModel model) {
		super(model);
	}

	@Override
	protected JComponent buildFormPanel() {
		final FormLayout layout=new FormLayout(
				" p,2dlu,max(p;60dlu),3dlu " +
				",p,2dlu,max(p;60dlu),3dlu " +
				",p,2dlu,p:g"
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		if(model.getValue("id")!=null)
			getControl("clave").setEnabled(false);
		builder.append("Clave",getControl("clave"),true);
		builder.append("Descripción1",getControl("descripcion1"),9);
		
		builder.appendSeparator("Clasificación");
		builder.append("Linea",getControl("linea"));
		builder.append("Clase",getControl("clase"),true);
		builder.append("Marca",getControl("marca"));
		builder.append("Clasificación",getControl("clasificacion"),true);
		builder.append("Acabado",getControl("acabado"));
		builder.append("Color",getControl("color"));
		builder.append("Nacional",getControl("nacional"));
		
		builder.appendSeparator("Dimensiones");		
		builder.append("Kilos",getControl("kilos"));
		builder.append("Gramos",getControl("gramos"));
		builder.append("Calibre",getControl("calibre"));
		builder.append("Largo",getControl("largo"));
		builder.append("Ancho",getControl("ancho"));
		builder.append("Caras",getControl("caras"));
		
		builder.appendSeparator("Precios & Costos");
		builder.append("Costo P",getControl("costoP"));
		builder.append("Precio Neto",getControl("precioNeto"));
		
		return builder.getPanel();
	}
	protected JComponent createNewComponent(final String property){
		JComponent c=super.createNewComponent(property);
		c.setEnabled(!model.isReadOnly());
		return c;
	}
	
	private JFormattedTextField costoField;
	
	@Override
	protected JComponent createCustomComponent(String property) {		
		if("linea".equals(property)){
			JComboBox box= COMBindings.createLineasBinding(model.getModel(property));
			//box.setEnabled(model.isReadOnly());
			//box.getEditor().getEditorComponent().setEnabled(model.isReadOnly());
			return box;
		}else if("clase".equals(property)){
			return COMBindings.createClasesBinding(model.getModel(property));
		}else if("marca".equals(property)){
			return COMBindings.createMarcaBinding(model.getModel(property));
		}else if("clasificacion".equals(property)){
			return COMBindings.createClasificacionBox(model.getModel(property));
		}else if("color".equals(property)){
			return COMBindings.createColorBox(model.getModel(property));
		}else if("acabado".equals(property)){
			return COMBindings.createAcabadoBox(model.getModel(property));
		}else if("kilos".equals(property) || "largo".equals(property)|| "ancho".equals(property)){
			return COMBindings.createNumberBinding(model.getModel(property));
		}else if("costoP".equals(property)){
			costoField=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
			BigDecimal cp=(BigDecimal)model.getValue("costoP");
			costoField.setValue(cp.doubleValue());
			costoField.addPropertyChangeListener("value", new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					Number val=(Number)evt.getNewValue();
					model.setValue("costoP", BigDecimal.valueOf(val.doubleValue()));
				}				
			});
			return costoField;
			//return COMBindings.createBigDecimalMonetaryBinding(model.getModel(property));
		}		
		return super.createCustomComponent(property);
	}
	
	

	public static void main(String[] args) {
		final Articulo a=new Articulo();
		a.setClave("T001");
		a.setDescripcion1("Descripcion 1");
		final DefaultFormModel model=new DefaultFormModel(a);
		final ArticuloForm form=new ArticuloForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			System.out.println(a.getDescripcion1());
			System.out.println(a.getLinea());
			System.out.println(a.getClase());
			System.out.println(a.getMarca());
		}
		System.exit(0);
		
	}

}
