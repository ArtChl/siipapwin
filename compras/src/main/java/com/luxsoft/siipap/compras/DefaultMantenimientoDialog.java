package com.luxsoft.siipap.compras;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.uif.builder.ToolBarBuilder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class DefaultMantenimientoDialog extends SXAbstractDialog{
	
	private Class beanClass;
	private String[] properties;
	private String[] labels;
	
	protected JXTable grid;
	protected EventList source;
	protected EventSelectionModel selectionModel;

	public DefaultMantenimientoDialog() {
		super("Mantenimiento de Catálogos");
		
	}
	protected void setResizable() {
        setResizable(true);
    }

	@Override
	protected JComponent buildContent() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildToolbar(),BorderLayout.NORTH);
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	protected JComponent buildToolbar(){
		ToolBarBuilder builder=new ToolBarBuilder();
		CommandUtils.decorateCommonCURD_Actions(this, builder);
		return builder.getToolBar();
	}
	
	@SuppressWarnings("unchecked")
	protected JComponent buildGridPanel(){
		final TableFormat tf=GlazedLists.tableFormat(
				getBeanClass(), getProperties(), getLabels());
		source=new BasicEventList();
		final SortedList sortedList=new SortedList(source,null);
		final EventTableModel tm=new EventTableModel(sortedList,tf);
		selectionModel=new EventSelectionModel(sortedList);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	public Class getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public String[] getProperties() {
		return properties;
	}

	public void setProperties(String[] properties) {
		this.properties = properties;
	}

	

}
