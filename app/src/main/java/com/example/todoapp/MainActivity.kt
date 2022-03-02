package com.example.todoapp

import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.AppDatabase.Companion.getInstance
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    val list = arrayListOf<ToDoModel>()
    var adapter = TodoAdapter(list)

    val db by lazy {
        getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        rvTodo.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        initSwipe()

        db.todoDao().getTask().observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            } else {
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })

    }

    private fun initSwipe() {
        val simpleItemTouch = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val view = viewHolder.itemView

                    val paint = Paint()
                    val icon : Bitmap

                    if(dX > 0) {
                        icon = BitmapFactory.decodeResource(resources,R.mipmap.check)

                        paint.color = Color.parseColor("#388E3C")

                        val left = view.left.toFloat() + convertDptoPx(16)
                        val top = view.top.toFloat() + (view.bottom.toFloat() - view.top.toFloat() - icon.height)/2

                        canvas.drawRect(
                                view.left.toFloat(),view.top.toFloat(),dX,view.bottom.toFloat(),paint
                        )

                        canvas.drawBitmap(
                                icon,left,top,paint
                        )
                    }
                    else {
                        icon = BitmapFactory.decodeResource(resources,R.mipmap.delete)

                        paint.color = Color.parseColor("#D32F2F")

                        val left = view.right.toFloat() - icon.width - convertDptoPx(16)
                        val top = view.top.toFloat() + (view.bottom.toFloat() - view.top.toFloat() - icon.height)/2

                        canvas.drawRect(
                                view.right.toFloat() + dX,view.top.toFloat(),view.right.toFloat(),view.bottom.toFloat(),paint
                        )

                        canvas.drawBitmap(
                                icon,left,top,paint
                        )
                    }
                    view.translationX = dX
                }

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition

                if(direction == ItemTouchHelper.RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().finishTask(adapter.getItemId(position))

                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity,"Item Completed",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else if(direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(adapter.getItemId(position))

                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity,"Item Deleted",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouch)
        itemTouchHelper.attachToRecyclerView(rvTodo)
    }

    private fun convertDptoPx(dp: Int): Int {
        return (dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt();
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.searchMenu)
        val searchView = item.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displayTodo()
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayTodo(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    fun displayTodo(newText: String = "") {
        db.todoDao().getTask().observe(this, Observer {
            if(it.isNotEmpty()){
                list.clear()
                list.addAll(
                        it.filter { todo ->
                            todo.title.contains(newText,true)
                        }
                )
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.historyMenu -> {
                startActivity(Intent(this,HistoryActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun openNewTask(view: View) {
        startActivity(Intent(this,TaskActivity::class.java))
    }
}