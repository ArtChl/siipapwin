package com.luxsoft.siipap.swing.controls;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.ventas.domain.Venta;

public class SXTable  extends JXTable{

	public SXTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SXTable(int numRows, int numColumns) {
		super(numRows, numColumns);
		// TODO Auto-generated constructor stub
	}

	public SXTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}

	public SXTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		// TODO Auto-generated constructor stub
	}

	public SXTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		// TODO Auto-generated constructor stub
	}

	public SXTable(TableModel dm) {
		super(dm);
		// TODO Auto-generated constructor stub
	}

	public SXTable(Vector rowData, Vector columnNames) {
		super(rowData, columnNames);
	}

	@Override
	protected void createDefaultRenderers() {		
		super.createDefaultRenderers();
		setDefaultRenderer(Date.class, new DefaultTableRenderer(new ToDate()));
		setDefaultRenderer(java.sql.Date.class, new DefaultTableRenderer(new ToDate()));
		setDefaultRenderer(CantidadMonetaria.class, Renderers.getCantidadMonetariaTableCellRenderer());
	}

	

	
	
	//private TableCellRenderer dateRenderer=new DefaultTableRenderer(new ToDate());
	
	private class ToDate implements StringValue{
		
		final DateFormat df=new SimpleDateFormat("dd/MM/yyyy");

		public String getString(Object value) {
			try {
				return df.format(value);
			} catch (Exception e) {				
				if(value!=null)
					return value.toString();
				else
					return "";
			}
			
		}
		
	}
	
	
	public static void main(String[] args) {
		final SXAbstractDialog dialog=new SXAbstractDialog("Test Grid"){

			@Override
			protected JComponent buildContent() {
				Venta v=new Venta();
				v.setFecha(new Date());
				EventList<Venta> ventas=new BasicEventList<Venta>();
				ventas.add(v);
				TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class, new String[]{"fecha"}, new String[]{"Fecha"});
				EventTableModel<Venta> tm=new EventTableModel<Venta>(ventas,tf);
				SXTable table=new SXTable(tm);
				return UIFactory.createTablePanel(table);
			}
			
		};
		dialog.open();
		System.exit(0);
	}

}
