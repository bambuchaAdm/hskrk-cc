package pl.hskrk.cc.auth

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future

/**
  * Created by bambucha on 20.09.16.
  */
class AuthenticationDirectivesTest extends FlatSpec with Matchers with ScalatestRouteTest with AuthenticationDirectives {

  case class ExampleUser(id: Int, secret: String)

  class MissingCookieException extends Exception

  val user = ExampleUser(1, "secret")

  val users = Map("1" -> user)

  val authenticator: GeneralAuthenticator[ExampleUser] = new GeneralAuthenticator[ExampleUser] {
    override def authenticate(context: RequestContext) = {
      context.request.uri.queryString().flatMap {
        case "user=1" =>
          Some(user)
        case _ =>
          None
      }
    }
  }

  val asyncAuthenticator: AsyncGeneralAuthenticator[ExampleUser] =  new AsyncGeneralAuthenticator[ExampleUser] {
    override def authenticate(context: RequestContext) = {
      Future.successful(context.request.uri.queryString().flatMap {
        case "user=1" =>
          Some(user)
        case _ =>
          None
      })
    }
  }

  val syncSUT = {
    path("unsecure") {
      complete("OK")
    } ~
    path("secure") {
      authenticateUsing(authenticator) { user =>
        complete(user.secret)
      }
    }
  }

  behavior of "Synchornius authenticator"

  it should "allow access to resources outsite" in {
    Get("/unsecure") ~> syncSUT ~> check {
      responseAs[String] shouldEqual "OK"
    }
  }

  it should "not allow access to secure resource without authenticator" in {
    Get("/secure") ~> syncSUT ~> check {
      rejections should contain(AuthorizationFailedRejection)
    }
  }

  it should "allow access to secure resource with credentials" in {
    Get("/secure?user=1") ~> syncSUT ~> check {
      responseAs[String] shouldEqual "secret"
    }
  }

  it should "not allow access to secure resource with wrong credentials" in {
    Get("/secure?user=2") ~> syncSUT ~> check {
      rejections should contain(AuthorizationFailedRejection)
    }
  }

  val asyncSUT = {
    path("unsecure") {
      complete("OK")
    } ~
    path("secure") {
      authenticateUsing(asyncAuthenticator){ user =>
        complete(user.secret)
      }
    }
  }

  behavior of "Asynchornius authenticator"

  it should "allow access to resources outsite with future interface " in {
    Get("/unsecure") ~> asyncSUT ~> check {
      responseAs[String] shouldEqual "OK"
    }
  }

  it should "not allow access to secure resource without authenticator" in {
    Get("/secure") ~> asyncSUT ~> check {
      rejections should contain(AuthorizationFailedRejection)
    }
  }

  it should "allow access to secure resource with credentials" in {
    Get("/secure?user=1") ~> asyncSUT ~> check {
      responseAs[String] shouldEqual "secret"
    }
  }

  it should "not allow access to secure resource with wrong credentials" in {
    Get("/secure?user=2") ~> asyncSUT ~> check {
      rejections should contain(AuthorizationFailedRejection)
    }
  }

}
