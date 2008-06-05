package com.luxsoft.siipap.cxc.swing;

import java.beans.PropertyChangeListener;

import javax.swing.JTextField;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.dao.ArticuloDao;



public class PruebasGenericas extends MockObjectTestCase{
	
	/**
	 * Probar como funciona el SelectionInList
	 * 
	 */
	public static void selectionInList(){
		
	}
	
	public static void componentValueModel(){
		
	}
	
	public void testDescuentoPorArticuloModel(){
		DescuentoPorArticulo d=new DescuentoPorArticulo();
		PresentationModel model=new PresentationModel(d);
		ValueModel vm=model.getModel("comentario");
		JTextField tfComentario=BasicComponentFactory.createTextField(vm,false);
		tfComentario.setText("COMENTARIO");
		assertEquals("COMENTARIO",d.getComentario());		
	}
	
	public void testEnabledComponent(){
		DescuentoPorArticulo d=new DescuentoPorArticulo();
		PresentationModel model=new PresentationModel(d);
		ComponentValueModel vm=model.getComponentModel("comentario");
		JTextField tfComentario=BasicComponentFactory.createTextField(vm,false);
		vm.setEnabled(false);
		vm.setEditable(false);
		assertFalse(tfComentario.isEnabled());
		assertFalse(tfComentario.isEditable());		
	}
	
	public void testPropertyListener(){
		DescuentoPorArticulo d=new DescuentoPorArticulo();
		PresentationModel model=new PresentationModel(d);
		ValueModel vm=model.getModel("comentario");
		
		Mock mockListener=mock(PropertyChangeListener.class);
		mockListener.expects(once()).method("propertyChange").withAnyArguments();
		vm.addValueChangeListener((PropertyChangeListener)mockListener.proxy());
		
		JTextField tfComentario=BasicComponentFactory.createTextField(vm,false);
		tfComentario.setText("COMENTARIO");
		ArticuloDao obj=(ArticuloDao)this.newDummy(ArticuloDao.class);
		
		//obj.buscarPorClave("test");
		System.out.println("Dummy: "+obj.getClass().getName());
		//assertEquals("COMENTARIO",d.getComentario());		
	}

}
