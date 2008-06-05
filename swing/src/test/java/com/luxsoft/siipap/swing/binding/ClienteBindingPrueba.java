package com.luxsoft.siipap.swing.binding;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.swing.PruebaVisualConDao;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

public class ClienteBindingPrueba extends PruebaVisualConDao{

	@Override
	protected void execute() {
		
		final ValueModel vm=new ValueHolder();
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				JPanel pp=new JPanel(new GridLayout(3,1));
				//Normal
				final ClienteBinding binding=new ClienteBinding(buffer(vm));
				final JComponent c1=binding.getControl();
				c1.setBorder(BorderFactory.createTitledBorder("Normal"));
				pp.add(c1);
				
				//Deshabilitado por Control
				final ClienteBinding binding2=new ClienteBinding(buffer(vm));				
				JComponent c2=binding2.getControl();
				binding2.setEnabled(false);
				c2.setBorder(BorderFactory.createTitledBorder("Inhabilidado por Componente"));
				pp.add(c2);
				
				//Deshabilitado por Control
				Cliente c=new Cliente();
				c.setClave("U349394");
				c.setNombre("Cliente de prueba");
				vm.setValue(c);
				final ComponentValueModel cvm=new ComponentValueModel(vm);				
				final ClienteBinding binding3=new ClienteBinding(cvm);				
				final JComponent c3=binding3.getControl();
				cvm.setEnabled(false);
				c3.setBorder(BorderFactory.createTitledBorder("Inhabilidado por ValueModel"));
				pp.add(c3);
				
				JPanel panel=new JPanel(new BorderLayout());
				panel.add(pp,BorderLayout.CENTER);
				panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return panel;
			}
			
		};
		dialog.open();
		System.out.println("Cliente: "+vm.getValue());
	}
	
	public static void main(String[] args) {
		new ClienteBindingPrueba().start();
		System.exit(0);
	}

}
