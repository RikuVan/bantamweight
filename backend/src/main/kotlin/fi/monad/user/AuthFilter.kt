package fi.monad.user

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import org.http4k.core.Filter
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

// pass lambda to filter to check if user can access route based on parsed accessToken and req
val isAdmin = { payload: Payload, req: Request -> payload.roles.contains("admin") }
val all = { payload: Payload, req: Request -> true }

fun AuthFilter(tokenHandler: TokenHandler, predicate: (payload: Payload, req: Request) -> Boolean = all): Filter {
    return Filter { next ->
        { req ->
            req.accessToken?.let {
                tokenHandler.readAccessToken(it).fold(
                    { tokenContent ->
                        when(tokenContent) {
                            is Payload -> {
                                if (predicate(tokenContent, req)) {
                                    next(req)
                                } else {
                                    Response(Status.UNAUTHORIZED)
                                }

                            }
                            else -> Response(Status.FORBIDDEN)
                        }
                    },
                    {
                        Response(Status.FORBIDDEN)
                    }
                )
            } ?: Response(Status.FORBIDDEN)
        }
    }
}


val Request.accessToken: String? get() = header(ACCESS_TOKEN_HEADER)?.trim()

fun Result<Token, Throwable>.filterAdmin() = map {
    println(it)
    when(it) {
        is Payload -> it.roles.contains("admin")
        else       -> throw Exception("Not admin")
    }
}
