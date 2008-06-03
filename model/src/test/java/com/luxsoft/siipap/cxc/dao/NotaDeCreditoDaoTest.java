package com.luxsoft.siipap.cxc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.domain.Periodo;

public class NotaDeCreditoDaoTest extends AbstractDaoTest{
	
	private NotaDeCreditoDao dao;
	
	@NotTransactional
	public void testNotasPorPeriodo(){
		Periodo p=new Periodo("01/02/2007","28/02/2007");
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_NOTAS where FECHA between ? and ? "
				,new Object[]{p.getFechaInicial(),p.getFechaFinal()},new int[]{Types.DATE,Types.DATE});
		logger.info("Notas en el periodo: "+expected);
		
		List<NotaDeCredito> list=dao.buscarNotas(p);
		int actual=list.size();
		assertEquals(expected, actual);
		
		//Probar cliente not lazy
		for(NotaDeCredito n:list){
			assertNotNull(n.getCliente().getNombre());
		}
		
	}
	
	@NotTransactional
	public void testNotasConSaldo(){
		this.jdbcTemplate.execute(new ConnectionCallback(){

			public Object doInConnection(Connection con) throws SQLException, DataAccessException {
				System.out.println(con.getMetaData().getURL());
				return null;
			}
			
		});
		String clave="U050008";
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_NOTAS a where a.clave=? and serie not in(\'U\',\'V\',\'M\') and (round(-a.importe*1.15,2)-nvl((select sum(b.importe) from sw_pagos b where b.notapago_id=a.nota_id),0))>1",new Object[]{clave});
		System.out.println("Expected: "+expected);
		List<NotaDeCredito> notas=dao.buscarNotasConSaldo(clave);
		
		assertEquals(expected, notas.size());
	}

	public void setDao(NotaDeCreditoDao dao) {
		this.dao = dao;
	}
	

}
