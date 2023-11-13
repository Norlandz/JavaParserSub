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
          label 'jenkinsAgent-jdk17-docker'
          image 'openjdk@sha256:d732b25fa8a6944d312476805d086aeaaa6c9e2fbc3aefd482b819d5e0e32e10' // https://hub.docker.com/layers/library/openjdk/17.0.2-jdk-slim/images/sha256-d732b25fa8a6944d312476805d086aeaaa6c9e2fbc3aefd482b819d5e0e32e10?context=explore
          args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
      }

      tools {
        maven "maven-v3.8.4"
      }
      
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
        sh 'echo "this curl will fail -- if watchtower is not up yet. \nwhich can happen at the first time of the whole project setup -- \n1. this script need to build the image to dockerhub \n2. docker-compose.yml file pulls the image and start up all containers \n3. watchtower will be started in that docker-compose.yml together \n-- once watchtower is up, all later builds will be able to call to watchtower no problem."'
        sh 'curl -H "Authorization: Bearer tokenVal_WATCHTOWER_HTTP_API_TOKEN_toBeSentFromJenkins" 10.15.1.137:8080/v1/update' // FIXME @config the ip address need know ahead of time?...
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






