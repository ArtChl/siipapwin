package com.luxsoft.siipap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;

import org.springframework.util.Assert;



/**
 * Utilerias para el manejo de archivos
 * 
 * @author Ruben Cancino
 *
 */
public class FileUtils {
	
	/**
	 * Copia el contenido de un directorio a otro, si el directorio destino no existe lo crea
	 * 
	 * @param sourceDirPath
	 * @param targetDirPath
	 * @param filter
	 */
	public static void copyDir(final String sourceDirPath,final String targetDirPath,final FilenameFilter filter){
		File sourceDir=new File(sourceDirPath);		
		File targetDir=new File(targetDirPath);
		if(!targetDir.exists()){
			targetDir.mkdirs();
		}
		copyDir(sourceDir, targetDir, filter);		
	}	
	
	/**
	 * Copia el contenido de un directorio a otro
	 * 
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @param filter
	 */
	public static void copyDir(final File sourceDir,final File targetDir,final FilenameFilter filter){
		Assert.isTrue(sourceDir.isDirectory());
		Assert.isTrue(targetDir.isDirectory());
		final File[] files;
		if(filter!=null)
			files=sourceDir.listFiles();
		else
			files=sourceDir.listFiles(filter);
		for(File file:files){
			if(file.isFile()){				
				copiFile(file,targetDir);
			}
		}
	}
	
	public static void copiFile(final File f,final File targetDir){
		System.out.println("Copiando archivo: "+f.getPath());
		Assert.isTrue(f.isFile());		
		Assert.isTrue(targetDir.isDirectory());
		
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
	
	public static void main(String[] args) {
		FileUtils.copyDir("Z:\\2007", "C:\\basura\\b7", null);
	}
	

}
