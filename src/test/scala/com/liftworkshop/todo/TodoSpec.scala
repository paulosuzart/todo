package com.liftworkshop.todo

import org.specs._
import org.specs.specification._
import org.specs.runner.JUnit4
import net.liftweb.http.{S, LiftSession}
import net.liftweb.mapper.BaseMetaMapper
import net.liftweb.util._
import net.liftweb.mocks.MockHttpSession
import javax.servlet.http.HttpSession
import model._
import net.liftweb.mapper._
import bootstrap.liftweb._
import net.liftweb.http._
import scala.xml._
import com.liftworkshop.todo.snippet._
import org.specs.matcher.Matcher
import CustomMatcher._

object CustomMatcherSpec extends Specification {
    "beAtGmail" can {
        "assert users at Gmail" in {
            "paulosuzart@gmail.com" must beAtGmail
        }
    }
}

object CustomMatcher {
    import java.util.regex.Pattern
    val beAtGmail = new Matcher[String]{
        def apply(m : => String) = {
            val pattern = Pattern.compile(".+\\@gmail.com")
            (pattern.matcher(m).matches, m + " is at Gmail",  m + " is not at Gmail")
        }
    }
}

object TodoSpec extends Specification with Contexts {

    "Create/Update a ToDo item" should {
        "show the following error messages:" >> {
            "Description must be 3 characters" >> {
                "if description field length is less than 3" in {
                    val todo = ToDo.create.owner(User.currentUser)
                    todo.desc("").validate must
                    contain(FieldError(todo.desc, Text("Description must be 3 characters")))
                }
            }

            "<b>Priority must be 1-10</b>" >> {
                "if priority field is not between, including, 1 and 10" in {
                    val todo = ToDo.create.owner(User.currentUser)
                    todo.priority(-1).validate must
                    contain(FieldError(todo.priority, <b>Priority must be 1-10</b>))
                }
            }

        }

    }


    "A loged user" should {
        "be a gmail user" in {
            val user = User.currentUser.open_!
            user.email.asString must beAtGmail
        }
    }

    val session = new LiftSession("", StringHelpers.randomString(20), new MockHttpSession , null)
    def inSession(a: => Any) = {
        S.initIfUninitted(session) { a }
    }


    def loginUser = inSession {
        val user = User.create
        user.email("tester@gmail.com").password("xxxxxx")
        user.save
        User.logUserIn(user)
    }

    new SpecContext {

        beforeExample {
            if (!DB.jndiJdbcConnAvailable_?)
            DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
            loginUser
        }

        aroundExpectations(inSession(_))

        afterSpec {
            val user = User.find(By(User.email, "tester@gmail.com"))
            User.delete_!(user.open_!)
        }

    }
}
       


class TodoSpecTest extends JUnit4(TodoSpec)
class CustomSpecTest extends JUnit4(CustomMatcherSpec)
