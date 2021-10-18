import com.ibm.dbb.build.*

// Change the following variables to match your system
hlq        = "VASUKI"
sourceDir  = "/u/VASUKI/jenkins/workspace/Checkout_SCM/Build/HelloWorld"
def tempCreateOptions = "cyl space(5,5) unit(vio) new"


    // copy a member from one dataset to another
def memberCopy = new TSOExec().command("OCOPY INDD(IN) OUTDD(OUT) TEXT CONVERT(YES) TO1047")
memberCopy.dd(new DDStatement().name("IN").dsn("PROJECT.PROG.LOADLIB(HELLO)").options("shr"))
memberCopy.dd(new DDStatement().name("OUT").dsn("PROJECT.PROD.LOADLIB(HELLO)").options("shr"))
memberCopy.logFile(new File("${sourceDir}/prod_copy_ispf.log"))
int rc = memberCopy.execute();

if (rc = 0)
    println("Production Copy Successful!  RC=$rc")
else
    println("Production Copy Failed!  RC=$rc")


