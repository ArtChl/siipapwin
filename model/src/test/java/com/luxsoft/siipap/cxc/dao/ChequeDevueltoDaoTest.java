package com.luxsoft.siipap.cxc.dao;

import java.util.Date;

import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.dao2.UniversalDao;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.utils.DateUtils;

public class ChequeDevueltoDaoTest extends AbstractDaoTest{
	
	private UniversalDao universalDao;
	private PagoMDao pagoMDao;
	@SuppressWarnings("unused")
	private NotaDeCreditoDao notaDeCreditoDao;
	
	
	public void testAddRemove(){
		final PagoM cheque=pagoMDao.buscarPagos(DateUtils.obtenerFecha("01/11/2007"),FormaDePago.H, "CRE").get(0);
		//final NotaDeCredito cargo=NotasFactory.generaNotaDeCargoPorChequeDevuelto(cheque);
		final ChequeDevuelto ch=new ChequeDevuelto();
		ch.setCliente(cheque.getCliente());
		ch.setNumero(Integer.valueOf(cheque.getReferencia()));
		ch.setBanco(ch.getBanco()!=null?ch.getBanco():"SB");
		ch.setImporte(cheque.getImporte().amount());
		ch.setOrigen(cheque);
		ch.setComentario("Prueba de cheque devuelto ");
		final Date fecha=new Date();
		ch.setFecha(fecha);
		ch.setCreado(fecha);
		ch.setYear(Periodo.obtenerYear(fecha));
		ch.setMes(Periodo.obtenerMes(fecha));
		
		//notaDeCreditoDao.salvar(cargo);
		universalDao.save(ch);
		setComplete();
		
	}

	public void setUniversalDao(UniversalDao universalDao) {
		this.universalDao = universalDao;
	}

	public void setPagoMDao(PagoMDao pagoMDao) {
		this.pagoMDao = pagoMDao;
	}

	public void setNotaDeCreditoDao(NotaDeCreditoDao notaDeCreditoDao) {
		this.notaDeCreditoDao = notaDeCreditoDao;
	}
	
	

}
