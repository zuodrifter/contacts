package com.example.materialtest;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

public class register extends AppCompatActivity {

    private EditText edit_text1,edit_text2,edit_text3;
    private Button button;
    private MyDatabaseHelper dbHelper;
    private static final String DATABASE_NAME="ui.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="UI";
    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edit_text1 = (EditText) findViewById(R.id.edit_view1);
        edit_text2 = (EditText) findViewById(R.id.edit_view2);
        edit_text3 = (EditText) findViewById(R.id.edit_view3);
        button = (Button) findViewById(R.id.tijiao);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String account = edit_text1.getText().toString();
                String password = edit_text2.getText().toString();
                String password2 = edit_text3.getText().toString();

                dbHelper = new MyDatabaseHelper(register.this, DATABASE_NAME, null, DATABASE_VERSION);
                db = dbHelper.getReadableDatabase();
                Cursor cursor=db.query(TABLE_NAME, new String[]{"account"},null,null,null,null,null);
                if(cursor.moveToFirst()) {
                    do {
                        String account2=cursor.getString(cursor.getColumnIndex("account"));
                        if (account.equals(account2)) {
                                /*Toast.makeText(register.this,"此账号已注册",Toast.LENGTH_SHORT).show();
                                弹出提示后并不能禁止用户的继续注册，即仍能注册，只是跳出了一个弹窗
                                如果做成Dialog然后在其onclick里添加edit_text的清除文本就好了，这样每次重复的账号就能有效的清除*/
                            new AlertDialog.Builder(register.this).setTitle("警告")
                                    .setMessage("此账号已注册")
                                    .setPositiveButton("确定",null)
                                    .show();
                        }
                    }while (cursor.moveToNext());
                    cursor.close();
                }


                if (account.equals("") || password.equals("") || password2.equals("")) {
                    new AlertDialog.Builder(register.this).setTitle("警告")
                            .setMessage("注册信息禁止为空")
                            .setPositiveButton("确定", null)
                            .show();
                }
                else if(password.equals(password2))
                {
                    dbHelper=new MyDatabaseHelper(register.this,DATABASE_NAME,null,DATABASE_VERSION);
                    db =  dbHelper.getReadableDatabase();
                    db.execSQL("insert into UI (account,password) values(?,?)",new String[]{account,password});

                    Toast.makeText(register.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    Intent d=new Intent(register.this,MainActivity.class);
                    startActivity(d);
                }
                else
                {
                    Toast.makeText(register.this,"两次密码不一致", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
