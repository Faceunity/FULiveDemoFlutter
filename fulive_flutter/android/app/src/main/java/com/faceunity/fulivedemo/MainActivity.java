package com.faceunity.fulivedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fulive_plugin.entity.FuEvent;
import com.example.fulive_plugin.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import io.flutter.embedding.android.FlutterFragment;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PHOTO = 1000;
    private static final int REQUEST_CODE_VIDEO = 1001;
    private static final String IMAGE_FORMAT_JPG = ".jpg";
    private static final String IMAGE_FORMAT_JPEG = ".jpeg";
    private static final String IMAGE_FORMAT_PNG = ".png";
    private static final String TAG_FLUTTER_FRAGMENT = "flutter";
    private FlutterFragment flutterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the Activity's FragmentManager to add a new
        // FlutterFragment, or find an existing one.
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Attempt to find an existing FlutterFragment,
        // in case this is not the first time that onCreate() was run.
        flutterFragment = (FlutterFragment) fragmentManager
                .findFragmentByTag(TAG_FLUTTER_FRAGMENT);

        // Create and attach a FlutterFragment if one does not exist.
        if (flutterFragment == null) {
            flutterFragment = FlutterFragment.createDefault();

            fragmentManager
                    .beginTransaction()
                    .add(
                            R.id.fl_flutter_container,
                            flutterFragment,
                            TAG_FLUTTER_FRAGMENT
                    )
                    .commit();
        }

        checkSelfPermission();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(new FuEvent(FuEvent.activity_resume));
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(new FuEvent(FuEvent.activity_pause));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        GeneratedPluginRegistrant.registerWith(flutterFragment.getFlutterEngine());
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        flutterFragment.onPostResume();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        flutterFragment.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        flutterFragment.onBackPressed();
    }

    @Override
    public void onUserLeaveHint() {
        flutterFragment.onUserLeaveHint();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        flutterFragment.onTrimMemory(level);
    }

    @Subscribe
    public void onGetEvent(FuEvent event) {
        switch (event.getCode()) {
            case FuEvent.choose_photo: {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PHOTO);
                break;
            }
            case FuEvent.choose_video: {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_CODE_VIDEO);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) return;
        Uri uri = data.getData();
        String path = FileUtils.getFilePathByUri(this, uri);
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (!checkIsImage(path)) {
                Toast.makeText(MainActivity.this, "请选择正确的图片文件", Toast.LENGTH_SHORT).show();
                return;
            }
            EventBus.getDefault().post(new FuEvent(FuEvent.choose_result_photo, path));
        } else if (requestCode == REQUEST_CODE_VIDEO) {
            if (!checkIsVideo(path)) {
                Toast.makeText(MainActivity.this, "请选择正确的视频文件", Toast.LENGTH_SHORT).show();
                return;
            }
            EventBus.getDefault().post(new FuEvent(FuEvent.choose_result_video, path));
        }
    }

    /**
     * 校验文件是否是图片
     *
     * @param path String
     * @return Boolean
     */
    private Boolean checkIsImage(String path) {
        String name = new File(path).getName().toLowerCase();
        return (name.endsWith(IMAGE_FORMAT_PNG) || name.endsWith(IMAGE_FORMAT_JPG)
                || name.endsWith(IMAGE_FORMAT_JPEG));
    }

    /**
     * 校验文件是否是视频
     *
     * @param path String
     * @return Boolean
     */
    private Boolean checkIsVideo(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, Uri.fromFile(new File(path)));
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            return "yes".equals(hasVideo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //begin check permission

    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private void checkSelfPermission() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 10001);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        flutterFragment.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );
    }
    //end check permission
}