package com.example.ipdemo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.SocketException;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Spinner equipment;
    private EditText ipAddress;
    private RadioGroup ipType;
    private EditText dnsAddress;
    private EditText subnetMask;
    private EditText gateAddress;
    private Button cancel;
    private Button confirm;
    private ImageView code;
    private String net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init() {
        initView();
        initData();
    }


    private void initView() {
        equipment = findViewById(R.id.equipment);
        ipAddress = findViewById(R.id.ip_address);
        dnsAddress = findViewById(R.id.dns_address);
        subnetMask = findViewById(R.id.subnet_mask);
        gateAddress = findViewById(R.id.net_address);
        ipType = findViewById(R.id.ip_type);
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        code = findViewById(R.id.code);
        ipType.setOnCheckedChangeListener(this);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.dynamic_ip:
                setEditFocus(false);
                break;
            case R.id.static_ip:
                setEditFocus(true);
                break;
            default:
                break;
        }
    }


    private void setEditFocus(boolean state) {
        ipAddress.setEnabled(state);
        subnetMask.setEnabled(state);
        dnsAddress.setEnabled(state);
        gateAddress.setEnabled(state);
    }

    private void initData() {
        if (NetUtil.getAllNetInterface() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, NetUtil.getAllNetInterface());
            equipment.setAdapter(adapter);
            equipment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    net = (String) equipment.getSelectedItem();
                    dnsAddress.setText(NetUtil.getLocalDNS(net));
                    subnetMask.setText(NetUtil.getLocalMask(net));
                    gateAddress.setText(NetUtil.getLocalGATE(net));
                    try {
                        ipAddress.setText(NetUtil.getIpAddress(net));
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "无可用网卡", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                verify();
                break;
            case R.id.cancel:
                code.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void verify() {
        String ip = ipAddress.getText().toString();
        if (NetUtil.isIP(ip) || ip.equals("")) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            Bitmap qr = QRCodeUtil.createQRImage("网卡设备：" + net + ";ip地址：" + ip + ";子网掩码：" + subnetMask.getText().toString() + ";DNS地址：" + dnsAddress.getText().toString() + ";网关地址：" + gateAddress.getText().toString(), 150, 150, bmp);
            code.setImageBitmap(qr);
            code.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(MainActivity.this, "ip格式不正确", Toast.LENGTH_SHORT).show();
        }
    }

}
