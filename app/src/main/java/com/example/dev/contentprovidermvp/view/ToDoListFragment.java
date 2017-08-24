package com.example.dev.contentprovidermvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.m1040033.contentprovidermvp.R;
import com.example.dev.contentprovidermvp.adapter.ToDoListAdapter;
import com.example.dev.contentprovidermvp.contract.ItemClickedListener;
import com.example.dev.contentprovidermvp.contract.TaskListContract;
import com.example.dev.contentprovidermvp.customControl.RecyclerViewEmptySupport;
import com.example.dev.contentprovidermvp.loaders.ToDoListLoader;
import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.presenter.ToDoListPresenter;
import com.example.dev.contentprovidermvp.service.Injection;
import com.example.dev.contentprovidermvp.service.ToDoRepository;
import com.example.dev.contentprovidermvp.service.local.TodoNotificationService;
import com.example.dev.contentprovidermvp.utils.AppUtils;
import com.example.dev.contentprovidermvp.utils.DividerItemDecoration;
import com.example.dev.contentprovidermvp.utils.SwipeUtil;

import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by M1040033 on 7/23/2017.
 */

public class ToDoListFragment extends Fragment implements TaskListContract.View,
        ItemClickedListener<ToDo>, AddEditToDoFragment.DataPassListener {

    private TaskListContract.Presenter mPresenter;
    private RecyclerViewEmptySupport mRecyclerView;
    private ToDoListAdapter mAdapter;
    private static int sessionDepth = 0;

    private View rootView;

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        AddEditToDoFragment.setOnItemClickedListener(this);
        setSwipeForRecyclerView();
    }

   //region OnActivity Back form Lock, IDle or Return button.
    @Override
    public void onStart() {
        super.onStart();
        sessionDepth++;
        if (sessionDepth == 1) {
            mPresenter.resetLoader(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sessionDepth > 0)
            sessionDepth--;
        if (sessionDepth == 0) {
            // app went to background
        }
    }
    //endregion

    public ToDoListFragment() {
        // Requires empty public constructor
    }

    public static ToDoListFragment newInstance() {
        return new ToDoListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ToDoRepository repository = Injection.provideTasksRepository(getContext());

        // Create the presenter
        mPresenter = new ToDoListPresenter(this, getLoaderManager(), repository, this);
        mAdapter = new ToDoListAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearCompleted:
                mPresenter.clearCompletedToDo();
                mAdapter.swapList(mPresenter.getUpdatedList());
                return true;
            case R.id.aboutMeMenuItem:
                AppUtils.showAlertDialog(getString(R.string.about_title), getString(R.string.about_message), getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //region Base View Interface Method Implementation
    @Override
    public void setPresenter(TaskListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void triggerManualSync() {

    }
    //endregion

    //region TaskList Contract view Interface Implementation
    @Override
    public void initializeDefaultView() {

        if (mRecyclerView == null) {
            mRecyclerView = rootView.findViewById(R.id.toDoRecyclerView);

            mRecyclerView.setEmptyView(rootView.findViewById(R.id.toDoEmptyView));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            final DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
            mRecyclerView.addItemDecoration(itemDecorator);

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setHasFixedSize(true);

            mAdapter.setOnItemClickedListener(this);

            // Set up floating action button
            FloatingActionButton fab =
                    rootView.findViewById(R.id.addToDoItemFAB);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.addNewTask();
                    mRecyclerView.removeItemDecoration(itemDecorator);
                }
            });
        }
    }

    @Override
    public void showAddTask() {
        Intent intent = new Intent(getContext(), AddEditToDoActivity.class);
        startActivityForResult(intent, AddEditToDoActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showToDoListing(List<ToDo> toDoList) {
        mAdapter.swapList(toDoList);
    }

    @Override
    public void onItemClicked(ToDo item) {
        Intent intent = new Intent(getContext(), AddEditToDoActivity.class);
        intent.putExtra("toDoItem", item);
        startActivityForResult(intent, AddEditToDoActivity.REQUEST_EDIT_TASK);
    }
    //endregion

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddEditToDoActivity.REQUEST_ADD_TASK) {
            if (resultCode == RESULT_OK) {
                mPresenter.resetLoader(true);
            } else
                mPresenter.resetLoader(false);
        } else if (requestCode == AddEditToDoActivity.REQUEST_EDIT_TASK && resultCode == RESULT_OK) {
            mPresenter.resetLoader(true);
        }
    }

    @Override
    public void passData(ToDo toDoItem) {
        if (toDoItem != null) {
            Intent intent = new Intent(getContext(), TodoNotificationService.class);
            intent.putExtra(TodoNotificationService.TODOUUID, toDoItem.getmId());
            intent.putExtra(TodoNotificationService.TODOTEXT, toDoItem.getmTitle());

            int uuid = UUID.fromString(toDoItem.getmId()).hashCode();
            if (toDoItem.isEdited()) //if the todo item is edited in previous Fragment
            {
                AppUtils.deleteAlarm(intent, uuid, getContext());
            }

            AppUtils.createAlarm(intent, uuid, toDoItem.getmToDoDate().getTime(), getContext());
        }
        AddEditToDoFragment.mCallback = null;
    }

    private void setSwipeForRecyclerView() {

        SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ToDoListAdapter adapter = (ToDoListAdapter) mRecyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ToDoListAdapter adapter = (ToDoListAdapter) mRecyclerView.getAdapter();
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //set swipe label
        swipeHelper.setLeftSwipeLable("Delete");
        //set swipe background-Color
        swipeHelper.setLeftcolorCode(ContextCompat.getColor(getActivity(), R.color.swipebg));
    }

    public void updateUI(Intent intent) {
        mAdapter.swapList(mPresenter.getUpdatedList());
    }
}
