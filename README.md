generator
===========================
generator是基于jsr269可插拔注释处理器的android注解框架,框架采用代码生成方式处理注解，其中没以反射方式调用Field或Method，提高了效率。代码生成在编译器，通俗点来说就是在eclipse或android studio build project的时候已经生成，有疑问请搜jsr269。

****
###　　　　　　　　　　　　Author:苏海双
###　　　　　　　　　 E-mail:573732089@qq.com

===========================
使用generator进行开发，可以为你带来以下方便
* 在Activity或Fragment中你可以用@setLayout设置layout
* 用@findview替代findViewByid
* 用@onClick替代setOnClick监听，还有@onLongClick,@onItemCLick等等
* 如果你需要在任一生命周期内做处理，可以在相关方法通过@onStart,@onPuase,@onResum,@onStop等处理

```Java
@setLayout(R.layout.activity_main)
public class MainActivity extends Activity {
    @findview(R.id.jump) Button jump;
    @findview(R.id.getImage) Button getImage;
    @findview(R.id.frag_show) Button frag_show;

    @onClick(R.id.jump) void click(){
        Intent intent = new Intent(this,SecActivity_.class);
        startActivity(intent);
        finish();
    }

    @onLongClick(R.id.jump) public void longClick(){
        Toast.makeText(this,"logClick",Toast.LENGTH_SHORT).show();
    }
```
===========================
使用gennerator，你需要注意的
* 生成的代码名称为你的Activity或Fragment名称加上"_",比如"MainActivity"生成后的源码为"MainActivity_"，在AndroidManif   est.xml中Activity名称要为"MainActivity_"
* 必须用@setLayout方式设置layout
* 使用注释的method和field不能为private






