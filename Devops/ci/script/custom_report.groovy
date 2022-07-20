import groovy.json.JsonSlurper
import groovy.json.JsonOutput;


if(args[0] == "lint"){
 List json = new JsonSlurper().parse(new File("./build/swiftlint.result.json").newReader()) 

    // remove attributes 
    json.each{
        it.remove('character')
        it.remove('rule_id')
        it.remove('type')
    }

   def result  = json.groupBy({it.file},{it.reason}).collect{[unique: it.value]}

   def output = []

   output << "Done Linting. Found ${json.size()} violations.\n\n"

   result.each {
       def lineno = []
       it['unique'].eachWithIndex{
           name,index ->   
                 if((it['unique']["${it['unique'].keySet()[index]}"].size()) != 1){
                     it['unique']["${it['unique'].keySet()[index]}"].eachWithIndex{
                        key, value -> 
                        lineno << "${key['line']}"
                     }
                  }

                if((it['unique']["${it['unique'].keySet()[index]}"].size()) == 1){
                     output << "FileName: ${it['unique']["${it['unique'].keySet()[index]}"][0]['file'].replace('/Users/shashiks/workspace/PDS_DDRD_LillyAnalytics','')}\nViolations: ${ it['unique']["${it['unique'].keySet()[index]}"][0]['reason']}\nLine No: ${ it['unique']["${it['unique'].keySet()[index]}"][0]['line']}\n\n"
                  }else{
                     output << "FileName: ${it['unique']["${it['unique'].keySet()[index]}"][0]['file'].replace('/Users/shashiks/workspace/PDS_DDRD_LillyAnalytics','')}\nViolations: ${ it['unique']["${it['unique'].keySet()[index]}"][0]['reason']}\nLine No: ${ lineno.join(', ')}\n"
                  }
       }
   }
   print output.join('')
   }else{
        def xml = new XmlParser().parse("./build/report.junit")
        xml['testsuite'].findAll { testsuite ->
            testsuite.attributes()['failures'].toInteger() == 0
        }.each { testsuite ->
            testsuite.parent().remove(testsuite)
        }

        def failure_test_cases = []

        xml['testsuite']['testcase'].findAll { testcase ->
            !testcase.attributes()['time']
        }.each { testcase ->
            failure_test_cases << "${testcase.attributes()['classname']}.${testcase.attributes()['name']}\n${testcase['failure'][0].attributes()['message']}\n\n"
        }

        def output = "${xml.attributes()['tests'].toInteger()} tests total, ${xml.attributes()['failures'].toInteger()} failed\n\n"

        // if (!stage_result) {
            output += failure_test_cases.join("\n\n")
        // }

        print output
   }

