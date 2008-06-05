package com.luxsoft.siipap.em.replica.catalogos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.dao.ClaseDao;
import com.luxsoft.siipap.dao.LineaDao;
import com.luxsoft.siipap.dao.MarcaDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.Clase;
import com.luxsoft.siipap.domain.Linea;
import com.luxsoft.siipap.domain.Marca;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;

@SuppressWarnings("unchecked")
public class ImportaNuevosCatalogos extends AbstractReplicatorSupport{
	
	private LineaDao lineaDao;
	private MarcaDao marcaDao;
	private ClaseDao claseDao;
	private ArticuloDao articuloDao;

	public List<ReplicaLog> validar(Periodo periodo) {
		
		return null;
	}

	public void bulkImport(Periodo p) {		
	}
	
	public List importar(final Periodo p){
		List<Linea> lineas=cargarLineas();
		List<Marca> marcas=cargarMarcas();
		List<Clase> clases=cargarClases();
		
		for(Linea l:lineas){
			getLineaDao().salvar(l);
		}
		for(Marca m: marcas){
			getMarcaDao().salvar(m);
		}
		
		for(Clase c:clases){
			getClaseDao().salvar(c);
		}
		
		return null;
	}
	
	public void vincular(){
		String sql="select * from NARTICUL";
		getTemplate().query(sql, new RowCallbackHandler(){

			public void processRow(ResultSet rs) throws SQLException {
				
				try {
					Long id=rs.getLong(1);
					String linea=rs.getString(2);
					String marca=rs.getString(3);
					String clase=rs.getString(4);
					int caras=rs.getInt(5);
					String acabado=rs.getString(6);
					String presentacion=rs.getString(7);
					String color=rs.getString(8);
					String nacional=rs.getString(9);
					if(nacional==null)
						nacional="N";
					Articulo a=getArticuloDao().findById(id);
					String s=nacional.trim();
					boolean nac=s.equalsIgnoreCase("N");
					if(linea.equalsIgnoreCase("CONTABLE")){
						System.out.println("Nacoinal: "+nac);
					}
					a.setNacional(nac);
					a.setColor(color);
					a.setAcabado(acabado);
					a.setCaras(caras);
					a.setPresentacion(presentacion);
					
					if(linea!=null){
						a.setLinea(getLineaDao().buscarPorNombre(linea.trim()));
						a.setLineaClave(linea.trim());
					}
					if(marca!=null){
						a.setMarca(getMarcaDao().buscarPorNombre(marca.trim()));
						a.setMarcaClave(marca.trim());
					}
					if(clase!=null){
						a.setClase(getClaseDao().buscarPorNombre(clase.trim()));
						a.setClaseClave(clase.trim());
					}
					//System.out.println("Actualizando: "+a+" "+a.isNacional());
					getArticuloDao().update(a);
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
				
				
			}
			
		});	
				
	}
	
	
	private List<Linea> cargarLineas(){
		String sql="select * from LINEA";		
		return getTemplate().query(sql,new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				String nombre=rs.getString(2);
				Linea l=new Linea();
				l.setNombre(nombre.trim());
				return l;
			}
			
		});		
	}
	
	private List<Marca> cargarMarcas(){
		String sql="select * from MARCA";		
		return getTemplate().query(sql,new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				String nombre=rs.getString(2);
				Marca l=new Marca();
				l.setNombre(nombre.trim());
				return l;
			}
			
		});
	}
	
	private List<Clase> cargarClases(){
		String sql="select * from CLASE";		
		return getTemplate().query(sql,new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				String nombre=rs.getString(2);
				Clase l=new Clase();
				l.setNombre(nombre.trim());
				return l;
			}			
		});
	}
	
	private JdbcTemplate getTemplate(){
		return getFactory().getJdbcTemplate(2007);
	}

	public ClaseDao getClaseDao() {
		return claseDao;
	}

	public void setClaseDao(ClaseDao claseDao) {
		this.claseDao = claseDao;
	}

	public LineaDao getLineaDao() {
		return lineaDao;
	}

	public void setLineaDao(LineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public MarcaDao getMarcaDao() {
		return marcaDao;
	}

	public void setMarcaDao(MarcaDao marcaDao) {
		this.marcaDao = marcaDao;
	}
	
	
	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}

	public static void main(String[] args) {
		ServiceManager manager=ServiceManager.instance();
		LineaDao ld=(LineaDao)manager.getContext().getBean("lineaDao");
		MarcaDao md=(MarcaDao)manager.getContext().getBean("marcaDao");
		ClaseDao cd=(ClaseDao)manager.getContext().getBean("claseDao");
		ArticuloDao ad=(ArticuloDao)manager.getContext().getBean("articuloDao");
		
		ImportaNuevosCatalogos imp=new ImportaNuevosCatalogos();
		imp.setLineaDao(ld);
		imp.setMarcaDao(md);
		imp.setClaseDao(cd);
		imp.setArticuloDao(ad);
		imp.setFactory(manager.getSiipapTemplateFactory());
		
		//imp.importar(null);
		imp.vincular();
		
	}
	

}
