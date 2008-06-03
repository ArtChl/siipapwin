package com.luxsoft.siipap.clipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.utils.SQLUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;


/**
 * 
 * Exporta propiedades y datos importantes relacionadas a un cliente
 * de SiipapWin a Siipap
 * 
 * @author Ruben Cancino
 *
 */
public class ExportadorCliente extends JdbcDaoSupport{
	
	private File directorioDestino;
	private String templatePath="templates/toCarter.ftl";
	
	public String  exportarSaldo(final String cliente) {		
		return exportarSaldo(cliente, new Date());
		
	}
	
	@SuppressWarnings("unchecked")
	public String  exportarSaldo(final String cliente,final Date fecha){
		
		int year=Periodo.obtenerYear(fecha);		
		String mes=StringUtils.leftPad(String.valueOf(Periodo.obtenerMes(fecha)+1),2, '0');
		final Map root=new HashMap();
		
		//Formato para la cantidad
		NumberFormat nf=NumberFormat.getInstance(Locale.US);
		nf.setGroupingUsed(false);
		
		root.put("mes", mes);		
		root.put("clienteClave", cliente);		
		root.put("saldo", nf.format(getSaldo(cliente, fecha)));
		root.put("year", year);
		
		Configuration cfg=new Configuration();
		cfg.setDateFormat("dd/MM/yyyy");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setClassForTemplateLoading(ExportadorCliente.class, "/");
		
		
		try {						 
			final String fileName = "20"+obtenerConsecutivo()+".DOR";
			final Template temp=cfg.getTemplate(getTemplatePath());
			final File dir=getDirectorioDestino();
			final File target=new File(dir,fileName);			
			final FileOutputStream os=new FileOutputStream(target);			
			final Writer out=new OutputStreamWriter(os,"ISO-8859-1");
			
			temp.process(root, out);
			out.flush();
			out.close();
			os.close();			
			String pattern="Saldo del cliente {0} enviado a SIIPAP es de: {1} \n Archivo: {2}\n Fecha: {3} \n en el archivo: {4}";
			String res=MessageFormat.format(pattern, cliente,root.get("saldo"),fileName,fecha,target.getAbsolutePath());
			logger.info(res);
			return res;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return "ERROR en exportacion";
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private double getSaldo(final String cliente,final Date fecha){
		String sql=SQLUtils.loadSQLQueryFromResource("sql/SaldoCliente.sql");
		sql=sql.replaceAll("@CLAVE", "\'"+cliente+"\'");
		Object args[]={fecha,fecha,fecha,fecha,fecha,fecha,fecha,fecha,fecha,fecha,fecha};
		 int[] types= new int[]{Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.DATE,Types.DATE};
		Map<String ,Number> row=getJdbcTemplate().queryForMap(sql, args,types);		
		final Number res=row.values().iterator().next();
		logger.info("Saldo para el cliene: "+cliente+" :"+res);
		return res.doubleValue();
	}
	
	public String obtenerConsecutivo(){	
		try {
			final String sql="select consecutivos_dor.nextval from dual";
			final int next=getJdbcTemplate().queryForInt(sql);
			return StringUtils.leftPad(String.valueOf(next), 6,'0');
		} catch (BadSqlGrammarException be) {
			if(be.getSQLException().getErrorCode()==2289){
				logger.info("No existe la secuencia para los archivos dor generandola");
				createSequenceSupport();
				return obtenerConsecutivo();
			}
			logger.error(be);
		}
		return null;
	}
	
	public void createSequenceSupport(){
		final String sql="create sequence consecutivos_dor increment by 1 start with 10 maxvalue 999999";
		getJdbcTemplate().execute(sql);
	}
	
	
	public File getDirectorioDestino() {
		return directorioDestino;
	}
	public void setDirectorioDestino(File directorioDestino) {
		this.directorioDestino = directorioDestino;
	}

	public String getTemplatePath() {
		return templatePath;
	}
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}	

	public static void main(String[] args) {		
		final ExportadorCliente exportador=(ExportadorCliente)ServiceLocator.getDaoContext().getBean("exportadorClientes");
		//exportador.setDirectorioDestino(new File("C\\:basura\\"));
		exportador.exportarSaldo("P010394",DateUtils.obtenerFecha("30/01/2008"));
		//String next=exportador.obtenerConsecutivo();
		//System.out.println(next);
	}

}
