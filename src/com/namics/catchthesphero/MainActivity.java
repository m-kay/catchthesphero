package com.namics.catchthesphero;


import com.namics.catchthesphero.robot.DriveController;
import com.namics.catchthesphero.robot.DriveInput;

import orbotix.robot.base.RGBLEDOutputCommand;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RollCommand;
import orbotix.view.connection.SpheroConnectionView;
import orbotix.view.connection.SpheroConnectionView.OnRobotConnectionEventListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Robot mRobot;

	private DriveController controller;

	private DriveInput input;
	
	private SeekBar speedBar;

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

						controller = new DriveController(input, mRobot);
						controller.start();
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
		input.setSpeed(0f);
		RollCommand.sendCommand(mRobot, 0f, 0f);
		controller.stopRoll();
		
		input.setSpeed(((float) speedBar.getProgress()) / 100);
		controller = new DriveController(input, mRobot);
		controller.start();
		
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

}
