package it.cs.unipd.multiplesensors;

import java.util.ArrayList;

import it.cs.unipd.androiddefinitivesensorstester.R;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class MultipleSensors extends Activity implements SensorEventListener, OnItemSelectedListener {

	private class VectorValues {
		public float timestamp;
		public float x;
		public float y;
		public float z;
		
		VectorValues(float x, float y, float z, float timestamp) {
			this.x = x; this.y = y; this.z = z; this.timestamp = timestamp;
		}
	}
	
	private boolean recordingAccelerometer = false;
	private boolean recordingRotationVector = false;
	private boolean recordingGyroscope = false;
	private boolean recordingProximity = false;
	private boolean recordingLuminosity = false;
	private boolean recordingLinearAcceleration = false;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mRotationVector;
	private Sensor mGyroscope;
	private Sensor mProximity;
	private Sensor mLuminosity;
	private Sensor mLinearAcceleration;
	
	private int speedAccelerometer = (1000 / 30) * 1000;
	private int speedRotationVector = (1000 / 30) * 1000;
	private int speedGyroscope;
	private int speedProximity;
	private int speedLuminosity;
	private int speedLinearAcceleration;
	
	private double bufferDuration = 500000000.0; // 1 secondo
	private boolean bufferFull = false;
	private ArrayList<VectorValues> buffer = new ArrayList<VectorValues>();
	private float[] lastValueRotationVector = null;
	
	private long lastTimestampAccelerometer = 0;
	private long lastTimestampLinearAcceleration = 0;
	private long lastTimestampRotationVector = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiple_sensors);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.speeds_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		((Spinner) findViewById(R.id.speedAccelerometer)).setAdapter(adapter);
		((Spinner) findViewById(R.id.speedRotationVector)).setAdapter(adapter);
		((Spinner) findViewById(R.id.speedGyroscope)).setAdapter(adapter);
		((Spinner) findViewById(R.id.speedProximity)).setAdapter(adapter);
		((Spinner) findViewById(R.id.speedLuminosity)).setAdapter(adapter);
		((Spinner) findViewById(R.id.speedLinear)).setAdapter(adapter);
		
		((Spinner) findViewById(R.id.speedAccelerometer)).setOnItemSelectedListener(this);
		((Spinner) findViewById(R.id.speedRotationVector)).setOnItemSelectedListener(this);
		((Spinner) findViewById(R.id.speedGyroscope)).setOnItemSelectedListener(this);
		((Spinner) findViewById(R.id.speedProximity)).setOnItemSelectedListener(this);
		((Spinner) findViewById(R.id.speedLuminosity)).setOnItemSelectedListener(this);
		((Spinner) findViewById(R.id.speedLinear)).setOnItemSelectedListener(this);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		mLuminosity = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startOrStopASensor(View view) {
		
		Log.d("CLICK", "button clicked");
		int sensorID = view.getId();
		
		switch (sensorID) {
		case R.id.buttonStartAccelerometer:
		{
			if (recordingAccelerometer) {
				mSensorManager.unregisterListener(this, mAccelerometer);
				((Button) findViewById(R.id.buttonStartAccelerometer)).setText("Start Accelerometer");
			}
			else {
				Log.d("SPEED", Integer.toString(speedAccelerometer));
				mSensorManager.registerListener(this, mAccelerometer, speedAccelerometer);
				((Button) findViewById(R.id.buttonStartAccelerometer)).setText("Stop Accelerometer");
			}
			recordingAccelerometer = !recordingAccelerometer;
			break;
		}
		case R.id.buttonLinearRotation:
		{
			if (recordingLinearAcceleration) {
				mSensorManager.unregisterListener(this, mLinearAcceleration);
				((Button) findViewById(R.id.buttonLinearRotation)).setText("Start Linear Acceleration");
			}
			else {
				Log.d("SPEED", Integer.toString(speedLinearAcceleration));
				mSensorManager.registerListener(this, mLinearAcceleration, speedLinearAcceleration);
				((Button) findViewById(R.id.buttonLinearRotation)).setText("Stop Linear Acceleration");
			}
			recordingLinearAcceleration = !recordingLinearAcceleration;
		}
		case R.id.buttonStartGyroscope:
		{
			if (recordingGyroscope) {
				mSensorManager.unregisterListener(this, mGyroscope);
				((Button) findViewById(R.id.buttonStartGyroscope)).setText("Start Gyroscope");
			}
			else {
				mSensorManager.registerListener(this, mGyroscope, speedGyroscope);
				((Button) findViewById(R.id.buttonStartGyroscope)).setText("Stop Gyroscope");
			}
			recordingGyroscope = !recordingGyroscope;
		}
		case R.id.buttonStartLuminosity:
			if (recordingLuminosity) {
				mSensorManager.unregisterListener(this, mLuminosity);
				((Button) findViewById(R.id.buttonStartLuminosity)).setText("Start Luminosity");
			}
			else {
				mSensorManager.registerListener(this, mLuminosity, speedLuminosity);
				((Button) findViewById(R.id.buttonStartLuminosity)).setText("Stop Luminosity");
			}
			recordingLuminosity = !recordingLuminosity;
			break;
		
		case R.id.buttonStartProximitySensor:
			if (recordingProximity) {
				mSensorManager.unregisterListener(this, mProximity);
				((Button) findViewById(R.id.buttonStartProximitySensor)).setText("Start proximity");
			}
			else {
				mSensorManager.registerListener(this, mProximity, speedProximity);
				((Button) findViewById(R.id.buttonStartProximitySensor)).setText("Stop Proximity");
			}
			recordingProximity = !recordingProximity;
			break;
		case R.id.buttonStartRotationVector:
			if (recordingRotationVector) {
				mSensorManager.unregisterListener(this, mRotationVector);
				((Button) findViewById(R.id.buttonStartRotationVector)).setText("Start Rotation Vector");
			}
			else {
				Log.d("SPEED", Integer.toString(speedRotationVector));
				mSensorManager.registerListener(this, mRotationVector, speedRotationVector);
				((Button) findViewById(R.id.buttonStartRotationVector)).setText("Stop Rotation Vector");
			}
			recordingRotationVector = !recordingRotationVector;
		default:
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor == mAccelerometer) {
			
			if (lastTimestampAccelerometer != 0) {
				Log.d("TIMESTAMP", Long.toString((event.timestamp - lastTimestampAccelerometer) / 1000000));
			}
			lastTimestampAccelerometer = event.timestamp;
			
			actWithAccelerometerDataAndRotation(event);
			
			//actWithAccelerometerDataAndMitzell(event);
		}
		else if (event.sensor == mLinearAcceleration) {
			
			if (lastTimestampLinearAcceleration != 0) {
				Log.d("TIMESTAMP", Long.toString((event.timestamp - lastTimestampLinearAcceleration) / 1000000));
			}
			lastTimestampLinearAcceleration = event.timestamp;
			actWithLinearAccelerationData(event);
		}
		else if (event.sensor == mGyroscope) {
			if (((CheckBox) findViewById(R.id.printResultsGyroscope)).isChecked()) {
				((TextView) findViewById(R.id.xGyroscope)).setText("X: " + event.values[0]);
				((TextView) findViewById(R.id.yGyroscope)).setText("Y: " + event.values[1]);
				((TextView) findViewById(R.id.zGyroscope)).setText("Z: " + event.values[2]);
			}
		}
		else if (event.sensor == mLuminosity) {
			if (((CheckBox) findViewById(R.id.printResultsLuminosity)).isChecked()) {
				((TextView) findViewById(R.id.valueLuminosity)).setText("Value: " + event.values[0]);
			}
		}
		else if (event.sensor == mProximity) {
			if (((CheckBox) findViewById(R.id.printResultsProximity)).isChecked()) {
				((TextView) findViewById(R.id.valueProximity)).setText("Value: " + event.values[0]);
			}
		}
		else if (event.sensor == mRotationVector) {
			
			lastValueRotationVector = event.values.clone();
			
			if (lastTimestampRotationVector != 0) {
				Log.d("TIMESTAMP", Long.toString((event.timestamp - lastTimestampRotationVector) / 1000000));
			}
			lastTimestampRotationVector = event.timestamp;

			if (((CheckBox) findViewById(R.id.printResultsRorationVector)).isChecked()) {
				((TextView) findViewById(R.id.xRotationVector)).setText("X: " + event.values[0]);
				((TextView) findViewById(R.id.yRotationVector)).setText("Y: " + event.values[1]);
				((TextView) findViewById(R.id.zRotationVector)).setText("Z: " + event.values[2]);
				/*((TextView) findViewById(R.id.xRotationVector)).setText("X: " + componentsAccelerometerWithAngle[0]);
				((TextView) findViewById(R.id.yRotationVector)).setText("Y: " + componentsAccelerometerWithAngle[1]);
				((TextView) findViewById(R.id.zRotationVector)).setText("Z: " + componentsAccelerometerWithAngle[2]);*/
			}
		}
		
	}
	
	private void calculateXYZSecond(float x, float y, float z, float teta, 
			float xFirst, float yFirst, float zFirst) {

		double xSquare = Math.pow(x, 2), ySquare = Math.pow(y, 2), zSquare = Math.pow(z, 2);
		double sinTeta = Math.sin(teta), cosTeta = Math.cos(teta);
		
		double finalX = (float)((xSquare + (1 - xSquare) * cosTeta) * xFirst +
		(((1 - cosTeta) * x * y) - sinTeta * z) * yFirst +
		(((1 - cosTeta) * x * z) + sinTeta * y) * zFirst);
		
		double finalY = (float)((((1 - cosTeta) * y * x) + sinTeta * z) * xFirst +
		(ySquare + (1 - ySquare) * cosTeta) * yFirst +
		(((1 - cosTeta) * y * z) - sinTeta * x)  * zFirst);
		
		double finalZ = (float)((((1 - cosTeta) * z * x) - sinTeta * y) * xFirst +
		((1 - cosTeta) * z * y + sinTeta * x) * yFirst +
		(zSquare + (1 - zSquare) * cosTeta) * zFirst);
	}
	
	private void actWithAccelerometerDataAndRotation(SensorEvent event) {
		
		if (buffer.size() > 0 && event.timestamp - buffer.get(0).timestamp 
				> bufferDuration) {
			bufferFull = true;
		}
		else {
			buffer.add(new VectorValues(event.values[0], event.values[1], event.values[2],
					event.timestamp));
		}
		
		if (bufferFull) {
			float meanValueX = 0, meanValueY = 0, meanValueZ = 0;
			
			for (int i = 0; i < buffer.size(); i++) {
				meanValueX += buffer.get(i).x;
				meanValueY += buffer.get(i).y;
				meanValueZ += buffer.get(i).z;
			}
			
			meanValueX /= buffer.size();
			meanValueY /= buffer.size();
			meanValueZ /= buffer.size();
			
			float x = event.values[0] - meanValueX,
					y = event.values[1] - meanValueY,
					z = event.values[2] - meanValueZ;
			
			if (lastValueRotationVector != null) {
				
				float norm = this.calculateNorm(event.values[0], event.values[1], event.values[2]);
				float alpha = 2 * (float)Math.asin(norm);
				
				this.calculateXYZSecond(lastValueRotationVector[0] / norm, lastValueRotationVector[1] / norm, 
						lastValueRotationVector[2] / norm, alpha, 
						event.values[0], event.values[1], event.values[2]);
			}
		}
	}
	
	private void actWithAccelerometerDataAndMitzell(SensorEvent event) {
		if (buffer.size() > 0 && event.timestamp - buffer.get(0).timestamp 
				> bufferDuration) {
			bufferFull = true;
		}
		else {
			buffer.add(new VectorValues(event.values[0], event.values[1], event.values[2],
					event.timestamp));
		}
		
		if (bufferFull) {
			float meanValueX = 0, meanValueY = 0, meanValueZ = 0;
			
			for (int i = 0; i < buffer.size(); i++) {
				meanValueX += buffer.get(i).x;
				meanValueY += buffer.get(i).y;
				meanValueZ += buffer.get(i).z;
			}
			
			meanValueX /= buffer.size();
			meanValueY /= buffer.size();
			meanValueZ /= buffer.size();
			
			float x = event.values[0] - meanValueX,
					y = event.values[1] - meanValueY,
					z = event.values[2] - meanValueZ;
			
			float normMeanValues = (float)Math.sqrt(Math.pow(meanValueX, 2) + Math.pow(meanValueY, 2) 
					+ Math.pow(meanValueZ, 2));
			
			float vectorProduct = (x * meanValueX + y * meanValueY + z * meanValueZ) / (float)Math.pow(normMeanValues, 2);
		
			float vectorPComponentX = meanValueX * vectorProduct,
					vectorPComponentY = meanValueY * vectorProduct,
					vectorPComponentZ = meanValueZ * vectorProduct;
			
			float vectorHComponentX = x - vectorPComponentX,
					vectorHComponentY = y - vectorPComponentY,
					vectorHComponentZ = z - vectorPComponentZ;
		}
	}
	
	private void actWithLinearAccelerationData(SensorEvent event) {
		
		if (lastValueRotationVector != null) {
			
			float norm = this.calculateNorm(event.values[0], event.values[1], event.values[2]);
			float alpha = 2 * (float)Math.asin(norm);
			
			this.calculateXYZSecond(lastValueRotationVector[0] / norm, lastValueRotationVector[1] / norm, 
					lastValueRotationVector[2] / norm, alpha, 
					event.values[0], event.values[1], event.values[2]);
		}
		
	}
	
	private float calculateNorm(float x, float y, float z) {
		return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View view, int arg2,
			long id) {
		// TODO Auto-generated method stub
		
		Log.d("Log id view", Integer.toString(((Spinner) arg0).getId()));
		Log.d("Log id linear", Integer.toString(R.id.speedLinear));
		int frequency = 1;
		Log.d("ID", Long.toString(id));
		if (id == 0) {
			frequency = 20;
		}
		else if (id == 1) {
			frequency = 30;
		}
		else if (id == 2) {
			frequency = 50;
		}
		
		switch (((Spinner) arg0).getId()) {
		case R.id.speedAccelerometer:
			Log.d("Spinner", "acclerometer");
			speedAccelerometer = (1000 / frequency) * 1000;
			break;
			
		case R.id.speedGyroscope:
			Log.d("Spinner", "gyroscope");
			speedGyroscope = (1000 / frequency) * 1000;
			break;
		
		case R.id.speedLuminosity: 
			Log.d("Spinner", "luminosity");
			speedLuminosity = (1000 / frequency) * 1000;
			break;
			
		case R.id.speedProximity:
			Log.d("Spinner", "proximity");
			speedProximity = (1000 / frequency) * 1000;
			break;
			
		case R.id.speedRotationVector:
			Log.d("Spinner", "rotation vector");
			speedRotationVector = (1000 / frequency) * 1000;
			break;
		
		case R.id.speedLinear:
			Log.d("Spinner", "Changing speed linear");
			speedLinearAcceleration = (1000 / frequency) * 1000;
			break;
		default:
			break;
		}
		
	}
	
	public void updateSpeedSensor(Sensor sensor, int speed) {
		mSensorManager.unregisterListener(this, sensor);
		mSensorManager.registerListener(this, sensor, speed);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
