package ir.vira.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

/**
 * This Toast can set custon font
 *
 * @author Ali Ghasemi
 */
public class AdvancedToast extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     * or {@link Activity} object.
     */
    private static Toast toast;

    public AdvancedToast(Context context) {
        super(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Toast makeText(Context context, String message, int duration, int font) {
        toast = Toast.makeText(context, message, duration);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTypeface(context.getResources().getFont(font));
        return toast;
    }

    @Deprecated
    public static Toast makeText(Context context, String message, int duration, String font) {
        toast = Toast.makeText(context, message, duration);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
        textView.setTypeface(typeface);
        return toast;
    }


    @Override
    public void show() {
        toast.show();
    }
}
