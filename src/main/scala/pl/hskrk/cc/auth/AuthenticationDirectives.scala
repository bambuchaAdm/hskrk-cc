package pl.hskrk.cc.auth

import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.Directive
import akka.http.scaladsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.server.directives.BasicDirectives._
import akka.http.scaladsl.server.directives.FutureDirectives._
import akka.http.scaladsl.server.directives.RouteDirectives._

import scala.concurrent.Future

trait GeneralAuthenticator[T] {
  def authenticate(requestContext: RequestContext): Option[T]
}

trait AsyncGeneralAuthenticator[T] {
  def authenticate(requestContext: RequestContext): Future[Option[T]]
}

/**
  * Created by bambucha on 25.09.16.
  */
trait AuthenticationDirectives {

  def authenticateUsing[T](authenticator: GeneralAuthenticator[T]): Directive[Tuple1[T]] = {
    extractRequestContext.flatMap { requestContext =>
      authenticator.authenticate(requestContext) match {
        case Some(identity) => provide(identity)
        case None => reject(AuthorizationFailedRejection)
      }
    }
  }

  def authenticateUsing[T](asyncAuthenticator: AsyncGeneralAuthenticator[T]): Directive[Tuple1[T]] = {
    extractExecutionContext.flatMap { executionContext =>
      extractRequestContext.flatMap { requestContext =>
        onSuccess(asyncAuthenticator.authenticate(requestContext)).flatMap {
          case Some(identity) => provide(identity)
          case None => reject(AuthorizationFailedRejection)
        }
      }
    }
  }
}

object AuthenticationDirectives extends AuthenticationDirectives
