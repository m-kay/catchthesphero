package com.namics.catchthesphero.robot;

public class DriveInput {
	
	private float angle = 0f;
	private float speed = 0f;
	
	public synchronized float getAngle(){
		return angle;
	}
	
	public synchronized void setAngle(float angle){
		this.angle = angle;
	}
	
	public synchronized float getSpeed(){
		return speed;
	}
	
	public synchronized void setSpeed(float speed){
		this.speed = speed;
	}

}
