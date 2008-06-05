package com.luxsoft.siipap.cxc.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXTable;
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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.utils.SQLUtils;

/**
 * Consulta para la isnpeccion de provisiones
 * 
 * @author Ruben Cancino
 *
 */
public class CProvision extends AbstractView implements Consulta{
	
	private JTextField clave;
	private JTextField nombre;
	private JTextField sucursal;
	private Action loadAction;
	private EventList source=new BasicEventList();
	private EventList eventList;
	private JXTable grid;
	
	private JLabel total;
	private JLabel provision;
	
	
	private void initComponents(){
		total=new JLabel();
		total.setHorizontalAlignment(SwingConstants.RIGHT);
		provision=new JLabel();
		provision.setHorizontalAlignment(SwingConstants.RIGHT);
		
		clave=new JTextField();
		nombre=new JTextField();
		sucursal=new JTextField();
		grid=new JXTable();
		grid.setColumnControlVisible(true);
		grid.setHorizontalScrollEnabled(true);
		grid.setSortable(false);
		grid.setRolloverEnabled(false);
		Highlighter l=HighlighterFactory.createAlternateStriping();
		grid.setHighlighters(new Highlighter[]{l});
		grid.setRolloverEnabled(true);
		ComponentUtils.decorateActions(grid);
		
		loadAction=CommandUtils.createLoadAction(this, "load");
		
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		FormLayout layout=new FormLayout("p:g","p,2dlu,p");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(buildFilterPanel(),cc.xy(1, 1));
		builder.add(buildGridPanel(),cc.xy(1,3));
		return builder.getPanel();
	}
	
	private JComponent buildFilterPanel(){
		
		final PanelBuilder pb=new PanelBuilder(new FormLayout("p:g(.3),p:g(.7)","f:max(p;100dlu):g"));
		CellConstraints cc=new CellConstraints();
		
		FormLayout layout=new FormLayout(
				"l:40dlu,3dlu,max(p;90dlu) "				
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);	
		
		//builder.nextLine();
		builder.append("Cliente",clave,true);
		builder.append("Nombre",nombre,true);
		builder.append("Sucursal",sucursal,true);
		
		Border b1=Borders.DLU4_BORDER;
		Border b2=BorderFactory.createTitledBorder("Filtros");
		Border bb=BorderFactory.createCompoundBorder(b2,b1);
		builder.getPanel().setBorder(bb);
		
		pb.add(builder.getPanel(),cc.xy(1, 1));
		pb.add(buildTotalesPanel(),cc.xy(2, 1));
		
		return pb.getPanel();
	}
	
	private JComponent buildTotalesPanel(){
		
		FormLayout layout=new FormLayout(
				"l:40dlu,3dlu,max(p;90dlu) "				
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();		
		
		Border b1=Borders.DLU4_BORDER;
		Border b2=BorderFactory.createTitledBorder("Totales");
		builder.append("Venta ",total,true);
		builder.append("Provisión",provision);
		Border bb=BorderFactory.createCompoundBorder(b2,b1);
		builder.getPanel().setBorder(bb);
		
		return builder.getPanel();
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	private JComponent buildGridPanel(){
		
		JPanel p=new JPanel(new BorderLayout());
		ToolBarBuilder builder=new ToolBarBuilder();
		builder.add(loadAction);
		p.add(builder.getToolBar(),BorderLayout.NORTH);
		
		source=createEventList();		
		EventTableModel tm=new EventTableModel(source,new TF());
		
		grid.setModel(tm);
		grid.packAll();
		
		JComponent c=UIFactory.createTablePanel(grid);
		c.setPreferredSize(new Dimension(500,400));
		p.add(c,BorderLayout.CENTER);
		
		return p;
	}
	
	@SuppressWarnings("unchecked")
	private EventList createEventList(){
		
		final SortedList sortedList=new SortedList(source,null);
		
		final TextFilterator<Map<String, Object>> claveFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("CLAVE").toString();
				baseList.add(s);
			}			
		};
		final TextComponentMatcherEditor claveEditor=new TextComponentMatcherEditor(clave,claveFilterator);
		final FilterList claveList=new FilterList(sortedList,claveEditor);
		
		final TextFilterator<Map<String, Object>> nombreFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("NOMBRE").toString();
				baseList.add(s);
			}			
		};
		final TextComponentMatcherEditor nombreEditor=new TextComponentMatcherEditor(nombre,nombreFilterator);
		final FilterList nombreList=new FilterList(claveList,nombreEditor);
		
		final TextFilterator<Map<String, Object>> sucursalFilterator=new TextFilterator<Map<String,Object>>(){
			
			public void getFilterStrings(List<String> baseList, Map<String, Object> element) {
				String s=element.get("SUCURSAL").toString();
				baseList.add(s);
			}			
		};
		final TextComponentMatcherEditor sucursalEditor=new TextComponentMatcherEditor(sucursal,sucursalFilterator);
		final FilterList sucursalList=new FilterList(nombreList,sucursalEditor);
		this.eventList=sucursalList;
		return sucursalList;
	}
	
	@SuppressWarnings("unchecked")
	private void updateTotales(){
		BigDecimal importe=BigDecimal.ZERO;
		BigDecimal prov=BigDecimal.ZERO;
		for(Object o:eventList){
			Map<String, Object> row=(Map<String, Object>)o;
			BigDecimal dd=(BigDecimal)row.get("IMPORTE");
			if(dd!=null)
				importe=importe.add(dd);
			BigDecimal d1=(BigDecimal)row.get("PROVISION");
			if(d1!=null)
				prov=prov.add(d1);
		}
		
		total.setText(NumberFormat.getCurrencyInstance().format(importe.doubleValue()));
		provision.setText(NumberFormat.getCurrencyInstance().format(prov.doubleValue()));
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
	
	private List loadData(){
		String sql=SQLUtils.loadSQLQueryFromResource("sql/CProvision1.sql");
		return ServiceLocator.getJdbcTemplate().queryForList(sql);
	}
	
	
	public void close(){
		
	}
	
	
	private class TF implements TableFormat<Map<String, Object>>{
		
		String cols[]={"PROVISION_ID","CLAVE","NOMBRE","SUCURSAL","NUMERO","PERIODO","FECHA","VENCIMIENTO","DIASATRASO"
				,"IMPORTE","DESCUENTO1","DESCUENTO2","CARGOCALCULADO","PORCENTAJE","PROVISION"};

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
	
	
	public static void main(String[] args) {
		
		SWExtUIManager.setup();
		final CProvision p=new CProvision();
		SXAbstractDialog dialog=new SXAbstractDialog("Prueba"){

			@Override
			protected JComponent buildContent() {
				return p.getContent();
			}

			@Override
			protected void setResizable() {
				setResizable(true);
			}			
			
			
		};
		
		dialog.open();
		
		//String sql=SQLUtils.loadSQLQueryFromResource("sql/CProvision1.sql");
		//System.out.println(sql);
		//SQLUtils.printColumnNames(sql);
	}

}
