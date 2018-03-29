package me.dounx.nintendoeshophelper;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class CustomGlideModule extends AppGlideModule{
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }
}
