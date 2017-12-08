package com.example.supuni.myfoodapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddFoodActivity extends AppCompatActivity {

    private ImageButton foodImage;
    private static final int GALLREQ =1;
    private EditText name,desc,price;
    private Uri uri =null;
    private StorageReference storageReference =null;
    private DatabaseReference mref;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        name =(EditText)findViewById(R.id.itemName);//set the gui to name variable
        desc =(EditText)findViewById(R.id.itemDes);
        price =(EditText)findViewById(R.id.itemPrice);
        storageReference = FirebaseStorage.getInstance().getReference();//set a folder as Item in second line
        mref= FirebaseDatabase.getInstance().getReference("Item");//add the DatabaseReference where it uses

    }

    public void imageButtonClicked(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLREQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLREQ && resultCode==RESULT_OK){
            uri=data.getData();
            foodImage=(ImageButton)findViewById(R.id.foodImageButton);
            foodImage.setImageURI(uri);
        }
    }

    public void  addItemButtonClicked(View view){
         final String name_text =name.getText().toString().trim();
         final String desc_text =desc.getText().toString().trim();
         final String price_text =price.getText().toString().trim();

        if(!TextUtils.isEmpty(name_text) && !TextUtils.isEmpty(desc_text) && !TextUtils.isEmpty(price_text)){

            StorageReference filepath =storageReference.child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadurl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(AddFoodActivity.this,"Image upladed",Toast.LENGTH_LONG).show();
                    final DatabaseReference newPost = mref.push();
                    newPost.child("name").setValue(name_text);
                    newPost.child("desc").setValue(desc_text);
                    newPost.child("price").setValue(price_text);
                    newPost.child("image").setValue(downloadurl.toString());
                }
            });
        }
    }
}
