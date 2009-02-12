package com.luxsoft.siipap.cxc.consultas;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.util.Assert;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uifextras.util.ActionLabel;


import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;

/**
 * Extiend BrowserPanel para facilitar la generacion
 * del panel de filtrado. Adicionalmente aumenta las acciones
 * para generando template methods para Altas/Bajas/Cambios 
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public abstract class FilteredBrowserPanel<E> extends BrowserPanel{
	
	private String[] properties;
	private String[] labels;
	protected final Class<E> beanClazz;
	
	private String title;
	private boolean defaultPanel=false;

	public FilteredBrowserPanel(Class<E> beanClazz) {
		this.beanClazz = beanClazz;
		init();
	}
	
	protected void init(){
		
	}
	
	
	
	
	public boolean isDefaultPanel() {
		return defaultPanel;
	}

	public void setDefaultPanel(boolean defaultPanel) {
		this.defaultPanel = defaultPanel;
	}

	@Override
	protected TableFormat buildTableFormat() {
		Assert.notEmpty(properties);
		if(labels==null)
			labels=this.properties;
		return GlazedLists.tableFormat(beanClazz,getProperties(), getLabels());
	}
	
	/**
	 * Comodity method para asignar las propiedades 
	 * a despelgar en el grid
	 * 
	 * @param props
	 * @see buildTableFormat
	 */
	public void addProperty(String...props){
		properties=props;
	}
	
	/**
	 * Comodity method para asignar las etiquetas de las propiedades
	 * a despelgar en el grid
	 * 
	 * @param labels
	 * @see buildTableFormat
	 */
	public void addLabels(String...labels){
		this.labels=labels;
	}

	public String[] getProperties() {
		return properties;
	}
	public void setProperties(String[] properties) {
		this.properties = properties;
	}

	public String[] getLabels() {
		return labels;
	}
	public void setLabels(String[] labels) {
		this.labels = labels;
	}
	
	protected DefaultFormBuilder filterPanelBuilder;
	protected Map<String, JComponent> textEditors=new LinkedHashMap<String, JComponent>();
	
	/**
	 * Genera un panel de filtros diferente al
	 * default
	 */	
	
	public JPanel getFilterPanel() {
		if(filterPanel==null){
			filterPanel=getFilterPanelBuilder().getPanel();
			installFilters(filterPanelBuilder);
		}
		return filterPanel;
	}
	
	protected void installFilters(final DefaultFormBuilder builder){
		//Instalamos los filtros basados en textComponents
		for(Map.Entry<String, JComponent> entry:textEditors.entrySet()){
			builder.append(entry.getKey(),entry.getValue());
		}
		installCustomComponentsInFilterPanel(builder);
	}
	
	protected void installCustomComponentsInFilterPanel(DefaultFormBuilder builder){
		
	}
	
	public TextComponentMatcherEditor installTextComponentMatcherEditor(final String label,String...propertyNames){
		Assert.notEmpty(propertyNames,"Debe indicar por lo menos una propiedad de filtrado");
		final JTextField tf=new JTextField(5);
		final TextFilterator<E> filterator=GlazedLists.textFilterator(propertyNames);
		final TextComponentMatcherEditor<E> editor=new TextComponentMatcherEditor<E>(tf,filterator);
		matcherEditors.add(editor);
		textEditors.put(label, tf);
		return editor;
	}
	
	/**
	 * Instala un text component usando un filterator personalizado
	 * 
	 * @param label
	 * @param filterator
	 */
	public TextComponentMatcherEditor<E> installTextComponentMatcherEditor(final String label,TextFilterator filterator,final JTextField tf){		
		final TextComponentMatcherEditor<E> editor=new TextComponentMatcherEditor<E>(tf,filterator);		
		matcherEditors.add(editor);
		textEditors.put(label, tf);
		return editor;
	}
	
	/**
	 * Instala un matcher editor personalizado 
	 * 
	 * @param label
	 * @param filterator
	 */
	public MatcherEditor<E> installCustomMatcherEditor(final String label,final JComponent component,MatcherEditor<E> editor){
		matcherEditors.add(editor);
		textEditors.put(label, component);
		return editor;
	}


	protected DefaultFormBuilder getFilterPanelBuilder(){
		if(filterPanelBuilder==null){
			FormLayout layout=new FormLayout("p,2dlu,f:max(100dlu;p)","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.getPanel().setOpaque(false);
			filterPanelBuilder=builder;
		}
		return filterPanelBuilder;
	}
	
	protected Action insertAction;
	protected Action deleteAction;
	protected Action editAction;
	protected Action viewAction;


	

	@Override
	public Action[] getActions() {
		if(actions==null)
			actions=new Action[]{getLoadAction(),getInsertAction(),getDeleteAction(),getEditAction(),getViewAction()};
		return actions;
	}


	public Action getInsertAction() {
		if(insertAction==null){
			insertAction=CommandUtils.createInsertAction(this, "insert");
		}
		return insertAction;
	}
	public void setInsertAction(Action insertAction) {
		this.insertAction = insertAction;
	}


	public Action getDeleteAction() {
		if(deleteAction==null)
			deleteAction=CommandUtils.createDeleteAction(this, "delete");
		return deleteAction;
	}
	public void setDeleteAction(Action deleteAction) {
		this.deleteAction = deleteAction;
	}


	public Action getEditAction() {
		if(editAction==null)
			editAction=CommandUtils.createEditAction(this, "edit");
		return editAction;
	}
	public void setEditAction(Action editAction) {
		this.editAction = editAction;
	}


	public Action getViewAction() {
		if(viewAction==null){
			viewAction=CommandUtils.createViewAction(this, "view");
		}
		return viewAction;
	}
	public void setViewAction(Action viewAction) {
		this.viewAction = viewAction;
	}
	
	public void insert(){
		E bean=doInsert();
		if(bean!=null){
			source.add(bean);
			afterInsert(bean);
		}
	}
	
	protected void afterInsert(E bean){
		grid.packAll();
	}
	
	protected E doInsert(){
		if(logger.isDebugEnabled()){
			logger.debug("Inserting new bean..."+beanClazz.getName());
		}
		return null;
	}
	
	public void delete(){
		E bean=(E)getSelectedObject();
		if(bean!=null){
			if(MessageUtils.showConfirmationMessage("Seguro que desea eliminar el registro:\n "+bean,"Borrar"))
				try {
					if(doDelete(bean)){
						source.remove(bean);
					}
				} catch (Exception e) {
					MessageUtils.showError("Error al eliminar registro", e);
				}
		}
	}
	
	public boolean doDelete(E bean){
		return false;
	}
	
	public void edit(){
		E origen=(E)super.getSelectedObject();
		if(origen!=null){
			E bean=doEdit(origen);
			if(bean!=null){
				//int index=source.indexOf(origen);
				source.remove(origen);
				source.add(bean);
				setSelected(bean);
				afterEdit(bean);
			}
		}
	}
	
	protected void afterEdit(final E bean){
		grid.packAll();
	}
	
	public void setSelected(E bean){
		int index=sortedSource.indexOf(bean);
		if(index!=-1){
			selectionModel.setSelectionInterval(index, index);
		}
	}
	
	protected E doEdit(final E bean){
		if(logger.isDebugEnabled()){
			logger.debug("Editing bean: "+bean);
		}
		return null;
	}
	
	public void view(){
		select();
	}
	
	protected abstract List<E> findData();
	
	public void close(){
		String id=getTitle()!=null?getTitle():getClass().getName();
		logger.info("Cerrando browser: "+id+" (lipiando glazedlist...)");
		source.clear();
	}
	
	/**
	 * 
	 */
	public void open(){
		
	}
	
	/**
	 * Utiliti comparator para crear un comparador basado en el campo Id del
	 *  bean base.
	 * 
	 * @return
	 */
	protected Comparator createIdComparator(){
		return GlazedLists.reverseComparator(
				GlazedLists.beanPropertyComparator(this.beanClazz, "id")
				);
	}
	
	protected Periodo periodo;
	private ActionLabel periodoLabel;
	
	protected void manejarPeriodo(){
		periodo=Periodo.getPeriodoDelMesActual();
	}
	
	public ActionLabel getPeriodoLabel(){
		if(periodoLabel==null){
			manejarPeriodo();
			periodoLabel=new ActionLabel("Periodo: "+periodo.toString());
			periodoLabel.addActionListener(EventHandler.create(ActionListener.class, this, "cambiarPeriodo"));
		}
		return periodoLabel;
	}
	
	public void cambiarPeriodo(){
		ValueHolder holder=new ValueHolder(periodo);
		AbstractDialog dialog=Binder.createPeriodoSelector(holder);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			periodo=(Periodo)holder.getValue();
			
			nuevoPeriodo(periodo);
			updatePeriodoLabel();
		}
	}
	
	protected void updatePeriodoLabel(){
		periodoLabel.setText("Periodo:" +periodo.toString());
	}
	
	protected void nuevoPeriodo(Periodo p){
		load();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Action addAction(String actionId,String method,String label){
		Action a=new DispatchingAction(this,method);
		configAction(a, actionId,label);
		return a;
	}
	
	/** Implementacion de ejecuciones usando Template pattern similar a Spring JdbcTemplate***/
	
	protected void execute(final ExecutionSelectionTemplate<E> template){
		if(!getSelected().isEmpty()){
			try {
				doExecute(template);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(getControl(), e.getMessage(),"Error en tarea",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	
	
	protected void doExecute(final ExecutionSelectionTemplate<E> template){
		
	}
	
	/**
	 * Comodity para confirmar una accion
	 * 
	 * @param msg
	 * @return
	 */
	public boolean confirmar(String msg){
		int res=JOptionPane.showConfirmDialog(getControl(), msg, "Ejecutar?",JOptionPane.OK_CANCEL_OPTION);
		return res==JOptionPane.OK_OPTION;
	}

	/**
	 * Template para ejeccuion de tareas sobre una seleccion
	 * 
	 * @author Ruben Cancino Ramos
	 *
	 */
	public static interface ExecutionSelectionTemplate<E>{
		
		/**
		 * Ejecuta una tarea sobre la seleccion
		 * 
		 * @param selected
		 * @return
		 */
		public List<E> execute(final List<E> selected);
		
	}

}
