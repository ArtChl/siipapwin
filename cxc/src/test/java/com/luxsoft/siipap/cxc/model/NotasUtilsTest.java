package com.luxsoft.siipap.cxc.model;

import java.util.ArrayList;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

import junit.framework.TestCase;

/**
 * Pruebas unitarias de las utilerias prestadas por 
 * NotasUtils
 * 
 * @author Ruben Cancino
 *
 */
public class NotasUtilsTest extends TestCase{
	
	
	
	/**
	 * Probar una nota con 9 partidas
	 *
	 */
	public void testNotaDeCreditoOK(){
		List<NotasDeCreditoDet> partidas=partidas(9);
		List<NotaDeCredito> notas=NotasUtils.getNotasFromDetalles(partidas);
		assertEquals(1,notas.size());
		assertEquals(9,notas.get(0).getPartidas().size() );
		
	}
	
	/**
	 * Probar una nota con 15 partidas
	 *
	 */
	public void testNotaDeCredito15(){
		List<NotasDeCreditoDet> partidas=partidas(15);
		List<NotaDeCredito> notas=NotasUtils.getNotasFromDetalles(partidas);
		assertEquals(2,notas.size());
		assertEquals(NotasUtils.MAX_PARTIDAS,notas.get(0).getPartidas().size() );
		assertEquals(15-NotasUtils.MAX_PARTIDAS,notas.get(1).getPartidas().size() );
	}
	
	/**
	 * Probar una nota con 40 partidas
	 *
	 */
	public void testNotaDeCredito40(){
		List<NotasDeCreditoDet> partidas=partidas(40);
		List<NotaDeCredito> notas=NotasUtils.getNotasFromDetalles(partidas);
		int res=40/NotasUtils.MAX_PARTIDAS;
		assertTrue(res<=notas.size());
		for(int i=0;i<res;i++){
			assertTrue(NotasUtils.MAX_PARTIDAS>=notas.get(i).getPartidas().size() );
		}
		
	}
	
	public void testVinculacion(){
		List<NotasDeCreditoDet> partidas=partidas(40);
		List<NotaDeCredito> notas=NotasUtils.getNotasFromDetalles(partidas);		
		for(final NotaDeCredito n:notas){
			for(NotasDeCreditoDet det:n.getPartidas()){
				assertTrue(n==det.getNota());
			}
		}
	}
	
	/**
	 * Prueba que el numero de notas para una devolucion sea correcta
	 *
	 */
	public void testNumeroDeNotasPorDevolucion(){
		Devolucion devo=DatosDePrueba.createDevolucionDePrueba();
		final List<DevolucionDet> partidas=new ArrayList<DevolucionDet>();
		partidas.addAll(devo.getPartidas());
		int expected=2;		
		List<NotaDeCredito> notas=NotasUtils.generarNotasDeCreditoParaDevolucion(devo);
		assertEquals(expected, notas.size());
		
		CantidadMonetaria monto=CantidadMonetaria.pesos(0);
		for(int i=0;i<NotasUtils.MAX_DEVO_PARTIDAS;i++){
			monto=monto.add(partidas.get(i).getImporteAsMoneda());
		}
		assertEquals(monto, notas.get(0).getImporte());
		devo.actualizarImporte();
		
		final CantidadMonetaria imp1=notas.get(0).getImporte();
		System.out.println("Imp1: "+imp1);
		final CantidadMonetaria imp2=notas.get(1).getImporte();
		System.out.println("Imp2: "+imp2);
		final CantidadMonetaria tot=imp1.add(imp2);
		System.out.println("Tot: "+tot);
		assertEquals(devo.getImporte(),tot);
	}
	
	/**
	 * Prueba la generacion correcta de notas de devolucion 
	 *
	 */
	public void testNotaPorDevolucion()
	{
		Devolucion devo=DatosDePrueba.createDevolucionDePrueba();
		final List<DevolucionDet> partidas=new ArrayList<DevolucionDet>();
		partidas.addAll(devo.getPartidas());
		System.out.println("Partidas: "+partidas.size());
		final List<DevolucionDet> p1=partidas.subList(0, NotasUtils.MAX_DEVO_PARTIDAS);
		final CantidadMonetaria e1=calcularImporte(p1);
		
		final NotaDeCredito nota=NotasUtils.createNotaDeCreditoDeDevolucion(p1);
		System.out.println("Nota1 "+nota.getImporte());
		assertEquals(e1, nota.getImporte());
		for(DevolucionDet dd:p1){
			assertTrue(nota==dd.getNota());
		}
		
		final List<DevolucionDet> p2=partidas.subList(NotasUtils.MAX_DEVO_PARTIDAS,partidas.size());
		final CantidadMonetaria e2=calcularImporte(p2);
		final NotaDeCredito nota2=NotasUtils.createNotaDeCreditoDeDevolucion(p2);
		System.out.println("Nota2 "+nota2.getImporte());
		assertEquals(e2, nota2.getImporte());
		
	}
	
	public void testDescuentoFinanciero(){
		final Venta v=DatosDePrueba.ventasDePrueba().get(0);
		v.getCredito().setPlazo(30);
		v.setFecha(DateUtils.obtenerFecha("10/10/2007"));
		double desc=NotasUtils.calcularDescuentoFinanciero(v, DateUtils.obtenerFecha("16/10/2007"), 1.0);
		assertEquals(.80, desc);
		
	}
	
	private CantidadMonetaria calcularImporte(final List<DevolucionDet> dets){
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(DevolucionDet d:dets){
			d.getVentaDet();
			importe=importe.add(CantidadMonetaria.pesos(d.getImporte()));
		}
		return importe;
	}
	
	private List<NotasDeCreditoDet> partidas(int n){
		List<NotasDeCreditoDet> partidas=new ArrayList<NotasDeCreditoDet>();
		for(int i=0;i<n;i++){
			NotasDeCreditoDet det=new NotasDeCreditoDet();
			det.setClave("U05008");
			partidas.add(det);
		}
		return partidas;
	}
	
	
	

}
