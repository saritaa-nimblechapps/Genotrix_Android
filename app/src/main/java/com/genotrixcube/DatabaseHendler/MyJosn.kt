package com.genotrixcube.DatabaseHendler

import android.content.Context
import com.genotrixcube.util.Utils
import org.json.JSONArray
import org.json.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class MyJosn(val context: Context) {

    companion object{
        val file_name ="records"
        val data ="records_data"
    }

    //create json file
    fun createFileOrExists() : Boolean {

        var isFileExists : Boolean =false
        val directory: File = Utils.getDirectory(context)!!

        if(directory!= null){

            val fileName= file_name

            val f = File(
                directory, fileName + ".json"
            )
          //  f.createNewFile()

            if(f.exists()){

                isFileExists=true
            }
            else{
                isFileExists=true
                f.createNewFile()

                val fileWriter : FileWriter=FileWriter(f)

                //Creating a JSONObject object
                val jsonObject = JSONObject()
                //Creating a json array
                val array = JSONArray()

                jsonObject.put(data, array);

                val userString = jsonObject.toString()


                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(userString);

                bufferedWriter.close()


            }
        }

        return  isFileExists
    }

    fun getFileData(){


    }

    fun checkObjectExists() : Boolean{

        var isExist : Boolean ?= false
        val directory: File = Utils.getDirectory(context)!!

        val fileName= file_name

        val f = File(
            directory, fileName + ".json"
        )


        val parser = JSONParser()
        val main: JSONObject? = null

        try{
            val obj = parser.parse(FileReader(f))
            val jsonObject = obj as JSONObject

            if(jsonObject.has(data)){

                isExist=true
            }

        }catch (e: Exception){
            e.printStackTrace()
        }

        return isExist!!
    }

    fun wirteToJson(){

    }
}