package com.weicong.gotravelling.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.activity.NearbyActivity;
import com.weicong.gotravelling.adapter.FollowUserAdapter;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class FollowUserFragment extends Fragment {

    private RefreshCircleProgress mRefreshProgress;
    private RecyclerView mRecyclerView;

    private FollowUserAdapter mAdapter;

    private List<User> mRoutes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_route, container, false);

        mRefreshProgress = (RefreshCircleProgress) view.findViewById(R.id.refresh_progress);
        mRefreshProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshProgress.setRefreshing(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createCallback());
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        getFollowUser();

        return view;
    }

    public ItemTouchHelper.Callback createCallback() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                showDeleteUserDialog(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

        };
    }

    /**
     * 获取关注的路线
     */
    private void getFollowUser() {
        User user = BmobUser.getCurrentUser(getActivity(), User.class);
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("followUsers", new BmobPointer(user));
        query.include("creator");
        query.order("-createdAt");
        query.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                mRoutes = list;
                mAdapter = new FollowUserAdapter(getActivity(), mRoutes);
                mRecyclerView.setAdapter(mAdapter);
                mRefreshProgress.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                mRefreshProgress.setRefreshing(false);
            }
        });
    }

    /**
     * 显示删除用户对话框
     *
     * @param position 位置
     */
    private void showDeleteUserDialog(final int position) {
        String message = "您确定要取消关注" + mRoutes.get(position).getUsername() + "吗？";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.not_follow)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    /**
     * 取消用户
     *
     * @param position 位置
     */
    private void deleteUser(final int position) {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(getActivity());
        progressDialog.show();
        User user = mRoutes.get(position);
        UserManager.getInstance(getActivity()).notFollowUser(user, new UserManager.OnUpdateListener() {
            @Override
            public void onSuccess() {
                mAdapter.delete(position);
                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_nearby, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_nearby) {
            Intent intent = new Intent(getActivity(), NearbyActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
