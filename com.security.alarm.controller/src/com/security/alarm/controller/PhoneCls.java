package com.security.alarm.controller;

class PhoneCls {
	public short m_PhoneId=-1;
	public String m_PhoneNumber="";
	
	/** Default constructor */
	public PhoneCls(){}
	
	/** Not default constructor */
	public PhoneCls(short phoneId, String phoneNumber){
		m_PhoneId=phoneId;
		m_PhoneNumber=phoneNumber;
	}
}
