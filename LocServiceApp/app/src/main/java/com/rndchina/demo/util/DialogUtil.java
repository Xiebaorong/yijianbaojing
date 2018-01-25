package com.rndchina.demo.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rndchina.demo.R;
import com.rndchina.demo.widget.MyDialog;

/**
 * Created by dell on 2017-8-28.
 */

public class DialogUtil {
    public interface DialogActionListener{
        public void onHandle(Bundle bundle);
    }

    public static void showConfirmCancelDialog(Activity context, String title, String content, final DialogActionListener listener1, final DialogActionListener listener2){
        View view = context.getLayoutInflater().inflate(R.layout.dialog_prompt_two_button, null);
        Button bn_promt = (Button) view.findViewById(R.id.bn_dialog_prompt_ob);
        Button bn_cancel = (Button) view.findViewById(R.id.bn_dialog_cancel_ob);
        TextView msg = (TextView) view.findViewById(R.id.tv_message);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        msg.setText(content);
        final MyDialog builder = new MyDialog(context,0,0,view,R.style.scDialogStyle);
        builder.setCancelable(false);
        builder.show();
        bn_promt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.cancel();
                if(listener1 != null) {
                    listener1.onHandle(null);
                }
            }
        });
        bn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.cancel();
                if(listener2 != null){
                    listener2.onHandle(null);
                }

            }
        });
    }

    public static void showConfirmDialog(Activity context, String title, String content){
        View view = context.getLayoutInflater().inflate(R.layout.dialog_prompt_one_button, null);
        Button bn_promt = (Button) view.findViewById(R.id.bn_dialog_prompt_ob);
        Button bn_cancel = (Button) view.findViewById(R.id.bn_dialog_cancel_ob);
        TextView msg = (TextView) view.findViewById(R.id.tv_message);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        msg.setText(content);
        final MyDialog builder = new MyDialog(context,0,0,view,R.style.scDialogStyle);
        builder.setCancelable(false);
        builder.show();
        bn_promt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.cancel();
            }
        });

    }
}
