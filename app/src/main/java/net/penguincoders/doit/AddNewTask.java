package net.penguincoders.doit;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Objects;

@Entity
data class Task(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val task: String,
        val status: Int
)

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM Task")
    fun getAllTasks(): LiveData<List<Task>>
}
@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
abstract fun taskDao(): TaskDao

        companion object {
@Volatile
private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "task_database"
        ).build()
        INSTANCE = instance
        instance
        }
        }
        }
        }
class AddTaskViewModel(application: Application) : AndroidViewModel(application) {
private val db: AppDatabase = AppDatabase.getInstance(application)

        fun insertTask(task: Task) {
        viewModelScope.launch {
        db.taskDao().insertTask(task)
        }
        }

        fun updateTask(task: Task) {
        viewModelScope.launch {
        db.taskDao().updateTask(task)
        }
        }
class AddNewTask : BottomSheetDialogFragment() {

private lateinit var viewModel: AddTaskViewModel
private val args: AddNewTaskArgs by navArgs()

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        viewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        val binding = FragmentAddNewTaskBinding.inflate(inflater, container, false)

        // ... view binding and other initialization here

        binding.newTaskSaveButton.setOnClickListener {
        val taskText = binding.newTaskText.text.toString()
        if (args.isUpdate) {
        val updatedTask = Task(args.taskId, taskText, 0)
        viewModel.updateTask(updatedTask)
        } else {
        val newTask = Task(task = taskText, status = 0)
        viewModel.insertTask(newTask)
        }
        dismiss()
        }
        return binding.root
        }
        }
