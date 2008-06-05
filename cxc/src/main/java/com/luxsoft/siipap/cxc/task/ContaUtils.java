package com.luxsoft.siipap.cxc.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.luxsoft.siipap.contabilidad.model.CobranzaPorBanco;
import com.luxsoft.siipap.cxc.dao.DepositosDao;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;

import com.luxsoft.siipap.cxc.model2.DescuentoContable;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Metodos auxiliares para exportar a contabilidad
 * 
 * @author Ruben Cancino
 *
 */
public class ContaUtils {
	
	
	public static List<Pago> buscarPagosCredito(final Date fecha){
		return Collections.unmodifiableList(ServiceLocator.getPagosManager().buscarPagos(fecha, "CRE"));
	}
	
	public static List<NotasDeCreditoDet> buscarNotasCre(final Date fecha){
		return ServiceLocator.getNotasManager().buscarNotasNoAplicables(fecha, "CRE");
	}
	
	public static List<PagoM> buscarSaldosAFavor(final Date fecha){
		return ServiceLocator.getPagosManager().buscarSaldosAFavor(fecha);	
	}
	
	public static List<PagoConOtros> buscarPagosConOtros(final Date fecha){
		return Collections.unmodifiableList(ServiceLocator.getPagosManager().buscarPagosConOtros(fecha));
	}
	
	public static List<PagoM> buscarPagosDeMenos(final Date fecha){		
		return ServiceLocator.getPagosManager().buscarPagosDeMenos(fecha);
	}
	
	 
	public static void generarPolizaDeCobranzaCredito(final Date fecha){
		final String recDir=System.getProperty("polizas.dir",System.getProperty("user.home"));
		final File dir=new File(recDir);
		generarPolizaDeCobranzaCredito(fecha, dir);
	}
	
	@SuppressWarnings("unchecked")
	public static void generarPolizaDeCobranzaCredito(final Date fecha,final File dir){
		
		final List<Pago> pagos=buscarPagosCredito(fecha);
		
		final List<NotasDeCreditoDet> notas=buscarNotasCre(fecha);
		final List<NotasDeCreditoDet> cargos=new ArrayList<NotasDeCreditoDet>();
		final ListIterator<NotasDeCreditoDet> iter=notas.listIterator();
		while(iter.hasNext()){
			NotasDeCreditoDet n=iter.next();
			n.getCuenta();
			if(n.getNota().getTipo().equals("M")){
				cargos.add(n);
				iter.remove();
			}
		}
		
		double interesesMoratorios=0;
		for(NotasDeCreditoDet det:cargos){
			interesesMoratorios+=det.getImporteSinIva()/1.15;
		}
		double ivaEnOtrosIngresos=interesesMoratorios*.15;
		
		
		CantidadMonetaria cuadre=CantidadMonetaria.pesos(0);
		
		final List<PagoM> saldos=buscarSaldosAFavor(fecha);
		for(PagoM saldo:saldos){
			cuadre=cuadre.add(CantidadMonetaria.pesos(saldo.getSaldo().doubleValue()));
		}
		
		
		final List<PagoM> acredores=new ArrayList<PagoM>();
		
		final ListIterator<PagoM> saldosIterator=saldos.listIterator();
		while(saldosIterator.hasNext()){
			PagoM next=saldosIterator.next();
			//if(next.getSaldo().doubleValue()>=2000){
			//if(next.getSaldoAl(fecha).amount().doubleValue()>=2000){
				saldosIterator.remove();
				acredores.add(next);
			//}
		}
		//		 Calculo complementario de ivas
		//double ivaVentaOtrosIngresos=0;
		double saldosAFavor=0;
		for(PagoM p:saldos){			
			saldosAFavor+=p.getImporteAsDouble();
		}
		//ivaVentaOtrosIngresos+=saldosAFavor;
		
		final List<PagoConOtros> otros=buscarPagosConOtros(fecha);
		
		final List<Pago> pagosConOtros=new ArrayList<Pago>();
		for(PagoM otroM:otros){
			pagosConOtros.addAll(otroM.getPagos());
		}
		
		final List<PagoM> menos=buscarPagosDeMenos(fecha);
		
		/*Actualizar ivas
		for(PagoM p:menos){
			ivaVentaOtrosIngresos-=p.getImporteAsDouble();
		}
		for(PagoM p:otros){
			ivaVentaOtrosIngresos-=p.getImporteAsDouble();
		}
		*/
		
		for(Pago p:pagos){
			cuadre=cuadre.add(p.getImporte());
		}
		for(Pago p:pagosConOtros){
			cuadre=cuadre.add(p.getImporte());
		}
		
		for(PagoConOtros o:otros){
			o.getCuenta();
			cuadre=cuadre.subtract(o.getImporte().abs());
		}
		for(PagoM m:menos){
			m.getCuenta();
			//cuadre=cuadre.subtract(m.getImporte());
		}
		for(NotasDeCreditoDet det:cargos){
			//cuadre=cuadre.subtract(det.getImporte()); DEBUG: Las notas de cargo se netean con los interese moratorios
		}
		
		for(NotasDeCreditoDet det:notas){
			Assert.isTrue(!StringUtils.isEmpty(det.getTipo()),"Tipo invalido para el registro de NotasDeCreditoDet id: "+det.getId()+" Valor: "+det.getTipo());
		}
		for(PagoConOtros p:otros){
			Assert.isTrue(!StringUtils.isEmpty(p.getFormaDePagoAsString())
					,"Forma de pago invalido para el registro de PagoM.id: "+p.getId()+" Valor: "+p.getFormaDePagoAsString());
			System.out.println("FP:"+p.getFormaDePagoAsString());
			;
		}
		
		//Procesar descuentos
		
		final Map<String,Map<Integer, CantidadMonetaria>> map=procesarDescuentos(notas);		
		final Map<Integer, CantidadMonetaria> totalDescMap=getTotalDescuentos(map);
		final List<DescuentoContable> descuentos=generarDescuentosContables(totalDescMap);		
		double ivaDesc=0;
		for(DescuentoContable d:descuentos){
			ivaDesc+=d.getIva();
		}
		
		final Map<Integer, CantidadMonetaria> totalDevoMap=getTotalDevos(map);
		final List<DescuentoContable> devos=generarDescuentosContables(totalDevoMap);
		double ivaDevos=0;
		for(DescuentoContable d:devos){
			ivaDevos+=d.getIva();
			d.setCuenta("405-0002-"+d.getSucursalAsString());
		}
		
		//Bancos
		final List<CobranzaPorBanco> bancos=pagosPorBanco(fecha);
		for(CobranzaPorBanco c:bancos){
			cuadre=cuadre.subtract(c.getImporte());
		}		
		
		//Pagos con tarjeta
		List<PagoM> tarjetas=ServiceLocator.getPagosManager().buscarPagosConTarjeta(fecha);
		for(PagoM p:tarjetas){
			cuadre=cuadre.subtract(p.getImporte());
		}
		
		//Iva de ventas
		CantidadMonetaria ivaVentas=CantidadMonetaria.pesos(0);
		for(CobranzaPorBanco cc:bancos){
			double iva=cc.getImporte().amount().doubleValue()/1.15;
			iva=iva*.15;
			ivaVentas=ivaVentas.add(CantidadMonetaria.pesos(iva));
		}		
		
		//Sumando el iva de las tarjetas
		for(PagoM p:tarjetas){
			double iva=p.getImporte().amount().doubleValue()/1.15;
			iva=iva*.15;
			ivaVentas=ivaVentas.add(CantidadMonetaria.pesos(iva));
		}
		
			
		Configuration cfg=new Configuration();
		cfg.setDateFormat("dd/MM/yyyy");		
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setClassForTemplateLoading(ContaUtils.class, "/");
		
		final DateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
		final DateFormat df2=new SimpleDateFormat("ddMMMyy");
		
		//Fix Nuevo: 
		double ivaVentasOtrosIngresos2=0;
		for(PagoM pp:acredores){
			ivaVentasOtrosIngresos2+=pp.getSaldoAsDouble();
		}
		for(PagoM pp:saldos){
			ivaVentasOtrosIngresos2+=pp.getSaldoAsDouble();
		}
		for(Pago pp:pagosConOtros){
			ivaVentasOtrosIngresos2-=pp.getImporteAsDouble();
		}
		for(PagoM pp:menos){
			ivaVentasOtrosIngresos2-=pp.getImporteAsDouble();
		}
		ivaVentasOtrosIngresos2=-(ivaVentasOtrosIngresos2/1.15)*.15;
		
		//
		double totalDepositos=0;
		for(CobranzaPorBanco c:bancos){
			totalDepositos+=c.getImporteAsDouble();
		}
		//
		double totalCobranza=0;
		for(Pago p:pagos){
			if(p.esPagoEnEfectivo()){
				totalCobranza+=p.getImporteAsDouble();
			}
		}
		double difCobranzaDepositos=totalDepositos-totalCobranza;
		System.out.println("Diferencia: "+difCobranzaDepositos);
		//cuadre=cuadre.add(CantidadMonetaria.pesos(difCobranzaDepositos));
		
		Map root=new HashMap();
		root.put("fecha", df.format(fecha));
		root.put("pagos", pagos);
		root.put("notas", notas);
		root.put("cargos", cargos);
		root.put("saldos",saldos);
		root.put("acredores",acredores);
		root.put("otros",otros);
		root.put("pagosConOtros",pagosConOtros);
		root.put("menos", menos);
		root.put("descuentos", descuentos);
		root.put("devos", devos);
		root.put("cuadre", cuadre.amount().doubleValue());
		root.put("ivaDesc", ivaDesc);
		root.put("ivaDevos", ivaDevos);
		root.put("bancos", bancos);
		root.put("interesesMoratorios", interesesMoratorios);
		root.put("ivaEnOtrosIngresos", ivaEnOtrosIngresos);
		root.put("ivaVentaOtrosIngresos", ivaVentasOtrosIngresos2);
		root.put("ivaVentas", ivaVentas.amount().doubleValue());
		root.put("tarjetas", tarjetas);
		root.put("ietu", getBaseIetu(pagos));
		
		//String sucu=StringUtils.leftPad(String.valueOf(sucursal), 2,'0');
		try {
			final String pattern="C{0}.POL";
			 
			final String fileName = MessageFormat.format(pattern, df2.format(fecha));
			final Template temp=cfg.getTemplate("templates/polizaDiarioCobranzaCre_A.ftl");			
			
			final File target=new File(dir,fileName);			
			final FileOutputStream os=new FileOutputStream(target);
			final Writer out=new OutputStreamWriter(os);
			temp.process(root, out);
			out.flush();
			out.close();
			os.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static Map<String,Map<Integer, CantidadMonetaria>> procesarDescuentos(final List<NotasDeCreditoDet> notas){
		System.out.println("Notas a procesar: "+notas.size());
		final Map<Integer, CantidadMonetaria> mapDescuento1=new HashMap<Integer, CantidadMonetaria>();
		final Map<Integer, CantidadMonetaria> mapDescuento2=new HashMap<Integer, CantidadMonetaria>();
		final Map<Integer, CantidadMonetaria> mapBonificacion=new HashMap<Integer, CantidadMonetaria>();
		final Map<Integer, CantidadMonetaria> mapDevolucion=new HashMap<Integer, CantidadMonetaria>();
		
		final Map<String,Map<Integer, CantidadMonetaria>> map=new HashMap<String, Map<Integer,CantidadMonetaria>>();
		
		map.put("U",mapDescuento1);
		map.put("V",mapDescuento2);
		map.put("L",mapBonificacion);
		map.put("J",mapDevolucion);
		
		for(NotasDeCreditoDet det:notas){
			NotaDeCredito nota=det.getNota();
			if(nota.getTipo().equals("U")){
				cargar(mapDescuento1, det);
			}else if(nota.getTipo().equals("V")){
				cargar(mapDescuento2, det);
			}else if(nota.getTipo().equals("L")){
				cargar(mapBonificacion, det);
			}else if(nota.getTipo().equals("J")){
				cargar(mapDevolucion, det);
			}
		}
		return map;
	}
	
	public static Map<Integer, CantidadMonetaria> getTotalDescuentos(final Map<String,Map<Integer, CantidadMonetaria>> map){		
		
		final Map<Integer, CantidadMonetaria> totalMap=new HashMap<Integer, CantidadMonetaria>();
		
		for(Map.Entry<String,Map<Integer, CantidadMonetaria>> entry:map.entrySet()){
			if(entry.getKey().equals("J"))
				continue;
			final Map<Integer, CantidadMonetaria> r=entry.getValue();
			for(Entry<Integer,CantidadMonetaria> e:r.entrySet()){
				CantidadMonetaria totSuc=totalMap.get(e.getKey());
				if(totSuc==null){
					totSuc=CantidadMonetaria.pesos(0);					
				}
				totSuc=totSuc.add(e.getValue());
				totalMap.put(e.getKey(), totSuc);
			}
		}
		return totalMap;
	}
	
	public static List<DescuentoContable> generarDescuentosContables(final Map<Integer, CantidadMonetaria> map){
		final List<DescuentoContable> desc=new ArrayList<DescuentoContable>();
		for(Entry<Integer, CantidadMonetaria> e:map.entrySet()){
			final DescuentoContable d=new DescuentoContable(e.getKey(),"Descuento sobre ventas",e.getValue());
			if(d.getSucursal()==10)
				d.setSucursal(6);
			else if(d.getSucursal()==9)
				d.setSucursal(10);
			d.setCuenta("406-0002-"+d.getSucursalAsString());
			desc.add(d);
			
			
		}		
		return desc;
	}
	
	private static void cargar(final Map<Integer,CantidadMonetaria> map,final NotasDeCreditoDet det){		
		int sucursal=det.getSucDocumento();
		CantidadMonetaria importe=map.get(sucursal);
		if(importe==null){
			importe=CantidadMonetaria.pesos(0);			
		}		
		importe=importe.add(det.getImporte());
		map.put(sucursal, importe);
	}
	
	public static Map<Integer, CantidadMonetaria> getTotalDevos(final Map<String,Map<Integer, CantidadMonetaria>> map){		
		
		final Map<Integer, CantidadMonetaria> totalMap=new HashMap<Integer, CantidadMonetaria>();
		
		for(Map.Entry<String,Map<Integer, CantidadMonetaria>> entry:map.entrySet()){
			if(!entry.getKey().equals("J"))
				continue;
			final Map<Integer, CantidadMonetaria> r=entry.getValue();
			for(Entry<Integer,CantidadMonetaria> e:r.entrySet()){
				CantidadMonetaria totSuc=totalMap.get(e.getKey());
				if(totSuc==null){
					totSuc=CantidadMonetaria.pesos(0);					
				}
				totSuc=totSuc.add(e.getValue());
				totalMap.put(e.getKey(), totSuc);
			}
		}
		return totalMap;
	}
	
	@SuppressWarnings("unchecked")
	public static List<CobranzaPorBanco> pagosPorBanco(final Date fecha){
		
		final List<Deposito> depositos=((DepositosDao)ServiceLocator.getDaoContext().getBean("depositosDao")).buscarDepositosCredito(fecha); 
		depositos.addAll(((DepositosDao)ServiceLocator.getDaoContext().getBean("depositosDao")).buscarDepositosEnCobranzaCre(fecha));
		final List<CobranzaPorBanco> list=new ArrayList<CobranzaPorBanco>();
		
		
		for(Deposito d:depositos){
			CobranzaPorBanco cc=new CobranzaPorBanco();			
			String banco=d.getBanco();
			cc.setBanco(banco);			
			cc.setDeposito(d.getId());
			cc.setImporte(d.getImporte());			
			cc.setCuenta(d.getCuentaDestino());
			cc.setFormaDePago(d.getFormaDePago());
			list.add(cc);
		}
		for(CobranzaPorBanco cc:list){
			System.out.println(cc);
		}
		
		return list;
	}
	
	
	public static double getBaseIetu(final List<Pago> source){
		double importe=0;
		for(Pago p:source){
			if( (p.getVenta()==null) && (p.getNota()==null) )
				continue;
			final int year=p.getVenta()!=null?p.getVenta().getYear():p.getNota().getYear();
			if(year>2007)
				importe+=p.getImporte().amount().doubleValue();
		}
		return importe/1.15;
	}
	
	public static void main(String[] args) {
		final Date fecha=DateUtils.obtenerFecha("14/01/2008");
		/**
		
		final List<NotasDeCreditoDet> notas=buscarNotasCre(fecha);
		final Map map=procesarDescuentos(notas);
		for(Object e:map.entrySet()){
			System.out.println(e);
		}
		final Map tot=getTotalDescuentos(map);
		for(Object e:tot.entrySet()){
			System.out.println(e);
		}
		final List<DescuentoContable> desc=generarDescuentosContables(tot);
		for(DescuentoContable d:desc){
			System.out.println(d);
		}
		**/
		generarPolizaDeCobranzaCredito(fecha);
		
		/*
		List<CobranzaPorBanco> bancos=pagosPorBanco(fecha);		
		for(CobranzaPorBanco c:bancos){
			System.out.println(c);
		}*/
		
	}

}
