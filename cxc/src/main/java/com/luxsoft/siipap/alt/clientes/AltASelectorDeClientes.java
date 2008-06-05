package com.luxsoft.siipap.alt.clientes;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;


/**
 * Permite seleccinar un cliente 
 * 
 * @author Ruben Cancino
 *
 */
public class AltASelectorDeClientes extends SXAbstractDialog{
	
	public AltASelectorDeClientes() {
		super("Selector de Clientes");
	}
	
	public AltASelectorDeClientes(final JFrame parent) {
		super(parent,"Selector de Clientes");
	}

	private EventList<ClienteCredito> clientes;
	private FilterList<ClienteCredito> clientesFiltrados;
	private EventSelectionModel<ClienteCredito> selectionModel;
	private JXTable grid;
	private JTextField clave;
	private JTextField nombre;
	
	
	@Override
	protected JComponent buildContent() {
		initGlazedLists();
		initComponents();		
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildFilterPanel(),BorderLayout.NORTH);
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithClose(),BorderLayout.SOUTH);
		return panel;
	}
	
	private void initGlazedLists(){
		clientes=GlazedLists.threadSafeList(new BasicEventList<ClienteCredito>());
		clave=new JTextField();
		final TextFilterator<ClienteCredito> claveFilterator=GlazedLists.textFilterator(new String[]{"cliente.clave"});
		final MatcherEditor<ClienteCredito> claveEditor=new TextComponentMatcherEditor<ClienteCredito>(clave,claveFilterator);
		nombre=new JTextField();
		final TextFilterator<ClienteCredito> nombreFilterator=GlazedLists.textFilterator(new String[]{"cliente.nombre"});
		final MatcherEditor<ClienteCredito> nombreEditor=new TextComponentMatcherEditor<ClienteCredito>(nombre,nombreFilterator);
		
		final EventList<MatcherEditor<ClienteCredito>> editors=new BasicEventList<MatcherEditor<ClienteCredito>>();
		editors.add(claveEditor);
		editors.add(nombreEditor);
		final CompositeMatcherEditor<ClienteCredito> ceditor=new CompositeMatcherEditor<ClienteCredito>(editors);
		ceditor.setMode(CompositeMatcherEditor.AND);
		clientesFiltrados=new FilterList<ClienteCredito>(clientes,ceditor);
	}
	
	private void initComponents(){
		final String[] props={"cliente.clave","cliente.nombre"};
		final String[] cols={"Clave","Nombre"};
		final TableFormat<ClienteCredito> tf=GlazedLists.tableFormat(ClienteCredito.class,props,cols);
		grid=ComponentUtils.getStandardTable();
		grid.setColumnControlVisible(false);
		final SortedList<ClienteCredito> sortedList=new SortedList<ClienteCredito>(clientesFiltrados,null);		
		final EventTableModel<ClienteCredito> tm=new EventTableModel<ClienteCredito>(sortedList,tf);		
		grid.setModel(tm);
		selectionModel=new EventSelectionModel<ClienteCredito>(sortedList);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		grid.setSelectionModel(selectionModel);
		ComponentUtils.addEnterAction(grid, getOKAction());
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					doAccept();
				}
			}			
		});
		grid.getColumn(0).setPreferredWidth(120);
		grid.getColumn(1).setPreferredWidth(450);
		
	}
	
	private JComponent buildGridPanel(){
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	private JComponent buildFilterPanel(){
		FormLayout layout=new FormLayout(
				"l:40dlu,3dlu,40dlu,3dlu,l:40dlu,3dlu,p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append("Clave",clave);
		builder.append("Nombre",nombre);
		return builder.getPanel();
	}
	
	public ClienteCredito getSelected(){
		if(!selectionModel.isSelectionEmpty()){
			return selectionModel.getSelected().get(0);
		}
		return null;
	}
	
	public void clean(){
		clientes.clear();
	}
	
	@Override
	protected void onWindowOpened() {
		load();
	}

	public void load(){
		SwingWorker<List<ClienteCredito>, Object> worker=new SwingWorker<List<ClienteCredito>, Object>(){			
			protected List<ClienteCredito> doInBackground() throws Exception {
				ClienteDao dao=(ClienteDao)ServiceLocator.getDaoContext().getBean("clienteDao");
				return dao.buscarClientesDeCredito();
			}			
			protected void done() {				
				try {
					clientes.addAll(get());
					grid.packAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		TaskUtils.executeSwingWorker(worker);		
	}
	
	public static ClienteCredito seleccionarCliente(final JFrame parent){		
		final AltASelectorDeClientes selector;
		if(parent==null)
			selector=new AltASelectorDeClientes();
		else
			selector=new AltASelectorDeClientes(parent);
		ClienteCredito c=null;
		selector.open();
		if(!selector.hasBeenCanceled()){
			c=selector.getSelected();
		}
		selector.clean();
		return c;
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		ClienteCredito c=seleccionarCliente(null);
		if(c!=null)
			System.out.println(c);
	}
	

}
