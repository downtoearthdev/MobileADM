package com.scorchedcode.darklust.mobileadm;

import android.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class IPThread implements Runnable
{
	private String ipaddress = null;
	private CopyOnWriteArrayList<Device> ips = null; 
	public IPThread(int octet, CopyOnWriteArrayList<Device> ips) {
		this.ips = ips;
		try
		{
			String wifi = "";
			Enumeration<InetAddress> nets = NetworkInterface.getByName("wlan0").getInetAddresses();
			while(nets.hasMoreElements()) {
				InetAddress net = nets.nextElement();
				if(net.getHostAddress() != "127.0.0.1")
					wifi = net.getHostAddress();
			}
			this.ipaddress = wifi.replaceFirst("\\.(\\d+)$", "." + String.valueOf(octet));
		}
		catch (SocketException e)
		{}
	}

	@Override
	public void run()
	{
		try
		{
			Socket sc = new Socket(ipaddress, 5555);
			sc.close();
			ips.add(new Device(ipaddress));
		}
		catch (IOException e)
		{

		}
	}


}
