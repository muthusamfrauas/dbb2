import com.ibm.dbb.build.*

// Change the following variables to match your system
hlq        = "VASUKI"
sourceDir  = "/u/VASUKI/jenkins/workspace/Checkout_SCM/Build/HelloWorld"
def tempCreateOptions = "cyl space(5,5) unit(vio) new"


// define the MVSExec command to link the file
def run = new MVSExec().pgm("HELLO").parm("")
//
//// add DD statements to the MVSExec command
run.dd(new DDStatement().name("TASKLIB").dsn("PROJECT.PROG.LOADLIB(HELLO)").options("shr"))
run.dd(new DDStatement().name("SYSOUT").options(tempCreateOptions))
run.dd(new DDStatement().name("SYSPRINT").options(tempCreateOptions))
// the important stuff
run.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("${sourceDir}/hello_cbs.log")))
run.copy(new CopyToHFS().ddName("SYSOUT").file(new File("${sourceDir}/hello_cbss.log")))

def rc = run.execute()

if (rc = 0)
    println("Run Successful!  RC=$rc")
else
    println("Run Failed!  RC=$rc")

