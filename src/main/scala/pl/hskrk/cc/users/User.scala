package pl.hskrk.cc.users

case class UserId(get: Int) extends AnyVal {
  def isEmpty = get == 0
}
case class User(id: UserId, login: String) {

}
