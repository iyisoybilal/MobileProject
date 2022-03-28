package com.example.mobileproject1.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mobileproject1.R;
import com.example.mobileproject1.adapter.PostAdapter;
import com.example.mobileproject1.databinding.ActivityFeedBinding;
import com.example.mobileproject1.databinding.ActivityMainBinding;
import com.example.mobileproject1.databinding.ActivityProfileBinding;
import com.example.mobileproject1.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String userEmail;
    private ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        binding= ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();

        userEmail= auth.getCurrentUser().getEmail();

        getData();

        //profile username yazdırmak için data çekme
        firebaseFirestore.collection("UserName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot querySnapshot: task.getResult()){
                        String id = querySnapshot.getId();
                        if (id.equals(userEmail)){
                            String username = (String) querySnapshot.getData().get("username");
                            binding.profileUsername.setText(username);
                        }
                    }
                }
            }
        });

        //profile post sayısı yazdırmak için data çekme
        firebaseFirestore.collection("Posts").whereEqualTo("useremail",userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int i = 0;
                    for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        i+=1;
                    }
                    binding.profilePost.setText(String.valueOf(i));
                }
            }
        });

        binding.profileRecycler.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.profileRecycler.setAdapter(postAdapter);
    }
    private  void  getData(){

        //feed ile farkı sadece kendi postlarımızı çekme.
        firebaseFirestore.collection("Posts").whereEqualTo("useremail",userEmail).orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    for(DocumentSnapshot snapshot: value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();
                        //Casting
                        String userEmail =(String) data.get("useremail");

                        firebaseFirestore.collection("UserName").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value !=null){
                                    for (DocumentSnapshot documentSnapshot: value.getDocuments()){
                                        String id = documentSnapshot.getId();
                                        if (id.equals(userEmail)){
                                            String postId = snapshot.getId();
                                            String username = (String) documentSnapshot.getData().get("username");
                                            String comment = (String) data.get("comment");
                                            String downloadUrl = (String) data.get("downloadurl");
                                            String numberOfFav = (String) data.get("numberOfFav");
                                            Post post = new Post(username,comment,downloadUrl,numberOfFav,postId);
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
}