package com.ashlikun.xviewpager.simple;

import android.content.Context;
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
            "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1606378608&di=e9634670eaf53ea726b8750eae7ddb66&src=http://pic1.win4000.com/pic/8/7d/6efd393311.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606388709669&di=33cf88738d5bc5978d0d7a40e08dc831&imgtype=0&src=http%3A%2F%2Fwww.yewn.cn%2Fdata%2Fattachment%2Fforum%2F201306%2F29%2F223918t3mm2blj5sofjsbf.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606388694852&di=4eb73e6eb3892036a8aaf0c82305d6ce&imgtype=0&src=http%3A%2F%2Fpic29.nipic.com%2F20130510%2F12670735_133906313000_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606388761042&di=1aca6eaea68ad6ca5191a6727707c741&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2018-08-15%2F5b738504d10e8.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606388761040&di=187f66604bbd559d421edcbf52afc81c&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F9vo3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F72f082025aafa40fe237bdb3a964034f79f019ee.jpg",


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        bannerViewPager = findViewById(R.id.bannerViewPager);
//        bannerViewPager.setRefreshLayout(findViewById(R.id.swipeRefresh));
        convenientBanner = findViewById(R.id.convenientBanner);
        convenientBanner.setIndicator(new TransIndicator(this));
//        convenientBanner.setPages(this, new ArrayList(Arrays.asList(RESURL)));
        convenientBanner.setPages(this,new ArrayList(Arrays.asList(RESURL3)));
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BannerViewPager banner, Object data, int position) {
                Toast.makeText(MainActivity.this, "aaa" + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View view) {
//        Intent intent = new Intent(this, Main2Activity.class);
//        startActivity(intent);
        convenientBanner.setPages(new ArrayList(Arrays.asList(RESURL3)));
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
        GlideUtils.show((ImageView)inflate.findViewById(R.id.goodsIv), data);

        inflate.findViewById(R.id.goodsPrintAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "aaaaa", Toast.LENGTH_LONG).show();
            }
        });
        return inflate;
    }

}
