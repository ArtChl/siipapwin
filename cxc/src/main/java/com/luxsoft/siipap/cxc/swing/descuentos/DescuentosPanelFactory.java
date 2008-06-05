package com.luxsoft.siipap.cxc.swing.descuentos;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;
import com.luxsoft.siipap.cxc.managers.DescuentosManager;
import com.luxsoft.siipap.cxc.swing.descuentos.DescuentosView.DescuentosCURD;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.crud.BasicCURDGridPanel;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.swing.utils.TaskUtils;


/**
 * Fabrica para crear implementaciones de BasicCURDGridPanel para los descuentos del sistema
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class DescuentosPanelFactory {
	
	
	
	
	public static DescuentoBrowser getDescuentoPorVolumen(){
		TableFormat VOLUMEN_TF=GlazedLists.tableFormat(
				new String[]{"id","origen","activo","maximo","descuento","year","mes"}
				,new String[]{"Id","Origen","Activo","Máximo","Descuento","Año","Mes"});
		final EventList source=GlazedLists.threadSafeList(new BasicEventList());
		final DescuentoBrowser porVolumen=new DescuentoBrowser(source,VOLUMEN_TF){

			@Override
			protected List getData() {
				return getManager().buscarDescuentosPorVolumen();
			}

			@Override
			protected void installRenderers(JTable table) {
				table.getColumnModel().getColumn(2).setCellRenderer(Renderers.getBooleanRenderer());
				table.getColumnModel().getColumn(3).setCellRenderer(Renderers.getCantidadNormalTableCellRenderer());
				table.getColumnModel().getColumn(4).setCellRenderer(Renderers.getPorcentageRenderer());
				table.getColumnModel().getColumn(5).setCellRenderer(Renderers.buildRightAllignRenderer());
				table.getColumnModel().getColumn(6).setCellRenderer(Renderers.buildRightAllignRenderer());
			}

			@Override
			protected void eliminar(Object o) {
				DescuentoPorVolumen d=(DescuentoPorVolumen)o;
				getManager().eliminarDescuento(d);
				
				
			}			
			
		};		
		return porVolumen;
	}	
	
	@SuppressWarnings("unchecked")
	public static DescuentoBrowser getDescuentoPorArticulo(){
		final TableFormat xArticuloTF=GlazedLists.tableFormat(
				new String[]{"id","clave","nombre","articulo.clave","articulo.descripcion1","articulo.lineaClave"
						,"articulo.claseClave","articulo.marcaClave","articulo.precioCredito","descuento","precioPorMillar","precioKilo","gramMax","gramMin","activo"},
				new String[]{"Id","Cliente","Nombre","Articulo","Descripción","Línea"
						,"Clase","Marca","P.L.(MIL)","Descuento","Precio (MIL)","Precio Kg","Max (gms)","Min (gms)","Activo"}
				);
		
		final EventList source=GlazedLists.threadSafeList(new BasicEventList());
		
		final DescuentoBrowser porArticulo=new DescuentoBrowser(source,xArticuloTF){
			
			private ValueModel clienteHolder=new ValueHolder(null);
			
			@Override
			protected void installRenderers(JTable table) {				
				table.getColumnModel().getColumn(8).setCellRenderer(Renderers.getPorcentageRenderer());
				table.getColumnModel().getColumn(9).setCellRenderer(Renderers.getCantidadMonetariaTableCellRenderer());				
				table.getColumnModel().getColumn(10).setCellRenderer(Renderers.buildRightAllignRenderer());
				table.getColumnModel().getColumn(11).setCellRenderer(Renderers.buildRightAllignRenderer());
				table.getColumnModel().getColumn(12).setCellRenderer(Renderers.getBooleanRenderer());
			}
			
			@Override
			public void refresh() {
				SXAbstractDialog dialog=Binder.createSeleccionDeClienteDialog(clienteHolder);
				dialog.open();			
				if(!dialog.hasBeenCanceled() ||( clienteHolder.getValue()==null)){
					super.refresh();
				}
			}
			
			public void refreshAsIs() {				
				super.refresh();
				
			}

			@Override
			protected List getData() {				
				try {
					Cliente c=(Cliente)clienteHolder.getValue();
					return getManager().buscarDescuentosPorArticulo(c);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new ArrayList();
			}

			
			@Override
			public void insert() {				
				Cliente c=DescuentosPorArticulosForm.showForm(clienteHolder.getValue());
				if(c!=null){
					clienteHolder.setValue(c);
					refreshAsIs();
				}
			}

			@Override
			protected void eliminar(Object o) {
				DescuentoPorArticulo d=(DescuentoPorArticulo)o;
				getManager().eliminarDescuento(d);
				
				
			}
			
		};
		return porArticulo;
	}
	
	public static DescuentoBrowser getDescuentoPorCliente(){
		
		TableFormat xClienteTF=GlazedLists.tableFormat(
				new String[]{"id","clave","nombre","descSiipap","descuento","tipoFac","activo","precioNeto"}
				,new String[]{"Id","Clinete","Nombre","Desc(SIIPA)","Desc","Tipo Fac","Activo","P.Neto"});
		final EventList source=GlazedLists.threadSafeList(new BasicEventList());
		final DescuentoBrowser porCliente=new DescuentoBrowser(source,xClienteTF){

			@Override
			protected List getData() {
				return getManager().buscarDescuentosPorCliente();
			}

			@Override
			protected void installRenderers(JTable table) {
				table.getColumnModel().getColumn(4).setCellRenderer(Renderers.getPorcentageRenderer());
				table.getColumnModel().getColumn(6).setCellRenderer(Renderers.getBooleanRenderer());
				table.getColumnModel().getColumn(7).setCellRenderer(Renderers.getBooleanRenderer());
			}
			
			public void insert(){				
				DescuentoPorClienteFormFactory factory=new DescuentoPorClienteFormFactory();
				factory.setManager(getManager());
				DescuentoPorCliente dd=new DescuentoPorCliente();
				FormDialog dialog=factory.getFormIndialog(dd,true);
				dialog.open();
				if(!dialog.hasBeenCanceled()){
					//refresh();
					try {
						getSource().add(dd);
					} catch (Exception e) {
						e.printStackTrace();
						refresh();
					}
					
				}
				dialog.dispose();
			}
			
			protected void edit(final EventList selected){
				DescuentoPorClienteFormFactory factory=new DescuentoPorClienteFormFactory();
				factory.setManager(getManager());
				FormDialog dialog=factory.getFormIndialog(selected.get(0),true);
				dialog.open();
				if(!dialog.hasBeenCanceled()){
					refresh();
				}
				dialog.dispose();
			}

			@Override
			protected void view(EventList selected) {
				DescuentoPorClienteFormFactory factory=new DescuentoPorClienteFormFactory();
				factory.setManager(getManager());
				
				FormDialog dialog=factory.getFormIndialog(selected.get(0),true);
				dialog.getForm().getControl();
				dialog.getForm().setEnabled(false);
				dialog.open();				
				dialog.dispose();
			}

			@Override
			protected void eliminar(Object o) {
				DescuentoPorCliente d=(DescuentoPorCliente)o;
				getManager().eliminarDescuento(d);				
			}
			
			
			
			
		};
		porCliente.setTextFilterator(GlazedLists.textFilterator(new String[]{"clave","nombre"}));
		
		return porCliente;
		
	}
	
	public abstract static class DescuentoBrowser extends BasicCURDGridPanel implements DescuentosCURD{
		
		private Object[] params;

		public DescuentoBrowser(EventList source, TableFormat tableFormat) {
			super(source, tableFormat);			
		}
		
		public void setParameters(Object... params){
			this.params=params;
		}
		
		public Object[] getParams(){
			return params;
		}
		
		public DescuentosManager getManager(){
			return (DescuentosManager)ServiceLocator.getDaoContext().getBean("descuentosManager");
			
		}
		
		protected abstract List getData();
		
		@Override
		public void refresh() {
			SwingWorker<List,String> worker=new SwingWorker<List,String>(){				
				protected List doInBackground() throws Exception {
					return getData();
				}
				@SuppressWarnings("unchecked")
				protected void done() {						
					try{
						getSource().clear();
						getSource().addAll(get());
						pack();
					}catch (Exception e) {
						MessageUtils.showError("Error cargando dato", e);
					}
				}				
			};
			TaskUtils.executeSwingWorker(worker,"Cargando Datos","Cargando descuentos desde la base de datos");
		}

		public void activar() {
			if(!getSelected().isEmpty()){
				final String msg=MessageFormat.format("Seguro que desa activar {0} descuentos, " +
						"\nEsto puede tener serias implicaciones con, por ejemplo," +
						" la generación\n de Notas de Crédito por descuento, seguro que desa continuar"
						, getSelected().size());
				int res=JOptionPane.showConfirmDialog(getGrid(),msg,"Activar Descuentos",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
				if(JOptionPane.YES_OPTION==res)
					activar(getSelected());
			}
		}

		public void desactivar() {
			if(!getSelected().isEmpty()){
				final String msg=MessageFormat.format("Seguro que desa inactivar {0} descuentos, " +
						"\nEsto puede tener serias implicaciones con, por ejemplo," +
						" la generación\n de Notas de Crédito por descuento, seguro que desa continuar"
						, getSelected().size());
				int res=JOptionPane.showConfirmDialog(getGrid(),msg,"Desactivar Descuentos",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
				if(JOptionPane.YES_OPTION==res)
					desactivar(getSelected());
			}
			
		}
		
		protected void activar(final EventList selected){
			
		}
		
		protected void desactivar(final EventList selected){
			
		}
		
		protected void delete(final EventList selected){
			
			final List delete=new ArrayList();
			delete.addAll(selected);
			if(MessageUtils.showConfirmationMessage(MessageFormat.format("Seguro que desa eliminar {0} descuentos ?",delete.size(),""),"Elminar Descuentos")){
				for(Object o:delete){
					eliminar(o);
					int index=this.source.indexOf(o);
					//source.set(index, o);
					source.remove(index);
				}
				
			}
			/**		
			
			
			for(Object o:delete){
				try {
					
					int res=JOptionPane.showConfirmDialog(getControl()
							, "Eliminar registro ?"+o
							,"Eliminar registro",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
					if(JOptionPane.OK_OPTION==res){
						eliminar(o);
						selected.remove(o);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.showError("Error eliminando descuento", e);
				}				
			}
			**/
								
		}
		
		protected abstract void eliminar(Object o);
		
		
	}
}
