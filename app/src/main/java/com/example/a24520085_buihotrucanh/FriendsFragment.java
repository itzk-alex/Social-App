package com.example.a24520085_buihotrucanh;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FriendsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACT_LOADER = 1;

    private FriendViewModel viewModel;

    private SwipeRefreshLayout swipeFriends;
    private Button btnFriendRequests;
    private SearchView searchView;
    private TextView tvMyFriendsLabel;
    private RecyclerView rvFriends;
    private RecyclerView rvServerSuggested;
    private RecyclerView rvSuggestedFriends;

    private FriendAdapter friendAdapter;
    private ServerSuggestedAdapter serverSuggestedAdapter;
    private SuggestedFriendAdapter suggestedAdapter;

    private List<FriendItem> myFriendsList = new ArrayList<>();
    private List<ContactItem> suggestedContactsList = new ArrayList<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) loadContacts();
                else Toast.makeText(requireContext(), "Need contact permission", Toast.LENGTH_SHORT).show();
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        viewModel.loadInitialFriends();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        initViews(view);
        setupRecyclerViews();
        observeViewModel();
        checkPermissionAndLoadContacts();
        return view;
    }

    private void initViews(View view) {
        swipeFriends = view.findViewById(R.id.swipeFriends);
        btnFriendRequests = view.findViewById(R.id.btnFriendRequests);
        searchView = view.findViewById(R.id.searchView);
        tvMyFriendsLabel = view.findViewById(R.id.tvMyFriendsLabel);
        rvFriends = view.findViewById(R.id.rvFriends);
        rvServerSuggested = view.findViewById(R.id.rvServerSuggested);
        rvSuggestedFriends = view.findViewById(R.id.rvSuggestedFriends);

        swipeFriends.setOnRefreshListener(() ->
                viewModel.refreshFriendsAndSuggestions(() -> swipeFriends.setRefreshing(false)));

        btnFriendRequests.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FriendRequestFragment())
                    .addToBackStack(null)
                    .commit();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterFriendsList(newText);
                return true;
            }
        });
    }

    private void observeViewModel() {
        viewModel.getFriendCount().observe(getViewLifecycleOwner(), count -> {
            int n = count != null ? count : 0;
            tvMyFriendsLabel.setText(getString(R.string.friends_list_with_count, n));
        });

        viewModel.getServerSuggestions().observe(getViewLifecycleOwner(), list -> {
            if (serverSuggestedAdapter != null) {
                serverSuggestedAdapter.setItems(list != null ? list : Collections.emptyList());
            }
        });
    }

    private void setupRecyclerViews() {
        rvFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
        friendAdapter = new FriendAdapter(myFriendsList);
        rvFriends.setAdapter(friendAdapter);

        viewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            myFriendsList.clear();
            if (friends != null) myFriendsList.addAll(friends);
            friendAdapter.notifyDataSetChanged();
        });

        rvServerSuggested.setLayoutManager(new LinearLayoutManager(requireContext()));
        serverSuggestedAdapter = new ServerSuggestedAdapter();
        rvServerSuggested.setAdapter(serverSuggestedAdapter);

        rvSuggestedFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
        suggestedAdapter = new SuggestedFriendAdapter(suggestedContactsList);
        rvSuggestedFriends.setAdapter(suggestedAdapter);
    }

    private void filterFriendsList(String text) {
        List<FriendItem> filteredList = new ArrayList<>();
        for (FriendItem item : myFriendsList) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item);
            }
        }
        if (friendAdapter != null) friendAdapter.filterList(filteredList);
    }

    private void checkPermissionAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private void loadContacts() {
        LoaderManager.getInstance(this).restartLoader(CONTACT_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(requireContext(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        suggestedContactsList.clear();
        if (data != null) {
            while (data.moveToNext()) {
                suggestedContactsList.add(new ContactItem(data.getString(1), data.getString(0)));
            }
        }
        if (suggestedAdapter != null) suggestedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        suggestedContactsList.clear();
    }
}
