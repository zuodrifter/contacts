package com.example.materialtest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.name;
import static android.R.attr.phoneNumber;
import static com.example.materialtest.R.attr.title;

public class contacts extends AppCompatActivity {
    private ListView listView;
    private Map<String, String> contact;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
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
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_register:
                        Intent regi = new Intent(contacts.this, register.class);
                        startActivity(regi);
                        break;
                    case R.id.nav_alter:
                        Intent alt = new Intent(contacts.this, alter.class);
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

        Button button = (Button) findViewById(R.id.go_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent f = new Intent(contacts.this, MainActivity.class);
                startActivity(f);
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.
                    permission.READ_CONTACTS}, 1);
        } else {
            readContacts();
        }
    }

    private void readContacts() {
        listView = (ListView)findViewById(R.id.contacts_view);
        SimpleAdapter simpleAdapter;
        /*List<String> contactsList = new ArrayList<>();和Map<K,V>*/
        List<Map<String, String>> listmaps=new ArrayList<Map<String, String>>();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.
                    Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.NUMBER));

                    Map<String,String> map=new HashMap<String,String>();
                    map.put("name", displayName);
                    map.put("number", number);
                    listmaps.add(map);
                }
                /*SimpleAdapter构造函数
                第一个参数是context，即当前的Activity；第二个参数是要去填充ListView每一行内容的list；
                第三个参数resource是ListView每一行填充的布局文件。第四个参数String[] from表示名字数组，
                因为在ArrayList存放的都是Map<String,Object>的item，from中的名字就是为了索引ArrayList中的Object。
                第五个参数在构造函数中表示为int[] to，是索引layout中的id，对应前面每项的布局格式。*/
                simpleAdapter=new SimpleAdapter(contacts.this, listmaps,
                        android.R.layout.simple_expandable_list_item_2, new String[]{"name","number"},
                        new int[]{android.R.id.text1,android.R.id.text2});

                listView.setAdapter(simpleAdapter);
                simpleAdapter.notifyDataSetChanged();
                myCall(listmaps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public void myCall(final List<Map<String, String>> mapList){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(ContextCompat.checkSelfPermission(contacts.this, android.Manifest.
                        permission.CALL_PHONE) !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(contacts.this, new String[]{android.Manifest.
                            permission.CALL_PHONE},2);
                }
                else {
                    Map<String,String> mMap=mapList.get(position);
                    contact =  mapList.get(position);
                    String number = contact.get("number");
                    callphone();
                }
            }
        });
    }


    private void callphone(){
        try{
            Intent intent = new Intent(Intent.ACTION_CALL);
            String num = (String)contact.get("number");
            intent.setData(Uri.parse("tel:"+num));
            startActivity(intent);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]  permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }
                else {
                    Toast.makeText(this, "你拒绝了这个权限.", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if(grantResults.length > 0 && grantResults[0] ==PackageManager.
                        PERMISSION_GRANTED){
                    callphone();
                }
                else {
                    Toast.makeText(this, "你拒绝了这个权限.",Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

}