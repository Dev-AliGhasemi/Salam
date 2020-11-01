package ir.vira.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * This class provide some utils
 *
 * @author Ali Ghasemi
 */
public class Utils {

    private Context context;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static File tempImage = null;
    private static Utils utils;
    private static final String UNICODE_FORMAT = "utf-8";
    private static SecretKey secretKey;
    private static Cipher cipher;

    private Utils(Context context) {
        this.context = context;
    }

    public static Utils getInstance(Context context) {
        if (utils == null)
            utils = new Utils(context);
        return utils;
    }

    public Bitmap getBitmap(int imageId) {
        Drawable drawable = context.getResources().getDrawable(imageId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void setFont(String fontPath, View view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
        //When set inputType=textPassword fontFamily not work for textInputLayout so set font programmatically solve this problem .
        if (view instanceof TextInputLayout)
            ((TextInputLayout) view).setTypeface(typeface);
    }

    @Deprecated
    public void changeLayoutDirection(int direction) {
        Activity activity = (Activity) context;
        activity.getWindow().getDecorView().setLayoutDirection(direction);
    }

    public void setToolbar(Toolbar toolbar, AppCompatActivity activity, DrawerLayout drawerLayout, int colorArrow) {
        activity.setSupportActionBar(toolbar);
        activity.setTitle("");
        actionBarDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(colorArrow);
    }

    public void setSubToolbar(Toolbar toolbar, String title, AppCompatActivity activity, int direction, int homeIndicator, int textViewTitleID) {
        toolbar.setLayoutDirection(direction);
        activity.setTitle("");
        activity.setSupportActionBar(toolbar);
        ((TextView) toolbar.findViewById(textViewTitleID)).setText(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(homeIndicator);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return actionBarDrawerToggle;
    }

    public boolean checkPermission(String permission, String message, int requestCode, Activity activity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("permissionsList", Context.MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            if (!sharedPreferences.contains(String.valueOf(requestCode))) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(String.valueOf(requestCode), String.valueOf(requestCode));
                editor.commit();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                // This statement work when permission not very important
            else if (message.length() > 0) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                AdvancedToast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
            return false;
        } else {
            return true;
        }
    }

    public void takeImage(Activity activity, int imageCaptureRequestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempImage = createTempImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (tempImage != null) {
            Uri uri = FileProvider.getUriForFile(context, "image-capture", tempImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, imageCaptureRequestCode);
        } else
            AdvancedToast.makeText(context, "در گرفتن عکس مشکلی پیش آمده است .", Toast.LENGTH_LONG).show();
    }

    public void chooseImageFromGallery(Activity activity, int chooseImageFromGalleryRequestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpg");
        activity.startActivityForResult(intent, chooseImageFromGalleryRequestCode);
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        timeStamp = "JPEG_" + timeStamp;
        File tempImage = File.createTempFile(timeStamp, ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        return tempImage;
    }

    public static String getEncodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        String encodedStr = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        return encodedStr;
    }

    public static Bitmap getBitmap(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public HashMap<String, Integer> getScreenSize(Activity activity) {
        HashMap<String, Integer> screenSize = new HashMap<>();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenSize.put("Width", point.x);
        screenSize.put("Height", point.y);
        return screenSize;
    }

    public static Uri getTempImage() {
        return Uri.fromFile(tempImage);
    }

    public boolean isNationalCodeValid(String nationalCode) {
        if (nationalCode.length() == 10) {
            short mul = 0, remain;
            for (int i = nationalCode.length() - 2; i >= 0; i--) {
                mul += (Byte.parseByte(nationalCode.charAt(i) + "") * (10 - i));
            }
            remain = (short) (mul % 11);
            if (remain < 2) {
                if (remain == nationalCode.charAt(nationalCode.length() - 1))
                    return true;
                else
                    return false;
            } else {
                if ((Byte.parseByte(nationalCode.charAt(nationalCode.length() - 1) + "")) == 11 - remain)
                    return true;
                else
                    return false;
            }
        } else
            return false;
    }

    public static String encodeToString(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static byte[] decodeToByte(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }

    public SecretKey generateKey(EncryptionAlgorithm encryptionAlgorithm, int keySize) {
        try {
            if (secretKey == null) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(encryptionAlgorithm.name());
                keyGenerator.init(keySize);
                secretKey = keyGenerator.generateKey();
            }
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SecretKey generateKey(EncryptionAlgorithm encryptionAlgorithm) {
        try {
            if (secretKey == null) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(encryptionAlgorithm.name());
                secretKey = keyGenerator.generateKey();
            }
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher getCipher(EncryptionAlgorithm encryptionAlgorithm) {
        try {
            if (cipher == null)
                cipher = Cipher.getInstance(encryptionAlgorithm.name());
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encryptData(String data, EncryptionAlgorithm encryptionAlgorithm) {
        try {
            byte[] dataToEncrypt = data.getBytes(UNICODE_FORMAT);
            getCipher(encryptionAlgorithm).init(Cipher.ENCRYPT_MODE, generateKey(encryptionAlgorithm));
            return cipher.doFinal(dataToEncrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptData(byte[] dataToDecrypt, SecretKey secretKey, EncryptionAlgorithm encryptionAlgorithm) {
        try {
            getCipher(encryptionAlgorithm).init(Cipher.DECRYPT_MODE, secretKey);
            byte[] dataDecrypted = cipher.doFinal(dataToDecrypt);
            return new String(dataDecrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
