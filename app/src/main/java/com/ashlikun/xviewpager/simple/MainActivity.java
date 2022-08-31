package com.ashlikun.xviewpager.simple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ashlikun.glideutils.GlideUtils;
import com.ashlikun.xviewpager.ConvenientBanner;
import com.ashlikun.xviewpager.indicator.TransIndicator;
import com.ashlikun.xviewpager.listener.OnItemClickListener;
import com.ashlikun.xviewpager.listener.ViewPageHelperListener;
import com.ashlikun.xviewpager.view.BannerViewPager;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ViewPageHelperListener<String> {
    BannerViewPager bannerViewPager;
    ConvenientBanner convenientBanner;
    private static final Object[] RESURL = {
            "http://img.mukewang.com/54bf7e1f000109c506000338-590-330.jpg",
            "http://upload.techweb.com.cn/2015/0114/1421211858103.jpg",
            "http://img1.cache.netease.com/catchpic/A/A0/A0153E1AEDA115EAE7061A0C7EBB69D2.jpg",
            "http://image.tianjimedia.com/uploadImages/2015/202/27/57RF8ZHG8A4T_5020a2a4697650b89" +
                    "c394237ba9ffbb45fe8555a2cbec-6O6nmI_fw658.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4135477902,3355939884&fm=26&gp=0.jpg"};
    private static final Object[] RESURL2 = {
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4135477902,3355939884&fm=26&gp=0.jpg"};
    private static final Object[] RESURL3 = {
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F1115%2F101021113337%2F211010113337-6-1200.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650119085&t=68c7e6490778c511f908ef94404a6c16",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_bt%2F0%2F13879301757%2F641.jpg&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650119085&t=49ea2327fb3b425fd9572af344302a5a",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_bt%2F0%2F13879301788%2F641.jpg&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650119085&t=0bee7d9a47a5b4f26ba34e816c3284fb"
            , "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_bt%2F0%2F12140897000%2F1000.jpg&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650119085&t=a9fef4103946056c2164ee72b2dc9166"
    };
    private static final Object[] RESURL4 = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        bannerViewPager = findViewById(R.id.bannerViewPager);
//        bannerViewPager.setRefreshLayout(findViewById(R.id.swipeRefresh));
        convenientBanner = findViewById(R.id.convenientBanner);
        convenientBanner.setIndicator(new TransIndicator(this));
        convenientBanner.setPages(this, new ArrayList(Arrays.asList(RESURL3)));
//        convenientBanner.setPages(this, new ArrayList(Arrays.asList(RESURL3)));
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BannerViewPager banner, Object data, int position) {
                Toast.makeText(MainActivity.this, "aaa" + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
//        ArrayList aa = new ArrayList(Arrays.asList(RESURL3));
//        Collections.reverse(aa);
//        convenientBanner.setPages(aa);
//        if (bannerViewPager.getRealItemCount() == RESURL.length) {
//            bannerViewPager.setPages(new ArrayList(Arrays.asList(RESURL2)));
//            convenientBanner.setPages(new ArrayList(Arrays.asList(RESURL2)));
//        } else {
//            bannerViewPager.setPages(new ArrayList(Arrays.asList(RESURL)));
//            convenientBanner.setPages(new ArrayList(Arrays.asList(RESURL)));
//        }
    }

    @Override
    public View createView(Context context, BannerViewPager banner, String data, int position) {
        View inflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.main_item_order_details_goods, null);
        GlideUtils.show((ImageView) inflate.findViewById(R.id.goodsIv), data);

        inflate.findViewById(R.id.goodsPrintAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "aaaaa", Toast.LENGTH_LONG).show();
            }
        });
        return inflate;
    }

}
