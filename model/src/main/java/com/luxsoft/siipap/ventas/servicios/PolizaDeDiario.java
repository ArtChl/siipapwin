package com.luxsoft.siipap.ventas.servicios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ca.odell.glazedlists.EventList;

import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Sucursales;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Fabrica la poliza de ingreso para su exportacion a COI 
 * 
 * @author Ruben Cancino
 *
 */
public class PolizaDeDiario {
	
	private final PolizaDeDiarioModel model;	
	private final Date fecha;
		
	
	public PolizaDeDiario(PolizaDeDiarioModel model){
		this.model=model;
		this.fecha=model.fecha;
	}
	
	public void load(){
		model.load();
	}
	
	/**
	 * Genera la poiliza de diario para todas las suucursales
	 *
	 */
	public void generar(){
		for(Sucursales s:Sucursales.values()){
			generar(s);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void generar(final Sucursales suc){
		
		final String recDir=System.getProperty("polizas.dir",System.getProperty("user.home"));
		final File dir=new File(recDir);
		
		Configuration cfg=new Configuration();
		cfg.setDateFormat("dd/MM/yyyy");		
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setClassForTemplateLoading(PolizaDeDiario.class, "/");
		
		final DateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
		final DateFormat df2=new SimpleDateFormat("ddMMM");
		final String sucursal=StringUtils.leftPad(String.valueOf(Sucursales.fixSucursalNumero(suc)),3, '0');
		final String sucursalName=suc.name();
		
		//Seccion de Bancos (Depositos tipo CAM importados desde SIIPAP)
		final List<Deposito> bancos=model.getDepositos(suc, "CAM");
		
		//Seccion de Iva
		double ivaEnVentas=model.calcularIvaEnVentas(suc).amount().doubleValue();
		double ivaEnVentasAnticipos=model.calcularIvaAnticipos(suc).amount().doubleValue();
		
		//Seccion de IETU
		double ietu=model.ietu(suc).amount().doubleValue();
		
		//Seccion de Anticipos
		double anticipos=model.anticipos(suc).amount().doubleValue();
		double ivaAnticipos=model.calcularIvaAnticipos(suc).amount().doubleValue();
		
		//Devoluciones
		double devoluciones=model.calcularDevolucionesCam(suc).amount().doubleValue();
		
		//Iva en devo pendientes de trasladar
		double ivaEnDevPendienteTrasladar=devoluciones*.15;
		
		
		
		//Acredores
		double acredores=model.calcularPagosConTarjetaCamioneta(suc).amount().doubleValue();
		
		//Otros ingresos
		double otrosIngresosDiversos=0;
		double otrosProductos=model.calcularOtrosProductos(suc).amount().doubleValue();
		double pagoConOtros=model.pagoConOtros(suc).amount().doubleValue();		
		double otrosIngresosSaldoAFavor=(otrosProductos-pagoConOtros);
		
		//Clientes 
		double clientesPagos=model.calcularPagosCamioneta(suc).amount().doubleValue();
		clientesPagos-=otrosProductos;
		
		//Iva en cobro x otros ingresos
		
		double ivaOtrosIngresos=(otrosIngresosSaldoAFavor/1.15)*.15;
		
		
		Map root=new HashMap();
		root.put("fecha", df.format(fecha));
		root.put("sucursal", sucursal);
		root.put("sucursalName", sucursalName);
		root.put("bancos", bancos);
		root.put("ivaEnVentas", ivaEnVentas);
		root.put("ivaEnVentasAnticipos", ivaEnVentasAnticipos);
		root.put("ietu", ietu);
		root.put("anticipos", anticipos);
		root.put("ivaAnticipos",ivaAnticipos);
		root.put("devoluciones",devoluciones);		
		root.put("ivaEnDevPendienteTrasladar",ivaEnDevPendienteTrasladar);
		root.put("clientesPagos",clientesPagos);
		root.put("acredores",acredores);
		root.put("otrosIngresosDiversos",otrosIngresosDiversos);
		root.put("otrosIngresosSaldoAFavor",otrosIngresosSaldoAFavor);
		root.put("ivaOtrosIngresos",ivaOtrosIngresos);
				
		
		try {
			final String pattern="D{0}{1}.POL";
			 
			final String fileName = MessageFormat.format(pattern, df2.format(fecha),StringUtils.leftPad(String.valueOf(suc.getNumero()),2, '0'));
			final Template temp=cfg.getTemplate("templates/polizaDeIngresoCam.ftl");			
			
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
	
	public static void main(String[] args) {
		final Periodo per=new Periodo("07/04/2008","07/04/2008");
		final List<Date> dias=per.getListaDeDias();
		for(Date d:dias){
			System.out.println("Generando poliza Ingreso para : "+d);
			PolizaDeDiarioModel model=new PolizaDeDiarioModel(d);
			model.setJdbcTemplate(ServiceLocator.getJdbcTemplate());
			model.setSessionFactory(ServiceLocator.getSessionFactory());
			final PolizaDeDiario poliza=new PolizaDeDiario(model);
			poliza.load();
			poliza.generar();
			
		}
		
	}

}
