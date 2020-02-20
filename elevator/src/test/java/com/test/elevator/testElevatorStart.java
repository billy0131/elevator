package com.test.elevator;

import java.util.concurrent.TimeUnit;

import com.elevator.ElevatorOperate;




public class testElevatorStart {

    public static void main(String[] args) throws InterruptedException{

    	ElevatorOperate.init();
    	ElevatorOperate.elevatorManager manager=new ElevatorOperate.elevatorManager();
    	ElevatorOperate.one.start();
    	ElevatorOperate.two.start();
    	ElevatorOperate.three.start();
    	ElevatorOperate.four.start();
    	ElevatorOperate.five.start();
    	ElevatorOperate.six.start();
    	
    	TimeUnit.SECONDS.sleep(1);
    	ElevatorOperate.elevatorUp(10);
    }
}
