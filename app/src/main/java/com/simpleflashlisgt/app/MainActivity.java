package com.simpleflashlisgt.app;

import android.app.Activity;
i
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import java.util.List;


public class MainActivity extends Activity {

    private boolean isLightOn = false;

    private Camera camera;

    private ToggleButton button;

    // accelerometer variables
    SensorManager sensorManager;
    Sensor accelerometerSensor;
    boolean accelerometerPresent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (ToggleButton) findViewById(R.id.buttonFlashlight);

        Context context = this;
        PackageManager pm = context.getPackageManager();

        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device does not have camera");
            return;
        }

        if (camera == null) {
            camera = Camera.open();
        }

        final Parameters p = camera.getParameters();

        //accelerometer stuff
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        } else {
            // no accelerometer detected
            accelerometerPresent = false;
        }


        /*
        randomButtonThing.setOnTouchListener(new OnSwipeTouchListener() {

        public void onSwipeTop() {

        // do something

        }

        public void onSwipeRight() {

        //something else

        }

        public void onSwipeLeft() {

        }

        public void onSwipeBottom() {

        });
        */

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (isLightOn) {

                    Log.i("info", "light is turned off!");

                    p.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    camera.stopPreview();
                    isLightOn = false;

                } else {

                    Log.i("info", "light is turned on!");

                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);

                    camera.setParameters(p);
                    camera.startPreview();
                    isLightOn = true;

                }

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
        }

        if (accelerometerPresent){
            sensorManager.unregisterListener(accelerometerListener);
        }
    }

    protected void onDestroy() {
        super.onDestroy();

        if (camera!=null) {
            camera.release();
            camera=null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (camera == null) {
            camera = Camera.open();
        }

        if(accelerometerPresent){
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /** Menu inflater Method */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /** Called when the user clicks a menu button */
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.about:
                aboutMenuItem();
                break;
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void aboutMenuItem() {
        new AlertDialog.Builder(this)
                .setTitle("About the Developer")
                .setMessage("Welcome to the simplest flashlight app on Google Play!" + "\n" +
                        "This application toggles your LED light on your smart phone so that you can use it as a flashlight. " +
                        "I am a high school student learning the ropes of building elegant and simple Android applications. " +
                        "Please leave a rating on the Play Store and check out some of my other applications too!" + "\n" +
                        "If you would like to donate to my cause, all donations are accepted but not expected. All proceeds go towards new applications. " +
                        "I hope this application suits all your flashlight needs. " + "\n" +
                        "Thank you!")
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //leave blank
                    }
                }).show();
    }

    /**
     This sets up gestures
     */

    public class OnSwipeTouchListener implements View.OnTouchListener {

        @SuppressWarnings("deprecation")
        private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener implements GestureDetector.OnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {

                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                onSwipeBottom();
                            } else {
                                onSwipeTop();
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

    }

    public SensorEventListener accelerometerListener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            // IF THE PREFERENCE IS ON (declare this as a boolean) then proceed to use this functionality
            //if (flipToggle) {
            float z_value = event.values[2];

            // z_value as < 0 means the following action will be triggered if the device is turned sideways or upside-down
            if (z_value < 0) {

                // turn the flashlight on

            }
            //}
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };

}