package com.example.l3cache;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/*三级缓存:

1、网络缓存, 不优先加载, 速度慢,浪费流量
2、本地缓存, 次优先加载, 速度快
3、内存缓存, 优先加载, 速度最快


三级缓存原理：

首次加载 Android App 时，肯定要通过网络交互来获取图片，之后我们可以将图片保存至本地SD卡和内存中
之后运行 App 时，优先访问内存中的图片缓存，若内存中没有，则加载本地SD卡中的图片
总之，只在初次访问新内容时，才通过网络获取图片资源
* */

public class MainActivity extends AppCompatActivity {

    private static final String URL = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1512093937&di=71adce72ee4b74123657439144433092&src=http://img1.50tu.com/meinv/xinggan/2013-11-16/e65e7cd83f37eed87067299266152807.jpg";
    private BitmapManager mManager;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = new BitmapManager();
        mImageView = (ImageView) findViewById(R.id.iv);

        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
    }

    private void loadImage() {
        mManager.disPlay(mImageView, URL);

    }
}
