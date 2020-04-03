package com.mauryanlabs.wheelspinner

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mauryanlabs.wheelspinner.Utils.convertDpToPixel
import com.mauryanlabs.wheelspinner.WheelSpinnerView.RotateListener
import java.util.*

class WheelSpinnerMasterView : RelativeLayout, RotateListener {
    private var mBackgroundColor = 0
    private var mTextColor = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mBorderColor = 0
    private var mTopTextPadding = 0
    private var mEdgeWidth = 0
    private var mCenterImage: Drawable? = null
    private var mCursorImage: Drawable? = null
    private var wheelSpinnerView: WheelSpinnerView? = null
    private var ivCursorView: ImageView? = null
    private var mSliceSelectedListener: SliceSelectedListener? = null
    override fun rotateDone(index: Int) {
        if (mSliceSelectedListener != null) {
            mSliceSelectedListener!!.selectedSlice(index)
        }
    }

    interface SliceSelectedListener {
        fun selectedSlice(index: Int)
    }

    fun setSliceSelectedListener(listener: SliceSelectedListener) {
        mSliceSelectedListener = listener
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    var wrappedDrawable: DrawableWrapper? = null
    private fun init(ctx: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray =
                ctx.obtainStyledAttributes(attrs, R.styleable.WheelSpinnerAttributes)
            mBackgroundColor =
                typedArray.getColor(R.styleable.WheelSpinnerAttributes_BackgroundColor, -0x340000)
            mTopTextSize = typedArray.getDimensionPixelSize(
                R.styleable.WheelSpinnerAttributes_TopTextSize,
                convertDpToPixel(10f, context).toInt()
            )
            mSecondaryTextSize = typedArray.getDimensionPixelSize(
                R.styleable.WheelSpinnerAttributes_SecondaryTextSize,
                convertDpToPixel(20f, context).toInt()
            )
            mTextColor = typedArray.getColor(R.styleable.WheelSpinnerAttributes_TopTextColor, 0)
            mTopTextPadding = typedArray.getDimensionPixelSize(
                R.styleable.WheelSpinnerAttributes_TopTextPadding,
                convertDpToPixel(10f, context).toInt()
            ) + convertDpToPixel(10f, context).toInt()
            mCursorImage = typedArray.getDrawable(R.styleable.WheelSpinnerAttributes_Cursor)
            mCenterImage = typedArray.getDrawable(R.styleable.WheelSpinnerAttributes_CenterImage)
            mEdgeWidth = typedArray.getInt(R.styleable.WheelSpinnerAttributes_EdgeWidth, 10)
            mBorderColor = typedArray.getColor(
                R.styleable.WheelSpinnerAttributes_EdgeColor,
                Color.parseColor("#263f44")
            )
            //Bitmap bitmap = ((BitmapDrawable) mCenterImage).getBitmap();
// Scale it to 50 x 50
//drawableCenter = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
            wrappedDrawable = DrawableWrapper(mCenterImage!!)
            // set bounds on wrapper
            wrappedDrawable!!.setBounds(0, 0, 150, 150)
            // use wrapped drawable
            typedArray.recycle()
        }
        val inflater = LayoutInflater.from(context)
        val frameLayout =
            inflater.inflate(R.layout.wheel_spinner_view, this, false) as FrameLayout
        wheelSpinnerView = frameLayout.findViewById(R.id.wheelSpinnerView)
        ivCursorView = frameLayout.findViewById(R.id.cursorView)
        wheelSpinnerView!!.setRotateListener(this)
        wheelSpinnerView!!.setWheelSpinnerBackgroundColor(mBackgroundColor)
        wheelSpinnerView!!.setPrimaryTextPadding(mTopTextPadding)
        wheelSpinnerView!!.setPrimaryTextSize(mTopTextSize)
        wheelSpinnerView!!.setSecondaryTextSizeSize(mSecondaryTextSize)
        wheelSpinnerView!!.setWheelSpinnerCenterImage(wrappedDrawable)
        wheelSpinnerView!!.setBorderColor(mBorderColor)
        wheelSpinnerView!!.setBorderWidth(5)
        if (mTextColor != 0) wheelSpinnerView!!.setWheelSpinnerTextColor(mTextColor)
        ivCursorView!!.setImageDrawable(mCursorImage)
        addView(frameLayout)
    }

    var isTouchEnabled: Boolean
        get() = wheelSpinnerView!!.isTouchEnabled
        set(touchEnabled) {
            wheelSpinnerView!!.isTouchEnabled = touchEnabled
        }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean { //This is to control that the touch events triggered are only going to the Wheel Spinner View
        for (i in 0 until childCount) {
            if (isWheelSpinnerView(getChildAt(i))) {
                return super.dispatchTouchEvent(ev)
            }
        }
        return false
    }

    private fun isWheelSpinnerView(view: View): Boolean {
        if (view is ViewGroup) {
            for (i in 0 until childCount) {
                if (isWheelSpinnerView(view.getChildAt(i))) {
                    return true
                }
            }
        }
        return view is WheelSpinnerView
    }

    fun setWheelSpinnerBackgrouldColor(color: Int) {
        wheelSpinnerView!!.setWheelSpinnerBackgroundColor(color)
    }

    fun setWheelSpinnerCursorImage(drawable: Int) {
        ivCursorView!!.setBackgroundResource(drawable)
    }

    fun setWheelSpinnerCenterImage(drawable: Drawable?) {
        wheelSpinnerView!!.setWheelSpinnerCenterImage(drawable)
    }

    fun setBorderColor(color: Int) {
        wheelSpinnerView!!.setBorderColor(color)
    }

    fun setWheelSpinnerTextColor(color: Int) {
        wheelSpinnerView!!.setWheelSpinnerTextColor(color)
    }

    fun setData(data: MutableList<SliceData>) {
        wheelSpinnerView!!.setData(data)
    }

    fun setRound(numberOfRound: Int) {
        wheelSpinnerView!!.setRound(numberOfRound)
    }

    fun setPredeterminedNumber(fixedNumber: Int) {
        wheelSpinnerView!!.setPredeterminedNumber(fixedNumber)
    }

    fun startWheelSpinnerWithTargetIndex(index: Int) {
        wheelSpinnerView!!.rotateTo(index)
    }

    fun startWheelSpinnerWithRandomTarget() {
        val r = Random()
        wheelSpinnerView!!.rotateTo(r.nextInt(wheelSpinnerView!!.sliceDataListSize - 1))
    }
}