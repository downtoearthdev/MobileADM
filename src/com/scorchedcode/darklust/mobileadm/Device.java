package com.scorchedcode.darklust.mobileadm;

import android.graphics.*;
import java.io.*;
import java.util.*;

public class Device
{
	private String ip;
	private String model = null;
	//private Process process = null;
	public Device(String ip)
	{
		this.ip = ip;
		final ArrayList<List> commands = new ArrayList<List>(); 
		commands.add(Arrays.asList(("adb -s " + ip + ":5555 shell getprop ro.product.model").split(" ")));
		commands.add(Arrays.asList(("adb -s " + ip + ":5555 shell getprop ro.product.manufacturer").split(" ")));
		commands.add(Arrays.asList(("adb -s " + ip + ":5555 shell getprop ro.build.characteristics").split(" ")));
		try
		{
			Runtime.getRuntime().exec(" adb connect " + ip);
			try
			{
				Thread.sleep(700); // Required to prevent Error: Device not found
			}
			catch (InterruptedException e) {}
		}
		catch (IOException e) {}
		model = MainActivity.executeForResult(commands, true, null);
	}

	public String getModel()
	{
		return (model.contains("offline")) ? "Requires RSA Key!" : model.split("\n")[0];
	}

	public String getIP()
	{
		return ip;
	}
	
	public String getManufacturer() {
		return (model.contains("offline")) ? "" :  model.split("\n")[1].replaceFirst("^\\.", "^\\.".toUpperCase());
	}
	
	public Bitmap getType() {
		return (model.contains("offline")) ? null : (model.split("\n")[2].contains("tablet")) ?
			BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.drawable.ic_action_computer) :
			BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.drawable.ic_action_phone);
	}
}
