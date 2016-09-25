package pl.hskrk.cc.auth

import akka.http.scaladsl.model.headers.HttpCookiePair
import org.scalatest.{FlatSpec, Matchers}
import pl.hskrk.cc.users.{User, UserId}

/**
  * Created by bambucha on 25.09.16.
  */
class CookieAuthenticatorTest extends FlatSpec with Matchers {

  behavior of "Cookie Authenticator"

  val sut = new CookieAuthenticator("ERdIF+5ucw2iYBiBxIHjSO3BytjoayRYnFGpofl4hheSkK/bRBBfDJqKrByA/BeTk+WmgqSTGgeLcz4pnp5uFw==")

  it should "reject empty cookie" in {
    val emptyCookie = HttpCookiePair("auth", "")
    sut.authenticateCookie(emptyCookie) shouldBe None
  }

  it should "reject cookie with bad hmac" in {
    val cookie = HttpCookiePair("auth", "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=")
    sut.authenticateCookie(cookie) shouldBe None
  }

  it should "pass cookie with good hmac" in {
    val cookie = HttpCookiePair("auth", "xG3cJ5uByab+ueAw8GgnnQNuqzIMEZAYHwzd4k02FRIAAAAB")
    sut.authenticateCookie(cookie) shouldEqual Some(UserId(1))
  }

  it should "create proper cookie to authenticate as" in {
    val user = User(UserId(1), "example")
    val cookie = sut.createCookieFor(user)
    cookie.cookies should contain(HttpCookiePair("auth","xG3cJ5uByab+ueAw8GgnnQNuqzIMEZAYHwzd4k02FRIAAAAB"))
  }

}
