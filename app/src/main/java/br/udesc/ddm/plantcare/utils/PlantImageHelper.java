package br.udesc.ddm.plantcare.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.udesc.ddm.plantcare.R;

public class PlantImageHelper {

    public static void loadPlantImage(ImageView imageView, String imageKey, String customPhotoPath) {
        if (customPhotoPath != null && !customPhotoPath.trim().isEmpty()) {
            File file = new File(customPhotoPath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return;
                }
            }
        }
        imageView.setImageResource(getDrawableForKey(imageKey));
    }

    public static int getDrawableForKey(String imageKey) {
        if (imageKey == null) return R.drawable.img_plant_monstera;
        switch (imageKey.toLowerCase()) {
            case "zamioculca":
                return R.drawable.img_plant_zamioculca;
            case "samambaia":
                return R.drawable.img_plant_samambaia;
            case "cacto":
                return R.drawable.img_plant_cacto;
            case "jiboia":
                return R.drawable.img_plant_jiboia;
            case "monstera":
            default:
                return R.drawable.img_plant_monstera;
        }
    }

    public static String saveCameraBitmapToInternalStorage(Context context, Bitmap bitmap) {
        if (bitmap == null) return "";
        File dir = new File(context.getFilesDir(), "plant_photos");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "plant_" + System.currentTimeMillis() + ".jpg";
        File file = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

