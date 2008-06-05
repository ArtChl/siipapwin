package com.luxsoft.siipap.cxc.swing.notas;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;


import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.TiposDeNotas;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class NotasFormFactory {
	
	public static void generarNota(){
		
		final ValueModel model=new ValueHolder();
		
		SXAbstractDialog dialog=new SXAbstractDialog("Alta"){			
			
			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				JComboBox box=CXCBindings.createBindingParaAltas(model);
				p.add(box,BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}
			
			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Alta de Nota de Credito"," Seleccione un tipo de Nota a generar");
			}
			
		};
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			TiposDeNotas tipo=(TiposDeNotas)model.getValue();
			switch (tipo) {
			case J:
			case H:
			case I:
				NotaDeCreditoPorDevolucionForm.show();
				break;
			default:
				NotaDeCreditoForm.show();
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		generarNota();
	}

}
