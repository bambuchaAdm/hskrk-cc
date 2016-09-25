package pl.hskrk.cc.auth

import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.Directive
import akka.http.scaladsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.server.directives.BasicDirectives._
import akka.http.scaladsl.server.directives.FutureDirectives._
import akka.http.scaladsl.server.directives.RouteDirectives._

import scala.concurrent.Future

/**
  * Created by bambucha on 25.09.16.
  */
trait AuthenticationDirectives {

  type Authenticator[T] = RequestContext => Option[T]

  type AsyncAuthenticator[T] = RequestContext => Future[Option[T]]


  def authenticate[T](authenticator: Authenticator[T]): Directive[Tuple1[T]] = {
    extractRequestContext.flatMap { requestContext =>
      authenticator(requestContext) match {
        case Some(identity) => provide(identity)
        case None => reject(AuthorizationFailedRejection)
      }
    }
  }

  def asyncAuthenticate[T](asyncAuthenticator: AsyncAuthenticator[T]): Directive[Tuple1[T]] = {
    extractExecutionContext.flatMap { executionContext =>
      extractRequestContext.flatMap { requestContext =>
        onSuccess(asyncAuthenticator(requestContext)).flatMap {
          case Some(identity) => provide(identity)
          case None => reject(AuthorizationFailedRejection)
        }
      }
    }
  }
}

object AuthenticationDirectives extends AuthenticationDirectives
