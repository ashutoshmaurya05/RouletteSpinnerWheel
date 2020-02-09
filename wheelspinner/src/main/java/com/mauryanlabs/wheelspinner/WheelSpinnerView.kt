package com.mauryanlabs.wheelspinner

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.TimeInterpolator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.graphics.ColorUtils
import com.mauryanlabs.wheelspinner.Utils.drawableToBitmap
import java.util.*

class WheelSpinnerView : View {
    private var mRange = RectF()
    private var mRadius = 0
    private var mArcPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mPrimaryTextPadding = 0
    private var mPrimaryTextSize = 0
    private var mSecondaryTextSize = 0
    private var mRoundOfNumber = 4
    private var mEdgeWidth = -1
    private var isRunning = false
    private var borderColor = 0
    private var defaultBackgroundColor = 0
    private var drawableCenterImage: Drawable? = null
    private var textColor = 0
    private var predeterminedNumber = -1
    var viewRotation = 0f
    var fingerRotation = 0.0
    var downPressTime: Long = 0
    var upPressTime: Long = 0
    var newRotationStore = DoubleArray(3)
    private var mSliceDataList: List<SliceData>? = null
    private var mRotateListener: RotateListener? = null

    interface RotateListener {
        fun rotateDone(index: Int)
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    fun setRotateListener(listener: RotateListener?) {
        mRotateListener = listener
    }

    private fun init() {
        mArcPaint = Paint()
        mArcPaint!!.isAntiAlias = true
        mArcPaint!!.isDither = true
        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        if (textColor != 0) mTextPaint!!.color = textColor
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14f,
            resources.displayMetrics
        )
        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
    }

    val sliceDataListSize: Int
        get() = mSliceDataList!!.size

    fun setData(sliceDataList: List<SliceData>?) {
        mSliceDataList = sliceDataList
        invalidate()
    }

    fun setWheelSpinnerBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setBorderColor(color: Int) {
        borderColor = color
        invalidate()
    }

    fun setPrimaryTextPadding(padding: Int) {
        mPrimaryTextPadding = padding
        invalidate()
    }

    fun setWheelSpinnerCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setPrimaryTextSize(size: Int) {
        mPrimaryTextSize = size
        invalidate()
    }

    fun setSecondaryTextSizeSize(size: Int) {
        mSecondaryTextSize = size
        invalidate()
    }

    fun setBorderWidth(width: Int) {
        mEdgeWidth = width
        invalidate()
    }

    fun setWheelSpinnerTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    private fun drawWheelSpinnerBackgroundWithBitmap(
        canvas: Canvas,
        bitmap: Bitmap
    ) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2, mPadding / 2,
                measuredWidth - mPadding / 2,
                measuredHeight - mPadding / 2
            ), null
        )
    }

    /**
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mSliceDataList == null) {
            return
        }
        drawBackgroundColor(canvas, defaultBackgroundColor)
        init()
        var tmpAngle = mStartAngle
        val sweepAngle = 360f / mSliceDataList!!.size
        for (i in mSliceDataList!!.indices) {
            if (mSliceDataList!![i].sliceColor != 0) {
                mArcPaint!!.style = Paint.Style.FILL
                mArcPaint!!.color = mSliceDataList!![i].sliceColor
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }
            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint!!.style = Paint.Style.STROKE
                mArcPaint!!.color = borderColor
                mArcPaint!!.strokeWidth = mEdgeWidth.toFloat()
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }
            val sliceColor =
                if (mSliceDataList!![i].sliceColor != 0) mSliceDataList!![i].sliceColor else defaultBackgroundColor
            if (!TextUtils.isEmpty(mSliceDataList!![i].primaryText)) drawTopText(
                canvas,
                tmpAngle,
                sweepAngle,
                mSliceDataList!![i].primaryText,
                sliceColor
            )
            if (!TextUtils.isEmpty(mSliceDataList!![i].secondaryText)) drawSecondaryText(
                canvas,
                tmpAngle,
                mSliceDataList!![i].secondaryText,
                sliceColor
            )
            if (mSliceDataList!![i].sliceIcon != 0) drawImage(
                canvas, tmpAngle, BitmapFactory.decodeResource(
                    resources,
                    mSliceDataList!![i].sliceIcon
                )
            )
            tmpAngle += sweepAngle
        }
        drawCenterImage(canvas, drawableCenterImage)
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == 0) return
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = color
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            mCenter - 5.toFloat(),
            mBackgroundPaint!!
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = Math.min(measuredWidth, measuredHeight)
        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        setMeasuredDimension(width, width)
    }

    private fun drawImage(
        canvas: Canvas,
        tmpAngle: Float,
        bitmap: Bitmap
    ) {
        val imgWidth = mRadius / mSliceDataList!!.size
        val angle =
            ((tmpAngle + 360f / mSliceDataList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth / 2, y - imgWidth / 2,
            x + imgWidth / 2, y + imgWidth / 2
        )
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawCenterImage(
        canvas: Canvas,
        drawable: Drawable?
    ) {
        var bitmap = drawableToBitmap(drawable!!)
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            false
        )
        canvas.drawBitmap(
            bitmap, measuredWidth / 2 - bitmap.width / 2.toFloat(),
            measuredHeight / 2 - bitmap.height / 2.toFloat(), null
        )
    }

    private fun isColorDark(color: Int): Boolean {
        val colorValue = ColorUtils.calculateLuminance(color)
        val compareValue = 0.30
        return colorValue <= compareValue
    }

    private fun drawTopText(
        canvas: Canvas,
        tmpAngle: Float,
        sweepAngle: Float,
        mStr: String?,
        backgroundColor: Int
    ) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textAlign = Paint.Align.LEFT
        mTextPaint!!.textSize = mPrimaryTextSize.toFloat()
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset =
            (mRadius * Math.PI / mSliceDataList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mPrimaryTextPadding
        canvas.drawTextOnPath(mStr!!, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint!!)
    }

    private fun drawSecondaryText(
        canvas: Canvas,
        tmpAngle: Float,
        mStr: String?,
        backgroundColor: Int
    ) {
        canvas.save()
        val arraySize = mSliceDataList!!.size
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textSize = mSecondaryTextSize.toFloat()
        mTextPaint!!.textAlign = Paint.Align.LEFT
        val textWidth = mTextPaint!!.measureText(mStr)
        val initFloat = tmpAngle + 360f / arraySize / 2
        val angle = (initFloat * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = RectF(
            x + textWidth, y.toFloat(),
            x - textWidth, y.toFloat()
        )
        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        path.close()
        canvas.rotate(initFloat + arraySize / 18f, x.toFloat(), y.toFloat())
        canvas.drawTextOnPath(
            mStr!!,
            path,
            mPrimaryTextPadding / 7f,
            mTextPaint!!.textSize / 2.75f,
            mTextPaint!!
        )
        canvas.restore()
    }

    private fun getAngleOfIndexTarget(index: Int): Float {
        return 360f / mSliceDataList!!.size * index
    }

    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }

    fun setPredeterminedNumber(predeterminedNumber: Int) {
        this.predeterminedNumber = predeterminedNumber
    }

    fun rotateTo(index: Int) {
        val rand = Random()
        rotateTo(index, rand.nextInt() * 3 % 2, true)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun rotateTo(index: Int, rotation: Int, startSlow: Boolean) {
        if (isRunning) {
            return
        }
        val rotationAssess = if (rotation <= 0) 1 else -1
        //If the staring position is already off 0 degrees, make an illusion that the rotation has smoothly been triggered.
// But this inital animation will just reset the position of the circle to 0 degreees.
        if (getRotation() != 0.0f) {
            setRotation(getRotation() % 360f)
            val animationStart: TimeInterpolator =
                if (startSlow) AccelerateInterpolator() else LinearInterpolator()
            //The multiplier is to do a big rotation again if the position is already near 360.
            val multiplier: Float = if (getRotation() > 200f) 2f else 1.toFloat()
            animate()
                .setInterpolator(animationStart)
                .setDuration(500L)
                .setListener(object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        isRunning = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isRunning = false
                        setRotation(0f)
                        rotateTo(index, rotation, false)
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
                .rotation(360f * multiplier * rotationAssess)
                .start()
            return
        }
        // This addition of another round count for counterclockwise is to simulate the perception of the same number of spin
// if you still need to reach the same outcome of a positive degrees rotation with the number of rounds reversed.
        if (rotationAssess < 0) mRoundOfNumber++
        val targetAngle =
            360f * mRoundOfNumber * rotationAssess + 270f - getAngleOfIndexTarget(index) - 360f / mSliceDataList!!.size / 2
        animate()
            .setInterpolator(DecelerateInterpolator())
            .setDuration(mRoundOfNumber * 100 + 900L)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    isRunning = false
                    setRotation(getRotation() % 360f)
                    if (mRotateListener != null) {
                        mRotateListener!!.rotateDone(index)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            .rotation(targetAngle)
            .start()
    }

    var isTouchEnabled = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isRunning || !isTouchEnabled) {
            return false
        }
        val x = event.x
        val y = event.y
        val xc = width / 2.0f
        val yc = height / 2.0f
        val newFingerRotation: Double
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewRotation = (rotation + 360f) % 360f
                fingerRotation = Math.toDegrees(
                    Math.atan2(
                        x - xc.toDouble(),
                        yc - y.toDouble()
                    )
                )
                downPressTime = event.eventTime
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                newFingerRotation = Math.toDegrees(
                    Math.atan2(
                        x - xc.toDouble(),
                        yc - y.toDouble()
                    )
                )
                if (isRotationConsistent(newFingerRotation)) {
                    rotation = newRotationValue(viewRotation, fingerRotation, newFingerRotation)
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                newFingerRotation = Math.toDegrees(
                    Math.atan2(
                        x - xc.toDouble(),
                        yc - y.toDouble()
                    )
                )
                var computedRotation =
                    newRotationValue(viewRotation, fingerRotation, newFingerRotation)
                fingerRotation = newFingerRotation
                // This computes if you're holding the tap for too long
                upPressTime = event.eventTime
                if (upPressTime - downPressTime > 700L) { // Disregarding the touch since the tap is too slow
                    return true
                }
                // These operators are added so that fling difference can be evaluated
// with usually numbers that are only around more or less 100 / -100.
                if (computedRotation <= -250f) {
                    computedRotation += 360f
                } else if (computedRotation >= 250f) {
                    computedRotation -= 360f
                }
                var flingDiff = computedRotation - viewRotation.toDouble()
                if (flingDiff >= 200 || flingDiff <= -200) {
                    if (viewRotation <= -50f) {
                        viewRotation += 360f
                    } else if (viewRotation >= 50f) {
                        viewRotation -= 360f
                    }
                }
                flingDiff = computedRotation - viewRotation.toDouble()
                if (flingDiff <= -60 ||  //If you have a very fast flick / swipe, you an disregard the touch difference
                    flingDiff < 0 && flingDiff >= -59 && upPressTime - downPressTime <= 200L
                ) {
                    if (predeterminedNumber > -1) {
                        rotateTo(
                            predeterminedNumber,
                            WheelRotationType.COUNTERCLOCKWISE,
                            false
                        )
                    } else {
                        rotateTo(
                            fallBackRandomIndex,
                            WheelRotationType.COUNTERCLOCKWISE,
                            false
                        )
                    }
                }
                if (flingDiff >= 60 ||  //If you have a very fast flick / swipe, you an disregard the touch difference
                    flingDiff > 0 && flingDiff <= 59 && upPressTime - downPressTime <= 200L
                ) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, WheelRotationType.CLOCKWISE, false)
                    } else {
                        rotateTo(
                            fallBackRandomIndex,
                            WheelRotationType.CLOCKWISE,
                            false
                        )
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun newRotationValue(
        originalWheenRotation: Float,
        originalFingerRotation: Double,
        newFingerRotation: Double
    ): Float {
        val computationalRotation = newFingerRotation - originalFingerRotation
        return (originalWheenRotation + computationalRotation.toFloat() + 360f) % 360f
    }

    private val fallBackRandomIndex: Int
        private get() {
            val rand = Random()
            return rand.nextInt(mSliceDataList!!.size - 1) + 0
        }

    /**
     * This detects if your finger movement is a result of an actual raw touch event of if it's from a view jitter.
     * This uses 3 events of rotation temporary storage so that differentiation between swapping touch events can be determined.
     *
     * @param newRotValue
     */
    private fun isRotationConsistent(newRotValue: Double): Boolean {
        if (java.lang.Double.compare(newRotationStore[2], newRotationStore[1]) != 0) {
            newRotationStore[2] = newRotationStore[1]
        }
        if (java.lang.Double.compare(newRotationStore[1], newRotationStore[0]) != 0) {
            newRotationStore[1] = newRotationStore[0]
        }
        newRotationStore[0] = newRotValue
        return if (java.lang.Double.compare(newRotationStore[2], newRotationStore[0]) == 0 ||
            java.lang.Double.compare(newRotationStore[1], newRotationStore[0]) == 0 ||
            java.lang.Double.compare(
                newRotationStore[2],
                newRotationStore[1]
            ) == 0 //Is the middle event the odd one out
            || newRotationStore[0] > newRotationStore[1] && newRotationStore[1] < newRotationStore[2]
            || newRotationStore[0] < newRotationStore[1] && newRotationStore[1] > newRotationStore[2]
        ) {
            false
        } else true
    }

    internal interface WheelRotationType {
        companion object {
            const val CLOCKWISE = 0
            const val COUNTERCLOCKWISE = 1
        }
    }
}