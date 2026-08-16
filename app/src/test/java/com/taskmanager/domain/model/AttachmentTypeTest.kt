package com.taskmanager.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AttachmentTypeTest {

    private fun attachment(mime: String) = Attachment(
        taskId = 1, name = "f", mimeType = mime, uri = "content://x", sizeBytes = 0
    )

    @Test
    fun `classifies image types`() {
        assertEquals(AttachmentType.IMAGE, attachment("image/png").type)
        assertEquals(AttachmentType.IMAGE, attachment("IMAGE/JPEG").type)
        assertEquals(AttachmentType.IMAGE, attachment("image/webp").type)
    }

    @Test
    fun `classifies pdf`() {
        assertEquals(AttachmentType.PDF, attachment("application/pdf").type)
    }

    @Test
    fun `classifies audio`() {
        assertEquals(AttachmentType.AUDIO, attachment("audio/mpeg").type)
        assertEquals(AttachmentType.AUDIO, attachment("audio/mp4").type)
    }

    @Test
    fun `classifies video`() {
        assertEquals(AttachmentType.VIDEO, attachment("video/mp4").type)
    }

    @Test
    fun `unknown mime falls back to OTHER`() {
        assertEquals(AttachmentType.OTHER, attachment("application/zip").type)
        assertEquals(AttachmentType.OTHER, attachment("text/plain").type)
    }
}
