package com.luxsoft.siipap.swing.drill;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;


import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.utils.BrowserUtils;


public class Drill extends AbstractControl{
	
	private JTable drillGrid;	
	private final EventList source;
	private final TableFormat tableFormat;
	private TextFilterator textFilterator;
	
	public Drill(final EventList source,final TableFormat tableFormat){
		this.source=source;
		this.tableFormat=tableFormat;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected JComponent buildContent() {
		JPanel cpanel=new JPanel(new BorderLayout(5,5));
		
		final SortedList sortedList=new SortedList(source,null);
		EventList targetList=sortedList;
		if(getTextFilterator()!=null){
			final JTextField inputField=new JTextField(20);		
			final MatcherEditor editor=new TextComponentMatcherEditor(inputField,getTextFilterator());
			final FilterList filterList=new FilterList(sortedList,editor);
			targetList=filterList;
			cpanel.add(BrowserUtils.buildTextFilterPanel(inputField),BorderLayout.NORTH);
		}
		
		EventTableModel tm=new EventTableModel(targetList, tableFormat);
		final EventSelectionModel sm=new EventSelectionModel(targetList);
		final JXTable grid=BrowserUtils.buildBrowserGrid();
		drillGrid=grid;
		grid.setModel(tm);		
		grid.setSelectionModel(sm);
		grid.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					System.out.println("Drilling..");
					if(sm.getSelected().isEmpty())
						return;
					drillDown(sm.getSelected().get(0));
				}
			}
		});
		JComponent c=UIFactory.createTablePanel(grid);
		
		cpanel.add(c,BorderLayout.CENTER);		
		return cpanel;
		
	}
	

	public JTable getDrillGrid() {
		return drillGrid;
	}
	
	public void addDrill(Drill drill){
		
	}
	
	public void drillDown(final Object obj){
		
	}
	public void drillUp(){
		
	}

	public TextFilterator getTextFilterator() {
		return textFilterator;
	}
	public void setTextFilterator(TextFilterator textFilterator) {
		this.textFilterator = textFilterator;
	}
	
}
