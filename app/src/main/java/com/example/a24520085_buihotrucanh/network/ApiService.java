package com.example.a24520085_buihotrucanh.network;

import com.example.a24520085_buihotrucanh.network.models.CreatePostRequest;
import com.example.a24520085_buihotrucanh.network.models.DeleteResponse;
import com.example.a24520085_buihotrucanh.network.models.FriendsResponse;
import com.example.a24520085_buihotrucanh.network.models.LoginRequest;
import com.example.a24520085_buihotrucanh.network.models.LoginResponse;
import com.example.a24520085_buihotrucanh.network.models.PostResponse;
import com.example.a24520085_buihotrucanh.network.models.PostsResponse;
import com.example.a24520085_buihotrucanh.network.models.ProfileResponse;
import com.example.a24520085_buihotrucanh.network.models.RegisterRequest;
import com.example.a24520085_buihotrucanh.network.models.RegisterResponse;
import com.example.a24520085_buihotrucanh.network.models.UpdateProfileRequest;
import com.example.a24520085_buihotrucanh.network.models.UpdateProfileResponse;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("api/register")
    Call<RegisterResponse> register(@Body RegisterRequest body);

    @GET("api/users/emails")
    Call<JsonElement> getAllUserEmails();

    @GET("api/users/{user_id}/friends")
    Call<FriendsResponse> getFriends(@Path("user_id") String userId);

    @GET("api/users/{user_id}/profile")
    Call<ProfileResponse> getProfile(@Path("user_id") String userId);

    @PATCH("api/users/{user_id}/profile")
    Call<UpdateProfileResponse> updateProfile(@Path("user_id") String userId, @Body UpdateProfileRequest body);

    @POST("api/posts")
    Call<PostResponse> createPost(@Body CreatePostRequest body);

    @GET("api/posts")
    Call<PostsResponse> getAllPosts();

    @GET("api/posts/user/{user_id}")
    Call<PostsResponse> getPostsByUser(@Path("user_id") String userId);

    @DELETE("api/posts/{post_id}")
    Call<DeleteResponse> deletePost(@Path("post_id") String postId);
}

