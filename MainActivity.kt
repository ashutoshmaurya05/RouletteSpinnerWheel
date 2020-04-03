package com.mauryanlabs.roulettespinnerwheel

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mauryanlabs.wheelspinner.SliceData
import com.mauryanlabs.wheelspinner.WheelSpinnerMasterView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setWheelSpinnerData()
    }

    fun setWheelSpinnerData() {
        var sliceAshutosh = SliceData().apply {
            primaryText = "Ashutosh"
            secondaryText = "Maurya"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#9be3de")
        }

        var slicePankaj = SliceData().apply {
            primaryText = "Pankaj"
            secondaryText = "Meel"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#fffdf9")
        }

        var slice3 = SliceData().apply {
            primaryText = "PrimaryTest3"
            secondaryText = "Test3"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#ffd369")
        }

        var slice4 = SliceData().apply {
            primaryText = "PrimaryTest4"
            secondaryText = "Test4"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#9aceff")
        }

        var slice5 = SliceData().apply {
            primaryText = "PrimaryTest5"
            secondaryText = "Test5"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#c3f584")
        }

        var slice6 = SliceData().apply {
            primaryText = "PrimaryTest6"
            secondaryText = "Test6"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#c9d1d3")
        }

        var slice7 = SliceData().apply {
            primaryText = "PrimaryTest7"
            secondaryText = "Test7"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#f4efd3")
        }

        var slice8 = SliceData().apply {
            primaryText = "PrimaryTest8"
            secondaryText = "Test8"
            secondaryTextOrientation = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
            sliceColor = Color.parseColor("#f4efd3")
        }

        val listOfSlice: MutableList<SliceData> = arrayListOf()

        listOfSlice.add(slicePankaj)
        listOfSlice.add(slice3)
        listOfSlice.add(sliceAshutosh)
        listOfSlice.add(slice4)
        listOfSlice.add(slice6)
        listOfSlice.add(slice7)
        listOfSlice.add(slice5)
        listOfSlice.add(slice8)


        wheelSpinnerMasterView.setData(listOfSlice)
        wheelSpinnerMasterView.setPredeterminedNumber(6)

        play.setOnClickListener {
            wheelSpinnerMasterView.setRound(Random().nextInt(50))
            //listOfSlice.shuffle()
            //wheelSpinner.setData(listOfSlice)
            wheelSpinnerMasterView.startWheelSpinnerWithTargetIndex(4)
        }


        val listener = object : WheelSpinnerMasterView.SliceSelectedListener {
            override fun selectedSlice(index: Int) {
                Toast.makeText(
                    applicationContext,
                    "Selected Slice is: " + listOfSlice.get(index).primaryText,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        wheelSpinnerMasterView.setSliceSelectedListener(listener)
    }
}