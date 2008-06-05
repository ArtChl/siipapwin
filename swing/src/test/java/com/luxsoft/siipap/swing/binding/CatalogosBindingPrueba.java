package com.luxsoft.siipap.swing.binding;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.swing.PruebaVisualConDao;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

public class CatalogosBindingPrueba extends PruebaVisualConDao{

	@Override
	protected void execute() {
		
		final ValueModel linea=new ValueHolder();
		final ValueModel marca=new ValueHolder();
		final ValueModel clase=new ValueHolder();
		
		SXAbstractDialog dialog=new SXAbstractDialog("Catalogos"){

			@Override
			protected JComponent buildContent() {
				JPanel pp=new JPanel(new GridLayout(3,1));
				
				//Linea
				final JComboBox lineas=CatalogosBinding.createLineasBinding(linea);
				pp.add(lineas);	
				//Marcas
				final JComboBox marcas=CatalogosBinding.createMarcasBinding(marca);
				pp.add(marcas);
				//Clases
				final JComboBox clases=CatalogosBinding.createClasesBinding(clase);
				pp.add(clases);
				
				JPanel panel=new JPanel(new BorderLayout());
				panel.add(pp,BorderLayout.CENTER);
				panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return panel;
			}
			
		};
		dialog.open();
		System.out.println("Linea : "+linea.getValue()+" Type: "+linea.getValue().getClass().getName());
		System.out.println("Marca : "+marca.getValue()+" Type: "+marca.getValue().getClass().getName());
		System.out.println("Clase : "+clase.getValue()+" Type: "+clase.getValue().getClass().getName());
		
	}
	
	public static void main(String[] args) {
		new CatalogosBindingPrueba().start();
		System.exit(0);
	}

}
