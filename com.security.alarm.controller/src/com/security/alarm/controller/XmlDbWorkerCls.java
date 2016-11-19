package com.security.alarm.controller;

import java.io.FileOutputStream;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Xml;

public class XmlDbWorkerCls extends XmlUtilsCls {
	private final String LOG_TAG="security.alarm.controller - XmlDbWorkerCls";
	private final String m_xmlFileName="security_alarm_settings.xml";
	private Context m_Ctx=null;
	private String m_XmlPath="Documents~"+m_xmlFileName;
	
	/** Not default constructor */
	public XmlDbWorkerCls(Context ctx){
		m_Ctx=ctx;
	}
	
	/** Adds data from security_alarm_settings.xml */
	public boolean addDataFromXml(){
		boolean result=false;
		
		try{
			XmlPullParser parser = createParserFromFile(m_XmlPath.trim().split("~")[0],m_XmlPath.trim().split("~")[1]);
			String nodeName="";
			SettingCls settingsInst=null;
			
			while (parser.getEventType()!= XmlPullParser.END_DOCUMENT){  
				switch(parser.getEventType()){
					case XmlPullParser.START_TAG:{
						nodeName=parser.getName().trim();
					
						if(nodeName.trim().equals("settings")){
							settingsInst=new SettingCls(m_Ctx);
						}
						else if(parser.getName().trim().equals("main_block_phone"))
							settingsInst.m_MainBlockPhone=parser.nextText();
						else if(parser.getName().trim().equals("operational_pwd"))
							settingsInst.m_OperationalPwd=parser.nextText();
						else if(parser.getName().trim().equals("program_pwd"))
							settingsInst.m_ProgramPwd=parser.nextText();
						else if(parser.getName().trim().equals("phone")){
							if(parser.getAttributeCount()>0){
								PhoneCls phoneObj=new PhoneCls();
								phoneObj.m_PhoneId=Short.parseShort(parser.getAttributeValue(0));
								phoneObj.m_PhoneNumber=parser.nextText();
								settingsInst.m_PhoneLst.add(phoneObj);
							}
						}
					}
					break;
					
					case XmlPullParser.END_TAG:{
						nodeName=parser.getName().trim();
						
						if(nodeName.trim().equals("settings")&&settingsInst!=null)
							settingsInst.setSettingData();
					}
					break;
				}
				
				parser.next();
			}
			
			result=true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе AddDataFromXml - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}	
	
	/**Writes startup settings into security_alarm_settings.xml*/
	public boolean writeXmlData(){
		boolean result=false;
		
		try{
			SettingCls settingsObj=(new SettingCls(m_Ctx)).getSettingData();
			String strDirName="";
			
			if(settingsObj==null)
				throw new Exception("Не удалось получить настройки");
			
			strDirName="Documents/security.alarm"+DateFormat.format("dd-MM-yyyy-hh-mm-ss", (new Date()));
			FileOutputStream targetFile=writeFileStream(strDirName,m_xmlFileName);				
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(targetFile,"utf-8");
		    serializer.startDocument("windows-1251", Boolean.valueOf(true));
		    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		    serializer.startTag("", "settings");
			serializer.startTag("", "main_block_phone");
			serializer.text(settingsObj.m_MainBlockPhone);
			serializer.endTag("", "main_block_phone");
			serializer.startTag("", "operational_pwd");
			serializer.text(settingsObj.m_OperationalPwd);
			serializer.endTag("", "operational_pwd");
			serializer.startTag("", "program_pwd");
			serializer.text(settingsObj.m_ProgramPwd);
			serializer.endTag("", "program_pwd");

			if(settingsObj.m_PhoneLst!=null&&settingsObj.m_PhoneLst.size()>0)
				for(int j=0;j<settingsObj.m_PhoneLst.size();j++){
					serializer.startTag("", "phone");
			        serializer.attribute("", "id", ""+settingsObj.m_PhoneLst.get(j).m_PhoneId);
					serializer.text(settingsObj.m_PhoneLst.get(j).m_PhoneNumber);
					serializer.endTag("", "phone");
			    }

			serializer.endTag("", "settings");
		    serializer.endDocument();
		    serializer.flush();
		    targetFile.close();
		    result=true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе WriteSettings - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}
	
}
