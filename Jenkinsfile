pipeline {
  agent {
    node {
      label 'jenkinsAgent-jdk17-docker'
    }
  }

  stages {
    stage('inside docker image') {
      agent {
        docker { 
          // image 'openjdk@sha256:d732b25fa8a6944d312476805d086aeaaa6c9e2fbc3aefd482b819d5e0e32e10' // https://hub.docker.com/layers/library/openjdk/17.0.2-jdk-slim/images/sha256-d732b25fa8a6944d312476805d086aeaaa6c9e2fbc3aefd482b819d5e0e32e10?context=explore
          // dk some issue with above image -- container is not running // maybe cuz ec2 is amd64, must match?..
          // image 'eclipse-temurin@sha256:2419c9c7116601aee0c939888e2eed78e235d38f5f5e9e9f1d1d18d043df55eb' // https://hub.docker.com/layers/library/eclipse-temurin/17.0.9_9-jre-ubi9-minimal/images/sha256-2419c9c7116601aee0c939888e2eed78e235d38f5f5e9e9f1d1d18d043df55eb?context=explore
          // image 'openjdk@sha256:779635c0c3d23cc8dbab2d8c1ee4cf2a9202e198dfc8f4c0b279824d9b8e0f22' // https://hub.docker.com/layers/library/openjdk/17.0.2-jdk-slim/images/sha256-779635c0c3d23cc8dbab2d8c1ee4cf2a9202e198dfc8f4c0b279824d9b8e0f22?context=explore
          // image 'maven@sha256:76b11de3a90a9dd4b2b1765850087296ec630c16636c91f0181d2fb7859f8502' // https://hub.docker.com/layers/library/maven/3.8.4-openjdk-17-slim/images/sha256-76b11de3a90a9dd4b2b1765850087296ec630c16636c91f0181d2fb7859f8502?context=explore
          // image 'maven@sha256:0b27c7feef457b6773e078b0ab679d97a471d9fdebd07df3f9b0cdc762c5b4a6' // https://hub.docker.com/layers/library/maven/3.8.4-eclipse-temurin-17/images/sha256-0b27c7feef457b6773e078b0ab679d97a471d9fdebd07df3f9b0cdc762c5b4a6?context=explore
          image 'maven@sha256:bc229c50a02b3c17c09085b9fceca1fcf44392f463d650e8e64bcabbc9ffd58a' // https://hub.docker.com/layers/library/maven/3.9.5-eclipse-temurin-17/images/sha256-bc229c50a02b3c17c09085b9fceca1fcf44392f463d650e8e64bcabbc9ffd58a?context=explore
          args '-v /var/run/docker.sock:/var/run/docker.sock'
          reuseNode true
        }
      }

      // @pb: seems Maven Plugin is incompatible with Docker plugin
      // tools {
      //   maven "maven-v3.8.4"
      // }
      //         stage('debug2') {@¦          steps {@¦            sh '''@¦            export PATH="/home/jenkins/agent/tools/hudson.tasks.Maven_MavenInstallation/maven-v3.8.4/bin:$PATH"@¦            mvn --version || true@¦            '''@¦          }@¦        }@¦        stage('debug3') {@¦          steps {@¦            // https://plugins.jenkins.io/maven-plugin/ recommended installed@¦            // https://plugins.jenkins.io/pipeline-maven/ manually installed // https://www.jenkins.io/doc/pipeline/steps/pipeline-maven/@¦            withMaven() {@¦              sh 'echo $PATH' // path doesnt has Maven@¦              sh 'mvn --version || true' // fails too (though I didnt config anything in the global one, just the below one like nodejs ...)@¦            }@¦          }@¦        }@¦@¦        stage('fail & quit') { steps { sh 'false' } } // sh 'exit 1'
  
      stages {
        stage('checkout') {
          steps {
            git branch: 'main', url: 'https://github.com/Norlandz/JavaParserSub' // @config[project name]
            sh 'pwd'
            sh 'ls -la'
          }
        }
        stage('setup env') {
          steps {
            sh 'java --version'
            sh 'mvn --version'
          }
        }
        // mvn jar:jar
        // junit
        // https://stackoverflow.com/questions/55183989/maven-lifecycle-within-jenkins-pipeline-how-to-best-separate-responsibilities
        stage('mvn clean') {
          steps {
            sh "mvn --batch-mode clean"
          }
        }
        stage('test & build package') {
          steps {
            sh "mvn --batch-mode package"
            // bat "start /b java -jar ./target/JavaParserSub-0.0.1.jar"
          }
        }
        stage('build docker image') {
          steps {
            sh 'docker build -t mindde/javaparsersub:v0.0.1 .' // @config[project name]
          }
        }
        stage('publish docker image') {
          environment {
            CredDockerhub = credentials('idVal_CredDockerhub')
          }
          steps {
            sh 'docker login -u $CredDockerhub_USR -p $CredDockerhub_PSW'
            sh 'docker push mindde/javaparsersub:v0.0.1' // @config[project name]
            sh 'docker logout'
          }
        }
      }
    }
    stage('call (async) remote server to pull & run (deploy) docker image (using watchtower)') { // watchtower will do this, no need to _ special docker trigger / publish_over_ssh _
      steps {
        script {
          sh '''\
          echo "this curl will fail -- if watchtower is not up yet. 
            which can happen at the first time of the whole project setup -- 
            1. this script need to build the image to dockerhub 
            2. docker-compose.yml file pulls the image and start up all containers 
            3. watchtower will be started in that docker-compose.yml together 
            -- once watchtower is up, all later builds will be able to call to watchtower no problem."
          '''.stripIndent()

          def ipAddr_MainApp_withWatchtower = "10.14.1.11" // "mainApp.rff.com" // FIXME @config need use Dns instead of a fixed ip
          sh """
          curl -H "Authorization: Bearer tokenVal_WATCHTOWER_HTTP_API_TOKEN_toBeSentFromJenkins" --max-time 20 --location http://${ipAddr_MainApp_withWatchtower}:8686/v1/update
          """ 
        }
      }
    }
    stage('clean up docker image volume') { 
      steps {
        sh 'yes | docker system prune'
      }
    }
    stage('done') {
      steps {
        sh 'echo done'
        sh 'ls -la'
      }
    }
  }

}

// ;archived; // pipeline {@¦    agent any@¦@¦//     tools {@¦//         // Install the Maven version configured as "M3" and add it to the path.@¦//         maven "maven_394"@¦//     }@¦//     // seems not needed, if server already have... dk@¦@¦    stages {@¦        stage('Build & Test') {@¦            steps {@¦                // To run Maven on a Windows agent, use@¦                bat "mvn clean package"@¦            }@¦        }@¦        stage('Run') {@¦            steps {@¦              echo 'Just run in Master directly; this starts the program in the background; <> though a proper way should just deploy to Docker / use Publish_Over_Ssh.; currenly has no way to terminate & replace the old running app.'@¦              @¦          	  script {@¦                withEnv ( ['JENKINS_NODE_COOKIE=doNotKill'] ) {@¦                  bat "start /b java -jar ./target/JavaParserSub-0.0.1.jar"@¦                }@¦              }@¦    	   }@¦        }@¦    }@¦}@¦






