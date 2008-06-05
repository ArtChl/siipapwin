package com.luxsoft.siipap.cxc.selectores;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Selector para instancias de tipo {@link PagoM}
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public abstract class SelectorDePagoM extends SXAbstractDialog{
	
	private final EventList list;
	private FilterList filterList;
	private EventSelectionModel selectionModel;
	protected JXTable grid;
	private boolean filtros=false;
	private JTextField clienteField=new JTextField();

	public SelectorDePagoM(final EventList source) {
		super("Pagos aplicados");
		list=source;
	}

	
	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout(2,5));
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		if(isFiltros()){
			panel.add(buildFilterPanel(),BorderLayout.NORTH);
		}
		return panel;
	}
	
	public JComponent buildFilterPanel(){		
		FormLayout layout=new FormLayout(
				"3dlu,l:50dlu,p:g",
				"p");
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		JLabel l=DefaultComponentFactory.getInstance().createTitle("Cliente: ");
		builder.add(l,cc.xy(2,1));
		builder.add(clienteField,cc.xy(3,1));
		return builder.getPanel();
	}
	
	protected JComponent buildGridPanel(){
		
		final SortedList sorted;
		/*
		final Comparator<Map<String, Object>> c1=new Comparator<Map<String,Object>>(){
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {				
				return o1.get("CLAVE").toString().compareTo(o2.get("CLAVE").toString());
			}			
		};
		*/
		if(isFiltros()){
			final TextFilterator<Map<String, Object>> filterator1=new TextFilterator<Map<String,Object>>(){
				public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
					baseList.add(element.get("CLAVE").toString());
					baseList.add(element.get("NOMBRE").toString());
				}				
			};
			final TextComponentMatcherEditor editor1=new TextComponentMatcherEditor(clienteField,filterator1);
			filterList=new FilterList(list,editor1);
			sorted=new SortedList(filterList,null);
		}else		
			//sorted=new SortedList(list,c1);
			sorted=new SortedList(list,null);
		
		final EventTableModel tm=new EventTableModel(sorted,getTableFormat());
		selectionModel=new EventSelectionModel(sorted);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		final Action select=new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				doAccept();
			}
		};
		ComponentUtils.addEnterAction(grid, select);
		grid.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) doAccept();
			}			
		});
		grid.packAll();
		ComponentUtils.decorateActions(grid);
		decorateGrid(grid);
		return new JScrollPane(grid);
	}
	
	/**
	 * Template method para personalizar el grid 
	 * 
	 * @param grid
	 */
	protected void decorateGrid(final JXTable grid){
		
	}
	
	protected TableFormat getTableFormat(){
		return CXCTableFormats.getPagoMTF();
	}

	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel(getHeaderTitle(),getHeaderDesc());
	}
	
	public PagoM getSelected(){
		if(!selectionModel.getSelected().isEmpty()){
			return (PagoM)selectionModel.getSelected().get(0);
		}
		return null;
	}
	
	protected String getHeaderTitle(){
		return "Pagos con disponible (Otros productos)";
	}
	
	protected abstract String getHeaderDesc();


	public boolean isFiltros() {
		return filtros;
	}


	public void setFiltros(boolean filtros) {
		this.filtros = filtros;
	}
	
	

}
