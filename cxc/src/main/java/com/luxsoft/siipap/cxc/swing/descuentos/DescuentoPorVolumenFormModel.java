package com.luxsoft.siipap.cxc.swing.descuentos;

import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;

public class DescuentoPorVolumenFormModel extends AbstractGenericFormModel<DescuentoPorVolumen, Long>{

	public DescuentoPorVolumenFormModel() {
		this(new DescuentoPorVolumen());
		
	}

	public DescuentoPorVolumenFormModel(Object bean) {
		super(bean);
	}
	
	

}
