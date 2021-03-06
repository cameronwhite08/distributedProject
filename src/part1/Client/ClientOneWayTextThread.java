package part1.Client;

import logWriter.LogWriter;

import java.io.*;
import java.net.Socket;

//class to run some network test
public class ClientOneWayTextThread extends BaseClientThread
{
    LogWriter twoWayTextLogWriter;

    public ClientOneWayTextThread(String _routerName, String destinationIp, boolean onLocalMachine, String choice, LogWriter writer) throws IOException {
        super(_routerName, destinationIp, onLocalMachine, choice);
        twoWayTextLogWriter = writer;
    }

    @Override
    protected int PortToRunOn()
    {
        return 8787;
    }

    @Override
    protected int PortToConnectTo()
    {
        return 7878;
    }

    //the test to be performed
    //you are provided a socket, you can perform whatever options over it
    //clean up writers/readers you create
    //DO NOT clean up socket provided
    @Override
    protected void RunTest(Socket socket)
    {
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        String fromUser;
        long startTime, timeDifference;

        // Variables for message passing
        Reader reader = null;
        try
        {
            reader = new FileReader("src/file.txt");
        }
        catch (FileNotFoundException e)
        {
            ERROR("Could Not find file to send");
        }
        BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
        String fromServer = ""; // messages received from ServerRouter

        try
        {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e)
        {
            ERROR("Couldn't get I/O for the connection to: ");
        }

        try
        {

            String timeFinished = "-1";

            fromServer = in.readLine();
            PRINT("from server: " + fromServer);
            // Communication while loop
            startTime = System.currentTimeMillis();
            while (isRunning && (fromUser = fromFile.readLine()) != null)
            {
                PRINT("Client: " + fromUser);
                out.println(fromUser); // sending the strings to the Server via ServerRouter

                if (fromUser.equals("Finished."))
                    break;
            }
            //read time in from server that the transmission completed
            timeFinished = in.readLine();
            PRINT("Time received: " + timeFinished);
            long diff = Long.parseLong(timeFinished)-startTime;
            String output = String.format("One Way text Transmission occurred in %s milliseconds", diff);
            twoWayTextLogWriter.WriteToFile(output);

            //read in closing statement from file
            fromUser = fromFile.readLine();
            PRINT("Client: " + fromUser);
            //send closing statement
            out.println(fromUser);
            //receive last message from server
            fromServer = in.readLine();
            PRINT("Server said: " + fromServer);
        }
        catch (IOException e)
        {
            ERROR("Couldn't get I/O from the client");
        }

        // closing connections
        out.close();

        try
        {
            in.close();
        }
        catch (IOException e)
        {
            ERROR("Error when closing socket");
        }
    }

    @Override
    protected void PRINT(String message)
    {

        super.PRINT("ClientTwoWayTextThread " + message);
    }
}


