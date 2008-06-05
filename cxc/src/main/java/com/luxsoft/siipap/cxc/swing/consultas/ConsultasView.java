package com.luxsoft.siipap.cxc.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;

import org.jdesktop.swingx.VerticalLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.DockingUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class ConsultasView extends AbstractView {

	private RootWindow rootWindow;
	private ApplicationContext consultasCtx;
	private Map<String, ConsultaView> consultas=new HashMap<String, ConsultaView>();
	//private Set<ConsultaView> consultas=new HashSet<ConsultaView>();
	

	@Override
	protected JComponent buildContent() {
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				buildNavegatorPanel(), buildConsutlasPanel());			
		return sp;
	}
	

	private JComponent buildNavegatorPanel() {
		//final JPanel panel=new JPanel(new VerticalLayout());		
		final DefaultTreeModel model=new DefaultTreeModel(createRootNode(),true);
		final JTree tree = new JTree(model);
		tree.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){					
					Object node=tree.getLastSelectedPathComponent();
					load(node);
				}
			}
			
		});
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new ConsultasTreeRenderer());
		final JScrollPane sp=new JScrollPane(tree);
		//panel.add(sp,BorderLayout.CENTER);
		return sp;
	}
	
	

	private JComponent buildConsutlasPanel() {
		ViewMap map = new ViewMap();
		rootWindow = DockingUtil.createRootWindow(map, true);
		DockingUtils.configRootWindow(rootWindow);
		DockingUtils.configTabWindowProperties(rootWindow);
		rootWindow.addListener(new ViewHandler());
		rootWindow.setPreferredSize(new Dimension(300,250));
		return rootWindow;
	}
	
	public DefaultMutableTreeNode createRootNode(){
		DefaultMutableTreeNode root=new DefaultMutableTreeNode("Directorio",true);
		
		//Consultas
		final DefaultMutableTreeNode consultasNode=new DefaultMutableTreeNode("Consultas",true);
		root.add(consultasNode);
		for(String s:buscarConsutlasIds()){
			ConsultaNode node=new ConsultaNode(s);
			consultasNode.add(node);
		}
		
		final DefaultMutableTreeNode reportesNode=new DefaultMutableTreeNode("Reportes",true);
		root.add(reportesNode);
		
		return root;
	}
	
	private String[] buscarConsutlasIds(){
		if(consultasCtx==null){
			consultasCtx=new ClassPathXmlApplicationContext("classpath:com/luxsoft/siipap/cxc/swing/consultas/consultas-ctx.xml");
		}
		final String[] consutlas=consultasCtx.getBeanDefinitionNames();
		return consutlas;
	}
	
	private void load(Object node){
		if(node==null) return;
		final String viewId=node.toString();
		final AbstractView vx=(AbstractView)consultasCtx.getBean(viewId);
		vx.setId(viewId);
		vx.setLabel(viewId);
		ConsultaView view=consultas.get(viewId);		
		if(view==null){
			System.out.println("Creando una nueva consulta");
			view=new ConsultaView(vx);
			consultas.put(viewId, view);
		}
		DockingUtil.addWindow(view, rootWindow);
	}
	
	

	@Override
	public void close() {
		consultasCtx=null;
		for(ConsultaView v:consultas.values()){
			System.out.println("Cerrando consulta: "+v.getTitle());
			v.close();
		}
		consultas.clear();
		
	}


	public static class ConsultaNode extends DefaultMutableTreeNode{
				

		public ConsultaNode(Object userObject) {
			super(userObject,false);
			
		}
		
	}
	
	
	
	private class ViewHandler extends DockingWindowAdapter{

		@Override
		public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
			System.out.println("Window added to: "+addedToWindow.getTitle());
			System.out.println("Window added:     "+addedWindow.getTitle());
		}

		@Override
		public void windowClosed(final DockingWindow window) {
			
		}

		@Override
		public void windowClosing(DockingWindow window) throws OperationAbortedException {			
			System.out.println("Window closing "+window.getTitle());
		}

		@Override
		public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow) {
			System.out.println("Window removed from: "+removedFromWindow.getTitle());
			System.out.println("Window removed: "+removedWindow.getTitle());
		}

		@Override
		public void windowRestored(DockingWindow window) {
			System.out.println("Window restored: "+window.getTitle());
		}

		@Override
		public void windowRestoring(DockingWindow window) throws OperationAbortedException {
			System.out.println("Window restoring "+window.getTitle());
		}
		
	}
	
	public class ConsultaView extends View{
		
		private AbstractView view;

		public ConsultaView(AbstractView v) {
			super(v.getLabel(), null, v.getContent());
			this.view=v;
		}
		public AbstractView getView() {
			return view;
		}
		@Override
		public void close() {
			view.close();
			super.close();
		}
		
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		final ConsultasView p=new ConsultasView();
		SXAbstractDialog dialog = new SXAbstractDialog("Prueba") {

			@Override
			protected JComponent buildContent() {
				p.open();
				JComponent c=p.getContent();
				
				return c;
			}

			@Override
			protected void setResizable() {
				setResizable(true);
			}
			
			

		};

		dialog.open();

	}
	

}
