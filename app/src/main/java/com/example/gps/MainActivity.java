package com.example.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import com.example.gps.databinding.ActivityMainBinding;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Iterator;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements RangeNotifier {
    private BeaconManager mBeaconManager;
    private ActivityMainBinding binding;
    SurfaceHolder holder;
    int scale = 1;
    boolean isRunning = false;
    Region region = new Region("beacon region", null, null, null);

    Bitmap back;
    Bitmap point;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        back = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        point = BitmapFactory.decodeResource(getResources(), R.drawable.point);

        binding.containedButton.setOnClickListener(v -> {
            start();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(StaticData.PERMISSIONS_CODE)
    private void start() {
        String[] perms = {android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT
                , android.Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (isRunning) {
                isRunning = false;
                binding.thirdRssi.setText("PAUSED");
                binding.firstRssi.setText("PAUSED");
                binding.secondRssi.setText("PAUSED");
                binding.containedButton.setText("Start");
                mBeaconManager.stopRangingBeacons(region);
                return;
            }
            holder = binding.canvasData.getHolder();

            mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
            mBeaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout(StaticData.BEACON_TYPE));
            mBeaconManager.addRangeNotifier(this);
            mBeaconManager.startRangingBeacons(region);

            binding.containedButton.setText("Stop");
            isRunning = true;
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.app_name),
                    StaticData.PERMISSIONS_CODE, perms);
        }
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if(!isRunning) return;
        Iterator<Beacon> iterator = beacons.iterator();

        if (!iterator.hasNext()) {
            binding.firstRssi.setText("Нет сигнала");
            binding.secondRssi.setText("Нет сигнала");
            binding.thirdRssi.setText("Нет сигнала");
            return;
        }
        Beacon beacon = iterator.next();
        double distance1 = beacon.getDistance();
        PointF point1 = StaticData.getPoints().get(beacon.getId1().toUuid().toString());
        binding.firstRssi.setText(String.format("Дистанция: %.3f", distance1));

        if (!iterator.hasNext()) {
            binding.secondRssi.setText("Нет сигнала");
            binding.thirdRssi.setText("Нет сигнала");
            return;
        }

        beacon = iterator.next();
        double distance2 = beacon.getDistance();
        PointF point2 = StaticData.getPoints().get(beacon.getId1().toUuid().toString());
        binding.secondRssi.setText(String.format("Дистанция: %.3f", distance2));


        if (!iterator.hasNext()) {
            binding.thirdRssi.setText("Нет сигнала");
            return;
        }

        beacon = iterator.next();
        double distance3 = beacon.getDistance();
        PointF point3 = StaticData.getPoints().get(beacon.getId1().toUuid().toString());
        binding.thirdRssi.setText(String.format("Дистанция: %.3f", distance3));


        PointF pointF = StaticData.getCoordinates(point1, distance1, point2, distance2, point3, distance3);

        Canvas canvas = holder.lockCanvas();

        canvas.save();
        canvas.scale(scale, scale);
        int x = canvas.getWidth() / 2, y = canvas.getHeight() / 2;


        canvas.drawRect(new RectF(0,0, canvas.getWidth(), canvas.getHeight()), StaticData.LINE);
        canvas.drawBitmap(back, new Rect(0,0, back.getWidth(), back.getHeight()),
                new Rect(0,0, canvas.getWidth(), canvas.getHeight()), new Paint());



        canvas.drawBitmap(point, pointF.x * x, pointF.y * y, new Paint());

        canvas.restore();

        holder.unlockCanvasAndPost(canvas);

    }
}