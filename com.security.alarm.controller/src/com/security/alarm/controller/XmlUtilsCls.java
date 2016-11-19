package com.security.alarm.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class XmlUtilsCls {
	private final String LOG_TAG="security.alarm.controller - XmlUtilsCls";
	
	/** Default constructor */
	public XmlUtilsCls(){
	}
	
	/**Writes smth into file on SD card*/
	public FileOutputStream writeFileStream(String strPathToFile,String strFileName){
		FileOutputStream result=null;
		
		try{
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				throw new Exception("Не найдена SD карта");

			File sdPath = Environment.getExternalStorageDirectory();	
			sdPath = new File(sdPath.getAbsolutePath() + "/" + strPathToFile);
			
			if(!sdPath.exists())
				if(!sdPath.mkdirs())
					throw new Exception("Невожможно создать дерево каталогов");
			
			File sdFile = new File(sdPath, strFileName);
			
			if(sdFile.exists())
				if(!sdFile.delete()){
					strPathToFile=strPathToFile.split(".")[0]+DateFormat.format("dd-MM-yyyy-hh-mm-ss", (new Date()))+"."+strPathToFile.split(".")[1];
					sdFile = new File(sdPath, strPathToFile);
				}

			if(!sdFile.createNewFile())
				throw new Exception("Невожможно создать новый файл"); 

			result=new FileOutputStream(sdFile);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeFile - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
	
		return result;
	}
	
	/**Writes smth into file on SD card*/
	public FileWriter writeFile(String strPathToFile,String strFileName){
		FileWriter result=null;
		
		try{
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				throw new Exception("Не найдена SD карта");
		
			File sdPath = Environment.getExternalStorageDirectory();
			sdPath = new File(sdPath.getAbsolutePath() + "/" + strPathToFile);
			
			if(!sdPath.exists())
				if(!sdPath.mkdirs())
					throw new Exception("Невожможно создать дерево каталогов");
			
			File sdFile = new File(sdPath, strFileName);
			
			if(sdFile.exists())
				if(!sdFile.delete()){
					strPathToFile=strPathToFile.split(".")[0]+DateFormat.format("dd-MM-yyyy-hh-mm-ss", (new Date()))+"."+strPathToFile.split(".")[1];
					sdFile = new File(sdPath, strPathToFile);
				}

			if(!sdFile.createNewFile())
				throw new Exception("Невожможно создать новый файл"); 

			result=new FileWriter(sdFile);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeFile - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
	
		return result;
	}
	
	/** Creates parser instance from xml file*/
	public XmlPullParser createParserFromFile(String strPathToFile,String strFileName){
		XmlPullParser result=null;
		
		try{
		    
		    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				throw new Exception("Не найдена SD карта");
			
			File sdPath = Environment.getExternalStorageDirectory();
			sdPath = new File(sdPath.getAbsolutePath() + "/" + strPathToFile);
			File sdFile = new File(sdPath, strFileName);
			
			if(!sdFile.exists())
				throw new Exception("Не найден файл");
				
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		    factory.setNamespaceAware(true);
		    result = factory.newPullParser();
		    result.setInput(new FileReader(sdFile));
		}
		catch(Exception ex){
			String strErr="Ошибка в методе createParserFromFile - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
}
