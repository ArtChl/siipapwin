package com.luxsoft.siipap.cxc.depositos;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.luxsoft.siipap.cxc.dao.DepositosDao;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.dao2.UniversalDao;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;
import com.luxsoft.siipap.swing.views2.DefaultTaskView;
import com.luxsoft.siipap.swing.views2.InternalTaskTab;

public class DepositosView extends DefaultTaskView{
	
	private DepositosDao depositosDao;
	
	private DepositosPorDiaView porDiaView;
	private InternalTaskTab porDiaTab;
	

	public void showDepositosPorDia(){
		if(porDiaView==null){
			porDiaView=new DepositosPorDiaView();
			porDiaTab=new InternalTaskTab(porDiaView);
		}
		addTab(porDiaTab);
		porDiaView.load();
		
	}
	
	public void open() {
		showDepositosPorDia();
	}
	
	
	
	
	public class DepositosPorDiaView extends AbstractInternalTaskView{
		
		private Action loadAction;
		private Action insertAction;
		private Action viewAction;
		private Action editAction;
		private Action deleteAction;
		
		private JXTable grid;
		private EventList<Deposito> source;
		private SortedList<Deposito> sortedList;
		private EventSelectionModel<Deposito> selectionModel;

		public JComponent getControl() {
			final JPanel panel=new JPanel(new BorderLayout());
			
			grid=ComponentUtils.getStandardTable();
			
			source=GlazedLists.threadSafeList(new BasicEventList<Deposito>());
			sortedList=new SortedList<Deposito>(getFilterList(source),null);
			final String[] props={"id","fecha","banco","cuenta","importe"};
			final String[] names={"Id","Fecha","Banco","Cuenta","Total"};
			final TableFormat<Deposito> tf=GlazedLists.tableFormat(Deposito.class,props,names);
			final EventTableModel<Deposito> tm=new EventTableModel<Deposito>(sortedList,tf);
			selectionModel=new EventSelectionModel<Deposito>(sortedList);
			
			grid.setModel(tm);
			grid.setSelectionModel(selectionModel);
			grid.addMouseListener(new MouseAdapter(){				
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()==2){
						view();
					}
				}				
			});
			ComponentUtils.decorateActions(grid);	
			ComponentUtils.addEnterAction(grid, viewAction);
			new TableComparatorChooser<Deposito>(grid,sortedList,true);
			
			final JScrollPane sp=new JScrollPane(grid);
			panel.add(sp,BorderLayout.CENTER);
			return panel;
		}
		
		private EventList<Deposito> getFilterList(EventList<Deposito> data){
			return data;
		}
		
		@Override
		public void instalOperacionesAction(JXTaskPane operaciones) {
			if(loadAction==null){
				loadAction=CommandUtils.createLoadAction(this, "load");
			}
			if(insertAction==null){
				insertAction=CommandUtils.createInsertAction(this, "insert");
			}if(viewAction==null){
				viewAction=CommandUtils.createViewAction(this, "view");
			}if(editAction==null){
				editAction=CommandUtils.createEditAction(this, "edit");
			}if(deleteAction==null){
				deleteAction=CommandUtils.createDeleteAction(this, "delete");
			}
			operaciones.add(loadAction);
			operaciones.add(insertAction);
			operaciones.add(editAction);
			operaciones.add(deleteAction);
			operaciones.add(viewAction);
		}

		public void load(){
			SwingWorker<List<Deposito>, String> worker=new SwingWorker<List<Deposito>, String>(){				
				@SuppressWarnings("unchecked")
				protected List<Deposito> doInBackground() throws Exception {					
					return getDepositosDao().buscarDepositos("CRE");
				}
				
				protected void done() {
					try {
						List<Deposito> res=get();
						source.clear();
						source.addAll(res);
						grid.packAll();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			};
			TaskUtils.executeSwingWorker(worker);
		}
		
		public void insert(){
			final DepositoFormModel model=new DepositoFormModel();
			DepositosForm form=new DepositosForm(model);
			form.open();
			if(!form.hasBeenCanceled()){
				model.commit();
				final Deposito next=getDepositosDao().save(model.getDeposito());
				source.add(next);
			}
		}
		
		public void view(){
			if(!selectionModel.getSelected().isEmpty()){
				Deposito selected=selectionModel.getSelected().get(0);
				Deposito d=getDepositosDao().buscarDeposito(selected.getId());
				final DepositoFormModel model=new DepositoFormModel(d);
				model.setReadOnly(true);
				final DepositosForm form=new DepositosForm(model);
				form.open();
			}
		}
		
		public void edit(){
			if(!selectionModel.getSelected().isEmpty()){
				Deposito selected=selectionModel.getSelected().get(0);
				Deposito d=getDepositosDao().buscarDeposito(selected.getId());
				final DepositoFormModel model=new DepositoFormModel(d);				
				final DepositosForm form=new DepositosForm(model);
				form.open();
				if(!form.hasBeenCanceled()){
					model.commit();
					getDepositosDao().save(model.getDeposito());
					int index=source.indexOf(selected);
					source.set(index, model.getDeposito());
				}
			}
		}
		
		public void delete(){
			if(!selectionModel.getSelected().isEmpty()){
				final List<Deposito> selected=new ArrayList<Deposito>();
				selected.addAll(selectionModel.getSelected());
				if(MessageUtils.showConfirmationMessage("Eliminar los registros seleccionados? "+selected.size(), "Depositos")){
					for(Deposito d:selected){
						getDepositosDao().remove(d.getId());
						source.remove(d);
					}
				}
				
			}
		}
	
		
	}




	public DepositosDao getDepositosDao() {
		return depositosDao;
	}

	public void setDepositosDao(DepositosDao depositosDao) {
		this.depositosDao = depositosDao;
	}

}
