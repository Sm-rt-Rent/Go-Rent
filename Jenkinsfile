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
        stage('Docker Deploy') {
                    steps {
                        echo 'Deploying application stack using Docker Compose...'
                        // Use the absolute path to Docker on Windows
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose down'
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose up --build -d'
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