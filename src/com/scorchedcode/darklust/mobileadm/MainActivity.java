package com.scorchedcode.darklust.mobileadm;

import android.app.*;
import android.content.*;
import android.net.wifi.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import java.lang.Process;

public class MainActivity extends Activity implements OnItemClickListener
{
	static final String TAG = "MobileADM";
	private static MainActivity instance = null;
	TextView logOut = null; EditText ip = null;
	ListView projects = null; ListView devices = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.a9p_09_11_00217));
		logOut = ((TextView) findViewById(R.id.logcatTv));
		devices = ((ListView) findViewById(R.id.deviceLv));
		projects = ((ListView) findViewById(R.id.projectsLv));
		projects.setOnItemClickListener(this);
		devices.setOnItemClickListener(this);
		this.instance = this;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		File exDir = new File(prefs.getString("pref_apps", Environment.getExternalStorageDirectory().toString() + "/AppProjects/"));
		setupProjects(exDir);		
	}
	
	public static MainActivity getInstance() {
		return instance;
	}
	
	public void setupProjects(File exDir) {
		ArrayList<AppProject> appProjects = new ArrayList<AppProject>();
		if (exDir.exists())
		{
			File[] dirs = exDir.listFiles();
			for (int x = 0; x < dirs.length; x++)
			{
				if ((new File(exDir.listFiles()[x].toString() + "/src/").exists()) && (new File(exDir.listFiles()[x].toString() + "/AndroidManifest.xml").exists()))
					appProjects.add(new AppProject(dirs[x]));
			}
			projects.setAdapter(new ProjectsAdapter(this, R.layout.projects, appProjects));		
		}
	}
	
	static public String executeForResult(final ArrayList<List> commands, final boolean isSplit, final TextView out) {
		final StringBuilder stringOut = new StringBuilder();
		FutureTask<String> prog = new FutureTask<String>(new Callable<String>() {
				@Override
				public String call()
				{
					Process process = null;
					for (List cmd : commands)
					{
						try
						{
							char[] buffer = new char[200];
							process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
							InputStreamReader isr = new InputStreamReader(process.getInputStream());
							process.waitFor();
							int count = isr.read(buffer);
							for (int x = 0; x < count; x++) {
								if((isSplit && !(Character.compare(buffer[x], '\n') == 0) || !isSplit))
									stringOut.append(buffer[x]);
							}
							if(isSplit)
								stringOut.append("\n");
							isr.close();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						finally
						{
							process.destroy();
						}
					}
					if(!isSplit && out != null) {
						out.post(new Runnable() {

								@Override
								public void run()
								{
									out.setText(stringOut.toString());
								}
						});
						return null;
					}
					else
						return stringOut.toString();				
				}
			});
			Executors.newSingleThreadExecutor().execute(prog);
		try
		{
			return prog.get();
		}
		catch (ExecutionException e)
		{}
		catch (InterruptedException e)
		{}
		return null;
	}
	
	public void initiateADM(final String ip, final AppProject app)
	{
		final ArrayList<List> commands = new ArrayList<List>();
		commands.add(Arrays.asList(("adb -s " + ip + ":5555 install -r " + app.getPath() + "/bin/" + app.getBuild().getName()).split(" ")));
		commands.add(Arrays.asList(("adb -s " + ip + ":5555 shell am start -n " + app.getComponent()).split(" ")));
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					executeForResult(commands, false, logOut);
				}
		}).start();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		//NetworkInfo net = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		WifiManager net = ((WifiManager)getSystemService(Context.WIFI_SERVICE));
		if(net.getWifiState() == WifiManager.WIFI_STATE_ENABLED && net.getConnectionInfo().getSSID() != null)
			menu.findItem(R.id.action_refresh).setEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.action_refresh:
				new PopulateDevices().execute(this);
				break;
			case R.id.action_settings:
				startActivity(new Intent(this, com.scorchedcode.darklust.mobileadm.MADMPreferences.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		switch (p1.getId()) {
			case R.id.projectsLv:
				AppProject project = (AppProject)p1.getItemAtPosition(p3);
				if (!(logOut.getText().toString() == "")) {
					if (project.isDeployable())
						initiateADM(logOut.getText().toString() , project);
					else
						Toast.makeText(this, "This application has not been compiled yet!\nBuild the project first.", Toast.LENGTH_LONG).show();		
				}
				else
					Toast.makeText(this, "Please select a device first!", Toast.LENGTH_LONG).show();
				break;		
			case R.id.deviceLv:
				logOut.setText(((Device)p1.getItemAtPosition(p3)).getIP());
		}
	}
	
	private class PopulateDevices extends AsyncTask<MainActivity,  Void, CopyOnWriteArrayList<Device>>
	{
		MainActivity main = null;
		ADBProgress progress = new ADBProgress();
		@Override
		protected CopyOnWriteArrayList<Device> doInBackground(MainActivity[] p1)
		{
			CopyOnWriteArrayList<Device> ips = new CopyOnWriteArrayList<Device>();
			this.main = p1[0];
			progress.show(main.getFragmentManager(), "progress");
			for (int x = 1; x < 256; x++) {	
				new Thread(new IPThread(x, ips)).start();
				publishProgress();
			}
			try
			{
				Thread.sleep(3000); //Need to make sure the thing lasts long enough on slow shit
			}
			catch (InterruptedException e){}
			return ips;
		}

		@Override
		protected void onProgressUpdate(Void... v)
		{
			((ProgressDialog)progress.getDialog()).incrementProgressBy(1);
		}
		
		@Override
		protected void onPostExecute(CopyOnWriteArrayList<Device> result)
		{
			progress.getDialog().dismiss();
			devices.setAdapter(new DeviceAdapter(main, R.layout.devices, result));
		}
	}
}
