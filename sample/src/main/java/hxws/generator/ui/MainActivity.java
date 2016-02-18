package hxws.generator.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import hxws.generator.R;
import hxws.generator.annotations.findview;
import hxws.generator.annotations.lifecycle.onCreate;
import hxws.generator.annotations.onClick;
import hxws.generator.annotations.onLongClick;
import hxws.generator.annotations.rest.addRequest;
import hxws.generator.annotations.rest.mark.Ref;
import hxws.generator.annotations.rest.mark.RequestType;
import hxws.generator.annotations.setLayout;


@setLayout(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    public RequestQueue queue;
    @findview(R.id.jump) Button jump;
    @findview(R.id.getImage) Button getImage;
    @findview(R.id.frag_show) Button frag_show;
    @findview(R.id.image) ImageView pic;
    @findview(R.id.show) TextView show;

    @onClick(R.id.jump) void click(){
        Intent intent = new Intent(this,SecActivity_.class);
        startActivity(intent);
        finish();
    }

    @onLongClick(R.id.jump) public void longClick(){
        Toast.makeText(this,"longClick",Toast.LENGTH_SHORT).show();
    }

    @onClick(R.id.getImage) public void setGetImage(){ }

    @onClick(R.id.frag_show) void jumpFrag(){
//        FragmentTransaction tran = getFragmentManager().beginTransaction(); app
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        Myfrag myfrag = new Myfrag_();
        tran.replace(R.id.frag_lay,myfrag);
        tran.commit();
    }

    @onCreate
    void initRequestQueue() {
       queue = Volley.newRequestQueue(getApplicationContext());
    }

    @addRequest(methodType = "Get",url = "http://d.hiphotos.baidu.com/zhidao/pic/item/562c11dfa9ec8a13e028c4c0f603918fa0ecc0e4.jpg",
                requestType = RequestType.IMAGE,headers = {"Content-type:application/json"},
                ref_id = R.id.getImage,ref = Ref.CLICK)
    public void getIm(Bitmap bitmap,VolleyError error){
        pic.setImageBitmap(bitmap);
    }
}
