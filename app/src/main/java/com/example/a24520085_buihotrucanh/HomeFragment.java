package com.example.a24520085_buihotrucanh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.models.ApiPostDto;
import com.example.a24520085_buihotrucanh.network.models.CreatePostRequest;
import com.example.a24520085_buihotrucanh.network.models.PostResponse;
import com.example.a24520085_buihotrucanh.network.models.PostsResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private EditText edtContent;
    private AppCompatButton btnPost;
    private ListView lvPosts;
    private List<Post> postList;
    private PostAdapter adapter;
    private boolean isSortDateAsc = true;
    private boolean isSortAuthorAsc = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtContent = view.findViewById(R.id.edt_post_content);
        btnPost = view.findViewById(R.id.btn_post);
        lvPosts = view.findViewById(R.id.lv_posts);

        postList = UserData.globalPostList;
        adapter = new PostAdapter(requireContext(), postList);
        lvPosts.setAdapter(adapter);

        fetchPosts();

        btnPost.setOnClickListener(v -> {
            String content = edtContent.getText().toString().trim();
            if (!content.isEmpty()) {
                createPost(content);
            }
        });

        setupMenu();
    }

    private void fetchPosts() {
        ApiClient.api().getAllPosts().enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().data == null) return;

                postList.clear();
                for (ApiPostDto p : response.body().data) {
                    String authorName = (p.author != null && p.author.name != null) ? p.author.name : UserData.registeredName;
                    String createdAt = p.createdAt != null ? p.createdAt : "";
                    postList.add(new Post(p.id, p.userId, authorName, createdAt, p.content != null ? p.content : ""));
                }
                if (adapter != null) adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                // im lặng để UI không spam; user vẫn có thể post
            }
        });
    }

    private void createPost(String content) {
        String uid = UserData.userId;
        if (uid == null || uid.isEmpty()) {
            postList.add(0, new Post(UserData.registeredName, getCurrentDate(), content));
            adapter.notifyDataSetChanged();
            edtContent.setText("");
            return;
        }

        ApiClient.api().createPost(new CreatePostRequest(uid, content)).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    ApiPostDto p = response.body().data;
                    String authorName = (p.author != null && p.author.name != null) ? p.author.name : UserData.registeredName;
                    String createdAt = p.createdAt != null ? p.createdAt : getCurrentDate();
                    postList.add(0, new Post(p.id, p.userId, authorName, createdAt, p.content != null ? p.content : content));
                } else {
                    postList.add(0, new Post(UserData.registeredName, getCurrentDate(), content));
                }
                if (adapter != null) adapter.notifyDataSetChanged();
                edtContent.setText("");
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                postList.add(0, new Post(UserData.registeredName, getCurrentDate(), content));
                if (adapter != null) adapter.notifyDataSetChanged();
                edtContent.setText("");
            }
        });
    }

    private void setupMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.options_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.opt_profile) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateTo(R.id.nav_profile);
                    }
                    return true;
                }

                if (id == R.id.opt_sort_date) {
                    if (isSortDateAsc) {
                        postList.sort((p1, p2) -> p1.getDate().compareTo(p2.getDate()));
                    } else {
                        postList.sort((p1, p2) -> p2.getDate().compareTo(p1.getDate()));
                    }
                    isSortDateAsc = !isSortDateAsc;
                    adapter.notifyDataSetChanged();
                    return true;
                }

                if (id == R.id.opt_sort_author) {
                    if (isSortAuthorAsc) {
                        postList.sort((p1, p2) -> p1.getAuthor().compareToIgnoreCase(p2.getAuthor()));
                    } else {
                        postList.sort((p1, p2) -> p2.getAuthor().compareToIgnoreCase(p1.getAuthor()));
                    }
                    isSortAuthorAsc = !isSortAuthorAsc;
                    adapter.notifyDataSetChanged();
                    return true;
                }

                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(calendar.getTime());
    }
}