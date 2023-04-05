package com.example.gps;

import android.Manifest;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements RangeNotifier {
    private BeaconManager mBeaconManager;
    HashMap<String, PointF> points;
    private final int permCode = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        points = new HashMap<>();
        points.put("33333333-3333-3333-3333-333333333333", new PointF(0, 0));
        points.put("22222222-2222-2222-2222-222222222222", new PointF(0, 0.8f));
        points.put("11111111-1111-1111-1111-111111111111", new PointF(0.8f, 0));

        start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(permCode)
    private void start() {
        String[] perms = {android.Manifest.permission.BLUETOOTH_ADMIN,android.Manifest.permission.BLUETOOTH_SCAN,android.Manifest.permission.BLUETOOTH_CONNECT
                , android.Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
            mBeaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            Region region = new Region("beacon region", null, null, null);
            mBeaconManager.addRangeNotifier(this);
            mBeaconManager.startRangingBeacons(region);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.app_name),
                    permCode, perms);
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        TextView info = findViewById(R.id.info);
        if (beacons.size() < 3) {
            info.setText("датчиков меньше чем 3");
            return;
        }
        StringBuilder builder = new StringBuilder();

        Iterator<Beacon> iterator = beacons.iterator();

        Beacon beacon = iterator.next();
        double distance1 = beacon.getDistance();
        PointF point1 = points.get(beacon.getId1().toUuid().toString());

        builder.append("1 маячок: позиция ").append(point1.x).append(":").append(point1.y).append(",").append("RSSI ").append(beacon.getRssi()).append(", длина ")
                .append(distance1).append("\n");

        beacon = iterator.next();
        double distance2 = beacon.getDistance();
        PointF point2 = points.get(beacon.getId1().toUuid().toString());
        builder.append("2 маячок: позиция ").append(point2.x).append(":").append(point2.y).append(",").append("RSSI ").append(beacon.getRssi()).append(", длина ")
                .append(distance2).append("\n");


        beacon = iterator.next();
        double distance3 = beacon.getDistance();
        PointF point3 = points.get(beacon.getId1().toUuid().toString());
        builder.append("3 маячок: позиция ").append(point3.x).append(":").append(point3.y).append(",").append("RSSI ").append(beacon.getRssi()).append(", длина ")
                .append(distance3).append("\n");


        PointF pointF = getCoordinates(point1, distance1, point2, distance2, point3, distance3);
        builder.append("Резльутат алгоритма: ").append(pointF.x).append(":").append(pointF.y);

        info.setText(builder.toString());
    }

    public PointF getCoordinates(PointF point1, double distance1, PointF point2, double distance2, PointF point3, double distance3) {
        Log.e("coor", point1.x + " " + point1.y + " " + distance1);
        Log.e("coor", point2.x + " " + point2.y + " " + distance2);
        Log.e("coor", point3.x + " " + point3.y + " " + distance3);

        double firstVarX = point2.x - point1.x, firstVarY = point2.y - point1.y,
                firstSum = 0.5 * (point2.x * point2.x - point1.x * point1.x + point2.y * point2.y - point1.y * point1.y + distance1 * distance1 - distance2 * distance2);
        double secondVarX = point3.x - point1.x, secondVarY = point3.y - point1.y,
                secondSum = 0.5 * (point3.x * point3.x - point1.x * point1.x + point3.y * point3.y - point1.y * point1.y + distance1 * distance1 - distance3 * distance3);


        double prevFirstVarX = firstVarX, prevSum = firstSum;

        if(firstVarY != 0) {
            secondVarY /= firstVarY;

            prevFirstVarX *= secondVarY;
            prevSum *= secondVarY;
        }
        else{
            secondVarX = 0;
            secondSum = 0;
        }


        double lastX = prevFirstVarX - secondVarX;
        double lastSum = prevSum - secondSum;

        double responseX = lastSum / lastX;
        double responseY = (firstSum - responseX * firstVarX) / firstVarY;


        return new PointF((float)responseX, (float)responseY);
    }

}