package com.elevator;

import java.util.ArrayList;

public class ElevatorOperate {

	public static ArrayList<Elevator> elevators = new ArrayList<Elevator>();
	public static ArrayList[][] queue = new ArrayList[20][2];
	public static boolean queLock[][] = new boolean[20][2];

	public static Elevator one;
	public static Elevator two;
	public static Elevator three;
	public static Elevator four;
	public static Elevator five;
	public static Elevator six;

	public static void init() {
		// 应答队列控制锁初始化
		for (int i = 0; i < 20; i++){
			queLock[i][0] = true;
			queLock[i][1] = true;
		}

		// 应答队列初始化
		for (int i = 0; i < 20; i++) {
			// 上升等待队列
			queue[i][0] = new ArrayList<Integer>();
			// 下降等待队列
			queue[i][1] = new ArrayList<Integer>();
		}

		// 初始化电梯
		one = new Elevator(1, 0 );
		elevators.add(one);

		two = new Elevator(2, 0);
		elevators.add(two);

		three = new Elevator(3, 0);
		elevators.add(three);

		four = new Elevator(4, 0);
		elevators.add(four);

		five = new Elevator(5, 0);
		elevators.add(five);

		six = new Elevator(6, 0);
		elevators.add(six);
	}

	public static class elevatorManager extends Thread{
		public elevatorManager(){
			start();
		}

		public void adjust(int index, int i) throws InterruptedException {
			// 最优停滞电梯位于当前楼层下方
			if (elevators.get(index).getCurrentFloor()< i){
				elevators.get(index).setCurrentState(1);
				elevators.get(index).addUp(i);
				elevators.get(index).setMaxUp(i);
				System.out.println("电梯" + (index + 1) + "启动 上升\n");
				Thread.sleep(500);
				return;
			}
			// 最优停滞电梯位于当前楼层上方
			if (elevators.get(index).getCurrentFloor()> i){
				elevators.get(index).setCurrentState(-1);
				elevators.get(index).addDown(i);
				elevators.get(index).setMinDown(i);
				System.out.println("电梯" + (index + 1) + "启动 下降\n");
				Thread.sleep(500);
				return;
			}
			// 最优停滞电梯位于当前楼层
			if (elevators.get(index).getCurrentFloor() == i){
				elevators.get(index).setCurrentState(1);
				System.out.println("电梯" + (index + 1) + "启动\n");
				Thread.sleep(500);
				return;
			}
		}

		public void run(){
			while (true){
				for (int i = 0; i < 20; i++){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 监测上升队列
					//	                    System.out.println(queLock[i][0]);
					while (!queLock[i][0]);
					if (!queue[i][0].isEmpty()){
						int index = -1,  distance = 1000000;
						for (int k = 0; k < 6; k++){
							if (elevators.get(k).getCurrentState() == 0 && !queue[i][0].isEmpty()){
								if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
									index = k;
									distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
								}
							}
							if (elevators.get(k).getCurrentFloor() >= i && elevators.get(k).getCurrentState() == -1
									&& elevators.get(k).downMin() >= i){
								if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
									index = -1;
									distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
								}
							}
							if (elevators.get(k).getCurrentFloor() <= i && elevators.get(k).getCurrentState() == 1
									&& elevators.get(k).upMax() >= i){
								if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
									index = -1;
									distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
								}
							}
						}
						System.out.println(i + ":" + index);
						if (index != -1 && !queue[i][0].isEmpty()){
							try {
								adjust(index, i);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				for (int i = 0; i < 20; i++){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 监测下降队列
					//	                    System.out.println(queLock[i][1]);
					while (!queLock[i][1]);
					if (!queue[i][1].isEmpty()){
						int index = -1,  distance = 1000000;
						for (int k = 0; k < 6; k++){
							if (elevators.get(k).getCurrentState() == 0 && !queue[i][1].isEmpty()){
								if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
									index = k;
									distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
								}
							}
							if (elevators.get(k).getCurrentFloor() >= i && elevators.get(k).getCurrentState() == -1
									&& elevators.get(k).downMin() <= i){
								if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
									index = -1;
									distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
								}
							}
							if (elevators.get(k).getCurrentFloor() <= i && elevators.get(k).getCurrentState() == 1
									&& elevators.get(k).upMax() <= i){
								if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
									index = -1;
									distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
								}
							}
						}
						//	                        System.out.println(i + ":" + index);
						if (index != -1 && !queue[i][1].isEmpty()){
							try {
								adjust(index, i);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void elevatorUp(int floor){
		queue[floor][0].add(floor);
	}
	
	public static void elevatorDown(int floor){
		queue[floor][1].add(floor);
	}

}
