package com.example.mobileproject1.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mobileproject1.R;
import com.example.mobileproject1.adapter.PostAdapter;
import com.example.mobileproject1.databinding.ActivityFeedBinding;
import com.example.mobileproject1.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        //dataların adaptere yollanması
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this)); //itemlerin ekranda nasıl görüneceği
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

    }

    private void getData() {

        //bütün verilerin çekilmesi
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    postArrayList.clear(); //veriler tekrarlanmasın diye önlem
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        //Casting
                        String userEmail = (String) data.get("useremail");

                        //burada postların üzerinde username gözükmesi için username çekiyoruz.
                        firebaseFirestore.collection("UserName").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null) {
                                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                        String id = documentSnapshot.getId();
                                        if (id.equals(userEmail)) {//postun sahibinin e-maili ile gelen usernamein sahibinin e-maili aynı ise

                                            //burda çekmemizin amacı...
                                            String postId = snapshot.getId();
                                            String username = (String) documentSnapshot.getData().get("username");
                                            String comment = (String) data.get("comment");
                                            String downloadUrl = (String) data.get("downloadurl");
                                            String numberOfFav = (String) data.get("numberOfFav");

                                            Post post = new Post(username, comment, downloadUrl,numberOfFav,postId);
                                            postArrayList.add(post);
                                        }
                                    }
                                    postAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post) {
            //Upload Activity
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId() == R.id.sign_out) {
            //Sign out
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            auth.signOut();
                            Intent intentToMain = new Intent(FeedActivity.this, SignIn.class);
                            startActivity(intentToMain);
                            finish();

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Hesabından çıkış yapmak istediğine emin misin?").setPositiveButton("Evet", dialogClickListener)
                    .setNegativeButton("Hayır", dialogClickListener).show();

        } else if (item.getItemId() == R.id.profile) {
            //Profile
            Intent intentToUpload = new Intent(FeedActivity.this, Profile.class);
            startActivity(intentToUpload);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finishAndRemoveTask();

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Uygulamayı kapatmak istediğine emin misin?").setPositiveButton("Evet", dialogClickListener)
                .setNegativeButton("Hayır", dialogClickListener).show();
    }
}