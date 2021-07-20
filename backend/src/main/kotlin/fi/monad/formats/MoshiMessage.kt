package fi.monad.formats

import org.http4k.core.Body
import org.http4k.format.Moshi.auto

data class MoshiMessage(val subject: String, val message: String)

val moshiMessageLens = Body.auto<MoshiMessage>().toLens()
