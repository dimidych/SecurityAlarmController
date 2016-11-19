package com.security.alarm.controller;

import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AboutFragment extends DialogFragment implements OnClickListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		    getDialog().setTitle("О проге...");
		    View v = inflater.inflate(R.layout.about_fragment, null);
		    v.findViewById(R.id.btnOkAbout).setOnClickListener(this);
		    return v;
		  }
	
	@Override
	public void onClick(View v) {
		dismiss();
	}
	
}
