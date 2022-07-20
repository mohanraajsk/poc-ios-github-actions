import groovy.json.JsonSlurper
import groovy.json.JsonOutput;


if(args[0] == "lint"){
   List json = new JsonSlurper().parse(new File("./build/swiftlint.result.json").newReader()) 
   def output = "Done Linting. Found ${json.size()} violations.\n\n"
   print output
   }else{
        def xml = new XmlParser().parse("./build/report.junit")
        def output = "${xml.attributes()['tests'].toInteger()} tests total, ${xml.attributes()['failures'].toInteger()} failed\n\n"
        print output
   }

