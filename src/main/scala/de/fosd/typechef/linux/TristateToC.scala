package de.fosd.typechef.linux

import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer

object TristateToC extends App {
    if (args.size != 2) {
        println("expected feature model file as 1st parameter and outfile as 2nd parameter")
        System.exit(0)
    }

	val file:File = new File(args(1));
	file.delete();
    val outWr = new BufferedWriter(new FileWriter(file))

    val fmLines = scala.io.Source.fromFile(args(0)).getLines()
    val features = new ArrayBuffer[String](100000)
    features += "Dummy"
    assert (features(0) == "Dummy")

    outWr.write("extern int __VERIFIER_nondet_int(void);\nint select_one() {if (__VERIFIER_nondet_int()) return 1; else return 0;}\n\n")

    var firstOutLine=true
    for (line:String <- fmLines) {
      if (line.startsWith("@ ") || line.startsWith("$ ")) {
        //$ _X31331_m
        //@ BCMA_POSSIBLE
        val parts:Array[String] = line.split(" ")
        assert (parts.apply(0)=="@" || parts.apply(0)=="$")
        features += parts.apply(1)
      } else {
        if (line.trim != "") {
          // (WAN | !WAN_m)
          if (firstOutLine) {
			// begin code for feature variable introduction
			for (f:String <- features) {
			  outWr.write ("int feature_" + f + ";\n")
			}
			outWr.write("void select_features() {\n")
			for (f:String <- features) {
			  outWr.write ("feature_" + f + " = select_one();\n")
			}
			outWr.write("}\n\n\n")
			outWr.write("int valid_product() {\nreturn \n")
			// end code for feature variable introduction
            firstOutLine=false;
            outWr.write("(")
          } else {
            outWr.write("&& (")
          }
          val sep  : Array[Char] = Array('(',')','!','|',' ','<','>','=','&')
          val featuresInLine = line.split(sep)
          var workLine : String = line
          for (s <- sep) {
            workLine = workLine.replaceAllLiterally(s.toString, " " + s + " ")
          }
          workLine = workLine + " "
          for (feat <- featuresInLine) {
            if (feat.trim != "" && feat != "1" && feat != "0") {
              workLine= workLine.replaceAllLiterally(" " + feat + " ", " feature_" + feat + " ")
            }
          }
          while (workLine.contains("  "))
            workLine = workLine.replaceAllLiterally("  ", " ")
          workLine = workLine.replaceAllLiterally("< = >", "<=>")
          outWr.write(workLine.
			replaceAllLiterally(" | ", " || ").
			replaceAllLiterally(" & ", " && ").
			replaceAllLiterally(" <=> ", " == "))
          outWr.write(")\n")
        }
      }
    }
    outWr.write(";\n}")
    outWr.close();
}
