package pl.hskrk.cc.issues

import java.time.LocalDateTime

import pl.hskrk.cc.users.User

case class IssueId(get: Int) extends AnyVal {
  def isEmpty = get == 0
  def next = IssueId(get + 1)
}

object IssueId {
  implicit val ordering = Ordering.by[IssueId, Int](_.get)
}

case class Issue(id: IssueId, author: User, title: String, description: String, comments: List[Comment]) {
  def appendComment(comment: Comment) = copy(comments = comments :+ comment)
}

case class CommentId(get: Int) extends AnyVal {
  def isEmpty = get == 0
  def next = CommentId(get + 1)
}

object CommentId {
  implicit val ordering = Ordering.by[CommentId, Int](_.get)
}

case class Comment(id: CommentId, author: User, createdAt: LocalDateTime, content: String)
