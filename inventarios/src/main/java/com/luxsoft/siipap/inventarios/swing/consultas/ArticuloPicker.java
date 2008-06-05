package com.luxsoft.siipap.inventarios.swing.consultas;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Permite seleccionar un articulo de un combo
 * 
 * @author Ruben Cancino
 *
 */
public class ArticuloPicker extends AbstractControl{
	
	private JComboBox box;
	private EventList<Articulo> articulos;
	private WeakReference<ValueModel> vmodel;
	
	
	public ArticuloPicker(final ValueModel model) {
		this.vmodel=new WeakReference<ValueModel>(model);
	}

	public ArticuloPicker() {
		
	}

	@Override
	protected JComponent buildContent() {				
		return getBox();
	}
	
	public JComboBox getBox(){
		if(box==null){
			box=createBox();
			box.getModel().addListDataListener(new ListDataListener(){
				public void contentsChanged(ListDataEvent e) {
					if(vmodel!=null){
						if(vmodel.get()!=null){
							System.out.println("Asignando articulo: "+box.getSelectedItem());
							vmodel.get().setValue(box.getSelectedItem());
						}
					}
					
				}
				public void intervalAdded(ListDataEvent e) {}
				public void intervalRemoved(ListDataEvent e) {}				
			});
			ComponentUtils.addAction((JComponent)box.getEditor().getEditorComponent(), CommandUtils.createLoadAction(this, "load"),KeyStroke.getKeyStroke("F5"));
		}
		return box;
	}
	
	/**
	 * Must be called from the EDT
	 * 
	 * @return
	 */
	private JComboBox createBox(){
		Assert.isTrue(SwingUtilities.isEventDispatchThread(),"Este panel solo se puede construir desde el EDT");
		box=new JComboBox();
		Articulo a=new Articulo();
		a.setClave("12345678912345");
		a.setDescripcion1("........................................................................");
		box.setPrototypeDisplayValue(a);
		articulos=GlazedLists.threadSafeList(new BasicEventList<Articulo>());
		final TextFilterator<Articulo> textFilterator=GlazedLists.textFilterator(new String[]{"clave","descripcion1"});		
		AutoCompleteSupport support = AutoCompleteSupport.install(box, articulos, textFilterator,new ArticuloFormat());
        support.setFilterMode(TextMatcherEditor.CONTAINS);        
        return box;
	}
	
	public void load(){
		SwingWorker<List<Articulo>, String> worker=new SwingWorker<List<Articulo>, String>(){
			@Override
			protected List<Articulo> doInBackground() throws Exception {
				return ServiceLocator.getArticuloDao().buscarTodosLosActivos();
			}			
			protected void done() {
				try {
					articulos.addAll(get());					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void close(){
		articulos.clear();
	}
	
	public Articulo getArticulo(){
		return (Articulo)box.getSelectedItem();
	}
	
	private static class ArticuloFormat extends Format{
		
		private String patter="{0} ({1})";

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
			if(obj!=null && (obj instanceof Articulo)){
				Articulo a=(Articulo)obj;				
				toAppendTo.append(MessageFormat.format(patter, a.getDescripcion1(),a.getClave()));
			}
			return toAppendTo;
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			Articulo a=new Articulo();
			a.setClave(source);
			return a;
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		
		SWExtUIManager.setup();
		ValueModel vm=new ValueHolder();
		final ArticuloPicker piker=new ArticuloPicker(vm);
		final SXAbstractDialog dialog=new SXAbstractDialog("Test"){
			
			@Override
			protected JComponent buildContent() {				
				return piker.getControl();
			}
			
			
		};
		SwingUtilities.invokeAndWait(new Runnable(){

			public void run() {
				dialog.open();
				
			}
			
		});
		
	}
	

}
