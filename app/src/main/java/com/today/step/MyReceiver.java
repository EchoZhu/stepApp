package com.today.step;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String steps = intent.getStringExtra(context.getString(R.string.steps));
        Log.d("step",steps+"");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
