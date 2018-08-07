
[![Release](https://jitpack.io/v/ashLikun/XViewPager.svg)](https://jitpack.io/#ashLikun/XViewPager)

# **XViewPager**

## 使用方法

build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
并且:

```gradle
dependencies {
    implementation 'com.github.ashLikun:XViewPager:{latest version}'//XViewPager
}
```
### 1.用法
```java
         <com.ashlikun.xviewpager.ConvenientBanner
                android:id="@+id/convenientBanner"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="10dp"
                app:banner_turningTime="3000"
                app:banner_isOneDataOffLoopAndTurning="false"
                app:banner_isCanTouchScroll="true"
                app:banner_ratio="2" />

         <!--是否可以循环 默认true-->
         <attr name="banner_canLoop" format="boolean" />
         <!--自动播放时间间隔 默认5000-->
         <attr name="banner_turningTime" format="integer" />
         <!--大小比例，按照宽度 默认16/9.0f-->
         <attr name="banner_ratio" format="float" />
         <!--是否可以触摸, 默认true-->
         <attr name="banner_isCanTouchScroll" format="boolean" />
         <!--是否只有一条数据的时候禁用翻页, 默认true-->
         <attr name="banner_isOneDataOffLoopAndTurning" format="boolean" />

         <!--Indicatorde 属性-->
         <!--间距-->
         <attr name="ind_space" format="dimension" />
         <!--未选中的资源Id-->
         <attr name="ind_no_select" format="reference" />
         <!--选中的资源Id-->
         <attr name="ind_select" format="reference" />
        <attr name="ind_style">
            <!--默认指示器-->
            <enum name="ind_default" value="1"></enum>
            <!--缩放的指示器-->
            <enum name="ind_zoom" value="2"></enum>
            <!--平移的指示器-->
            <enum name="ind_trans" value="3"></enum>
        </attr>

        convenientBanner.setIndicator(new ZoomIndicator(this));
        bannerViewPager.setPages(this, new ArrayList(Arrays.asList(RESURL)));
        convenientBanner.setPages(this, new ArrayList(Arrays.asList(RESURL)));

        //创建itemview由使用者实现
        @Override
        public View createView(Context context, BannerViewPager banner, String data) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            GlideUtils.show(imageView, data);
            return imageView;
        }
```
### 混肴
####


