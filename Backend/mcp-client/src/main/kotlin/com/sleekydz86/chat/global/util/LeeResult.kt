package com.sleekydz86.chat.global.util

data class LeeResult(
    var status: Int? = null,
    var msg: String? = null,
    var data: Any? = null,
    var ok: String? = null
) {
    fun isOK(): Boolean = status == 200

    companion object {
        fun build(status: Int, msg: String, data: Any?): LeeResult {
            return LeeResult(status, msg, data)
        }

        fun build(status: Int, msg: String, data: Any?, ok: String?): LeeResult {
            return LeeResult(status, msg, data, ok)
        }

        fun ok(data: Any?): LeeResult {
            return LeeResult(200, "OK", data)
        }

        fun ok(): LeeResult {
            return LeeResult(200, "OK", null)
        }

        fun errorMsg(msg: String): LeeResult {
            return LeeResult(500, msg, null)
        }

        fun errorUserTicket(msg: String): LeeResult {
            return LeeResult(557, msg, null)
        }

        fun errorMap(data: Any?): LeeResult {
            return LeeResult(501, "error", data)
        }

        fun errorTokenMsg(msg: String): LeeResult {
            return LeeResult(502, msg, null)
        }

        fun errorException(msg: String): LeeResult {
            return LeeResult(555, msg, null)
        }

        fun errorUserQQ(msg: String): LeeResult {
            return LeeResult(556, msg, null)
        }
    }
}
