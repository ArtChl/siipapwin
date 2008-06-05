package com.luxsoft.siipap.inventarios.swing.consultas;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.ActionLabel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.Salida;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.BrowserUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Información de movimientos de tipo XCO,DEC
 * permite vincular con un movimiento de entrada COM
 * 
 * @author Ruben Cancino
 *
 */
public class SalidaDecForm extends SXAbstractDialog{
	
	private JXHeader header;
	private final Salida salida;
	private final PresentationModel model;
		
	private Action seleccionarCom;
	
	private JFormattedTextField comNumer;
	private JTextField comSucursal;
	private JTextField comArtic;
	private JTextField comDesc;
	private JTextField comCantidad;

	public SalidaDecForm(Salida salida) {
		super("Salida de Inventario");
		this.salida=salida;
		model=new PresentationModel(salida);
		
	}
	
	private void initComponents(){
		NumberFormat nf=NumberFormat.getIntegerInstance();
		NumberFormatter nff=new NumberFormatter(nf);
		nff.setValueClass(Integer.class);
		comNumer=new JFormattedTextField(nff);
		
		comNumer.addPropertyChangeListener("value",new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Cambio el valor");
				if(evt.getNewValue()==null ){
					salida.setDestino(null);
					updateCom();
				}else{
					Integer val=(Integer)evt.getNewValue();
					if(val.intValue()==0){
						salida.setDestino(null);
						updateCom();
					}
				}
			}
		});
		comSucursal=getComponent("");
		comArtic=getComponent("");
		comDesc=getComponent("");
		comCantidad=getComponent("");
	}
	
	private void initActions(){
		seleccionarCom=new com.luxsoft.siipap.swing.actions.DispatchingAction(this,"seleccionar");
		seleccionarCom.putValue(Action.NAME, "Com");
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout());
		initActions();
		initComponents();
		FormLayout layout=new FormLayout(
				"l:p,3dlu,max(p;60dlu), 2dlu," +
				"l:p,3dlu,max(p;60dlu)"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.appendSeparator("Salida");
		
		builder.nextLine();
		addSalidaComponets(builder);
		builder.appendSeparator("Compra");
		ActionLabel com=new ActionLabel("Com");
		com.setAction(seleccionarCom);
		
		builder.append(com,comNumer);
		builder.append("Sucursal",comSucursal,true);
		builder.append("Articulo",comArtic,true);
		builder.append("Descripcion",comDesc,5);
		builder.nextLine();
		builder.append("Cantidad",comCantidad);
		if(salida.getDestino()!=null)
			updateCom();
		panel.add(builder.getPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	protected void addSalidaComponets(final DefaultFormBuilder builder){
		builder.append("Fecha",getComponent("ALMFECHA"),true);
		builder.append("Numero",getComponent("ALMNUMER"));
		builder.append("Sucursal",getComponent("ALMSUCUR"),true);
		builder.append("Articulo",getComponent("ALMARTIC"),true);
		builder.append("Descripcion",getComponent("ALMNOMBR"),5);
		builder.nextLine();
		builder.append("Cantidad",getComponent("cantidad"),true);
	}
	
	protected JTextField getComponent(final String property){
		JTextField c=null;
		if("ALMFECHA".equals(property)){
			JFormattedTextField tf =new JFormattedTextField(new DateFormatter(new SimpleDateFormat("dd/MM/yyyy")));
			tf.setValue(salida.getALMFECHA());
			c=tf;
		}else{
			try {		
				c=new JTextField();
				c.setText(model.getValue(property).toString());
							
			} catch (Exception e) {
				c=new JTextField(10);			
				ValidationComponentUtils.setWarningBackground(c);
			}
		}
		
		c.setHorizontalAlignment(SwingConstants.RIGHT);
		c.setEditable(false);
		c.setFocusable(false);
		return c;
	}
	
	@Override
	protected JComponent buildHeader() {
		header=new JXHeader(
				"Devolucion de compra  "
				,"Salida de inventario DEC/XCO"
				);
		return header;
	}
	
	private void updateCom(){
		if(salida.getDestino()==null){
			comNumer.setText("");
			comSucursal.setText("");
			comArtic.setText("");
			comDesc.setText("");
			comCantidad.setText("");
			return;
		}
		try {
			comNumer.setText(salida.getDestino().getALMNUMER().toString());
			comSucursal.setText(salida.getDestino().getALMSUCUR().toString());
			comArtic.setText(salida.getDestino().getALMARTIC());
			comDesc.setText(salida.getDestino().getALMNOMBR());
			comCantidad.setText(String.valueOf(salida.getDestino().getCantidad()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void seleccionar(){
		SelectorDeComs selector=new SelectorDeComs(buscarComs());
		selector.open();
		if(!selector.hasBeenCanceled()){
			Entrada com=selector.getSelected();						
			salida.setDestino(com);			
			updateCom();
		}
		selector.dispose();
	}
	
	public List<AnalisisDeEntrada> buscarComs(){
		return new ArrayList<AnalisisDeEntrada>();
	}
	
	
	
	private class SelectorDeComs extends SXAbstractDialog{
		
		private final EventList<AnalisisDeEntrada> coms;
		private EventSelectionModel<AnalisisDeEntrada> selectionModel;
		private JXTable grid;
		private JTextField input;

		public SelectorDeComs(final List<AnalisisDeEntrada> coms) {
			super("Entradas tipo COM");
			this.coms=GlazedLists.threadSafeList(new BasicEventList<AnalisisDeEntrada>());
			this.coms.addAll(coms);
			
		}

		@Override
		protected JComponent buildContent() {
			JPanel p=new JPanel(new BorderLayout(5,5));
			input=new JTextField(20);
			p.add(BrowserUtils.buildTextFilterPanel(input),BorderLayout.NORTH);
			p.add(buildGridPanel(),BorderLayout.CENTER);
			return p;
		}
		
		private JComponent buildGridPanel(){			
			TextFilterator<AnalisisDeEntrada> filterator=GlazedLists.textFilterator(new String[]{"PROVCLAVE","PROVNOMBR","COM"});
			MatcherEditor<AnalisisDeEntrada> editor=new TextComponentMatcherEditor<AnalisisDeEntrada>(input,filterator);
			FilterList<AnalisisDeEntrada> filterList=new FilterList<AnalisisDeEntrada>(coms,new ThreadedMatcherEditor<AnalisisDeEntrada>(editor));
			
			
			grid=ComponentUtils.getStandardTable();
			final String[] props={"clave","descripcion","unidad","PROVCLAVE","PROVNOMBR","COM","FENT","FACREM","ingresada","analizadoCXP","analizadoHojas","analizadoBobinas","porAnalizar"};
			final String[] cols={"Artículo","Descripción","Unidad","Proveedor","Nombre","Com","Fecha","Facrem","Ingresadas","CXP","MAQ HOJ","MAQ BOB","Por Analizar"};
			TableFormat<AnalisisDeEntrada> tf=GlazedLists.tableFormat(AnalisisDeEntrada.class, props,cols);
			final SortedList<AnalisisDeEntrada> sortedList=new SortedList<AnalisisDeEntrada>(filterList,null);
			EventTableModel<AnalisisDeEntrada> tm=new EventTableModel<AnalisisDeEntrada>(sortedList,tf);			
			selectionModel=new EventSelectionModel<AnalisisDeEntrada>(sortedList);
			selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
			grid.setModel(tm);
			grid.setSelectionModel(selectionModel);			
			grid.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()==2)
						doAccept();
				}				
			});
			grid.packAll();
			new TableComparatorChooser<AnalisisDeEntrada>(grid,sortedList,true);
			ComponentUtils.addEnterAction(grid, getOKAction());
			JScrollPane sp=new JScrollPane(grid);	
			
			return sp;
		}
		
		public Entrada getSelected(){
			if(!selectionModel.isSelectionEmpty()){
				return selectionModel.getSelected().get(0).getCom();
			}
			return null;
			
		}
		
			
		public void clean(){
			coms.clear();
		}
		
	}
	
	public static void main(String[] args) {
		Salida s=new Salida();
		s.setALMNUMER(12524L);
		s.setALMARTIC("POL74");
		s.setALMNOMBR("PPOOLL");
		s.setALMSUCUR(2);
		s.setALMCANTI(452L);
		SalidaDecForm form=new SalidaDecForm(s);
		form.open();
	}

}
