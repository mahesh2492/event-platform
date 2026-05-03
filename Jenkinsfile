pipeline {
    agent any

    tools {
         jdk 'jdk 1.8'
         sbt 'sbt-1.9.8'
    }

    stages {


        stage('Build') {
            steps {
                sh 'sbt clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'sbt "project apiService" test'
            }
        }
    }

    post {
        success {
            echo 'Build & Tests Passed ✅'
        }
        failure {
            echo 'Build Failed ❌'
        }
    }
}