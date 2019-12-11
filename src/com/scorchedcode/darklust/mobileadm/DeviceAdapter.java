package com.scorchedcode.darklust.mobileadm;

import android.content.*;
import android.view.*;
import android.widget.*;
import java.net.*;
import java.util.concurrent.*;

public class DeviceAdapter extends ArrayAdapter
{
	Context app = null;
	int view = 0;
	
	public DeviceAdapter(Context c, int res, CopyOnWriteArrayList<Device> ips) {
		super(c, res, ips);
		this.app = c;
		this.view = res;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		RelativeLayout deviceView;
		if(convertView == null) {
			LayoutInflater li = LayoutInflater.from(app);
			deviceView = (RelativeLayout)li.inflate(R.layout.devices, null, true);
		}
		else
			deviceView = (RelativeLayout)convertView;
		final TextView model = ((TextView)deviceView.findViewById(R.id.devicesTv));
		final TextView ip = ((TextView)deviceView.findViewById(R.id.ipTv));
		final ImageView type = ((ImageView)deviceView.findViewById(R.id.deviceIv));
		final Device net = (Device)getItem(position);
		model.setText(net.getManufacturer() + " " + net.getModel());	
		ip.setText(net.getIP());
		type.setImageBitmap(net.getType());
		return deviceView;
	}
	
}
