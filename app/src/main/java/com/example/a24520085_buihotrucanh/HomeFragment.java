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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private SwipeRefreshLayout swipeHome;
    private EditText edtContent;
    private AppCompatButton btnPost;
    private ListView lvPosts;
    private List<Post> postList;
    private PostAdapter adapter;
    private boolean isSortDateAsc = true;
    private boolean isSortAuthorAsc = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeHome = view.findViewById(R.id.swipeHome);
        edtContent = view.findViewById(R.id.edt_post_content);
        btnPost = view.findViewById(R.id.btn_post);
        lvPosts = view.findViewById(R.id.lv_posts);

        postList = UserData.globalPostList;
        adapter = new PostAdapter(requireContext(), postList, () -> fetchPosts(null));
        lvPosts.setAdapter(adapter);

        swipeHome.setOnRefreshListener(() -> fetchPosts(() -> swipeHome.setRefreshing(false)));

        fetchPosts(null);

        btnPost.setOnClickListener(v -> {
            String content = edtContent.getText().toString().trim();
            if (!content.isEmpty()) {
                createPost(content);
            }
        });

        setupMenu();
    }

    private void fetchPosts(@Nullable Runnable onComplete) {
        ApiClient.api().getAllPosts().enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    postList.clear();
                    for (ApiPostDto p : response.body().data) {
                        postList.add(Post.fromApiDto(p));
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                }
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                if (onComplete != null) onComplete.run();
            }
        });
    }

    private void createPost(String content) {
        String uid = UserData.userId;
        if (uid == null || uid.isEmpty()) {
            postList.add(0, new Post(UserData.registeredName, getCurrentDate(), content, UserData.AvatarUrl));
            adapter.notifyDataSetChanged();
            edtContent.setText("");
            return;
        }

        ApiClient.api().createPost(new CreatePostRequest(uid, content)).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    postList.add(0, Post.fromApiDto(response.body().data));
                } else {
                    postList.add(0, new Post(UserData.registeredName, getCurrentDate(), content, UserData.AvatarUrl));
                }
                if (adapter != null) adapter.notifyDataSetChanged();
                edtContent.setText("");
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                postList.add(0, new Post(UserData.registeredName, getCurrentDate(), content, UserData.AvatarUrl));
                if (adapter != null) adapter.notifyDataSetChanged();
                edtContent.setText("");
            }
        });
    }

    private static int compareNullable(String a, String b) {
        String sa = a != null ? a : "";
        String sb = b != null ? b : "";
        return sa.compareTo(sb);
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
                        postList.sort((p1, p2) -> compareNullable(p1.getDate(), p2.getDate()));
                    } else {
                        postList.sort((p1, p2) -> compareNullable(p2.getDate(), p1.getDate()));
                    }
                    isSortDateAsc = !isSortDateAsc;
                    adapter.notifyDataSetChanged();
                    return true;
                }

                if (id == R.id.opt_sort_author) {
                    if (isSortAuthorAsc) {
                        postList.sort((p1, p2) -> compareNullable(p1.getAuthor(), p2.getAuthor()));
                    } else {
                        postList.sort((p1, p2) -> compareNullable(p2.getAuthor(), p1.getAuthor()));
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
