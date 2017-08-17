package com.today.step;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.VitalityStepService;

import org.apache.log4j.chainsaw.Main;

/**
 * Created by Yikun on 2017/8/17.
 */
public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    
    private static final int REFRESH_STEP_WHAT = 0;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());
    private int mStepSum;

    private ISportStepInterface iSportStepInterface;

    private TextView stepTextView;
    private TextView tv_day;
    private String REVEIVER_TAG = "RECORD_CLOCK";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        startVitalityStepService();
//        recordSteps();
    }

    private void recordSteps() {
        Intent intent = new Intent(REVEIVER_TAG);
        intent.putExtra(getApplicationContext().getString(R.string.steps),tv_day.getText().toString());
        PendingIntent pIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),3*1000,pIntent);
    }

    private void startVitalityStepService() {
        Intent intent = new Intent(this, VitalityStepService.class);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
                try {
                    mStepSum = iSportStepInterface.getCurrTimeSportStep();
                    updateStepCount();//更新步数

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void initUI() {
        stepTextView = (TextView)findViewById(R.id.stepTextView);
        tv_day = (TextView)findViewById(R.id.tv_day);
    }

    class TodayStepCounterCall implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {

                    if (null != iSportStepInterface) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (mStepSum != step) {
                            mStepSum = step;
                            updateStepCount();
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }
    }

    private void updateStepCount() {
        Log.e(TAG,"updateStepCount : " + mStepSum);
        stepTextView.setText(mStepSum + "步");
    }
}
