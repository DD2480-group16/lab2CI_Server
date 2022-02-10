package CIserver;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CIServerTest {
    
    @Test
    public void getBodyReturnsStringOfReaderContent() throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/test/java/CIserver/testFile.txt"));
        ContinuousIntegrationServer ci = new ContinuousIntegrationServer();
        String result = ci.getBody(reader);

        assertEquals("{    1:this is line1,    2:this is line2}", result);
    }

    @Test
    public void getBodyEmptyStringWhenNoContent()throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/test/java/CIserver/testEmptyFile.txt"));
        ContinuousIntegrationServer ci = new ContinuousIntegrationServer();
        String result = ci.getBody(reader);

        assertEquals("", result);
    }

    @Test
    public void runCommandCanRunCommand(){
        ContinuousIntegrationServer ci = new ContinuousIntegrationServer();
        String result = ci.runCommand("rm test123", null, new File(System.getProperty("user.dir")));

        assertEquals("rm: cannot remove 'test123': No such file or directory", result);
    }
}
