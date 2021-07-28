package com.ficat.sample;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ficat.easyble.BleDevice;
import com.ficat.easyble.BleManager;
import com.ficat.easyble.Logger;
import com.ficat.easyble.gatt.bean.CharacteristicInfo;
import com.ficat.easyble.gatt.bean.ServiceInfo;
import com.ficat.easyble.gatt.callback.BleConnectCallback;
import com.ficat.easyble.gatt.callback.BleWriteCallback;
import com.ficat.easyble.scan.BleScanCallback;
import com.ficat.easypermissions.EasyPermissions;
import com.ficat.easypermissions.RequestExecutor;
import com.ficat.easypermissions.bean.Permission;
import com.ficat.sample.adapter.ScanDeviceAdapter;
import com.ficat.sample.adapter.CommonRecyclerViewAdapter;
import com.ficat.sample.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "EasyBle";
    public BleDevice device, mDevice, mDevice2;
    private RecyclerView rv;
    private BleManager manager;
    private List<BleDevice> deviceList = new ArrayList<>();
    private ScanDeviceAdapter adapter;
    private ProgressBar progress_bar1, progress_bar2;
    TextView key1state, key1name, key1address;
    TextView key2state, key2name, key2address;
    TextView tv_connect2,tv_connect, tv_disconnect, tv_disconnect2;
    public ServiceInfo curService;
    TextView writeResult, writeResult2;
    CharacteristicInfo curCharacteristic;
    Button cyan1,purple1,lime1,orange1,red1,yellow1,lemon1;
    Button cyan2,purple2,lime2,orange2,red2,yellow2,lemon2;
    SharedPreferences keyPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initBleManager();
        showDevicesByRv();

     //   manager.connect("F0:A1:8E:89:70:0C", bleConnectCallback);
        //connect with specified connectOptions
       // manager.connect(bleDevice, connectOptions, bleConnectCallback);

        //connect with mac address
    //    manager.connect("F0:A1:8E:89:70:0C", bleConnectCallback1);
      //  manager.connect(address, connectOptions, bleConnectCallback);
   //     manager.connect("E6:54:C5:60:F0:51",bleConnectCallback2);

    }
    private void initView() {
        Button btnScan = findViewById(R.id.btn_scan);
        rv = findViewById(R.id.rv);
        keyPref = getSharedPreferences("keyPref",MODE_PRIVATE);
        key1state = findViewById(R.id.key1state);
        key1name = findViewById(R.id.key1name);
        key1address = findViewById(R.id.key1address);
        progress_bar1 = findViewById(R.id.progress_bar1);
        key2state = findViewById(R.id.key2state);
        key2name = findViewById(R.id.key2name);
        key2address = findViewById(R.id.key2address);
        progress_bar2 = findViewById(R.id.progress_bar2);
        writeResult = findViewById(R.id.tvWrite);
        writeResult2 = findViewById(R.id.tvWrite2);
        tv_connect2 = findViewById(R.id.tv_connect2);
        tv_connect = findViewById(R.id.tv_connect);
        tv_disconnect = findViewById(R.id.tv_disconnect);
        tv_disconnect2 = findViewById(R.id.tv_disconnect2);
        cyan1 = findViewById(R.id.cyan1);
        purple1 = findViewById(R.id.purple1);
        red1 = findViewById(R.id.red1);
        purple1 = findViewById(R.id.red2);
        lime1 = findViewById(R.id.lime1);
        orange1 = findViewById(R.id.orange1);
        red1 = findViewById(R.id.red1);
        yellow1 = findViewById(R.id.yellow1);
        lemon1 = findViewById(R.id.lemon1);

        cyan2 = findViewById(R.id.cyan2);
        purple2 = findViewById(R.id.purple2);
        lime2 = findViewById(R.id.lime2);
        orange2 = findViewById(R.id.orange2);
        red2 = findViewById(R.id.red2);
        yellow2 = findViewById(R.id.yellow2);
        lemon2 = findViewById(R.id.lemon2);
        //TextView tvDisconnect = findViewById(R.id.tv_disconnect);
        //TextView tvReadRssi = findViewById(R.id.tv_read_rssi);
        key1name.setText("Name: LumnKey 1");
        key1address.setText("MAC: "+ keyPref.getString("mac1",""));
        key1state.setText("Scanning");

        key2name.setText("Name: LumnKey 2");
        key2address.setText("MAC: " + keyPref.getString("mac2",""));
        key2state.setText("Scanning");


        writeResult.setOnClickListener(this);
        writeResult2.setOnClickListener(this);
        tv_connect.setOnClickListener(this);
        tv_connect2.setOnClickListener(this);
        tv_disconnect.setOnClickListener(this);
        tv_disconnect2.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        cyan1.setOnClickListener(this);
        purple1.setOnClickListener(this);
        red1.setOnClickListener(this);
        orange1.setOnClickListener(this);
        yellow1.setOnClickListener(this);
        lemon1.setOnClickListener(this);
        lime1.setOnClickListener(this);

        cyan2.setOnClickListener(this);
        purple2.setOnClickListener(this);
        red2.setOnClickListener(this);
        orange2.setOnClickListener(this);
        yellow2.setOnClickListener(this);
        lemon2.setOnClickListener(this);
        lime2.setOnClickListener(this);
    }
    BleConnectCallback bleConnectCallback1 = new BleConnectCallback() {
        @Override
        public void onStart(boolean startConnectSuccess, String info, BleDevice device) {
            if (startConnectSuccess) {
                //start to connect successfully
            } else {
                //fail to start connection, see details from 'info'
                String failReason = info;
                Toast.makeText(MainActivity.this,failReason,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(int failCode, String info, BleDevice device) {
            if(failCode == BleConnectCallback.FAIL_CONNECT_TIMEOUT){
                //connection timeout
            }else{
                //connection fail due to other reasons
            }

        }

        @Override
        public void onConnected(BleDevice device) {
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT ).show();
            mDevice = device;
            key1name.setText(getResources().getString(R.string.device_name_prefix) + device.name);
            key1address.setText(getResources().getString(R.string.device_address_prefix) + device.address);
            key1state.setText("Connected");
            key1state.setTextColor(Color.GREEN);
            progress_bar1.setVisibility(View.GONE);
        }

        @Override
        public void onDisconnected(String info, int status, BleDevice device) {
            Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT ).show();
            key1state.setText("Disconnected");
            key1state.setTextColor(Color.RED);
        }
    };
    BleConnectCallback bleConnectCallback2 = new BleConnectCallback() {
        @Override
        public void onStart(boolean startConnectSuccess, String info, BleDevice device) {
            mDevice2 = device;
            if (startConnectSuccess) {
                //start to connect successfully
            } else {
                //fail to start connection, see details from 'info'
                String failReason = info;
                Toast.makeText(MainActivity.this,failReason,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(int failCode, String info, BleDevice device) {
            if(failCode == BleConnectCallback.FAIL_CONNECT_TIMEOUT){
                //connection timeout
            }else{
                //connection fail due to other reasons
            }

        }

        @Override
        public void onConnected(BleDevice device) {

            key2name.setText(getResources().getString(R.string.device_name_prefix) + device.name);
            key2address.setText(getResources().getString(R.string.device_address_prefix) + device.address);
            key2state.setText("Connected");
            key2state.setTextColor(Color.GREEN);
            progress_bar2.setVisibility(View.GONE);
        }

        @Override
        public void onDisconnected(String info, int status, BleDevice device) {
            Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT ).show();
            key2state.setText("Disconnected");
            key2state.setTextColor(Color.RED);
        }
    };
    private BleWriteCallback writeCallback = new BleWriteCallback() {
        @Override
        public void onWriteSuccess(byte[] data, BleDevice device) {
            Logger.e("write success:" + ByteUtils.bytes2HexStr(data));
            writeResult.setText(ByteUtils.bytes2HexStr(data));
        }

        @Override
        public void onFailure(int failCode, String info, BleDevice device) {
            Logger.e("write fail:" + info);
            writeResult.setText("write fail:" + info);
        }
    };


    private void initBleManager() {
        //check if this android device supports ble
        if (!BleManager.supportBle(this)) {
            return;
        }
        //open bluetooth without a request dialog
        BleManager.toggleBluetooth(true);

        BleManager.ScanOptions scanOptions = BleManager.ScanOptions
                .newInstance()
                .scanPeriod(8000)
                .scanDeviceName(null);

        BleManager.ConnectOptions connectOptions = BleManager.ConnectOptions
                .newInstance()
                .connectTimeout(12000);

        manager = BleManager
                .getInstance()
                .setScanOptions(scanOptions)
                .setConnectionOptions(connectOptions)
                .setLog(true, "EasyBle")
                .init(this.getApplication());
    }

    private void showDevicesByRv() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 3;
            }
        });
        SparseArray<int[]> res = new SparseArray<>();

        res.put(R.layout.item_rv_scan_devices, new int[]{R.id.tv_name, R.id.tv_address, R.id.tv_connection_state});
        adapter = new ScanDeviceAdapter(this, deviceList, res);
        adapter.setOnItemClickListener(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                manager.stopScan();
                BleDevice device = deviceList.get(position);
                Intent intent = new Intent(MainActivity.this, OperateActivity.class);
                intent.putExtra(OperateActivity.KEY_DEVICE_INFO, device);
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_connect) {
            BleManager.getInstance().connect(keyPref.getString("mac1",""), bleConnectCallback1);
            return;
        }
        if (v.getId() == R.id.tv_connect2) {
            BleManager.getInstance().connect(keyPref.getString("mac2",""), bleConnectCallback2);
            return;
        }
     //   BleManager.getInstance().connect("F0:A1:8E:89:70:0C", bleConnectCallback1);
        /*

        */

        switch (v.getId()) {

            case R.id.tvWrite:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                String str = "fffff0";
                if (TextUtils.isEmpty(str)) {
                    Toast.makeText(this, getResources().getString(R.string.tips_write_operation), Toast.LENGTH_SHORT).show();
                    return;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes(str), writeCallback);
                writeResult.setText(str);
                break;

            case R.id.tvWrite2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                String str2 = "fffff0";
                if (TextUtils.isEmpty(str2)) {
                    Toast.makeText(this, getResources().getString(R.string.tips_write_operation), Toast.LENGTH_SHORT).show();
                    return;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes(str2), writeCallback);
                writeResult2.setText(str2);
                break;
            case R.id.btn_scan:
                if (!BleManager.isBluetoothOn()) {
                    BleManager.toggleBluetooth(true);
                }
                //for most devices whose version is over Android6,scanning may need GPS permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isGpsOn()) {
                    Toast.makeText(this, getResources().getString(R.string.tips_turn_on_gps), Toast.LENGTH_LONG).show();
                    return;
                }
                EasyPermissions
                        .with(this)
                        .request(Manifest.permission.ACCESS_FINE_LOCATION)
                        .autoRetryWhenUserRefuse(true, null)
                        .result(new RequestExecutor.ResultReceiver() {
                            @Override
                            public void onPermissionsRequestResult(boolean grantAll, List<Permission> results) {
                                if (grantAll) {
                                    if (!manager.isScanning()) {
                                        startScan();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            getResources().getString(R.string.tips_go_setting_to_grant_location),
                                            Toast.LENGTH_LONG).show();
                                    EasyPermissions.goToSettingsActivity(MainActivity.this);
                                }
                            }
                        });
                break;
            case R.id.tv_disconnect:
                manager.disconnect(keyPref.getString("mac1",""));
                break;
            case R.id.tv_disconnect2:
                manager.disconnect(keyPref.getString("mac2",""));
                break;
            case R.id.red1:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("FF000000"), writeCallback);
                writeResult.setText("Clicked");
                break;
            case R.id.cyan1:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("0000FF00"), writeCallback);
                writeResult.setText("0000FF00");
                break;
            case R.id.purple1:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("4F00FF00"), writeCallback);
                writeResult.setText("4F00FF00");
                break;
            case R.id.lime1:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("BFFF0000"), writeCallback);
                writeResult.setText("BFFF0000");
                break;
            case R.id.orange1:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("ff7f0000"), writeCallback);
                writeResult.setText("ff7f0000");
                break;
            case R.id.yellow1:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("FFFF0000"), writeCallback);
                writeResult.setText("FFFF0000");
                break;
            case R.id.lemon1:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac1",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("C0DC6B00"), writeCallback);
                writeResult.setText("C0DC6B00");
                break;

            case R.id.red2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("FF000000"), writeCallback);
                writeResult2.setText("FF000000");
                break;
            case R.id.cyan2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("0000FF00"), writeCallback);
                writeResult2.setText("0000FF00");
                break;
            case R.id.purple2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("4F00FF"), writeCallback);
                writeResult2.setText("004F00FF");
                break;
            case R.id.lime2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("BFFF0000"), writeCallback);
                writeResult2.setText("BFFF0000");
                break;
            case R.id.orange2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("ff7f0000"), writeCallback);
                writeResult2.setText("ff7f0000");
                break;
            case R.id.yellow2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("FFFF0000"), writeCallback);
                writeResult2.setText("FFFF0000");
                break;
            case R.id.lemon2:
                if (!BleManager.getInstance().isConnected(keyPref.getString("mac2",""))) {
                    Toast.makeText(this, "Key is disconnected", Toast.LENGTH_SHORT).show();
                    break;
                }
                BleManager.getInstance().write(mDevice2, "6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e", ByteUtils.hexStr2Bytes("C0DC6B00"), writeCallback);
                writeResult2.setText("C0DC6B00");
                break;
            default:
                break;
        }
    }

    private void startScan() {
        manager.startScan(new BleScanCallback() {
            @Override
            public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
                for (BleDevice d : deviceList) {
                    if (device.address.equals(d.address)) {
                        return;
                    }
                }
                if(device.name.equals("LumnKey")) {
                    deviceList.add(device);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onStart(boolean startScanSuccess, String info) {
                Log.e(TAG, "start scan = " + startScanSuccess + "   info: " + info);
                if (startScanSuccess) {
                    deviceList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "scan finish");
            }
        });
    }

    private boolean isGpsOn() {
        LocationManager locationManager
                = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager != null) {
            //you must call BleManager#destroy() to release resources
            manager.destroy();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        key1address.setText("MAC: "+ keyPref.getString("mac1",""));
        key2address.setText("MAC: "+ keyPref.getString("mac2",""));
    }
}
