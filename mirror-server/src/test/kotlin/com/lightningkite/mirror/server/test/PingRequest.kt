package com.lightningkite.mirror.server.test

import com.lightningkite.mirror.info.ThrowsTypes
import com.lightningkite.mirror.request.Request

class PingRequest(val name: String) : Request<String>