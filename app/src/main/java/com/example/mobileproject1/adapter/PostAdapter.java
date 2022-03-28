package com.example.mobileproject1.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject1.databinding.RecyclerRowBinding;
import com.example.mobileproject1.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private ArrayList<Post> postArrayList;
    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//araştır
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {
        String postId = postArrayList.get(position).id;
        String userEmail = holder.firebaseAuth.getCurrentUser().getEmail();

        //verileri setleme
        holder.recyclerRowBinding.recyclerViewUserEmailText.setText(postArrayList.get(position).email);
        holder.recyclerRowBinding.recyclerViewCommentText.setText(postArrayList.get(position).comment);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.recyclerViewImageView);
        holder.recyclerRowBinding.recyclerViewNumberOfFav.setText(postArrayList.get(position).numberOfFav);
        /////////////////////////////////////////////////////

        //fav butona basma
        holder.recyclerRowBinding.recyclerViewFavButton.setOnClickListener(v -> {

            //doküman adı bu pozisyondaki id olan veriyi çekme
            holder.firebaseFirestore.collection("Posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        //gelen verinin değerini alma
                        String fav = (String) task.getResult().getData().get("numberOfFav");

                        //değeri 1 artırma
                        int newFav = Integer.parseInt(fav) + 1;

                        //favorileme data oluşumu
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("Fav", true);

                        //fav koleksiyonunu çekme
                        holder.firebaseFirestore.collection("Fav"+userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    boolean isFav = false;
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                                        //gelen verinin id si
                                        String id = queryDocumentSnapshot.getId();
                                        if (id.equals(postId)){//eğer gelen verinin id si ile pozisyondaki id aynı ise
                                            Toast.makeText(v.getContext(), "You already like it.", Toast.LENGTH_SHORT).show();
                                            isFav = true; //demek ki favorilenmiş
                                            break;
                                        }
                                    }
                                    if (!isFav){ //favorilenmemişse yeni bir fav kolesiyonu oluştur.
                                        holder.firebaseFirestore.collection("Fav" + userEmail).document(postId).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    //numberoffavı arttırma
                                                    holder.firebaseFirestore.collection("Posts").document(postId).update("numberOfFav", String.valueOf(newFav));
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            });
        });
        //////////////////////////
    }

    @Override
    public int getItemCount() {
        //adapter kaç defa dönecek belirlenmesi
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        private RecyclerRowBinding recyclerRowBinding;
        private FirebaseFirestore firebaseFirestore;
        private FirebaseAuth firebaseAuth;

        //recyclerview idlerinin çekilmesi

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
        }
    }
}
