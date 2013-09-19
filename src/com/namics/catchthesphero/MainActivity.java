package com.namics.catchthesphero;


import orbotix.robot.base.RGBLEDOutputCommand;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RollCommand;
import orbotix.view.connection.SpheroConnectionView;
import orbotix.view.connection.SpheroConnectionView.OnRobotConnectionEventListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Robot mRobot;

	/**
	 * The Sphero Connection View
	 */
	private SpheroConnectionView mSpheroConnectionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.buttonView).requestFocus();

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
						RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 0);

					}

					@Override
					public void onBluetoothNotEnabled() {
						Toast.makeText(MainActivity.this,
								"Bluetooth Not Enabled", Toast.LENGTH_LONG)
								.show();
					}
				});

		View.OnClickListener btnActions = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
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
		};

		Button btnRed = (Button) findViewById(R.id.btnRed);
		btnRed.setOnClickListener(btnActions);

		Button btnBlue = (Button) findViewById(R.id.btnBlue);
		btnBlue.setOnClickListener(btnActions);

		Button btnGreen = (Button) findViewById(R.id.btnGreen);
		btnGreen.setOnClickListener(btnActions);
		
		mSpheroConnectionView.showSpheros();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
