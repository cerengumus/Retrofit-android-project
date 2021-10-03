package com.example.homework4.fragments

import android.annotation.SuppressLint
import android.graphics.Insets.add
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.androidmobilebootcampfourthweek.fragments.adapter.Adapter
import com.example.homework4.MainActivity
import com.example.homework4.R
import com.example.homework4.base.BaseCallback
import com.example.homework4.service.GetResponse
import com.example.homework4.service.ServiceConnector
import com.example.homework4.service.Todo
import com.patikadev.deneme1.utils.changeStatusBarColor
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call


class HomeFragment : Fragment(), Adapter.OnClickListener {

    private var todoList = ArrayList<Todo>()
    private lateinit var todosAdapter : Adapter

    val limit = 10
    var skip = 0
    var scrollLimit: Int = 1
    var scrollCount: Int = 0
    var taskCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        changeStatusBarColor(R.color.purple_700)

        getDataByPagination(limit, skip)

        return inflater.inflate(R.layout.fragment_home, container, false)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        floatingActionButton.setOnClickListener {
            showMaterialDialog()
        }



        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState== RecyclerView.SCROLL_STATE_IDLE && scrollCount <= scrollLimit) {
                    skip += limit
                    scrollCount++
                    progressBar.visibility = View.VISIBLE
                    getDataByPagination(limit, skip)

                    //region Optional Toast
                    // Toast.makeText( requireContext(), "Reached the end of page: $scrollCount", Toast.LENGTH_SHORT).show()
                    //endregion
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()

        // in this block what we're simply doing is that we show a dialog
        // when the user presses the back button when in this fragment

        val activity = activity as MainActivity

        activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity.finish()
            }
        })

    }

    private fun getDataByPagination(limit: Int, skip: Int){

        ServiceConnector.restInterface.getTaskByPagination(limit, skip).enqueue(object: BaseCallback<GetResponse>(){
            override fun onSuccess(getResponse: GetResponse) {
                super.onSuccess(getResponse)

                progressBar.visibility = View.GONE

                todoList.addAll(getResponse.data)

                if(todoList.size == 0){
                    noTaskMessage.visibility = View.VISIBLE
                }
                todosAdapter = Adapter(todoList, this@HomeFragment)

                recyclerView.adapter = todosAdapter

                recyclerView.layoutManager = LinearLayoutManager(requireContext())

            }

            override fun onFailure() {
                super.onFailure()
                noTaskMessage.visibility = View.VISIBLE
                Toast.makeText(requireActivity(), "Something went wrong, no tasks have been fetched", Toast.LENGTH_SHORT).show()
            }

        })
    }

    @SuppressLint("CheckResult")
    private fun showMaterialDialog(){

        val newTodo = Todo()

        MaterialDialog(requireActivity())
//            .customView(R.layout.layout_dialog) // Optional
            .show {
                title(R.string.add_new_task)
                input { _, newTaskDesc ->
                    newTodo.description = newTaskDesc.toString()
                    newTodo.completed = false
                }
            }
            .positiveButton(R.string.add){
        ServiceConnector.restInterface.addTask(newTodo).enqueue(object: BaseCallback<Unit>(){
                    override fun onSuccess(data: Unit) {
                        Toast.makeText(requireActivity(), "Successfully added new task", Toast.LENGTH_SHORT).show()

                        getDataByPagination(limit, skip)

                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show()
                        Log.e("failure", "on triggered")
                    }
                })

                Log.d("object", newTodo.description + " " + newTodo.completed.toString())
            }
            .negativeButton(R.string.dismiss)


    }


    data class CompleteBody(
        var completed: Boolean
    )


    override fun onCompleteButtonClick(position: Int) {
        val clickedItem = todoList[position]

        clickedItem.completed = !clickedItem.completed

        todosAdapter.notifyItemChanged(position)

        val completeBody = CompleteBody(clickedItem.completed)

        ServiceConnector.restInterface.updateTaskById(clickedItem._id!!, completeBody).enqueue(object: BaseCallback<Unit>(){

            override fun onSuccess(data: Unit) {
                super.onSuccess(data)
                if(clickedItem.completed)
                    Toast.makeText(requireContext(), "Task ${position + 1} is completed", Toast.LENGTH_SHORT).show()
                else if (!clickedItem.completed){
                    Toast.makeText(requireContext(), "Task ${position + 1} is uncompleted", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure() {
                super.onFailure()

                Toast.makeText(requireContext(), "Failed to complete the task", Toast.LENGTH_SHORT).show()
            }

        })

    }



    override fun onDeleteButtonClick(position: Int) {

//        Toast.makeText(requireContext(), "Delete button clicked", Toast.LENGTH_SHORT).show() //Optional

        val clickedItem = todoList[position]

        ServiceConnector.restInterface.deleteTaskById(clickedItem._id!!).enqueue(object: BaseCallback<Unit>(){

            override fun onSuccess(data: Unit) {
                super.onSuccess(data)

                todoList.removeAt(position)

                todosAdapter.notifyItemRemoved(position)

                Toast.makeText(requireContext(), "Task ${position + 1} is deleted", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure() {
                super.onFailure()
                Toast.makeText(requireContext(), "Failed to delete the task", Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun scrollToLastPosition(){
        recyclerView.scrollToPosition(todoList.size - 1)
    }


}