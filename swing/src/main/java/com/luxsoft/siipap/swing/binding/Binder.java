package com.luxsoft.siipap.swing.binding;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.formatter.EmptyDateFormatter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.panel.HeaderPanel;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Sucursales;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.controls.UpperCaseField;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class Binder {
	
	protected static Logger logger=Logger.getLogger(Binder.class);
	
	
	
	
	/**
	 * Debug method para verificar el funcionamiento de un binding
	 *  
	 * @param content
	 * @param valueModel
	 */
	protected static void viewBinding(JComponent content,final ValueModel valueModel){
		valueModel.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("New ValueModel value: "+evt.getNewValue());				
			}			
		});
		
		SWExtUIManager.setup();
		final JFrame app=new JFrame("Probando binding");
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(content,BorderLayout.CENTER);
		app.setContentPane(panel);
		app.pack();
		app.setVisible(true);
	}
	
	/**
	 * Hace un binding adecuado para un campo de tipo java.util.Date
	 * 
	 * @param model
	 * @return
	 */
	public static JComponent createDateComponent(final ValueModel model){
		final JXDatePicker datePicker=new JXDatePicker();		
		SimpleDateFormat df1=new SimpleDateFormat("dd/MM/yy");
		df1.setLenient(false);
		//SimpleDateFormat df2=new SimpleDateFormat("dd/MM/yy");
		datePicker.setFormats(new DateFormat[]{df1});
		datePicker.getComponent(1).setFocusable(false);
		
		
		//datePicker.setFormats(new String[]{"dd/MM/yy","dd/MM/yyyy"});
		//datePicker.getEditor().setFocusLostBehavior(JFormattedTextField.REVERT);
		//DateFormatter dateF=(DateFormatter)datePicker.getEditor().getFormatter();
		//dateF.setAllowsInvalid(false);
		Bindings.bind(datePicker.getEditor(),model);
		if(model instanceof ComponentValueModel){
			ComponentValueModel vm=(ComponentValueModel)model;
			datePicker.setEnabled(vm.isEnabled());
			datePicker.setEditable(vm.isEnabled());
			datePicker.setVisible(vm.isVisible());
			vm.addPropertyChangeListener(new PropertyChangeListener(){

				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(ComponentValueModel.PROPERTYNAME_VALUE))
						return;
					Boolean val=(Boolean)evt.getNewValue();
					if(evt.getPropertyName().equals(ComponentValueModel.PROPERTYNAME_EDITABLE))
						datePicker.setEditable(val);
					else if(evt.getPropertyName().equals(ComponentValueModel.PROPERTYNAME_ENABLED))
						datePicker.setEnabled(val);					
					else if(evt.getPropertyName().equals(ComponentValueModel.PROPERTYNAME_VISIBLE))
						datePicker.setVisible(val);					
				}
				
			});
		}
		return datePicker;
	}
	
	/**
	 * 
	 * @param valueModel
	 * @return
	 */
	public static PeriodoPicker createPeriodoBinder(final ValueModel valueModel ){
		
		if (valueModel == null)
			throw new NullPointerException("The value model must not be null.");
		if(valueModel.getValue()==null){
			valueModel.setValue(new Periodo());
		}
		PeriodoPicker picker=new PeriodoPicker((Periodo)valueModel.getValue());    
	    PropertyConnector connector = new PropertyConnector(valueModel, "value", picker, "periodo");
	    connector.updateProperty2();
		Bindings.addComponentPropertyHandler(picker, valueModel);
		return picker;
	}
	
	public static JFormattedTextField createCantidadMonetariaBinding(final ValueModel vm){
		CantidadMonetariaBinding b=new CantidadMonetariaBinding(vm);
		return b.getControl();
	}
	
	/**
	 * Binding para un campo de tipo descuento
	 * 
	 * @param vm
	 * @return
	 */
	public static JFormattedTextField createDescuentoBinding(final ValueModel vm){
		JFormattedTextField tf=BasicComponentFactory
		.createFormattedTextField(vm, FormatUtils.getPorcentageFormatterFactory());					
		return tf;
	}
	
	/**
	 * Crea un binding adecuado para campos y propiedades numericos fraccionarios
	 * 
	 * @param vm
	 * @param presicion
	 * @return
	 */
	public static JFormattedTextField createNumberBinding(final ValueModel vm){
		NumberFormat format=NumberFormat.getInstance();		
		NumberFormatter formatter=new NumberFormatter(format);		
		JFormattedTextField tf=BasicComponentFactory.createFormattedTextField(vm, formatter);
		return tf;
	}
	
	/**
	 * Crea un binding adecuado para campos y propiedades numericos fraccionarios
	 * 
	 * @param vm
	 * @param presicion
	 * @return
	 */
	public static JFormattedTextField createNumberBinding(final ValueModel vm,int decimales){
		NumberFormat format=NumberFormat.getInstance();
		format.setMaximumFractionDigits(decimales);
		NumberFormatter formatter=new NumberFormatter(format);		
		JFormattedTextField tf=BasicComponentFactory.createFormattedTextField(vm, formatter);
		return tf;
	}
	
	/**
	 * Crea un binding adecuado para campos y propiedades numericos fraccionarios
	 * 
	 * @param vm
	 * @param presicion
	 * @return
	 */
	public static JFormattedTextField createMonetariNumberBinding(final ValueModel vm){
		JFormattedTextField tf=BasicComponentFactory.createFormattedTextField(vm, FormatUtils.getMoneyFormatterFactory());
		return tf;
	}
	
	/**
	 * Crea un binding adecuado para campos y propiedades monetarias implementadas con BigDecimal
	 * 
	 * @param vm
	 * @param presicion
	 * @return
	 */
	public static JFormattedTextField createBigDecimalMonetaryBinding(final ValueModel vm){
		final ValueHolder holder=new ValueHolder(new Double(0.00));
		final JFormattedTextField tf=BasicComponentFactory.createFormattedTextField(holder, FormatUtils.getBigDecimalMoneyFormatterFactory());
		
		holder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				Number n=(Number)evt.getNewValue();
				if(n!=null)
					vm.setValue(BigDecimal.valueOf(n.doubleValue()));
			}			
		});
		vm.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				Number val=(Number)evt.getNewValue();
				if(val!=null)
					holder.setValue(val);
			}			
		});
		return tf;
	}
	
	public static JFormattedTextField createGramosBinding(final ValueModel vm){
		return createNumberBinding(vm, 3);
	}
	
	/**
	 * Binding para seleccion de tipo de factura
	 * 
	 * @param vm
	 * @return
	 */
	public static JComponent createTipoDeFacturaBinding(final ValueModel vm,final boolean enabled){
		TiposDeFacturaBinding binding=new TiposDeFacturaBinding(vm);
		binding.setEnabled(enabled);
		return binding.getControl();
	}
	
	
	
	/**
	 * Crea un componente para la seleccion de clientes
	 * 
	 */
	public static JComponent createClientesBinding(final ValueModel valueModel){
		ClienteBinding binding=new ClienteBinding(valueModel);
		JComponent c=binding.getControl();
		if(valueModel instanceof ComponentValueModel){
			ComponentValueModel cc=(ComponentValueModel)valueModel;
			binding.setEnabled(cc.isEnabled());
		}
		return c;
	}
	
	public static SXAbstractDialog createSeleccionDeClienteDialog(final ValueModel model){
		return new ClienteSelectorDialog(model);
	}
	
	/**
	 * Binder para Articulos
	 * 
	 * @param valueModel
	 * @return
	 */
	public static JComponent createArticulosBinding(final ValueModel valueModel){
		final EventList<Articulo> articulos=GlazedLists.eventList(ServiceLocator.getArticuloDao().buscarTodosLosActivos());
		final Format format=new Format(){

			@Override
			public StringBuffer format(Object obj, StringBuffer buff, FieldPosition pos) {
				Articulo a=(Articulo)obj;
				if(a!=null){
					buff.append("(");
					buff.append(a.getClave());
					buff.append(") ");
					buff.append(a.getDescripcion1());
				}
				return buff;
			}

			@Override
			public Object parseObject(String source, ParsePosition pos) {				
				return null;
			}
			
		};
		final TextFilterator<Articulo> tf=GlazedLists.textFilterator(new String[]{"clave","descripcion1"});
		return createBindingBox(valueModel, articulos,tf,format);
	}
	
	/**
	 * JComboBox para clientes de credito
	 * 
	 * @param valueModel
	 * @return
	 */
	public static JComboBox createClientesCreditoBindingBox(final ValueModel valueModel){
		final EventList<ClienteCredito> clientes=GlazedLists.eventList(ServiceLocator.getClienteDao().buscarClientesDeCredito());
		final Format format=new Format(){

			@Override
			public StringBuffer format(Object obj, StringBuffer buff, FieldPosition pos) {
				ClienteCredito a=(ClienteCredito)obj;
				if(a!=null){
					buff.append(a.getNombre());
					buff.append(" (");
					buff.append(a.getClave());
					buff.append(") ");
					
				}
				return buff;
			}

			@Override
			public Object parseObject(String source, ParsePosition pos) {				
				return null;
			}
			
		};
		final TextFilterator<ClienteCredito> tf=GlazedLists.textFilterator(new String[]{"clave","nombre"});
		return createBindingBox(valueModel, clientes,tf,format);
	}
	
	/**
	 * Combo box para seleccion de credito o contado
	 * 
	 * @param valueModel
	 * @return
	 */
	public static JComponent createCreditoContadoBinding(final ValueModel valueModel){
		SelectionInList list=new SelectionInList(new String[]{"CRE","CON"},valueModel);
		return BasicComponentFactory.createComboBox(list,new TransformerRenderer(){

			@Override
			protected Object transform(Object val) {
				String s=val.toString();
				if("CRE".equals(s))
					return "CREDITO";
				else if("CON".equals(s))
					return "CONTADO";
				else return "";
			}
			
		});
	}
	
	
	
	public static JComboBox createBindingBox(final ValueModel valueModel,final EventList list){
		return createBindingBox(valueModel, list,null);
	}
	
	public static JComboBox createBindingBox(final ValueModel valueModel,final EventList list,final Format format){
		final TextFilterator filterator=GlazedLists.toStringTextFilterator();
		return createBindingBox(valueModel, list, filterator,format);
	}	
	
	public static JComboBox createBindingBox(final ValueModel valueModel,final EventList list,final TextFilterator filterator,Format format){
		final JComboBox box=new JComboBox();
		addValueModelHandler(valueModel, box);				
		decorateAutoSupport(box, list, filterator,format);
		return box;
	}
	
	@SuppressWarnings("unchecked")
	public static void decorateAutoSupport(final JComboBox box,final EventList list,final TextFilterator filterator,final Format format){
		if(SwingUtilities.isEventDispatchThread()){
			AutoCompleteSupport support = AutoCompleteSupport.install(box, list, filterator,format);
	        support.setFilterMode(TextMatcherEditor.CONTAINS);
	        return;
		}
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    
				public void run() {
			        AutoCompleteSupport support = AutoCompleteSupport.install(box, list, filterator,format);
			        support.setFilterMode(TextMatcherEditor.CONTAINS);
			        //support.setStrict(true);
			    }
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void addValueModelHandler(final ValueModel valueModel,final JComboBox box){
		Assert.notNull(valueModel,"No se permite un valueModel nulo para enlazar el combo box ");
		box.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				valueModel.setValue(box.getSelectedItem());
			}
			
		});
	}
	
	
	public static JComboBox createTipoDeVenta(ValueModel valueModel){
		DefaultListModel lmodel=new DefaultListModel();
		lmodel.addElement("Todos");
		lmodel.addElement("Contado");
		lmodel.addElement("Credito");
		SelectionInList list=new SelectionInList(lmodel,valueModel);
		return BasicComponentFactory.createComboBox(list);
	}
	
	public static JComboBox createYearBinding(ValueModel valueModel){
		Integer ini=2002;
		Integer hoy=Calendar.getInstance().get(Calendar.YEAR);
		DefaultListModel lmodel=new DefaultListModel();
		for(int i=ini;i<=hoy;i++){
			lmodel.addElement(i);
		}
		SelectionInList list=new SelectionInList(lmodel,valueModel);
		return BasicComponentFactory.createComboBox(list);
		
	}
	
	public static JComboBox createMesBindingConTodos(ValueModel valueModel){
		Integer ini=1;
		Integer hoy=13;
		DefaultListModel lmodel=new DefaultListModel();
		for(int i=ini;i<=hoy;i++){
			lmodel.addElement(i);
		}
		SelectionInList list=new SelectionInList(lmodel,valueModel);
		return BasicComponentFactory.createComboBox(list,new MesYTodosRenderer());
		
	}
	
	
	public static JComboBox createMesBinding(ValueModel valueModel){
		Integer ini=1;
		Integer hoy=12;
		DefaultListModel lmodel=new DefaultListModel();
		for(int i=ini;i<=hoy;i++){
			lmodel.addElement(i);
		}
		SelectionInList list=new SelectionInList(lmodel,valueModel);
		return BasicComponentFactory.createComboBox(list,new MesRenderer());
		
	}
	
	public static JComponent createYearMesBinding(ValueModel yearModel,ValueModel mesModel){
		DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:p,3dlu,f:max(p;100dlu)",""));
		builder.setDefaultDialogBorder();
		builder.append("Año",createYearBinding(yearModel),true);
		builder.append("Mes",createMesBinding(mesModel),true);
		return builder.getPanel();
	}

	public static JComboBox createDiaDeLaSemanaBinding(ValueModel valueModel){
		Integer ini=1;
		Integer hoy=7;
		DefaultListModel lmodel=new DefaultListModel();
		for(int i=ini;i<=hoy;i++){
			lmodel.addElement(i);
		}
		SelectionInList list=new SelectionInList(lmodel,valueModel);
		return BasicComponentFactory.createComboBox(list,new DiaRenderer());
		
	}
	
	
	public static AbstractDialog createSelectorMesYear(final ValueModel yearModel,final ValueModel mesModel,JFrame owner){
		AbstractDialog dialog=new AbstractDialog(owner,"Periodo"){
			
			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				p.add(buildMainPanel(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}
			
			private JComponent buildMainPanel(){
				return createYearMesBinding(buffer(yearModel),buffer(mesModel));
			}			

			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Periodo","Seleccione el Año y mes desado");
			}

			protected String getString(String key, String defaultText) {
				return defaultText;
			}
		};
		return dialog;
	}
	
	
	public static AbstractDialog createSelectorYear(final ValueModel yearModel,JFrame owner){
		AbstractDialog dialog=new AbstractDialog(owner,"Periodo"){
			
			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				p.add(buildMainPanel(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}
			
			private JComponent buildMainPanel(){
				return createYearBinding(buffer(yearModel));
			}			

			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Periodo","Seleccione el Año  desado");
			}

			protected String getString(String key, String defaultText) {
				return defaultText;
			}
		};
		return dialog;
	}
	
	public static SXAbstractDialog createDateSelector(final ValueModel vm){
		SXAbstractDialog dialog=new SXAbstractDialog("Selección por Día"){
			
			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				p.add(buildMainPanel(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}
			
			private JComponent buildMainPanel(){
				DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:p,3dlu,f:p:g",""));
				builder.setDefaultDialogBorder();
				builder.append("Fecha ",createDateComponent(vm));
				return builder.getPanel();
			}			

			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Selección de Fecha","Seleccione la fecha desada");
			}

			protected String getString(String key, String defaultText) {
				return defaultText;
			}
		};
		return dialog;
	}
	
	public static JComponent createUnboundComponent(final Object bean,final String propertyName){
		final Class propertyType=BeanUtils.findPropertyType(propertyName, new Class[]{bean.getClass()});
		final String clazz=ClassUtils.getShortName(bean.getClass());
		final String msg=MessageFormat.format("No existe un binding apripiado para la propiedad de tipo {0} usada en {1}.{2}",
				propertyType.getName(),clazz,propertyName);
		logger.error(msg);
		final JTextField tf=new JTextField(10);
		tf.setToolTipText(msg);
		tf.setBackground(Color.RED);
		return tf;
	}
	
	public static JTextField createMayusculasTextField(final ValueModel vm){		
		return createMayusculasTextField(vm, false);
	}
	
	public static JTextField createMayusculasTextField(final ValueModel vm,boolean commitOnFocusLost){
		UpperCaseField tf=new UpperCaseField();
		Bindings.bind(tf,vm,commitOnFocusLost);
		return tf;
	}
	
	public static class MesRenderer extends DefaultListCellRenderer{
		
		private static String[] meses={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// TODO Auto-generated method stub
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if(value instanceof Number){
				int idd=((Number)value).intValue();
				setText(meses[idd-1]);
			}			
			return this;
		}
		
	}
	
	public static abstract class TransformerRenderer extends DefaultListCellRenderer{
		
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// TODO Auto-generated method stub
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if(value!=null){
				Object newVal=transform(value);
				setText(newVal.toString());
			}
			return this;
		}
		
		protected abstract Object transform(final Object val);
		
	}
	
	
	public static class MesYTodosRenderer extends DefaultListCellRenderer{
		
		private static String[] meses={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre","Todos"};

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// TODO Auto-generated method stub
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if(value instanceof Number){
				int idd=((Number)value).intValue();
				setText(meses[idd-1]);
			}			
			return this;
		}
		
	}
	public static AbstractDialog createPeriodoSelector(final ValueModel periodoModel){
		JFrame f=new JFrame();
		if(Application.isLoaded())
			f=Application.instance().getMainFrame();
		return createPeriodoSelector(periodoModel, f);
	}
	
	public static AbstractDialog createPeriodoSelector(final ValueModel periodoModel,JFrame owner){
		AbstractDialog dialog=new AbstractDialog(owner,"Periodo"){
			
			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				p.add(buildMainPanel(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}
			
			private JComponent buildMainPanel(){
				return createPeriodoBinder(periodoModel);
			}			

			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Periodo","Seleccione el periodo desado");
			}

			protected String getString(String key, String defaultText) {
				return defaultText;
			}
		};
		return dialog;
	}
	
	public static class DiaRenderer extends DefaultListCellRenderer{
		
		private static String[] dias={"Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Diario"};

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {			
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if(value instanceof Number){
				int idd=((Number)value).intValue();
				setText(dias[idd-1]);
			}			
			return this;
		}
		
	}
	
	
	
	/**
     * Creates and returns a formatted text field that is bound 
     * to the Date value of the given ValueModel.
     * The JFormattedTextField is configured with an AbstractFormatter 
     * that uses two different DateFormats to edit and display the Date.
     * A <code>SHORT</code> DateFormat with strict checking is used to edit 
     * (parse) a date; the DateFormatter's default DateFormat is used to
     * display (format) a date. In both cases <code>null</code> Dates are
     * mapped to the empty String. 
     * 
     * @param valueModel  the model that holds the value to be edited
     * @return a formatted text field for Date instances that is bound
     *     to the given value model
     * 
     * @throws NullPointerException if the valueModel is <code>null</code>
     */
    public static JFormattedTextField createDateField(
        ValueModel valueModel) {
        DateFormat shortFormat = new SimpleDateFormat("dd/MM/yyyy");
        shortFormat.setLenient(false);
        
        JFormattedTextField.AbstractFormatter defaultFormatter = 
            new EmptyDateFormatter(shortFormat);
        JFormattedTextField.AbstractFormatter displayFormatter = 
            new EmptyDateFormatter(shortFormat);
        DefaultFormatterFactory formatterFactory = 
            new DefaultFormatterFactory(defaultFormatter, displayFormatter);
        
        return createFormattedTextField(valueModel, formatterFactory);
    }
    
    /**
     * Creates and returns a formatted text field that binds its value 
     * to the given model and converts Strings to values using 
     * Formatters provided by the given AbstractFormatterFactory.
     * 
     * @param valueModel  the model that provides the value
     * @param formatterFactory   provides formatters for different field states
     *     that in turn are used to convert values to a text representation and 
     *     vice versa via <code>#valueToString</code> 
     *     and <code>#stringToValue</code>
     * @return a formatted text field that is bound to the given value model
     * 
     * @throws NullPointerException if the valueModel is <code>null</code>
     */
    public static JFormattedTextField createFormattedTextField(
        ValueModel valueModel,
        JFormattedTextField.AbstractFormatterFactory formatterFactory) {
        JFormattedTextField textField = new JFormattedTextField(formatterFactory);
        textField.setColumns(15);
        Bindings.bind(textField, valueModel);
        return textField;
    }
    
    public static JComboBox createSucursalesBinding(final ValueModel vm){
    	final SelectionInList sl=new SelectionInList(Sucursales.getSucursales(),vm);		
		return BasicComponentFactory.createComboBox(sl);
    }
    
    public static JComboBox createClientesUnionBinder(final ValueModel vm){
    	EventList<Cliente> clientes=GlazedLists.threadSafeList(new BasicEventList<Cliente>());
    	
    	TextFilterator<Cliente> filter=new TextFilterator<Cliente>(){
			public void getFilterStrings(final List baseList, Cliente element) {
				baseList.add(element.getClave());
				baseList.add(element.getNombre());
			}    		
    	};
    	Format format=new Format(){			
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				Cliente c=(Cliente)obj;
				toAppendTo.append(c.getNombre()+"("+c.getClave()+")" );
				return toAppendTo;
			}
			public Object parseObject(String source, ParsePosition pos) {				
				return source;
			}
    		
    	};
    	JComboBox box=createBindingBox(vm, clientes, filter, format);
    	return box;
    }
    
	
	public static void main(String[] args) {
		final Periodo p=new Periodo("01/03/2007","30/05/2007");
		final ValueModel vm=new ValueHolder(p);
		AbstractDialog dialog=createPeriodoSelector(vm);
		dialog.open();
	}
	
	

}
