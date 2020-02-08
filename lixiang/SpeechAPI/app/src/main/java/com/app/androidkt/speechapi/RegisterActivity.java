package com.app.androidkt.speechapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity implements View.OnClickListener{

    private EditText user;
    private EditText pw;
    private EditText pw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = findViewById(R.id.register_edit_name);
        pw =  findViewById(R.id.register_edit_pwd);
        pw2 =  findViewById(R.id.register_edit_checkpwd);
        Button button = findViewById(R.id.register_btn_register);

        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        /* REGISTER part
        final String username1 = user.getText().toString().trim();
        final String password1 = pw.getText().toString().trim();
        final String password2 = pw2.getText().toString().trim();
        //服务端地址
        final String serverPath = "http://xxx.xxx.xxx.xxx:8080/ServletTest/register";

        if (TextUtils.isEmpty(username1) || TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
            Toast.makeText(RegisterActivity.this, "请输入用户名，密码和确认密码！！！", Toast.LENGTH_SHORT).show();
        } else {
            if (!(password1.equals(password2))) {
                Toast.makeText(RegisterActivity.this, "两次输入的密码不同，请重新输入！！！", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //使用GET方式请求服务器只能这样
                            URL url = new URL(serverPath + "?username=" + username1 + "&password=" + password1);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("GET");
                            httpURLConnection.setConnectTimeout(5000);
                            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");
                            final int responseCode = httpURLConnection.getResponseCode();
                            if (200 == responseCode) {
                                InputStream inputStream = httpURLConnection.getInputStream();
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                final String responseMsg = bufferedReader.readLine();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (responseMsg.equals("equal")){
                                            Toast.makeText(RegisterActivity.this, "账号存在，请更改用户名！！", Toast.LENGTH_LONG).show();

                                        }else if(responseMsg.equals("true")){
                                            Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
                                            //注册成功跳转登录界面
                                            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                bufferedReader.close();
                                httpURLConnection.disconnect();
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }*/

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
