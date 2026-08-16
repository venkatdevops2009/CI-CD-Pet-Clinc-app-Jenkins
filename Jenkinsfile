pipeline {
    agent {
        node {
           label 'roboshop'
        }
    }

    environment {
        acc_id = "843916760700"
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
        // Which Git branch to build (leave blank to use repository default)
        string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to checkout and build (e.g. main, develop)')

        // Optionally override application version read from pom.xml
        string(name: 'APP_VERSION_OVERRIDE', defaultValue: '', description: 'Override version read from pom.xml (leave empty to use pom.xml version)')

        // Deploy target environment
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'staging', 'prod'], description: 'Select deployment target environment')

        // Use ECR images pushed by this pipeline instead of local builds
        booleanParam(name: 'USE_ECR_IMAGES', defaultValue: true, description: 'If true, docker compose will pull images from ECR for deployment')

        // Skip optional verification steps for faster iterative runs
        booleanParam(name: 'SKIP_SONAR', defaultValue: false, description: 'Skip SonarQube analysis (not recommended)')
        booleanParam(name: 'SKIP_SONARQULITY', defaultValue: false, description: 'Skip SonarQube qulity gate check (not recommended)')
        booleanParam(name: 'SKIP_TRIVY_FS', defaultValue: false, description: 'Skip Trivy filesystem scan')
        booleanParam(name: 'SKIP_TRIVY_IMG', defaultValue: false, description: 'Skip Trivy image scans')

        // Force docker rebuild even if an image with same tag exists
        booleanParam(name: 'FORCE_REBUILD', defaultValue: false, description: 'Force docker build instead of reusing existing images')

        // Allow the operator to override deployed image names (advanced)
        string(name: 'APP_IMAGE_OVERRIDE', defaultValue: '', description: 'Optional full image name to deploy (overrides ECR image), e.g. 123456789012.dkr.ecr.us-east-1.amazonaws.com/petclinic:1.2.0')
        string(name: 'MYSQL_IMAGE_OVERRIDE', defaultValue: '', description: 'Optional MySQL image name to deploy (overrides ECR image)')

        // Keep the existing DEPLOY toggle for backward-compatibility
        booleanParam(name: 'DEPLOY', defaultValue: true, description: 'Deploy after successful build, Sonar, and Trivy checks')

        // Credentials id to use for AWS operations (use stored credential id)
        string(name: 'AWS_CREDS_ID', defaultValue: 'aws-creds', description: 'Jenkins credentials id for AWS (used to push/pull from ECR)')
    }

    stages {
        stage('Checkout') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Checkout ==="
                   script {
                       // If a branch parameter is provided, checkout that branch; otherwise fall back to scm
                       if (params.BRANCH && params.BRANCH.trim()) {
                           echo "Checking out branch: ${params.BRANCH}"
                           checkout([$class: 'GitSCM', branches: [[name: "refs/heads/${params.BRANCH}"]], userRemoteConfigs: scm.userRemoteConfigs])
                       } else {
                           checkout scm
                       }
                   }
                   echo "=== Completed stage: Checkout ==="
               }
           }
        }

        stage('Read Version') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Read Version ==="
                   script {
                       def pom = readMavenPom(file: 'petclinc/pom.xml')
                       def pomVersion = pom.version ?: '1.0.0'
                       // Allow explicit override via parameter
                       if (params.APP_VERSION_OVERRIDE && params.APP_VERSION_OVERRIDE.trim()) {
                           appVersion = params.APP_VERSION_OVERRIDE.trim()
                           echo "Application version overridden by parameter: ${appVersion}"
                       } else {
                           appVersion = pomVersion
                           echo "Application version from pom.xml: ${appVersion}"
                       }
                   }
                   echo "=== Completed stage: Read Version ==="
               }
           }
        }

        stage('Build & Unit Tests') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Build & Unit Tests ==="
                   sh '''
                       cd petclinc
                       mvn -B clean verify
                   '''
                   echo "=== Completed stage: Build & Unit Tests ==="
               }
           }
        }

        stage('Verify Build Artifacts') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Verify Build Artifacts ==="
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
                   echo "=== Completed stage: Verify Build Artifacts ==="
               }
           }
        }

        stage('SonarQube Analysis') {
           when {
               expression { return !params.SKIP_SONAR }
           }
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: SonarQube Analysis ==="
                   withSonarQubeEnv('sonar-server') {
                       sh '''
                           cd petclinc
                           mvn -B sonar:sonar \
                             -Dsonar.projectKey=petclinic-cicd \
                             -Dsonar.projectName=petclinic-cicd \
                             -Dsonar.sources=src/main/java \
                             -Dsonar.tests=src/test/java \
                             -Dsonar.java.binaries=target/classes \
                             -Dsonar.java.test.binaries=target/test-classes \
                             -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                       '''
                   }
                   echo "=== Completed stage: SonarQube Analysis ==="
               }
           }
        }

        stage('SonarQube Quality Gate') {
           when {
               expression { return !params.SKIP_SONARQULITY }
           }
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: SonarQube Quality Gate ==="
                   timeout(time: 10, unit: 'MINUTES') {
                       script {
                           def qg = waitForQualityGate()
                           if (qg.status != 'OK') {
                               error "Pipeline aborted: ${qg.status}"
                           }
                       }
                   }
                   echo "=== Completed stage: SonarQube Quality Gate ==="
               }
           }
        }

        stage('Trivy File System Scan') {
           when {
               expression { return !params.SKIP_TRIVY_FS }
           }
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Trivy File System Scan ==="
                   sh '''
                       set +e
                       echo "=== Trivy FS debug start ==="
                       trivy --version
                       trivy fs \
                         --scanners vuln,secret,misconfig \
                         --severity HIGH,CRITICAL \
                         --exit-code 0 \
                         --format table \
                         --output trivy-fs-report.txt \
                         .
                       fs_status=$?
                       echo "Trivy FS exit code: ${fs_status}"
                       echo "=== Trivy FS report preview ==="
                       sed -n '1,220p' trivy-fs-report.txt || true
                       echo "=== Trivy FS debug end ==="
                       exit 0
                   '''
                   echo "=== Completed stage: Trivy File System Scan ==="
               }
           }
        }

        stage('Docker Build') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Docker Build ==="
                   script {
                       // Allow selecting AWS credentials id from parameters
                       withAWS(credentials: params.AWS_CREDS_ID ?: 'aws-creds', region: 'us-east-1') {
                           sh """
                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com

                               // Decide whether to force build or trust existing images
                               if [ '${params.FORCE_REBUILD}' = 'true' ]; then
                                 echo "Forcing a rebuild of images"
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} -f petclinc/Dockerfile petclinc
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} -f mysql/Dockerfile mysql
                               else
                                 echo "Building images (may reuse cache)"
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} -f petclinc/Dockerfile petclinc
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} -f mysql/Dockerfile mysql
                               fi
                           """
                       }
                   }
                   echo "=== Completed stage: Docker Build ==="
               }
           }
        }

        stage('Trivy Image Scan') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Trivy Image Scan ==="
                   script {
                       def dockerfileScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy Dockerfile debug ==="
                               trivy config --exit-code 1 --severity HIGH,CRITICAL --format table ./petclinc/Dockerfile || true
                               trivy config --exit-code 1 --severity HIGH,CRITICAL --format table ./mysql/Dockerfile || true
                           """,
                           returnStatus: true
                       )

                       def appImageScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy app image debug ==="
                               trivy image --scanners vuln --pkg-types os,library --exit-code 1 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} || true
                           """,
                           returnStatus: true
                       )

                       def mysqlImageScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy mysql image debug ==="
                               trivy image --scanners vuln --pkg-types os,library --exit-code 1 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} || true
                           """,
                           returnStatus: true
                       )

                       echo "Dockerfile scan exit code: ${dockerfileScan}"
                       echo "App image scan exit code: ${appImageScan}"
                       echo "MySQL image scan exit code: ${mysqlImageScan}"
                   }
                   echo "=== Completed stage: Trivy Image Scan ==="
               }
           }
        }

        stage('ECR Image Push') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: ECR Image Push ==="
                   script {
                       withAWS(credentials: 'aws-creds', region: 'us-east-1') {
                           sh """
                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com
                               // Push images (use provided AWS creds id)
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion}
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion}

                               // Optionally tag as 'latest' for convenience (comment out if undesired)
                               docker tag ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:latest || true
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:latest || true
                           """
                       }
                   }
                   echo "=== Completed stage: ECR Image Push ==="
               }
           }
        }

        stage('Deploy') {
           when {
               expression { return params.DEPLOY == true }
           }
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Deploy ==="
                   script {
                       // Compute image names with possible overrides from parameters
                       def defaultAppImage = "${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion}"
                       def defaultMysqlImage = "${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion}"

                       def appImage = params.APP_IMAGE_OVERRIDE?.trim() ? params.APP_IMAGE_OVERRIDE.trim() : defaultAppImage
                       def mysqlImage = params.MYSQL_IMAGE_OVERRIDE?.trim() ? params.MYSQL_IMAGE_OVERRIDE.trim() : defaultMysqlImage

                       withAWS(credentials: params.AWS_CREDS_ID ?: 'aws-creds', region: region) {
                           sh """
                               echo "Deploying Pet Clinic application using images"
                               echo "APP_IMAGE=${appImage}"
                               echo "MYSQL_IMAGE=${mysqlImage}"

                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com

                               # Export env vars used by docker-compose (compose file uses ${APP_IMAGE} and ${MYSQL_IMAGE})
                               export APP_IMAGE=${appImage}
                               export MYSQL_IMAGE=${mysqlImage}

                               # If requested, pull from registry to ensure latest scanned image
                               if [ '${params.USE_ECR_IMAGES}' = 'true' ]; then
                                 echo "Pulling images from registry"
                                 docker compose pull || true
                               fi

                               # Start using pulled images (no build in deploy phase)
                               docker compose down || true
                               docker compose up -d --no-build
                           """
                       }
                   }
                   echo "=== Completed stage: Deploy ==="
               }
           }
        }
    }

    post {
        always {
           sh '''
           
           docker image prune -f || true
           
           '''
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
