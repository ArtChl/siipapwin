package com.luxsoft.siipap.cxc.selectores;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Facade para el uso de selectores
 * 
 * @author Ruben Cancino
 *
 */
public class Selectores {
	
	/**
	 * Selector para {@link PagoM}
	 * 
	 * @param c
	 * @param pagos
	 * @return
	 */
	public static PagoM seleccionarPagoM(final Cliente c,final List<PagoM> pagos){
		final SelectorDePagoM selector=new SelectorDePagoM(GlazedLists.eventList(pagos)){

			@Override
			protected String getHeaderDesc() {
				String pattern="{0} ({1})";				
				return MessageFormat.format(pattern, c.getNombre(),c.getClave());
			}
			
		};
		selector.open();
		if(!selector.hasBeenCanceled()){
			return selector.getSelected();
		}else
			return null;
	}
	
	/**
	 * Permite seleccionar un {@link PagoM}
	 * 
	 * @param pagos
	 * @return
	 */
	public static PagoM seleccionarPagoM(List<Map<String, Object>> pagos){
		final SelectorDePagoM selector=new SelectorDePagoM(GlazedLists.eventList(pagos)){

			@Override
			protected String getHeaderDesc() {
				String pattern="{0} ({1})";				
				return MessageFormat.format(pattern, "Saldos a Favor","Todos los Clientes");
			}
			
			protected TableFormat getTableFormat(){
				return new DisponiblesTableFormat();
			}

			@Override
			protected void decorateGrid(JXTable grid) {
				grid.getColumnExt("TIPO").setVisible(false);
				grid.getColumnExt("ORIGEN_ID").setVisible(false);
				grid.getColumnExt("NOTA_ID").setVisible(false);
			}
			
			
			
		};
		selector.setFiltros(true);
		selector.open();
		if(!selector.hasBeenCanceled()){
			return selector.getSelected();
		}else
			return null;
	}
	
	/**
	 * Presenta un selector adecuado para seleccionar una {@link NotaDeCredito} misma que regresa 
	 * 
	 * @param c
	 * @param notas
	 * @return
	 */
	public static NotaDeCredito seleccionarNotaDeCredito(final Cliente c,final EventList<NotaDeCredito> notas){
		final Selector<NotaDeCredito> selector=new AbstractSelector<NotaDeCredito>(notas,"Notas de Crédito"
				,"Disponibles para pago",MessageFormat.format("{0} ({1})", c.getNombre(),c.getClave())){
						
			@Override
			protected TableFormat<NotaDeCredito> getTableFormat() {
				return CXCTableFormats.getNotaDeCreditoTF();
			}			
			
		};
		selector.open();
		if(!selector.hasBeenCanceled()){
			return selector.getSelected();
		}
		return null;
	}
	
	public static Devolucion seleccionarDevolucion(final Cliente c,final EventList<Devolucion> devos){
		final Selector<Devolucion> selector=new AbstractSelector<Devolucion>(devos,"Devoluciones"
				,"Lista de devoluciones pendientes",MessageFormat.format("{0} ({1})", c.getNombre(),c.getClave())){
					@Override
					protected TableFormat<Devolucion> getTableFormat() {						
						return CXCTableFormats.getDevolucionTF();
						
					}
			
		};
		selector.open();
		if(!selector.hasBeenCanceled()){
			return selector.getSelected();
		}
		return null;
	}
	
	/**
	 * Presenta un selector adecuado para seleccionar una {@link Venta} misma que regresa 
	 * 
	 * @param c
	 * @param notas
	 * @return
	 */
	public static Venta seleccionarVentaCredito(final Cliente c,final EventList<Venta> ventas){
		final Selector<Venta> selector=new AbstractSelector<Venta>(ventas,"Lista de venas"
				,"Ventas a credito pendientes de pago",MessageFormat.format("{0} ({1})", c.getNombre(),c.getClave())){
						
			@Override
			protected TableFormat<Venta> getTableFormat() {
				return CXCTableFormats.getVentasCreTF();
			}			
			
		};
		selector.open();
		if(!selector.hasBeenCanceled()){
			return selector.getSelected();
		}
		return null;
	}
	
	/**
	 *	{@link TableFormat} personalizado para la seleccion de {@link PagoM} con disponible
	 *	Para obtimizar su desempeño este Tableformat funciona con un List de registros
	 *  utilizando JDBC de maner directa 
	 * 
	 * @author RUBEN
	 *
	 */
	public  static class DisponiblesTableFormat implements AdvancedTableFormat<Map<String, Object>>{
		
		String[] cols={
				"TIPO",
				"CLAVE",
				"NOMBRE",
				"IMPORTE",
				"APLICADO",
				"DISPONIBLE",
				"FORMADP",
				"REFERENCIA",
				"COMENTARIO",
				"FECHA",
				"ORIGEN_ID",
				"NOTA_ID", 
				
		};

		public Class getColumnClass(int column) {
			switch (column) {
			case 1:
			case 2:
			case 3:
			case 7:
			case 8:
			case 9:
				return String.class;
			case 4:
			case 5:
			case 6:
				return BigDecimal.class;
			case 10:
				return String.class;
			case 11:
			case 12:
				return Long.class;
			default:
				return String.class;
			}
		}

		public Comparator getColumnComparator(int column) {
			return GlazedLists.comparableComparator();
		}

		public int getColumnCount() {
			return cols.length;
		}

		public String getColumnName(int column) {
			
			return cols[column];
		}

		private DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		
		public Object getColumnValue(Map<String, Object> baseObject, int column) {
			Object o=baseObject.get(getColumnName(column));
			if(o instanceof Date )
				return df.format((Date)o);
			return o;
		}
		
	}

}
