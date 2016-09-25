package pl.hskrk.cc.issues

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import pl.hskrk.cc.TwirlSupport
import akka.http.scaladsl.model.StatusCodes.SeeOther
import pl.hskrk.cc.assets.Assets
import pl.hskrk.cc.users.UserManager

import scala.concurrent.duration._

class IssuesModule(val system: ActorSystem, implicit val assets: Assets) extends TwirlSupport {

  implicit val dispatcher = system.dispatcher

  implicit val timeout: Timeout = 5.seconds

  val issueManager = system.actorOf(Props[IssueManager])

  import IssueManagerProtocol._
  import akka.pattern.ask

  val routes = {
    path("issues") {
      get { context =>
        issueManager.ask(GetAllIssue).mapTo[List[Issue]].flatMap{ issues =>
          context.complete(html.issuesList(issues))
        }
      } ~
      post {
        formField("summary", "description"){ (summary, description) => context =>
          issueManager ! CreateIssue(UserManager.example, summary, description)
          context.redirect(Issues.listPath, SeeOther)
        }
      }
    } ~
    path("issues" / "new") {
      complete{ html.newIssue() }
    } ~
    pathPrefix("issue" / IntNumber).as(IssueId.apply _) { id =>
      path("comments") {
        post {
          formFields("content") { content => context =>
            issueManager ! AppendComment(id, UserManager.example, content)
            context.redirect(Issues.pathFor(id), SeeOther)
          }
        }
      } ~
      get { context =>
        issueManager.ask(FindIssue(id)).mapTo[Option[Issue]].flatMap{ optionalIssue =>
          context.complete(optionalIssue.fold(html.page404()){ issue => html.issueDetails(issue)})
        }
      } ~
      put {
        ???
      } ~
      delete {
        ???
      }
    }
  }
}

object Issues {
  val listPath = Uri("/issues")

  val newIssuePath = Uri("/issues/new")

  def pathFor(issue: Issue): Uri = pathFor(issue.id)

  def pathFor(id: IssueId): Uri = Uri(s"/issue/${id.get}")

  def newCommentPathFor(issue: Issue) = Uri(s"/issue/${issue.id.get}/comments")
}
