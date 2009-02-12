package com.luxsoft.siipap.cxc.consultas;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jdesktop.swingx.JXTable;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;

/**
 * 
 * Este control es basicamente un panel con un {@link JXTable} respaldado por un {@link EventTableModel}
 * de GlazedList para presentar instancias de beans.
 * 
 * @author Ruben Cancino
 *
 */
public abstract class BrowserPanel<E> extends AbstractControl{
	
	protected EventList<E> source;
	protected SortedList<E> sortedSource;
	protected EventSelectionModel<E> selectionModel;
	protected EventList<MatcherEditor<E>> matcherEditors=new BasicEventList<MatcherEditor<E>>();
	
	protected JXTable grid;
	
	protected JPanel filterPanel;
	protected JTextField filterField1;
	protected Document filterDocument1=new PlainDocument();
	private Comparator<E> defaultComparator;
	 
	
	/**
	 * Inicializacion de GlazedList
	 * 
	 */
	protected void initGlazedList(){
		if(source==null)
			source=GlazedLists.threadSafeList(getSourceEventList());
		sortedSource=new SortedList<E>(getFilteredList(source),getDefaultComparator());
		selectionModel=new EventSelectionModel<E>(sortedSource);
	}
	
	protected EventList<E> getSourceEventList(){
		return new BasicEventList<E>();
	}
	
	/**
	 * Regresa una lista filtrada mediante un {@link CompositeMatcherEditor}
	 * 
	 * @param list
	 * @return
	 */
	protected EventList<E> getFilteredList(final EventList<E> list){
		
		final CompositeMatcherEditor<E> editor=new CompositeMatcherEditor<E>(matcherEditors);
		installEditors(matcherEditors);
		final FilterList<E> filterList=new FilterList<E>(list,new ThreadedMatcherEditor<E>(editor));
		return filterList;
	}
	
	/**
	 * Template method para instalar {@link MatcherEditor}'s para la lista filtrada
	 * Por default el unico editor instalado es uno que permite buscar con el metodo toString
	 * del bean
	 * 
	 * @param editors
	 */
	@SuppressWarnings("unchecked")
	protected void installEditors(final EventList<MatcherEditor<E>> editors){		
		final TextFilterator<E> filterator=GlazedLists.toStringTextFilterator();
		TextComponentMatcherEditor<E> editor=new TextComponentMatcherEditor<E>(filterDocument1,filterator);
		editors.add(editor);
	}
	
	@SuppressWarnings("unchecked")
	protected JXTable buildGrid(){
		initGlazedList();
		grid=ComponentUtils.getStandardTable();
		EventTableModel<E> tm=new EventTableModel<E>(sortedSource,buildTableFormat());
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addEnterAction(grid, getSelectAction());
		ComponentUtils.addF2Action(grid, getLoadAction());
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
					select();
			}			
		});
		//chooser=new TableComparatorChooser(grid,sortedSource,true);
		installChooser(grid, sortedSource);
		adjustMainGrid(grid);
		return grid;		
	}
	
	protected TableComparatorChooser chooser;
	
	protected void installChooser(JXTable grid,SortedList sortedSource){
		chooser=new TableComparatorChooser(grid,sortedSource,true);
	}
	
	
	protected JScrollPane buildGridPanel(final JXTable grid){
		
		//final JScrollPane sp=new JScrollPane(grid);
		//return sp;
		return UIFactory.createStrippedScrollPane(grid);
	}
	
	/**
	 * Template method para ajustar las columnas del grid
	 * 
	 * @param grid
	 */
	protected void adjustMainGrid(final JXTable grid){		
	}	
	
	@Override
	protected JComponent buildContent() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildGridPanel(buildGrid()),BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Metodo detonado cuando se selecciona un registro en el grid
	 * @see doSelect(E bean)
	 */
	public void select(){
		E selected=getSelectedObject();
		if(selected!=null)
			doSelect(selected);
	}
	
	/**
	 * Template method para facilitar el comportamiento cuando el usuario da doble click o enter en algun
	 * registro del grid. Este metodo es adecuado para ser implementado por las subclases 
	 * 
	 * @param bean
	 */
	protected void doSelect(E bean){
		if(logger.isDebugEnabled()){
			logger.debug("Seleccion; "+bean);
		}
	}
	
	public boolean isSelectionEmpty(){
		return selectionModel.isSelectionEmpty();
	}
	
	protected EventList<E> getSelected(){
		return selectionModel.getSelected();
	}
	
	protected E getSelectedObject(){
		if(!selectionModel.isSelectionEmpty()){
			return getSelected().get(0);
		}
		return null;
	}	
	
	public EventList<E> getSource() {
		return source;
	}
	
	public EventList<E> getFilteredSource(){
		return sortedSource;
	}

	public void setSource(EventList<E> source) {
		Assert.isNull(this.source,"La Lista fuente ya fue asignada, es inmutable");
		this.source = source;
	}

	/*
	protected JPanel getFilterPanel(){		
		if(filterPanel==null){
			filterPanel=buildFilterPanel();
		}
		return filterPanel;
	}
	*/
	/**
	 * La implementacion por default regresa un input field con un filterato
	 * que utiliza el metodo toString de los beans en el grid
	 * para realizar la busqueda
	 *  
	 * @return
	 */
	protected JPanel buildFilterPanel(){
		filterField1=new JTextField();
		filterField1.setDocument(filterDocument1);
		return (JPanel)ComponentUtils.buildTextFilterPanel(filterField1);
		
	}
	
	protected abstract TableFormat<E> buildTableFormat();
	
	
	protected Action selectAction;
	protected Action loadAction;
	
	//** Acciones **/
	
	public Action getSelectAction(){
		return selectAction;
	}
	public Action getLoadAction(){
		if(loadAction==null)
			loadAction=CommandUtils.createLoadAction(this, "load");
		return loadAction;
	}
	
	protected Action[] actions;
	
	public Action[] getActions(){
		if(actions==null)
			actions=new Action[]{getLoadAction()};
		return actions;
	}

	public void setActions(Action[] actions) {
		this.actions = actions;
	}
	
	/**
	 * Otra forma de asignar acciones
	 * 
	 * @param action
	 */
	public void addActions(Action...action){
		this.actions=action;
	}
	
	public void load(){
		SwingWorker<List<E>, String> worker=new SwingWorker<List<E>, String>(){
			
			protected List<E> doInBackground() throws Exception {
				publish("Cargando datos....");
				return findData();
			}
			protected void done(){
				
				List<E> data;
				try {
					data = get();
					dataLoaded(data);
				} catch (InterruptedException e) {
					e.printStackTrace();
					MessageUtils.showError("Error", e);
				} catch (ExecutionException e) {
					e.printStackTrace();
					MessageUtils.showError("Error", e);
					
				}
			}
			
			
		};
		executeLoadWorker(worker);
	}
	
	
	
	protected void executeLoadWorker(final SwingWorker worker){
		worker.execute();
		//TaskUtils.executeSwingWorker(worker);
	}
	
	/**
	 * Called safetly from the Load's method SwingWorker
	 * It is executed in the EDT
	 * 
	 * @param data
	 */
	protected void dataLoaded(final List<E> data){		
		source.clear();
		source.addAll(data);
		grid.packAll();
		afterLoad();
	}
	
	/**
	 * Template method para hacer algo en el EDT despues de cargar los datos
	 * 
	 */
	protected void afterLoad(){
		
	}
	
	protected List<E> findData(){
		return new ArrayList<E>();
	}
	
	public JXTable getGrid(){
		return grid;
	}

	public Comparator<E> getDefaultComparator() {
		return defaultComparator;
	}

	public void setDefaultComparator(Comparator<E> defaultComparator) {
		this.defaultComparator = defaultComparator;
	}
	

	protected void configAction(final Action a,String id,String label){
		CommandUtils.configAction(a, id, null);
		a.putValue(Action.NAME, label);
	}
	
	protected void configAction(final Action a,String id){
		CommandUtils.configAction(a, id, null);
	}

}
