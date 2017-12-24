package com.example.anastasia.threadfullexecutor;

/**
 * Created by Anastasia on 10/27/2017.
 */

public class RunnableTask implements Runnable {
    private String message;
   public RunnableTask(String message){
       this.message = message;

   }
    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        System.out.println(""+Thread.currentThread().getName() + " says " +message);
        processmessage();
        System.out.println(""+Thread.currentThread().getName() + " says bye");




    }
    private void processmessage(){
       try {
        Thread.sleep(5000);
       }
       catch (InterruptedException e){
           e.printStackTrace();
       }
    }



}
