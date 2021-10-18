import com.ibm.dbb.build.*

// Change the following variables to match your system
hlq        = "VASUKI"
sourceDir  = "/u/VASUKI/jenkins/workspace/Checkout_SCM/Build/HelloWorld"
compilerDS = "IGY630.SIGYCOMP"

println("compare the test files")

String file1 = new File("${sourceDir}/hello_cbss.log").text

String file2 = new File("${sourceDir}/hello_cbss.log").text

println (file1)

if (file1 == file2)
    println("Compare success")
else
    println("Compare Fail")

