package com.example.android.courtcounter.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.android.courtcounter.adaptors.UsersHistoryListAdaptor;
import com.example.android.courtcounter.model.HistoryDetails;
import com.example.android.courtcounter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.courtcounter.utils.Constants.INTENT_KEY_SELECTED_SPORT;

public class HistoryDetailsActivity extends AppCompatActivity {

    String TAG = "History";
    private FirebaseAuth mAuth;
    String selectedSport;
    private RecyclerView mHistoryList;
    private UsersHistoryListAdaptor usersHistoryListAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        mAuth = FirebaseAuth.getInstance();
        mHistoryList = findViewById(R.id.history_list);
        final Activity activity = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.setTitle("History Details");


        mHistoryList.setHasFixedSize(true);
        mHistoryList.setLayoutManager(new LinearLayoutManager(this));
        usersHistoryListAdaptor = new UsersHistoryListAdaptor(new ArrayList<HistoryDetails>());
        mHistoryList.setAdapter(usersHistoryListAdaptor);


        // [START get_multiple_all]
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String email = mAuth.getCurrentUser().getEmail();
        selectedSport = getIntent().getStringExtra(INTENT_KEY_SELECTED_SPORT);

        db.collection("users").document(email).collection(selectedSport)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<HistoryDetails> details = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HistoryDetails model = document.toObject(HistoryDetails.class);
                                details.add(model);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            usersHistoryListAdaptor.updateList(details);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

