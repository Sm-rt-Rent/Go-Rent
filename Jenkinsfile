pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Building Spring Boot project...'
                // Use 'bat' for Windows command line execution
                bat 'mvnw.cmd clean package -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline built successfully!'
        }
        failure {
            echo 'Pipeline build failed.'
        }
    }
}