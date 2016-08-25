package akai.pet.one.piece.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Formatter;

import android.os.Environment;



public class KLog2File {
	
	private static final boolean LOG_K = false;

	public static void saveLog2File(Exception e){
		
		if(!LOG_K)
			return;
		
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		
		Throwable cause = e.getCause();
		while(cause != null){
			cause.printStackTrace(printWriter);
			cause = e.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		
		//save to file
		try {
			long timestamp = System.currentTimeMillis();
			String fileName = "crash-" + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = "/sdcard/0kai/log/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(result.getBytes());
				fos.close();
			}
		}catch(Exception ex){}
	}
}
