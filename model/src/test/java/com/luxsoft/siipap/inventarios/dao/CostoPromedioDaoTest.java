package com.luxsoft.siipap.inventarios.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.inventarios.domain.CostoPromedio;

public class CostoPromedioDaoTest extends AbstractDaoTest{
	
	private CostoPromedioDao dao;
	private ArticuloDao articuloDao;
	
	@SuppressWarnings("unchecked")
	@NotTransactional
	public void testCostoMasReciente(){
		String sql="select PROMEDIO_ID from sw_promedios where articulo_id=524 order by to_date(periodo,\'MM/YYYY\') desc";
		List<Map<String, Object>> rows=jdbcTemplate.queryForList(sql);
		long expected=((BigDecimal)rows.get(0).get("PROMEDIO_ID")).longValue();
		Articulo a=articuloDao.buscarPorClave("PB36");
		CostoPromedio cp=dao.buscarCostoMasReciente(a);
		assertEquals(expected, cp.getId().longValue());
		
	}
	
	

	
	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}

	public void setDao(CostoPromedioDao dao) {
		this.dao = dao;
	}
	
	

}
