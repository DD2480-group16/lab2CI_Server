# DT2480-Continuous integration

DT2480-Continuous integration is a project consisting in implementing a continuous integration server.  

This CI server is meant to be called as a webhook by github. It uses Jetty for the HTTP part, Graddle to build the project and Ngrok for the communication part. 
The server is able to build a github project after some changes were commited, run a set of test and communicate results of the test to the programmer by email.

Note : this server is intended to run on Unix systems and may not work on Windows.

## How to run the server:
1. Clone this repository into the directory of your choice.
2. Run `gradlew.bat` inside the /gradleCI folder.
3. Install Ngrok in the repository root folder  
4. Go into /gradleCI and run `./gradlew build`    
5. Then run `./gradlew run`
6. Run `.ngrok http 8016` from repository root
7. Copy the Ngrok http address and add it to your Github webhook.
   
The server should work.

To shutdown everything:

* `Ctrl-C` in the ngrok terminal window
* `Ctrl-C` in the ngrok java window
* delete the webhook in the webhook configuration page.

## How compilation has been implemented and unit-tested:
Compilation is implemented by running commands in the command line from the code. From the XML data sent with the webhook when a commit occurs, the branch and repo are extracted. The repo is cloned into a folder named tempRepo. In the tempRepo folder the branch is changed to the branch the commit occured. Then the project is built with gradle. 


## How test execution has been implemented and unit-tested.
Test execution is implemented by building the already cloned repo with gradle. Since this will run all the associated tests for the project. We have tests for the helper functions and email notification that will run on each webhook call.


## Statement of contributions:
#### Thomas:
Creating function for the Notification part, i.e. send an email to give the server status. Wrote tests for notifications functions.

#### Agnes:
Parsing the request body, runCommand(), and cloning and branching of the repo into testRepo for building and testing. Wrote tests for helper functions.

#### Fredrik Svanholm:
Made the server properly run, and test builds after cloning the branch and formatted comprehensive server output based on the results.

#### Nicolas Wittmann
Creating the readme skeleton, adding the description on how to run the programm and the way of working.

#### Malin Svenberg

## Way of Working, SEMAT:

We decided to use similar organisation as first lab, using a slack channel to organise and github to manage the project by creating issues, solving them on separated branches and adding them to main branch with pull request. 
On the state of our group in regard to the states described in "Kernel and Language for Software Engineering Method (Essence), p51-52", we think we are in the Formed state. All the team members have met both virtually and in person and are able to coordinate the work in order to perform the demanded task. The communication is well organised with defined mechanism (mainly on slack to coordinate work and meetings). Maybe the team has not reach the Collaborating state yet because the exchanges are at the moment almost only work related.
