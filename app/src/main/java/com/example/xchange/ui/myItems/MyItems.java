package com.example.xchange.ui.myItems;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

import com.example.xchange.Item;
import com.example.xchange.R;
import com.example.xchange.RecyclerViewAdapter;
import com.example.xchange.helper.ConfigurationFirebase;
import com.example.xchange.helper.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyItems extends Fragment {

    public MyItems(){

    }

    private RecyclerView myrv;
    private List<Item> items = new ArrayList<>();
    private RecyclerViewAdapter myAdaptor;
    private DatabaseReference mDatabase;
    private AlertDialog dialog;
    private static View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myitems, container, false);

        ConfigurationFirebase.getFirebaseAuthentication();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("my items").child(ConfigurationFirebase.getIdUser());


        //Configurations initial
       /* mDatabase = ConfigurationFirebase.getFirebase()
                .child("my items")
                .child(ConfigurationFirebase.getIdUser());*/

        myrv = view.findViewById(R.id.recyclerItem);


        //Configurate ReyclerView
        myrv.setLayoutManager(new GridLayoutManager(getActivity(),1));
        myrv.setHasFixedSize(true);
        //Configure Adapter
        myAdaptor = new RecyclerViewAdapter(getActivity(),items);
        myrv.setAdapter(myAdaptor);

        //recovery advertisement for user
        recoveryItems();

        //add event of click in recyclerview
        myrv.addOnItemTouchListener(

                new RecyclerItemClickListener(
                        getActivity(), myrv,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Item itemSelected = items.get(position);
                                itemSelected.remove();

                                myAdaptor.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )

        );
        return view;
    }

    private void recoveryItems() {

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Retrieving Items")
                .setCancelable(false)
                .build();
        dialog.show();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                items.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    items.add(ds.getValue(Item.class));
                }

                Collections.reverse(items);
                myAdaptor.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}