package com.example.anastasia.threadfullexecutor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOICE_PREFERENCE = 2;
    private String corethreadpoolsize_string = "";
    private String maximumthreadpoolsize_string = "";
    private String number_of_tasks_string = "";
    private boolean prestart;
    private Button button;
    private Button button2;
    //private boolean corethreadtimeout;
    PoolManager poolManager;
    private TextView activeCountView;
    private TextView corePoolView;
    private TextView completedTaskCountView;
    private TextView taskCountView;

    public  Handler handler ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button1);

        button2 = (Button) findViewById(R.id.button2);
        loadPreferences();
        activeCountView = (TextView) findViewById(R.id.textView2);
        activeCountView.setText("0");
        corePoolView =  (TextView) findViewById(R.id.textView4);
        corePoolView.setText("0");
        completedTaskCountView = (TextView) findViewById(R.id.textView6);
        completedTaskCountView.setText("0");
        taskCountView = (TextView) findViewById(R.id.textView8);
        taskCountView.setText("0");
        activeCountView.setVisibility(View.VISIBLE);
        corePoolView.setVisibility(View.VISIBLE);
        completedTaskCountView.setVisibility(View.VISIBLE);
        taskCountView.setVisibility(View.VISIBLE);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                poolManager.createTasks();

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poolManager.cancelTasks();

            }
        });
         handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                    Bundle bundle = msg.getData();
                    String b1 = bundle.getString("result","");
                    int activeCount = bundle.getInt("activecount",0);
                    int core_poolsize = bundle.getInt("corepoolsize",0);
                    Long completedtask = bundle.getLong("completedtask",0);
                    Long taskcount = bundle.getLong("taskcount",0);
                    String active_countstring = " "+activeCount;
                    String poolsize_countstring = " "+core_poolsize;
                    String completedtask_string = ""+completedtask;
                    String task_string = ""+taskcount;
              //  activeCountView.setVisibility(View.VISIBLE);
                activeCountView.setText(active_countstring);
                //corePoolView.setVisibility(View.VISIBLE);
                corePoolView.setText(poolsize_countstring);
               // completedTaskCountView.setVisibility(View.VISIBLE);
                completedTaskCountView.setText(completedtask_string);
               // taskCountView.setVisibility(View.VISIBLE);
                    taskCountView.setText(task_string);



            }
        };






    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.threadpool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {


            case R.id.menu_preferences:
                // Display Settings page

                Intent preferenceList = new Intent(this,ThreadPreferencesActivity.class);
                startActivityForResult(preferenceList,REQUEST_CODE_CHOICE_PREFERENCE);
                //TODO


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void loadPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Resources resources = getResources();
        corethreadpoolsize_string = preferences.getString(resources.getString(R.string.corepoolsize_preference),"5");
        maximumthreadpoolsize_string = preferences.getString(resources.getString(R.string.maximumpoolsize_preference),"10");
        if (Integer.parseInt(corethreadpoolsize_string ) > Integer.parseInt(maximumthreadpoolsize_string) || Integer.parseInt(maximumthreadpoolsize_string) < 0 || Integer.parseInt(corethreadpoolsize_string) < 0){
            Toast.makeText(this,"You entered invalid values . Please reset them", Toast.LENGTH_LONG).show();

        }
        prestart = preferences.getBoolean(resources.getString(R.string.prestart),false);
       // corethreadtimeout = preferences.getBoolean(resources.getString(R.string.corethreadtimeout),false);
        number_of_tasks_string = preferences.getString(resources.getString(R.string.tasks),"3");

         poolManager = new PoolManager(corethreadpoolsize_string,maximumthreadpoolsize_string,prestart,number_of_tasks_string);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOICE_PREFERENCE){
           loadPreferences();
            Toast.makeText(this,"Maximum threadpoolsize: " +maximumthreadpoolsize_string + " Core threadpoolsize: "+corethreadpoolsize_string,Toast.LENGTH_LONG).show();

        }
    }

    public  class PoolManager {
        private ThreadPoolExecutor threadPoolExecutor;
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        private int KEEP_ALIVE_TIME = 1;
        private int tasks;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        private List<Future> mRunningTaskList ;
        public boolean running = TRUE;




        public PoolManager(String corethreadpoolsize, String maxthreadpoolsize, boolean prestart, String number_of_tasks){
            int core_thread_poolsize = Integer.parseInt(corethreadpoolsize);
            int maximum_threadpoolsize = Integer.parseInt(maxthreadpoolsize);
            mRunningTaskList = new ArrayList<Future>();
            tasks = Integer.parseInt(number_of_tasks);
            if (core_thread_poolsize <= 0){
                core_thread_poolsize = 5;
            }
            if (maximum_threadpoolsize <= 0){
                core_thread_poolsize = 10;
            }
            if (core_thread_poolsize > maximum_threadpoolsize ){
                maximum_threadpoolsize = core_thread_poolsize * 2;
            }
            threadPoolExecutor = new ThreadPoolExecutor(core_thread_poolsize,maximum_threadpoolsize,KEEP_ALIVE_TIME,KEEP_ALIVE_TIME_UNIT,queue);
            if (prestart == true){
                threadPoolExecutor.prestartAllCoreThreads();
            }





        }

        public void createTasks(){
            for(int i = 0; i < tasks; i++){
                RunnableTask runnableTask = new RunnableTask("Task no: " +i);
                Toast.makeText(getApplicationContext(),"no  added to queue"+i,Toast.LENGTH_LONG).show();
                Future future = threadPoolExecutor.submit(runnableTask);
                mRunningTaskList.add(future);

            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(running && !mRunningTaskList.isEmpty()){
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Bundle msgBundle = new Bundle();
                        msgBundle.putString("result", "Sending data main thread");
                        msgBundle.putInt("activecount",threadPoolExecutor.getActiveCount());
                        msgBundle.putInt("corepoolsize",threadPoolExecutor.getCorePoolSize());
                        msgBundle.putLong("completedtask",threadPoolExecutor.getCompletedTaskCount());
                        msgBundle.putLong("taskcount",threadPoolExecutor.getTaskCount());
                        Message msg = new Message();
                        msg.setData(msgBundle);
                        msg.setTarget(handler);
                        msg.sendToTarget();
                    }
                }
            }).start();












        }









        public void cancelTasks(){
            synchronized (this){
                queue.clear();
                for (Future task : mRunningTaskList) {
                    if (!task.isDone()) {
                        task.cancel(true);
                    }
                }

            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // this will run in the main thread
                    Toast.makeText(getApplicationContext(),"tasks cancelled",Toast.LENGTH_LONG).show();

                }

            });
            mRunningTaskList.clear();

        }




    }








}
