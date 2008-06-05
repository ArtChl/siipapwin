package com.luxsoft.siipap.compras.catalogos;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.Matchers;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.dialog.AbstractCatalogDialog;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.selectores.CheckBoxSelector;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.Renderers;


public class ArticuloBrowser extends AbstractCatalogDialog<Articulo>{
	
	private JTextField claveField=new JTextField(10); 
	private JTextField lineaField=new JTextField(10);
	private JTextField marcaField=new JTextField(10);
	private JTextField claseField=new JTextField(10);
	private JTextField colorField=new JTextField(10);
	private JTextField carasField=new JTextField(10);	
	private CheckBoxSelector<Articulo> deLineaSelector;

	public ArticuloBrowser() {
		super("Catálogo de articulos");		
	}	

	@Override
	protected void setResizable() {
		setResizable(true);
	}

	@Override
	protected TableFormat<Articulo> getTableFormat() {
		
		return GlazedLists.tableFormat(Articulo.class
				,new String[]{"id","clave","descripcion1","kilos","lineaClave","claseClave","marcaClave","clasificacion","caras","acabado","color","nacional","costoP"}
				,new String[]{"id","clave","Desc","Kg","Linea","Clase","Marca","Clasific","Caras","Acabado","Color","Nac","CP"}
		);
	}
	
	@Override
	protected JComponent buildContent() {				
		final JComponent c=super.buildContent();
		c.setPreferredSize(new Dimension(850,650));
		
		final JSplitPane sp=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setOneTouchExpandable(true);
		
		sp.setTopComponent(buildFilterPanel());
		sp.setBottomComponent(c);
		
		
		ComponentUtils.decorateActions(grid);		
		getToolbarActions().get(5).setEnabled(false);
		getToolbarActions().get(6).setEnabled(false);
		
		grid.packAll();
		grid.getColumnExt("CP").setCellRenderer(Renderers.getCantidadNormalTableCellRenderer());
		deLineaSelector.getBox().setSelected(true);
		
		return sp;
	}
	
	private JPanel buildFilterPanel(){
		final FormLayout layout=new FormLayout(
				"p,2dlu,p, 3dlu " +
				"p,2dlu,p, 3dlu " +
				"p,2dlu,p, 3dlu " +
				""
				);
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Articulo",claveField,5);
		builder.nextLine();
		builder.append("Linea",lineaField);
		builder.append("Clase",claseField);
		builder.append("Marca",marcaField);
		builder.nextLine();
		builder.append("Color",colorField);		
		builder.append("De Línea",deLineaSelector.getBox());		
		builder.append("Caras",carasField);
		builder.nextLine();
		return builder.getPanel();
	}
	
	
	
	protected EventList<Articulo> getFilteredSource(){
		final EventList<MatcherEditor<Articulo>> editors=new BasicEventList<MatcherEditor<Articulo>>();
		final CompositeMatcherEditor<Articulo> compositeEditor=new CompositeMatcherEditor<Articulo>(editors);
		
		final TextFilterator<Articulo> claveFilterator=GlazedLists.textFilterator(new String[]{"clave","descripcion1"});
		final TextComponentMatcherEditor<Articulo> editor1=new TextComponentMatcherEditor<Articulo>(claveField,claveFilterator);
		editors.add(editor1);
		
		final TextFilterator<Articulo> lineaFilterator=GlazedLists.textFilterator(new String[]{"lineaClave"});
		final TextComponentMatcherEditor<Articulo> editor2=new TextComponentMatcherEditor<Articulo>(lineaField,lineaFilterator);
		editors.add(editor2);
		
		final TextFilterator<Articulo> claseFilterator=GlazedLists.textFilterator(new String[]{"claseClave"});
		final TextComponentMatcherEditor<Articulo> editor3=new TextComponentMatcherEditor<Articulo>(claseField,claseFilterator);
		editors.add(editor3);
		
		final TextFilterator<Articulo> marcaFilterator=GlazedLists.textFilterator(new String[]{"marcaClave"});
		final TextComponentMatcherEditor<Articulo> editor4=new TextComponentMatcherEditor<Articulo>(marcaField,marcaFilterator);
		editors.add(editor4);
		
		final TextFilterator<Articulo> colorFilterator=GlazedLists.textFilterator(new String[]{"color"});
		final TextComponentMatcherEditor<Articulo> editor5=new TextComponentMatcherEditor<Articulo>(colorField,colorFilterator);
		editors.add(editor5);
		
		final TextFilterator<Articulo> carasFilterator=GlazedLists.textFilterator(new String[]{"caras"});
		final TextComponentMatcherEditor<Articulo> editor6=new TextComponentMatcherEditor<Articulo>(carasField,carasFilterator);
		editors.add(editor6);
		
		deLineaSelector=new CheckBoxSelector<Articulo>(){			
			protected Matcher<Articulo> getSelectMatcher(Object... obj) {				
				return Matchers.beanPropertyMatcher(Articulo.class, "clasificacion", "L");
			}			
		};
		editors.add(deLineaSelector);
		
		final FilterList<Articulo> filterList=new FilterList<Articulo>(source,compositeEditor);
 		return filterList;
	}
	
	public void view(){
		mostrar(getSelected());
	}
	
	public void edit(){
		final DefaultFormModel model=new DefaultFormModel(getSelected());
		final ArticuloForm form=new ArticuloForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			try {
				ServiceLocator.getArticuloDao().update((Articulo)model.getBaseBean());				
			} catch (Exception e) {
				MessageUtils.showError("Error salvando Articulo",e);
			}
		}
	}
	
	protected Object doInsert(){
		return crear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Articulo> getData() {
		return ServiceLocator.getArticuloDao().buscarTodos();
	}
	
	public void mostrar(final Articulo a){
		final DefaultFormModel model=new DefaultFormModel(a,true);
		final ArticuloForm form=new ArticuloForm(model);
		form.open();
	}
	
	public Articulo crear(){
		final DefaultFormModel model=new DefaultFormModel(new Articulo());
		final ArticuloForm form=new ArticuloForm(model);		
		form.open();
		if(!form.hasBeenCanceled()){
			try {
				ServiceLocator.getArticuloDao().create((Articulo)model.getBaseBean());
				return (Articulo)model.getBaseBean();
			} catch (Exception e) {
				MessageUtils.showError("Error salvando Articulo",e);
			}
		}
		return null;
	}
	
	protected boolean doDelete(){				
		ServiceLocator.getArticuloDao().delete(getSelected());
		return true;
	}
	
}
