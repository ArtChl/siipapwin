package com.luxsoft.siipap.cxc.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.springframework.util.StringUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
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

public class ClientesCreditoStView extends AbstractView {

	private JTextField status;
	private JTextField aviso;
	private JTextField nombre;
	private JLabel total;
	private JLabel provision;
	
	private Action loadAction;
	private JXTable grid;
	private JXDatePicker picker;
	JXTaskPane t;

	private EventList source = new BasicEventList();
	private SortedList sortedList;
	

	protected JComponent buildContent() {
		initComponents();
		initGlazedList();		
		JPanel content = new JPanel(new BorderLayout());
		content.add(new HeaderPanel("Clientes de Credito",
				"Estado General de Clientes de Credito"), BorderLayout.NORTH);
		content.add(createBrowser(), BorderLayout.CENTER);

		return content;
	}

	private JComponent createBrowser() {
		JPanel p = new JPanel(new BorderLayout(10, 10));
		ToolBarBuilder tb = new ToolBarBuilder();
		tb.add(CommandUtils.createRefreshAction(this, "load"));
		p.add(tb.getToolBar(), BorderLayout.NORTH);
		p.add(buildGridPanel(), BorderLayout.CENTER);
		return p;
	}

	private EventTableModel tm;

	@SuppressWarnings( { "unchecked" })
	private JComponent buildGridPanel() {
		JPanel p = new JPanel(new BorderLayout());
		//Acciones
		ToolBarBuilder builder = new ToolBarBuilder();
		builder.add(loadAction);
		
		//Filtros
		p.add(buildFilros(), BorderLayout.NORTH);
		
		//Grid
		grid = ComponentUtils.getStandardTable();
		tm = new EventTableModel(sortedList, new TF());
		grid.setModel(tm);
		ComponentUtils.decorateActions(grid);				
		new TableComparatorChooser(grid,sortedList,true);
		tm.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e) {
				updateTotales();
			}			
		});
		//grid.getColumn("").setCellRenderer(Renderers.)
		grid.packAll();
		
		JComponent c = UIFactory.createTablePanel(grid);
		c.setPreferredSize(new Dimension(700, 400));
		p.add(c, BorderLayout.CENTER);
		
		return p;
	}

	private void initComponents() {
		status = new JTextField();
		aviso = new JTextField();
		nombre = new JTextField();
		total = new JLabel();
		provision = new JLabel();
		loadAction = CommandUtils.createLoadAction(this, "load");
		picker = new JXDatePicker();
		picker.setFormats(new String[] { "dd/MM/yyyy" });
	}

	private JComponent buildFilros() {
		JPanel p = new JPanel(new BorderLayout());
		p.add(buildFilPanel(), BorderLayout.NORTH);
		p.add(buildtotalesPanel(), BorderLayout.SOUTH);
		return p;
	}

	@SuppressWarnings( { "unchecked" })
	private JComponent buildFilPanel() {
		FormLayout layout = new FormLayout(
				"30dlu,4dlu,50dlu,4dlu,50dlu,4dlu,30dlu,4dlu,50dlu,"
						+ "50dlu,4dlu,10dlu,4dlu,30dlu,4dlu,50dlu,4dlu,50dlu,"
						+ "50dlu,4dlu,10dlu,4dlu,30dlu,4dlu,50dlu,4dlu,50dlu",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setBorder(new TitledBorder("Filtros"));
		builder.add(new JLabel("Fecha"), cc.xyw(3, 1, 3));
		builder.add(new JLabel("Status"), cc.xy(9, 1));
		builder.add(new JLabel("Aviso"), cc.xy(16, 1));
		builder.add(new JLabel("Nombre"), cc.xyw(22, 1, 2));
		builder.add(picker, cc.xyw(4, 1, 4));
		builder.add(status, cc.xyw(10, 1, 4));
		builder.add(aviso, cc.xyw(17, 1, 4));
		builder.add(nombre, cc.xyw(24, 1, 4));
		return builder.getPanel();
	}

	@SuppressWarnings( { "unchecked" })
	private JComponent buildtotalesPanel() {
		FormLayout layout = new FormLayout(
				"50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu,4dlu,50dlu", "p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setBorder(new TitledBorder("Totales"));
		builder.add(new JLabel("Saldo"), cc.xyw(1, 1, 2));
		builder.add(new JLabel("Saldo Vencido"), cc.xyw(6, 1, 2));
		builder.add(total, cc.xyw(2, 1, 3));
		builder.add(provision, cc.xyw(8, 1, 2));
		return builder.getPanel();
	}

	@SuppressWarnings( { "unchecked" })
	private void initGlazedList() {		
		source=GlazedLists.threadSafeList(new BasicEventList());
		
		final TextFilterator<Map<String, Object>> statusFilterator = new TextFilterator<Map<String, Object>>() {
			public void getFilterStrings(List<String> baseList,
					Map<String, Object> element) {
				String s = element.get("STATUS").toString();
				baseList.add(s);
			}
		};
		final TextComponentMatcherEditor statusEditor = new TextComponentMatcherEditor(
				status, statusFilterator);
		final FilterList statusList = new FilterList(source, statusEditor);

		final TextFilterator<Map<String, Object>> sucursalFilterator = new TextFilterator<Map<String, Object>>() {

			public void getFilterStrings(List<String> baseList,
					Map<String, Object> element) {
				String s = element.get("AVISO").toString();
				baseList.add(s);
			}
		};
		@SuppressWarnings( { "unchecked" })
		final TextComponentMatcherEditor sucursalEditor = new TextComponentMatcherEditor(
				aviso, sucursalFilterator);
		final FilterList sucursalList = new FilterList(statusList,
				sucursalEditor);

		final TextFilterator<Map<String, Object>> nombreFilterator = new TextFilterator<Map<String, Object>>() {

			public void getFilterStrings(List<String> baseList,
					Map<String, Object> element) {
				String s = element.get("NOMBRE").toString();
				baseList.add(s);
			}
		};
		final TextComponentMatcherEditor nombreEditor = new TextComponentMatcherEditor(
				nombre, nombreFilterator);
		final FilterList nombreList = new FilterList(sucursalList, nombreEditor);		
		sortedList = new SortedList(nombreList, null);
		
		
	}

	private class TF implements AdvancedTableFormat<Map<String, Object>> {

		String cols[] = { "CLAVE", "NOMBRE", "VENDEDOR", "PLAZO", "LIMITE",
				"ATR_SEM_P", "PAG_SEM_P", "ATRASO_MAX", "PAG_SEM_A",
				"ULT_VENTA", "ULT_PAGO", "SALD_XXX", "SALDO_VENC",
				"FACTS_VENC", "COMENTARIOCXC", "STATUS", "AVISO", "PORC" };

		public int getColumnCount() {
			return cols.length;
		}

		public String getColumnName(int column) {
			return StringUtils.capitalize(cols[column].toLowerCase());
		}

		public Object getColumnValue(Map<String, Object> row, int column) {
			String key = cols[column];
			return row.get(key);
		}

		public Class getColumnClass(int column) {
			if("ULT_VENTA".equalsIgnoreCase(getColumnName(column))){
				return Date.class;
			}if("ULT_PAGO".equalsIgnoreCase(getColumnName(column))){
				return Date.class;
			}
			return Object.class;
		}

		public Comparator getColumnComparator(int column) {			
			return GlazedLists.comparableComparator();
		}

	}

	private List loadData() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String c = format.format(picker.getDate());
			String sql = SQLUtils
					.loadSQLQueryFromResource("sql/ClientesCreditoSt.sql");
			sql = sql.replaceAll("\'14/02/2008\'", "\'" + c + "\'");
			System.out.println("SQL" + sql);			
			List data = ServiceLocator.getJdbcTemplate().queryForList(sql);
			System.out.println(data.size());
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void load() {
		SwingWorker<List, String> worker = new SwingWorker<List, String>() {
			protected List doInBackground() throws Exception {
				return loadData();
			}

			@SuppressWarnings("unchecked")
			protected void done() {
				
				try {
					source.clear();
					source.addAll(get());
					grid.packAll();
					updateTotales();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		TaskUtils.executeSwingWorker(worker);
	}

	@SuppressWarnings("unchecked")
	private void updateTotales() {
		BigDecimal importe = BigDecimal.ZERO;
		BigDecimal prov = BigDecimal.ZERO;
		// final EventList eventList=tm.
		/***********************************************************************
		 * for(int x=0;x<tm.getRowCount();x++){ Map<String, Object> row=(Map<String,
		 * Object>)tm.getElementAt(x); BigDecimal
		 * dd=(BigDecimal)row.get("SALD_XXX"); if(dd!=null)
		 * importe=importe.add(dd); BigDecimal
		 * d1=(BigDecimal)row.get("SALDO_VENC"); if(d1!=null) prov=prov.add(d1); }
		 **********************************************************************/

		for (Object o : sortedList) {
			Map<String, Object> row = (Map<String, Object>) o;
			BigDecimal dd = (BigDecimal) row.get("SALD_XXX");
			if (dd != null)
				importe = importe.add(dd);
			BigDecimal d1 = (BigDecimal) row.get("SALDO_VENC");
			if (d1 != null)
				prov = prov.add(d1);
		}

		total.setText(NumberFormat.getCurrencyInstance().format(
				importe.doubleValue()));
		provision.setText(NumberFormat.getCurrencyInstance().format(
				prov.doubleValue()));
	}

	@SuppressWarnings( { "serial" })
	public static void main(String[] args) {
		SWExtUIManager.setup();
		final ClientesCreditoStView e = new ClientesCreditoStView();
		SXAbstractDialog dialog = new SXAbstractDialog("Prueba") {

			protected JComponent buildContent() {

				return e.getContent();
			}
		};
		dialog.open();
		System.exit(0);

	}

}
