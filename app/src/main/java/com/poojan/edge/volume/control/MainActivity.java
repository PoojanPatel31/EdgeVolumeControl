package com.poojan.edge.volume.control;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.samsung.android.sdk.look.Slook;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView mImageView = findViewById(R.id.imageView);
        TextView mTextView = findViewById(R.id.description);
        CheckBox mCheckBox = findViewById(R.id.checkBox);

        Slook slook = new Slook();
        try {
            slook.initialize(this);
            mImageView.setImageDrawable(getDrawable(R.mipmap.ic_ok));
            mTextView.setText("Hooray...!!!\nYour device is fully supported.\nNow go to the Edge Panel and enable it.");
            mCheckBox.setVisibility(View.VISIBLE);
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        PackageManager packageManager = getPackageManager();
                        ComponentName name = new ComponentName(MainActivity.this, MainActivity.class);
                        if (isChecked) {
                            packageManager.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                        } else {
                            packageManager.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                        }
                        Toast.makeText(MainActivity.this, "Please reboot the device", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            if (!BuildConfig.DEBUG) {
                Crashlytics.logException(e);
            }
            e.printStackTrace();
            mImageView.setImageDrawable(getDrawable(R.mipmap.ic_error));
            mTextView.setText("Ops...!!!\nIt seems your device is not Samsung device or it does not support edge screen.");
            mCheckBox.setVisibility(View.GONE);

            PackageManager packageManager = getPackageManager();
            ComponentName componentName = new ComponentName(this, EdgeSingleProvider.class);
            if (packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                    || packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
