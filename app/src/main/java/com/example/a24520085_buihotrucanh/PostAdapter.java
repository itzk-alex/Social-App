package com.example.a24520085_buihotrucanh;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.models.DeleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends BaseAdapter {

    public interface PostListListener {
        void onPostListChanged();
    }

    private final Context context;
    private final List<Post> postList;
    @Nullable
    private final PostListListener listListener;

    public PostAdapter(Context context, List<Post> postList) {
        this(context, postList, null);
    }

    public PostAdapter(Context context, List<Post> postList, @Nullable PostListListener listListener) {
        this.context = context;
        this.postList = postList;
        this.listListener = listListener;
    }

    @Override
    public int getCount() { return postList.size(); }
    @Override
    public Object getItem(int i) { return postList.get(i); }
    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        }

        Post post = postList.get(position);

        if (UserData.isPostHidden(post)) {
            convertView.setVisibility(View.GONE);
            convertView.setLayoutParams(new android.widget.AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1));
        } else {
            convertView.setVisibility(View.VISIBLE);
            convertView.setLayoutParams(new android.widget.AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView imgAvatar = convertView.findViewById(R.id.img_item_avatar);
            TextView tvName = convertView.findViewById(R.id.tv_item_name);
            TextView tvDate = convertView.findViewById(R.id.tv_item_date);
            TextView tvContent = convertView.findViewById(R.id.tv_item_content);

            tvName.setText(post.getName() != null ? post.getName() : "");
            tvDate.setText(post.getDate() != null ? post.getDate() : "");
            tvContent.setText(post.getContent() != null ? post.getContent() : "");

            AvatarImageLoader.load(imgAvatar,
                    AvatarImageLoader.resolveAvatarUrl(post.getAvatarUrl(), post.getUserId()));

            tvName.setOnClickListener(v -> openProfileFragment(post.getName(), post.getUserId()));

            convertView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                menu.setHeaderTitle("Options");
                menu.add(0, 1, 0, "Detail").setOnMenuItemClickListener(item -> {
                    showDetailDialog(post);
                    return true;
                });
                menu.add(0, 2, 0, "Hide").setOnMenuItemClickListener(item -> {
                    UserData.hidePost(post);
                    notifyDataSetChanged();
                    return true;
                });

                boolean canDelete = UserData.userId != null && !UserData.userId.isEmpty()
                        && post.getUserId() != null && UserData.userId.equals(post.getUserId())
                        && post.getId() != null && !post.getId().isEmpty();
                if (canDelete) {
                    menu.add(0, 3, 0, "Delete post").setOnMenuItemClickListener(item -> {
                        confirmDeletePost(post);
                        return true;
                    });
                }
            });
        }

        return convertView;
    }

    private void confirmDeletePost(Post post) {
        new AlertDialog.Builder(context)
                .setTitle("Delete post")
                .setMessage("Are you sure? This action cannot undo")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> deletePostOnServer(post))
                .show();
    }

    private void deletePostOnServer(Post post) {
        ApiClient.api().deletePost(post.getId()).enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.isSuccessful()) {
                    postList.remove(post);
                    notifyDataSetChanged();
                    if (listListener != null) listListener.onPostListChanged();
                    Toast.makeText(context, "Delete succesfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete failed (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(context, "Internet connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openProfileFragment(String ownerName, String ownerUserId) {
        if (!(context instanceof FragmentActivity)) return;

        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();

        ProfileFragment fragment = ProfileFragment.newInstance(ownerName, ownerUserId);
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDetailDialog(Post post) {
        new AlertDialog.Builder(context)
                .setTitle("Post Detail")
                .setMessage("Author: " + (post.getAuthor() != null ? post.getAuthor() : "")
                        + "\nDate: " + (post.getDate() != null ? post.getDate() : "")
                        + "\n\nContent: " + (post.getContent() != null ? post.getContent() : ""))
                .setPositiveButton("Close", null)
                .show();
    }
}
