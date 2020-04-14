package com.example.application.ui.notifications;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.face.FaceRegistActivity;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.login.LoginActivity;
import com.leon.lib.settingview.LSettingItem;

import java.io.File;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsFragment extends Fragment {
    public ImageView imageView;
    public CircleImageView circleImageView;
    public LSettingItem settingItem1;
    public LSettingItem settingItem2;
    public LSettingItem settingItem3;
    public Button button;
    public TextView username;

    private Uri imageUri;

    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        imageView = root.findViewById(R.id.profile_image);
        circleImageView = root.findViewById(R.id.profile_image);
        settingItem1 = (LSettingItem) root.findViewById(R.id.item_one);
        settingItem2 = (LSettingItem) root.findViewById(R.id.item_two);
        settingItem3 = root.findViewById(R.id.item_three);
        button = root.findViewById(R.id.delete);
        username = root.findViewById(R.id.username);

        settingItem1.setLeftText("注册人脸信息");
        settingItem2.setLeftText("用户信息修改");
        settingItem3.setLeftText("其他设置");
        username.setText(SharedPrefUtil.getUserName(getActivity()));         //获取登录的用户名

        circleImageView.setOnClickListener(new View.OnClickListener() {             //更改用户头像
            @Override
            public void onClick(View view) {
                String title = "选择头像来源";
                String[] items = new String[]{"相册","拍照"};
                new AlertDialog.Builder(getActivity())
                        .setTitle(title)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                switch (which){
                                    case 0:
                                        Toast.makeText(getActivity(), "相册", Toast.LENGTH_SHORT).show();
                                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
                                        }else {
                                            //从相册选择
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                            intent.setType("image/*");
                                            startActivityForResult(intent, PICK_PHOTO);
                                        }
                                        break;
                                    case 1:
                                        Toast.makeText(getActivity(), "拍照", Toast.LENGTH_SHORT).show();
                                        File outputImage = new File(getContext().getExternalCacheDir(),"output_image.jpg");

                                        /*从6.0开始，读写sd卡被列为危险权限，如果将图片存放在sd卡的任何其他目录，
                                        都要进行运行时的权限处理才行，而使用应用关联目录则可以跳过这一步
                                         */
                                        try {
                                            if (outputImage.exists()){
                                                outputImage.delete();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        /*从7.0开始，直接用本地真实路径Uri被认为不安全，会抛出一个FileUriExposedException异常。
                                        而FileProvider则是一种特殊的内容提供器，他使用了内容提供器类似的机制对数据进行保护
                                        ，可以选择性地的、将封装过的Uri共享给外部，从而提高了应用的安全性
                                         */
                                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                                            //大于等于24（7.0）场合
                                            imageUri = FileProvider.getUriForFile(getContext(),"com.wx.photo.provider", outputImage);
                                        }else {
                                            //小于android版本7.0（2.4）的场合
                                            imageUri = Uri.fromFile(outputImage);
                                        }
                                        //启动相机程序
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        //MediaStore.ACTION_IMAGE_CAPTURE =
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                        startActivityForResult(intent, TAKE_CAMERA);
                                        break;
                                }
                            }
                        }).show();
            }
        });

        settingItem1.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {      //注册人脸信息点击
            @Override
            public void click(boolean isChecked) {
                Intent intent = new Intent(getContext(), FaceRegistActivity.class);         //启动判断人脸的Activity，在里面注册人脸
                getContext().startActivity(intent);
            }
        });

        settingItem2.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {       //设置用户信息按钮
            @Override
            public void click(boolean isChecked) {
                Intent intent = new Intent(getActivity(), SettingUserActivity.class);
                startActivity(intent);
            }
        });

        settingItem3.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {             //第三个按钮点击
            @Override
            public void click(boolean isChecked) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {            //退出登录事件
            @Override
            public void onClick(View view) {
                //删除登录信息
                SharedPrefUtil.removeParam(getActivity(), SharedPrefUtil.LOGIN_DATA);
                SharedPrefUtil.removeParam(getActivity(), SharedPrefUtil.IS_LOGIN);
                //跳转到登录界面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {            //获取完图片后显示出来
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_PHOTO:
                handleImageOnKitKat(data);
                break;
            case TAKE_CAMERA:
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                    circleImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        username.setText(SharedPrefUtil.getUserName(getActivity()));         //获取登录的用户名
    }

    private void handleImageOnKitKat(Intent data) {               //从相册寻找图片显示
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getContext(), uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                //解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("conten://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //根据图片路径显示图片
        displayImage(imagePath);

    }

    private void displayImage(String imagePath) {
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            circleImageView.setImageBitmap(bitmap);
        }else{
            Toast.makeText(getContext(),"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri externalContentUri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(externalContentUri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;

    }
}