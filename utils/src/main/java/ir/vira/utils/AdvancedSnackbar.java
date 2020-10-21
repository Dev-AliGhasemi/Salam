package ir.vira.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;

/**
 * This snackbar can set can font
 *
 * @author Ali Ghasemi
 */
public class AdvancedSnackbar {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Snackbar make(View view, CharSequence charSequence, int duration, Context context, int font) {
        Snackbar snackbar = Snackbar.make(view, charSequence, duration);
        snackbar.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        Button button = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
        textView.setTypeface(context.getResources().getFont(font));
        button.setTypeface(context.getResources().getFont(font));
        return snackbar;
    }

    @Deprecated
    public static Snackbar make(View view, CharSequence charSequence, int duration, Context context, String font) {
        Snackbar snackbar = Snackbar.make(view, charSequence, duration);
        snackbar.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        Button button = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
        textView.setTypeface(typeface);
        button.setTypeface(typeface);
        return snackbar;
    }

    public static Snackbar makeWithMargin(View view, CharSequence charSequence, int duration, Context context, String font, int margin) {
        Snackbar snackbar = Snackbar.make(view, charSequence, duration);
        snackbar.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        layoutParams.setMargins(margin, layoutParams.topMargin, margin, margin);
        snackbar.getView().setLayoutParams(layoutParams);
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        Button button = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
        textView.setTypeface(typeface);
        button.setTypeface(typeface);
        return snackbar;
    }


}
