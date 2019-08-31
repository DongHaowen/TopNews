package com.example.oddnews.bean;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.ParcelableSpan;
import android.view.View;

public class HyperLinkSpan extends ClickableSpan implements ParcelableSpan {
    private String metion;
    private String link;

    public HyperLinkSpan(final String metion, final String link) {
        this.metion = metion;
        this.link = link;
    }

    @Override
    public int getSpanTypeId() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public void onClick(View widget) {
        Context context = widget.getContext();
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        context.startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.RED);
        ds.setUnderlineText(false);
        ds.bgColor = Color.TRANSPARENT;
    }
}
