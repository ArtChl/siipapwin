package com.luxsoft.siipap.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Minimiza el esfuerzo requerido para el mantenimiento de entidades
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public  abstract class AbstractCatalogDialog<T> extends SXAbstractDialog {
	
	protected Logger logger=Logger.getLogger(getClass());
	
	protected final EventList<T> source;
	protected SortedList<T> sortedSource;
	private EventSelectionModel<T> selectionModel; 
	private String headerTitle;
	private String headerDescription;
	protected JXTable grid;
	protected boolean confirmarEliminar=true;
	private List<Action> toolbarActions;
	

	public AbstractCatalogDialog(final String title) {
		this(new BasicEventList(),title);
	}
	
	public AbstractCatalogDialog(final EventList<T> source,final String title) {
		super(title);
		this.source=source;
	}
	
	

	
	public AbstractCatalogDialog(final EventList<T> source,final String title,final String header,final String headerDesc) {
		this(source,title);
		setHeaderTitle(header);
		setHeaderDescription(headerDesc);
	}
	
	@Override
	protected JComponent buildContent(){
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		panel.add(buildToolbar(),BorderLayout.NORTH);
		panel.setPreferredSize(getDefaultSize());
		return panel;
	}
	
	/**
	 * Sobre escribir para ajustar un tamaño 
	 * @return
	 */
	protected Dimension getDefaultSize(){
		return new Dimension(600,400);
	}
	
	
	protected JComponent buildToolbar(){
		final ToolBarBuilder builder=new ToolBarBuilder();
		for (Action a:getToolbarActions()){
			builder.add(a);
		}
		return builder.getToolBar();
	}
	
	protected List<Action> getToolbarActions(){
		if(toolbarActions==null)
			toolbarActions=CommandUtils.createCommonCURD_Actions(this);
		return toolbarActions;
	}
	
	
	protected JComponent buildMainPanel() {
		sortedSource=new SortedList<T>(getFilteredSource(),null);
		final EventTableModel<T> tm=new EventTableModel<T>(sortedSource,getTableFormat());
		selectionModel=new EventSelectionModel(sortedSource);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		final Action select=new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				view();
			}
		};
		ComponentUtils.addEnterAction(grid, select);
		grid.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) view();
			}			
		});
		grid.packAll();
		new TableComparatorChooser<T>(grid,sortedSource,true);
		ComponentUtils.decorateActions(grid);
		return new JScrollPane(grid);
	}
	
	protected EventList<T> getFilteredSource(){
		return this.source;
	}
		
	public T getSelected() {
		if(!selectionModel.getSelected().isEmpty()){
			return selectionModel.getSelected().get(0);
		}
		return null;
	}
	
	public void view(){
		T selected=getSelected();
		if(selected!=null){
			doView(selected);
		}
	}
	
	/**
	 * Template Method para personalizar la consulta de beans
	 * 
	 * @param bean
	 */
	protected void doView(T bean){}
	
	public void edit(){
		T selected=getSelected();
		if(selected!=null){
			T bean=doEdit(selected);
			if(bean!=null){
				int index=source.indexOf(bean);
				source.set(index, bean);
			}
		}
	}
	
	/**
	 * Template Method para personalizar la edicion de beans
	 * 
	 * @param bean
	 */
	protected T doEdit(T bean){return null;}
	
	
	public void delete(){
		if(isConfirmarEliminar()){
			if(!MessageUtils.showConfirmationMessage("Seguro que desa eliminar: \n"+getSelected(), "Eliminar registro"))
				return;
		}
		try {
			if(doDelete())				
				source.remove(getSelected());
		} catch (Exception e) {
			MessageUtils.showError("Error eliminando registro",e);
		}
		
	}
	
	protected boolean doDelete(){
		
		return false;
	}
	
	public void insert(){
		Object o=doInsert();
		if(o!=null){
			source.add((T)o);
			grid.packAll();
			int index=sortedSource.indexOf(o);
			selectionModel.setSelectionInterval(index, index);
		}
	}
	
	/**
	 * Template method para facilitar la generacion de nuevos registros en el browser
	 * 
	 * @return
	 */
	protected Object doInsert(){
		return null;
	}
	
	public void filter(){
		
	}
	public void print(){
		
	}
	
	public void refresh(){
		SwingWorker<List<T>, String> worker=new SwingWorker<List<T>, String>(){
			@Override
			protected List<T> doInBackground() throws Exception {
				return getData();
			}
			@Override
			protected void done() {
				try {
					source.clear();
					source.addAll(get());
					grid.packAll();
				} catch (Exception e) {
					MessageUtils.showError("Error cargando datos",e);
				}
			}
			
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	protected abstract List<T> getData();


	protected abstract TableFormat<T> getTableFormat();
	
	

	@Override
	protected void onWindowOpened() {
		refresh();
	}

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

	public boolean isConfirmarEliminar() {
		return confirmarEliminar;
	}

	public void setConfirmarEliminar(boolean confirmarEliminar) {
		this.confirmarEliminar = confirmarEliminar;
	}
	
	

}
