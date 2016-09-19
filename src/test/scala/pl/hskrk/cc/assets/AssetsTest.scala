package pl.hskrk.cc.assets

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.sun.javafx.sg.prism.NGCanvas
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

/**
  * Created by bambucha on 19.09.16.
  */
class AssetsTest extends FlatSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {

  val path = Files.createTempDirectory("example")

  override protected def afterAll(): Unit = {
    super.afterAll()
    Files.walkFileTree(path, new SimpleFileVisitor[Path]{
      override def visitFile(t: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult = {
        Files.delete(t)
        FileVisitResult.CONTINUE
      }

      override def postVisitDirectory(t: Path, e: IOException): FileVisitResult = {
        Files.delete(t)
        FileVisitResult.CONTINUE
      }
    })
  }

  val exampleJS: String = "(function(){ alert('dupa') })()"

  val exampleCSS: String = "p { background-color: black; }"

  override protected def beforeAll(): Unit = {
    Files.write(path.resolve("example.js"),exampleJS.getBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING)
    Files.write(path.resolve("example.css"),exampleCSS.getBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING)
    val dir = path.resolve("subdir")
    Files.createDirectory(dir)
    Files.write(dir.resolve("example.js"),exampleJS.getBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING)
    Files.write(dir.resolve("example.css"),exampleCSS.getBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING)
  }

  val assets = new Assets(path.toAbsolutePath.toString)

  val sut = assets.routes

  it should "serve js assets from top level" in {
    Get("/assets/example.js") ~> sut -> check {
      responseAs[String] shouldEqual exampleJS
    }
  }

  it should "serve css assets from top level" in {
    Get("/assets/example.css") ~> sut -> check {
      responseAs[String] shouldEqual exampleCSS
    }
  }

  it should "serve js assets from directory" in {
    Get("/assets/subdir/example.js") ~> sut ~> check {
      responseAs[String] shouldEqual exampleJS
    }
  }

  it should "serve css assets from directory" in {
    Get("/assets/subdir/example.css") ~> sut ~> check {
      responseAs[String] shouldEqual exampleCSS
    }
  }

}
