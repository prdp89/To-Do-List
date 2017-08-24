package com.example.dev.contentprovidermvp.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.m1040033.contentprovidermvp.R;
import com.example.dev.contentprovidermvp.contract.ItemClickedListener;
import com.example.dev.contentprovidermvp.contract.ToDoEntryContract;
import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.presenter.AddEditToDoPresenter;
import com.example.dev.contentprovidermvp.presenter.ToDoListPresenter;
import com.example.dev.contentprovidermvp.service.Injection;
import com.example.dev.contentprovidermvp.service.ToDoRepository;
import com.example.dev.contentprovidermvp.utils.AppUtils;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by M1040033 on 7/31/2017.
 */

public class AddEditToDoFragment extends Fragment implements ToDoEntryContract.View,
        View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    ToDoEntryContract.Presenter mPresenter;
    private FloatingActionButton mFloatingActionButton;
    private View rootView;
    private EditText mTitle;
    private Button mToDoChooseDateButton;
    private Button mToDoChooseTimeButton;
    private SwitchCompat mSwitchCompact;

    private Calendar dateValue = Calendar.getInstance();
    private Calendar timeValue = Calendar.getInstance();

    private boolean isEdited;
    private String toDoID;

    public AddEditToDoFragment() {
        // Requires empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ToDoRepository repository = Injection.provideTasksRepository(getContext());
        mPresenter = new AddEditToDoPresenter(this, repository);
    }

    static DataPassListener mCallback;

    interface DataPassListener {
        void passData(ToDo data);
    }

    public static AddEditToDoFragment newInstance() {
        return new AddEditToDoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_edit_to_do, container, false);
        mTitle = rootView.findViewById(R.id.userToDoEditText);

        mToDoChooseDateButton = rootView.findViewById(R.id.newToDoChooseDateButton);
        mToDoChooseDateButton.setOnClickListener(this);

        mToDoChooseTimeButton = rootView.findViewById(R.id.newToDoChooseTimeButton);
        mToDoChooseTimeButton.setOnClickListener(this);

        mSwitchCompact = rootView.findViewById(R.id.toDoHasDateSwitchCompat);
        mSwitchCompact.setOnCheckedChangeListener(this);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static void setOnItemClickedListener(DataPassListener listener) {
        mCallback = checkNotNull(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    //region ToDoEntryContract.View Implemented methods
    @Override
    public void setPresenter(ToDoEntryContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void triggerManualSync() {

    }

    @Override
    public void initializeDefaultView() {
        mFloatingActionButton = rootView.findViewById(R.id.makeToDoFloatingActionButton);
        mFloatingActionButton.setOnClickListener(this);

        if(getActivity().getIntent().hasExtra("toDoItem"))
        {
            ToDo toDoItem = (ToDo)getActivity().getIntent().getSerializableExtra("toDoItem");

            toDoID = toDoItem.getmId();

            mTitle.setText(toDoItem.getmTitle());

            if(toDoItem.getmTime() != null && toDoItem.getmTime().length() > 0)
            {
                mSwitchCompact.setChecked(true);
                ((TextView) rootView.findViewById(R.id.newToDoDateTimeReminderTextView)).setText( toDoItem.getmTime());
            }
        }
    }

    //This method finishes current Activity and pass back TimeValue to ToDoList Fragment that will help in creating and updating alarm.
    @Override
    public void finishActivity(Date timeValue) {
        getActivity().setResult(RESULT_OK);

        if (null != mCallback && mSwitchCompact.isChecked()) {  //set alarm if switch is ON
            ToDo toDoModel = new ToDo(timeValue, mTitle.getText().toString(), mPresenter.getToDoEntryID());
            toDoModel.setEdited(getActivity().getIntent().hasExtra("toDoItem")); //helps to create and update alarm in ToDoListFragment
            mCallback.passData(toDoModel);
        }

        getActivity().finish();
    }

//    private Date calculateTime() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(dateValue.get(Calendar.YEAR), dateValue.get(Calendar.MONTH), dateValue.get(Calendar.DATE), timeValue.get(Calendar.HOUR), timeValue.get(Calendar.MINUTE));
//        return timeValue.getTime();
//    }

    @Override
    public void showTimePicker() {
        Date date = new Date();
        AppUtils.hideKeyboard(mTitle, getContext());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), this, hour, minute, DateFormat.is24HourFormat(getContext()));
        timePickerDialog.show();
    }

    @Override
    public void showDatePicker() {

        Date date = new Date();
        AppUtils.hideKeyboard(mTitle, getContext());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);
        datePickerDialog.show();
    }

    //endregion

    //Handles Save / Update / TimePicker and DatePicker callbacks
    @Override
    public void onClick(View view) {
        if (view != null) {
            if (view.getId() == mFloatingActionButton.getId() && mTitle.getText().toString().length() > 0) {
                mPresenter.saveEditNewTask(mTitle.getText().toString(), mSwitchCompact.isChecked() ? timeValue.getTime() : null, toDoID);
            } else if (view.getId() == mFloatingActionButton.getId() && mTitle.getText().toString().length() == 0) {
                mFloatingActionButton.animate().setInterpolator(new CycleInterpolator(1.0f)).rotation(-105f).setDuration(1200).start();
            } else if (view.getId() == mToDoChooseDateButton.getId()) {
                showDatePicker();
            } else if (view.getId() == mToDoChooseTimeButton.getId()) {
                showTimePicker();
            }
        }
    }

    //region Date time picker Callbacks
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (year > 0 && month > 0 && day > 0) {
            dateValue.set(year, month, day);
            setDateTimeTextView(dateValue, timeValue);
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        if (hour > 0 && minutes > 0) {
            timeValue.set(dateValue.get(Calendar.YEAR), dateValue.get(Calendar.MONTH), dateValue.get(Calendar.DATE), hour, minutes);
            setDateTimeTextView(dateValue, timeValue);
        }
    }
    //endregion

    private void setDateTimeTextView(Calendar dateValue, Calendar timeValue) {
        String dateString = "", timeString = "", amPmString = "";
        if (dateValue != null)
            dateString = AppUtils.formatDate("d MMM, yyyy", dateValue.getTime());
        if (timeValue != null) {
            if (DateFormat.is24HourFormat(getContext())) {
                timeString = AppUtils.formatDate("k:mm", timeValue.getTime());
            } else {
                timeString = AppUtils.formatDate("h:mm", timeValue.getTime());
                amPmString = AppUtils.formatDate("a", timeValue.getTime());
            }
        }
        String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
        ((TextView) rootView.findViewById(R.id.newToDoDateTimeReminderTextView)).setText(finalString);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        LinearLayout toDoDateTimeButtons = rootView.findViewById(R.id.toDoEnterDateLinearLayout);

        AppUtils.hideKeyboard(mTitle, getContext());
        toDoDateTimeButtons.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        AppUtils.animateFadeInFadeOut(toDoDateTimeButtons, isChecked);
    }
}
