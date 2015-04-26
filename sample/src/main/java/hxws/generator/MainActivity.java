package hxws.generator;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import hxws.generator.annotations.findview;
import hxws.generator.annotations.lifecycle.afterInject;
import hxws.generator.annotations.lifecycle.onStart;
import hxws.generator.annotations.onClick;
import hxws.generator.annotations.onLongClick;
import hxws.generator.annotations.setLayout;


@setLayout(R.layout.activity_main)
public class MainActivity extends Activity {

    private RequestQueue queue;

    @findview(R.id.jump) Button jump;
    @findview(R.id.getImage) Button getImage;
    @findview(R.id.frag_show) Button frag_show;
    @findview(R.id.image) ImageView pic;

    @onClick(R.id.jump) void click(){
        Intent intent = new Intent(this,SecActivity_.class);
        startActivity(intent);
        finish();
    }

    @onLongClick(R.id.jump) public void longClick(){
        Toast.makeText(this,"logClick",Toast.LENGTH_SHORT).show();
    }

    @onClick(R.id.getImage) public void setGetImage(){
        ImageRequest request = new ImageRequest("http://i1.dpfile.com/2011-01-13/6475922_b.jpg(700x700)/thumb.jpg", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap returnBitmap) {
                pic.setImageBitmap(returnBitmap);
            }
        },0, 0, null ,
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        queue.add(request);
    }

    @onClick(R.id.frag_show) void jumpFrag(){
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        Myfrag myfrag = new Myfrag_();
        tran.replace(R.id.frag_lay,myfrag);
        tran.commit();
    }

    @onStart
    void doSome(){
        Toast.makeText(this,"onStart",Toast.LENGTH_SHORT).show();
    }

    @afterInject
    void initRequestQueue() {
        queue = Volley.newRequestQueue(getApplicationContext());
    }
}
