package net.penguincoders.doit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

// تعريف الدالة الخاصة باغلاق الدايلوج

public interface DialogCloseListener
        void handleDialogClose(DialogInterface dialog);
.    // تعريف الدالة الخاصة باغلاق الدايلوج

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback

        private ToDoAdapter adapter;

        public RecyclerItemTouchHelper(ToDoAdapter adapter)
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                this.adapter = adapter;


@Override
public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
        return false;


@Override
public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT)
        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Confirm",
        new DialogInterface.OnClickListener()
@Override
public void onClick(DialogInterface dialog, int which)
        adapter.deleteTask(position);

        );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
@Override
public void onClick(DialogInterface dialog, int which)
        adapter.notifyItemChanged(position);

        );
        AlertDialog dialog = builder.create();
        dialog.show();
        else
        adapter.editTask(position);


        .    // تعريف الكلاس الخاص بالتعامل مع حذف وتعديل المهمة من خلال السحب

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>

        private List<ToDoModel> tasksList;
        private DatabaseHandler db;
        private MainActivity activity;

        public ToDoAdapter(DatabaseHandler db, MainActivity activity)
        this.db = db;
                this.activity = activity;


@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);


@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        db.openDatabase();
final ToDoModel item = tasksList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnClickListener(new View.OnClickListener()
@Override
public void onClick(View v)
        if (holder.task.isChecked())
        db.updateStatus(item.getId(), 1);
        else
        db.updateStatus(item.getId(), 0);


        );


@Override
public int getItemCount()
        return tasksList.size();


public Context getContext()
        return activity;


public void setTasks(List<ToDoModel> tasksList)
        this.tasksList = tasksList;
        notifyDataSetChanged();


public void deleteTask(int position)
        ToDoModel item = tasksList.get(position);
        db.deleteTask(item.getId());
        tasksList.remove(position);
        notifyItemRemoved(position);


public void editTask(int position)
        ToDoModel item = tasksList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);


public boolean toBoolean(int num)
        return num != 0;


public classViewHolder extends RecyclerView.ViewHolder

        CheckBox task;

public ViewHolder(@NonNull View itemView)
        super(itemView);
        task = itemView.findViewById(R.id.todoCheckBox);


        .    // تعريف الادابتر الخاص بعرض المهمات والتعامل معها

public class AddNewTask extends AppCompatDialogFragment

        public static final String TAG = "AddNewTask";

        private EditText newTaskText;
        private Button addNewTaskButton;

        private DatabaseHandler db;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_task_layout, null);
        builder.setView(view).setTitle("Add New Task");
                newTaskText = view.findViewById(R.id.newTaskText);
                addNewTaskButton = view.findViewById(R.id.addNewTaskButton);

                db = new DatabaseHandler(getActivity());

                addNewTaskButton.setOnClickListener(new View.OnClickListener()
@Override
public void onClick(View v)
        if (!newTaskText.getText().toString().isEmpty())
        db.insertTask(newTaskText.getText().toString());
        dismiss();
        else
        Toast.makeText(getActivity(), "Please enter a task", Toast.LENGTH_SHORT).show();


        );
        return builder.create();


@Override
public void onDismiss(@NonNull DialogInterface dialog)
        super.onDismiss(dialog);
        DialogCloseListener activity = (DialogCloseListener) getActivity();
        if (activity != null)
        activity.handleDialogClose(dialog);



public static AddNewTask newInstance()
        return new AddNewTask();
        }
        }