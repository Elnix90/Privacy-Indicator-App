package com.nitish.privacyindicator.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.nitish.privacyindicator.R
import com.nitish.privacyindicator.databinding.ContentCustomizationBinding
import com.nitish.privacyindicator.databinding.FragmentCustomizationBinding
import com.nitish.privacyindicator.helpers.setViewTint
import com.nitish.privacyindicator.helpers.updateOpacity
import com.nitish.privacyindicator.helpers.updateSize
import com.nitish.privacyindicator.models.IndicatorOpacity
import com.nitish.privacyindicator.models.IndicatorPosition
import com.nitish.privacyindicator.models.IndicatorSize


class CustomizationFragment : Fragment(R.layout.fragment_customization) {

    lateinit var binding: FragmentCustomizationBinding

    lateinit var viewModel: HomeViewModel

    lateinit var customizationBinding: ContentCustomizationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCustomizationBinding.bind(view)

        customizationBinding = binding.contentCustomization
        viewModel = (activity as HomeActivity).viewModel

        setUpView()
        setUpObservers()
        setUpListeners()
    }

    private fun setUpView() {
        // Fix selectedTab → checkedButtonId mapping (0=first, 1=second, etc.)
        when (viewModel.indicatorPosition.value!!.vertical) {
            0 -> customizationBinding.multiSwitchVerticalHeight.check(R.id.tab_vertical_small)
            1 -> customizationBinding.multiSwitchVerticalHeight.check(R.id.tab_vertical_medium)
            2 -> customizationBinding.multiSwitchVerticalHeight.check(R.id.tab_vertical_large)
        }

        when (viewModel.indicatorPosition.value!!.horizontal) {
            0 -> customizationBinding.multiSwitchHorizontalHeight.check(R.id.tab_horizontal_left)
            1 -> customizationBinding.multiSwitchHorizontalHeight.check(R.id.tab_horizontal_center)
            2 -> customizationBinding.multiSwitchHorizontalHeight.check(R.id.tab_horizontal_right)
        }

        when (viewModel.indicatorSize.value!!.ordinal) {
            0 -> customizationBinding.multiSwitchSize.check(R.id.tab_size_tiny)
            1 -> customizationBinding.multiSwitchSize.check(R.id.tab_size_small)
            2 -> customizationBinding.multiSwitchSize.check(R.id.tab_size_medium)
            3 -> customizationBinding.multiSwitchSize.check(R.id.tab_size_large)
        }

        when (viewModel.indicatorOpacity.value!!.ordinal) {
            0 -> customizationBinding.multiSwitchOpacity.check(R.id.tab_opacity_low)
            1 -> customizationBinding.multiSwitchOpacity.check(R.id.tab_opacity_medium)
            2 -> customizationBinding.multiSwitchOpacity.check(R.id.tab_opacity_high)
        }
    }

    private fun setUpObservers() {
        viewModel.indicatorForegroundColor.observe(viewLifecycleOwner) {
            customizationBinding.tileForeGround.setViewTint(it)
            binding.indicatorsLayout.ivCam.setViewTint(it)
            binding.indicatorsLayout.ivMic.setViewTint(it)
            binding.indicatorsLayout.ivLoc.setViewTint(it)
        }

        viewModel.indicatorBackgroundColor.observe(viewLifecycleOwner) {
            customizationBinding.tileBackGround.setViewTint(it)
            binding.indicatorsLayout.llBackground.setBackgroundColor(it.toColorInt())
        }

        viewModel.indicatorSize.observe(viewLifecycleOwner) {
            binding.indicatorsLayout.ivCam.updateSize(it.size)
            binding.indicatorsLayout.ivMic.updateSize(it.size)
            binding.indicatorsLayout.ivLoc.updateSize(it.size)
        }

        viewModel.indicatorOpacity.observe(viewLifecycleOwner) {
            binding.indicatorsLayout.root.updateOpacity(it.opacity)
        }
    }

    private fun setUpListeners() {
        customizationBinding.tileForeGround.setOnClickListener {
            MaterialColorPickerDialog.Builder(requireContext())
                .setTitle("Indicator Foreground Color")
                .setColorShape(ColorShape.SQAURE)
                .setColorSwatch(ColorSwatch._200)
                .setDefaultColor(viewModel.indicatorForegroundColor.value!!)
                .setColorListener { _, colorHex ->
                    viewModel.setIndicatorForegroundColor(colorHex)
                }
                .show()
        }

        customizationBinding.tileBackGround.setOnClickListener {
            MaterialColorPickerDialog.Builder(requireContext())
                .setTitle("Indicator Background Color")
                .setColorShape(ColorShape.SQAURE)
                .setColorSwatch(ColorSwatch._900)
                .setDefaultColor(viewModel.indicatorBackgroundColor.value!!)
                .setColorListener { _, colorHex ->
                    viewModel.setIndicatorBackgroundColor(colorHex)
                }
                .show()
        }

        // Vertical position toggle
        customizationBinding.multiSwitchVerticalHeight.addOnButtonCheckedListener { _, checkedId, _ ->
            val verticalPos = when (checkedId) {
                R.id.tab_vertical_small -> 0
                R.id.tab_vertical_medium -> 1
                R.id.tab_vertical_large -> 2
                else -> 0
            }
            val horizontalPos = getHorizontalPosition()
            viewModel.setIndicatorPosition(IndicatorPosition.getIndicatorPosition(verticalPos, horizontalPos))
        }

        // Horizontal position toggle
        customizationBinding.multiSwitchHorizontalHeight.addOnButtonCheckedListener { _, checkedId, _ ->
            val horizontalPos = when (checkedId) {
                R.id.tab_horizontal_left -> 0
                R.id.tab_horizontal_center -> 1
                R.id.tab_horizontal_right -> 2
                else -> 0
            }
            val verticalPos = getVerticalPosition()
            viewModel.setIndicatorPosition(IndicatorPosition.getIndicatorPosition(verticalPos, horizontalPos))
        }

        // Size toggle
        customizationBinding.multiSwitchSize.addOnButtonCheckedListener { _, checkedId, _ ->
            val sizeOrdinal = when (checkedId) {
                R.id.tab_size_tiny -> 0
                R.id.tab_size_small -> 1
                R.id.tab_size_medium -> 2
                R.id.tab_size_large -> 3
                else -> 1
            }
            viewModel.setIndicatorSize(IndicatorSize.values()[sizeOrdinal])
        }

        // Opacity toggle
        customizationBinding.multiSwitchOpacity.addOnButtonCheckedListener { _, checkedId, _ ->
            val opacityOrdinal = when (checkedId) {
                R.id.tab_opacity_low -> 0
                R.id.tab_opacity_medium -> 1
                R.id.tab_opacity_high -> 2
                else -> 1
            }
            viewModel.setIndicatorOpacity(IndicatorOpacity.values()[opacityOrdinal])
        }
    }

    private fun getVerticalPosition(): Int {
        return when (customizationBinding.multiSwitchVerticalHeight.checkedButtonId) {
            R.id.tab_vertical_small -> 0
            R.id.tab_vertical_medium -> 1
            R.id.tab_vertical_large -> 2
            else -> 0
        }
    }

    private fun getHorizontalPosition(): Int {
        return when (customizationBinding.multiSwitchHorizontalHeight.checkedButtonId) {
            R.id.tab_horizontal_left -> 0
            R.id.tab_horizontal_center -> 1
            R.id.tab_horizontal_right -> 2
            else -> 0
        }
    }

}
