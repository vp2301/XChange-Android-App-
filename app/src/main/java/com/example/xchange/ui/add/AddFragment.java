package com.example.xchange.ui.add;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.example.xchange.Item;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.santalu.maskedittext.MaskEditText;


import com.example.xchange.Permission;
import com.example.xchange.helper.ConfigurationFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.example.xchange.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements View.OnClickListener {

    public AddFragment(){

    }

    private Spinner mState, mCategory;
    private ImageView mSelectImage;
    private EditText mPostTitle;
    private MaskEditText mPhone;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private StorageReference mStorage;
    private View view;
    private AlertDialog dialog;
    private  Item item;

    private List<String> listItemPhotos = new ArrayList<>();
    private List<String> listUrlPhotos = new ArrayList<>();


    private String[] permission = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add, container, false);

        //configurate initial
        mStorage = ConfigurationFirebase.getFirebaseStorage();

        //validate permission
        Permission.validatePermission(permission, getActivity(), 1);

        initializeComponent();
        loadingDataSpinner();
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneAux = "";
                item = configurateItem();
                if(mPhone.getRawText() != null){
                    phoneAux = mPhone.getRawText().toString();
                }

                if(listItemPhotos.size() != 0){
                    if(!item.getState().isEmpty()){
                        if(!item.getCategory().isEmpty()){
                            if(!item.getTitle().isEmpty()){
                                if(!item.getPhone().isEmpty() && phoneAux.length() >= 10){
                                    if(!item.getDescription().isEmpty()){

                                        saveItem();

                                    }else{
                                        showMessageError("Fill in the description field!");
                                    }
                                }else{
                                    showMessageError("Fill in the phone field, enter at least 10 numbers!");
                                }
                            }else{
                                showMessageError("Fill in the title field!");
                            }
                        }else{
                            showMessageError("Fill in the category field!");
                        }
                    }else{
                        showMessageError("Fill in the state field!");
                    }
                }else{
                    showMessageError("Select photo");
                }
            }
        });
        return view;
    }



    private void showMessageError(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {

            if(v.getId()== R.id.imageSelect)
                choiceImage(1);
            else
                ;


    }

    public void choiceImage(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //recovery image
            Uri imageChoice = data.getData();
            String pathImage = imageChoice.toString();

            //Configure image in ImageView
            if (requestCode == 1) {
                mSelectImage.setImageURI(imageChoice);}
                listItemPhotos.add(pathImage);

        }
    }

    private Item configurateItem(){
        String state = mState.getSelectedItem().toString();
        String category = mCategory.getSelectedItem().toString();
        String title = mPostTitle.getText().toString();
        String phone = mPhone.getText().toString();
        String description = mPostDesc.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailId = user.getEmail();


        Item item = new Item();
        item.setState(state);
        item.setCategory(category);
        item.setTitle(title);
        item.setPhone(phone);
        item.setDescription(description);
        item.setEmail(emailId);

        return item;
    }

    private void loadingDataSpinner(){
        //configurate spinner de state
        String[] estate = getResources().getStringArray(R.array.estate);
        ArrayAdapter<String> adapterState = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, estate);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState.setAdapter(adapterState);

        //configurate spinner de category
        String[] category = getResources().getStringArray(R.array.category);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_spinner_item, category);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapterCategory);
    }


    public void saveItem(){
        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Saving Item")
                .setCancelable(false)
                .build();
        dialog.show();
        //save image in storage
        for(int i=0; i < listItemPhotos.size(); i++){
            String urlImage = listItemPhotos.get(i);
            int sizeList = listItemPhotos.size();
            savePhotoStorage(urlImage, sizeList, i);
        }
    }

    private void savePhotoStorage(String urlString, final int totalPhotos, int cont){
        //Create nó in firebase
        final StorageReference imageItem= mStorage.child("images")
                .child("items")
                .child(item.getIdItem())
                .child("images"+cont);

        imageItem.putFile( Uri.parse(urlString)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri firebaseUri = taskSnapshot.getDownloadUrl();

                    String downloadUrl = firebaseUri.toString();
                    listUrlPhotos.add(downloadUrl);


                if(totalPhotos == listUrlPhotos.size()){
                    item.setPhoto(listUrlPhotos);
                    item.save();

                    dialog.dismiss();
                    onDestroy();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessageError("Fail to upload");
                Log.i("INFO", "Fail to upload: " + e.getMessage());
            }
        });
    }

    public void initializeComponent(){
        mSelectImage = (ImageView) view.findViewById(R.id.imageSelect);
        mPostTitle = (EditText)  view.findViewById(R.id.itemTitle);
        mPhone = (MaskEditText) view.findViewById(R.id.editPhone);
        mPostDesc = (EditText) view.findViewById(R.id.itemDescription);
        mState = view.findViewById(R.id.spinnerState);
        mCategory = view.findViewById(R.id.spinnerCategory);
        mSubmitBtn = (Button) view.findViewById(R.id.submitBtn);

        mSelectImage.setOnClickListener(this);


        Locale locale = new Locale("EN", "IN");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissionResult : grantResults){

            if (permissionResult == PackageManager.PERMISSION_DENIED){
                AlertValidationPermission();
            }
        }
    }

    private void AlertValidationPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permissions Denied");
        builder.setMessage("To use the app you must accept the permissions");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDestroy();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }






}
