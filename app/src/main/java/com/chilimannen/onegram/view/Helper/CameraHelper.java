package com.chilimannen.onegram.view.Helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.chilimannen.onegram.BuildConfig;
import com.chilimannen.onegram.view.Exception.CameraFailureException;
import com.chilimannen.onegram.view.Exception.NoPictureTakenException;
import com.chilimannen.onegram.view.Exception.FileFailureException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * "Taking photos simply", Android documentation.
 * https://developer.android.com/training/camera/photobasics.html
 */

public class CameraHelper {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private Activity activity;
    private File image;


    public CameraHelper(Activity activity) {
        this.activity = activity;

        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.CAMERA",
                "android.media.action.IMAGE_CAPTURE"
        };
        int requestCode = 200;
        activity.requestPermissions(permissions, requestCode);
    }


    private void createImageFile() throws FileFailureException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = activity.getCacheDir();
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            throw new FileFailureException(e);
        }
    }

    public Intent makeIntent() throws CameraFailureException, FileFailureException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        createImageFile();

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(activity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            image));
        } else
            throw new CameraFailureException();

        return intent;
    }


    private Bitmap getBitmap() throws FileFailureException {
        File file = new File(image.getAbsolutePath());

        if (file.exists()) {
            return ScaledBitmapReader.readFile(file);
        } else {
            throw new FileFailureException();
        }
    }

    public Bitmap onCameraResult(int requestCode, int resultCode, Intent data) throws NoPictureTakenException, FileFailureException {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            mediaScan();
            return getBitmap();
        }

        throw new NoPictureTakenException();
    }

    /**
     * Getting the rotation of an image capture on Android.
     * Author Jason Robinson, http://stackoverflow.com/a/14066265
     */
    public int getShotRotation() throws IOException {
        ExifInterface ei = new ExifInterface(image.getAbsolutePath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            default:
                return 0;
        }
    }

    /**
     * Issue a gallery scan to add the image to the users phone gallery.
     */
    private void mediaScan() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(FileProvider.getUriForFile(activity,
                BuildConfig.APPLICATION_ID + ".provider",
                image));
        activity.sendBroadcast(mediaScanIntent);
    }
}
