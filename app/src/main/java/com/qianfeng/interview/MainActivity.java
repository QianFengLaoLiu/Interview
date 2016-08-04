package com.qianfeng.interview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private UserHelper userHelper;

    private RecyclerView recyclerView;

    private RecyclerAdapter adapter;

    // 显示还剩多少人没有面试
    private TextView textView;

    // 所有的学生
    private List<User> allUsers = new ArrayList<>();

    // 还没有面试的学生
    private List<User> unUsers = new ArrayList<>();

    // task运行的次数
    private int times;

    // 一次面几个学生?
    private final int count = 3;

    // 当前选中的学生
    private List<User> tempUsers = new ArrayList<>();

    // task是否继续运行?
    private boolean run = true;

    // timer是否已经执行
    private boolean hasScheduled;

    private Timer timer = new Timer();

    private List<Integer> selectPositions = new ArrayList<>();

    // task每运行一次，从没有面试的学生中随机找一位
    private TimerTask task = new TimerTask()
    {
        @Override
        public void run()
        {
            if (!run)
            {
                return;
            }
            if (unUsers.size() == 0)
            {
                Toast.makeText(MainActivity.this, "面试结束", Toast.LENGTH_LONG)
                        .show();
                task.cancel();
                timer.cancel();
                // 让button不能再点击了
                findViewById(R.id.users_btn).setEnabled(false);
                return;
            }

            Random random = new Random();
            int ran = -1;
            selectPositions.clear();
            User tempUser = null;
            ran = random.nextInt(unUsers.size());
            tempUser = unUsers.get(ran);
            Log.d("tag", "tempUser = " + tempUser.getName() + ", id = "
                    + tempUser.getId());
            selectPositions.add(tempUser.getId());

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    adapter.setChecked(selectPositions);
                }
            });

            times++;
            if (times == 10)
            {
                run = false;
                times = 0;

                List<User> selectedList = new ArrayList<>();
                int i = 0;
                while(i < count)
                {
                    ran = random.nextInt(unUsers.size());
                    tempUser = unUsers.get(ran);
                    Log.d("tag", "tempUser = " + tempUser.getName() + ", id = "
                            + tempUser.getId());
                    if (selectedList.contains(tempUser))
                    {
                        continue;
                    }

                    i++;
                    selectedList.add(tempUser);

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    selectPositions.add(tempUser.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setChecked(selectPositions);
                        }
                    });
                }
            }
        }
    };

    // 手动改变某位同学的状态
    private RecyclerAdapter.IOnClickListener onClickListener = new RecyclerAdapter.IOnClickListener()
    {
        @Override
        public void onClick(final int position, View view)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this)
                    .setTitle("提示")
                    .setMessage(allUsers.get(position).getName() + " 已面试?")
                    .setPositiveButton("是的",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    User user = allUsers.get(position);
                                    user.setState(UserHelper.State.INTERVIEWED);
                                    userHelper.updateState(user);

                                    unUsers.clear();
                                    allUsers.clear();

                                    refreshData();
                                    adapter.notifyDataSetChanged();
                                }
                            })
                    .setNegativeButton("并没有",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    User user = allUsers.get(position);
                                    user.setState(UserHelper.State.UN_INTERVIEWED);
                                    userHelper.updateState(user);

                                    unUsers.clear();
                                    allUsers.clear();

                                    refreshData();
                                    adapter.notifyDataSetChanged();
                                }
                            }).setCancelable(true);

            builder.show();
        }
    };

    private RecyclerAdapter.IOnLongClickListener onLongClickListener = new RecyclerAdapter.IOnLongClickListener()
    {
        @Override
        public void onLongClick(final int position, View view)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this)
                    .setTitle("提示")
                    .setMessage(
                            "是否删除 " + allUsers.get(position).getName() + " ?")
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {

                                }
                            })
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    userHelper.deleteUser(allUsers
                                            .get(position));
                                    allUsers.clear();
                                    unUsers.clear();
                                    refreshData();
                                    adapter.notifyDataSetChanged();
                                }
                            });

            builder.show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        userHelper = UserHelper.getInstance();
        userHelper.insertUser();

        recyclerView = (RecyclerView) findViewById(R.id.users_rv);
        textView = (TextView) findViewById(R.id.users_left_tv);

        refreshData();

        adapter = new RecyclerAdapter(this, allUsers);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        adapter.setIOnClickListener(onClickListener);
        adapter.setIOnLongClickListener(onLongClickListener);
    }

    /**
     * 随机获取一位
     *
     * @param view
     */
    public void getUserByRandom(View view)
    {
        /*
         * if (!tempUsers.isEmpty()) { for (int i = 0; i < tempUsers.size();
         * i++) { User tempUser = tempUsers.get(i); if (tempUser != null) {
         * tempUser.setState(UserHelper.State.INTERVIEWED);
         * userHelper.updateState(tempUser);
         * 
         * unUsers.clear(); allUsers.clear();
         * 
         * refreshData(); adapter.notifyDataSetChanged(); } } }
         */

        tempUsers.clear();

        run = true;
        if (!hasScheduled)
        {
            hasScheduled = true;
            timer.schedule(task, 1000, 200);
        }
    }

    /**
     * 从数据库重新获取数据
     */
    private void refreshData()
    {
        List<User> unInterViewedUsers = getAllUnInterViewedUsers();
        for (User user : unInterViewedUsers)
        {
            Log.d("tag", "user = " + user.getName());
        }
        unUsers.addAll(unInterViewedUsers);
        textView.setText("还剩下" + unUsers.size() + "位同学");

        allUsers.addAll(getAllUsers());
    }

    private void resetData()
    {
        userHelper.reset();

        allUsers.clear();
        unUsers.clear();
        refreshData();
        adapter.notifyDataSetChanged();
    }

    public List<User> getAllUsers()
    {
        return userHelper.getAllUsers();
    }

    public List<User> getAllUnInterViewedUsers()
    {
        return userHelper.getAllUnInterViewedUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.start_inter_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_start)
        {
            Intent intent = new Intent(MainActivity.this,
                    QuestionActivity.class);
            startActivity(intent);

            return true;
        }
        else if (itemId == R.id.menu_reset)
        {
            resetData();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (tempUsers.isEmpty())
        {
            for (int i = 0; i < tempUsers.size(); i++)
            {
                User tempUser = tempUsers.get(i);
                if (tempUser != null)
                {
                    tempUser.setState(UserHelper.State.INTERVIEWED);
                    userHelper.updateState(tempUser);
                }
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        task.cancel();
        timer.cancel();
        userHelper = null;
    }

}
