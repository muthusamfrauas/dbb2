import com.ibm.dbb.build.*

// Change the following variables to match your system
hlq        = "VASUKI"
sourceDir  = "/u/VASUKI/jenkins/workspace/Checkout_SCM/Build/HelloWorld"
def tempCreateOptions = "cyl space(5,5) unit(vio) new"

    // copy a member from one dataset to another
def memberCopy = new MVSExec().pgm("IEBCOPY")
memberCopy.dd(new DDStatement().name("SYSUT1").dsn("PROJECT.PROG.LOADLIB(HELLO)").options("shr"))
memberCopy.dd(new DDStatement().name("SYSUT2").dsn("PROJECT.PROD.LOADLIB(HELLO)").options("shr"))
memberCopy.dd(new DDStatement().name("SYSOUT").options(tempCreateOptions))
memberCopy.dd(new DDStatement().name("SYSPRINT").options(tempCreateOptions))
// the important stuff
memberCopy.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("${sourceDir}/copy_sysp.log")))
memberCopy.copy(new CopyToHFS().ddName("SYSOUT").file(new File("${sourceDir}/copy_syso.log")))
int rc = memberCopy.execute();

if (rc == 0)
    println("Production Copy Successful!  RC=$rc")
else
    println("Production Copy Failed!  RC=$rc")


