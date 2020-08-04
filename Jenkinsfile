def err = null

node {
      try {
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
  
