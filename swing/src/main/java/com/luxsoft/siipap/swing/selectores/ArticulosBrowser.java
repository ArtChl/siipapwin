package com.luxsoft.siipap.swing.selectores;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.uif.util.ComponentUtils;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uif.util.ScreenUtils;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.ArticuloRow;
import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.controls.ViewControl;
import com.luxsoft.siipap.swing.utils.BrowserUtils;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.SwingWorkerWaiter;
import com.luxsoft.siipap.swing.utils.SwingWorkerWaiter2;

/**
 * Grid de Articulos standard
 * 
 * @author Ruben Cancino
 *
 */
public class ArticulosBrowser implements ViewControl{
	
	private Logger logger=Logger.getLogger(getClass());
	private ArticuloDao manager;
	private EventList<ArticuloRow> articulos;	 
	private EventSelectionModel<ArticuloRow> selectionModel;
	private int selectionMode=ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE;
	private JComponent content;
	private JXTable grid;
	private JTextField inputField; 
	private Action loadAction;
	
	
	public static final String[] properties={"clave","descripcion1","gramos","kilos","familia","famDesc"};
	public static final String[] columnNames={"Clave","Descripción","Gramos","Kilos","Familia","Fam Descripción"};

	public JComponent getControl() {
		if(content==null){
			content=new JPanel(new BorderLayout(2,5));
			
			grid=BrowserUtils.buildBrowserGrid();
			initGlazedList();
			final JPanel gridPanel=new JPanel(new BorderLayout(0,5));		
			gridPanel.add(UIFactory.createTablePanel(grid),BorderLayout.CENTER);
			gridPanel.setPreferredSize(Resizer.FIVE2FOUR.fromWidth(700));			
			
			content.add(BrowserUtils.buildTextFilterPanel(getInputField()),BorderLayout.NORTH);
			content.add(gridPanel,BorderLayout.CENTER);				
		}
		return content;
	}
	
	@SuppressWarnings("unchecked")
	private void initGlazedList(){
		//final EventList<ArticuloRow> threadSageList=GlazedLists.threadSafeList(new BasicEventList<ArticuloRow>());
		//articulos=GlazedListsSwing.swingThreadProxyList(threadSageList);
		articulos=GlazedLists.threadSafeList(new BasicEventList<ArticuloRow>());
		
		//Sorted
		final SortedList<ArticuloRow> sortedArticulos=new SortedList<ArticuloRow>(articulos,null);
		
		//TextFilter
		final TextFilterator<ArticuloRow> textFilterator=GlazedLists.toStringTextFilterator();
		final TextComponentMatcherEditor<ArticuloRow> editor=new TextComponentMatcherEditor<ArticuloRow>(getInputField(),textFilterator);
		final FilterList<ArticuloRow> filterList=new FilterList<ArticuloRow>(sortedArticulos,editor);
		
		//TableModel
		final TableFormat<ArticuloRow> tformat=GlazedLists.tableFormat(properties, columnNames);
		final EventTableModel<ArticuloRow> tm=new EventTableModel<ArticuloRow>(filterList,tformat);
		grid.setModel(tm);
		
		//Selection Model
		selectionModel=new EventSelectionModel<ArticuloRow>(filterList);
		grid.setSelectionModel(selectionModel);
		grid.packAll();
		//new TableComparatorChooser(table,sortedList,true);
		
		
		
	}

	public JTextField getInputField(){
		if(inputField==null){
			inputField=new JTextField(10);	
			ComponentUtils.addAction(inputField, getLoadAction(), KeyStroke.getKeyStroke("F5"));
		}		
		return inputField;		
	}
	
	private Action getLoadAction(){
		if(loadAction==null){
			loadAction=CommandUtils.createLoadAction(this, "load");
		}
		return loadAction;
	}

	public EventList<ArticuloRow> getArticulos() {		
		return articulos;
	}	

	public JXTable getGrid() {
		return grid;
	}
	
	public List<ArticuloRow> getData() {
		if(getManager()!=null){
			return getManager().browse();
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Imposible localizar el manager para este selector...");
			}
			return null;
		}
	}
	
	public void load(){
		SwingWorker<List<ArticuloRow> ,String> worker=new SwingWorker<List<ArticuloRow>, String>(){

			@Override
			protected List<ArticuloRow> doInBackground() throws Exception {
				List<ArticuloRow> data=getData();
				return data!=null?data:new ArrayList<ArticuloRow>();
			}

			@Override
			protected void done() {
				try {					
					getArticulos().getReadWriteLock().writeLock().lock();
					getArticulos().clear();
					getArticulos().addAll(get());
					getInputField().transferFocus();
					
					getArticulos().addAll(get());
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					getArticulos().getReadWriteLock().writeLock().unlock();
				}
			}			
		};		
		JDialog dialog=new JDialog();
		dialog.getContentPane().add(new HeaderPanel("Trabajando ...","Cargando información de la base de datos"));
		dialog.setModal(true);
		dialog.pack();
		ScreenUtils.locateOnScreenCenter(dialog);
		SwingWorkerWaiter2 waiter=new SwingWorkerWaiter2(dialog);
		worker.addPropertyChangeListener(waiter);
		
		worker.execute();
	}

	public int getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloRow> getSelected(){
		List selected=new ArrayList<ArticuloRow>();
		if(!selectionModel.isSelectionEmpty())
			selected.addAll(selectionModel.getSelected());
		return selected;
	}
	
	public ArticuloDao getManager() {
		if(manager==null){
			//Tratar de localizar el manager en el Spring ApplicationContext
			if(Application.isLoaded()){
				if(Application.instance().getApplicationContext().containsBean("articuloDao")){
					manager=(ArticuloDao)Application.instance().getApplicationContext().getBean("articuloDao");					
				}
			}
		}
		return manager;
	}

	public void setManager(ArticuloDao manager) {
		this.manager = manager;
	}	
	
	

}
