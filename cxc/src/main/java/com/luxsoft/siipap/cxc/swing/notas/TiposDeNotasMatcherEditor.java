package com.luxsoft.siipap.cxc.swing.notas;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.TiposDeNotas;

public class TiposDeNotasMatcherEditor extends AbstractMatcherEditor<NotaDeCredito> implements ItemListener{
	
	private JPanel selector;
	private JCheckBox todos;
	final List<JCheckBox> boxes=new ArrayList<JCheckBox>();
	
	
	public TiposDeNotasMatcherEditor(){
		initComponents();
	}
	
	private void initComponents(){
		FormLayout layout=new FormLayout("p,3dlu,f:p");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		todos=new JCheckBox("");
		todos.setSelected(true);
		todos.setOpaque(false);
		
		todos.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				boolean val=e.getStateChange()==ItemEvent.SELECTED;
				for(JCheckBox bb:boxes)
					bb.setSelected(val);
			}				
		});
		
		builder.append("Todos",todos,true);
		for(TiposDeNotas t:TiposDeNotas.values()){
			final JCheckBox box=new JCheckBox(t.toString(),true);
			box.addItemListener(this);
			box.setOpaque(false);
			box.putClientProperty("tipoNota", t);
			builder.append(t.getDesc(),box,true);
			boxes.add(box);
		}
		
		
		selector=builder.getPanel();
		selector.setOpaque(false);
	}

	public void itemStateChanged(ItemEvent e) {			
		final List<TiposDeNotas> selected=new ArrayList<TiposDeNotas>();
		for(JCheckBox box:boxes){
			if(box.isSelected()){
				TiposDeNotas tipo=(TiposDeNotas)box.getClientProperty("tipoNota");
				selected.add(tipo);
			}
		}
		fireChanged(new TipoDeNotaMatcher(selected));
		
	}
	
	public JPanel getSelector(){
		return selector;
	}
	
	
	public class TipoDeNotaMatcher implements Matcher<NotaDeCredito>{
		
		private final List<TiposDeNotas> tipos;		
		
		private TipoDeNotaMatcher(final List<TiposDeNotas> tipos) {			
			this.tipos = tipos;
		}


		public boolean matches(NotaDeCredito item) {
			if(item==null)
				return false;
			for(TiposDeNotas t:tipos){
				boolean res=t.toString().equalsIgnoreCase(item.getTipo());
				if(res)
					return true;
			}
			return false;			
		}
		
	}
	


}
