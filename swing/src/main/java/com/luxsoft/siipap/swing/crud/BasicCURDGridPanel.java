package com.luxsoft.siipap.swing.crud;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.swing.actions.CURD;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.utils.BrowserUtils;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;


public class BasicCURDGridPanel extends AbstractControl implements CURD{
	
	protected JTable drillGrid;
	protected JTextField inputField;
	protected final EventList source;
	protected final TableFormat tableFormat;
	protected TextFilterator textFilterator;
	protected EventSelectionModel selectinModel;
	private Comparator defaultComparator;
	
	private Action insertAction;
	private Action deleteAction;
	
	public BasicCURDGridPanel(final TableFormat tableFormat){
		this(new BasicEventList(),tableFormat);
	}
	public BasicCURDGridPanel(final EventList source,final TableFormat tableFormat){
		this.source=source;
		this.tableFormat=tableFormat;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected JComponent buildContent() {
		JPanel cpanel=new JPanel(new BorderLayout(5,5));
		
		final SortedList sortedList=new SortedList(source,getDefaultComparator());
		EventList targetList=sortedList;
		if(getTextFilterator()!=null){			
			final TextComponentMatcherEditor editor=new TextComponentMatcherEditor(getInputField(),getTextFilterator());
			final FilterList filterList=new FilterList(targetList,new ThreadedMatcherEditor(editor));
			targetList=filterList;
			cpanel.add(createTextFilteratorPanel(),BorderLayout.NORTH);
		}
		targetList=pipeLists(targetList);
		EventTableModel tm=new EventTableModel(targetList, tableFormat);
		//selectinModel=new EventSelectionModel(targetList);
		selectinModel=new EventSelectionModel(targetList);
		selectinModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		final JTable grid=getGrid();
		//drillGrid=grid;
		grid.setModel(tm);		
		grid.setSelectionModel(selectinModel);
		grid.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){					
					if(selectinModel.getSelected().isEmpty())
						return;
					select(selectinModel.getSelected());
				}
			}
		});
		ComponentUtils.addEnterAction(grid, new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				if(selectinModel.getSelected().isEmpty())
					return;
				select(selectinModel.getSelected());
			}			
		});
		grid.getActionMap().put("delete", getDeleteAction());
		grid.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "delete");
		ComponentUtils.addAction(grid,getInsertAction(), KeyStroke.getKeyStroke("INSERT"));
		new TableComparatorChooser(grid,sortedList,true);
		JComponent c=UIFactory.createTablePanel(grid);		
		cpanel.add(c,BorderLayout.CENTER);		
		installRenderers(grid);
		ComponentUtils.decorateActions(grid);
		return cpanel;		
	}
	
	protected JComponent createTextFilteratorPanel(){
		return BrowserUtils.buildTextFilterPanel(getInputField());
	}
	
	public JTextField getInputField(){
		if(inputField==null){
			inputField=new JTextField(20);
		}
		return inputField;
	}
	
	protected EventList pipeLists(final EventList source){
		return source;
	}

	public JTable getGrid() {
		if(drillGrid==null){
			drillGrid=ComponentUtils.getStandardTable();
			/**=new JXTable();
			JXTable grid=(JXTable)drillGrid;
			grid.setColumnControlVisible(true);
			grid.setHorizontalScrollEnabled(true);
			grid.setSortable(false);							
			grid.setRolloverEnabled(false);
			
			//Highlighter alternate=HighlighterFactory.createAlternateStriping();//new HighlighterPipeline();
			//Color cfg=UIManager.getColor("Table.selectionForeground");
			//Color cbg=UIManager.getColor("Table.selectionBackground");
			//RolloverHighlighter rh=new RolloverHighlighter(cbg,cfg);
			//pipe.addHighlighter(new AlternateRowHighlighter());
			//pipe.addHighlighter(rh);
			//grid.setHighlighters(new Highlighter[]{alternate});
			grid.setRolloverEnabled(true);
			**/
			
		}
		return drillGrid;
	}
	
	public void dispose(){
		
	}
	
	/**
	 * Template method para instalar custom renreres
	 * 
	 * @param table
	 */
	protected void installRenderers(final JTable table){
		
	}
	
	public EventSelectionModel getSelectinModel() {
		return selectinModel;
	}
	
	public EventList getSelected(){
		return getSelectinModel().getSelected();
	}
	
	protected void select(List selected){
		
	}
	
	public void pack(){
		if(getGrid()!=null)
			((JXTable)getGrid()).packAll();
	}
	

	public TextFilterator getTextFilterator() {
		return textFilterator;
	}
	public void setTextFilterator(TextFilterator textFilterator) {
		this.textFilterator = textFilterator;
	}
	public Comparator getDefaultComparator() {
		return defaultComparator;
	}
	public void setDefaultComparator(Comparator defaultComparator) {
		this.defaultComparator = defaultComparator;
	}
	public EventList getSource() {
		return source;
	}
	
	
	
	public Action getInsertAction(){
		if(insertAction==null){
			//insertAction=CommandUtils.createInsertAction(this, "insert");
			insertAction=new AbstractAction(){

				public void actionPerformed(ActionEvent e) {
					insert();
					
				}
				
			};
		}
		return insertAction;
	}
	
	
	
	public Action getDeleteAction(){
		if(deleteAction==null){
			deleteAction=CommandUtils.createDeleteAction(this, "delete");
		}
		return deleteAction;
	}
	
	/*** CURD Implementation ****/
	
	public void delete() {
		if(!getSelected().isEmpty()){
			delete(getSelected());
		}
	}
	public void edit(){
		if(!getSelected().isEmpty()){
			edit(getSelected());
		}
	}
	public void view(){
		if(!getSelected().isEmpty()){
			view(getSelected());
		}
	}
	
	/**
	 * Template method para facilitar la implementacion de delete
	 * 
	 * @param selected
	 */
	protected void delete(final EventList selected){
		
	}
	
	protected void edit(final EventList selected){
		
	}
	
	protected void view(final EventList selected){
		
	}
	
	public void insert() {
				
	}
	
	
	
	public void refresh() {
				
	}
	
	
	
}
