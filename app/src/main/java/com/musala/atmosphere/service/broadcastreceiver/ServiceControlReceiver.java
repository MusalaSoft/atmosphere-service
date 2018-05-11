// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

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
