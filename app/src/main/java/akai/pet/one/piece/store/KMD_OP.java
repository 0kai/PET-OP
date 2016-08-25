package akai.pet.one.piece.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * this file only using in the java project, to encrypt the image files in the pc
 * @author K
 */
public class KMD_OP
{
	/**
	 * the suffix of files, akai record the data
	 */
	private final static String FILE_SUFFIX = ".dat";
	
	/**
	 * encrypt the image files
	 * @param filePath
	 */
	public static void encrypt(String filePath)
	{
		byte[] tempbytes = new byte[100];
        try
        {
        	InputStream in = new FileInputStream(filePath);
        	OutputStream out = new FileOutputStream(filePath.subSequence(0, filePath.lastIndexOf("."))+".dat");
        	in.read(tempbytes);
        	for(int i=0; i<100-1; i+=2)
        	{
        		byte temp = tempbytes[i];
        		tempbytes[i] = tempbytes[i+1];
        		tempbytes[i+1] = temp;
        	}
        	byte temp = tempbytes[5];
    		tempbytes[5] = tempbytes[99];
    		tempbytes[99] = temp;
        	out.write(tempbytes);
			while (in.read(tempbytes) != -1)
			{
			    out.write(tempbytes);
			}
		}
        catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * using a pc-path and encrypt all image files to .dat image
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("running===========");
		File root = new File("F:/app_images/");
	    File[] files = root.listFiles();
	    for(File file:files)
	    {     
	    	System.out.println(file.getAbsolutePath());
	    	if(!file.isDirectory())
	    	{
	    		KMD_OP.encrypt(file.getAbsolutePath());
	    		System.out.println(file.getAbsolutePath());
	    	}  
	    }
	}
}
