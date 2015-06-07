package org.hprose.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hprose.client.HproseHttpClient;
import hprose.common.HproseCallback1;
import hprose.common.HproseErrorEvent;
import hprose.io.HproseClassManager;

import java.sql.Date;

public class MainActivity extends Activity {

    static public enum Sex {
        Unknown, Male, Female, InterSex
    }
    static public class User {
        public String name;
        public Sex sex;
        public Date birthday;
        public int age;
        public boolean married;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HproseClassManager.register(User.class, "User");
        final Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HproseHttpClient client = new HproseHttpClient("http://hprose.com/example/index.php");
                client.invoke("hello", new Object[] {"hprose"}, new HproseCallback1<String>() {
                    @Override
                    public void handler(final String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setText(s);
                        }
                    });
                    }
                });
                client.invoke("getUserList", new HproseCallback1<User[]>() {
                    @Override
                    public void handler(final User[] users) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView text = (TextView)findViewById(R.id.textView);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < users.length; i++) {
                                sb.append("name: ");
                                sb.append(users[i].name);
                                sb.append("\r\n");
                                sb.append("sex: ");
                                sb.append(users[i].sex);
                                sb.append("\r\n");
                                sb.append("birthday: ");
                                sb.append(users[i].birthday);
                                sb.append("\r\n");
                                sb.append("age: ");
                                sb.append(users[i].age);
                                sb.append("\r\n");
                                sb.append("married: ");
                                sb.append(users[i].married);
                                sb.append("\r\n");
                                sb.append("\r\n");
                            }
                            text.setText(sb.toString());
                        }
                    });
                    }
                },
                new HproseErrorEvent() {
                    @Override
                    public void handler(final String s, final Throwable throwable) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView text = (TextView)findViewById(R.id.textView);
                            android.util.Log.e(s, throwable.getMessage(), throwable);
                        }
                    });
                    }
                }, User[].class);
                //button.setVisibility(View.GONE);//设置button隐藏不可见
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
