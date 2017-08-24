package com.example.dev.contentprovidermvp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dev.contentprovidermvp.contract.ItemClickedListener;
import com.example.dev.contentprovidermvp.model.ToDo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

import com.example.dev.contentprovidermvp.service.Injection;
import com.example.dev.contentprovidermvp.service.ToDoDataSource;
import com.example.dev.contentprovidermvp.service.ToDoRepository;
import com.example.dev.contentprovidermvp.service.local.TodoNotificationService;
import com.example.dev.contentprovidermvp.utils.AppUtils;
import com.example.dev.contentprovidermvp.utils.LetterTileProvider;

import com.example.m1040033.contentprovidermvp.R;

/**
 * Created by prdp on 7/25/2017.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {

    private List<ToDo> mData = new ArrayList<>();
    private ItemClickedListener<ToDo> mItemClickedListener;

    private Resources res;
    private int tileSize;
    private Context mContext;

    private List<String> itemsPendingRemoval;
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private Handler handler = new Handler(); // handler for running delayed runnables
    private HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables,
    // so we can cancel a removal if need be

    private ToDoRepository mRepository;

    public ToDoListAdapter(Context context) {
        itemsPendingRemoval = new ArrayList<>();
        mRepository = Injection.provideTasksRepository(context);
    }

    //region RecyclerView.Adapter implemented methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        res = parent.getContext().getResources();
        tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);

        View v = inflater.inflate(R.layout.item_todo_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ToDo sprintModel = getItem(position);
        final String mID = sprintModel.getmId();

        if (itemsPendingRemoval.contains(mID)) {
            /** {show swipe layout} and {hide regular layout} */
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
            holder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undoOpt(mID);
                }
            });
        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);

            holder.mToDoTextview.setText(sprintModel.getmTitle());

            if (sprintModel.getmToDoDate() != null) {
//                if (System.currentTimeMillis() > sprintModel.getmToDoDate().getTime())
//                    holder.mTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.red_primary));

                holder.mTimeTextView.setText(sprintModel.getmTime());
            }

            final LetterTileProvider tileProvider = new LetterTileProvider(mContext);
            final Bitmap letterTile = tileProvider.getLetterTile(sprintModel.getmTitle(), "key", tileSize, tileSize);

            holder.mColorImageView.setImageBitmap(letterTile);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    //endregion

    private ToDo getItem(int position) {
        return mData.get(position);
    }

    public void swapList(List<ToDo> toDoList) {
        mData = toDoList;
        notifyDataSetChanged();
    }

    public void setOnItemClickedListener(ItemClickedListener<ToDo> listener) {
        mItemClickedListener = checkNotNull(listener);
    }

    //region Swipe to Delete related modules
    private void undoOpt(String toID) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(toID);
        pendingRunnables.remove(toID);

        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);

        itemsPendingRemoval.remove(toID);

        // this will rebind the row in "normal" state
        notifyItemChanged(mData.indexOf(toID));
    }

    public void pendingRemoval(int position) {

        final ToDo toDoItem = getItem(position);
        final String data = toDoItem.getmId();

        if (!itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.add(data);

            // this will redraw row in "undo" state
            notifyItemChanged(position);

            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(toDoItem);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(data, pendingRemovalRunnable);
        }
    }

    public void remove(final ToDo toDoItem) {
        final int index = mData.indexOf(toDoItem);
        if (itemsPendingRemoval.contains(toDoItem.getmId())) {
            itemsPendingRemoval.remove(toDoItem.getmId());
        }
        if (mData.contains(toDoItem)) {
            mData.remove(index);

            mRepository.deleteTask(toDoItem, new ToDoDataSource.InsertDataCallBack<Integer>() {
                @Override
                public void dataChanged(Integer isSuccess) {
                    if (isSuccess != -1) {
                        deletePendingAlarm(toDoItem);
                        notifyItemRemoved(index);
                    }
                }
            });
        }
    }

    private void deletePendingAlarm(ToDo toDoItem) {
        Intent intent = new Intent(mContext, TodoNotificationService.class);
        intent.putExtra(TodoNotificationService.TODOUUID, toDoItem.getmId());
        intent.putExtra(TodoNotificationService.TODOTEXT, toDoItem.getmTitle());

        int uuid = UUID.fromString(toDoItem.getmId()).hashCode();
        AppUtils.deleteAlarm(intent, uuid, mContext);
    }

    public boolean isPendingRemoval(int position) {
        ToDo data = getItem(position);
        return itemsPendingRemoval.contains(data.getmId());
    }
    //endregion

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout regularLayout;
        public LinearLayout swipeLayout;
        public TextView undo;

        TextView mToDoTextview;
        ImageView mColorImageView;
        TextView mTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            regularLayout = itemView.findViewById(R.id.regularLayout);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            undo = itemView.findViewById(R.id.undo);

            mToDoTextview = itemView.findViewById(R.id.notesTitle);
            mTimeTextView = itemView.findViewById(R.id.notesTime);
            mColorImageView = itemView.findViewById(R.id.toDoLetter);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != mItemClickedListener) {
                mItemClickedListener.onItemClicked(getItem(getLayoutPosition()));
            }
        }
    }
}
