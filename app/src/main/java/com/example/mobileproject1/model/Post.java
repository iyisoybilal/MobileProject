package com.example.mobileproject1.model;

public class Post {
  public   String email;
   public String comment;
   public String downloadUrl;
   public String numberOfFav;
   public String id;

    public Post(String email,String comment,String downloadUrl,String numberOfFav,String id){
        this.email= email;
        this.comment=comment;
        this.downloadUrl=downloadUrl;
        this.numberOfFav = numberOfFav;
        this.id=id;
    }

}
