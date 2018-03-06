package com.example.materialtest;

import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import static android.R.attr.name;
import static android.R.attr.phoneNumber;
import static com.example.materialtest.R.attr.title;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME="ui.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="UI";
    private Button login_button;
    private EditText account_edit,password_edit;
    private Intent intent;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass,auto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbal);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.my2);
        }
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_call:
                        Intent call = new Intent(MainActivity.this, contacts.class);
                        startActivity(call);
                        break;
                    case R.id.nav_register:
                        Intent regi = new Intent(MainActivity.this, register.class);
                        startActivity(regi);
                        break;
                    case R.id.nav_alter:
                        Intent alt = new Intent(MainActivity.this, alter.class);
                        startActivity(alt);
                        break;
                    case R.id.nav_lianxi1:
                        Intent add = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                        add.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, name);
                        add.putExtra(android.provider.ContactsContract.Intents.Insert.JOB_TITLE,title);
                        add.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE,phoneNumber);
                        startActivity(add);
                        break;
                    case R.id.nav_lianxi2:
                        Intent update = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                        update.setType("vnd.android.cursor.item/person");
                        update.setType("vnd.android.cursor.item/contact");
                        update.setType("vnd.android.cursor.item/raw_contact");
                        update.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, name);
                        update.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, phoneNumber);
                        update.putExtra(android.provider.ContactsContract.Intents.Insert.JOB_TITLE, title);
                        startActivity(update);
                        break;
                }
                return true;
            }
        });

        //preferenceManager类中的getDefaultSharedPreferences()方法只接受一个context参数
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        account_edit = (EditText) findViewById(R.id.account);
        password_edit = (EditText) findViewById(R.id.password);
        auto_login = (CheckBox) findViewById(R.id.auto_login);
        login_button = (Button) findViewById(R.id.login);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            //将账号和密码都设置到文本框中
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            account_edit.setText(account);
            password_edit.setText(password);
            rememberPass.setChecked(true);
        }
        boolean isAuto = pref.getBoolean("auto_isCheck", false);
        if (isAuto) {
            auto_login.setChecked(true);
            Intent auto = new Intent(MainActivity.this, contacts.class);
            startActivity(auto);
        }

        login_button.setOnClickListener(new LoginListener());
    }

    class LoginListener implements OnClickListener{
        public void onClick(View v){
            String accountString = account_edit.getText().toString();
            String passwordString = password_edit.getText().toString();
            if(accountString.equals("")||passwordString.equals(""))
            {
                new AlertDialog.Builder(MainActivity.this).setTitle("警告")
                        .setMessage("帐号或密码不能空")
                        .setCancelable(false)
                        .setPositiveButton("确定", null)
                        .show();
            }else{
                isUserinfo(accountString,passwordString);
            }
        }
    }

    public Boolean isUserinfo(String account,String pass)
    {
        String accountString=account;
        String passwordString=pass;
        dbHelper = new MyDatabaseHelper(MainActivity.this, DATABASE_NAME, null, DATABASE_VERSION);
        db = dbHelper.getReadableDatabase();
        try{
            Cursor cursor=db.query(TABLE_NAME, new String[]{"account","password"},"account=?",new String[]{accountString},null,null,"password");
            while(cursor.moveToNext())
            {
                String password=cursor.getString(cursor.getColumnIndex("password"));
                if(passwordString.equals(password))
                {
                    editor = pref.edit();
                    if (rememberPass.isChecked()) {
                        //检查复选框是否被选中
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", accountString);
                        editor.putString("password", passwordString);
                    } else {
                        editor.clear();
                    }
                    if (auto_login.isChecked()) {
                        editor.putBoolean("auto_isCheck", true);
                    } else {
                        editor.putBoolean("auto_isCheck", false);
                    }
                    editor.apply();

                    new AlertDialog.Builder(MainActivity.this).setTitle("提示")
                            .setMessage("登录成功")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent c=new Intent(MainActivity.this,contacts.class);
                                    startActivity(c);
                                    finish();
                                }
                            }).show();
                    break;
                }
                else
                {
                    Toast.makeText(this, "用户名密码不正确",Toast.LENGTH_SHORT).show();
                    break;
                }
            }

        }catch(SQLiteException e){
            CreateTable();
        }
        return false;
    }

    private void CreateTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " (account varchar(30) primary key,password varchar(30));";
        try{
            db.execSQL(sql);
        }catch(SQLException ex){ }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "未定义按钮",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }
}
