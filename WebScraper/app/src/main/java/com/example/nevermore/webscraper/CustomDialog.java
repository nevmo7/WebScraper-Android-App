package com.example.nevermore.webscraper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import javax.xml.transform.Templates;

/**
 * Created by Never More on 1/6/2019.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {

    public Activity a;
    public Dialog d;

    public CustomDialog (Activity c){
        super(c);
        this.a = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firsttime_custom_dialog);

        ImageView imageView = (ImageView) findViewById(R.id.firsttime_image);
        Button buttonOk = (Button) findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
