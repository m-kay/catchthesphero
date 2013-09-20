package com.namics.catchthesphero;


import com.namics.catchthesphero.robot.DriveChangeHandler;
import com.namics.catchthesphero.robot.DriveInput;

import orbotix.robot.base.CollisionDetectedAsyncData;
import orbotix.robot.base.ConfigureCollisionDetectionCommand;
import orbotix.robot.base.DeviceAsyncData;
import orbotix.robot.base.DeviceMessenger;
import orbotix.robot.base.RGBLEDOutputCommand;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RollCommand;
import orbotix.robot.base.CollisionDetectedAsyncData.CollisionPower;
import orbotix.robot.base.DeviceMessenger.AsyncDataListener;
import orbotix.robot.sensor.Acceleration;
import orbotix.view.connection.SpheroConnectionView;
import orbotix.view.connection.SpheroConnectionView.OnRobotConnectionEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Robot mRobot;
	
	private TextView mCollisionText;

	private DriveChangeHandler driveHandler;

	private DriveInput input;
	
	private SeekBar speedBar;
	
	private Handler mHandler = new Handler();

	/**
	 * The Sphero Connection View
	 */
	private SpheroConnectionView mSpheroConnectionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		input = new DriveInput();

		setContentView(R.layout.activity_main);
		findViewById(R.id.buttonView).requestFocus();

		speedBar = ((SeekBar) findViewById(R.id.speedBar));

		speedBar.setProgress((int) (input.getSpeed() * 100));

		speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				input.setSpeed(((float) progress) / 100);
			}
		});

		mSpheroConnectionView = (SpheroConnectionView) findViewById(R.id.sphero_connection_view);

		// Set the connection event listener
		mSpheroConnectionView
				.setOnRobotConnectionEventListener(new OnRobotConnectionEventListener() {
					// If the user clicked a Sphero and it failed to connect,
					// this event will be fired
					@Override
					public void onRobotConnectionFailed(Robot robot) {
						Toast.makeText(MainActivity.this, "Failed to connect",
								Toast.LENGTH_LONG).show();
					}

					// If there are no Spheros paired to this device, this event
					// will be fired
					@Override
					public void onNonePaired() {
						Toast.makeText(MainActivity.this, "No one paired",
								Toast.LENGTH_LONG).show();
						mSpheroConnectionView.setVisibility(View.GONE);
					}

					// The user clicked a Sphero and it successfully paired.
					@Override
					public void onRobotConnected(Robot robot) {
						mRobot = robot;

						mSpheroConnectionView.setVisibility(View.GONE);
						RGBLEDOutputCommand.sendCommand(mRobot, 255, 0, 255);

						driveHandler = new DriveChangeHandler(input, mRobot);
						
						// Calling Configure Collision Detection Command right after the robot connects, will not work
						// You need to wait a second for the robot to initialize
						mHandler.postDelayed(new Runnable() {
		                    @Override
		                    public void run() {
		        				// Start streaming collision detection data
		        				//// First register a listener to process the data
		        				DeviceMessenger.getInstance().addAsyncDataListener(mRobot,
		        						mCollisionListener);

		        				ConfigureCollisionDetectionCommand.sendCommand(mRobot, ConfigureCollisionDetectionCommand.DEFAULT_DETECTION_METHOD,
		        						45, 45, 100, 100, 100);
		                    }
		                }, 1000);
					}

					@Override
					public void onBluetoothNotEnabled() {
						Toast.makeText(MainActivity.this,
								"Bluetooth Not Enabled", Toast.LENGTH_LONG)
								.show();
					}
				});
		if (mRobot == null) {
			mSpheroConnectionView.showSpheros();
		}
	}

	public void onControllClick(View v) {
		driveHandler.directionChange((float) speedBar.getProgress() / 100);
				
		switch (v.getId()) {
		case R.id.left:
			input.setAngle(269f);
			break;
		case R.id.right:
			input.setAngle(89f); 
			break;
		case R.id.fwd:
			input.setAngle(0f);
			break;
		case R.id.bwd:
			input.setAngle(179);
			break;
		case R.id.stop:
			input.setAngle(0f);
			input.setSpeed(0f);
			break;
		default:
			input.setAngle(0f);
			input.setSpeed(0f);
		}
	}

	public void onRGBClick(View v) {
		switch (v.getId()) {
		case R.id.btnRed:
			RGBLEDOutputCommand.sendCommand(mRobot, 255, 0, 0);
			break;
		case R.id.btnBlue:
			RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 255);
			break;
		case R.id.btnGreen:
			RGBLEDOutputCommand.sendCommand(mRobot, 0, 255, 0);
			break;
		default:
			RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 0);
		}
	}
	
	private final AsyncDataListener mCollisionListener = new AsyncDataListener() {

		public void onDataReceived(DeviceAsyncData asyncData) {
			if (asyncData instanceof CollisionDetectedAsyncData) {
				final CollisionDetectedAsyncData collisionData = (CollisionDetectedAsyncData) asyncData;
				
				CollisionPower power = collisionData.getImpactPower();

				if (power.x + power.y >= 160) {
					mCollisionText = (TextView) findViewById(R.id.collisionText);
					mCollisionText.setText(mCollisionText.getText() + "Bang! " + power.x + " " + power.y +"\n");
					speedBar.setProgress(30);
					input.setSpeed(0.3f);
					// wrong calculation. Correct would be something like RemainingOf((input.getAngle() + 180) % 360) 
					Float angle = (float) (359 - input.getAngle());
					input.setAngle(angle);
					RollCommand.sendCommand(mRobot, angle, input.getSpeed(), true);
				}
			}
		}
	};

}
