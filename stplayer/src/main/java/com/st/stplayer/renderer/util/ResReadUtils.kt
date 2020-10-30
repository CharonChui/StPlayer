package com.st.stplayer.renderer.util

import android.content.Context
import android.content.res.Resources.NotFoundException
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ResReadUtils {
    companion object {
        /**
         * 读取资源
         * @param resourceId
         */
        fun readResource(
            context: Context,
            @RawRes resourceId: Int
        ): String? {
            val builder = StringBuilder()
            try {
                val inputStream =
                    context.resources.openRawResource(resourceId)
                val streamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(streamReader)
                var textLine: String?
                while (bufferedReader.readLine().also { textLine = it } != null) {
                    builder.append(textLine)
                    builder.append("\n")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
            return builder.toString()
        }
    }
}