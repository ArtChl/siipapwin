package com.luxsoft.siipap.cxc.swing;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXImageView;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class PruebaVisual1 {
	
	public static void testSelectionInList(){
SWExtUIManager.setup();
		
		final EventList<String> source=new BasicEventList<String>();
		final List<String> selected=new ArrayList<String>();
		final ValueHolder selectedHolder=new ValueHolder(selected);
		for(Locale l:Locale.getAvailableLocales()){
			source.add(l.getDisplayCountry());
		}
		
		final SortedList<String> sorted=new SortedList<String>(source,null);
		final EventSelectionModel<String> selectionModel=new EventSelectionModel<String>(sorted);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		SXAbstractDialog dialog=new SXAbstractDialog(""){

			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());				
				SelectionInList sl=new SelectionInList(sorted,selectedHolder);
				JComponent comp=BasicComponentFactory.createComboBox(sl);
				p.add(comp,BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}
			
		};
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			System.out.println("Seleccion: "+selectedHolder.getValue().getClass().getName());
		}
	}
	
	public static void test3(){
		JXFrame f=new JXFrame("Test");
		f.pack();
		f.setDefaultCloseOperation(JXFrame.EXIT_ON_CLOSE);
		JXImageView view=new JXImageView();
		f.addComponent(view);
		f.addComponent(new JXImagePanel());
		f.setVisible(true);
	}
	
	public static void main(String[] args) {
		test3();
		//System.exit(0);
	}

}
