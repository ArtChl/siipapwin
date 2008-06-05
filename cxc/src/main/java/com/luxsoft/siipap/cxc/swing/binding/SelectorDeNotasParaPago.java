package com.luxsoft.siipap.cxc.swing.binding;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Selector de notas de credito con saldo. Este se ocupa para localizar notas
 * que pueden ser usadas para realizar un pago con forma de pago tipo T
 * 
 * @author Ruben Cancino
 *
 */
public class SelectorDeNotasParaPago extends SXAbstractDialog{
	
	private JTable grid;
	private EventList<NotaDeCredito> source;
	private EventSelectionModel<NotaDeCredito> selectionModel;
	private final String cliente;
	 
	
	public SelectorDeNotasParaPago(final String cliente) {
		super("Notas para pago : "+cliente);
		Assert.isTrue(!StringUtils.isBlank(cliente));
		this.cliente=cliente;
		addWindowListener(LoadHandler);
	}


	

	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	@SuppressWarnings("unchecked")
	private void initComponents(){
		source=new BasicEventList<NotaDeCredito>();
		SortedList<NotaDeCredito> sortedList=new SortedList<NotaDeCredito>(source,null);
		
		String[] props={"id","serie","tipo","numero","fecha","saldo"};
		String[] labels={"Id","Serie","Tipo","Numero","Fecha","Saldo"};
		TableFormat<NotaDeCredito> tf=GlazedLists.tableFormat(props, labels);
		EventTableModel<NotaDeCredito> tm=new EventTableModel<NotaDeCredito>(sortedList,tf);		
		selectionModel=new EventSelectionModel<NotaDeCredito>(sortedList);
		grid=new JTable(tm);
		grid.setSelectionModel(selectionModel);
		grid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		grid.getActionMap().put("select",new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				doAccept();
			}
		});
		grid.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"select");
		grid.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					doAccept();
				}
			}
		});
		setGridColumns(grid.getColumnModel());
		new TableComparatorChooser(grid,sortedList,true);		
	}
	
	private void setGridColumns(final TableColumnModel tm){
		tm.getColumn(0).setPreferredWidth(40);
		tm.getColumn(1).setPreferredWidth(40);
		tm.getColumn(2).setPreferredWidth(70);
		tm.getColumn(3).setPreferredWidth(70);
		tm.getColumn(4).setPreferredWidth(70);
	}
	
	private JComponent buildGridPanel(){
		JComponent c=UIFactory.createTablePanel(grid);
		c.setPreferredSize(new Dimension(350,200));
		return c;
	}
	
	
	public EventList<NotaDeCredito> getSelection(){
		return  getSelectionModel().getSelected();
	}
	
	public NotaDeCredito getSelected(){
		if(getSelection().isEmpty()) return null;
		return getSelection().get(0);
	}
	
	public void load(){
		SwingWorker<List<NotaDeCredito>, String> worker=new SwingWorker<List<NotaDeCredito>, String>(){

			@Override
			protected List<NotaDeCredito> doInBackground() throws Exception {				
				return getDao().buscarNotasConSaldo(cliente);
			}
			@Override
			protected void done() {
				try{
					source.getReadWriteLock().writeLock().lock();
					source.clear();
					source.addAll(get());
					System.out.println("Notas cargadas: "+source.size());
				}catch (Exception e) {
					logger.error(e);
				}finally{
					source.getReadWriteLock().writeLock().unlock();
				}
			}			
			
		};
		TaskUtils.executeSwingWorker(worker);
	}

	public void disposeGlazedList(){
		source.clear();		
	}

	public EventSelectionModel<NotaDeCredito> getSelectionModel() {
		return selectionModel;
	}

	public NotaDeCreditoDao getDao() {
		return (NotaDeCreditoDao)ServiceLocator.getDaoContext().getBean("notasDeCreditoDao");
	}
	
	private WindowAdapter LoadHandler =new WindowAdapter(){
		@Override
		public void windowOpened(WindowEvent e) {
			load();
		}
		
	};

}
