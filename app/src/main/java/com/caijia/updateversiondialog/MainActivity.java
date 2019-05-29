package com.caijia.updateversiondialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.caijia.updateversion.AlertDialog;
import com.caijia.updateversion.UpdateVersionDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

  public void update(View view) {
      String remark = "1、版本下载<br>2、版本下载二<br>";
      String apkUrl = "http://128.1.46.182:7003/appUpdate/app-debug.apk";
    new AlertDialog.Builder(this)
        .setTitle("版本更新")
        .setMessage(Html.fromHtml(remark))
        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .setNegativeButton(
            "取消",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
              }
            })
        .create()
        .show();
  }
}
