package de.telekom.getmyprivateip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Nullable
    private String getLocalIpAddress() {
        try {
            StringBuilder sb = null;
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            if (en == null) {
                return null;
            }
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (sb == null) {
                            sb = new StringBuilder();
                            sb.append("name: ").append(intf.getDisplayName());
                        } else {
                            sb.append(" name: ").append(intf.getDisplayName());
                        }
                        sb.append(" ip: ").append(inetAddress.getHostAddress());
                        sb.append('\n');
                    }
                }
            }
            if (sb != null) {
                return sb.toString();
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView interfacesTextView = findViewById(R.id.interfaces);
        TextView wifiTextView = findViewById(R.id.wifiaddress);

        try {
            String myIP = getLocalIpAddress();
            interfacesTextView.setText(myIP);
            String wifiAddress = getWifiAddress();
            wifiTextView.setText(wifiAddress);
        } catch (Exception ex) {
            ex.printStackTrace();
            interfacesTextView.setText(ex.getMessage());
        }


    }

    private String getWifiAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = Objects.requireNonNull(wifiManager).getConnectionInfo();
        int ipNum = wifiInfo.getIpAddress();
        byte[] myIPAddress = BigInteger.valueOf(ipNum).toByteArray();
        ArrayUtils.reverse(myIPAddress);
        InetAddress myInetIP = InetAddress.getByAddress(myIPAddress);
        return myInetIP.getHostAddress();
    }
}