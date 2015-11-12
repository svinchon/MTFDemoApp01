package com.diy.helpers.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class Utils {
	
	public static String generateUniqueIdentifier() {
		String strReturn = null;
		UUID ui = UUID.randomUUID();
		strReturn = ui.toString();
		ui = null;
		return strReturn;
	}

	public static String convertFile2String(String path) {
		String strReturn = "";
		try {
			File f = new File(path);
			FileInputStream fis = new FileInputStream(f);
			byte[] bDoc = new byte[fis.available()];
			fis.read(bDoc);
			strReturn = new String(bDoc);
			bDoc = null;
			fis.close();
			f = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strReturn;
	}
	

	public static void convertByte2File(byte[] bData, String strFileName) {
		try {
			File outFile = new File(strFileName);
			if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
                System.out.println("Directory Created");
            }
			FileOutputStream fos = new FileOutputStream(outFile);
			fos.write(bData);
			System.out.println("File written");
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] convertFile2Byte(String strFileName) {
		byte[] b = null;
		try {
			File inFile = new File(strFileName);
			FileInputStream fis = new FileInputStream(inFile);
			b = new byte[fis.available()];
			fis.read(b);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public static int generateRandomInteger(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public static String generateTimeStamp() {
		return generateTimeStamp("yyyy-MM-dd@HH-mm-ss-SSS");
	}
	
	public static String generateTimeStamp(String mask) {
		SimpleDateFormat DateFormat = new SimpleDateFormat(mask, Locale.US);
		//SimpleDateFormat DateFormat = new SimpleDateFormat("ddHmsSSS", Locale.US);
		Date d = new Date();
		return DateFormat.format(d);
	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
	
	public static void log(String msg, Object classInstance) {
		String className = classInstance.getClass().getSimpleName();
		//String className = classInstance.getClass().getCanonicalName();
		System.out.println(generateTimeStamp("MM/dd@HH:mm") + " - " + className + " => " + msg);
	}

	public static void log(String msg, String context) {
		System.out.println(generateTimeStamp("MM/dd@HH:mm") + " - " + context + " => " + msg);
	}
	
}
