package com.example.accountplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
                         implements AdapterView.OnItemClickListener{  //實作 listview 監聽器，來判斷listview的觸發
        static final String DB_NAME = "AccountDB";
        static final String TB_NAME = "acclist";
        static final int MAX = 8;
        static final String[] FROM =
                new String[]{"web", "account", "password"};
        //各個物件的宣告
        SQLiteDatabase db;
        Cursor cur;
        SimpleCursorAdapter adapter;
        EditText etweb, etaccount, etpassword;
        Button btDelete,btInsert,btUpdate;
        ListView lv;
        Toast tos;

        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ID的存取和使用
        etweb = (EditText)findViewById(R.id.WEB);
        etaccount = (EditText)findViewById(R.id.acc);
        etpassword = (EditText)findViewById(R.id.pass);
        btInsert =(Button)findViewById(R.id.add);
        btDelete = (Button) findViewById(R.id.del);
        btUpdate = (Button) findViewById(R.id.update);
        //建構SQLite
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "web VARCHAR(32), " +
                "account VARCHAR(16), " +
                "password VARCHAR(64))";

        db.execSQL(createTable);
        // cursor 指出位置和設定資料
        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        adapter = new SimpleCursorAdapter(this,                               //負責連接loayout
                R.layout.item, cur,
                FROM,
                new int[]{R.id.web, R.id.account, R.id.password}
                , 0);
        //listvieew 建構和觸發
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        requery();
    }
    //增加資料函式
    public void add(View v){
            String web = etweb.getText().toString();
            String account =  etaccount.getText().toString();
            String password =  etpassword.getText().toString();
            if(web.length()==0 || account.length()==0||password.length()==0)return;
            addData(web,account,password);
            tos = Toast.makeText(this,"",Toast.LENGTH_SHORT);
            tos.setText("已新增"+web+"帳號");
            tos.show();
        }
        private void addData(String web,String account,String password) {
            ContentValues cv = new ContentValues(3);

            cv.put(FROM[0], web);
            cv.put(FROM[1], account);
            cv.put(FROM[2], password);

            db.insert(TB_NAME, null, cv);
            requery();
        }
        @Override
        //選擇的listview 資料讀取
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            cur.moveToPosition(position);
            etweb.setText(cur.getString(cur.getColumnIndex(FROM[0])));
            etaccount.setText(cur.getString(cur.getColumnIndex(FROM[1])));
            etpassword.setText(cur.getString(cur.getColumnIndex(FROM[2])));

            btInsert.setEnabled(false);
            btUpdate.setEnabled(true);
            btDelete.setEnabled(true);
        }
        //刪除資撩
        public void onDelete(View v){
            db.delete(TB_NAME,"_id="+cur.getInt(0),null);
            requery();
            tos = Toast.makeText(this,"",Toast.LENGTH_SHORT);
            tos.setText("已刪除");
            tos.show();
        }
        //更新資料
        public void onInsertUpdate(View v) {
            String web = etweb.getText().toString().trim();
            String account = etaccount.getText().toString().trim();
            String password = etpassword.getText().toString().trim();
            if (web.length() == 0 || account.length() == 0 || password.length() == 0) return;

            if (v.getId() == R.id.update) {
                update(web, account, password, cur.getInt(0));
                tos = Toast.makeText(this, "", Toast.LENGTH_SHORT);
                tos.setText("已更新");
                tos.show();
            } else
                addData(web, account, password);

            requery();
        }
        private void update(String web,String account,String password,int id){
            ContentValues cv=new ContentValues(3);

            cv.put(FROM[0], web);
            cv.put(FROM[1], account);
            cv.put(FROM[2], password);

            db.update(TB_NAME,cv,"_id="+id,null);
         }
         //資料庫重新搜尋
        private void requery(){
              cur=db.rawQuery("SELECT * FROM "+TB_NAME,null);
              adapter.changeCursor(cur);
              if(cur.getCount()== MAX)
                  btInsert.setEnabled(false);
              else
                  btInsert.setEnabled(true);

            btUpdate.setEnabled(false);
            btDelete.setEnabled(false);
    }

}