package com.namics.catchthesphero.robot;

import orbotix.robot.base.Robot;
import orbotix.robot.base.RollCommand;

public class DriveChangeHandler {
	
	private DriveInput input;
	private DriveController drive;
	private Robot robot;
	
	public DriveChangeHandler(DriveInput input, Robot robot) {
		this.input = input;
		this.robot = robot;
		this.drive = new DriveController(this);
		this.drive.start();
	}
	
	public void directionChange(float speed) {
		RollCommand.sendCommand(robot, input.getAngle(), 0.01f);
		drive.stopRoll();

		input.setSpeed(speed);
		
		drive = new DriveController(this);
		
		Long delay = (long) (200 + Math.round(1000 * speed));
		
		drive.start(delay);
	}
	
	public void driveApply() {
		RollCommand.sendCommand(robot, input.getAngle(), input.getSpeed());
	}
	
	public void driveStop() {
		RollCommand.sendCommand(robot, input.getAngle(), 0f);
	}
	
}
