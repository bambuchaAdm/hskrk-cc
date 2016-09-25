package pl.hskrk.cc.auth

import java.nio.{BufferUnderflowException, ByteBuffer}
import java.nio.charset.Charset
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import akka.http.scaladsl.model.headers.{Cookie, HttpCookiePair}
import akka.http.scaladsl.server.RequestContext
import pl.hskrk.cc.users.{User, UserId}

/**
  * Created by bambucha on 25.09.16.
  */
class CookieAuthenticator(key: String) extends GeneralAuthenticator[UserId] {

  private val charset = Charset.forName("utf8")

  override def authenticate(requestContext: RequestContext): Option[UserId] = {
    requestContext.request.cookies.find( _.name == "auth").flatMap(authenticateCookie)
  }

  private val hmacKey = {
    val bytes = Base64.getDecoder.decode(key)
    new SecretKeySpec(bytes, "Hmac256")
  }

  def authenticateCookie(authCookie: HttpCookiePair): Option[UserId] = {
    val mac = Mac.getInstance("HmacSHA256") // Extract as parameter
    val decoder = Base64.getDecoder
    val content = decoder.decode(authCookie.value)
    val buffer = ByteBuffer.wrap(content)
    try {
      val signature = Array.ofDim[Byte](mac.getMacLength)
      buffer.get(signature)
      val rawValue = Array.ofDim[Byte](Integer.BYTES)
      buffer.get(rawValue)
      if(validateMessage(mac, signature, rawValue)){
        val value = ByteBuffer.wrap(rawValue).getInt()
        Some(UserId(value))
      } else {
        None
      }
    } catch {
      case e: BufferUnderflowException => None
    }
  }

  def validateMessage(instance: Mac, signature: Array[Byte], value: Array[Byte]): Boolean = {
    instance.init(hmacKey)
    val result = instance.doFinal(value)
    signature.zip(result).forall{
      case (first, second) => first == second
    }
  }

  def createCookieFor(user: User): Cookie = {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(hmacKey)
    val encoder = Base64.getEncoder
    val valueBuffer = ByteBuffer.allocate(Integer.BYTES)
    val id = user.id.get
    valueBuffer.putInt(id)
    valueBuffer.flip()
    val signature = mac.doFinal(valueBuffer.array())
    val resultBuffer = ByteBuffer.allocate(Integer.BYTES + mac.getMacLength)
    resultBuffer.put(signature)
    resultBuffer.putInt(id)
    val encodedResult = encoder.encodeToString(resultBuffer.array())
    Cookie("auth", encodedResult)
  }
}
