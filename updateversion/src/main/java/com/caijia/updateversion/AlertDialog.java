package com.caijia.updateversion;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class AlertDialog extends Dialog {

  private CharSequence title;
  private CharSequence content;
  private CharSequence okTitle;
  private CharSequence cancelTitle;
  private OnClickListener okClickListener;
  private OnClickListener cancelClickListener;

  public AlertDialog(@NonNull Context context, Builder builder) {
    super(context, R.style.Dialog_update_version);
    this.title = builder.title;
    this.content = builder.content;
    this.okTitle = builder.okTitle;
    this.cancelTitle = builder.cancelTitle;
    this.okClickListener = builder.okClickListener;
    this.cancelClickListener = builder.cancelClickListener;
  }

  public static int getScreenWidth(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  @Override
  public void show() {
    super.show();
    Window window = getWindow();
    if (window != null) {
      final WindowManager.LayoutParams params = window.getAttributes();
      params.width = (int) (getScreenWidth(getContext()) * 0.85f);
      params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
      window.setAttributes(params);
      setCanceledOnTouchOutside(true);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.alert_dialog);
    TextView tvTitle = findViewById(R.id.tv_title);
    TextView tvContent = findViewById(R.id.tv_content);
    TextView tvOk = findViewById(R.id.tv_ok);
    TextView tvCancel = findViewById(R.id.tv_cancel);

    tvTitle.setText(title.toString());
    tvContent.setText(content.toString());
    tvOk.setText(okTitle.toString());
    tvCancel.setText(cancelTitle.toString());

    tvOk.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (okClickListener != null) {
              okClickListener.onClick(AlertDialog.this, 1);
            }
          }
        });

    tvCancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (cancelClickListener != null) {
              cancelClickListener.onClick(AlertDialog.this, 0);
            }
          }
        });
  }

  public static class Builder {
    private CharSequence title;
    private CharSequence content;
    private CharSequence okTitle;
    private CharSequence cancelTitle;
    private OnClickListener okClickListener;
    private OnClickListener cancelClickListener;
    private Context context;

    public Builder(Context context) {
      this.context = context;
    }

    public Builder setTitle(CharSequence title) {
      this.title = title;
      return this;
    }

    public Builder setMessage(CharSequence content) {
      this.content = content;
      return this;
    }

    public Builder setPositiveButton(CharSequence okTitle, OnClickListener okClickListener) {
      this.okTitle = okTitle;
      this.okClickListener = okClickListener;
      return this;
    }

    public Builder setNegativeButton(
        CharSequence cancelTitle, OnClickListener cancelClickListener) {
      this.cancelTitle = cancelTitle;
      this.cancelClickListener = cancelClickListener;
      return this;
    }

    public AlertDialog create() {
      return new AlertDialog(context, this);
    }
  }
}
