package com.viewcompose.graphics.core

import org.junit.Assert.assertEquals
import org.junit.Test

class DrawRecorderTest {
    @Test
    fun `recorder emits expected commands`() {
        val recorder = DrawRecorder()
        val paint = DrawPaint(
            brush = Brush.SolidColor(0xFFFF0000.toInt()),
        )
        recorder.save()
        recorder.translate(4f, 8f)
        recorder.drawRect(
            rect = Rect(0f, 0f, 20f, 10f),
            paint = paint,
        )
        recorder.restore()

        val commands = recorder.toCommands()
        assertEquals(4, commands.size)
        assertEquals(DrawCommand.Save, commands[0])
        assertEquals(DrawCommand.Translate(4f, 8f), commands[1])
        assertEquals(
            DrawCommand.DrawRect(
                rect = Rect(0f, 0f, 20f, 10f),
                paint = paint,
            ),
            commands[2],
        )
        assertEquals(DrawCommand.Restore, commands[3])
    }
}
