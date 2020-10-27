package com.google.android.material.chip

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.children
import com.google.android.material.R

class ChipGroupSelectionLimit @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.chipGroupStyle
) : ChipGroup(context, attrs, defStyleAttr) {

    /**
     * maximum chip selection 0 no limit
     */
    private var maxLimit=0

    /**
     * minimum chip selection 0 no limit
     */
    private var minLimit=0

    private var isSelectionLocked=false

    private val checkedStateTracker: CheckedStateTracker = CheckedStateTracker()

    private val passThroughListener: PassThroughHierarchyChangeListener = PassThroughHierarchyChangeListener()

    private var ignoreButton=false

    var checkListner:CheckListner?=null

    init {
        super.setOnHierarchyChangeListener(passThroughListener)
        setAttributeSet(attrs)
    }

    private fun setAttributeSet(attrs: AttributeSet? = null)
    {
        val ta = context.obtainStyledAttributes(attrs, com.dadohome.app.R.styleable.ChipGroupSelectionLimit, 0, 0)
        try {
            maxLimit = ta.getInteger(com.dadohome.app.R.styleable.ChipGroupSelectionLimit_maxLimit,0)
            minLimit = ta.getInteger(com.dadohome.app.R.styleable.ChipGroupSelectionLimit_minLimit,0)
            isSelectionLocked = ta.getBoolean(com.dadohome.app.R.styleable.ChipGroupSelectionLimit_selectionLocked,false)
        }
        finally {
            ta.recycle()
        }
    }


    override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener?) {
        super.setOnHierarchyChangeListener(listener)
        passThroughListener.onHierarchyChangeListener = listener
    }

    /**
     * Chip check change listener
     */
    private inner class CheckedStateTracker :
        CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton,isChecked: Boolean)
        {
            if(isSelectionLocked && !ignoreButton)
            {
                ignoreButton=true
                buttonView.isChecked=!isChecked
                ignoreButton=false
                return
            }
            val checkedChipIds = checkedChipIds
            if(maxLimit>0 && isChecked)
            {
                if(checkedChipIds.size>maxLimit)
                {
                    buttonView.isChecked=false
                }
            }
            if(minLimit>0 && !isChecked)
            {
                if(checkedChipIds.size<minLimit)
                {
                    buttonView.isChecked=true
                }
            }
            checkListner?.onCheckChange(buttonView,buttonView.isChecked)
        }
    }

    /**
     * A pass-through listener acts upon the events and dispatches them to another listener. This
     * allows the layout to set its own internal hierarchy change listener without preventing the user
     * to setup theirs.
     */
    private inner class PassThroughHierarchyChangeListener :
        OnHierarchyChangeListener {
        var onHierarchyChangeListener: OnHierarchyChangeListener? =
            null

        override fun onChildViewAdded(
            parent: View,
            child: View
        ) {
            if (parent === this@ChipGroupSelectionLimit && child is Chip) {
                var id = child.getId()
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        View.generateViewId()
                    } else {
                        child.hashCode()
                    }
                    child.setId(id)
                }
                child.setOnCheckedChangeListenerInternal(checkedStateTracker)
            }
            onHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        override fun onChildViewRemoved(
            parent: View,
            child: View
        ) {
            if (parent === this@ChipGroupSelectionLimit && child is Chip) {
                child.setOnCheckedChangeListenerInternal(null)
            }
            onHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }

    fun removeAllChipsView()
    {
        for (chip in children)
        {
//            if(chip is Chip)
//            {
                removeView(chip)
//            }
        }
        for (chip in children)
        {
//            if(chip is Chip)
//            {
                removeView(chip)
//            }
        }
    }

    fun setSelectionLocked(isSelectionLocked:Boolean)
    {
        this.isSelectionLocked=isSelectionLocked
    }

    interface CheckListner{
        fun onCheckChange(buttonView: CompoundButton,isChecked: Boolean)
    }
}