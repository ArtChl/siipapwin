package com.luxsoft.siipap.cxc.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.utils.BrowserUtils;

/**
 * Colaborador de DescuentosView a quien se le delega la tarea de 
 * presentar una lista  apropiadas para cada tipo de descuento.
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentosPanels {
	
	private DescuentosGeneralesPanel porVolumen;
	
	private DescuentosGeneralesPanel porCliente;
	
	private DescuentosGeneralesPanel porArticulo;
	
	private DescuentosGeneralesPanel porVenta;
	
	public DescuentosPanels(){
		
	}
	
	public DescuentosGeneralesPanel getPorArticulo() {
		if(porArticulo==null){
			porArticulo=new DescuentosGeneralesPanel("Descuentos Por Artículo"
					,"Lista de descuentos fijos por artículo o familia");
			String[] props={"clave","nombre","articulo.clave","articulo.desecripcion1","activo","descuento","descSiipap"};
			String[] labels={"Cliente","Nombre","Articulo","Descripción","Activo","Descuento","Siipap"};
			TableFormat tf=GlazedLists.tableFormat(props,labels);
			TextFilterator textF=GlazedLists.textFilterator(props);
			porArticulo.setTableFormat(tf);
			porArticulo.setTextFilterator(textF);
		}
		return porArticulo;
	}

	public DescuentosGeneralesPanel getPorCliente() {
		if(porCliente==null){
			porCliente=new DescuentosGeneralesPanel("Descuentos por Cliente"
					,"Lista de descuentos fijos por cliente");
			String[] props={"clave","nombre","descSiipap","tipoFac","activo","precioNeto","descuento"};
			String[] labels={"Clave","Nombre","Siipap","TipoFac","Activo","Precio Neto","Descuento"};
			TableFormat tf=GlazedLists.tableFormat(props,labels);
			TextFilterator textF=GlazedLists.textFilterator(props);
			porCliente.setTableFormat(tf);
			porCliente.setTextFilterator(textF);
		}
		return porCliente;
	}

	public DescuentosGeneralesPanel getPorVenta() {
		if(porVenta==null){
			porVenta=new DescuentosGeneralesPanel("Descuentos Por Venta (Especiales)"
					,"Lista de descuentos por venta tambien conocidos como especiales");
			String[] props={"id","venta.id","venta.numero","venta.tipo","venta.serie","descCliente.id","descVol.id","fijo","descuento"};
			String[] labels={"id","venta.id","venta.numero","venta.tipo","venta.serie","descCliente.id","descVol.id","fijo","descuento"};
			TableFormat tf=GlazedLists.tableFormat(props,labels);
			TextFilterator textF=GlazedLists.textFilterator(props);
			porVenta.setTableFormat(tf);
			porVenta.setTextFilterator(textF);
		}
		return porVenta;
	}

	public DescuentosGeneralesPanel getPorVolumen() {
		if(porVolumen==null){
			porVolumen=new DescuentosGeneralesPanel("Descuentos Por Volumen (Escala)"
					,"Lista de descuentos por volumen tambien conocidos como escala");
			String[] props={"id","clave","nombre","origen","baja","activo","maximo","descuento","year","mes"};
			String[] labels={"id","clave","nombre","origen","baja","activo","maximo","descuento","year","mes"};
			TableFormat tf=GlazedLists.tableFormat(props,labels);
			TextFilterator textF=GlazedLists.textFilterator(props);
			porVolumen.setTableFormat(tf);
			porVolumen.setTextFilterator(textF);
		}
		return porVolumen;
	}
	
	public class DescuentosGeneralesPanel extends AbstractControl{
		
		private final String title;
		private final String desc;
		private TableFormat tableFormat;
		private EventList descuentos;
		private TextFilterator textFilterator;
		private JTextField inputField=new JTextField(30);
		private JTable grid;
		
		

		public DescuentosGeneralesPanel(final String title, final String desc) {			
			this.title = title;
			this.desc = desc;
		}

		@Override
		protected JComponent buildContent() {
			JPanel panel=new JPanel(new BorderLayout());
			panel.add(new HeaderPanel(getTitle(),getDesc()),BorderLayout.NORTH);
			panel.add(buildGridPanel(),BorderLayout.CENTER);
			pack();
			return panel;
		}
		
		@SuppressWarnings("unchecked")
		private JComponent buildGridPanel(){
			JPanel p=new JPanel(new BorderLayout());
			p.add(BrowserUtils.buildTextFilterPanel(inputField),BorderLayout.NORTH);
			
			if( (getDescuentos()!=null) && (getTableFormat()!=null) ){
				EventList source=getDescuentos();
				
				//Creamos el grid			
				if(getTextFilterator()!=null){
					TextComponentMatcherEditor editor=new TextComponentMatcherEditor(inputField,getTextFilterator());
					FilterList filterList=new FilterList(getDescuentos(),editor);
					source=filterList;
				}
				SortedList sortedList=new SortedList(source,null);
				EventTableModel tm=new EventTableModel(sortedList,getTableFormat());
				grid=BrowserUtils.buildBrowserGrid();
				grid.setModel(tm);
				new TableComparatorChooser(grid,sortedList,true);
				
			}else{
				grid=new JTable(10,10);
			}
			p.add(UIFactory.createTablePanel(grid),BorderLayout.CENTER);
			//p.setBorder(BorderFactory.createEmptyBorder());
			return p;			
		}
		
		public void pack(){
			if(getGrid()!=null)
				((JXTable)getGrid()).packAll();
		}

		public EventList getDescuentos() {
			if(descuentos==null){
				descuentos=new BasicEventList();
			}
			return descuentos;
		}
		public void setDescuentos(EventList descuentos) {
			this.descuentos = descuentos;
		}


		public TableFormat getTableFormat() {
			return tableFormat;
		}
		public void setTableFormat(TableFormat tableFormat) {
			this.tableFormat = tableFormat;
			pack();
		}

		public String getDesc() {
			return desc;
		}
		public String getTitle() {
			return title;
		}
		
		public JTable getGrid() {
			return grid;
		}

		public TextFilterator getTextFilterator() {
			return textFilterator;
		}
		public void setTextFilterator(TextFilterator textFilterator) {
			this.textFilterator = textFilterator;
		}
		
		
		
		
	}




	
}
