import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.build.*

// Change the following variables to match your system
hlq        = "VASUKI"
sourceDir  = "/u/VASUKI/project/dbb/dbb/Build/HelloWorld"
compilerDS = "IGY630.SIGYCOMP"
linkDS = "CEE.SCEELKEX"


println("Creating ${hlq}.COBOL. . .")
CreatePDS createPDSCmd = new CreatePDS();
createPDSCmd.setDataset("${hlq}.COBOL");
createPDSCmd.setOptions("tracks space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library)");
createPDSCmd.create();

println("Creating ${hlq}.OBJ. . .")
createPDSCmd.setDataset("${hlq}.OBJ");
createPDSCmd.setOptions("tracks space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library)");
createPDSCmd.create();

println("Creating ${hlq}.LOAD. . .")
createPDSCmd.setDataset("${hlq}.LOAD");
createPDSCmd.setOptions("tracks space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library)");
createPDSCmd.create();

println("Copying ${sourceDir}/hello.cbl to ${hlq}.COBOL(HELLO) . . .")
def copy = new CopyToPDS().file(new File("${sourceDir}/hello.cbl")).dataset("${hlq}.COBOL").member("HELLO")
copy.execute()

println("Compiling ${hlq}.COBOL(HELLO). . .")
def compile = new MVSExec().pgm("IGYCRCTL").parm("LIB")
compile.dd(new DDStatement().name("SYSIN").dsn("${hlq}.COBOL(HELLO)").options("shr"))
compile.dd(new DDStatement().name("SYSLIN").dsn("${hlq}.OBJ(HELLO)").options("shr"))

(1..17).toList().each { num ->
	compile.dd(new DDStatement().name("SYSUT$num").options("cyl space(5,5) unit(vio) new"))
	   }

compile.dd(new DDStatement().name("SYSMDECK").options("cyl space(5,5) unit(vio) new"))
compile.dd(new DDStatement().name("TASKLIB").dsn("${compilerDS}").options("shr"))
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio)  new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("${sourceDir}/hello_cbc.log")))

def link = new MVSExec().pgm("IEWBLINK").parm("")
link.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio)  new"))
link.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("${sourceDir}/hello_cbl.log")))

link.dd(new DDStatement().name("SYSLMOD").dsn("${hlq}.LOAD(HELLO)").options("shr"))
(1..10).toList().each { num ->
	link.dd(new DDStatement().name("SYSUT$num").options("cyl space(5,5) unit(vio) new"))
	   }
link.dd(new DDStatement().name("SYSLIN").dsn("${hlq}.OBJ(HELLO)").options("shr"))
link.dd(new DDStatement().name("SYSLIB").dsn("${linkDS}").options("shr"))
link.dd(new DDStatement().dsn("CEE.SCEELKED").options("shr"))  
link.dd(new DDStatement().dsn("SYS1.MACLIB").options("shr"))   

def rc = compile.execute()

if (rc > 4)
    println("Compile failed!  RC=$rc")
else
    println("Compile successful!  RC=$rc")


rc = link.execute()

if (rc > 4)
    println("Link failed!  RC=$rc")
else
    println("Link successful!  RC=$rc")


// define the MVSExec command to link the file
def run = new MVSExec().pgm("HELLO").parm("")
//
//// add DD statements to the MVSExec command
run.dd(new DDStatement().name("TASKLIB").dsn("${hlq}.LOAD(HELLO)").options("shr"))
run.dd(new DDStatement().name("SYSOUT").options(tempCreateOptions))
run.dd(new DDStatement().name("SYSPRINT").options(tempCreateOptions))
// the important stuff
run.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("${sourceDir}/hello_cbs.log")))
run.copy(new CopyToHFS().ddName("SYSOUT").file(new File("${sourceDir}/hello_cbss.log")))

println("Run Success")

