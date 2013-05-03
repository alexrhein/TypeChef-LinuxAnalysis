package de.fosd.typechef.linux

import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer

object DimacsToC extends App {
    if (args.size != 2) {
        println("expected feature model file as 1st parameter and outfile as 2nd parameter")
        System.exit(0)
    }

    val outWr = new BufferedWriter(new FileWriter(args(1)))

    val dimacsLines = scala.io.Source.fromFile(args(0)).getLines()
    val features = new ArrayBuffer[String](100000)
    features += "Dummy"
    assert (features(0) == "Dummy")

    outWr.write("extern int __VERIFIER_nondet_int(void);\nint select_one() {if (__VERIFIER_nondet_int()) return 1; else return 0;}\n\n")

    var firstOutLine=true
    for (line:String <- dimacsLines) {
      if (line.startsWith("c ")) {
        //c 11 BCMA_POSSIBLE
        val parts:Array[String] = line.split(" ")
        assert (parts.apply(0)=="c")
        features += parts.apply(2)
        val id = parts.apply(1).replaceAllLiterally("$","").toInt
        assert(features(id) == parts.apply(2))

      } else if (line.startsWith("p ")) {
        //p cnf 80258 388816
        for (f:String <- features) {
          outWr.write ("int feature_" + f + ";\n")
        }
        outWr.write("void select_features() {\n")
        for (f:String <- features) {
          outWr.write ("feature_" + f + " = select_one();\n")
        }
        outWr.write("}\n\n\n")
        outWr.write("int valid_product() {\nreturn \n")
      } else {
        if (line.trim != "") {
          //-17552 -11881 1490 17551 0
          if (firstOutLine) {
            firstOutLine=false;
            outWr.write("(")
          } else {
            outWr.write("&& (")
          }
          val parts:Array[String] = line.split(" ")
          var firstInThisLine = true;
          for (part:String <- parts) {
            if (part.trim != "") {
              val i:Int = part.toInt
              if (i!=0) {
                if (firstInThisLine) {
                  firstInThisLine=false;
                } else {
                  outWr.write(" ||")
                }
                if (i > 0) {
                  outWr.write(" feature_" + features(i))
                } else {
                  outWr.write(" !feature_" + features(-i))
                }
              }
            }
          }
          outWr.write(")\n")
        }
      }
    }
    outWr.write(";\n}")
    outWr.close();
}
