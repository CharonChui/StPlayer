package com.st.stplayer.renderer.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class BufferUtil {
    companion object {
        private const val BYTES_PER_FLOAT = 4
        private const val BYTES_PER_SHORT = 2
        fun getFloatBuffer(array: FloatArray): FloatBuffer {
            val floatBuffer =
                ByteBuffer.allocateDirect(array.size * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
            floatBuffer.put(array)
            floatBuffer.position(0)
            return floatBuffer
        }

        fun getShortBuffer(array: ShortArray): ShortBuffer {
            val shortBuffer =
                ByteBuffer.allocateDirect(array.size * BYTES_PER_SHORT)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
            shortBuffer.put(array)
            shortBuffer.position(0)
            return shortBuffer
        }
    }
}