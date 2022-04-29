package spartons.com.imagecropper.utils;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class UploadUtil {
    public static boolean uploadFile(String path, String filename)
    {
        OkHttpClient okhttp = new OkHttpClient();
        File file = new File(path);

        //debug infomation
//        Log.d("Image Path= ", path);
//        Log.d("Image name= ", filename);
        if(path.isEmpty() || !file.exists())
            return false;
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",filename,RequestBody.create(new File(path), MediaType.parse("image/jpeg")))
                .addFormDataPart("filename",filename)
                .build();
        FutureTask<Boolean> task = new FutureTask<>(()->
        {
            try
            {
                ResponseBody responseBody = okhttp.newCall(
                        new Request.Builder().post(body).url("http://192.168.50.219:8080/upload").build()
                ).execute().body();

                if(responseBody != null)
                    return Boolean.parseBoolean(responseBody.string());
                return false;
            }
            catch (IOException e)
            {
                return false;
            }
        });
        try
        {
            new Thread(task).start();
            return task.get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
