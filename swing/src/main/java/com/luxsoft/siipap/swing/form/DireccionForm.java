package com.luxsoft.siipap.swing.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.domain.Direccion;
import com.luxsoft.siipap.domain.ZonaPostal;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.utils.MessageUtils;

public class DireccionForm extends AbstractControl implements Form{
	
	
	private JTextField calle;
	private JTextField numero;
	private JTextField numeroExterior;
	private JTextField colonia;
	private JTextField cp;	
	private JComboBox municipio;
	private JComboBox ciudad;
	private JComboBox estado;
	private JComboBox pais;
	
	private final DireccionFormModel model;
	
	public DireccionForm() {
		this(new Direccion());
	}
	
	public DireccionForm(Object obj){
		this((Direccion)obj);
	}
	
	public DireccionForm(Direccion direccion){
		this.model=new DireccionFormModel(direccion);		
		initComponents();
		initAnnotatedComponents();
		decoreteAutoCompletion();
		
	}
	
	
	
	private void initComponents(){
		calle=Binder.createMayusculasTextField(model.getComponentModel("calle"));
		numero=BasicComponentFactory.createTextField(model.getComponentModel("numero"));
		numeroExterior=BasicComponentFactory.createTextField(model.getComponentModel("numeroExterior"));
		colonia=BasicComponentFactory.createTextField(model.getComponentModel("colonia"));
		cp=BasicComponentFactory.createTextField(model.getComponentModel("cp"));		
		municipio=new JComboBox();
		municipio.addActionListener(new ViewToModelHandler(municipio,"municipio"));
		
		ciudad=new JComboBox();
		ciudad.addActionListener(new ViewToModelHandler(ciudad,"ciudad"));
		
		estado=BasicComponentFactory.createComboBox(new SelectionInList(model.lestados,model.getModel("estado")));
		estado.addActionListener(new ViewToModelHandler(estado,"estado"));
		
		SelectionInList pp=new SelectionInList(new String[]{"México"},model.getModel("pais"));
		pais=BasicComponentFactory.createComboBox(pp);
		pais.setSelectedIndex(0);
		
	}
	
	private void initAnnotatedComponents(){
		
	}
	
	private void decoreteAutoCompletion(){
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run() {					 
					AutoCompleteSupport.install(ciudad, model.lciudades);
					AutoCompleteSupport.install(municipio, model.lmpos);
				}				
			});
			
		} catch (Exception e) {e.printStackTrace();}
		
	}

	@Override
	protected JComponent buildContent() {
		DefaultFormBuilder builder=new DefaultFormBuilder(
				new FormLayout("l:50dlu,3dlu,max(p;50dlu),2dlu,l:50dlu,3dlu,max(p;50dlu):g(.5)",""));
		builder.appendSeparator("Dirección");
		builder.append("Calle",calle,5);
		builder.nextLine();
		builder.append("No Interior",numero);
		builder.append("No Exterior",numeroExterior,true);
		builder.append("C.P.",cp,true);
		builder.append("Colonia",colonia,5);
		builder.nextLine();
		builder.append("Municipio",municipio,5);
		builder.nextLine();
		builder.append("Ciudad",ciudad,3);
		builder.nextLine();
		builder.append("Estado",estado,3);
		builder.nextLine();
		builder.append("Pais",pais,true);
		return builder.getPanel();
	}
	
	public FormModel getFormModel() {
		return this.model;
	}
	
	
	
	public class DireccionFormModel extends AbstractGenericFormModel<Direccion, String>{
		
		protected EventList<ZonaPostal> zonas;
		protected EventList<String> lestados;
		protected EventList<String> lciudades;
		protected EventList<String> lmpos;
		
		

		public DireccionFormModel() {
			super();			
		}

		public DireccionFormModel(Object bean) {
			super(bean);			
		}

		@Override
		protected void initModels() {			
			super.initModels();
			addBeanPropertyChangeListener( new ZipHandler());			
			readData();
		}
		
		private void readData(){
			zonas=new BasicEventList<ZonaPostal>();
			loadEstados();
			loadCiudades();
			loadMunicipios();
		}
		
		public void loadEstados(){
			lestados=GlazedLists.eventList(DireccionUtils.cargarEstados());
		}
		public void loadCiudades(){
			lciudades=GlazedLists.eventList(DireccionUtils.cargarCiudades());
		}
		public void loadMunicipios(){
			lmpos=GlazedLists.eventList(DireccionUtils.cargarMunicipios());
		}

		@Override
		public void commit() {
			System.out.println("Termino de editar: "+getFormBean());
		}
		
		private class ZipHandler implements  PropertyChangeListener{
			public void propertyChange(PropertyChangeEvent evt) {
				List<ZonaPostal> l=DireccionUtils.cargarZonas(evt.getNewValue().toString());
				if(l==null){
					String msg=MessageFormat.format("El codigo postal {0} no esta registrado", evt.getNewValue());
					MessageUtils.showMessage(msg, "Buscando Codigo");
				}
			}			
		}

		public PresentationModel getPresentationModel() {			
			return this;
		};
		
		

		
	}
	
	private class ViewToModelHandler implements ActionListener{
		
		private final JComboBox box;
		private final String property;

		public ViewToModelHandler(final JComboBox box, final String property) {			
			this.box = box;
			this.property = property;
			this.box.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			model.getModel(property).setValue(box.getSelectedItem());
			
		}
		
		
	};
	
	public static void main(String[] args) {
		DireccionForm form=new DireccionForm();
		FormDialog dialog=new FormDialog("Dirección",form);
		dialog.open();
		
	}

	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	

}
