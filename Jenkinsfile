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
                echo 'Fixing wrapper line endings and building project...'
                                // Automatically fix line endings for Windows batch wrapper
                                bat 'powershell -Command "(Get-Content mvnw.cmd -Raw) -replace \"`r`n?\", \"`r`n\" | Set-Content mvnw.cmd -NoNewline"'
                                bat 'mvnw.cmd clean package -DskipTests'
            }
        }
        stage('Docker Deploy') {
                    steps {
                        echo 'Deploying application stack using Docker Compose...'
                        // Correct syntax: 'docker compose -f file.yml down'
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose -f docker-compose.yml down || exit 0'
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose -f docker-compose.yml up --build -d'
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