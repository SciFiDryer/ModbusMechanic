/*
 * Copyright 2021 Matt Jamesson <scifidryer@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package modbusmechanic.gateway;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import modbusmechanic.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class GatewayClient extends Thread{
    ArrayList<GatewayClient> clients = null;
    Socket client = null;
    boolean isRunning = false;
    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();
    public GatewayClient(Socket aClient, ArrayList<GatewayClient> aClients)
    {
        client = aClient;
        clients = aClients;
    }
    public void run()
    {
        PrintWriter pw = null;
        try
        {
            pw = new PrintWriter(client.getOutputStream());
            pw.println("ModbusMechanic remote monitor");
            pw.flush();
            isRunning = true;
        }
        catch (IOException e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
        while (isRunning)
        {
            try
            {
                pw.println(queue.take());
                pw.flush();
            }
            catch(InterruptedException e)
            {
                isRunning = false;
                clients.remove(this);
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public void addToQueue(String text)
    {
        queue.add(text);
    }
}
