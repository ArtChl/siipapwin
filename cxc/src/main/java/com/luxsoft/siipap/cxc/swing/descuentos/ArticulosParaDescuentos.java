package com.luxsoft.siipap.cxc.swing.descuentos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.ThresholdList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Selector de articulos para asignar descuentos 
 * 
 * @author Ruben Cancino
 *
 */
public class ArticulosParaDescuentos extends SXAbstractDialog{
	 
	
	private JTextField lineaField;
	private JTextField claveField;
	private JTextField descField;
	private JTextField claseField;
	private JTextField marcaField;
	private MinEditor minEditor;
	private MaxEditor maxEditor;
	
	

	private JXTable grid;
	
	private EventList<Articulo> source=new BasicEventList<Articulo>();
	private final String[] props={"clave","descripcion1","lineaClave","kilos","gramos","marcaClave","claseClave"};
	private final String[] labels={"Clave","Descripción","Línea","Kilos","Gramos","Marca","Clase"};
	private TableFormat<Articulo> tableFormat=GlazedLists.tableFormat(props, labels);
	private EventSelectionModel<Articulo> selectionModel;
	
	public ArticulosParaDescuentos() {
		super("Articulos disponibles para descuento");		
	}

	

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout(5,5));
		panel.add(buildFilterPanel(),BorderLayout.NORTH);
		panel.add(buildGridPanel(),BorderLayout.CENTER);		
		return panel;
	}
	
	private JComponent buildFilterPanel(){
		lineaField=new JTextField();		
		claveField=new JTextField();		
		descField=new JTextField();
		claseField=new JTextField();
		marcaField=new JTextField();
		minEditor=new MinEditor();
		maxEditor=new MaxEditor();
		FormLayout layout=new FormLayout(
				"l:p,2dlu,60dlu,3dlu," +
				"l:p,2dlu,60dlu,3dlu," +
				"l:p,2dlu,60dlu,3dlu," +
				"l:p,2dlu,60dlu,3dlu," +
				"l:p,2dlu,60dlu:g"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Línea",lineaField);
		builder.append("Clase",claseField);
		builder.append("Marca",marcaField);
		builder.append("Gramos (min)",minEditor.getInputField());
		builder.append("Gramos (max)",maxEditor.getInputField(),true);
		
		builder.append("Clave",claveField);		
		builder.append("Descripción",descField,13);
		
		return builder.getPanel();
	}
	
	
	
	
	
	private JComponent buildGridPanel(){
		grid=createGrid();		
		final SortedList<Articulo> sortedList=new SortedList<Articulo>(source,null);
		
		//Primer filtro por linea
		final TextFilterator<Articulo> lineaFilterator=GlazedLists.textFilterator(new String[]{"lineaClave"});
		final TextComponentMatcherEditor<Articulo> lineaEditor=new TextComponentMatcherEditor<Articulo>(lineaField,lineaFilterator);
		final FilterList<Articulo> lineaList=new FilterList<Articulo>(sortedList,lineaEditor);
		
		//Primer filtro por clase
		final TextFilterator<Articulo> claseFilterator=GlazedLists.textFilterator(new String[]{"claseClave"});
		final TextComponentMatcherEditor<Articulo> claseEditor=new TextComponentMatcherEditor<Articulo>(claseField,claseFilterator);
		final FilterList<Articulo> claseList=new FilterList<Articulo>(lineaList,claseEditor);

		//Primer filtro por marca
		final TextFilterator<Articulo> marcaFilterator=GlazedLists.textFilterator(new String[]{"marcaClave"});
		final TextComponentMatcherEditor<Articulo> marcaEditor=new TextComponentMatcherEditor<Articulo>(marcaField,marcaFilterator);
		final FilterList<Articulo> marcaList=new FilterList<Articulo>(claseList,marcaEditor);

		//Primer filtro por gramos min
		//final ThresholdList<Articulo> tl=new ThresholdList<Articulo>()
		
		final FilterList<Articulo> gramMinList=new FilterList<Articulo>(marcaList,minEditor);
		final FilterList<Articulo> gramMaxList=new FilterList<Articulo>(gramMinList,maxEditor);
		
		
		
		//Segundo fitro por clave
		final TextFilterator<Articulo> claveFilterator=GlazedLists.textFilterator(new String[]{"clave"});
		final TextComponentMatcherEditor<Articulo> claveEditor=new TextComponentMatcherEditor<Articulo>(claveField,claveFilterator);
		final FilterList<Articulo> claveList=new FilterList<Articulo>(gramMaxList,claveEditor);
		
		//Tercer fitro por descripcion1
		final TextFilterator<Articulo> descFilterator=GlazedLists.textFilterator(new String[]{"descripcion1"});
		final TextComponentMatcherEditor<Articulo> descEditor=new TextComponentMatcherEditor<Articulo>(descField,descFilterator);
		final FilterList<Articulo> descList=new FilterList<Articulo>(claveList,descEditor);
		
		
		final EventTableModel<Articulo> tm=new EventTableModel<Articulo>(descList,tableFormat);
		grid.setModel(tm);
		
		selectionModel=new EventSelectionModel<Articulo>(descList);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser<Articulo>(grid,sortedList,true);
		
		JComponent c=UIFactory.createTablePanel(grid);
		c.setPreferredSize(new Dimension(600,400));
		return c;
	}
	
	/**
	 * Factory method para crear un grid elegante usando JXTable
	 * 
	 * @return
	 */
	private JXTable createGrid(){
		JXTable grid=new JXTable();
		grid.setColumnControlVisible(true);
		grid.setHorizontalScrollEnabled(true);
		grid.setSortable(false);						
		grid.setRolloverEnabled(false);
		Highlighter l=HighlighterFactory.createAlternateStriping();
		grid.setHighlighters(new Highlighter[]{l});
		grid.setRolloverEnabled(true);
		decorateActions(grid);
		return grid;
	}
	
	public EventList<Articulo> getSelected(){
		return selectionModel.getSelected();
	}
	
	public void select(){
		doAccept();
	}
	
	
	
	@Override
	protected void setResizable() {		
		super.setResizable(true);
	}



	public void decorateActions(final JTable table){
		final Action enterAction=new AbstractAction("enter"){
			public void actionPerformed(ActionEvent e) {
				select();
			}			
		};
		ComponentUtils.addEnterAction(table, enterAction);
		
		table.addMouseListener(new MouseAdapter(){			
			public void mousePressed(MouseEvent e) {				
				if(e.getClickCount()==2)
					select();
			}			
		});
		
		ComponentUtils.decorateActions(table);
	}
	
	public void loadData(final List<Articulo> articulos){
		source.getReadWriteLock().writeLock().lock();
		try {
			source.clear();
			source.addAll(articulos);			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			source.getReadWriteLock().writeLock().unlock();
			
		}
	}
	
	public  static class MinEditor extends AbstractMatcherEditor<Articulo> implements PropertyChangeListener{
		
		private JFormattedTextField inputField;
		
		public MinEditor(){
			NumberFormatter formatter=new NumberFormatter(NumberFormat.getIntegerInstance());
			formatter.setValueClass(Integer.class);
			formatter.setCommitsOnValidEdit(true);
			inputField=new JFormattedTextField(formatter);
			inputField.addPropertyChangeListener("value", this);
		}		
		
		public void propertyChange(PropertyChangeEvent evt) {
			Integer val=(Integer)evt.getNewValue();			
			fireChanged(new MinMatcher(val));
		}

		public JFormattedTextField getInputField(){
			return inputField;
		}
		
		private class MinMatcher implements Matcher<Articulo>{
			
			private final Integer gramos;
			
			public MinMatcher(Integer gramos){
				this.gramos=gramos;
			}

			public boolean matches(Articulo item) {
				if(gramos==null)return true;
				if(gramos==0) return true;
				return item.getGramos()>=gramos;
			}			
		}
		
	}
	
	public static class MaxEditor extends AbstractMatcherEditor<Articulo> implements PropertyChangeListener{
		
		private JFormattedTextField inputField;
		
		public MaxEditor(){
			NumberFormatter formatter=new NumberFormatter(NumberFormat.getIntegerInstance());
			formatter.setValueClass(Integer.class);
			formatter.setCommitsOnValidEdit(true);
			inputField=new JFormattedTextField(formatter);
			inputField.addPropertyChangeListener("value", this);
		}		
		
		public void propertyChange(PropertyChangeEvent evt) {
			Integer val=(Integer)evt.getNewValue();
			System.out.println("Nuevo valor: "+val);
			fireChanged(new MaxMatcher(val));
		}

		public JFormattedTextField getInputField(){
			return inputField;
		}
		
		private class MaxMatcher implements Matcher<Articulo>{
			
			private final Integer gramos;
			
			public MaxMatcher(Integer gramos){
				this.gramos=gramos;
			}

			public boolean matches(Articulo item) {
				if(gramos==null)return true;
				if(gramos==0) return true;
				return item.getGramos()<=gramos;
			}			
		}
		
	}
	
	
	public static void main(String[] args) {
		
		ArticuloDao dao=(ArticuloDao)ServiceLocator.getDaoContext().getBean("articuloDao");
		ArticulosParaDescuentos dialog=new ArticulosParaDescuentos();
		dialog.setResizable(true);
		dialog.loadData(dao.buscarTodos());
		dialog.open();
	}
	

}
