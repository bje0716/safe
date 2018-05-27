package kr.ac.dongeui.pangpang.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import kr.ac.dongeui.pangpang.R;
import kr.ac.dongeui.pangpang.databinding.ActivityPermissionBinding;
import kr.ac.dongeui.pangpang.util.PreferenceUtil;

public class PermissionActivity extends AppCompatActivity {

    private ActivityPermissionBinding binding;
    private PreferenceUtil util;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission);
        util = new PreferenceUtil(this);

        PermissionListener listener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                util.putBoolean("permission", false);
                finish();
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(PermissionActivity.this, "권한 허용되지 않아 어플을 종료합니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        TedPermission.with(getApplicationContext())
                .setPermissionListener(listener)
                .setDeniedMessage("권한 허용 설정해주셔야 어플 사용이 가능합니다")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }
}
