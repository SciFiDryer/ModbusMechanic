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
import modbusmechanic.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class GatewayMonitorServer extends Thread{
    
    ArrayList<GatewayClient> clients = new ArrayList<GatewayClient>();
    boolean isRunning = false;
    ExecutorService threadPool = Executors.newFixedThreadPool(10);
    ServerSocket server = null;
    public void run()
    {
        try
        {
            server = new ServerSocket(23);
            GatewayClient gc = new GatewayClient(server.accept(), clients);
            clients.add(gc);
            threadPool.execute(gc);
        }
        catch (IOException e)
        {
            if (modbusmechanic.ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
        if (server != null)
        {
            isRunning = true;
            while (isRunning)
            {
                try
                {
                    Socket client = server.accept();
                }
                catch (IOException e)
                {
                    isRunning = false;
                    if (modbusmechanic.ModbusMechanic.debug)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void sendToClients(String text)
    {
        for (int i = 0; i < clients.size(); i++)
        {
            GatewayClient currentClient = clients.get(i);
            currentClient.addToQueue(text);
        }
    }
    public void stopServer()
    {
        isRunning = false;
        interrupt();
        for (int i = 0; i < clients.size(); i++)
        {
            GatewayClient currentClient = clients.get(i);
            try
            {
                currentClient.client.close();
            }
            catch (IOException e)
            {
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
            clients.remove(currentClient);
        }
        try
        {
            server.close();
        }
        catch (IOException e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
    }
}
    