package org.apmem.tools.layouts.logic;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import static android.os.Build.VERSION.SDK;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

public class FlowLayoutProperties extends RecyclerView.LayoutManager.Properties {
    private static final int DEFAULT_ORIENTATION = CommonLogic.HORIZONTAL;
    private static final boolean DEFAULT_SHOULD_DRAW_DEBUG = false;
    private static final float DEFAULT_WEIGHT = 0.0f;
    private static final int DEFAULT_GRAVITY = Gravity.START | Gravity.TOP;
    private static final int DEFAULT_LAYOUT_DIRECTION = View.LAYOUT_DIRECTION_LTR;
    private static final boolean DEFAULT_CAN_FIT = true;

    private int orientation = DEFAULT_ORIENTATION;
    private boolean debugDraw = DEFAULT_SHOULD_DRAW_DEBUG;
    private float weightDefault = DEFAULT_WEIGHT;
    private int gravity = DEFAULT_GRAVITY;
    private int layoutDirection = View.LAYOUT_DIRECTION_LTR;
    private int maxWidth;
    private int maxHeight;
    private boolean checkCanFit;
    private int widthMode;
    private int heightMode;

    public static FlowLayoutProperties fromProperties(RecyclerView.LayoutManager.Properties props){
        FlowLayoutProperties flp = new FlowLayoutProperties();
        // Defaults:
        flp.setDebugDraw(DEFAULT_SHOULD_DRAW_DEBUG);
        flp.setWeightDefault(DEFAULT_WEIGHT);
        flp.setGravity(DEFAULT_GRAVITY);
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            flp.setLayoutDirection(DEFAULT_LAYOUT_DIRECTION);
        flp.setCheckCanFit(DEFAULT_CAN_FIT);
        // From inputs:
        flp.setOrientation(props.orientation);
        /* Unused LayoutManager.Properties:
            flp.spanCount = props.spanCount;
            flp.reverseLayout = props.reverseLayout;
            flp.stackFromEnd = props.stackFromEnd;
        */
        return flp;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int orientation) {
        if (orientation == CommonLogic.VERTICAL) {
            this.orientation = orientation;
        } else {
            this.orientation = CommonLogic.HORIZONTAL;
        }
    }

    public boolean isDebugDraw() {
        return this.debugDraw;
    }

    public void setDebugDraw(boolean debugDraw) {
        this.debugDraw = debugDraw;
    }

    public float getWeightDefault() {
        return this.weightDefault;
    }

    public void setWeightDefault(float weightDefault) {
        this.weightDefault = Math.max(0, weightDefault);
    }

    public int getGravity() {
        return this.gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public int getLayoutDirection() {
        return layoutDirection;
    }

    public void setLayoutDirection(int layoutDirection) {
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            this.layoutDirection = layoutDirection;
        } else {
            this.layoutDirection = View.LAYOUT_DIRECTION_LTR;
        }
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxLength() {
        return this.orientation == CommonLogic.HORIZONTAL ? this.maxWidth : this.maxHeight;
    }

    public int getMaxThickness() {
        return this.orientation == CommonLogic.HORIZONTAL ? this.maxHeight : this.maxWidth;
    }

    public void setCheckCanFit(boolean checkCanFit) {
        this.checkCanFit = checkCanFit;
    }

    public boolean isCheckCanFit() {
        return checkCanFit;
    }

    public void setWidthMode(int widthMode) {
        this.widthMode = widthMode;
    }

    public void setHeightMode(int heightMode) {
        this.heightMode = heightMode;
    }

    public int getLengthMode() {
        return this.orientation == CommonLogic.HORIZONTAL ? this.widthMode : this.heightMode;
    }

    public int getThicknessMode() {
        return this.orientation == CommonLogic.HORIZONTAL ? this.heightMode : this.widthMode;
    }
}
