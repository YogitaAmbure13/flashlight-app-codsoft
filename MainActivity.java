package com.example.flashlight1;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.Toast;

public class MainActivity extends Activity {

    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightOn = false;
    private ImageView torchImage;
    private ToggleButton flashlightToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        torchImage = findViewById(R.id.torchImage);
        flashlightToggle = findViewById(R.id.flashlightToggle);

        // Check if the device has a flash
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            // Handle devices without flash (Optional: Show a message to the user)
            flashlightToggle.setEnabled(false);
            showToast("Your device doesn't have a camera flash.");
            return;
        }

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
            showToast("Failed to access the camera flash.");
        }

        flashlightToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    turnOnFlashlight();
                } else {
                    turnOffFlashlight();
                }
            }
        });
    }

    private void turnOnFlashlight() {
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
                torchImage.setImageResource(R.drawable.torch_on);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            showToast("Failed to turn on the flashlight.");
        }
    }

    private void turnOffFlashlight() {
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
                torchImage.setImageResource(R.drawable.torch_off);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            showToast("Failed to turn off the flashlight.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Turn off the flashlight when the app is paused
        if (isFlashlightOn) {
            turnOffFlashlight();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Turn off the flashlight when the app is destroyed
        if (isFlashlightOn) {
            turnOffFlashlight();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
