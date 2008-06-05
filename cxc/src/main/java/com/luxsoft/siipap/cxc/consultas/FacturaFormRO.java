package com.luxsoft.siipap.cxc.consultas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * Forma que presenta informacion de la factura solo para lectura
 * 
 * @author Ruben Cancino
 *
 */
public class FacturaFormRO extends SXAbstractDialog{
	
	
	private final FacturaFormROModel model;
	
	private EventList<NotasDeCreditoDet> notas;
	private EventList<Pago> pagos;
	
	private JXTable pagosGrid;
	private JXTable notasGrid;
	private JFormattedTextField numero;
	private JFormattedTextField numeroFiscal;
	private JComponent fecha;
	private JFormattedTextField total;
	private JFormattedTextField saldo;
	private JCheckBox recibida=new JCheckBox();
	private JTextField frecibida=new JTextField(10);
	private JCheckBox revisada=new JCheckBox();
	private JTextField frevisada=new JTextField(10);
	private JTextField facturista;
	private JTextField cortes;
	private JTextField kilos;
	
	
	public FacturaFormRO(final FacturaFormROModel model) {
		super("Consulta de facturas");
		this.model=model;
	}
	
	@Override
	protected void build() {
		setUndecorated(true);		
		super.build();
	}

	private void initComponents(){		
		numero=Binder.createNumberBinding(model.getFacturaModel().getComponentModel("numero"),0);
		numero.setEnabled(false);
		fecha=BasicComponentFactory.createDateField(model.getFacturaModel().getComponentModel("fecha"));
		fecha.setFocusable(false);
		total=Binder.createCantidadMonetariaBinding(model.getFacturaModel().getComponentModel("total"));
		total.setFocusable(false);
		saldo=Binder.createMonetariNumberBinding(model.getFacturaModel().getComponentModel("saldo"));
		saldo.setFocusable(false);
		facturista=BasicComponentFactory.createTextField(model.getFacturaModel().getModel("facturista"));
		facturista.setEnabled(false);
		cortes=Binder.createNumberBinding(model.getFacturaModel().getModel("cortes"), 0);
		cortes.setEnabled(false);
		kilos=Binder.createNumberBinding(model.getFacturaModel().getModel("cantidadEnKilos"), 3);
		kilos.setEnabled(false);
		numeroFiscal=Binder.createNumberBinding(model.getFacturaModel().getComponentModel("numeroFiscal"),0);
		numeroFiscal.setFocusable(false);
	}
	
	protected JComponent buildContentPane(){
		JComponent c=super.buildContentPane();
		c.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		c.setOpaque(false);
		return c;
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		JTabbedPane tabPanel=new JTabbedPane();
		
		JPanel panel=new JPanel(new BorderLayout());		
		panel.setLayout(new BorderLayout());
		panel.add(buildFormPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithClose(),BorderLayout.SOUTH);
		
		tabPanel.addTab("General", panel);
		tabPanel.addTab("Detalle", buildPartidasView());
		return tabPanel;
	}
	
	protected JComponent buildFormPanel(){
		return buildEditorPanel();		
	}
	
	private JComponent buildEditorPanel(){
		FormLayout layout=new FormLayout(
				"l:40dlu,2dlu,50dlu, 2dlu, " +
				"l:40dlu,2dlu,50dlu, 2dlu, " +
				"l:40dlu,2dlu,50dlu:g"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		builder.append("Numero",numero);
		builder.append("N.Fiscal",numeroFiscal);
		builder.append("Fecha",fecha);
		
		builder.append("Total",total);
		builder.append("Saldo",saldo,true);
		
		builder.append("Facturista",facturista);
		builder.append("Kilos",kilos);
		builder.append("Cortes",cortes);		
		
		
		builder.append("Recibida",recibida);
		builder.append("F. Rec",frecibida,true);
		builder.append("Revisada",revisada);
		builder.append("F. Rev",frevisada);
		builder.nextLine();
		builder.appendSeparator("Notas de Credito");
		builder.nextLine();
		builder.append(buildNotasGrid(),11);		
		builder.appendSeparator("Pagos");		
		builder.append(buildPagosGrid(),11);
		JPanel p=builder.getPanel();
		p.setEnabled(false);
		return p;
	}
	
	private JComponent buildNotasGrid(){
		final String[] props={"nota.id","nota.numero","tipo","serie","fecha","descuento","importe"};
		final String[] cols={"Id","numero","tipo","serie","Fecha","Desc(%)","importe"};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class,props, cols);
		notas=GlazedLists.threadSafeList(new BasicEventList<NotasDeCreditoDet>());
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(notas,tf);
		notasGrid=ComponentUtils.getStandardTable();
		notasGrid.setModel(tm);
		JComponent c=UIFactory.createTablePanel(notasGrid);
		c.setPreferredSize(new Dimension(300,80));
		return c;		
	}
	
	
	
	private JComponent buildPagosGrid(){
		
		final String[] props={"id","formaDePago","referencia","fecha","importe","cuentaDestino","comentario","notaDelPago"};
		final String[] cols={"id","F.Pago","Referencia","Fecha","importe","Cta Dep","comentario","notaDelPago"};
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class,props, cols);
		pagos=GlazedLists.threadSafeList(new BasicEventList<Pago>());
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(pagos,tf);
		final EventSelectionModel<Pago> selection=new EventSelectionModel<Pago>(pagos);
		selection.setSelectionMode(ListSelection.SINGLE_SELECTION);
		pagosGrid=ComponentUtils.getStandardTable();
		JXTable grid=pagosGrid;
		grid.setModel(tm);
		grid.setSelectionModel(selection);
		final Action viewAction =new AbstractAction("view"){
			public void actionPerformed(ActionEvent e) {
				if(!selection.getSelected().isEmpty()){
					view(selection.getSelected().get(0));
				}
				
			}			
		};
		ComponentUtils.addEnterAction(grid, viewAction);
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					if(!selection.getSelected().isEmpty()){
						view(selection.getSelected().get(0));
					}
				}
			}			
		});
		final JComponent c=UIFactory.createTablePanel(grid);
		c.setPreferredSize(new Dimension(350,100));
		return c;		
	}
	
	private JComponent buildPartidasView(){
		final EventList<VentaDet> partidas=GlazedLists.eventList(model.getFactura().getPartidas());
		final SortedList<VentaDet> sorted=new SortedList<VentaDet>(partidas,null);
		final String[] props={"clave","descripcion","cantidad","devueltos","precioFacturado","importeNeto","kilos"};
		final String[] names={"Articulo","Desc","Cantidad","Dev","Precio","Importe","Kg"};
		final TableFormat<VentaDet> tf=GlazedLists.tableFormat(VentaDet.class, props,names);
		final EventTableModel<VentaDet> tm=new EventTableModel<VentaDet>(sorted,tf);
		
		final JXTable grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		
		new TableComparatorChooser<VentaDet>(grid,sorted,true);
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	/**
	 * Revisar un pago en modo de solo lectura
	 * 
	 * @param p
	 */
	private void view(Pago p){
		System.out.println("Mirando pago: "+p);
	}
	
	@Override
	protected JComponent buildHeader() {
		JComponent c= new HeaderPanel(model.getFacturaModel().getValue("clave").toString()
				,model.getFacturaModel().getValue("nombre").toString()
				,getIconFromResource("images/siipapwin/cxc64.gif"));		
		return c;
	}
	
	@Override
	protected void onWindowOpened() {
		load();
	}
	
	public void load(){
		notas.clear();
		pagos.clear();
		notas.addAll(model.getNotas());
		pagos.addAll(model.getPagos());
		pagosGrid.packAll();
		notasGrid.packAll();
		
		if(model.getFactura().getCredito()!=null){
			final DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
			recibida.setSelected(model.getFactura().getCredito().isRecibidaCXC());
			
			if(model.getFactura().getCredito().isRecibidaCXC())
				frecibida.setText(df.format(model.getFactura().getCredito().getFechaRecepcionCXC()));
			revisada.setSelected(model.getFactura().getCredito().isRevisada());
			if(model.getFactura().getCredito().isRevisada())
				frevisada.setText(df.format(model.getFactura().getCredito().getFechaRevisionCxc()) );
			
		}
		
		
		
		
	}

	public static void main(String[] args) {
		final Venta v=DatosDePrueba.buscarVenta(2712184L);
		//final Venta v=DatosDePrueba.ventasDePrueba().get(0);
		final FacturaFormROModel model=new FacturaFormROModel(v);
		final FacturaFormRO dialog=new FacturaFormRO(model);
		dialog.open();
		
		/*
		List<NotasDeCreditoDet> dets=ServiceLocator.getCXCManager().buscarNotas(v);
		for(NotasDeCreditoDet det:dets){
			det.getNota();
			System.out.println("Nota:" +det.getNota().getNumero());
		}
		*/
	}

}
