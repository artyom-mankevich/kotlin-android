package com.example.enrolleeslist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var enrollees: ArrayList<Enrollee> = arrayListOf()
    private var origEnrollees: ArrayList<Enrollee> = arrayListOf()
    private lateinit var enrolleesListView: ListView
    private lateinit var editText: EditText
    private val pickJsonFileCode = 101
    private val pickerInitialUri = "com.android.externalstorage.documents"

    companion object {
        lateinit var adapter: EnrolleeAdapter
    }

    private lateinit var switch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setInitialData()

        enrolleesListView = findViewById(R.id.enrolleesList)

        adapter = EnrolleeAdapter(this, R.layout.list_item, enrollees)
        enrolleesListView.adapter = adapter

        editText = findViewById(R.id.searchCityEditView)
        editText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    adapter.clear()
                    adapter.addAll(getFiltered(origEnrollees))
                    adapter.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }
        registerForContextMenu(enrolleesListView)

        switch = findViewById(R.id.filterSwitch)
        switch.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            adapter.clear()
            if (isChecked) {
                adapter.addAll(getFiltered(origEnrollees))
                adapter.notifyDataSetChanged()
            } else {
                adapter.addAll(origEnrollees)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openFileButton -> {
                openFileExplorer()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openFileExplorer() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, pickJsonFileCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickJsonFileCode && resultCode == RESULT_OK) {
            val selectedFile: Uri = data!!.data!!
            readJsonFile(selectedFile)
        }
    }

    private fun readJsonFile(fileUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(fileUri)
            val fileText = inputStream!!.bufferedReader().use(BufferedReader::readText)
            val mapper = jacksonObjectMapper()
            val objList: ArrayList<Enrollee> = mapper.readValue(fileText)
            adapter.clear()
            adapter.addAll(objList)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.fileError), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        origEnrollees = enrollees.toMutableList() as ArrayList<Enrollee>
        super.onResume()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.editOption -> {
                editListItem(info.id)
                true
            }
            R.id.deleteOption -> {
                deleteListItem(info.id)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun editListItem(id: Long) {
        toAddEditActivity(enrollees[id.toInt()])
    }

    private fun deleteListItem(id: Long) {
        origEnrollees.remove(enrollees[id.toInt()])
        adapter.remove(enrollees[id.toInt()])
        adapter.notifyDataSetChanged()
    }


    private fun setInitialData() {
        enrollees.add(Enrollee("Иван", "Иванов", "Минск", arrayListOf(4, 4, 5), "Иванович"))
        enrollees.add(Enrollee("Джон", "Смит", "Вашингтон", arrayListOf(5, 4, 5)))
        enrollees.add(Enrollee("Андрей", "Смирнов", "Солигорск", arrayListOf(4, 5, 5)))
        enrollees.add(Enrollee("Артём", "Манкевич", "Минск", arrayListOf(5, 5, 5), "Олегович"))
        enrollees.add(Enrollee("Константин", "Калиновский", "Минск", arrayListOf(5, 5, 5)))
    }

    private fun getCountWithAvgHigher(list: ArrayList<Enrollee>): Int {
        return list.count { enrollee -> enrollee.getAverageGrade() >= 4.5 }
    }

    private fun getFiltered(list: ArrayList<Enrollee>): ArrayList<Enrollee> {
        var filteredList: ArrayList<Enrollee> = arrayListOf()
        if (editText.text.isNotEmpty()) {
            filteredList = if (switch.isChecked) {
                list.filterTo(filteredList)
                { enrollee ->
                    enrollee.city == editText.text.toString() && enrollee.getAverageGrade() >= 4.5
                }
            } else {
                list.filterTo(filteredList)
                { enrollee ->
                    enrollee.city == editText.text.toString()
                }
            }
            filteredList.sortBy { it.secondName }
            return filteredList
        } else if (editText.text.isEmpty() && switch.isChecked) {
            filteredList = list.filterTo(filteredList) { enrollee ->
                enrollee.getAverageGrade() >= 4.5
            }
            filteredList.sortBy { it.secondName }
            return filteredList
        } else return list
    }

    fun countButtonOnClick(view: View) {
        val text = "${getString(R.string.enrolleesCount)}  ${getCountWithAvgHigher(enrollees)}"
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    fun addOnClick(view: View) {
        toAddEditActivity()
    }

    private fun toAddEditActivity(enrollee: Enrollee? = null) {
        val intent = Intent(this, AddEditActivity::class.java)
        if (enrollee != null) {
            intent.putExtra("enrollee", enrollee)
            origEnrollees.remove(enrollee)
            adapter.remove(enrollee)
        }
        startActivity(intent)
    }
}