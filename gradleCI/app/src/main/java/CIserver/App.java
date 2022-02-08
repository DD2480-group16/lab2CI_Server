/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package CIserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new App().getGreeting());
        Server server = new Server(8016);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
