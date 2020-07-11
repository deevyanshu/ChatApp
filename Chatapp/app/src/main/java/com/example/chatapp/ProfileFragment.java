package com.example.chatapp;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
FirebaseAuth firebaseAuth;
FirebaseUser user;
StorageReference storageReference;
String storagepath="User_profil_imgs/";
ImageView avatarTv;
TextView nameTv,emailTv;
FirebaseDatabase firebaseDatabase;
DatabaseReference databaseReference;
FloatingActionButton fab;
ProgressDialog progressDialog;
Uri imageuri;
String profile;
private static  final int CAMERA_REQUEST_CODE=100;
    private static  final int STORAGE_REQUEST_CODE=200;
    private static  final int IMAGE_PICK_REQUEST_CODE=300;
    private static  final int IMAGE_PICK_CAMERA_REQUEST_CODE=400;

    String cameraPermission[];
    String storagePermission[];

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("users");
        storageReference= getInstance().getReference();

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

avatarTv=view.findViewById(R.id.avatarIv);
        nameTv=view.findViewById(R.id.nameTv);
        emailTv=view.findViewById(R.id.emailTv);
fab=view.findViewById(R.id.fab);
progressDialog=new ProgressDialog(getActivity());

        Query query=databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String name=""+ds.child("name").getValue();
                    String email=""+ds.child("email").getValue();

                    String image=""+ds.child("image").getValue();

                    nameTv.setText(name);
                    emailTv.setText(email);


                    try
                    {
                        Picasso.get().load(image).into(avatarTv);
                    }catch(Exception e)
                    {
                        Picasso.get().load(R.drawable.ic_add_image).into(avatarTv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showeditprofile();
            }
        });
        return view;
    }

    private  boolean checkstorage()
    {
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requeststorage()
    {
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
    }


    private  boolean checkCamera()
    {
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCamera()
    {
        requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
    }



    private void showeditprofile() {
        String options[]={"Edit profile picture","Edit name"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    progressDialog.setMessage("Updating profile picture");
                    profile="image";
                    showimagedialod();
                }
                else
                {
                   progressDialog.setMessage("Updating Name");
                   shownameupdate("name");
                }
            }
        });
        builder.create().show();
    }

    private void shownameupdate(final String name) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("update "+name);
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setPadding(10,10,10,10);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText editText=new EditText(getActivity());
        editText.setHint("Enter "+name);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value=editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value))
                {
                    progressDialog.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(name,value);
                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"updated",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(),"Enter"+name,Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showimagedialod() {
        String options[]={"camera","gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("Pick image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    if(!checkCamera())
                    {
                        requestCamera();
                    }
                    else
                    {
                        pickformcamera();
                    }
                }
                else
                {
                   if(!checkstorage())
                   {
                       requeststorage();
                   }else
                   {
                       pickformgallery();
                   }
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:{
            if(grantResults.length>0)
            {
                boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean writestorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted && writestorageAccepted)
                {
                    pickformcamera();
                }
                else
                {
                    Toast.makeText(getActivity(),"please enable camera and storage permission",Toast.LENGTH_SHORT).show();
                }
            }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0)
                {

                    boolean writestorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if( writestorageAccepted)
                    {
                        pickformgallery();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"please enable storage permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            if(requestCode==IMAGE_PICK_REQUEST_CODE)
            {
              imageuri=data.getData();
              uploadprofilecover(imageuri);
            }
            if(requestCode==IMAGE_PICK_CAMERA_REQUEST_CODE)
            {
                uploadprofilecover(imageuri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadprofilecover(Uri imageuri) {
        progressDialog.show();
        String filepathandname=storagepath+""+profile+"_"+user.getUid();
        StorageReference storageReference2=storageReference.child(filepathandname);
        storageReference2.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloaduri=uriTask.getResult();

                if(uriTask.isSuccessful())
                {
                    HashMap<String,Object> results=new HashMap<>();

                    results.put(profile,downloaduri.toString());
                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Image updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickformcamera()
{
    ContentValues values=new ContentValues();
    values.put(MediaStore.Images.Media.TITLE,"Temp pic");
    values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
    imageuri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

    Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
    startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);
}
    private void pickformgallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_REQUEST_CODE);
    }
}
