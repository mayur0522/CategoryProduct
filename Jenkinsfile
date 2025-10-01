pipeline {
    agent any

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        PROJECT_ID   = 'springapp-gke'
        REGION       = 'asia-south1'
        ZONE         = 'asia-south1-b'
        CLUSTER      = 'gke-cluster'
        IMAGE_NAME   = 'springboot-app'
        REPO         = "asia-south1-docker.pkg.dev/${PROJECT_ID}/springboot-artifacts/${IMAGE_NAME}"
        PATH         = "/root/google-cloud-sdk/bin:${env.PATH}" // ensure correct plugin is used
    }

    tools {
        maven 'maven3'
        jdk 'jdk-21'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mayur0522/CategoryProduct.git'
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            ${env.SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.projectKey=springboot-app \
                            -Dsonar.projectName=springboot-app \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.token=$SONAR_TOKEN
                        """
                    }
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'DC'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withMaven(globalMavenSettingsConfig: 'global-maven', jdk: 'jdk-21', maven: 'maven3') {
                    sh "mvn deploy -DskipTests"
                }
            }
        }

        stage('Authenticate, Build & Push Docker Image') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account-key', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh """
                        # Authenticate with GCP
                        gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                        gcloud config set project ${PROJECT_ID}
                        gcloud auth configure-docker ${REGION}-docker.pkg.dev --quiet

                        # Build Docker image
                        docker build -t ${REPO}:latest -f Dockerfile .

                        # Push Docker image to Artifact Registry
                        docker push ${REPO}:latest
                    """
                }
            }
        }

        stage('Deploy to GKE') {
            steps {
                script {
                    // Ensure we remove old broken plugin (if exists)
                    sh 'sudo rm -f /usr/local/bin/gke-gcloud-auth-plugin || true'

                    // Authenticate and get credentials
                    sh """
                        gcloud container clusters get-credentials ${CLUSTER} --zone ${ZONE} --project ${PROJECT_ID}
                    """

                    // Apply Kubernetes manifests (skip OpenAPI validation if needed)
                    sh """
                        kubectl apply -f k8s/spring-deployment.yaml --wait --validate=false
                        kubectl apply -f k8s/spring-service.yaml --wait --validate=false
                    """

                    echo "✅ Kubernetes resources applied successfully"
                }
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

