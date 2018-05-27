package kr.ac.dongeui.pangpang.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.DeviceList;
import kr.ac.dongeui.pangpang.R;
import kr.ac.dongeui.pangpang.data.HelperData;
import kr.ac.dongeui.pangpang.databinding.ActivityMainBinding;
import kr.ac.dongeui.pangpang.databinding.HeaderBinding;
import kr.ac.dongeui.pangpang.util.LocationUtil;
import kr.ac.dongeui.pangpang.util.PreferenceUtil;
import kr.ac.dongeui.pangpang.util.Util;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static app.akexorcist.bluetotohspp.library.BluetoothState.DEVICE_OTHER;
import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_CONNECT_DEVICE;
import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_HELPER = 200;

    private ActivityMainBinding binding;
    private HeaderBinding headerBinding;

    private PreferenceUtil util;
    private LocationManager manager;
    private BluetoothSPP bt;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference database;

    private long pressedTime = 0;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.appbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.appbar.content.setActivity(this);

        util = new PreferenceUtil(this); // 앱 내부 db 저장 초기화
        bt = new BluetoothSPP(this); // Bluetooth 초기화

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        String token = FirebaseInstanceId.getInstance().getToken();

        // 보호자 알림 수신을 위한 토큰값 서버에 저장
        if (!TextUtils.isEmpty(token)) {
            Log.d("token", token);
            if (user != null) {
                HelperData helperData = new HelperData(user.getDisplayName(), user.getEmail(),
                        user.getPhotoUrl().toString(), token);
                database.child("helper").child(user.getUid()).setValue(helperData);
            }
            util.putString("token", token);
        }

        if (token == null) {
            util.putString("token", FirebaseInstanceId.getInstance().getToken());
        }

        // 안드로이드 마시멜로 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (util.getBoolean("permission", true)) {
                startActivity(new Intent(this, PermissionActivity.class));
                finish();
            }
        }

        // 로그인 체크
        mAuthListener = firebaseAuth -> {
            if (user == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        };


        // NavigationView & DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawer, binding.appbar.toolbar,
                R.string.app_name, R.string.app_name);

        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        binding.nav.setNavigationItemSelectedListener(this);

        View header = binding.nav.getHeaderView(0);
        headerBinding = DataBindingUtil.bind(header);

        // HeaderLayout initial Setting
        if (user != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(headerBinding.img);

            headerBinding.name.setText(user.getDisplayName());
            headerBinding.email.setText(user.getEmail());
        }

        // 위치 찾기
        if (binding.appbar.content.location.getText().toString().equals("현재 위치를 찾고 있습니다..")) {
            getLocation();
        }

        // Bluetooth
        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(this, "블루투스 미지원 단말입니다", Toast.LENGTH_SHORT).show();
        }

        // 블루투스 연결 상태
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(MainActivity.this, name + " 연결되었습니다", Toast.LENGTH_SHORT).show();
                binding.appbar.content.bluetooth.setText(name);
            }

            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(MainActivity.this, "블루투스 연결이 끊어졌습니다", Toast.LENGTH_SHORT).show();
                binding.appbar.content.bluetooth.setText("연결된 블루투스 없음");
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(MainActivity.this, "블루투스 연결에 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 블루투스 수신
        bt.setOnDataReceivedListener((data, message) -> {
            Log.d("Bluetooth Message", message);
            if (message.equals("A")) {
                result = "화재 발생";
            } else if (message.equals("B")) {
                result = "충돌 발생";
            } else if (message.equals("C")) {
                result = "화재 충돌 발생";
            }

            if (result != null) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });

        if (util.getString("helper", null) != null) {
            binding.appbar.content.user.setText(util.getString("helper", null) + " 보호자님");
        }

        binding.appbar.content.list.setText(Util.getToday(System.currentTimeMillis()));
    }

    /**
     * 블루투스 카드 클릭 이벤트
     * @param view
     */
    public void bluetoothConnect(View view) {
        if (binding.appbar.content.bluetooth.getText().toString().equals("연결된 블루투스 없음")) {
            if (bt.getServiceState() == STATE_CONNECTED) {
                bt.disconnect();
            } else {
                startActivityForResult(new Intent(getApplicationContext(), DeviceList.class), REQUEST_CONNECT_DEVICE);
            }
        }
    }

    /**
     * 보호자 카드 클릭 이벤트
     * @param view
     */
    public void setHelper(View view) {
        if (binding.appbar.content.user.getText().toString().equals("등록된 보호자가 없습니다")) {
            startActivityForResult(new Intent(this, UserActivity.class), REQUEST_HELPER);
        }
    }

    /**
     * 알림 카드 이벤트
     * @param view
     */
    public void notify(View view) {
        startActivity(new Intent(this, NotificationActivity.class));
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Log.d(MainActivity.class.getSimpleName(), location.getProvider());

            String myLocation = LocationUtil.getAddressInString(getBaseContext(), lat, lng);
            binding.appbar.content.location.setText(LocationUtil.replaceAddressString(myLocation));

            if (!binding.appbar.content.location.getText().toString().equals("현재 위치를 찾고 있습니다..")) {
                manager.removeUpdates(listener);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    /**
     * 위치 가져오기
     */
    private void getLocation() {
        if (Util.isNetwork(this)) {
            try {
                manager = (LocationManager) getSystemService(LOCATION_SERVICE);
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        100,
                        1,
                        listener);
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        100,
                        1,
                        listener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "현재 위치를 가져올 수 없습니다. 네트워크를 연결해주세요", Toast.LENGTH_SHORT).show();
            binding.appbar.content.location.setText("현재 위치를 찾을 수 없습니다");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_HELPER:
                if (resultCode == RESULT_OK) {
                    binding.appbar.content.user.setText(data.getStringExtra("helper") + " 보호자님");
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) bt.connect(data);
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    bt.setupService();
                    bt.startService(DEVICE_OTHER);
                } else {
                    Toast.makeText(this, "블루투스 활성이 불가능합니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 화면 시작 이벤트
     */
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
        if (!bt.isBluetoothEnabled()) {
            startActivityForResult(new Intent(ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(DEVICE_OTHER);
            }
        }
    }

    /**
     * 화면 종료 이벤트
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) auth.removeAuthStateListener(mAuthListener);
        if (manager != null) manager.removeUpdates(listener);
        bt.stopService();
    }

    /**
     * 네비게이션뷰 아이템 이벤트
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_notification:
                startActivity(new Intent(this, NotificationActivity.class));
                break;
            default:
                binding.drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    /**
     * back 키 버튼 이벤트
     */
    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            if (pressedTime == 0) {
                Toast.makeText(this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);
                if (seconds > 2000) {
                    Toast.makeText(this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
                    pressedTime = 0;
                } else {
                    finish();
                }
            }
        }
    }
}
