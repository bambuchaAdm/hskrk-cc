package pl.hskrk.cc.assets

import java.io.File
import java.nio.file._
import java.util.stream.Collectors

import akka.http.scaladsl.server.Directives._

class MissingAssetException(key: String, mapping: Map[String, String]) extends Exception {
  override def getMessage: String = s"There is not key=$key in mapping. Mapping is " + mapping.mkString("{\n", ",\n","}\n")
}

class Assets(path: String) {

  val assetsPath: String = path

  val routes = {
    pathPrefix("assets") {
      getFromBrowseableDirectory(assetsPath)
    }
  }

  val cssCache: Map[String, String] = {
//      Map.empty[String, String]
    import scala.collection.JavaConverters._
    val file = new File(assetsPath)
    val pathList = Files.walk(file.toPath).collect(Collectors.toList[Path]).asScala
    pathList.filter(_.getFileName.toString.endsWith(".css")).map { path =>
      val fileName: String = path.getFileName.toString
      fileName -> s"/assets/css/$fileName"
    }.toMap
  }

  def css(key: String): String = cssCache.getOrElse(key, throw new MissingAssetException(key, cssCache))

}
