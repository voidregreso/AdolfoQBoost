package com.adolfo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Qboost extends AppCompatActivity implements OnClickListener {
    public static final String TAG = "Qboost";
    private CheckBox checkBig;
    private CheckBox checkGPU;
    private CheckBox checkLittle;
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            boolean z;
            Qboost.this.myBinder = (QboostService.MyBinder) service;
            int[] param = Qboost.this.myBinder.getParam();
            Qboost.this.checkBig.setChecked(param[0] == 1);
            CheckBox access$200 = Qboost.this.checkLittle;
            if (param[1] == 1) {
                z = true;
            } else {
                z = false;
            }
            access$200.setChecked(z);
            access$200 = Qboost.this.checkGPU;
            if (param[2] == 1) {
                z = true;
            } else {
                z = false;
            }
            access$200.setChecked(z);
            Qboost.this.rbs[param[3]].setChecked(true);
            Qboost.this.sbmin.setProgress(param[4]);
            Qboost.this.sbmax.setProgress(param[5]);
            if (param[6] == 0) {
                Qboost.this.startService.setText("Start");
                Qboost.this.srunning = false;
                return;
            }
            Qboost.this.startService.setText("Stop");
            Qboost.this.srunning = true;
        }
    };
    private QboostService.MyBinder myBinder;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private RadioButton rb5;
    private RadioButton rb6;
    private OnCheckedChangeListener rbChange = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Qboost.this.sbmin.setEnabled(b);
            Qboost.this.sbmax.setEnabled(b);
        }
    };
    private RadioButton[] rbs;
    private SeekBar sbmax;
    private SeekBar sbmin;
    private OnSeekBarChangeListener seekListenerMax = new OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.e(Qboost.TAG, "onStopTrackingTouch");
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.e(Qboost.TAG, "onStartTrackingTouch");
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.e(Qboost.TAG, "onProgressChanged: " + seekBar.getProgress());
            Qboost.this.tmax.setText("Max Perf % " + seekBar.getProgress());
            if (Qboost.this.sbmin.getProgress() > seekBar.getProgress()) {
                Qboost.this.sbmin.setProgress(seekBar.getProgress());
            }
        }
    };
    private OnSeekBarChangeListener seekListenerMin = new OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.e(Qboost.TAG, "onStopTrackingTouch");
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.e(Qboost.TAG, "onStartTrackingTouch");
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.e(Qboost.TAG, "onProgressChanged: " + seekBar.getProgress());
            Qboost.this.tmin.setText("Min Perf % " + seekBar.getProgress());
            if (Qboost.this.sbmax.getProgress() < seekBar.getProgress()) {
                Qboost.this.sbmax.setProgress(seekBar.getProgress());
            }
        }
    };
    private boolean srunning = false;
    private Button startService;
    private TextView tmax;
    private TextView tmin;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_qboost);
        this.startService = (Button) findViewById(R.id.buttonStart);
        this.startService.setOnClickListener(this);
        this.checkBig = (CheckBox) findViewById(R.id.checkBoxBig);
        this.checkLittle = (CheckBox) findViewById(R.id.checkBoxLittle);
        this.checkGPU = (CheckBox) findViewById(R.id.checkBoxGPU);
        this.rb1 = (RadioButton) findViewById(R.id.radioButtonNormal);
        this.rb2 = (RadioButton) findViewById(R.id.radioButtonEff);
        this.rb3 = (RadioButton) findViewById(R.id.radioButtonPowerSave);
        this.rb4 = (RadioButton) findViewById(R.id.radioButtonPerf);
        this.rb5 = (RadioButton) findViewById(R.id.radioButtonWindow);
        this.rb6 = (RadioButton) findViewById(R.id.radioButtonSuperSave);
        this.rb5.setOnCheckedChangeListener(this.rbChange);
        this.tmin = (TextView) findViewById(R.id.textViewMin);
        this.tmax = (TextView) findViewById(R.id.textViewMax);
        this.sbmin = (SeekBar) findViewById(R.id.seekBarMin);
        this.sbmax = (SeekBar) findViewById(R.id.seekBarMax);
        this.sbmin.setOnSeekBarChangeListener(this.seekListenerMin);
        this.sbmax.setOnSeekBarChangeListener(this.seekListenerMax);
        this.sbmin.setEnabled(this.rb5.isChecked());
        this.sbmax.setEnabled(this.rb5.isChecked());
        this.rbs = new RadioButton[6];
        this.rbs[0] = this.rb1;
        this.rbs[1] = this.rb2;
        this.rbs[2] = this.rb3;
        this.rbs[3] = this.rb4;
        this.rbs[4] = this.rb5;
        this.rbs[5] = this.rb6;
    }

    public void onResume() {
        super.onResume();
        bindService(new Intent(this, QboostService.class), this.connection, 1);
    }

    public void onPause() {
        super.onPause();
        unbindService(this.connection);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart:
                if (this.srunning) {
                    this.myBinder.stopFore();
                    this.startService.setText("Start");
                    this.srunning = false;
                    return;
                }
                this.myBinder.setParam(getParam());
                startService(new Intent(this, QboostService.class));
                this.startService.setText("Stop");
                this.srunning = true;
                return;
            default:
                return;
        }
    }

    private int[] getParam() {
        int i;
        int[] param = new int[6];
        param[0] = this.checkBig.isChecked() ? 1 : 0;
        if (this.checkLittle.isChecked()) {
            i = 1;
        } else {
            i = 0;
        }
        param[1] = i;
        if (this.checkGPU.isChecked()) {
            i = 1;
        } else {
            i = 0;
        }
        param[2] = i;
        if (this.rb1.isChecked()) {
            param[3] = 0;
        }
        if (this.rb2.isChecked()) {
            param[3] = 1;
        }
        if (this.rb3.isChecked()) {
            param[3] = 2;
        }
        if (this.rb4.isChecked()) {
            param[3] = 3;
        }
        if (this.rb5.isChecked()) {
            param[3] = 4;
        }
        if (this.rb6.isChecked()) {
            param[3] = 5;
        }
        param[4] = this.sbmin.getProgress();
        param[5] = this.sbmax.getProgress();
        return param;
    }
}