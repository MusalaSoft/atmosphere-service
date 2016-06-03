package com.musala.atmosphere.service.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.musala.atmosphere.service.AtmosphereService;

/**
 * A broadcast receiver that listens for custom intents and controls the atmosphere service.
 * 
 * @author yordan.petrov
 * 
 */
public class ServiceControlReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras.containsKey("command")) {
            String commandValue = intentExtras.getString("command");
            if (commandValue.equals("stop")) {
                Intent atmosphereServiceIntent = new Intent(context, AtmosphereService.class);
                context.stopService(atmosphereServiceIntent);
            }
        }
    }
}
