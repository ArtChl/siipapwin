package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.swing.binding.ClienteBinding;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

/**
 * Interfaz grafica para 
 * 
 * 	a- La generacion de NC por descuento en forma automatica
 *  b- La impresion de las notas de credito
 *  
 * @author Ruben Cancino
 *
 */
public class NCDescuento extends SXAbstractDialog{
	
	
	private JFormattedTextField cliente;	
	private JFormattedTextField numero;
	private JXDatePicker fecha;
	private JXTable grid1;
	private JXTable grid2;
	private JButton load;
	private JCheckBox box;
	private ClienteBinding clienteB;
	
	
	private final NCDescuentoModel model;

	public NCDescuento() {
		super("Notas de Crédito por descuento");
		model=new NCDescuentoModel();
		
	}

	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel(
				"Notas de Crédito por descuento",
				"Generación automatica de notas de credito por descuento",
				getIconFromResource("images/siipapwin/cxc64.gif")
				);
	}

	private void initComponents(){
		fecha=new JXDatePicker();
		fecha.setFormats(new String[]{"dd/MM/yyyy","dd/MM/yy"});
		cliente=new JFormattedTextField();
		
		numero=new JFormattedTextField();
		load=new JButton(CommandUtils.createLoadAction(this, "load"));
		box=new JCheckBox("Por cobranza",true);
		box.addItemListener(new CobranzaHandler());
		load.setToolTipText("Cargar datos");
		clienteB=new ClienteBinding(model.getModel("cliente"));
		//clienteC=clienteB.getControl();
	}	
	
	private void initEventHandling(){
		 
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		initEventHandling();
		final JPanel panel=new JPanel(new BorderLayout());
		FormLayout layout = new FormLayout(
                "fill:p:grow",
                "pref, 6dlu, pref, 3dlu, p");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();
        builder.add(buildEditorPanel(), cc.xy(1, 3));
        builder.add(buildGridPanel(), cc.xy(1, 5));
        panel.add(builder.getPanel(),BorderLayout.CENTER);
        panel.add(buildButtonBarWithOKCancelApply(),BorderLayout.SOUTH);
        return panel;
	}
	
	protected JComponent buildEditorPanel() {
		
		FormLayout layout=new FormLayout(
				" l:max(50;p),3dlu,70dlu,3dlu" +
				",l:max(50;p),3dlu,max(p;150dlu)"
				,""); //NO ROWS
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		builder.append("Fecha ",fecha);
		builder.append(load);
		builder.append(box);
		builder.nextLine();
		builder.append("Consecutivo",numero);
		builder.nextLine();
		builder.append("Cliente ",cliente,true);
		builder.append("Cliente ",clienteB.getControl(),5);
		
		return builder.getPanel();
	}
	
	private JComponent buildGridPanel(){	
		FormLayout layout=new FormLayout("250dlu,3dlu,350dlu","p");
		PanelBuilder pb=new PanelBuilder(layout);		
		CellConstraints cc=new CellConstraints();
		pb.add(buildNotasDeCreditoGrid(),cc.xy(1, 1));
		pb.add(buildNotasDeCreditoDetGrid(),cc.xy(3, 1));		
		return pb.getPanel();
	}
	
	private EventList<NotasDeCreditoDet> notasDet;
	private EventList<NotaDeCredito> notas;
	
	private JComponent buildNotasDeCreditoDetGrid(){
		notasDet=new BasicEventList<NotasDeCreditoDet>();
		final String[] props1=new String[]{"factura.sucursal","factura.tipo","factura.numero","factura.fecha","factura.total","factura.saldo","descuento","importe"};
		final String[] cols1=new String[]{"Sucursal","Tipo","Número","Fecha","Total(F)","Saldo","Descuento","Importe"};
		
		final TableFormat<NotasDeCreditoDet> tf1=GlazedLists.tableFormat(NotasDeCreditoDet.class,props1,cols1);
		final EventTableModel<NotasDeCreditoDet> tm1=new EventTableModel<NotasDeCreditoDet>(notasDet,tf1);
		grid2=ComponentUtils.getStandardTable();
		grid2.setModel(tm1);		
		final JScrollPane sp1=new JScrollPane(grid2);	
		return sp1;
	}
	
	private JComponent buildNotasDeCreditoGrid(){
		
		notas=new BasicEventList<NotaDeCredito>();
		
		final TextFilterator<NotaDeCredito> clienteFilterator=GlazedLists.textFilterator(new String[]{"clave"});
		final MatcherEditor<NotaDeCredito> clienteEditor=new TextComponentMatcherEditor<NotaDeCredito>(cliente,clienteFilterator);
		final FilterList<NotaDeCredito> clientesList=new FilterList<NotaDeCredito>(notas,clienteEditor);		
		
		final String[] props1=new String[]{"numero","cliente.nombre","importe"};
		final String[] cols1=new String[]{"Numero","Cliente","Importe"};		
		final TableFormat<NotaDeCredito> tf1=GlazedLists.tableFormat(NotaDeCredito.class,props1,cols1);
		final EventTableModel<NotaDeCredito> tm1=new EventTableModel<NotaDeCredito>(clientesList,tf1);		
		grid1=ComponentUtils.getStandardTable();
		grid1.setModel(tm1);		
		final JScrollPane sp1=new JScrollPane(grid1);	
		return sp1;
		
	}
	
	 
	public void load(){
		
	}
	
	private class CobranzaHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e) {
			if(box.isSelected()){				
				cliente.setEnabled(true);
				model.setValue("cliente", null);				
			}else{
				cliente.setText("");
				cliente.setEnabled(false);
				
				
			}
		}
		
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		NCDescuento gd=new NCDescuento();
		gd.open();
		System.exit(0);
	}
	
	

}
