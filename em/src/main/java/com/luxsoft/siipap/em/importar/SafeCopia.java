package com.luxsoft.siipap.em.importar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.luxsoft.siipap.em.replica.ReplicationUtils;

/**
 * Copia los archivos DBF de un directorio a otro para
 * procedimiento de importacion
 * 
 * @author Ruben Cancino
 *
 */
public class SafeCopia {
	
	private static Logger logger=Logger.getLogger(SafeCopia.class);
	
	public static synchronized void execute(String path,String target){
		File sourceDir=new File(path);		
		Assert.isTrue(sourceDir.isDirectory(),
				MessageFormat.format("El Directorio {0} no existe ",path));
		
		for(File file:sourceDir.listFiles(new VentasFilter())){
			if(file.isFile()){
				System.out.println("Copiando archivo: "+file.getPath());
				copiFile(file,target);
			}
		}
		logger.info("Archivos copiados exitosamente");
	}
	
	public static void copiFile(File f,String desPath){
		Assert.isTrue(f.isFile());
		final String dirPath=desPath;
		File targetDir=new File(dirPath);
		Assert.isTrue(targetDir.isDirectory(),"No existe el directorio de los DBF's: "+dirPath);
		String name=f.getName();
		File target=new File(targetDir,name);		
		try {
			
			target.createNewFile();
			FileChannel srcChannel=new FileInputStream(f).getChannel();
			FileChannel targetChannel=new FileOutputStream(target).getChannel();
			targetChannel.transferFrom(srcChannel, 0, srcChannel.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static class VentasFilter implements FilenameFilter{
		
		Set<String> minimos=new HashSet<String>();
		String[] dbfs={"ALMACE","MOCOCA","MOCOMO","MOVCRE","MVALMA","ALMACE"};
		
		public VentasFilter(){
			minimos.add("ARTICULO.DBF");
			minimos.add("CLIENTES.DBF");
			for(String s:dbfs){
				String sf=ReplicationUtils.resolveTable(s, new Date());
				minimos.add(sf+".DBF");
			}
		}

		public boolean accept(File dir, String name) {			
			return minimos.contains(name);
		}
		
	}
	
	public static void main(String[] args) {
		execute("G:\\SIIPAP\\ARCHIVOS\\DATO2007","H:\\2008");
	}

}
