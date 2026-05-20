package com.example.a24520085_buihotrucanh;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import java.util.List;

public class PostAdapter extends BaseAdapter {
    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
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

            TextView tvName    = convertView.findViewById(R.id.tv_item_name);
            TextView tvDate    = convertView.findViewById(R.id.tv_item_date);
            TextView tvContent = convertView.findViewById(R.id.tv_item_content);

            tvName.setText(post.getName());
            tvDate.setText(post.getDate());
            tvContent.setText(post.getContent());

            tvName.setOnClickListener(v -> openProfileFragment(post.getName()));

            convertView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                menu.setHeaderTitle("Thao tác");
                menu.add(0, 1, 0, "Detail").setOnMenuItemClickListener(item -> {
                    showDetailDialog(post);
                    return true;
                });
                menu.add(0, 2, 0, "Hide").setOnMenuItemClickListener(item -> {
                    UserData.hidePost(post);
                    notifyDataSetChanged();
                    return true;
                });
            });
        }

        return convertView;
    }

    private void openProfileFragment(String ownerName) {
        if (!(context instanceof FragmentActivity)) return;

        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();

        ProfileFragment fragment = ProfileFragment.newInstance(ownerName);
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDetailDialog(Post post) {
        new AlertDialog.Builder(context)
                .setTitle("Post Detail")
                .setMessage("Author: " + post.getAuthor()
                        + "\nDate: " + post.getDate()
                        + "\n\nContent: " + post.getContent())
                .setPositiveButton("Đóng", null)
                .show();
    }
}