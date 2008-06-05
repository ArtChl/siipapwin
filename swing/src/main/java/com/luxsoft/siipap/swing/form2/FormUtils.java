package com.luxsoft.siipap.swing.form2;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationComponentUtils.Visitor;

@SuppressWarnings("unchecked")
public class FormUtils {
	
	
	
	public static void disableAllComponents(Container container){
		final ValidationComponentUtils.Visitor v=new ValidationComponentUtils.Visitor(){
			public void visit(JComponent component, Map keyMap) {
				component.setEnabled(false);
			}			
		};
		visitComponentTree(container, new HashMap(), v);
	}
	
	public static void visitComponentTree(Container container, Map keyMap, Visitor visitor) {
        int componentCount = container.getComponentCount();
        for (int i = 0; i < componentCount; i++) {
            Component child = container.getComponent(i);
            if (child instanceof JTextField) {
            	JComponent component = (JComponent) child;
                visitor.visit(component, keyMap);
                
            } else if (child instanceof Container) {
            	visitComponentTree((Container) child, keyMap, visitor);
            	
            }
        }
    }
	
	
	
	

}
