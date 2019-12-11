package com.scorchedcode.darklust.mobileadm;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class ProjectsAdapter extends ArrayAdapter
{
	private Context app = null;
	private int view = 0;
	
	public ProjectsAdapter(Context c, int res, ArrayList<AppProject> files) {
		super(c, res, files);
		this.app = c;
		this.view = res;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{	
		View appList;
		if(convertView == null) {
			LayoutInflater row = LayoutInflater.from(app);
			appList = row.inflate(R.layout.projects, null, true);
		}
		else
			appList = convertView;
		AppProject appProject = (AppProject)getItem(position);	
		TextView name = (TextView)appList.findViewById(R.id.nameTv);
		TextView buildable = (TextView)appList.findViewById(R.id.buildableTv);
		ImageView icon = (ImageView)appList.findViewById(R.id.appIv);
		icon.setImageBitmap(appProject.getIcon());
		name.setText(appProject.getName());
		buildable.setText((appProject.isDeployable()) ? "Ready!" : "No Build");
		buildable.setTextColor((appProject.isDeployable()) ?  Color.GREEN : Color.RED);
		return appList;
	}	
	
}
