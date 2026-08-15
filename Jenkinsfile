pipeline {
    agent {
        node {
           label 'roboshop'
        }
    }

    environment {
        acc_id = "160885265516"
        region = "us-east-1"
        app_repo = "petclinic"
        mysql_repo = "petclinic-mysql"
        appVersion = ""
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 25, unit: 'MINUTES')
        timestamps()
    }

    parameters {
        booleanParam(name: 'DEPLOY', defaultValue: true, description: 'Deploy after successful build, Sonar, and Trivy checks')
    }

    stages {
        stage('Checkout') {
           steps {
               checkout scm
           }
        }

        stage('Read Version') {
           steps {
               script {
                   def pom = readMavenPom(file: 'petclinc/pom.xml')
                   appVersion = pom.version ?: '1.0.0'
                   echo "Application version: ${appVersion}"
               }
           }
        }

        stage('Build & Unit Tests') {
           steps {
               sh '''
                   cd petclinc
                   mvn -B clean verify
               '''
           }
        }

        stage('Verify Build Artifacts') {
           steps {
               sh '''
                   cd petclinc
                   pwd
                   ls -la
                   ls -la target || true
                   ls -la target/site/jacoco || true
                   ls -la target/surefire-reports || true
                   test -f target/site/jacoco/jacoco.xml || echo "Jacoco report not found"
                   test -d target/surefire-reports || echo "Surefire reports not found"
               '''
           }
        }

        stage('SonarQube Analysis') {
           steps {
               withSonarQubeEnv('sonar-server') {
                   sh '''
                       cd petclinc
                       mvn -B sonar:sonar \
                         -Dsonar.projectKey=petclinic \
                         -Dsonar.projectName=petclinic \
                         -Dsonar.sources=src/main/java \
                         -Dsonar.tests=src/test/java \
                         -Dsonar.java.binaries=target/classes \
                         -Dsonar.java.test.binaries=target/test-classes \
                         -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                   '''
               }
           }
        }

        stage('SonarQube Quality Gate') {
           steps {
               timeout(time: 10, unit: 'MINUTES') {
                   script {
                       def qg = waitForQualityGate()
                       if (qg.status != 'OK') {
                           error "Pipeline aborted: ${qg.status}"
                       }
                   }
               }
           }
        }

        stage('Trivy File System Scan') {
           steps {
               sh '''
                   trivy fs \
                   --scanners vuln,secret,misconfig \
                   --severity HIGH,CRITICAL \
                   --exit-code 0 \
                   --format table \
                   --output trivy-fs-report.txt \
                   .
               '''
           }
        }

        stage('Docker Build') {
           steps {
               script {
                   withAWS(credentials: 'aws-creds', region: 'us-east-1') {
                       sh """
                           aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com

                           docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} \
                             -f petclinc/Dockerfile petclinc

                           docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} \
                             -f mysql/Dockerfile mysql
                       """
                   }
               }
           }
        }

        stage('Trivy Image Scan') {
           steps {
               script {
                   def dockerfileScan = sh(
                       script: """
                           trivy config --exit-code 0 --severity HIGH,CRITICAL --format table ./petclinc/Dockerfile
                       """,
                       returnStatus: true
                   )

                   def appImageScan = sh(
                       script: """
                           trivy image --scanners vuln --pkg-types os,library --exit-code 0 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion}
                       """,
                       returnStatus: true
                   )

                   def mysqlImageScan = sh(
                       script: """
                           trivy image --scanners vuln --pkg-types os,library --exit-code 0 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion}
                       """,
                       returnStatus: true
                   )

                   if (dockerfileScan != 0 || appImageScan != 0 || mysqlImageScan != 0) {
                       error "Trivy found HIGH/CRITICAL issues in Dockerfile and/or container images. Failing pipeline."
                   }
               }
           }
        }

        stage('ECR Image Push') {
           steps {
               script {
                   withAWS(credentials: 'aws-creds', region: 'us-east-1') {
                       sh """
                           aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com
                           docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion}
                           docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion}
                       """
                   }
               }
           }
        }

        stage('Deploy') {
           when {
               expression { return params.DEPLOY == true }
           }
           steps {
               sh '''
                   echo "Deploying Pet Clinic application"
               '''
           }
        }
    }

    post {
        always {
           archiveArtifacts artifacts: 'trivy-fs-report.txt, **/*.xml, **/*.html', allowEmptyArchive: true
           echo 'Pipeline finished'
        }
        success {
           echo 'Build, SonarQube, Trivy, and image push succeeded.'
        }
        failure {
           echo 'Pipeline failed. Check build/test/quality gate and Trivy output.'
        }
    }
}
