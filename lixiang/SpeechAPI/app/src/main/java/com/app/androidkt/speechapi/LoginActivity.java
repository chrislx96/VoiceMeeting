package com.app.androidkt.speechapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {

    // sharedPreference write and read account info
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private Button login;
    private Button register;
    private EditText accountEdit;
    private EditText passwordEdit;

    private CheckBox rememberPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("info",MODE_PRIVATE);
        accountEdit = findViewById(R.id.login_edit_account);
        passwordEdit = findViewById(R.id.login_edit_pwd);
        rememberPass = findViewById(R.id.login_checkbox_rememberpwd);
        login = findViewById(R.id.login_btn_login);
        register = findViewById(R.id.login_btn_register);
        boolean isRemember = sp.getBoolean("remember_password", false);

        if (isRemember){
            // write account and password into edit text
            String account = sp.getString("account", "");
            String password = sp.getString("password", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }

        /* LOGIN part
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = accountEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                //服务端路径
                final String serverPath = "http://xxx.xxx.xxx.xxx:8080/ServletTest/login";
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this,"用户名或密码不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    editor = sp.edit();
                    if (rememberPass.isChecked()) {
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", username);
                        editor.putString("password", password);
                    } else {
                        editor.clear();
                    }
                    editor.commit();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //使用GET方式请求服务器只能这样
                                URL url = new URL(serverPath + "?username=" + username + "&password=" + password);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("GET");
                                httpURLConnection.setConnectTimeout(5000);
                                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");
                                int responseCode = httpURLConnection.getResponseCode();
                                if (200 == responseCode) {
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                    final String responseMsg = bufferedReader.readLine();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (responseMsg.equals("true")){
                                                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_LONG).show();
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
            }
        });
        */

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RecordingActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
