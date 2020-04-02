/**
* Copyright (C) Altimetrik 2018. All rights reserved.
*
* This software is the confidential and proprietary information
* of Altimetrik. You shall not disclose such Confidential Information
* and shall use it only in accordance with the terms and conditions
* entered into with Altimetrik.
*/

pipeline {

	agent { label 'chp-tests' }

	options {
		buildDiscarder(logRotator(numToKeepStr: '10'))
		timeout(time: 60, unit: 'MINUTES')
		timestamps()
	}

	stages {
		stage ('Checkout') {
			steps {
				checkout scm
			}
		}
		stage('Test') {
			steps {
			   // sh "export MAVEN_OPTS=\"-Xms6144M -Xmx12288M -XX:+UseG1G\""
				sh 'export MAVEN_HOME=/opt/maven'
				sh 'export PATH=/opt/maven/bin:$PATH'
				sh "java -Xms6144M -Xmx12288M -XX:+UseG1GC -Dtest=\"SurveyGetParamTest\""
				//sh "/opt/maven/bin/mvn -Dtest=\"SurveyGetParamTest\" test"
				//sh "javac SurveyGetParamTest.java"
				
			}
		}
	}

	post {

		success {
			emailext (to: "rchennuboina@altimetrik.com", subject:"AUTOMATION RUN COMPLETED: ${currentBuild.fullDisplayName}", body: "Automation Run Completed! Please go to ${BUILD_URL}cucumber-html-reports/overview-features.html and review the automation report. Reports Attached.", attachmentsPattern: 'target/automation-report.pdf')
			cleanWs()
		}

		failure {
			emailext (to: "rchennuboina@altimetrik.com", subject:"AUTOMATION RUN FAILURE: ${currentBuild.fullDisplayName}", body: "Automation Run Failed! Your commits is suspected to have caused the build failure. Please go to ${BUILD_URL} for details and resolve the build failure at the earliest.", attachLog: true, compressLog: true)
			cleanWs()
		}

		aborted {
			emailext (subject:"AUTOMATION RUN ABORTED: ${currentBuild.fullDisplayName}", body: "Automation Run Aborted! Please go to ${BUILD_URL} and verify the run.", attachLog: false, compressLog: false)
			cleanWs()
		}

	}
}
