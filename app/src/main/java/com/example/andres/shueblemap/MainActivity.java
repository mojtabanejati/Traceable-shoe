package com.example.andres.shueblemap;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;

    private TextView textView;


    private ImageView image;
    private Bitmap emptyBmap;

    private Bitmap shoe;
    private Bitmap fpL;
    private Bitmap fpR;

    private BluetoothGatt mGatt;

    byte[] data_recieved = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    int coord_x = 500;
    int coord_y = 500;


    ArrayList<Integer> vector_x = new ArrayList<Integer>();
    ArrayList<Integer> vector_y = new ArrayList<Integer>();
    ArrayList<Integer> vector_angle = new ArrayList<Integer>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        textView = (TextView) findViewById(R.id.myText);

        textView.setText("Starting..." + "\n");

        ImageView image = (ImageView) findViewById(R.id.imageView);

        shoe = BitmapFactory.decodeResource(getResources(), R.drawable.shoe2);

        Bitmap emptyBmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);

        int width =  emptyBmap.getWidth();
        int height = emptyBmap.getHeight();
        //Bitmap charty = Bitmap.createBitmap(width , height , Bitmap.Config.ARGB_8888);

        vector_x.add(500);
        vector_y.add(500);
        vector_angle.add(0);

        fpL = BitmapFactory.decodeResource(getResources(), R.drawable.fpl);
        fpR = BitmapFactory.decodeResource(getResources(), R.drawable.fpr);

        Bitmap charty = my_draw(shoe, fpL, fpR, coord_x, coord_y, 0, vector_x, vector_y, vector_angle, 0); //angle = 0   angle_step=0

        image.setImageBitmap(charty);


    }



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            int angle = Integer.parseInt(bundle.getString("angle_read"));
            int x_pos_H = Integer.parseInt(bundle.getString("x_pos_H_read"));
            int x_pos_L = Integer.parseInt(bundle.getString("x_pos_L_read"));
            int sign_x = Integer.parseInt(bundle.getString("sign_x_read"));
            int y_pos_H = Integer.parseInt(bundle.getString("y_pos_H_read"));
            int y_pos_L = Integer.parseInt(bundle.getString("y_pos_L_read"));
            int sign_y = Integer.parseInt(bundle.getString("sign_y_read"));
            int total_H = Integer.parseInt(bundle.getString("total_H_read"));
            int total_L = Integer.parseInt(bundle.getString("total_L_read"));
            int step_H = Integer.parseInt(bundle.getString("step_H_read"));
            int step_L = Integer.parseInt(bundle.getString("step_L_read"));
            int angle_step = Integer.parseInt(bundle.getString("angle_step"));

            if (angle<0)
            {angle = 256+angle;}

            if (x_pos_H<0)
            {x_pos_H = 256+x_pos_H;}

            if (x_pos_L<0)
            {x_pos_L = 256+x_pos_L;}

            if (sign_x<0)
            {sign_x = 256+sign_x;}

            if (y_pos_H<0)
            {y_pos_H = 256+y_pos_H;}

            if (y_pos_L<0)
            {y_pos_L = 256+y_pos_L;}

            if (sign_y<0)
            {sign_y = 256+sign_y;}

            if (total_H<0)
            {total_H = 256+total_H;}

            if (total_L<0)
            {total_L = 256+total_L;}

            if (step_H<0)
            {step_H = 256+step_H;}

            if (step_L<0)
            {step_L = 256+step_L;}

            if (angle_step<0)
            {angle_step = 256+angle_step;}


            Log.i("**************angel", String.valueOf(angle));


            angle = angle*2;
            angle_step = angle_step*2;

            float x_pos;
            x_pos = x_pos_H*256 + x_pos_L;
            x_pos = x_pos/100; //in m

            float y_pos;
            y_pos = y_pos_H*256 + y_pos_L;
            y_pos = y_pos/100; //in m

            if (sign_x == 1) //negative
            {
                x_pos = (-x_pos);
            }

            if (sign_y == 0) //negative
            {
                y_pos = (-y_pos);
            }



            float temp_x = x_pos*6;
            float temp_y = y_pos*6;
            coord_x = (int)temp_x + 500;
            coord_y = (int)temp_y + 500;

            long walked_distance;
            walked_distance = total_H*256 + total_L;
            walked_distance = walked_distance/10;

            long step_length;
            step_length = step_H*256 + step_L;

            ImageView image = (ImageView) findViewById(R.id.imageView);
            shoe = BitmapFactory.decodeResource(getResources(), R.drawable.shoe2);
            fpL = BitmapFactory.decodeResource(getResources(), R.drawable.fpl);
            fpR = BitmapFactory.decodeResource(getResources(), R.drawable.fpr);

            Bitmap charty = my_draw(shoe, fpL, fpR, coord_x, coord_y, angle, vector_x, vector_y, vector_angle, angle_step);
            image.setImageBitmap(charty);


            textView.setText("Angle: " + angle + "°\n");
            textView.append(String.format("X: %.2f mts    ", (double)x_pos/10*1.8));  //***
            textView.append(String.format("Y: %.2f mts\n", (double)y_pos/10*1.8));  //***
            textView.append("Step length: " + (double)step_length/10*1.8 + " cm\n");


            textView.append("Walked distance: " + (double)walked_distance/100*1.8 + " mts\n");
        }
    };


    public static Bitmap my_draw(Bitmap shoe, Bitmap  fpL, Bitmap fpR, int coord_x, int coord_y, int angle, ArrayList<Integer> vector_x, ArrayList<Integer> vector_y, ArrayList<Integer> vector_angle,int angle_step)
    {
        int width =  1000;
        int height = 1000;


        if(vector_x.get(vector_x.size()-1) != coord_x  ||  vector_y.get(vector_x.size()-1) != coord_y)
        {
           vector_x.add(coord_x);
           vector_y.add(coord_y);
           vector_angle.add(angle_step);
        }

        // code to get bitmap onto screen
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff0B0B61;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        // get the little rounded cornered outside
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        final int color2 = 0xffFF6600; //0xff04B404;
        paint.setColor(color2);


        for(int i=0; i<vector_x.size()-1; i++)
        {
            Matrix r_matrix = new Matrix();
            r_matrix.postRotate(-vector_angle.get(i + 1));
            Bitmap rot_R = Bitmap.createBitmap(fpR, 0, 0, fpR.getWidth(), fpR.getHeight(), r_matrix, true);
            Bitmap rot_L = Bitmap.createBitmap(fpL, 0, 0, fpL.getWidth(), fpL.getHeight(), r_matrix, true);

            canvas.drawBitmap(rot_R, vector_x.get(i+1) - rot_R.getWidth() / 2, vector_y.get(i+1) - rot_R.getHeight() / 2, paint);

            int p_x = (vector_x.get(i) + vector_x.get(i+1))/2;
            int p_y = (vector_y.get(i) + vector_y.get(i+1))/2;

            canvas.drawBitmap(rot_L, p_x - rot_L.getWidth() / 2, p_y - rot_L.getHeight() / 2, paint);

        }


        Matrix matrix = new Matrix();
        matrix.postRotate(-angle);
        Bitmap rotatedjoy = Bitmap.createBitmap(shoe, 0, 0, shoe.getWidth(), shoe.getHeight(), matrix, true);

        canvas.drawBitmap(rotatedjoy, coord_x - rotatedjoy.getWidth() / 2, coord_y - rotatedjoy.getHeight() / 2, paint);


        return output;
    }


    @Override
    protected void onDestroy() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (btAdapter != null && btAdapter.isEnabled()) {

            btAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            btAdapter.getBluetoothLeScanner().flushPendingScanResults(scanCallback);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing()) {
            return;
        }
        btAdapter = btManager.getAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
            return;
        }

        btAdapter.getBluetoothLeScanner().startScan(scanCallback); //
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "App cannot work with Bluetooth disabled.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            printScanResult(result);

            byte[] data2 = result.getScanRecord().getBytes();

            if (data2[2] == 83) //S
            {
                BluetoothDevice btDevice = result.getDevice();
                connectToDevice(btDevice);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            textView.append("Received " + results.size() + " batch results:\n");
            for (ScanResult r : results) {
                printScanResult(r);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            switch (errorCode) {
                case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                    textView.append("Scan failed: already started.\n");
                    break;
                case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    textView.append("Scan failed: app registration failed.\n");
                    break;
                case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                    textView.append("Scan failed: feature unsupported.\n");
                    break;
                case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                    textView.append("Scan failed: internal error.\n");
                    break;
            }
        }

        private void printScanResult(ScanResult result) {
            String id = result.getDevice() != null ? result.getDevice().getAddress() : "unknown";


            byte[] data2 = result.getScanRecord().getBytes();
            if (data2[2] == 83) //S
            {
                textView.setText("Connecting..." + "\n");
            }
        }
    };



    public void send_button(View V) {

        vector_x.clear();
        vector_y.clear();
        vector_angle.clear();
        vector_x.add(500);
        vector_y.add(500);
        vector_angle.add(500);

        byte[] write_data = {50};

        List<BluetoothGattService> services = mGatt.getServices();
        BluetoothGattCharacteristic mWriteCharacteristic = services.get(2).getCharacteristics().get(1);


        Log.i("WRITE_charact", mWriteCharacteristic.getUuid().toString());

        mWriteCharacteristic.setValue(write_data);
        mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        mGatt.writeCharacteristic(mWriteCharacteristic);


    }




    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            //scanLeDevice(false);// will stop after first device detection
            btAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }



    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());


            for (BluetoothGattService service : services) {
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                Log.i("onSERVICIO", service.getUuid().toString());

                for (BluetoothGattCharacteristic charact : characteristics) {
                    Log.i("onCARAS", charact.getUuid().toString());
                }

            }

            BluetoothGattCharacteristic my_charact = services.get(2).getCharacteristics().get(0);
            List<BluetoothGattDescriptor> descriptors = my_charact.getDescriptors();

            Log.i("on_my_charact", my_charact.getUuid().toString());
            Log.i("onDESCRIPTORS", descriptors.toString());

            gatt.setCharacteristicNotification(my_charact,true);

            for (BluetoothGattDescriptor descriptor : my_charact.getDescriptors()) {
                //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
                // and then call setValue on that descriptor

                descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }


        }



        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation


            Log.i("otroDATAA rata", characteristic.getUuid().toString().substring(7,8));

            String mStr = characteristic.getUuid().toString().substring(7, 8);

            int yy=Integer.parseInt(mStr);


            if (yy == 1){
                Log.i("Hey", "You recieve data");

                data_recieved = characteristic.getValue();
                //textView.setText("other name: " + data.toString() + "\n");
                Log.i("AQUI", characteristic.toString());
                Log.i("DATA", data_recieved.toString());
                Log.i("otroDATA",  characteristic.getValue().toString());
                Log.i("otroDATAA", characteristic.getUuid().toString());

                String s = Byte.toString(data_recieved[0]); //angle
                //String ss = s;
                Log.i("Read Angle ", s);
                s=Byte.toString(data_recieved[1]);
                Log.i("XPOSH***", s);
                s=Byte.toString(data_recieved[2]);
                Log.i("XPOSL***", s);
                s=Byte.toString(data_recieved[3]);
                Log.i("XSIGN***", s);

                //angle = data_recieved[0];

                Runnable runnable = new Runnable() {
                    public void run() {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();

                        String dataString =  data_recieved.toString();


                        bundle.putString("angle_read", Byte.toString(data_recieved[0]));
                        bundle.putString("x_pos_H_read", Byte.toString(data_recieved[1]));
                        bundle.putString("x_pos_L_read", Byte.toString(data_recieved[2]));
                        bundle.putString("sign_x_read", Byte.toString(data_recieved[3]));
                        bundle.putString("y_pos_H_read", Byte.toString(data_recieved[4]));
                        bundle.putString("y_pos_L_read", Byte.toString(data_recieved[5]));
                        bundle.putString("sign_y_read", Byte.toString(data_recieved[6]));
                        bundle.putString("total_H_read", Byte.toString(data_recieved[7]));
                        bundle.putString("total_L_read", Byte.toString(data_recieved[8]));
                        bundle.putString("step_H_read", Byte.toString(data_recieved[9]));
                        bundle.putString("step_L_read", Byte.toString(data_recieved[10]));
                        bundle.putString("angle_step", Byte.toString(data_recieved[11]));

                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

            }

            if (yy == 2){
                Log.i("Hey", "You send data");

            }


        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
