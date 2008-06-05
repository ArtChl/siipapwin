package com.luxsoft.siipap.cxc.swing.binding;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.TipoDeFactura;

public class FacturasBindings {
	
	public static JComboBox createFacturasBinding(final ValueModel model){
		List<String> list=TipoDeFactura.asStringList();
		SelectionInList sl=new SelectionInList(list,model);
		return BasicComponentFactory.createComboBox(sl, new StringTipoFacListCellRenderer());
	}
	
	
	
	public static class StringTipoFacListCellRenderer extends DefaultListCellRenderer{

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if(value!=null){
				TipoDeFactura tip=TipoDeFactura.parse(value.toString());
				if(tip!=null)
					value=tip.getDesc();
			}
			return super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
		}
		
		
		
	}

}
