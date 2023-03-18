package com.example.meme;

public interface OnFetchDataListener {

   void OnFetchData(String postLink,String subreddit,String title,String url,boolean nsfw
    ,boolean spoiler,String author,int ups,String[] arr,String message);

    void OnError(String message);

}
