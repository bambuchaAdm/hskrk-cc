package pl.hskrk.cc.issues

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging}
import pl.hskrk.cc.users.{User, UserId}

object IssueManagerProtocol {
  object GetAllIssue
  case class CreateIssue(user: User, summary: String, description: String)
  case class FindIssue(id: IssueId)
  case class AppendComment(issueId: IssueId, author: User, content: String)
}

class IssueManager extends Actor with ActorLogging {

  import IssueManagerProtocol._

  var issues = List(
    Issue(IssueId(1),User(UserId(2), "Bambucha"), "Burdel na blatach", "Znowu gdy przyszedłem do HSu zastałem burdel na blatach",
      List(Comment(CommentId(1), User(UserId(2), "Bambucha"), LocalDateTime.now().minusMinutes(30), "Na szczęście bałagan zniknął"),Comment(CommentId(2), User(UserId(2), "Bambucha"), LocalDateTime.now(), "Mam nadzię że się to nie powtórzy"))),
    Issue(IssueId(2),User(UserId(1), "Temporal"), "TEDx Kraków 2014", "Mamy możliwość wystąpienia na TEDx Kraków 2014, ktoś zainteresowany",
      List(Comment(CommentId(3), User(UserId(2), "Bambucha"), LocalDateTime.now(), "Tak"))),
    Issue(IssueId(3),User(UserId(2), "Bambucha"), "Zakup frezów", "Frezy palcowe fi 3.5 się skończyły",
      List(Comment(CommentId(4), User(UserId(1), "Temporal"), LocalDateTime.now(), "Ok, kupujcie")))
  )

  override def receive: Receive = {
    case GetAllIssue =>
      sender() ! issues

    case CreateIssue(user, summary, description) =>
      val maxId = issues.map(_.id).max
      val newIssue = Issue(maxId.next, user, summary, description, Nil)
      issues = issues :+ newIssue

    case FindIssue(id) =>
      sender() ! issues.find(_.id == id)

    case AppendComment(id, author, content) =>
      val maxId = issues.flatMap(_.comments).map(_.id).max
      val newComment = Comment(maxId.next, author, LocalDateTime.now(), content)
      val issueIndex = issues.indexWhere(_.id == id)
      val issue = issues(issueIndex)
      issues = issues.updated(issueIndex, issue.appendComment(newComment))
  }
}
