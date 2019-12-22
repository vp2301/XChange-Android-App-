package com.example.xchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class ItemActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView state;
    private TextView emailid;
    private CarouselView carouselView;

    private Item itemSelect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        initializeComponents();

        itemSelect = (Item) getIntent().getSerializableExtra("itemSelected");
        if (itemSelect != null) {
            title.setText(itemSelect.getTitle());
            description.setText(itemSelect.getDescription());
            state.setText(itemSelect.getState());
            emailid.setText(itemSelect.getEmail());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = itemSelect.getPhoto().get(position);
                    Picasso.get().load(urlString).into(imageView); //load image
                }
            };


            carouselView.setPageCount(itemSelect.getPhoto().size());
            carouselView.setImageListener(imageListener);



        }
    }

    public void visualizePhone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemSelect.getPhone(), null));
        startActivity(i);
    }
    private void initializeComponents(){

        title = (TextView) findViewById(R.id.text_title);
        carouselView = findViewById(R.id.carouselView) ;
        state = (TextView)findViewById(R.id.text_StateDetail);
        description = (TextView)findViewById(R.id.text_description);
        emailid = findViewById(R.id.text_user);


    }


}
