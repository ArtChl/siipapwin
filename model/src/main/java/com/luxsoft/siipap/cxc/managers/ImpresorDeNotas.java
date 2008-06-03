package com.luxsoft.siipap.cxc.managers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.CantidadMonetaria;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.ImporteALetra;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

public class ImpresorDeNotas {

	public static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	public static final DateFormat df2 = new SimpleDateFormat("HH:mm:ss");

	
	
	public static void imprimir(final NotaDeCredito nota) throws Exception {
		/**
		File dir=new File("notas");
		if(!dir.exists()){
			dir.
		}
*/
		//FileOutputStream writer = new FileOutputStream(nota.getId().toString()
		FileOutputStream writer = new FileOutputStream("IMPRNOTA"+ ".txt");
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(writer,
				"ASCII"));
		PrintWriter p = new PrintWriter(w);
		p.println("<HEAD>");
		p.println(nota.getCliente().getCuentacontable() + ";");
		p.println(nota.getTipo() + ";");
		final String numero=nota.getNumero()!=0?String.valueOf(nota.getNumero()):nota.getId().toString();
		p.println(numero+ ";");
		p.println(nota.getClave() + ";");
		p.println(nota.getCliente().getNombre().trim() + ";");		
		p.println(nota.getCliente().getRfc() + ";");
		String msg = "{0} {1} {2} {3};";
		// p.println(nota.getCliente().getCalle()+"
		// "+nota.getCliente().getNMARCELINO DAVALOS NO. 22 ALGARIN CUAHUTEMOC
		// C.P 06880"+";");
		p.println(MessageFormat.format(msg, nota.getCliente().getCalle(), nota
				.getCliente().getColonia(), nota.getCliente().getEntidad(),
				nota.getCliente().getCpostal()));
		p.println(df.format(nota.getFecha()) + ";");
		//p.println(nota.getComentario()!=null?nota.getComentario():""+";");
		p.println("<DETALLE>");
		final NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		for (NotasDeCreditoDet det : nota.getPartidas()) {
			
			String sucursal = String.valueOf(det.getSucDocumento());
			sucursal = StringUtils.leftPad(sucursal, 2);
			
			String tipo = det.getTipoDocumento();
			tipo = StringUtils.leftPad(tipo, 1);
			
			String factura = String.valueOf(det.getNumDocumento());
			factura = StringUtils.leftPad(factura, 7);
			
			String fiscal="";
			if(det.getFactura()!=null){
				fiscal = String.valueOf(det.getFactura().getNumeroFiscal());				
			}
			fiscal = StringUtils.leftPad(fiscal.trim(), 7);
			CantidadMonetaria devoluciones;
			if(det.getFactura()!=null){
				devoluciones=CantidadMonetaria.pesos(det.getFactura().getDevolucionesCred());
				devoluciones=devoluciones.divide(1.15);
			}else
				devoluciones=CantidadMonetaria.pesos(0);
			
			String importe;
			if(det.getFactura()!=null){
				importe = nf.format(det.getFactura().getSubTotal().add(devoluciones).amount().abs().doubleValue());//.toString(); //
				importe = StringUtils.leftPad(importe.trim(), 12);
			}else
				importe = nf.format(det.getImporte().amount().doubleValue());//.toString(); //
			
			
			
			String desc = nf.format(Math.abs(det.getDescuento()));
			desc = StringUtils.leftPad(desc.trim(), 5);
			final BigDecimal impN=det.getImporte().abs().getAmount().divide(BigDecimal.valueOf(1.15), RoundingMode.HALF_EVEN);
			String impNota = nf.format(impN.doubleValue())+ ";";
			impNota = StringUtils.leftPad(impNota.trim(), 12);
			
			
			String m2 = "{0} {1}  {2}-{3}  {4}  {5}  {6}";
			p.println(MessageFormat.format(m2
					,sucursal
					,tipo
					,factura
					,fiscal
					,importe
					,desc
					,impNota
					)
				);
		}
		String comentario=nota.getComentario();
		
		if(!StringUtils.isEmpty(comentario) && nota.getPartidas().size()<=10)
			p.println(comentario.trim()+";");  //Temporalmente en lo que se libera el parche de Andrés *** 25/10/07 ***
		
		p.println("<TOTALES>");
		p.println(df2.format(new Date()) + ";");
		p.println("REV" + ";");
		p.println(nota.getCliente().getDia_revision() + ";");
		p.println("PAG" + ";");
		p.println(nota.getCliente().getDia_pago() + ";");
		p.println("AGE" + ";");
		p.println(nota.getCliente().getVendedor() + ";");
		p.println("COB" + ";");
		p.println(nota.getCliente().getCobrador() + ";");
		p.println(nf.format(nota.getImporte().abs().amount().doubleValue()) + ";");
		
		p.println("("+ImporteALetra.aLetra(nota.getTotalAsMoneda2().abs())+");");
		p.println(nf.format(nota.getIva().abs().amount().doubleValue()) + ";");
		p.println(nf.format(nota.getTotalAsBigDecimal().abs().doubleValue()));
		p.flush();
		p.close();
		writer.flush();
		writer.close();
		//Runtime.getRuntime().exec(new String[]{"c://SIIWUT-1.EXE",nota.getId().toString(),"33"});
		/**
		ProcessBuilder builder=new ProcessBuilder("SIIWUT-1.EXE",nota.getId().toString(),"33");
		Process proc=builder.start();
		proc.destroy();
		**/
		System.out.println("Archivo de impresion generado para nota: "+nota.getId());
		/**
		Runtime r=Runtime.getRuntime();
		Process proc=r.exec(new String[]{"IMPRNOTA.BAT"});
		int res=proc.waitFor();
		System.out.println("Terminacion:" +res);
		**/
	}
	
	public static void imprimirNotaDeDevo(final NotaDeCredito nota) throws Exception {
		
		final NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		
		//FileOutputStream writer = new FileOutputStream(nota.getId().toString()
		FileOutputStream writer = new FileOutputStream("IMPRNOTA"+ ".txt");
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(writer,
				"ASCII"));
		PrintWriter p = new PrintWriter(w);
		p.println("<HEAD>");
		p.println(nota.getCliente().getCuentacontable() + ";");
		p.println(nota.getTipo() + ";");
		final String numero=nota.getNumero()!=0?String.valueOf(nota.getNumero()):nota.getId().toString();
		p.println(numero+ ";");
		p.println(nota.getClave() + ";");
		p.println(nota.getCliente().getNombre().trim() + ";");		
		p.println(nota.getCliente().getRfc() + ";");
		String msg = "{0} {1} {2} {3};";
		p.println(MessageFormat.format(msg, nota.getCliente().getCalle(), nota
				.getCliente().getColonia(), nota.getCliente().getEntidad(),
				nota.getCliente().getCpostal()));
		p.println(df.format(nota.getFecha()) + ";");
		//p.println(nota.getComentario()!=null?nota.getComentario():""+";");
		p.println("<DETALLE>");
		insertarDetalleParaDevolucion(nota, p);		
		p.println("<TOTALES>");		
		p.println(df2.format(new Date()) + ";");
		p.println("REV" + ";");
		p.println(nota.getCliente().getDia_revision() + ";");
		p.println("PAG" + ";");
		p.println(nota.getCliente().getDia_pago() + ";");
		p.println("AGE" + ";");
		p.println(nota.getCliente().getVendedor() + ";");
		p.println("COB" + ";");
		p.println(nota.getCliente().getCobrador() + ";");		
		p.println(nf.format(nota.getImporte().abs().amount().doubleValue()) + ";");		
		p.println("("+ImporteALetra.aLetra(nota.getTotalAsMoneda2().abs())+");");
		p.println(nf.format(nota.getIva().abs().amount().doubleValue()) + ";");
		p.println(nf.format(nota.getTotalAsBigDecimal().abs().doubleValue()));
		p.flush();
		p.close();
		writer.flush();
		writer.close();
	}
	
	public static void insertarDetalleParaDevolucion(final NotaDeCredito nota,final PrintWriter p){
		final NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		
		final NumberFormat nf2=NumberFormat.getNumberInstance();
		
		final NumberFormat nf3=NumberFormat.getNumberInstance();
		nf3.setMinimumFractionDigits(1);
		Venta factura=nota.getPartidas().get(0).getFactura();
		
		String comentario=nota.getComentario()!=null?nota.getComentario():"";		
		p.println("   "+comentario+" DE LA FACTURA: "+factura.getNumero()+"/"+factura.getNumeroFiscal()+";"); 
				
		double subtotal=0;
		for(DevolucionDet det:nota.getDevolucion().getPartidas()){
			
			if(det.getNota().getId()==nota.getId()){
				
				String cantidad=nf2.format(det.getCantidad()*det.getFactorDeConversionUnitaria());
				String desc=det.getArticulo().getDescripcion1();
				String kilos=StringUtils.leftPad(nf3.format(det.getArticulo().getKilos().doubleValue()),5);
				String precio=nf.format(det.getVentaDet().getPrecioFacturado());
				double imp=det.getVentaDet().getPrecioFacturado()*det.getCantidad();
				String importe=nf.format(imp);
				//String importe=nf.format(det.getImporte());
				
				String c1=StringUtils.leftPad(String.valueOf(cantidad),10);
				//String c2=StringUtils.rightPad(desc.substring(0, 23),23);				
				String c2=StringUtils.rightPad(StringUtils.left(desc, 23),23);
				//String c3=StringUtils.leftPad(kilos,5);
				String c4=StringUtils.leftPad(String.valueOf(precio),10);
				String c5=StringUtils.leftPad(String.valueOf(importe),12);
				
				String m2 = "{0} {1} {2} {3} {4};";
				p.println(MessageFormat.format(m2
						,c1
						,c2
						,kilos
						,c4
						,c5
						)
					);
				subtotal+=imp;
			}			
			
		}
		//subtotal=nota.getDevolucion().getImporte().getAmount().doubleValue();
		
		p.println("                                              SUMA:"+StringUtils.leftPad(nf.format(subtotal),13)+";");
		/**
		CantidadMonetaria subTotalAsMoneda=CantidadMonetaria.pesos(subtotal);
		CantidadMonetaria totalSinDescuentos=MonedasUtils.aplicarDescuentosEnCascada(subTotalAsMoneda
				, nota.getDevolucion().getDescuento1()/100,nota.getDevolucion().getDescuento2()/100);
		final double impDescuento=subTotalAsMoneda.amount().doubleValue()-totalSinDescuentos.amount().doubleValue();
		final double descuento=nota.getDevolucion().getDescuentoNeto();
		**/
		
		final double descuento=nota.getDevolucion().getDescuentoNeto();
		final double subTotalCortes=subtotal*(descuento/100);
		NumberFormat dnf=NumberFormat.getNumberInstance();
		dnf.setMaximumIntegerDigits(2);
		dnf.setMaximumFractionDigits(2);
		dnf.setMinimumFractionDigits(2);
		
		final CantidadMonetaria cortesImpM=nota.getDevolucion().getImpCortes();
		final double cortes=cortesImpM!=null?cortesImpM.amount().doubleValue():0;
		p.println("                                            "+"Cortes:"+StringUtils.leftPad(nf.format(cortes),13)+";");
		
		if(descuento!=0){
			String desc=StringUtils.leftPad(" "+dnf.format(descuento),7);			
			p.println("                                            "+desc+StringUtils.leftPad(nf.format(subTotalCortes),13)+";");
		}
		/**
		final CantidadMonetaria cortesImpM=nota.getDevolucion().getImpCortes();
		final double cortes=cortesImpM!=null?cortesImpM.amount().doubleValue():0;
		p.println("                                            "+"Cortes:"+StringUtils.leftPad(nf.format(cortes),13)+";");
		**/
		
	}
	
	public static void main(String[] args) throws Exception {
		final Long id=107854L;
		NotaDeCredito nota=ServiceLocator.getNotasManager().buscarNotaDevolucion(id);
		//final Long id=107854L;
		//NotaDeCredito nota=ServiceLocator.getCXCManager().getNotaDeCreditoDao().buscar(id);
		if(nota!=null)
			ImpresorDeNotas.imprimirNotaDeDevo(nota);
			//ImpresorDeNotas.imprimir(nota);
		else 
			System.out.println("No existe la nota: "+id);
	}

}