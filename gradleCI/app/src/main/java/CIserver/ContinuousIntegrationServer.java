package CIserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import java.io.PrintWriter;

/** 
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);
        System.out.println(baseRequest.getMethod());

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        if(baseRequest.getMethod().equals("POST")){
            String body = getBody(baseRequest);
            String repo = null;
            String branch = null;


            for(String line : body.split(",")){
                if(line.contains("clone_url")){
                    repo = line.substring(13, line.length()-1);
                    //TODO check if default_branch is correct
                    System.out.println(repo);
                } else if(line.contains("\"ref\":")){
                    branch = line.split(":")[1].split("/")[2];
                    branch = branch.substring(0, branch.length()-1);
                    System.out.println(branch);
                }

            }

            if(repo != null && branch != null){
                Runtime runtime = Runtime.getRuntime();
                String currentDir = System.getProperty("user.dir");
                System.out.println("currentDir = " + currentDir.toString());
                String cloneOutput = runCommand("git clone " + repo + " tempRepo", runtime, new File(currentDir));
                String branchOutput = "Branch is Main.";
                if (!branch.equals("main")) {
                    branchOutput = runCommand("git checkout " + branch, runtime, new File(currentDir + "/tempRepo"));
                }

                boolean build_sccuessful = buildOutput.contains("BUILD SUCCESSFUL");

                PrintWriter writer = response.getWriter();
                writer.print(cloneOutput + "\n" + branchOutput + "\n" + "Running test... (See email for results");

                String buildOutput = runCommand("./gradlew build", runtime, new File(currentDir+"/tempRepo/gradleCI"));
                //String testOutput = runCommand("./gradlew test", runtime, new File(currentDir+"/tempRepo/gradleCI"));

                System.out.println("\n\n" + build_sccuessful? "BUILD SUCCESSFUL! \n" : "BUILD FAILED! \n");
                System.out.println("\n\nBUILD OUTPUT:---------------------------------");
                System.out.print(buildOutput);

                // Last: cleanup
                runCommand("rm -r tempRepo", runtime, new File(currentDir));



            }else{
                // The POST request does not have the intended headers, something is wrong.
                response.getWriter().println("You do not have the intended headers, something is wrong");
            }
        }else{
            // This is not a Webhook, so not a request we want to handle.
            response.getWriter().println("Whatever you are doing, it's not a webhook.");
        }
    }


    public String getBody(Request request) throws IOException{
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            reader.close();
        }

        return sb.toString();
    }

    public String runCommand(String command, Runtime runtime, File dir) {
        StringBuilder sb = new StringBuilder();
        try {
            Process proc = runtime.exec(command, null, dir);
            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = "";
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }

            while ((line = stdError.readLine()) != null) {
                System.out.println(line);
            }

            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
 
 
    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8016);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        server.join();
    }
}
