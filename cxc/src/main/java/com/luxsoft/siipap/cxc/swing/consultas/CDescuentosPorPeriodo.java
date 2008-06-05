package com.luxsoft.siipap.cxc.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
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


public class CDescuentosPorPeriodo extends AbstractView{
	private JTextField periodo;
	private JTextField sucursal;
	private JTextField contado;
	private JLabel total;
	private JLabel provision;
	private Action loadAction;
	private JXTable grid;
	private EventList source=new BasicEventList();
	private EventList eventList;
	JXTaskPane t;
	protected JComponent buildContent() {
	initComponents();
	JPanel content=new JPanel(new BorderLayout());
	content.add(new HeaderPanel("Descuentos Por Periodo","Consulta Descuentos Por Periodo"),BorderLayout.NORTH);
	content.add(createBrowser(),BorderLayout.CENTER);

	return content;
	}
	
	private JComponent createBrowser(){
		JPanel p=new JPanel(new BorderLayout(10,10));
		ToolBarBuilder tb=new ToolBarBuilder();
		tb.add(CommandUtils.createRefreshAction(this, "load"));
		p.add(tb.getToolBar(),BorderLayout.NORTH);
		p.add(buildGridPanel(),BorderLayout.CENTER);
		return p;
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
		periodo=new JTextField();
		sucursal=new JTextField();
		contado=new JTextField();
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
		p.add(buildFilPanel(),BorderLayout.NORTH);
		p.add(buildtotalesPanel(),BorderLayout.SOUTH);
		return p;
	}@SuppressWarnings({ "unchecked"})
	
	 private JComponent buildFilPanel(){
		FormLayout layout=new FormLayout(
				"30dlu,4dlu,50dlu,4dlu,50dlu,4dlu,30dlu,4dlu,50dlu,"+
				"50dlu,4dlu,10dlu,4dlu,30dlu,4dlu,50dlu,4dlu,50dlu",
				"p"
				);
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.setBorder(new TitledBorder("Filtros"));
		builder.add(new JLabel("Periodo"),cc.xy(1, 1));
		builder.add(new JLabel("Sucursal"),cc.xy(7, 1));
		builder.add(new JLabel("Contado"),cc.xyw(13, 1, 2));
		builder.add(periodo,cc.xyw(2, 1, 4));
		builder.add(sucursal,cc.xyw(8, 1, 4));
		builder.add(contado,cc.xyw(15, 1, 4));
		return builder.getPanel();
	}
	
	private JComponent buildtotalesPanel(){
		FormLayout layout=new FormLayout(
				"50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu",
				"p"
				);
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.setBorder(new TitledBorder("Totales"));
		builder.add(new JLabel("Totales"),cc.xyw(1, 1, 2));
		builder.add(new JLabel("Provision"),cc.xyw(6, 1, 2));
		builder.add(total,cc.xyw(2, 1, 3));
		builder.add(provision,cc.xyw(8, 1, 2));
		return builder.getPanel();
	}
	
	@SuppressWarnings({ "unchecked"})
	private EventList createEventList(){
		
		final SortedList sortedList=new SortedList(source,null);
		
		final TextFilterator<Map<String, Object>> periodoFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("PERIODO").toString();
				baseList.add(s);
			}			
		};
		final TextComponentMatcherEditor periodoEditor=new TextComponentMatcherEditor(periodo,periodoFilterator);
		final FilterList periodoList=new FilterList(sortedList,periodoEditor);
		
		final TextFilterator<Map<String, Object>> sucFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("SUCURSAL").toString();
				baseList.add(s);
			}			
		};@SuppressWarnings({ "unchecked"})
		final TextComponentMatcherEditor nombreEditor=new TextComponentMatcherEditor(sucursal,sucFilterator);
		final FilterList sucList=new FilterList(periodoList,nombreEditor);
		
		final TextFilterator<Map<String, Object>> contadoFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("CONTADO").toString();
				baseList.add(s);
			}			
		};
		final TextComponentMatcherEditor contadoEditor=new TextComponentMatcherEditor(contado,contadoFilterator);
		final FilterList contadoList=new FilterList(sucList,contadoEditor);
		this.eventList=contadoList;
		return contadoList;
	}
	
	private class TF implements TableFormat<Map<String, Object>>{
		
		String cols[]={"PERIODO","SUCURSAL","CONTADO","CREDITO","IMP_P_LIST","IMP_FACT"
				,"IMP_CON_DESCTO","DESCTO"};

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
		String sql=SQLUtils.loadSQLQueryFromResource("sql/CDescuentosPorPeriodoConcentrado.sql");
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
		final CDescuentosPorPeriodo e=new CDescuentosPorPeriodo();
		SXAbstractDialog dialog=new SXAbstractDialog("DESCUENTO POR PERIODO"){

			protected JComponent buildContent() {
				
				return e.getContent(); 
			}
		};
			dialog.open();
		
	}

}



