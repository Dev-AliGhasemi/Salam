package ir.vira.salam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.vira.salam.Network.NetworkInformation;
import ir.vira.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText editTextName, editTextIpAddress;
    private TextView textViewProfile;
    private Button btnSendRequest;
    private SharedPreferences sharedPreferences;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        utils = Utils.getInstance(this);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_name), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(getString(R.string.shared_key_profile))) {
            Bitmap bitmap = utils.getBitmap(sharedPreferences.getString(getString(R.string.shared_key_profile), ""));
            circleImageView.setImageBitmap(bitmap);
            textViewProfile.setVisibility(View.INVISIBLE);
        }

        if (sharedPreferences.contains(getString(R.string.shared_key_username))) {
            String username = sharedPreferences.getString(getString(R.string.shared_key_username), "");
            editTextName.setText(username);
        }
        NetworkInformation networkInformation = new NetworkInformation(this);
        editTextIpAddress.setText(networkInformation.getServerIpAddress());
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
                        circleImageView.setImageBitmap(null);
                        textViewProfile.setVisibility(View.VISIBLE);
                        sharedPreferences.edit().remove(getString(R.string.shared_key_profile)).commit();
                        break;
                }
            });
            alertDialog.show();
        });
        btnSendRequest.setOnClickListener((view) -> {
            if (editTextName.length() == 0)
                editTextName.setError("لطفا نامی برای خود وارد کنید.");
            else {
                sharedPreferences.edit().putString(getString(R.string.shared_key_username), editTextName.getText().toString()).commit();
                btnSendRequest.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.chooseImageFromGallery) && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                circleImageView.setImageURI(data.getData());
                textViewProfile.setVisibility(View.INVISIBLE);
                putProfileInSharedPreferences(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == getResources().getInteger(R.integer.takeImage)) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Utils.getTempImage());
                circleImageView.setImageBitmap(bitmap);
                putProfileInSharedPreferences(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textViewProfile.setVisibility(View.INVISIBLE);
        }
    }

    void putProfileInSharedPreferences(Bitmap bitmap) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_key_profile), utils.getEncodeImage(bitmap));
        editor.commit();
    }

    private void initializeViews() {
        circleImageView = findViewById(R.id.mainImageProfile);
        textViewProfile = findViewById(R.id.mainTextProfile);
        editTextName = findViewById(R.id.mainEditName);
        editTextIpAddress = findViewById(R.id.mainEditIP);
        btnSendRequest = findViewById(R.id.mainBtnSendRequest);
    }
}