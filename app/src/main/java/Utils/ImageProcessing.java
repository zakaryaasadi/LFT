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
        byte [] b = resizeToBytesArray(bitmap, maxPixelImage);
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }



    public static Bitmap resizeToBitmap(Bitmap b, int maxPixelImage){
        if(b.getWidth() > b.getHeight()){
            if(b.getWidth() < maxPixelImage)
                maxPixelImage = b.getWidth();
            float percent = b.getHeight() / Float.valueOf(b.getWidth());
            b = Bitmap.createScaledBitmap(b, maxPixelImage, Math.round(maxPixelImage * percent), false);
        }else{
            if(b.getHeight() < maxPixelImage)
                maxPixelImage = b.getHeight();
            float percent = b.getWidth() / Float.valueOf(b.getHeight());
            b = Bitmap.createScaledBitmap(b,Math.round(maxPixelImage * percent), maxPixelImage, false);
        }
        return b;
    }


    public static byte[] resizeToBytesArray(Bitmap b, int maxPixelImage){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        b = resizeToBitmap(b, maxPixelImage);
        b.compress(Bitmap.CompressFormat.JPEG,      80, baos);
        return baos.toByteArray();
    }
}
