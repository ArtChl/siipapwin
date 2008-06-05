package com.luxsoft.siipap.cxc.selectores;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Minimiza el esfuerzo requerido para implementar {@link Selector}
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public  abstract class AbstractSelector<T> extends SXAbstractDialog implements Selector<T>{
	
	private final EventList<T> list;
	private EventSelectionModel<T> selectionModel; 
	private String headerTitle;
	private String headerDescription;

	public AbstractSelector(final EventList<T> source,final String title) {
		super(title);
		list=source;
	}

	
	public AbstractSelector(final EventList<T> source,final String title,final String header,final String headerDesc) {
		this(source,title);
		setHeaderTitle(header);
		setHeaderDescription(headerDesc);
	}
	
	@Override
	protected JComponent buildContent() {
		final SortedList<T> sorted=new SortedList<T>(list,null);
		final EventTableModel<T> tm=new EventTableModel<T>(sorted,getTableFormat());
		selectionModel=new EventSelectionModel(sorted);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		final JXTable grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		final Action select=new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				doSelect();
			}
		};
		ComponentUtils.addEnterAction(grid, select);
		grid.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) doSelect();
			}			
		});
		grid.packAll();
		return new JScrollPane(grid);
	}
	
	protected void doSelect(){
		doAccept();
	}
		
	public T getSelected() {
		if(!selectionModel.getSelected().isEmpty()){
			return selectionModel.getSelected().get(0);
		}
		return null;
	}


	protected abstract TableFormat<T> getTableFormat();

	@Override
	protected JComponent buildHeader() {
		if(getHeaderTitle()!=null)
			return new HeaderPanel(getHeaderTitle(),getHeaderDescription());
		return null;
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}

	public String getHeaderDescription() {
		return headerDescription;
	}

	public void setHeaderDescription(String headerDescription) {
		this.headerDescription = headerDescription;
	}


	
	
	
	

}
