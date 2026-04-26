pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/mahesh2492/event-platform.git'
            }
        }

        stage('Build') {
            steps {
                bat 'sbt clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'sbt "project apiService" test'
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