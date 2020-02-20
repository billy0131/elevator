package com.elevator;



import java.util.*;



public class Elevator extends Thread{
    /**
     * Elevator's attributes:
     * currentState: to show the three state(stop -> -1, on - > 0, up -> 1)
     * currentFloor: to show the current floor
     * currentMaxFloor: to show the max floor the elevator will stop
     * stopList: to store the floors which the elevator will stop
     */
    private int name;
    private int currentState;
    private int emerState;
    private int currentFloor;
    private int currentMaxFloor;
    private int maxUp;
    private int minDown;



    private Comparator<Integer> cmpUp = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    };
    private Comparator<Integer> cmpDown = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    };
    private Queue<Integer> upStopList = new PriorityQueue<Integer>(15, cmpUp);
    private Queue<Integer> downStopList = new PriorityQueue<Integer>(15, cmpDown);


    Elevator(int name, int dir){
        this.name = name;
        maxUp = 0;
        minDown = 19;
        currentState = dir;
        currentFloor = 0;
        currentMaxFloor = 0;
        emerState = -1;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        if(currentState == -2){
            emerState = this.currentState;
        }
        if(currentState == 2){
            currentState = emerState;
            emerState = -1;
        }
        this.currentState = currentState;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void popUp() {
        upStopList.poll();
    }

    public void addUp(Integer pos){
        upStopList.add(pos);
    }

    public void popDown(Integer pos){
        downStopList.poll();
    }

    public void addDown(Integer pos){
        downStopList.add(pos);
    }

    public int upMax(){return maxUp;}

    public void setMaxUp(int maxUp){this.maxUp = maxUp;}

    public int downMin(){return minDown;}

    public void setMinDown(int minDown){this.minDown = minDown;}

    public void run() {
        while(true){
            // 上升状态
            while (currentState == 1){
                boolean blueFlag = false;
                // 下客
                if (!upStopList.isEmpty() && currentFloor  == upStopList.peek()) {
                    while (currentFloor  == upStopList.peek()) {
                        Integer a = upStopList.poll();
                        System.out.println("电梯" + name + ": 第" + (currentFloor + 1) + "楼" + "下客\n");
                        if(upStopList.isEmpty())
                            break;
                    }

                    blueFlag = true;
                }
                // 载上当前上升的人
                while (!ElevatorOperate.queLock[currentFloor][0]);
                ElevatorOperate.queLock[currentFloor][0] = false;
                if (!ElevatorOperate.queue[currentFloor][0].isEmpty()) {
                    for (int i = 0; i < ElevatorOperate.queue[currentFloor][0].size(); i++) {
                        if ((int) ElevatorOperate.queue[currentFloor][0].get(i) - 1 > maxUp) {
                            maxUp = (int) ElevatorOperate.queue[currentFloor][0].get(i) - 1;
                        }
                        addUp((Integer) ElevatorOperate.queue[currentFloor][0].get(i) - 1);
                        System.out.println("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ElevatorOperate.queue[currentFloor][0].get(i)
                        + "楼的乘客\n");
                    }

                    blueFlag = true;
                }
                ElevatorOperate.queue[currentFloor][0].clear();
                ElevatorOperate.queLock[currentFloor][0] = true;
                // 电梯走空 载上向下的人
                while (!ElevatorOperate.queLock[currentFloor][1]);
                ElevatorOperate.queLock[currentFloor][1] = false;
                if (upStopList.isEmpty() && !ElevatorOperate.queue[currentFloor][1].isEmpty()){
                    for (int i = 0; i < ElevatorOperate.queue[currentFloor][1].size();i++){
                        if ((int)ElevatorOperate.queue[currentFloor][1].get(i) - 1 < minDown){
                            minDown = (int)ElevatorOperate.queue[currentFloor][1].get(i) - 1;
                        }
                        addDown((Integer) ElevatorOperate.queue[currentFloor][1].get(i) - 1);
                        System.out.println("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ElevatorOperate.queue[currentFloor][1].get(i)
                                + "楼的乘客\n");
                    }
                    if (!downStopList.isEmpty()){
                    	ElevatorOperate.queue[currentFloor][1].clear();
                        setCurrentState(-1);
                        blueFlag = true;
                        ElevatorOperate.queLock[currentFloor][1] = true;
                        System.out.println("电梯" + name + " :开始下降\n");
                        break;
                    }
                }
                ElevatorOperate.queLock[currentFloor][1] = true;

                if (blueFlag){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
          
                }
                // 电梯空了 到顶了
                if (upStopList.isEmpty() || currentFloor == 19){
                    setCurrentState(0);
                    maxUp = 0;
                    minDown = 19;

                    System.out.println("电梯" + name + ": 停止运作\n");
                    break;
                }

                currentFloor++;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 下降状态
            while(currentState == -1){
                boolean blueFlag = false;
                // 下客
                if (!downStopList.isEmpty() && currentFloor  == downStopList.peek()) {
                    System.out.println(downStopList.peek());
                    while (currentFloor  == downStopList.peek()) {
                        Integer a = downStopList.poll();
                        System.out.println("电梯" + name + ": 第" + (currentFloor + 1) + "楼" + "下客\n");
                        if(downStopList.isEmpty())
                            break;
                    }
    
                    blueFlag = true;
                }
                // 载上当前下降的人
                while (!ElevatorOperate.queLock[currentFloor][1]);
                ElevatorOperate.queLock[currentFloor][1] = false;
                if (!ElevatorOperate.queue[currentFloor][1].isEmpty()) {
                    for (int i = 0; i < ElevatorOperate.queue[currentFloor][1].size(); i++) {
                        if ((int) ElevatorOperate.queue[currentFloor][1].get(i) - 1 < minDown) {
                            minDown = (int) ElevatorOperate.queue[currentFloor][1].get(i) - 1;
                        }
                        addDown((Integer) ElevatorOperate.queue[currentFloor][1].get(i) - 1);
                        System.out.println("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ElevatorOperate.queue[currentFloor][1].get(i)
                                + "楼的乘客\n");
                    }

                    blueFlag = true;
                }
                ElevatorOperate.queue[currentFloor][1].clear();
                ElevatorOperate.queLock[currentFloor][1] = true;

                // 电梯走空 载上向上的人
                while (!ElevatorOperate.queLock[currentFloor][0]);
                ElevatorOperate.queLock[currentFloor][0] = false;
                if (downStopList.isEmpty() && !ElevatorOperate.queue[currentFloor][0].isEmpty()){
                    for (int i = 0; i < ElevatorOperate.queue[currentFloor][0].size();i++){
                        if ((int)ElevatorOperate.queue[currentFloor][0].get(i) - 1 > maxUp){
                            maxUp = (int)ElevatorOperate.queue[currentFloor][0].get(i) - 1;
                        }
                        addUp((Integer) ElevatorOperate.queue[currentFloor][0].get(i) - 1);
                        System.out.println("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ElevatorOperate.queue[currentFloor][0].get(i)
                                + "楼的乘客\n");
                    }
                    if (!upStopList.isEmpty()){
                    	ElevatorOperate.queue[currentFloor][0].clear();
                        setCurrentState(1);
                        blueFlag = true;
                        ElevatorOperate.queLock[currentFloor][0] = true;
                        System.out.println("电梯" + name + " :开始上升\n");
                        break;
                    }
                }
                ElevatorOperate.queLock[currentFloor][0] = true;
                if (blueFlag){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
        
                }
                // 电梯走空 到底
                if (downStopList.isEmpty() || currentFloor == 0){

                    setCurrentState(0);
                    maxUp = 0;
                    minDown = 19;
                    System.out.println("电梯" + name + ": 停止运作\n");
                    break;
                }

                currentFloor--;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 停滞状态
            while(currentState == 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 防止线程阻塞
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
