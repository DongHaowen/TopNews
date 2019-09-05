package com.example.topnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.topnews.adapter.NewsFragmentPagerAdapter;
import com.example.topnews.bean.MarqueeText;
import com.example.topnews.bean.News;
import com.example.topnews.fragment.RecommendFragment;
import com.example.topnews.utils.ImageHandler;
import com.example.topnews.utils.RecommendAdpter;
import com.example.topnews.utils.UIModeUtil;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.jzvd.JZVideoPlayerStandard;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;

import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class NewsActivity extends AppCompatActivity implements ViewPagerEx.OnPageChangeListener {

    private News news = null;
    private MediaController mediaController = null;
    private int imageLength = 0;
    private File[] imagesSrc;
    private Set<String> loaded = new HashSet<>();
    private static int REQUEST_CODE=1;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private boolean favorite = false;

    private void getNews(){
        Intent intent = getIntent();
        GsonBuilder builder = new GsonBuilder();
        news = builder.create().fromJson(intent.getStringExtra("News"), News.class);
        try {
            imageLength = news.getImage().length;
        } catch (Exception e){

        }
        imagesSrc = new File[imageLength];
        for (int i = 0 ; i < imageLength ; ++i){
            imagesSrc[i] = null;
        }
    }

    public void updateNews(){
        // Highlight and HyperLink
        TextView tx = findViewById(R.id.news_content);
//        Map<String, HyperLink> keyword = news.getLinkMap();
//        SpannableString spanText = new SpannableString(news.content);
//        for (Map.Entry<String, HyperLink> pair:keyword.entrySet()){
//            // Log.d("Match Key", pair.getKey());
//            Pattern p = Pattern.compile(pair.getKey()+"\\W");
//            Matcher matcher = p.matcher(spanText);
//            while (matcher.find()){
//                int start = matcher.start();
//                int end = matcher.end() - 1;
//                // Log.d("Match",start+" "+end);
//                spanText.setSpan(new HyperLinkSpan(pair.getValue().mention, pair.getValue().linkedURL)
//                        ,start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            }
//        }
//        tx.setText(spanText);
        tx.setText(ToDBC(news.content));

        tx.setMovementMethod(LinkMovementMethod.getInstance());
//        DocumentView documentView = findViewById(R.id.document_view);
//        documentView.setText("Hello World"); // Set to `true` to enable justification
    }

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public void updateImages(){
        SliderLayout slider = findViewById(R.id.image_slider);

        if(imageLength == 0){
            Log.d("Image","No Images.");
            slider.setVisibility(View.GONE);
            return;
        }

        Log.d("Running:","Image Loading.");

        File[] images = new ImageHandler(this).loadImage(news.newsID);
        try {
            for (File image:images) {
                if(loaded.contains(image.getName()))
                    continue;
                try {
                    TextSliderView sliderView = new TextSliderView(this);
                    sliderView.image(image).setScaleType(TextSliderView.ScaleType.Fit);
                    slider.addSlider(sliderView);
                    loaded.add(image.getName());
                } catch (Exception e) {
                    Log.d("Image Fault.",news.newsID);
                    continue;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.d("Image Fault.",news.newsID);
            slider.setVisibility(View.GONE);
            return;
        }

        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(0);
        slider.stopAutoCycle();
        slider.addOnPageChangeListener(this);
    }

    void initVideo(){
        if (!news.video.equals("")) {
            JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
            jzVideoPlayerStandard.setVisibility(View.VISIBLE);
            jzVideoPlayerStandard.setUp("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4",
                    JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                    news.title);
        }

//        Glide.with(getBaseContext())
//                .load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640")
//                .into(jzVideoPlayerStandard.thumbImageView);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayerStandard.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayerStandard.releaseAllVideos();
    }

    void downloadImage() {
        String[] urls = news.getImage();
        if (urls == null)
            return;
        for (int i = 0; i < urls.length; ++i) {
            new ImageHandler(this).downloadImage(news.newsID, urls[i], i);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UIModeUtil.getInstance().changeModeUI(this);

        getNews();
        if(news == null)
            this.finish();

        Log.d("Images:",String.valueOf(imageLength));
        downloadImage();

        setContentView(R.layout.activity_news);
        toolbar = (Toolbar) findViewById(R.id.toolbar_news);
        toolbar.inflateMenu(R.menu.menu_news);

        if(MainActivity.favorite.has(news.newsID))
            toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_full);
        else
            toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_empty);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.favorite_btn) {
                    Log.d("MenuClick","Favorite Button");
                    if(MainActivity.favorite.has(news.newsID)) {
                        MainActivity.favorite.remove(news.newsID);
                        toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_empty);
                    }else{
                        MainActivity.favorite.add(news.newsID);
                        toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_full);
                    }
                    MainActivity.favorite.save();
                } else if (menuItem.getItemId() == R.id.share_btn) {
                    showShare();
                }
                return false;
            }
        });

        MarqueeText tx1 = findViewById(R.id.news_title);
        tx1.setText(news.title);

        updateNews();
        updateImages();
        initVideo();
        initRecommend();
    }

    private void initRecommend(){
        viewPager = findViewById(R.id.result_view_pager);
        viewPager.setCurrentItem(0);
        LinearLayout layout = findViewById(R.id.content_record_layout);
        layout.setBackgroundColor(getResources().getColor(R.color.activity_bg_color));
        TextView textView = findViewById(R.id.record_title);
        textView.setText("相关推荐");
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));

        fragments.clear();
        RecommendFragment fragment = new RecommendFragment();
        fragment.setAdapter(new RecommendAdpter(news));
        fragments.add(fragment);
        NewsFragmentPagerAdapter adapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(pageChangeListener);
    }

    public ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) { }

        @Override
        public void onPageSelected(int i) { }

        @Override
        public void onPageScrollStateChanged(int i) { }
    };


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) { }

    @Override
    public void onPageScrollStateChanged(int state) { }

    private void showShare() {

//        OnekeyShare oks = new OnekeyShare();
//        // title标题，微信、QQ和QQ空间等平台使用
//        oks.setTitle(news.title);
//        // titleUrl QQ和QQ空间跳转链接
////        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("GET SOME ABSTRACT");
//        // imagePath是图片的本地路径，确保SDcard下面存在此张图片
//
//        for (File img : new ImageHandler(this).loadImage(news.newsID)) {
//            Log.d("share", "showShare: " + img.getAbsolutePath());
//            try {
//                oks.setImagePath(img.getAbsolutePath());
//                break;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        // url在微信、Facebook等平台中使用
////        oks.setUrl("http://sharesdk.cn");
//        // 启动分享GUI
//        oks.show(this);



        final OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();

// title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("title");
// text是分享文本，所有平台都需要这个字段
        oks.setText("abstract");
// imagePath是图片的本地路径，确保SDcard下面存在此张图片

        try {
            for (File img : new ImageHandler(this).loadImage(news.newsID)) {
                Log.d("share", "showShare: " + img.getAbsolutePath());
                try {
                    oks.setImagePath(img.getAbsolutePath());
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


// url在微信、Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if (Wechat.NAME.equals(platform.getName()) ||
                        WechatMoments.NAME.equals(platform.getName())) {
                }
                if (SinaWeibo.NAME.equals(platform.getName())) {
                }
            }
        });

// 启动分享GUI
        oks.show(this);

    }
}
