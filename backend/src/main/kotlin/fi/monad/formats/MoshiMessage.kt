package fi.monad.formats

import org.http4k.core.Body
import org.http4k.format.Jackson.auto

data class Message(val subject: String, val message: String)

val moshiMessageLens = Body.auto<Message>().toLens()
