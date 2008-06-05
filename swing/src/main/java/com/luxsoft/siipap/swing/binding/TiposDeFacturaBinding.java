package com.luxsoft.siipap.swing.binding;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.luxsoft.siipap.cxc.domain.TipoDeFactura;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.utils.FormUtils;

/**
 * 
 * @author Ruben Cancino
 * 
 * TODO Hacer que la vista escuche cambios en el modelo de manera correcta
 *
 */
public class TiposDeFacturaBinding extends AbstractControl{
	
	Set<JCheckBox> boxes=new HashSet<JCheckBox>();
	Set<JCheckBox> selected=new HashSet<JCheckBox>();
	
	
	private ValueModel selectionAsStringModel;
	private boolean enabled;
	
	public TiposDeFacturaBinding(){		
	}
	
	public TiposDeFacturaBinding(final ValueModel stringSelection){
		setSelectionAsStringModel(stringSelection);
		//stringSelection.addValueChangeListener(stringModelHandler);
		
	}

	@Override
	protected JComponent buildContent() {
		DefaultFormBuilder builder=FormUtils
			.createCheckBoxSelector("Tipos de Factura");
		for(TipoDeFactura tipo:TipoDeFactura.values()){
			JCheckBox box=new JCheckBox(tipo.getDesc(),false);
			box.putClientProperty("tipo", tipo);
			box.setName(tipo.getId());
			box.addItemListener(checkHandler);
			box.setEnabled(isEnabled());
			builder.append(tipo.getId(),box);
			boxes.add(box);
		}
		final JCheckBox tbox=new JCheckBox("",false);
		tbox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				for(JCheckBox c:boxes){
					c.setSelected(tbox.isSelected());
				}
			}
			
		});
		builder.append("Todos" ,tbox);
		
		if(getSelectionAsStringModel().getValue()!=null){
			setSelectedAsString(getSelectionAsStringModel().getValue().toString());
		}
		//builder.setDefaultDialogBorder();
		return builder.getPanel();
	}

	public ValueModel getSelectionAsStringModel() {
		return selectionAsStringModel;
	}

	public void setSelectionAsStringModel(ValueModel selectionAsStringModel) {
		this.selectionAsStringModel = selectionAsStringModel;
	}
	
	
	
	private void updateModel(){
		if(getSelectionAsStringModel()!=null){
			Set<String> set=new TreeSet<String>();
			
			for(JCheckBox box:selected){
				TipoDeFactura tipo=(TipoDeFactura)box.getClientProperty("tipo");
				set.add(tipo.getId());
			}
			StringBuffer buff=new StringBuffer();
			for(String s:set){
				buff.append(s);
			}
			getSelectionAsStringModel().setValue(buff.toString());
		}
	}
	
	private ItemListener checkHandler=new ItemListener(){

		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED){
				JCheckBox box=(JCheckBox)e.getSource();				
				selected.add(box);
			}else if(e.getStateChange()==ItemEvent.DESELECTED){
				JCheckBox box=(JCheckBox)e.getSource();				
				selected.remove(box);
			}			
			updateModel();
			
		}
		
	};
	
	private void setSelectedAsString(String val){
		char[] cc=val.toCharArray();		
		for(JCheckBox box:boxes){
			TipoDeFactura tipo=(TipoDeFactura)box.getClientProperty("tipo");
			char key=tipo.getId().charAt(0);
			
			int index=Arrays.binarySearch(cc, key);
			//System.out.println(key+" En:"+Arrays.toString(cc)+" Index: "+index);
			box.setSelected((index>=0));
		}
		
	}
	
	public void setEnabled(boolean val){
		for(JCheckBox box:boxes){
			box.setEnabled(val);
		}
		this.enabled=val;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	
	
	/**
	 * Actualiza el modelo si cambia la vista
	 *  
	 
	private PropertyChangeListener stringModelHandler=new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue()!=null){				
				
				String val=evt.getNewValue().toString().toUpperCase();
				setSelectedAsString(val);
			}
		}
		
	};
	*/
	

}
