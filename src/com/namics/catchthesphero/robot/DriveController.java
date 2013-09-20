package com.namics.catchthesphero.robot;

import java.util.Timer;
import java.util.TimerTask;

import orbotix.robot.base.Robot;
import orbotix.robot.base.RollCommand;

public class DriveController extends TimerTask{

	private DriveInput input;
	private Robot robot;
	private Timer timer;
	
	public DriveController(DriveInput input, Robot robot){
		this.input = input;
		this.robot = robot;
		this.timer = new Timer();
	}
	
	@Override
	public void run() {
			RollCommand.sendCommand(robot, input.getAngle(), input.getSpeed());
	}
	
	public void stopRoll(){
		this.timer.cancel();
		RollCommand.sendCommand(robot, 0f, 0f);
	}
	
	public void start(){
		this.timer.scheduleAtFixedRate(this, (long) (300 + (700 * input.getSpeed())), 200);
	}
}

