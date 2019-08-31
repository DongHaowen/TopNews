package com.example.topnews;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.topnews.bean.HyperLink;
import com.example.topnews.bean.HyperLinkSpan;
import com.example.topnews.bean.MarqueeText;
import com.example.topnews.bean.News;
import com.example.topnews.utils.ImageHandler;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsActivity extends AppCompatActivity implements ViewPagerEx.OnPageChangeListener {
    private News news = null;
    private VideoView videoView = null;
    private MediaController mediaController = null;
    private int imageLength = 0;
    private File[] imagesSrc;
    private Set<String> loaded = new HashSet<>();
    private static int REQUEST_CODE=1;
    private Toolbar toolbar;

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
        Map<String, HyperLink> keyword = news.getLinkMap();
        SpannableString spanText = new SpannableString(news.content);
        for (Map.Entry<String, HyperLink> pair:keyword.entrySet()){
            // Log.d("Match Key", pair.getKey());
            Pattern p = Pattern.compile(pair.getKey()+"\\W");
            Matcher matcher = p.matcher(spanText);
            while (matcher.find()){
                int start = matcher.start();
                int end = matcher.end() - 1;
                // Log.d("Match",start+" "+end);
                spanText.setSpan(new HyperLinkSpan(pair.getValue().mention, pair.getValue().linkedURL)
                        ,start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        tx.setText(spanText);
        tx.setMovementMethod(LinkMovementMethod.getInstance());
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

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int prog = seekBar.getProgress();
            if(videoView != null && videoView.isPlaying())
                videoView.seekTo(prog);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    void initVideo(){
        final RelativeLayout video_part = findViewById(R.id.video_part);
        if(news.video.equals("")) video_part.setVisibility(View.GONE);
        videoView = findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(news.video);
    }

    void downloadImage(){
        String[] urls = news.getImage();
        if(urls == null)
            return;
        for (int i = 0 ; i < urls.length ; ++i){
            new ImageHandler(this).downloadImage(news.newsID,urls[i],i);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNews();
        if(news == null)
            this.finish();

        Log.d("Images:",String.valueOf(imageLength));
        downloadImage();

        setContentView(R.layout.activity_news);
        toolbar = (Toolbar) findViewById(R.id.toolbar_news);
        toolbar.inflateMenu(R.menu.news_menu);

        if(MainActivity.favorite.has(news.newsID))
            toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_full);
        else
            toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_empty);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.favorite_btn){
                    Log.d("MenuClick","Favorite Button");
                    if(MainActivity.favorite.has(news.newsID)){
                        MainActivity.favorite.remove(news.newsID);
                        toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_empty);
                    }else{
                        MainActivity.favorite.add(news.newsID);
                        toolbar.getMenu().findItem(R.id.favorite_btn).setIcon(R.drawable.star_full);
                    }
                    MainActivity.favorite.save();
                }
                return false;
            }
        });

        MarqueeText tx1 = findViewById(R.id.news_title);
        tx1.setText(news.title);

        updateNews();
        updateImages();
        initVideo();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) { }

    @Override
    public void onPageScrollStateChanged(int state) { }

}
