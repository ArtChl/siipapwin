package com.luxsoft.siipap.ventas.servicios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Sucursales;
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
public class PolizaDeIngreso {
	
	private final PolizaDeIngresoModel model;	
	private final Date fecha;
		
	
	public PolizaDeIngreso(PolizaDeIngresoModel model){
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
		cfg.setClassForTemplateLoading(PolizaDeIngreso.class, "/");
		
		final DateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
		final DateFormat df2=new SimpleDateFormat("ddMMM");
		final String sucursal=StringUtils.leftPad(String.valueOf(   ( Sucursales.fixSucursalNumero(suc)) ),3, '0');
		final String sucursalShort=StringUtils.leftPad(String.valueOf(   ( Sucursales.fixSucursalNumero(suc)) ),2, '0');
		final String sucursalName=suc.name();
		
		final double comisionesBancarias=0;
		final double comisionesBancariasSinIva=0;
		
		final EventList<Venta> ventasCam=model.getVentas(suc, "CAM");
		final EventList<Venta> ventasMos=model.getVentas(suc, "MOS");
		final EventList<Venta> ventasCre=model.getVentas(suc, "CRE");
		
		final EventList<Pago> pagosCre=model.getPagos(suc, "CRE");
		//final EventList<Pago> pagosMos=model.getPagos(suc, "MOS");
		final EventList<Pago> pagosTarjetaCre=model.buscarPagosConTarjeta(pagosCre);
		final EventList<Pago> pagosCam=model.getPagos(suc, "CAM");
		final EventList<Pago> pagosTarjetaCam=model.buscarPagosConTarjeta(pagosCam);
		
		final double ietuMos=model.totalSinIva(ventasMos);
		double ietuCam=model.calcularIetu(model.buscarPagosConTarjeta(pagosCam));
		final double anticipoCam=model.anticipos(suc).amount().doubleValue();
		//ietuCam+=anticipoCam;
		double ivaAnticipoCam=BigDecimal.valueOf( (anticipoCam)*.15).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
		
		final List<Deposito> bancos=model.getDepositos(suc, "MOS");
		
		//final double diferencia=model.diferenciaEnPagoContraFactura(pagosMos);  NOTA Hasta controlar ventas
		final double otrosGastos=0;//diferencia<0?Math.abs(diferencia):0;
		final double otrosIngresos=0;//diferencia>0?Math.abs(diferencia):0;
		
		Map root=new HashMap();
		root.put("fecha", df.format(fecha));
		root.put("sucursal", sucursal);
		root.put("sucursalShort", sucursalShort);
		root.put("sucursalName", sucursalName);
		root.put("bancos", bancos);
		root.put("facturasCam", model.getVentas(suc, "CAM"));
		root.put("facturasCre", model.getVentas(suc, "CRE"));
		root.put("comisionesBancarias", comisionesBancarias);
		root.put("comisionesBancariasSinIva",comisionesBancariasSinIva);
		root.put("pagosConTarjetaCre",pagosTarjetaCre);
		root.put("ivaPagosConTarjetaCre", model.ivaPagos(pagosTarjetaCre));
		root.put("ventasMos", model.totalSinIva(ventasMos));
		root.put("ivaEnVentasMos", model.ivaVentas(ventasMos));
		root.put("ventasCre", model.totalSinIva(ventasCre));
		root.put("ventasCam", model.totalSinIva(ventasCam));
		root.put("ventasIvaCre", model.ivaVentas(ventasCre));
		root.put("ventasIvaCam", model.ivaVentas(ventasCam));
		root.put("pagosConTarjetaCam", pagosTarjetaCam);
		root.put("ivaPagosConTarjetaCam", model.ivaPagos(pagosTarjetaCam));
		root.put("ietuMos", ietuMos);
		root.put("ietuCam", ietuCam);
		root.put("anticipoCam", anticipoCam);
		root.put("ivaAnticipoCam", ivaAnticipoCam);
		root.put("otrosGastos",otrosGastos );
		root.put("otrosIngresos",otrosIngresos);
		
		try {
			final String pattern="V{0}{1}.POL";
			 
			final String fileName = MessageFormat.format(pattern, df2.format(fecha),StringUtils.leftPad(String.valueOf(suc.getNumero()),2, '0'));
			final Template temp=cfg.getTemplate("templates/polizaDeIngresoMos.ftl");			
			
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
		final Periodo per=new Periodo("10/04/2008","10/04/2008");
		final List<Date> dias=per.getListaDeDias();
		for(Date d:dias){
			System.out.println("Generando poliza Ingreso para : "+d);
			PolizaDeIngresoModel model=new PolizaDeIngresoModel(d);
			model.setSessionFactory(ServiceLocator.getSessionFactory());
			final PolizaDeIngreso poliza=new PolizaDeIngreso(model);
			poliza.load();
			poliza.generar();
		}
		
	}

}
