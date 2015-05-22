package hxws.generator.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import hxws.generator.R;
import hxws.generator.annotations.findview;
import hxws.generator.annotations.onClick;
import hxws.generator.annotations.setLayout;


@setLayout(R.layout.activity_sec)
public class SecActivity extends Activity {

    @findview(R.id.back) Button back;

    @onClick(R.id.back) void back(){
        Intent intent = new Intent(this,MainActivity_.class);
        startActivity(intent);
        finish();
    }

}
