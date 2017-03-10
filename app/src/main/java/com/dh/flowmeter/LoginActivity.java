package com.dh.flowmeter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_rem_username)
    CheckBox cbRemUsername;
    @BindView(R.id.cb_auto_login)
    CheckBox cbAutoLogin;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_call_admin)
    TextView tvCallAdmin;

    private Context mContext;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mContext = this;

        sp = getSharedPreferences("account", MODE_PRIVATE);
        String spUsername = sp.getString("username", null);
        String spPassword = sp.getString("password", null);
        if (spPassword != null) {
            cbRemUsername.setChecked(true);
            cbAutoLogin.setChecked(true);
            etUsername.setText(spUsername);
            etPassword.setText(spPassword);
            checkUser(spUsername, spPassword);
        } else if (spUsername != null) {
            cbRemUsername.setChecked(true);
            etUsername.setText(spUsername);
        }

        btnLogin.setOnClickListener(login());

        tvCallAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:admin@nuc.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "账户问题");
                data.putExtra(Intent.EXTRA_TEXT, "请填写您的详细信息和说明");
                startActivity(data);
            }
        });
    }

    private View.OnClickListener login() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username!= null && password != null) {
                    checkUser(username, password);
                } else {
                    Toast.makeText(mContext, "用户名或密码为空！", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void checkUser(String username, String password) {
        String adminUsername = "admin";
        String adminPassword = "123456";

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            enterMainActivity(username, password);
        } else {
            Toast.makeText(mContext, "用户名或密码错误，请核实或联系管理员！", Toast.LENGTH_SHORT).show();
        }
    }

    private void enterMainActivity(String username, String password) {
        SharedPreferences.Editor editor = sp.edit();
        if (cbAutoLogin.isChecked()) {
            cbRemUsername.setChecked(true);
            editor.putString("username", username);
            editor.putString("password", password);
        } else if (cbRemUsername.isChecked()) {
            editor.putString("username", username);
            editor.putString("password", null);
        } else {
            editor.putString("username", null);
            editor.putString("password", null);

        }

        editor.commit();
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (!cbAutoLogin.isChecked()) {
            sp.edit().putString("password", null).commit();
        }
        if (!cbRemUsername.isChecked()) {
            sp.edit().putString("username", null).commit();
        }

        super.onDestroy();
    }
}
