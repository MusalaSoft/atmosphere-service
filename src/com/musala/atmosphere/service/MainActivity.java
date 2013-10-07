package com.musala.atmosphere.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.musala.atmophere.service.R;

/**
 * Activity designed to control the atmosphere service.
 * 
 * @author yordan.petrov
 * 
 */
public class MainActivity extends Activity
{
	private Button startServiceButton;

	private Button stopServiceButton;

	private Intent atmosphereServiceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		atmosphereServiceIntent = new Intent(this, AtmosphereService.class);

		startServiceButton = (Button) findViewById(R.id.buttonStartService);
		stopServiceButton = (Button) findViewById(R.id.buttonStopService);

		startServiceButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startService(atmosphereServiceIntent);
			}
		});

		stopServiceButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				stopService(atmosphereServiceIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
