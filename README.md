# RouletteSpinnerWheel

# Usage Example



```xml 
    <com.mauryanlabs.wheelspinner.WheelSpinnerMasterView
        android:id="@+id/wheelSpinnerMasterView"
        android:layout_width="400dp"
        android:layout_height="400dp"
        android:layout_marginStart="10dp"
        android:layout_marginLeft="10dp"
        android:layout_marginTop="5dp"
        android:layout_marginEnd="10dp"
        android:layout_marginRight="10dp"
        app:BackgroundColor="#FF9800"
        app:CenterImage="@drawable/spinner"
        app:Cursor="@drawable/ic_cursor_down"
        app:TopTextColor="#263238"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
        
  ```
  
  ```kotlin
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
  ```

# Screen and Demo

![Example Image](https://github.com/ashutoshmaurya05/RouletteSpinnerWheel/blob/master/Screens/MainUI.png)
![Example Video](https://github.com/ashutoshmaurya05/RouletteSpinnerWheel/blob/master/Screens/demo.mp4)














Used code from repo : https://github.com/thanhniencung/LuckyWheel


