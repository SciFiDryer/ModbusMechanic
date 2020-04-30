/*
 * Copyright 2020 Matt Jamesson <scifidryer@gmail.com>.
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
import java.io.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class RTUPacketListener extends Thread{
    GatewayManager manager = null;
    public RTUPacketListener(GatewayManager aManager)
    {
        manager = aManager;
    }
    public synchronized void run()
    {
        boolean isRunning = true;
        try
        {
            InputStream in = manager.port.getInputStream();
            byte[] buf = new byte[1024];
            int bytesRead = 0;
            while (isRunning)
            {
                bytesRead = in.read(buf);
                if (ModbusMechanic.debug)
                {
                    System.out.print("Got RTU Frame ");
                    for (int i = 0; i<bytesRead; i++)
                    {
                        System.out.print(String.format("%02X", buf[i]));
                    }
                    System.out.println("");
                }
                manager.queueManager.putMessage(bytesRead, buf);
            }
        }
        catch (IOException e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
            isRunning = false;
        }
        catch (Exception e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
    }
}
