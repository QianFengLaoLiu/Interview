package com.qianfeng.interview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu Jianping
 *
 * @date : 16/4/10.
 */
public class UserHelper extends SQLiteOpenHelper
{
    public static final String FILE_NAME = "users_1606.txt";

    public static final String DB_NAME = "db_user";

    private static final int VERSION_CODE = 1;

    private static final String CREATE_TABLE_SQL = "create table "
            + Table.TABLE_NAME + "(" + Table._ID
            + " integer primary key autoincrement," + Table.COLUMN_NAME
            + " varchar(20), " + Table.COLUMN_STATE + " integer, "
            + Table.COLUMN_ID + " integer" + ")";

    private SQLiteDatabase db;

    private static Context mContext;

    private static UserHelper instance;

    public UserHelper(Context context)
    {
        super(context, DB_NAME, null, VERSION_CODE);
    }

    /**
     * 初始化在UserApplication里面进行
     *
     * @param context
     */
    public static void init(Context context)
    {
        mContext = context;
    }

    public static UserHelper getInstance()
    {
        if (instance == null)
        {
            synchronized (UserHelper.class)
            {
                if (instance == null)
                {
                    instance = new UserHelper(mContext);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d("tag", "onCreate >> " + CREATE_TABLE_SQL);
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    /**
     * 从assets目录的txt文件里录入数据到数据库
     */
    public void insertUser()
    {
        db = getWritableDatabase();

        // 查询一下数据库
        Cursor cursor = db.query(Table.TABLE_NAME,
                new String[]{Table.COLUMN_NAME}, null, null, null, null, null);
        // 如果已经有数据了，不用重复录入了
        if (cursor.moveToNext())
        {
            return;
        }

        try
        {
            InputStream inputStream = mContext.getAssets().open(FILE_NAME);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));

            String read = "";
            User user = new User();
            int id = 0;
            while ((read = bufferedReader.readLine()) != null)
            {
                user.setName(read);
                user.setId(id++);
                db.insert(Table.TABLE_NAME, null, getUserContentValues(user));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        db.close();
    }

    /**
     * 删除一个用户
     *
     * @param user
     */
    public void deleteUser(User user)
    {
        db = getWritableDatabase();

        db.delete(Table.TABLE_NAME, Table.COLUMN_ID + " = ?", new String[]{"" + user.getId()});

        db.close();
    }

    private ContentValues getUserContentValues(User user)
    {
        ContentValues values = new ContentValues();
        values.put(Table.COLUMN_NAME, user.getName());
        values.put(Table.COLUMN_STATE, user.getState());
        values.put(Table.COLUMN_ID, user.getId());
        return values;
    }

    /**
     * 获取全部的学生
     *
     * @return
     */
    public List<User> getAllUsers()
    {
        List<User> userList = new ArrayList<>();

        db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + Table.TABLE_NAME, null);

        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(Table.COLUMN_ID));
            String name = cursor.getString(cursor
                    .getColumnIndex(Table.COLUMN_NAME));
            int state = cursor
                    .getInt(cursor.getColumnIndex(Table.COLUMN_STATE));
            User user = new User();
            user.setName(name);
            user.setState(state);
            user.setId(id);
            userList.add(user);
        }

        db.close();
        return userList;
    }

    /**
     * 获取所有没有面试的学生
     *
     * @return
     */
    public List<User> getAllUnInterViewedUsers()
    {
        List<User> userList = new ArrayList<>();

        db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + Table.TABLE_NAME
                + " where " + Table.COLUMN_STATE + " = ?", new String[]{""
                + State.UN_INTERVIEWED});

        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(Table.COLUMN_ID));
            String name = cursor.getString(cursor
                    .getColumnIndex(Table.COLUMN_NAME));
            User user = new User();
            user.setName(name);
            user.setId(id);
            userList.add(user);
        }

        db.close();
        return userList;
    }

    /**
     * 重置数据
     */
    public void reset()
    {
        List<User> allUsers = getAllUsers();

        db = getWritableDatabase();

        for (int i = 0; i < allUsers.size(); i++)
        {
            User user = allUsers.get(i);
            user.setState(State.UN_INTERVIEWED);
            db.update(Table.TABLE_NAME, getUserContentValues(user),
                    Table.COLUMN_NAME + " = ?", new String[]{user.getName()});
        }

        db.close();
    }

    /**
     * 更新状态
     * 
     * @param user
     */
    public void updateState(User user)
    {
        db = getWritableDatabase();

        db.update(Table.TABLE_NAME, getUserContentValues(user),
                Table.COLUMN_NAME + " = ?", new String[]{user.getName()});

        db.close();
    }

    /**
     * 显示状态
     */
    public static class State
    {
        /**
         * 已面试
         */
        public static final int INTERVIEWED = 1;

        /**
         * 未面试
         */
        public static final int UN_INTERVIEWED = 0;
    }

    /**
     * 用户数据表
     */
    public static class Table implements BaseColumns
    {
        public static final String TABLE_NAME = "table_user";

        private static final String COLUMN_NAME = "name";

        private static final String COLUMN_STATE = "state";

        private static final String COLUMN_ID = "id";
    }

}
