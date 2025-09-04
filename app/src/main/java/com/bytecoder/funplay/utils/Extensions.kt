package com.bytecoder.funplay.utils

import android.view.View
fun View.show(b: Boolean) { visibility = if (b) View.VISIBLE else View.GONE }
