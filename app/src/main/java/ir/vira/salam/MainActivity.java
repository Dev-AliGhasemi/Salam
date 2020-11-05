package ir.vira.salam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.vira.network.NetworkInformation;
import ir.vira.salam.Contracts.MessageContract;
import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.DesignPatterns.Factory.ThreadFactory;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Repositories.UserRepository;
import ir.vira.salam.Sockets.ErrorSocketListener;
import ir.vira.salam.Sockets.SocketListener;
import ir.vira.salam.Threads.ConnectToServerThread;
import ir.vira.utils.AdvancedToast;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText editTextName, editTextIpAddress;
    private TextView textViewProfile;
    private Button btnSendRequest;
    private SharedPreferences sharedPreferences;
    private Utils utils;
    private Bitmap profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        utils = Utils.getInstance(this);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_name), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(getString(R.string.shared_key_profile))) {
            profile = Utils.getBitmap(sharedPreferences.getString(getString(R.string.shared_key_profile), ""));
            circleImageView.setImageBitmap(profile);
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
                }
            });
            alertDialog.show();
        });
        btnSendRequest.setOnClickListener((view) -> {
            if (editTextName.length() == 0)
                editTextName.setError("لطفا نامی برای خود وارد کنید.");
            else if (profile == null)
                AdvancedToast.makeText(this, "شما باید یک پروفایل انتخاب کنید !", Toast.LENGTH_LONG, "fonts/iran_sans.ttf").show();
            else {
                sharedPreferences.edit().putString(getString(R.string.shared_key_username), editTextName.getText().toString()).commit();
                btnSendRequest.setVisibility(View.INVISIBLE);
                Thread thread = ThreadFactory.getThread(ThreadType.CONNECT_TO_SERVER);

                SocketListener socketListener = socket -> {
                    if (socket.isConnected()) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("event", "JOIN");
                            jsonObject.put("ip", Utils.encodeToString(Utils.encryptData(networkInformation.getIpAddress(), EncryptionAlgorithm.AES)));
                            jsonObject.put("name", Utils.encodeToString(Utils.encryptData(editTextName.getText().toString(), EncryptionAlgorithm.AES)));
                            jsonObject.put("secretKey", Utils.encodeToString(utils.generateKey(EncryptionAlgorithm.AES).getEncoded()));
                            float ratio = (float) profile.getWidth() / profile.getHeight();
                            if (ratio > 1 || ratio < 1)
                                jsonObject.put("profile", Utils.getEncodeImage(Bitmap.createScaledBitmap(profile, 100, (int) (100 / ratio), true)));
                            else
                                jsonObject.put("profile", Utils.getEncodeImage(Bitmap.createScaledBitmap(profile, 100, 100, true)));
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            dataOutputStream.writeUTF(jsonObject.toString());
                            dataOutputStream.flush();
                            dataOutputStream.close();
                            socket.close();
                            ServerSocket serverSocket = new ServerSocket(getResources().getInteger(R.integer.portNumber));
                            Socket socketReceived = serverSocket.accept();
                            DataInputStream dataInputStream = new DataInputStream(socketReceived.getInputStream());
                            jsonObject = new JSONObject(dataInputStream.readUTF());
                            dataInputStream.close();
                            socketReceived.close();
                            serverSocket.close();
                            if (jsonObject.getString("requestStatus").equals("accept")) {
                                UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
                                MessageContract messageContract = (MessageContract) RepositoryFactory.getRepository(RepositoryType.MESSAGE_REPO);
                                List<UserModel> userModels = new ArrayList<>();
                                List<MessageModel> messageModels = new ArrayList<>();
                                JSONArray jsonArrayUsers = jsonObject.getJSONArray("users");
                                JSONArray jsonArrayMessages = jsonObject.getJSONArray("messages");
                                SecretKey secretKey;
                                byte[] decodedKey;
                                decodedKey = Utils.decodeToByte(jsonObject.getString("secretKey"));
                                secretKey = new SecretKeySpec(decodedKey, EncryptionAlgorithm.AES.name());
                                String ip, name;
                                byte[] prof;
                                for (int i = 0; i < jsonArrayUsers.length(); i++) {
                                    ip = Utils.decryptData(Utils.decodeToByte(jsonArrayUsers.getJSONObject(i).getString("ip")), secretKey, EncryptionAlgorithm.AES);
                                    name = Utils.decryptData(Utils.decodeToByte(jsonArrayUsers.getJSONObject(i).getString("name")), secretKey, EncryptionAlgorithm.AES);
                                    prof = Utils.decodeToByte(jsonArrayUsers.getJSONObject(i).getString("profile"));
                                    Bitmap profile = BitmapFactory.decodeByteArray(prof, 0, prof.length);
                                    SecretKey secretKeyForUser = new SecretKeySpec(Utils.decodeToByte(jsonArrayUsers.getJSONObject(i).getString("secretKey")), EncryptionAlgorithm.AES.name());
                                    UserModel userModel = new UserModel(ip, name, profile, secretKeyForUser);
                                    userModels.add(userModel);
                                }
                                userContract.addAll(userModels);
                                String text;
                                for (int i = 0; i < jsonArrayMessages.length(); i++) {
                                    ip = Utils.decryptData(Utils.decodeToByte(jsonArrayMessages.getJSONObject(i).getString("ip")), secretKey, EncryptionAlgorithm.AES);
                                    text = Utils.decryptData(Utils.decodeToByte(jsonArrayMessages.getJSONObject(i).getString("text")), secretKey, EncryptionAlgorithm.AES);
                                    MessageModel messageModel = new MessageModel(userContract.findUserByIP(ip), text);
                                    messageModels.add(messageModel);
                                }
                                messageContract.addAll(messageModels);
                                startActivity(new Intent(this, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("isAdmin", false));
                            } else {
                                runOnUiThread(() -> {
                                    btnSendRequest.setVisibility(View.VISIBLE);
                                    AdvancedToast.makeText(MainActivity.this, getResources().getString(R.string.msg_admin_did_not_allowed), Toast.LENGTH_LONG, "fonts/iran_sans.ttf").show();
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        btnSendRequest.setVisibility(View.VISIBLE);
                        AdvancedToast.makeText(MainActivity.this, getString(R.string.problem_in_connect_to_admin), Toast.LENGTH_LONG, "fonts/iran_sans.ttf").show();
                    }
                };
                ErrorSocketListener errorSocketListener = message -> {
                    runOnUiThread(() -> {
                        btnSendRequest.setVisibility(View.VISIBLE);
                        AdvancedToast.makeText(this, getResources().getString(R.string.problem_in_connect_to_admin), Toast.LENGTH_LONG, "fonts/iran_sans.ttf").show();
                    });
                };
                ((ConnectToServerThread) thread).setupConnection(networkInformation.getServerIpAddress(), getResources().getInteger(R.integer.portNumber), this, socketListener, errorSocketListener);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.chooseImageFromGallery) && data != null) {
            try {
                profile = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                circleImageView.setImageURI(data.getData());
                textViewProfile.setVisibility(View.INVISIBLE);
                putProfileInSharedPreferences(profile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == getResources().getInteger(R.integer.takeImage)) {
            try {
                profile = MediaStore.Images.Media.getBitmap(getContentResolver(), Utils.getTempImage());
                circleImageView.setImageBitmap(profile);
                putProfileInSharedPreferences(profile);
                textViewProfile.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void putProfileInSharedPreferences(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inPurgeable = true;
        bitmap = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.toByteArray().length, options);
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