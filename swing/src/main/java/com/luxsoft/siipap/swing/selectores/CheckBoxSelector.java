package com.luxsoft.siipap.swing.selectores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * AbstractMatcherEditor para facilitar la creacion de un ChekBoxSelector
 * que sirven para seleccionar con ayuda de un JCheckBox algun el tipo de Matcher
 * 
 * @author Ruben Cancino 
 *
 */
public abstract class CheckBoxSelector<E> extends AbstractMatcherEditor<E> implements ActionListener{
	
	protected JCheckBox box;
	
	public CheckBoxSelector(){
		box=new JCheckBox("",false);
		box.setOpaque(false);
		box.addActionListener(this);
	}
	
	public JCheckBox getBox(){
		return box;
	}

	public void actionPerformed(ActionEvent e) {
		if(box.isSelected())
			select();
		else
			unSelect();
	}
	
	protected void select(){
		fireChanged(getSelectMatcher());
	}
	
	protected void unSelect(){
		fireMatchAll();
	}
	
	protected abstract Matcher<E> getSelectMatcher(Object... obj);
	
	
}