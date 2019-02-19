package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageProcessing {
    public static Bitmap base64ToBitmap(String image){
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }


    public static String bitmapToBase64(Bitmap bitmap){
        int maxPixelImage = 1000;
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        if(bitmap.getWidth() > bitmap.getHeight()){
            if(bitmap.getWidth() < maxPixelImage)
                maxPixelImage = bitmap.getWidth();
            float percent = bitmap.getHeight() / Float.valueOf(bitmap.getWidth());
            bitmap = Bitmap.createScaledBitmap(bitmap, maxPixelImage, Math.round(maxPixelImage * percent), false);
        }else{
            if(bitmap.getHeight() < maxPixelImage)
                maxPixelImage = bitmap.getHeight();
            float percent = bitmap.getWidth() / Float.valueOf(bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap,Math.round(maxPixelImage * percent), maxPixelImage, false);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,      80, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
