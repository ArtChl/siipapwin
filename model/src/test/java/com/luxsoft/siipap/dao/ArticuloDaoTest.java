package com.luxsoft.siipap.dao;

import java.util.List;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.domain.ArticuloRow;

public class ArticuloDaoTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private ArticuloDao dao;

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:swx-dao-ctx.xml"};
	}
	
	public void testArticuloBrowse(){
		List<ArticuloRow> articulos=dao.browse();
		for(ArticuloRow r:articulos){
			System.out.println(r);
		}
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_ARTICULOS");
		assertEquals(expected, articulos.size());
	}

	public void setDao(ArticuloDao dao) {
		this.dao = dao;
	}
	
	
}
