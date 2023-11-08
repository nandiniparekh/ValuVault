package com.example.cmput301groupproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmput301groupproject.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    Button multiselect;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    RecyclerAdapter adapter;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    //private static final int REQUEST_CODE = 22;
    //Button buttonTakePic;
    //ImageView imageView;
    //ActivityResultLauncher<Intent> activityResultLauncher;

    private static final int Read_Permissions = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.photos);
        recyclerView = findViewById(R.id.recyclerView_Gallery_Images);
        multiselect = findViewById(R.id.multiselect);

        adapter = new RecyclerAdapter(uriArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        recyclerView.setAdapter(adapter);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            !=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permissions);
        }

        multiselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && requestCode == Activity.RESULT_OK){
            if(data.getClipData() != null){
                int x = data.getClipData().getItemCount();

                for(int i=0; i<x; i++){
                    uriArrayList.add(data.getClipData().getItemAt(i).getUri());
                }
                adapter.notifyDataSetChanged();
                textView.setText("Photos ("+uriArrayList.size()+")");

            }else if(data.getData() != null){
                String imageURL = data.getData().getPath();
                uriArrayList.add(Uri.parse(imageURL));
            }
        }
    }
}


