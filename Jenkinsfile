pipeline {
    agent any

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        PROJECT_ID   = 'gke-springboot-472605'   // your GCP Project ID
        REGION       = 'asia-south1'
        ZONE         = 'asia-south1-b'
        CLUSTER      = 'gke-cluster'
        IMAGE_NAME   = 'springboot-app'
        REPO         = "asia-south1-docker.pkg.dev/${PROJECT_ID}/springboot-artifacts/${IMAGE_NAME}"
    }

    tools {
        maven 'maven3'
        jdk 'jdk-21'
    }

    triggers {
        githubPush()   // auto-trigger when GitHub repo is updated
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mayur0522/springboot-app-GKE.git'
            }
        }

        stage('Compile') {
    steps {
        dir('CategoryProduct') {   // <-- adjust if pom.xml is inside a subfolder
            sh "mvn compile"
        }
    }
}

stage('Unit Tests') {
    steps {
        dir('CategoryProduct') {
            sh "mvn test -DskipTests=true"
        }
    }
}

stage('Build') {
    steps {
        dir('CategoryProduct') {
            sh "mvn package -DskipTests=true"
        }
    }
}


        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-token') {
                    sh """
                        ${env.SCANNER_HOME}/bin/sonar-scanner \
                        -Dsonar.projectKey=springboot-app \
                        -Dsonar.projectName=springboot-app \
                        -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'DC'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        stage('Build') {
            steps {
                sh "mvn package -DskipTests=true"
            }
        }

        stage('Deploy to Nexus') {
            steps {
              withMaven(globalMavenSettingsConfig: '60870c10-d042-409e-bddf-c868fbc611c4', jdk: 'jdk-21', maven: 'maven3', traceability: true) {
                    sh "mvn deploy -DskipTests=true"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${REPO}:latest -f Dockerfile ."
                }
            }
        }

        stage('Authenticate with GCP') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account-key', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
                    sh "gcloud config set project ${PROJECT_ID}"
                    sh "gcloud auth configure-docker ${REGION}-docker.pkg.dev"
                }
            }
        }

        stage('Push Docker Image to Artifact Registry') {
            steps {
                sh "docker push ${REPO}:latest"
            }
        }

        stage('Get GKE Credentials') {
            steps {
                sh "gcloud container clusters get-credentials ${CLUSTER} --zone ${ZONE} --project ${PROJECT_ID}"
            }
        }

        stage('Deploy to GKE') {
            steps {
                sh """
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                """
            }
        }
    }

    post {
        success {
            echo '✅ Spring Boot project deployed successfully to GKE!'
        }
        failure {
            echo '❌ Deployment failed.'
        }
    }
}
