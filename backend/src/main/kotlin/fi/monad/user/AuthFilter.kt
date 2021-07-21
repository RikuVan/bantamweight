package fi.monad.user

import com.github.michaelbull.result.fold
import org.http4k.core.Filter
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

fun AuthFilter(tokenHandler: TokenHandler, roles: List<String> = listOf("admin", "customer")): Filter {
    return Filter { next ->
        { req ->
            req.accessToken?.let {
                tokenHandler.readAccessToken(it).fold(
                    { tokenContent ->
                        when(tokenContent) {
                            is Payload -> {
                                if (tokenContent.roles.intersect(roles).isNotEmpty()) {
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
