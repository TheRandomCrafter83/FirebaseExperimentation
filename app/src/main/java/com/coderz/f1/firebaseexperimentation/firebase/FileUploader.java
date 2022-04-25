package com.coderz.f1.firebaseexperimentation.firebase;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FileUploader {
    public interface FileUploaderListener{
        void onSuccess(String downloadUrl);
        void onFailure(@NonNull Exception e);
        void onProgress(double value);
    }

    private FileUploaderListener listener;

    private String getFileExtension(Context context, Uri uri) {  // get the file extension of the image
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void setFileUploadListener(FileUploaderListener listener){
        this.listener = listener;
    }

    public void uploadFile(@NonNull Context context,@NonNull Uri fileUri,@NonNull StorageReference storageReference){
        StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(context,fileUri));
        ref.putFile(fileUri)
            .addOnSuccessListener(taskSnapshot -> {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> listener.onProgress(0),0);
                listener.onSuccess(ref.getDownloadUrl().toString());
            })
            .addOnProgressListener(snapshot -> {
                double progress = 100d * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> listener.onProgress(progress),0);
            })
        .addOnFailureListener(e -> {
            listener.onFailure(e);
        })
        ;
    }
}
