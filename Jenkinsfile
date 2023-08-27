pipeline {
    agent any

//     tools {
//         // Install the Maven version configured as "M3" and add it to the path.
//         maven "maven_394"
//     }

    stages {
        stage('Build & Test') {
            steps {
                // To run Maven on a Windows agent, use
                bat "mvn clean package"
            }
        }
        stage('Run') {
            steps {
          	  script {
                withEnv ( ['JENKINS_NODE_COOKIE=doNotKill'] ) {
                  bat "start /b java -jar ./target/JavaParserSub-0.0.1.jar"
                }
              }
    	   }
        }
    }
}
