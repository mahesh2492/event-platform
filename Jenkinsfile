pipeline {
    agent any

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