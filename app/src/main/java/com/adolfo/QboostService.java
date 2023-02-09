package com.adolfo;

import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class QboostService extends Service {
    public static final String TAG = "QboostService";
    private int cnt;
    public int device;
    public boolean isRecording;
    private MyBinder mBinder = new MyBinder();
    public int mode;
    private Thread recThread;
    public int smax;
    public int smin;

    class MyBinder extends Binder {
        MyBinder() {
        }

        public int[] getParam() {
            int i = 1;
            int[] param = new int[7];
            int dd = QboostService.this.device;
            if (dd >= 4) {
                param[2] = 1;
                dd -= 4;
            } else {
                param[2] = 0;
            }
            if (dd >= 2) {
                param[1] = 1;
                dd -= 2;
            } else {
                param[1] = 0;
            }
            if (dd >= 1) {
                param[0] = 1;
            } else {
                param[0] = 0;
            }
            param[3] = QboostService.this.mode;
            param[4] = QboostService.this.smin;
            param[5] = QboostService.this.smax;
            if (!QboostService.this.isRecording) {
                i = 0;
            }
            param[6] = i;
            Log.e(QboostService.TAG, "getParam() executed");
            return param;
        }

        public void setParam(int[] param) {
            QboostService.this.device = 0;
            if (param[0] > 0) {
                QboostService.this.device++;
            }
            if (param[1] > 0) {
                QboostService.this.device += 2;
            }
            if (param[2] > 0) {
                QboostService.this.device += 4;
            }
            QboostService.this.mode = param[3];
            QboostService.this.smin = param[4];
            QboostService.this.smax = param[5];
        }

        public void stopFore() {
            QboostService.this.stopForeground(true);
            QboostService.this.isRecording = false;
        }
    }

    private native void init();

    private native int setMode(int i, int i2, int i3, int i4);

    private native void uninit();

    static {
        System.loadLibrary("boostpower");
        System.loadLibrary("qspower-1.2.1");
    }

    public void onCreate() {
        super.onCreate();
        this.cnt = 0;
        Log.e(TAG, "onCreate() executed");
    }

    public void onDestroy() {
        this.isRecording = false;
        super.onDestroy();
        Log.e(TAG, "onDestroy() executed");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() executed");
        if (!this.isRecording) {
            startFore();
            this.recThread = new Thread(new Runnable() {
                public void run() {
                    QboostService.this.doinit();
                    QboostService.this.cnt = 0;
                    while (QboostService.this.isRecording) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        QboostService.this.doSet();
                        if (QboostService.this.cnt > 200) {
                            QboostService.this.douninit();
                            QboostService.this.doinit();
                            QboostService.this.doSet();
                            QboostService.this.cnt = 0;
                        }
                    }
                    QboostService.this.douninit();
                }
            });
            this.recThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void doinit() {
        init();
        Log.e(TAG, "init!");
    }

    private void douninit() {
        uninit();
        Log.e(TAG, "uninit!");
    }

    private void doSet() {
        int res = setMode(this.device, this.mode, this.smin, this.smax);
        this.cnt++;
    }

    private void startFore() {
        startForeground(1, new Builder(this).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setSmallIcon(R.mipmap.ic_launcher).setTicker("Qboost Running").setContentTitle(Qboost.TAG).setContentText("Qboost Running...").setWhen(System.currentTimeMillis()).setPriority(0).setAutoCancel(true).setOngoing(false).setDefaults(3).setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, Qboost.class), 268435456)).build());
        this.isRecording = true;
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}