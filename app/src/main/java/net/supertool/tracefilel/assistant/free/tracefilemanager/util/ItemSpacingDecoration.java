package net.supertool.tracefilel.assistant.free.tracefilemanager.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    public ItemSpacingDecoration(Context context, int spacing) {
        // 在构造函数中传入间距值，单位为像素
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 在这个方法中设置每个item的间距
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.top = spacing;
        outRect.bottom = spacing;
    }
}
