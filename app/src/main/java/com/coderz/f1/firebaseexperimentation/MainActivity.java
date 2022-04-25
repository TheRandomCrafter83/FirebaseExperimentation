package com.coderz.f1.firebaseexperimentation;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.coderz.f1.firebaseexperimentation.firebase.FileUploader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    Button buttonLoad;
    Button buttonUpload;
    ImageView imageView;
    ProgressBar progressBar;
    TextView textviewError;

    Uri loadedImageUri = null;

    private final int ERROR_COLOR = Color.RED;
    private final int SUCCESS_COLOR = Color.GREEN;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Uploads");

    ActivityResultLauncher<String> loadFileResult = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            loadedImageUri = result;
            imageView.setImageURI(loadedImageUri);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
    }

    private void initializeViews() {
        buttonLoad = findViewById(R.id.button_load);
        buttonUpload = findViewById(R.id.button_upload);
        imageView = findViewById(R.id.image_loaded);
        progressBar = findViewById(R.id.progressBar);
        textviewError = findViewById(R.id.textview_error);
        buttonLoad.setOnClickListener(buttonLoadListener);
        buttonUpload.setOnClickListener(buttonUploadListener);

    }

    private final View.OnClickListener buttonLoadListener = view -> loadFileResult.launch("image/*");

    private final View.OnClickListener buttonUploadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(loadedImageUri == null){
                textviewError.setVisibility(View.VISIBLE);
                textviewError.setText(getString(R.string.image_not_set));
                textviewError.setTextColor(ERROR_COLOR);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            textviewError.setText("");
            FileUploader uploader = new FileUploader();
            uploader.setFileUploadListener(new FileUploader.FileUploaderListener() {
                @Override
                public void onSuccess(String downloadUrl) {
                    progressBar.setVisibility(View.GONE);
                    textviewError.setText(downloadUrl);
                    textviewError.setTextColor(SUCCESS_COLOR);
                }

                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    textviewError.setText(e.toString());
                    textviewError.setTextColor(ERROR_COLOR);
                }

                @Override
                public void onProgress(double value) {
                    progressBar.setProgress((int)value);
                }
            });
            uploader.uploadFile(view.getContext(),loadedImageUri,storageReference);
        }
    };
}