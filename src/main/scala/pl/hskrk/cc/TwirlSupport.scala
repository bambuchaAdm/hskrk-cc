package pl.hskrk.cc

import akka.http.scaladsl.marshalling.{Marshaller, _}
import akka.http.scaladsl.model.MediaTypes
import pl.hskrk.cc.assets.Assets
import play.twirl.api.{Html, Txt, Xml}

/**
  * Created by bambucha on 21.08.16.
  */
trait TwirlSupport {

  implicit val assets: Assets

  implicit val twirlHTMLMarshaller: ToEntityMarshaller[Html] = Marshaller.StringMarshaller.wrap(MediaTypes.`text/html`)(_.toString)

  implicit val twirlXMLMarshaller: ToEntityMarshaller[Xml] = Marshaller.StringMarshaller.wrap(MediaTypes.`text/xml`)(_.toString)

  implicit val twirlTextMarshaller: ToEntityMarshaller[Txt] = Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(_.toString)


}
