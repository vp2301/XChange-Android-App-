package com.example.xchange.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

import com.example.xchange.Item;
import com.example.xchange.ItemActivity;
import com.example.xchange.R;
import com.example.xchange.RecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.xchange.helper.RecyclerItemClickListener;
import com.example.xchange.helper.ConfigurationFirebase;


public class HomeFragment extends Fragment  {

    public HomeFragment(){

    }

    private DatabaseReference mDatabaseRef;
    private static View view;
    private ProgressBar mProgress;
    private RecyclerViewAdapter myAdapter;
    private Button mRegion,mCategory;
    private RecyclerView myrv;
    private List<Item> listItems = new ArrayList<>();
    private FirebaseAuth auth;
    private AlertDialog dialog;
    private String filterState = "";
    private String filterCategory = "";
    private boolean filteringForState = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        //authentication
        ConfigurationFirebase.getFirebaseAuthentication();
        //initial components
        initializeComponents();
        //database reference
        ConfigurationFirebase.getFirebaseAuthentication();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("items");
        //recyclerview
        myrv.setLayoutManager(new LinearLayoutManager(getContext()));
        myrv.setHasFixedSize(true);
        //adapter
        myAdapter = new RecyclerViewAdapter(getActivity(), listItems);
        myrv.setAdapter(myAdapter);

        //retrieving items
        recoveryItems();

        myrv.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), myrv, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Item itemSelect = listItems.get(position);
                        Intent i = new Intent(getActivity(), ItemActivity.class);
                        i.putExtra("itemSelected", itemSelect);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                ));
        //setting listener to region
        mRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogState = new AlertDialog.Builder(getActivity());
                dialogState.setTitle("Select Region");

                //configurate spinner
                View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

                //configurate spinner of state
                final Spinner spinnerState = viewSpinner.findViewById(R.id.spinnerFilter);
                String[] estate = getResources().getStringArray(R.array.estate);
                ArrayAdapter<String> adapterState = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                        estate
                );
                adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerState.setAdapter(adapterState);

                dialogState.setView(viewSpinner);

                dialogState.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filterState = spinnerState.getSelectedItem().toString();
                        Log.d("filter", "filter: " + filterState);
                        recoveryItemForState();
                        filteringForState = true;
                    }
                });
                dialogState.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = dialogState.create();
                dialog.show();

            }
        });
        //setting listener to category
        mCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filteringForState == true){

                    AlertDialog.Builder dialogCategory = new AlertDialog.Builder(getActivity());
                    dialogCategory.setTitle("Select desired Category");

                    //configurate spinner
                    View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

                    //configurate spinner of category
                    final Spinner spinnerCategory = viewSpinner.findViewById(R.id.spinnerFilter);
                    String[] category = getResources().getStringArray(R.array.category);
                    ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(
                            getActivity(),android.R.layout.simple_spinner_item,
                            category
                    );

                    adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapterCategory);

                    dialogCategory.setView(viewSpinner);

                    dialogCategory.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            filterCategory = spinnerCategory.getSelectedItem().toString();
                            Log.d("filter", "filter: " + filterState);
                            recoveryItemForCategory();
                        }
                    });
                    dialogCategory.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = dialogCategory.create();
                    dialog.show();
                }else{
                    Toast.makeText(getActivity(), "Choose a region first!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }




    private void recoveryItemForState(){

        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Retrieving Items")
                .setCancelable(false)
                .build();
        dialog.show();

        //Configurate no for state
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("items")
                .child(filterState);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for(DataSnapshot category: dataSnapshot.getChildren()){
                    for(DataSnapshot items: category.getChildren()){
                        Item item = items.getValue(Item.class);
                        listItems.add(item);
                    }
                }

                Collections.reverse(listItems);
                myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recoveryItemForCategory(){
        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Retrieving Items")
                .setCancelable(false)
                .build();
        dialog.show();

        //Configurate no for state
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("upload")
                .child(filterState)
                .child(filterCategory);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for(DataSnapshot items: dataSnapshot.getChildren()) {
                    Item item = items.getValue(Item.class);
                    listItems.add(item);
                }

                Collections.reverse(listItems);
                myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initializeComponents() {
        myrv = view.findViewById(R.id.text_home);
        mRegion = view.findViewById(R.id.buttonRegion);
        mCategory = view.findViewById(R.id.buttonCategory);
    }

    private void recoveryItems(){
        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Retrieving Items")
                .setCancelable(false)
                .build();
        dialog.show();

        listItems.clear();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot states : dataSnapshot.getChildren()) {
                    for (DataSnapshot category : states.getChildren()) {
                        for (DataSnapshot items : category.getChildren()) {
                            Item item = items.getValue(Item.class);
                            listItems.add(item);
                        }
                    }
                }
                Collections.reverse(listItems); //order list items
                myAdapter.notifyDataSetChanged();
                dialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    }
