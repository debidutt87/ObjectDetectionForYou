package com.ody.di.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.exifinterface.media.ExifInterface;

/**
 * Provides utility methods for various image manipulations.
 *
 * @author Debidutt Prasad
 */
public class ImageUtils {

    /**
     * Rotates the provided bitmap according to the EXIF orientation data.
     *
     * @param input           the original bitmap.
     * @param imageUri        the URI of the image.
     * @param contentResolver a ContentResolver instance to retrieve EXIF data.
     * @return a new rotated bitmap or the original bitmap if no rotation is needed.
     */
    public static Bitmap rotateBitmap(Bitmap input, Uri imageUri, ContentResolver contentResolver) {
        int orientation = getExifOrientation(imageUri, contentResolver);

        Matrix rotationMatrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                rotationMatrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationMatrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotationMatrix.setRotate(180);
                rotationMatrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotationMatrix.setRotate(90);
                rotationMatrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationMatrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                rotationMatrix.setRotate(-90);
                rotationMatrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationMatrix.setRotate(-90);
                break;
            default:
                return input;
        }

        try {
            Bitmap orientedBitmap = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), rotationMatrix, true);
            input.recycle();
            return orientedBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return input;
        }
    }

    /**
     * Converts URI to a Bitmap.
     *
     * @param context         the context for retrieving the ContentResolver.
     * @param selectedFileUri the URI of the image.
     * @return a Bitmap from the provided URI or null if conversion fails.
     */
    public static Bitmap uriToBitmap(Context context, Uri selectedFileUri) {
        try (ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(selectedFileUri, "r")) {
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            return BitmapFactory.decodeFileDescriptor(fileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getExifOrientation(Uri imageUri, ContentResolver contentResolver) {
        try (InputStream in = contentResolver.openInputStream(imageUri)) {
            if (in != null) {
                ExifInterface exifInterface = new ExifInterface(in);
                return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            }
        } catch (IOException e) {
            Log.e("tryOrientation", "Cannot get EXIF data: ", e);
        }
        return ExifInterface.ORIENTATION_UNDEFINED;
    }

    /**
     * Rotates the provided bitmap according to the orientation data from MediaStore.
     *
     * @param context  the context for retrieving the ContentResolver.
     * @param input    the original bitmap.
     * @param imageUri the URI of the image.
     * @return a new rotated bitmap.
     */
    public static Bitmap rotateBitmap(Context context, Bitmap input, Uri imageUri) {
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        try (Cursor cur = context.getContentResolver().query(imageUri, orientationColumn, null, null, null)) {
            int orientation = -1;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            }
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.setRotate(orientation);
            return Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        }
    }

    /**
     * Saves the provided bitmap as a PNG file to the given output file.
     *
     * @param bitmap     the bitmap to save.
     * @param outputFile the file to save the bitmap to.
     */
    public static void saveBitmapAsPNG(Bitmap bitmap, File outputFile) {
        try (FileOutputStream outStream = new FileOutputStream(outputFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
