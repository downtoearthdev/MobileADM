package com.scorchedcode.darklust.mobileadm;

import android.graphics.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import android.content.res.*;

public class AppProject
{
	private File manifest, apk = null;
	private String component, name;
	private Bitmap icon;

	public AppProject(File manifest)
	{
		this.manifest = manifest;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			Document db = dbf.newDocumentBuilder().parse(new File(manifest.toString() + "/AndroidManifest.xml"));
			Element root = db.getDocumentElement();
			NodeList strings = dbf.newDocumentBuilder().parse(new File(manifest.toString() + "/res/values/strings.xml")).getDocumentElement().getElementsByTagName("string");
			for (int x = 0; x < strings.getLength(); x++)
			{
				if (((Element)strings.item(x)).getAttribute("name").equals("app_name"))
					name = ((Element)strings.item(x)).getTextContent();
			}
			component = root.getAttribute("package") + "/.MainActivity";
			String drawable = ((Element)root.getElementsByTagName("application").item(0)).getAttribute("android:icon").split("/")[1] + ".png";
			if (new File(manifest.toString() + "/res/drawable-hdpi/" + drawable).exists())
				icon = BitmapFactory.decodeFile(manifest.toString() + "/res/drawable-hdpi/" + drawable);
			else if (new File(manifest.toString() + "/res/drawable-mdpi/" + drawable).exists())
				icon = BitmapFactory.decodeFile(manifest.toString() + "/res/drawable-mdpi/" + drawable);
			else if (new File(manifest.toString() + "/res/drawable-xhdpi/" + drawable).exists())
				icon = BitmapFactory.decodeFile(manifest.toString() + "/res/drawable-xhdpi/" + drawable);
			else if (new File(manifest.toString() + "/res/drawable/" + drawable).exists())
				icon = BitmapFactory.decodeFile(manifest.toString() + "/res/drawable/" + drawable);
			else if (new File(manifest.toString() + "/res/drawable-xxhdpi/" + drawable).exists())
				icon = BitmapFactory.decodeFile(manifest.toString() + "/res/drawable-xxhdpi/" + drawable);
			else
				icon = BitmapFactory.decodeResource(Resources.getSystem() ,android.R.drawable.ic_delete);
			File build = new File(manifest.toString() + "/bin/");
			if (build.exists())
			{
				for (int x = 0; x < build.listFiles().length; x++)
				{
					if (build.listFiles()[x].toString().contains(".apk"))
						this.apk = build.listFiles()[x];
				}   		
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
	}

	public String getComponent()
	{
		return component;
	}

	public Bitmap getIcon()
	{
		return icon;
	}

	public String getName()
	{
		return name;
	}

	public String getPath()
	{
		return manifest.toString();
	}

	public File getBuild()
	{
		return apk;
	}

	public boolean isDeployable()
	{
		return (apk == null) ? false : true;
	}
}
