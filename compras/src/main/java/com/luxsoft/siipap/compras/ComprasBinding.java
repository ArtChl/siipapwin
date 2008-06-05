package com.luxsoft.siipap.compras;

import javax.swing.JComboBox;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;

import com.luxsoft.siipap.compras.domain.MetodoDeEstimacion;
import com.luxsoft.siipap.domain.Sucursales;

public class ComprasBinding {
	
	public static JComboBox createMetodoDeAsignacionBinding(final ValueModel holder){
		final SelectionInList sl=new SelectionInList(MetodoDeEstimacion.values(),holder);
		return BasicComponentFactory.createComboBox(sl);
	}
	
	public static JComboBox createSucursalesBinding(final ValueModel holder){
		final SelectionInList sl=new SelectionInList(Sucursales.values(),holder);
		return BasicComponentFactory.createComboBox(sl);
	}

}
