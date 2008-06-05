package com.luxsoft.siipap.cxc.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.springframework.util.StringUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.utils.SQLUtils;

public class CDescuento extends AbstractView{
	
	private JTextField filtro1;
	private JTextField filtro2;
	private JTextField filtro3;
	private JLabel total;
	private JLabel provision;
	private JTabbedPane tabbedPestaña;
	private Action loadAction;
	private JXTable grid;
	private EventList source=new BasicEventList();
	private EventList eventList;
	JXTaskPane t;
	protected JComponent buildContent() {
	initComponents();
	JPanel content=new JPanel(new BorderLayout());
	content.add(new HeaderPanel("prueba","prueba"),BorderLayout.NORTH);
	content.add(createBrowser(),BorderLayout.CENTER);
	
	
		
	return content;
	}
	
	private JComponent createBrowser(){
		tabbedPestaña=new JTabbedPane();
		JPanel p=new JPanel(new BorderLayout(10,10));
		ToolBarBuilder tb=new ToolBarBuilder();
		tb.add(CommandUtils.createRefreshAction(this, "load"));
		p.add(tb.getToolBar(),BorderLayout.NORTH);
		p.add(buildGridPanel(),BorderLayout.CENTER);
		tabbedPestaña.addTab("Saldos", p);
		
		return tabbedPestaña;
	}
	
	@SuppressWarnings({ "unchecked"})
	private JComponent buildGridPanel(){
	JPanel p=new JPanel(new BorderLayout());
	ToolBarBuilder builder=new ToolBarBuilder();
	builder.add(loadAction);
	p.add(buildFilros(),BorderLayout.NORTH);
	grid.packAll();
	JComponent c=UIFactory.createTablePanel(grid);
	c.setPreferredSize(new Dimension(700,400));
	p.add(c,BorderLayout.CENTER);
	source=createEventList();		
	EventTableModel tm=new EventTableModel(source,new TF());
	
	grid.setModel(tm);
	
	JComponent comp=UIFactory.createTablePanel(grid);
	comp.setPreferredSize(new Dimension(500,400));
	p.add(comp,BorderLayout.CENTER);
		return p;
	}
	
	private void initComponents(){
		filtro1=new JTextField();
		filtro2=new JTextField();
		filtro3=new JTextField();
		total=new JLabel();
		provision=new JLabel();
		grid=new JXTable();
		grid.setColumnControlVisible(true);
		grid.setHorizontalScrollEnabled(true);
		grid.setSortable(false);
		//grid.packAll();				
		grid.setRolloverEnabled(false);
		Highlighter l=HighlighterFactory.createAlternateStriping();
		grid.setHighlighters(new Highlighter[]{l});		
		grid.setRolloverEnabled(true);
		ComponentUtils.decorateActions(grid);
		loadAction=CommandUtils.createLoadAction(this,"load");
	}
	
	private JComponent buildFilros(){
		JPanel p=new JPanel(new BorderLayout());
		p.add(buildFilPanel(),BorderLayout.WEST);
		p.add(buildtotalesPanel(),BorderLayout.EAST);
		return p;
	}@SuppressWarnings({ "unchecked"})
	
	private JComponent buildFilPanel(){
		FormLayout layout=new FormLayout(
				"50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu",
				"p,20dlu,p,20dlu"
				);
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.setBorder(new TitledBorder("Filtros"));
		builder.add(new JLabel("Filtro1"),cc.xyw(1, 1, 2));
		builder.add(new JLabel("Filtro2"),cc.xyw(1, 2, 2));
		builder.add(new JLabel("Filtro3"),cc.xyw(1, 3, 2));
		builder.add(filtro1,cc.xyw(2, 1, 6));
		builder.add(filtro2,cc.xyw(2, 2, 6));
		builder.add(filtro3,cc.xyw(2, 3, 6));
		return builder.getPanel();
	}
	
	@SuppressWarnings({ "unchecked"})
	private JComponent buildtotalesPanel(){
		FormLayout layout=new FormLayout(
				"50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu",
				"p,20dlu,p,20dlu"
				);
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.setBorder(new TitledBorder("Totales"));
		builder.add(new JLabel("Totales"),cc.xyw(1, 1, 2));
		builder.add(new JLabel("Provision"),cc.xyw(1, 3, 2));
		builder.add(total,cc.xyw(2, 1, 2));
		builder.add(provision,cc.xyw(2, 3, 2));
		return builder.getPanel();
	}
	
	@SuppressWarnings({ "unchecked"})
	private EventList createEventList(){
		
		final SortedList sortedList=new SortedList(source,null);
		
		final TextFilterator<Map<String, Object>> claveFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("LINEA").toString();
				baseList.add(s);
			}			
		};
		final TextComponentMatcherEditor claveEditor=new TextComponentMatcherEditor(filtro1,claveFilterator);
		final FilterList claveList=new FilterList(sortedList,claveEditor);
		
		final TextFilterator<Map<String, Object>> nombreFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("CLAVE").toString();
				baseList.add(s);
			}			
		};@SuppressWarnings({ "unchecked"})
		final TextComponentMatcherEditor nombreEditor=new TextComponentMatcherEditor(filtro2,nombreFilterator);
		final FilterList nombreList=new FilterList(claveList,nombreEditor);
		
		final TextFilterator<Map<String, Object>> sucursalFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("NOMBRE").toString();
				baseList.add(s);
			}			
		};
		final TextComponentMatcherEditor sucursalEditor=new TextComponentMatcherEditor(filtro3,sucursalFilterator);
		final FilterList sucursalList=new FilterList(nombreList,sucursalEditor);
		this.eventList=sucursalList;
		return sucursalList;
	}
	
	private class TF implements TableFormat<Map<String, Object>>{
		
		String cols[]={"LINEA","CLAVE","NOMBRE","SERIE","SUCURSAL","PERIODO","TIPO_VENTA","IMP_P_LIST","IMPFAC"
				,"IMP_CON_DESCTO","DESCTO","DESCTO_P_NETO"};

		public int getColumnCount() {			
			return cols.length;
		}

		public String getColumnName(int column) {			
			return StringUtils.capitalize(cols[column].toLowerCase());
		}

		public Object getColumnValue(Map<String, Object> row, int column) {
			String key=cols[column];
			return row.get(key);
		}
		
	}
	
	private List loadData(){
		String sql=SQLUtils.loadSQLQueryFromResource("sql/CDescuento.sql");
		return ServiceLocator.getJdbcTemplate().queryForList(sql);
	}
	
	public  void load(){
		SwingWorker<List, String> worker=new SwingWorker<List, String>(){			
			protected List doInBackground() throws Exception {
				return loadData();
			}
			@SuppressWarnings("unchecked")
			protected void done() {
				source.getReadWriteLock().writeLock().lock();
				try {
					source.clear();
					source.addAll(get());
					grid.packAll();
					updateTotales();
				} catch (Exception e) {
					
				}finally{
					source.getReadWriteLock().writeLock().unlock();
				}
			}
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	@SuppressWarnings("unchecked")
	private void updateTotales(){
		BigDecimal importe=BigDecimal.ZERO;
		BigDecimal prov=BigDecimal.ZERO;
		for(Object o:eventList){
			Map<String, Object> row=(Map<String, Object>)o;
			BigDecimal dd=(BigDecimal)row.get("IMP_P_LIST");
			if(dd!=null)
				importe=importe.add(dd);
			BigDecimal d1=(BigDecimal)row.get("IMP_CON_DESCTO");
			if(d1!=null)
				prov=prov.add(d1);
		}
		
		total.setText(NumberFormat.getCurrencyInstance().format(importe.doubleValue()));
		provision.setText(NumberFormat.getCurrencyInstance().format(prov.doubleValue()));
	}
	
	@SuppressWarnings({"serial"})
	public static void main(String[] args) {
		SWExtUIManager.setup();
		final CDescuento e=new CDescuento();
		SXAbstractDialog dialog=new SXAbstractDialog("Prueba"){

			protected JComponent buildContent() {
				
				return e.getContent(); 
			}
		};
			dialog.open();
		
	}

}
