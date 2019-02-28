package com.lightningkite.mirror.server.test

import com.lightningkite.mirror.info.ThrowsTypes
import com.lightningkite.mirror.request.Request

@ThrowsTypes("ForbiddenException")
class ThrowExceptionRequest() : Request<Unit>