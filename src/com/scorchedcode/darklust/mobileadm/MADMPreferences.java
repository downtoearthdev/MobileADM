package com.scorchedcode.darklust.mobileadm;

import android.content.*;
import android.os.*;
import android.preference.*;
import java.io.*;

public class MADMPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		/*if(p2.equals("pref_apps"))  {
			File proposed = new File(p1.getString("pref_apps", Environment.getExternalStorageDirectory().toString()));
			if(proposed.exists())
				((MainActivity)getApplicationContext()).setupProjects(proposed);
		}*/
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	
	
	
	
	
}
