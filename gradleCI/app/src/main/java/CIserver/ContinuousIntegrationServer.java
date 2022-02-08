package CIserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

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

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        if(baseRequest.getMethod().equals("POST")){
            String body = getBody(baseRequest);
            String repo = null;
            String branch = null;

            for(String line : body.split(",")){
                if(line.contains("full_name")){
                    repo = line.split(":")[1];
                    repo = repo.substring(1, repo.length());
                    //TODO check if default_branch is correct
                } else if(line.contains("default_branch")){
                    branch = line.split(":")[1];
                    branch = repo.substring(1, repo.length());
                }
            }

            if(repo != null && branch != null){
                Runtime runtime = Runtime.getRuntime();
                String cloneOutput = runCommand("git clone git@github.com:" + repo + " tempRepo", runtime);
                String cdOutput = runCommand("cd tempRepo", runtime);
                String branchOutput = runCommand("git checkout " + branch, runtime);



                // Last: cleanup
                runCommand("rm -r tempRepo", runtime);
            }else{
                // The POST request does not have the intended headers, something is wrong.
            }
        }else{
            // This is not a Webhook, so not a request we want to handle.
        }

        response.getWriter().println("CI job done");
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

    public String runCommand(String command, Runtime runtime) {
        StringBuilder sb = new StringBuilder();
        try {
            Process proc = runtime.exec(command);
            // Read the output
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while((line = reader.readLine()) != null) {
                sb.append(line);
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
