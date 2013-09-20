package com.namics.catchthesphero.robot;

import java.util.Timer;
import java.util.TimerTask;

public class DriveController extends TimerTask{
	
	private Timer timer;
	private DriveChangeHandler handler;
	
	public DriveController(DriveChangeHandler handler){
		this.timer = new Timer();
		this.handler = handler;
	}
	
	@Override
	public void run() {
		handler.driveApply();
	}
	
	public void stopTimer(){
		this.timer.cancel();
	}
	
	public void start(){
		this.timer.scheduleAtFixedRate(this, 0, 200);
	}
	
	public void start(long delay){
		// 
		this.timer.scheduleAtFixedRate(this, delay, 200);
	}
}

