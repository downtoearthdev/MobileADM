package com.scorchedcode.darklust.mobileadm;

import android.app.*;
import android.os.*;

public class ADBProgress extends DialogFragment
{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setTitle("Discovering devices...");
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setMax(255);
		return progress;
	}
}
