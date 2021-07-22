package fi.monad.user

import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.fold
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.replaceCookie
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind


val usersResponseLens = Body.auto<List<UserListItemOut>>().toLens()
val loginRequestLens = Body.auto<Credentials>().toLens()
val loginResponseLens = Body.auto<UserOut>().toLens()
val errorResponseLens = Body.auto<ErrorDetails>().toLens()
val passwordChangeLens = Body.auto<NewPasswordIn>().toLens()


data class ErrorDetails(val message: String?)

/*
  auth handlers
  TODO: break out each handler into a file
 */

fun UserRoutes(deps: UserDependencies = object : UserDependencies {}): RoutingHttpHandler {

    val users: HttpHandler = { request ->
        deps.userRepository.fetchAll().fold(
            { users ->
                Response(Status.OK).with(
                    usersResponseLens of users.toDTO()
                )
            },
            {
                Response(Status.NOT_FOUND).with(
                    errorResponseLens of ErrorDetails(it.toString())
                )
            }
        )

    }

    val login: HttpHandler = { request ->
        deps.authService.login(loginRequestLens(request)).fold(
            { loginResult ->
                when(loginResult) {
                    is UserOk               -> Response(Status.OK).with(
                        loginResponseLens of loginResult.user
                    ).cookie(loginResult.refreshCookie)
                    is UserNotFoundForLogin -> Response(Status.NOT_FOUND)
                    is InvalidCredentials   -> Response(Status.UNAUTHORIZED)
                }

            },
            { err ->
                Response(Status.FORBIDDEN).with(
                    errorResponseLens of ErrorDetails(err.toString())
                )
            }
        )
    }

    val logout: HttpHandler = { request ->
        Response(Status.OK).replaceCookie(deps.authService.logout().invalidatedCookie)
    }

    val refresh: HttpHandler = { request ->
        val token = request.cookie(REFRESH_TOKEN_NAME)?.value
        if (token == null) {
            Response(Status.UNAUTHORIZED)
        } else {
            deps.authService.refresh(token).fold(
                { refreshResult ->
                    when(refreshResult) {
                        is UserRefreshed          -> {
                            Response(Status.OK).with(
                                loginResponseLens of refreshResult.user
                            ).replaceCookie(refreshResult.refreshCookie)
                        }
                        is UserNotFoundForRefresh -> Response(Status.NOT_FOUND)
                    }
                },
                { err ->
                    Response(Status.FORBIDDEN).with(
                        errorResponseLens of ErrorDetails(err.toString())
                    )
                }
            )
        }
    }

    val passwordChange: HttpHandler = { request ->
        val changeReq = passwordChangeLens(request)
        deps.authService.verify(changeReq.email, changeReq.oldPassword ?: "").flatMap {
            deps.userRepository.updatePassword(
                deps.passwordEncryption.encryptPassword(changeReq.newPassword),
                changeReq.email
            )
        }.fold(
            { updated ->
                if (updated) Response(Status.OK) else Response(Status.INTERNAL_SERVER_ERROR)
            },
            { Response(Status.BAD_REQUEST) }

        )
    }

    return "/users" bind routes(
        "" bind Method.GET to AuthFilter(deps.tokenHandler, isAdmin).then(users),
        "/login" bind Method.POST to login,
        "/logout" bind Method.POST to logout,
        "/refresh" bind Method.POST to refresh,
        "/password-change" bind Method.POST to AuthFilter(deps.tokenHandler) { payload: Payload, req: Request ->
            isAdmin(payload, req) || passwordChangeLens(req).email == payload.email
        }.then(passwordChange)
    )
}