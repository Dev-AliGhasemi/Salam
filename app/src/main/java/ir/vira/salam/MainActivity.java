package ir.vira.salam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.vira.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText editTextName, editTextIpAddress;
    private TextView textViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        Utils utils = Utils.getInstance(this);
        circleImageView.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setItems(R.array.item_name, (dialog, position) -> {
                switch (position) {
                    case 0:
                        utils.chooseImageFromGallery(this, getResources().getInteger(R.integer.chooseImageFromGallery));
                        break;
                    case 1:
                        utils.takeImage(this, getResources().getInteger(R.integer.takeImage));
                        break;
                    case 2:
                        circleImageView.setImageURI(null);
                        textViewProfile.setVisibility(View.VISIBLE);
                        break;
                }
            });
            alertDialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.chooseImageFromGallery) && data != null) {
            circleImageView.setImageURI(data.getData());
            textViewProfile.setVisibility(View.INVISIBLE);
        } else if (requestCode == getResources().getInteger(R.integer.takeImage)) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Utils.getTempImage());
                circleImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textViewProfile.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeViews() {
        circleImageView = findViewById(R.id.mainImageProfile);
        textViewProfile = findViewById(R.id.mainTextProfile);
    }
}