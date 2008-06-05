package com.luxsoft.siipap.em.replica.catalogos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.dao.FamiliaDao;
import com.luxsoft.siipap.dao.UnidadDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Familia;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Unidad;
import com.luxsoft.siipap.domain.Articulo.Estado;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.ConverterUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;

public class ArticulosReplicator extends AbstractReplicatorSupport{
	
	private FamiliaDao familiaDao;
	private UnidadDao unidadDao;
	
	public ArticulosReplicator(){
		setBeanClass(Articulo.class);
		setFechado(false);
	}
	
	public List importar(Periodo periodo){
		List<Articulo> articulos=cargarBeans();
		for(Articulo a:articulos){
			try{
				Articulo art=getArticuloDao().buscarPorClave(a.getClave());
				if(art!=null){
					logger.info("El articulo existe actualizando.."+art.getClave());					
					BeanUtils.copyProperties(a, art,new String[]{"id","creado","clave","unidades","linea","lineaClave","marca","marcaClave","clase","claseClave","presentacion","color","nacional","acabado"});					
					getArticuloDao().update(art);
				}else{
					logger.info("Articulo nuevo salvando...");
					getArticuloDao().create(a);
				}				
			}catch(Exception e){
				logger.error("Error al salvar/actualizar articulo: "+a.getClave(),e);
				e.printStackTrace();
			}
		}
		return articulos;
	}
	
	@SuppressWarnings("unchecked")
	private List<Articulo> cargarBeans(){
		final String sql="select * from ARTICULO order by ARTCLAVE";
		List<Map<String, Object>> rows=getFactory().getJdbcTemplate(Periodo.getPeriodoDelMesActual()).queryForList(sql);
		List<Articulo> arts=new ArrayList<Articulo>();
		logger.info("Registros del dbf: "+rows.size());
		ListIterator iter=rows.listIterator();
		while(iter.hasNext()){
			Articulo a=transformarRegistro((Map)iter.next());
			arts.add(a);
			iter.remove();
		}
		
		/*
		for(Map row:rows){
			Articulo a=transformarRegistro(row);
			arts.add(a);
		}*/
		return arts;
	}

	public List<ReplicaLog> validar(Periodo periodo) {
		Periodo p=new Periodo();
		int mes=getMes(p);
		int year=getYear(p);
		List<ReplicaLog> logs=getReplicaLogDao().buscar("Articulo");
		if(!logs.isEmpty()){
			for(ReplicaLog log:logs){
				getReplicaLogDao().borrar(log);
			}
		}
		ReplicaLog log=new ReplicaLog();
		log.setEntity("Articulo");
		log.setTabla("SW_ARTICULOS");
		log.setPeriodo(p.toString());		
		log.setYear(year);
		log.setTipo("");
		log.setMonth(mes);
		int beans=contarBeans(p);
		int registros=contarRegistros(p,"SW_ARTICULOS", null);
		log.setBeans(beans);
		log.setRegistros(registros);
		log.setDia(p.getFechaInicial());
		try {
			getReplicaLogDao().salvar(log);
		} catch (Exception e) {
			logger.error("No se pudo guardar la bitacora: "+log,e);
		}		
		String res=log.validar()?"Carga de Periodo OK":"ERROR en carga de periodo: "+mes;
		logger.info(res);
		
		logs=new ArrayList<ReplicaLog>();
		logs.add(log);
		return logs;
	}
	
	public ArticuloDao getArticuloDao(){
		return (ArticuloDao)getDao();
	}
	

	public void bulkImport(Periodo p) {}

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
	
	@SuppressWarnings("unused")
	public Articulo transformarRegistro(Map row) {
		
		Articulo a=new Articulo();
		String ARTCLAVE=(String)row.get("ARTCLAVE");
		String ARTNOMBR=(String)row.get("ARTNOMBR");
		Double ARTKILOS=(Double)row.get("ARTKILOS");
		Double ARTGRAMS=(Double)row.get("ARTGRAMS");
		Double ARTPRECI=(Double)row.get("ARTPRECI");
		Double ARTPRECRE=(Double)row.get("ARTPRECRE");
		Double ARTPRENU=(Double)row.get("ARTPRENU");
		Double ARTPRENU2=(Double)row.get("ARTPRENU2");
		String ARTFACNETO=(String)row.get("ARTFACNETO");		
		
		String ARTUNIDAD=(String)row.get("ARTUNIDAD");		
		Double ARTCOSTO=(Double)row.get("ARTCOSTO");
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
		
		 
		
		Double ARTUCOST=(Double)row.get("ARTUCOST");
		//a.setCosto
		Double ARTCOSPO=(Double)row.get("ARTCOSPO");
		String ARTOPEVEN=(String)row.get("ARTOPEVEN");
		String ARTUSUAR=(String)row.get("ARTUSUAR");
		String ARTOPECOM=(String)row.get("ARTOPECOM");
		String ARTOPECOCO=(String)row.get("ARTOPECOCO");
		Double ARTPRECI_B=(Double)row.get("ARTPRECI_B");
		Double ARTPRECREB=(Double)row.get("ARTPRECREB");
		Double ARTPRENU_B=(Double)row.get("ARTPRENU_B");
		Double ARTPRENU2B=(Double)row.get("ARTPRENU2B");
		Double ARTPRECI_C=(Double)row.get("ARTPRECI_C");
		Double ARTPRECREC=(Double)row.get("ARTPRECREC");
		Double ARTPRENU_C=(Double)row.get("ARTPRENU_C");
		Double ARTPRENU2C=(Double)row.get("ARTPRENU2C");
		Double ARTPRECI_D=(Double)row.get("ARTPRECI_D");
		Double ARTPRECRED=(Double)row.get("ARTPRECRED");
		Double ARTPRENU_D=(Double)row.get("ARTPRENU_D");
		Double ARTPRENU2D=(Double)row.get("ARTPRENU2D");
		Double ARTFACTMIN=(Double)row.get("ARTFACTMIN");
		
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
		a.setFamilia(getFamilia(ARTFAMILIA.trim()));
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
		a.setUnidad(getUnidad(ARTUNIDAD));
		a.setUnidadDeVenta(getUnidad(ARTUNIDAD));
		a.setVisible(true);
		a.setVisibleEnComentariosDeAyuda(toBoolean(ARTDESPHP));
		desifrarMedidas(a);
		a.getUnidades().clear();
		a.setGramos(ConverterUtils.toInteger(ARTGRAMS));
		a.addUnidad(getUnidad("KGS"));
		a.addUnidad(getUnidad("MIL"));
		a.addUnidad(getUnidad("PZA"));
		
		String clasificacion=(String)row.get("ARTCLASIFI");
		boolean neto=ARTFACNETO.equalsIgnoreCase("N");
		a.setPrecioNeto(neto);
		a.setClasificacion(clasificacion);
		logger.info("Bean creado: "+a );
		return a;
	}
	
	public boolean toBoolean(String s){
		boolean val=false;
		try{
			val=BooleanUtils.toBoolean(s,"S","N");
		}catch(Exception ex){}
		return val;
	}
	
	public BigDecimal toBigDecimal(Double d){
		if(d==null)return null;
		return BigDecimal.valueOf(d.doubleValue());
	}
	
	public synchronized CantidadMonetaria resolverValor(Double val){
		if(val==null)
			return CantidadMonetaria.pesos(0.0);
		return CantidadMonetaria.pesos(val.doubleValue());
	}
	
	public Familia getFamilia(String clave){
		Familia f=getFamiliaDao().buscarFamilia(clave);
		Assert.notNull(f,"NO EXISTE LA FAMILIA: "+clave);
		return f;
	}
	public Unidad getUnidad(String clave){
		Unidad u=getUnidadDao().findByClave(clave);
		Assert.notNull(u,"NO EXISTE LA UNIDAD: "+clave);
		return u;
	}
	
	public static void desifrarMedidas(Articulo a){
		String codec=obtenerParteDeMedidas(a.getDescripcion1());
		a.setAncho(calcularAncho(codec));
		a.setLargo(calcularLargo(codec));
		a.setCalibre(calcularCalibre(a.getDescripcion1()));
	}
	
	private static int calcularCalibre(final String s){		
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
	
	public static String obtenerParteDeMedidas(String s){
		String[] partes=s.split(" ");
		for(int i=0;i<partes.length;i++){
			if(partes[i].indexOf("X")>0)
				return partes[i];
		}
		return "";
	}
	
	private static BigDecimal calcularAncho(final String s){
		String[] m=s.trim().split("X");
		if(m.length>1 && NumberUtils.isDigits(m[0])){
			double val=NumberUtils.createDouble(m[0]).doubleValue();
			return new BigDecimal(val);
		}
		return BigDecimal.valueOf(0);
	}
	
	private static BigDecimal calcularLargo(final String s){
		String[] m=s.trim().split("X");
		if(m.length>1 && NumberUtils.isDigits(m[1])){
			double val=NumberUtils.createDouble(m[1]).doubleValue();
			return new BigDecimal(val);
		}
		return BigDecimal.valueOf(0);
		
	}

}
