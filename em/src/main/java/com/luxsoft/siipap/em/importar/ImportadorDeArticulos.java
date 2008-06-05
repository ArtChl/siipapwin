package com.luxsoft.siipap.em.importar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.luxsoft.siipap.dao.FamiliaDao;
import com.luxsoft.siipap.dao.UnidadDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Unidad;
import com.luxsoft.siipap.domain.Articulo.Estado;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

/**
 * Importa articulos de siipap
 * 
 * @author Ruben Cancino
 *
 */
public class ImportadorDeArticulos  implements Importador{
	
	private Logger logger=Logger.getLogger(getClass());
	private FamiliaDao familiaDao;
	private UnidadDao unidadDao;
	private SiipapJdbcTemplateFactory factory;

	public Object[] importar(Object... params) {
		List<Articulo> articulos=new ArrayList<Articulo>();
		for(Object o:params){			
			Articulo a=importar(o.toString());
			articulos.add(a);
		}
		return articulos.toArray();
	}
	
	@SuppressWarnings("unchecked")
	public Articulo importar(final String clave){
		String sql="SELECT * FROM ARTICULO WHERE ARTCLAVE=\'@CLAVE\'";
		sql=sql.replaceAll("@CLAVE", clave);
		List<Map> rows=getFactory().getJdbcTemplate().queryForList(sql);
		if(rows.isEmpty()) return null;
		Articulo a=transformarRegistro(rows.get(0));
		return a;
	}
	
	/**
	 * TODO: Documentar e implementar los campos que no se encuentran
	 */	
	@SuppressWarnings("unused")
	public Articulo transformarRegistro(Map row) {
		Articulo a=new Articulo();
		String ARTCLAVE=(String)row.get("ARTCLAVE");
		String ARTNOMBR=(String)row.get("ARTNOMBR");
		Number ARTKILOS=(Number)row.get("ARTKILOS");
		Number ARTGRAMS=(Number)row.get("ARTGRAMS");
		Number ARTPRECI=(Number)row.get("ARTPRECI");
		Number ARTPRECRE=(Number)row.get("ARTPRECRE");
		Number ARTPRENU=(Number)row.get("ARTPRENU");
		Number ARTPRENU2=(Number)row.get("ARTPRENU2");
		String ARTFACNETO=(String)row.get("ARTFACNETO");
		String ARTUNIDAD=(String)row.get("ARTUNIDAD");		
		Number ARTCOSTO=(Number)row.get("ARTCOSTO");
		String ARTCODORIG=(String)row.get("ARTCODORIG");
		Date ARTFECORI=(Date)row.get("ARTFECORI");		
		String ARTFAMILIA=(String)row.get("ARTFAMILIA");
		String ARTOBSER=(String)row.get("ARTOBSER");
		Date ARTFECAL=(Date)row.get("ARTFECAL");
		Date ARTFEBAJ=(Date)row.get("ARTFEBAJ");
		String ARTESTADO=(String)row.get("ARTESTADO");		
		String ARTOPEVECO=(String)row.get("ARTOPEVECO");
		String ARTOPEINV=(String)row.get("ARTOPEINV");
		String ARTOPEINCO=(String)row.get("ARTOPEINCO");		
		Date ARTFESUS=(Date)row.get("ARTFESUS");
		String ARTCOMSUS=(String)row.get("ARTCOMSUS");
		String ARTDESPHP=(String)row.get("ARTDESPHP");
		String ARTAFEINV=(String)row.get("ARTAFEINV");
		
		 
		Number ARTUCOST=(Number)row.get("ARTUCOST");
		//a.setCosto
		Number ARTCOSPO=(Number)row.get("ARTCOSPO");
		String ARTOPEVEN=(String)row.get("ARTOPEVEN");
		String ARTUSUAR=(String)row.get("ARTUSUAR");
		String ARTOPECOM=(String)row.get("ARTOPECOM");
		String ARTOPECOCO=(String)row.get("ARTOPECOCO");
		Number ARTPRECI_B=(Number)row.get("ARTPRECI_B");
		Number ARTPRECREB=(Number)row.get("ARTPRECREB");
		Number ARTPRENU_B=(Number)row.get("ARTPRENU_B");
		Number ARTPRENU2B=(Number)row.get("ARTPRENU2B");
		Number ARTPRECI_C=(Number)row.get("ARTPRECI_C");
		Number ARTPRECREC=(Number)row.get("ARTPRECREC");
		Number ARTPRENU_C=(Number)row.get("ARTPRENU_C");
		Number ARTPRENU2C=(Number)row.get("ARTPRENU2C");
		Number ARTPRECI_D=(Number)row.get("ARTPRECI_D");
		Number ARTPRECRED=(Number)row.get("ARTPRECRED");
		Number ARTPRENU_D=(Number)row.get("ARTPRENU_D");
		Number ARTPRENU2D=(Number)row.get("ARTPRENU2D");
		Number ARTFACTMIN=(Number)row.get("ARTFACTMIN");
		
		a.setClave(ARTCLAVE.trim());
		a.setCodigoOrigen(ARTCODORIG.trim());
		a.setActivo(Estado.getEstado(ARTESTADO));
		a.setAfectaInventario(toBoolean(ARTAFEINV));		
		a.setArtfebaj(ARTFEBAJ);		
		a.setArtfecal(ARTFECAL);
		a.setArtprenu(resolverValor(ARTPRENU));
		a.setArtprenu2(resolverValor(ARTPRENU2));
		a.setComentarioDeSuspencion(ARTCOMSUS);
		a.setComentarioDeSuspencionEnCompra(ARTCOMSUS);
		a.setComentarioDeSuspencionEnInventario(ARTOPEINCO);
		a.setComentarioDeSuspencionEnVenta(ARTOPEVECO);
		a.setCosto(resolverValor(ARTCOSTO));		
		a.setDescripcion1(ARTNOMBR.trim());
		a.setDescripcion2(ARTNOMBR.trim());		
		a.setEstado(Estado.getEstado(ARTESTADO));
		a.setFamilia(getFamiliaDao().buscarFamilia(ARTFAMILIA.trim()));
		a.setFechaDeCodigoOrigen(ARTFECORI);
		a.setFechaDeSuspencion(ARTFESUS);		
		a.setKilos(toBigDecimal(ARTKILOS));		
		a.setModoDeVentaBruto(toBoolean(ARTFACNETO));
		a.setObservacion(ARTOBSER);		
		a.setPrecioContado(resolverValor(ARTPRECI));
		a.setPrecioCredito(resolverValor(ARTPRECRE));		
		a.setSuspendidoEnCompras(toBoolean(ARTCOMSUS));
		a.setSuspendidoEnInventarios(toBoolean(ARTOPEINV));
		a.setSuspendidoEnVentas(toBoolean(ARTOPEVECO));
		Unidad u=getUnidadDao().findByClave(ARTUNIDAD);
		if(u==null){
			u=new Unidad();
			u.setClave(ARTUNIDAD);
			String desc="NA";
			int cant=1;
			if(ARTUNIDAD.equalsIgnoreCase("MIL")){
				desc="Millares";
				cant=1000;
			}							
			u.setDescripcion(desc);
			u.setCantidad(cant);
			getUnidadDao().create(u);
		}
		a.setUnidad(u);
		a.setUnidadDeVenta(getUnidadDao().findByClave(ARTUNIDAD));
		a.setVisible(true);
		a.setVisibleEnComentariosDeAyuda(toBoolean(ARTDESPHP));
		desifrarMedidas(a);
		a.getUnidades().clear();
		a.setGramos(toInteger(ARTGRAMS));
		
		a.addUnidad(getUnidadDao().findByClave("KGS"));
		a.addUnidad(getUnidadDao().findByClave("MIL"));
		a.addUnidad(getUnidadDao().findByClave("PZA"));
		if(logger.isDebugEnabled()){
			logger.debug("Articulo importado: "+a);
		}
		return a;
	}
	
	public void desifrarMedidas(Articulo a){
		String codec=obtenerParteDeMedidas(a.getDescripcion1());
		a.setAncho(calcularAncho(codec));
		a.setLargo(calcularLargo(codec));
		a.setCalibre(calcularCalibre(a.getDescripcion1()));
	}
	private int calcularCalibre(final String s){		
		int res=0;
		int index=s.indexOf("PTS");
		if(index<0) return 0;
		String left=s.substring(0,index);
		String parts[]=left.split(" ");
		int l=parts.length-1;
		if(NumberUtils.isDigits(parts[l]))
			res=NumberUtils.toInt(parts[l]);
			//res=NumberUtils.stringToInt(parts[l]);
		else if(NumberUtils.isDigits(parts[l-1]))
			//res=NumberUtils.stringToInt(parts[l-1]);
			res=NumberUtils.toInt(parts[l-1]);
		return res;
	}
	
	public String obtenerParteDeMedidas(String s){
		String[] partes=s.split(" ");
		for(int i=0;i<partes.length;i++){
			if(partes[i].indexOf("X")>0)
				return partes[i];
		}
		return "";
	}
	
	public Integer toInteger(Object val){
		if(val==null) return new Integer(0);
		if(val instanceof BigDecimal){
			BigDecimal bd=(BigDecimal)val;
			return bd.intValue();
		}else if(val instanceof String){
			String s=(String)val;
			return Integer.valueOf(s);
		}else if(val instanceof Double){
			Double d=(Double)val;
			return d.intValue();
		}else if(val instanceof Integer){
			return (Integer)val;
		}else{
			throw new RuntimeException("Transformación a Integer no soportada origen: "
					+val.getClass().getName());
		}
	}
	
	private  BigDecimal calcularAncho(final String s){
		String[] m=s.trim().split("X");
		if(m.length>1 && NumberUtils.isDigits(m[0])){
			double val=NumberUtils.createDouble(m[0]).doubleValue();
			return new BigDecimal(val);
		}
		return BigDecimal.valueOf(0);
	}
	
	private BigDecimal calcularLargo(final String s){
		String[] m=s.trim().split("X");
		if(m.length>1 && NumberUtils.isDigits(m[1])){
			double val=NumberUtils.createDouble(m[1]).doubleValue();
			return new BigDecimal(val);
		}
		return BigDecimal.valueOf(0);
		
	}
	
	public BigDecimal toBigDecimal(Number d){
		return toBigDecimal(d.doubleValue());
	}
	
	public BigDecimal toBigDecimal(Double d){
		if(d==null)return null;
		return BigDecimal.valueOf(d.doubleValue());
	}
	public boolean toBoolean(String s){
		boolean val=false;
		try{
			val=BooleanUtils.toBoolean(s,"S","N");
		}catch(Exception ex){}
		return val;
	}
	
	public CantidadMonetaria resolverValor(Number val){
		if(val==null)
			return CantidadMonetaria.pesos(0.0); 
		return CantidadMonetaria.pesos(val.doubleValue());
	}
	
	public CantidadMonetaria resolverValor(BigDecimal val){
		if(val==null)
			return CantidadMonetaria.pesos(0.0);
		return CantidadMonetaria.pesos(val.doubleValue());
	}
	public CantidadMonetaria resolverValor(Double val){
		if(val==null)
			return CantidadMonetaria.pesos(0.0);
		return CantidadMonetaria.pesos(val.doubleValue());
	}


	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

	public FamiliaDao getFamiliaDao() {
		return familiaDao;
	}

	public void setFamiliaDao(FamiliaDao familiaDao) {
		this.familiaDao = familiaDao;
	}

	public UnidadDao getUnidadDao() {
		return unidadDao;
	}

	public void setUnidadDao(UnidadDao unidadDao) {
		this.unidadDao = unidadDao;
	}
		

}
