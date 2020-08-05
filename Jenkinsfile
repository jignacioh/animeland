@Library('jenkins-shared-library')_
def err = null

pipeline {
	agent any
      try {
	      stages {
			stage ('Clone sources') {
				git url: 'https://github.com/jignacioh/animeland.git'
			} 
			stage('Clean Build') {

		    		sh './gradlew clean'

			}
       
        		stage('Compile') {
		 
				// Compile the app and its dependencies
				sh './gradlew compileDebugSources'
		  
			}
			stage('Build APK') {
		  
				// Finish building and packaging the APK
				sh './gradlew assembleDebug'

				// Archive the APKs so that they can be downloaded from Jenkins
				archiveArtifacts artifacts: '**/*.apk', fingerprint: true, onlyIfSuccessful: true   
		  
			}
		}
	      post {
			always {
		   	 	/* Use slackNotifier.groovy from shared library and provide current build result as parameter */   
		    		slackNotifier(currentBuild.currentResult)
		    		cleanWs()
			}
    		}
	      
	}catch (caughtError) { 
    
		err = caughtError
		currentBuild.result = "FAILURE"

	} finally {
		if(currentBuild.result == "FAILURE"){
			 sh "echo 'Build FAILURE'"
		}else{
			 sh "echo 'Build SUCCESSFUL'"
		}
	}
}
  
