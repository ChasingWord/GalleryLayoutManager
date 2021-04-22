package com.chasing.myapplication.ui.main;

import android.content.Context;

import com.chasing.base.adapter.RecyclerQuickAdapter;
import com.chasing.base.adapter.RecyclerViewHelper;
import com.chasing.myapplication.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by chasing on 2021/4/22.
 */
public class SecondAdapter extends RecyclerQuickAdapter<String> {
    private static final int[] mColorArray = new int[]{R.color.teal_700, R.color.black, R.color.design_default_color_error,
            R.color.design_default_color_primary, R.color.design_default_color_secondary, R.color.purple_200, R.color.color_77f7f7};

    public SecondAdapter(@NotNull Context context) {
        super(context, R.layout.item_second);
    }

    @Override
    protected void convert(int viewType, RecyclerViewHelper helper, @Nullable String item) {
        helper.setImageResource(R.id.second_img, mColorArray[helper.getPosition() % 7]);
    }
}
