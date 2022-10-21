/*
 * Lunar Launcher
 * Copyright (C) 2022 Md Rasel Hossain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rasel.lunar.launcher.todos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf
import rasel.lunar.launcher.LauncherActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.TodoDialogBinding
import rasel.lunar.launcher.databinding.TodoManagerBinding
import java.util.*


internal class TodoManager : Fragment() {

    private lateinit var binding: TodoManagerBinding
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var databaseHandler: DatabaseHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = TodoManagerBinding.inflate(inflater, container, false)

        /* set window insets */
        setInsets()

        fragmentActivity = if (isAdded) {
            requireActivity()
        } else {
            LauncherActivity()
        }

        databaseHandler = DatabaseHandler(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* click listeners for add new and delete all buttons */
        binding.addNew.setOnClickListener { addNewDialog() }
        binding.deleteAll.setOnClickListener { deleteAllDialog() }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun setInsets() {
        Insetter.builder()
            .paddingTop(windowInsetTypesOf(statusBars = true))
            .applyToView(binding.todos)
        Insetter.builder()
            .marginBottom(windowInsetTypesOf(navigationBars = true))
            .applyToView(binding.addNew)
            .applyToView(binding.deleteAll)
    }

    fun refreshList() {
        binding.todos.adapter =
            context?.let { TodoAdapter(databaseHandler.todos, this, fragmentActivity, it) }
    }

    /* add new dialog */
    private fun addNewDialog() {
        val bottomSheetDialog = BottomSheetDialog(fragmentActivity)
        val dialogBinding = TodoDialogBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        dialogBinding.deleteAllConfirmation.visibility = View.GONE
        /* automatic keyboard popup */
        dialogBinding.todoInput.requestFocus()
        val inputMethodManager = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(dialogBinding.todoInput, InputMethodManager.SHOW_IMPLICIT)

        /* dismiss the dialog on cancel button click */
        dialogBinding.todoCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        /* add new item to the database */
        dialogBinding.todoOk.setOnClickListener {
            val todo = Todo()
            val todoString = Objects.requireNonNull(dialogBinding.todoInput.text).toString().trim { it <= ' ' }
            if (todoString.isNotEmpty()) {
                todo.name = todoString
                databaseHandler.addTodo(todo)
                bottomSheetDialog.dismiss()
                refreshList()
            } else {
                dialogBinding.todoInput.error = getString(R.string.empty_text_field)
            }
        }
    }

    /* delete all dialog */
    private fun deleteAllDialog() {
        val bottomSheetDialog = BottomSheetDialog(fragmentActivity)
        val dialogBinding = TodoDialogBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        /* if any item does not exist, then disable the ok button */
        if (!databaseHandler.isTodoExists) {
            dialogBinding.todoOk.isEnabled = false
        }

        dialogBinding.todoInput.visibility = View.GONE
        dialogBinding.todoOk.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))

        /* dismiss the dialog on cancel button click */
        dialogBinding.todoCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        /* delete all the existing items from the database */
        dialogBinding.todoOk.setOnClickListener {
            databaseHandler.deleteAll()
            bottomSheetDialog.dismiss()
            refreshList()
        }
    }

}