package com.example.materialtest;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class alter extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "ui.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "UI";
    private EditText et1, et2, et3;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);
        et1 = (EditText) findViewById(R.id.edit1);
        et2 = (EditText) findViewById(R.id.edit2);
        et3 = (EditText) findViewById(R.id.edit3);
        button = (Button) findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account1 = et1.getText().toString();
                String passwordOld = et2.getText().toString();
                String passwordNew = et3.getText().toString();
                if (account1.equals("") || passwordOld.equals("") || passwordNew.equals("")) {
                    new AlertDialog.Builder(alter.this).setTitle("警告")
                            .setMessage("帐号或密码不能空")
                            .setCancelable(false)
                            .setPositiveButton("确定", null)
                            .show();
                } else {
                    isUserinfo(account1, passwordOld, passwordNew);
                }
            }

            public Boolean isUserinfo(String account, String pass, String pass2) {
                String account1 = account;
                String passwordOld = pass;
                String passwordNew = pass2;
                dbHelper = new MyDatabaseHelper(alter.this, DATABASE_NAME, null, DATABASE_VERSION);
                db = dbHelper.getReadableDatabase();
                Cursor cursor=db.query(TABLE_NAME, new String[]{"account"},"account=?",new String[]{account1},null,null,"account");
                if(cursor.moveToNext()) {
                        /*该语句若是能在EditText的焦点离开时立即调用就不需要等到点击按钮时才响应了*/
                    Toast.makeText(alter.this, "存在此用户", Toast.LENGTH_SHORT).show();
                    Cursor cursor2 = db.query(TABLE_NAME, new String[]{"account", "password"}, "account=?", new String[]{account1}, null, null, "password");
                    if (cursor2.moveToFirst()) {
                        do {
                            String password = cursor2.getString(cursor2.getColumnIndex("password"));
                            if (passwordOld.equals(password)) {
                                db.execSQL("update UI set password = ? where account = ?", new String[]{passwordNew, account});
                                Toast.makeText(alter.this, "修改成功！", Toast.LENGTH_SHORT).show();
                                Intent e = new Intent(alter.this, MainActivity.class);
                                startActivity(e);
                            } else {
                                Toast.makeText(alter.this, "输入原密码不正确", Toast.LENGTH_SHORT).show();
                            }
                        } while (cursor2.moveToNext());
                        cursor2.close();
                    }
                    cursor.close();
                }
                else {
                    Toast.makeText(alter.this, "没有该用户", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}
