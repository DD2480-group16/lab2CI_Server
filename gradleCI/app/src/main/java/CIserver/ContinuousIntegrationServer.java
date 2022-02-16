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
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.sql.Timestamp;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;



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

        if(baseRequest.getMethod().equals("POST")){
            String body = getBody(baseRequest.getReader());
            String repo = null;
            String branch = null;


            for(String line : body.split(",")){
                if(line.contains("clone_url")){
                    repo = line.substring(13, line.length()-1);
                } else if(line.contains("\"ref\":")){
                    branch = line.split(":")[1].split("/")[2];
                    branch = branch.substring(0, branch.length()-1);
                }
            }

            if(repo != null && branch != null){
                System.out.println("\n\nINITIALIZING NEW BUILD TEST!--------------------------------");
                System.out.println("Repo:\t\t\t" + repo);
                System.out.println("Branch:\t\t\t" + branch);

                Runtime runtime = Runtime.getRuntime();
                String currentDir = System.getProperty("user.dir");
                //System.out.println("current directory:\t" + currentDir.toString());
                System.out.println("\nInitializing Build...");
                String cloneOutput = runCommand("git clone " + repo + " tempRepo", runtime, new File(currentDir));
                String branchOutput = "Branch is Main.";
                if (!branch.equals("main")) {
                    branchOutput = runCommand("git checkout " + branch, runtime, new File(currentDir + "/tempRepo"));
                }

                PrintWriter writer = response.getWriter();
                writer.print("Successfully cloned repo." + "\n" + "Now in branch " + branch + "\n"
                    + "Initializing build... See e-mail for results.");
                writer.close();

                String buildOutput = runCommand("./gradlew build", runtime, new File(currentDir+"/tempRepo/gradleCI"));
                //String testOutput = runCommand("./gradlew test", runtime, new File(currentDir+"/tempRepo/gradleCI"));

                boolean build_successful = buildOutput.contains("BUILD SUCCESSFUL");
                boolean failing_tests = buildOutput.contains("There were failing tests.");

                StringBuilder msg = new StringBuilder();
                msg.append((build_successful? "BUILD SUCCESSFUL! \n" : "BUILD FAILED! \n"));

                if (!build_successful){
                    msg.append((failing_tests? "REASON: Test(s) failed! \n" : "REASON: Syntax error! \n"));
                }

                msg.append("\nBUILD OUTPUT:\n-----------------------------------------------\n");
                msg.append(buildOutput);
                msg.append("------------------------------------------------------------\n");
                System.out.println(msg.toString());

                // Cleanup
                runCommand("rm -r tempRepo", runtime, new File(currentDir));

                // Send notification
                Notifications.send(// Create an email
                    "dd2480group16@gmail.com", //Sender mail
                    "6N9vpRzqZtY6rK8",// sender password
                    "dd2480group16@gmail.com",// Receiver mail
                    "Server Status", // Mail header
                    msg.toString() // Mail message
                );

                //Save build
                createBuildFile(msg, repo, branch, currentDir);

            }else{
                // The POST request body does not have the intended headers, something is wrong.
                System.out.println("\n RECIEVED MALFORMED POST REQUEST. (Discarded)\n");
                response.getWriter().println("You do not have the intended headers, something is wrong.");
            }
        }else{
            // This is not a Webhook, so not a request we want to handle.
            System.out.println("\n RECIEVED MALFORMED HTTP REQUEST. (Discarded)\n");
            response.getWriter().println("Whatever you are doing, it's not a webhook.");
        }
    }

    /** Creates a html file for the timestamp of the build, writes the build output to it. Adds a link to the new page in index.html.
     *
     * @param output The Stringbuilder containing the build output.
     * @param repo The repo that the commit occured in.
     * @param branch The branch the commit occured in.
     * @param currentDir The current path where the server is run.
     */
    public void createBuildFile(StringBuilder output, String repo, String branch, String currentDir){
      Timestamp time = new Timestamp(System.currentTimeMillis());
      String timestamp = time.toString().replace(" ", "-");
      timestamp = timestamp.toString().replace(":", "-");
      File file = new File(currentDir+"/../../../oldBuilds/build"+timestamp+".html");
      PrintWriter bw;
      boolean result;
      try{
        result = file.createNewFile();
        bw = new PrintWriter(new FileWriter(file));
        bw.print("<!DOCTYPE html><html><head><title>Build "+time+"</title></head><body><div style='white-space: pre-wrap;'>");
        bw.println("<h1>Build "+ time+"</h1>");
        bw.println("<b>Repo: "+repo+" </b>");
        bw.println("<b>Branch: "+branch+" </b>");
        bw.println("<b>Timestamp: "+ time +" </b><br>");
        bw.println(output.toString());
        bw.print("\n\n</div><a href=index.html>Back</a></body></html>");
        bw.close();
        Path path = Paths.get(currentDir+"/../../../oldBuilds/index.html");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        lines.add(7, "<a href='build"+timestamp+".html'>Build: "+time+"</a><br>");
        Files.write(path, lines, StandardCharsets.UTF_8);
      }
      catch(IOException e){
        e.printStackTrace();
      }
    }

    /** Reads a body from a BufferedReader and returns the String representation.
     *
     * @param reader The reader holding the body
     * @return String representation of body, without newlines.
     * @throws IOException
     */
    public String getBody(BufferedReader reader) throws IOException{
        StringBuilder sb = new StringBuilder();
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

    /** Runs a command and returns the output, error or not.
     *
     * @param command The command to run
     * @param runtime The runtime that the command is run in
     * @param dir   The directory in which the command is run
     * @return The string representation of the command output.
     */
    public String runCommand(String command, Runtime runtime, File dir) {
        StringBuilder sb = new StringBuilder();
        try {
            Process proc = runtime.exec(command, null, dir);
            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = "";

            while ((line = stdError.readLine()) != null) {
                sb.append(line + "\n");
            }
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
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
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
