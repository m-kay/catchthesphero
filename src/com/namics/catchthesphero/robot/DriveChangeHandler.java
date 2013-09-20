package com.namics.catchthesphero.robot;

import android.os.Handler;
import orbotix.robot.base.ConfigureCollisionDetectionCommand;
import orbotix.robot.base.DeviceMessenger;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RollCommand;

public class DriveChangeHandler {
	
	private DriveInput input;
	private DriveController drive;
	private Robot robot;
	private Handler mHandler = new Handler();
	
	public DriveChangeHandler(DriveInput input, Robot robot) {
		this.input = input;
		this.robot = robot;
		this.drive = new DriveController(this);
		this.drive.start();
	}
	
	public void directionChange(float speed) {
		RollCommand.sendCommand(robot, input.getAngle(), 0f);
		mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
            	RollCommand.sendCommand(robot, input.getAngle(), 1f, true);
            }
        }, (long) (300 * speed));
		
		drive.stopTimer();

		input.setSpeed(speed);
		
		drive = new DriveController(this);
		
		Long delay = (long) (200 + Math.round(1200 * speed));
		
		drive.start(delay);
	}
	
	public void driveApply() {
		RollCommand.sendCommand(robot, input.getAngle(), input.getSpeed());
	}
	
}
