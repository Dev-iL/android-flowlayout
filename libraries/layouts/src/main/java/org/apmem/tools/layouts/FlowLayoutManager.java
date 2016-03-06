package org.apmem.tools.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import org.apmem.tools.layouts.logic.CommonLogic;
import org.apmem.tools.layouts.logic.FlowLayoutProperties;
import org.apmem.tools.layouts.logic.LineDefinition;
import org.apmem.tools.layouts.logic.ViewDefinition;

import java.util.ArrayList;
import java.util.List;

public class FlowLayoutManager extends RecyclerView.LayoutManager {

    private final FlowLayoutProperties properties;

    List<LineDefinition> lines = new ArrayList<>();
    List<ViewDefinition> views = new ArrayList<>();

    public FlowLayoutManager() {
        this(new FlowLayoutProperties());
    }

    public FlowLayoutManager(FlowLayoutProperties properties) {
        this.properties = properties;
    }

    public FlowLayoutManager(Context ctx, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(FlowLayoutProperties.fromProperties(getProperties(ctx, attrs, defStyleAttr, defStyleRes)));
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return super.checkLayoutParams(lp);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        final int count = this.getItemCount();
        views.clear();
        lines.clear();
        for (int i = 0; i < count; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child); // reverted due to "java.lang.IllegalArgumentException: Called attach on a child which is not detached"
            measureChildWithMargins(child, 0, 0);

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            ViewDefinition view = new ViewDefinition(properties, child);
            view.setWidth(child.getMeasuredWidth());
            view.setHeight(child.getMeasuredHeight());
            view.setNewLine(lp.isNewLine());
            view.setGravity(lp.getGravity());
            view.setWeight(lp.getWeight());
            view.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
            views.add(view);
        }

        properties.setMaxWidth(this.getWidth() - this.getPaddingRight() - this.getPaddingLeft());
        properties.setMaxHeight(this.getHeight() - this.getPaddingTop() - this.getPaddingBottom());
        properties.setWidthMode(View.MeasureSpec.EXACTLY);
        properties.setHeightMode(View.MeasureSpec.EXACTLY);
        properties.setCheckCanFit(true);

        CommonLogic.fillLines(views, lines, properties);
        CommonLogic.calculateLinesAndChildPosition(lines);

        int contentLength = 0;
        final int linesCount = lines.size();
        for (int i = 0; i < linesCount; i++) {
            LineDefinition l = lines.get(i);
            contentLength = Math.max(contentLength, l.getLineLength());
        }

        LineDefinition currentLine = lines.get(lines.size() - 1);
        int contentThickness = currentLine.getLineStartThickness() + currentLine.getLineThickness();
        int realControlLength = CommonLogic.findSize(this.properties.getLengthMode(), this.properties.getMaxLength(), contentLength);
        int realControlThickness = CommonLogic.findSize(this.properties.getThicknessMode(), this.properties.getMaxThickness(), contentThickness);

        CommonLogic.applyGravityToLines(lines, realControlLength, realControlThickness, properties);

        for (int i = 0; i < linesCount; i++) {
            LineDefinition line = lines.get(i);
            applyPositionsToViews(line);
        }
    }

    private void applyPositionsToViews(LineDefinition line) {
        final List<ViewDefinition> childViews = line.getViews();
        final int childCount = childViews.size();
        for (int i = 0; i < childCount; i++) {
            final ViewDefinition child = childViews.get(i);
            final View view = child.getView();
            measureChildWithMargins(view, child.getWidth(), child.getHeight());

            layoutDecorated(view,
                    this.getPaddingLeft() + line.getLineStartLength() + child.getInlineStartLength(),
                    this.getPaddingTop() + line.getLineStartThickness() + child.getInlineStartThickness(),
                    this.getPaddingLeft() + line.getLineStartLength() + child.getInlineStartLength() + child.getWidth(),
                    this.getPaddingTop() + line.getLineStartThickness() + child.getInlineStartThickness() + child.getHeight()
            );
        }
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {
        @ViewDebug.ExportedProperty(mapping = {
                @ViewDebug.IntToString(from = Gravity.NO_GRAVITY, to = "NONE"),
                @ViewDebug.IntToString(from = Gravity.TOP, to = "TOP"),
                @ViewDebug.IntToString(from = Gravity.BOTTOM, to = "BOTTOM"),
                @ViewDebug.IntToString(from = Gravity.LEFT, to = "LEFT"),
                @ViewDebug.IntToString(from = Gravity.RIGHT, to = "RIGHT"),
                @ViewDebug.IntToString(from = Gravity.CENTER_VERTICAL, to = "CENTER_VERTICAL"),
                @ViewDebug.IntToString(from = Gravity.FILL_VERTICAL, to = "FILL_VERTICAL"),
                @ViewDebug.IntToString(from = Gravity.CENTER_HORIZONTAL, to = "CENTER_HORIZONTAL"),
                @ViewDebug.IntToString(from = Gravity.FILL_HORIZONTAL, to = "FILL_HORIZONTAL"),
                @ViewDebug.IntToString(from = Gravity.CENTER, to = "CENTER"),
                @ViewDebug.IntToString(from = Gravity.FILL, to = "FILL")
        })

        private boolean newLine = false;
        private int gravity = Gravity.NO_GRAVITY;
        private float weight = -1.0f;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.readStyleParameters(context, attributeSet);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        private void readStyleParameters(Context context, AttributeSet attributeSet) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout_LayoutParams);
            try {
                this.newLine = a.getBoolean(R.styleable.FlowLayout_LayoutParams_layout_newLine, false);
                this.gravity = a.getInt(R.styleable.FlowLayout_LayoutParams_android_layout_gravity, Gravity.NO_GRAVITY);
                this.weight = a.getFloat(R.styleable.FlowLayout_LayoutParams_layout_weight, -1.0f);
            } finally {
                a.recycle();
            }
        }

        public int getGravity() {
            return gravity;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public boolean isNewLine() {
            return newLine;
        }

        public void setNewLine(boolean newLine) {
            this.newLine = newLine;
        }
    }
}