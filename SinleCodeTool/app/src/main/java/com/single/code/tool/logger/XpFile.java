package com.single.code.tool.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * @author yao.guoju
 *
 */
public class XpFile {
	
	public static void dump(File file,String info) throws IOException {
		
		if(!file.exists() && !file.isDirectory()) {
			String root = file.getParent();
			File floder = new File(root);
			if(floder.isDirectory() && !floder.exists()) {
				floder.mkdirs();
			}
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file,true);
		fos.write(info.getBytes("utf-8"));
		fos.flush();
		fos.close();
	}
	
}
